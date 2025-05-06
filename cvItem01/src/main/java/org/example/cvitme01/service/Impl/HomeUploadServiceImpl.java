package org.example.cvitme01.service.Impl;

import com.alibaba.fastjson2.JSONObject;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.example.cvitme01.entity.dto.*;
import org.example.cvitme01.service.HomeUploadService;
import org.example.cvitme01.utils.Const;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeUploadServiceImpl implements HomeUploadService {

    private final RestTemplate restTemplate;
    private final MinioClient minioClient;
    private final JSqlClient sqlClient;

    @Override
    public Reading uploadImage(MultipartFile file, String type, int id) throws Exception {
        return switch (type) {
            case "water" -> water(file);
            case "electricity" ->
                // Implement electricity handling here
                    null;
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };
    }

    @Override
    @Transactional
    public String saveImage(Reading reading, int id) {
        Meter inputMeter = reading.meter();
        if (inputMeter == null || inputMeter.location() == null) return "仪表位置信息不能为空";
        String location = inputMeter.location();
        String type = inputMeter.type();

        MeterTable  meterTable = MeterTable.$;
        Optional<Meter> existingMeterOpt = sqlClient.createQuery(meterTable)
                        .where(meterTable.location().eq(location))
                        .where(meterTable.account().getId().eq(id))
                        .select(meterTable)
                        .fetchOptional();
        long meterId;
        if (existingMeterOpt.isPresent()) {
            meterId = existingMeterOpt.get().id();
            log.info("找到已存在的位置，ID: {}, 位置: {}", meterId, location);
        }else {
            log.info("未找到位置为 '{}' 的仪表，为用户 ID {} 创建新仪表", location, id);
            Meter newMeter = MeterDraft.$.produce(draft -> {
                draft.setLocation(location);
                draft.setType(type);
                draft.applyAccount(acc -> acc.setId(id));
            });
            var result = sqlClient.getEntities().saveCommand(newMeter).execute();
            Meter saveMeter = result.getModifiedEntity();
            if (saveMeter == null) return "未知错误，请联系管理员";
            meterId = saveMeter.id();
            log.info("新仪表创建成功，ID: {}", meterId);
        }

        try {
            Reading readingToSave = ReadingDraft.$.produce(draft -> {
                draft.setShotTime(reading.shotTime());
                draft.setValue(reading.value());
                draft.setImageUrl(reading.imageUrl());
//                draft.setPreviewUrl(reading.previewUrl());
                // 关联 Meter
                draft.applyMeter(m -> m.setId(meterId));
                // 计算 delta 和 cost，
                // draft.setDelta(calculateDelta(...));
                // draft.setCost(calculateCost(...));
            });

            sqlClient.getEntities().saveCommand(readingToSave)
                    .setMode(SaveMode.INSERT_ONLY)
                    .execute();
            log.info("读数记录保存成功，关联仪表 ID: {}", meterId);
            return null; // 返回 null 表示成功
        } catch (Exception e) {
            log.error("保存读数记录时出错: {}", e.getMessage(), e);
            return "保存读数记录失败，请联系管理员";
        }
    }

    private Reading water(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        body.add("image", resource);

        HttpHeaders multipartHeaders = new HttpHeaders();
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, multipartHeaders);

        JSONObject response = restTemplate.postForObject(
                "http://localhost:5000/api/read_water_meter",
                requestEntity,
                JSONObject.class
        );

        if (response == null) return null;

        log.info(response.toString());

        Integer code = response.getInteger("code");
        JSONObject dataObj = response.getJSONObject("data");

        // 获取pointer_readings
        BigDecimal reading = dataObj.getBigDecimal("reading");

        if (code == 200) {
            String imageName = UUID.randomUUID().toString().replace("-",  "");
            imageName = Const.MINIO_WATER + imageName;
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket("study")
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .object(imageName)
                    .build();

            try {
                minioClient.putObject(args);
                String finalImageName = imageName;
                return ReadingDraft.$.produce(draft -> {
                    draft.setShotTime(LocalDateTime.now());
                    draft.setValue(reading);
                    draft.setImageUrl(finalImageName);
                });
            } catch (Exception e) {
                log.error("图片上传出现问题: {}", e.getMessage(), e);
                return null;
            }
        }

        return null;
    }
}
