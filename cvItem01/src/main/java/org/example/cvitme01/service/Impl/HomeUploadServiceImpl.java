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
import org.example.cvitme01.service.MeterManagementService;
import org.example.cvitme01.service.calculator.CostCalculator;
import org.example.cvitme01.service.factory.MeterReadingStrategyFactory;
import org.example.cvitme01.service.strategy.MeterReadingStrategy;
import org.example.cvitme01.utils.Const;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeUploadServiceImpl implements HomeUploadService {

    private final MeterReadingStrategyFactory strategyFactory;
    private final MinioClient minioClient;
    private final JSqlClient sqlClient;
    private final CostCalculator costCalculator;
    private final MeterManagementService meterManagementService;

    @Override
    public Reading uploadImage(MultipartFile file, String type, int id) throws Exception {
        return readMeter(type, file);
    }

    @Override
    @Transactional
    public synchronized String saveImage(String selectType, Reading reading, int id) {
        try {
            Meter inputMeter = reading.meter();
            if (inputMeter == null || inputMeter.location() == null) {
                return "仪表位置信息不能为空";
            }

            long meterId = meterManagementService.getOrCreateMeter(
                    selectType,
                    inputMeter.location(),
                    id
            );

            Reading readingToSave = ReadingDraft.$.produce(draft -> {
                draft.setShotTime(reading.shotTime());
                draft.setValue(reading.value());
                draft.setImageUrl(reading.imageUrl());
                draft.applyMeter(m -> m.setId(meterId));
                draft.setCost(costCalculator.calculateCost(selectType, draft.value()));
            });

            sqlClient.getEntities().saveCommand(readingToSave)
                    .setMode(SaveMode.INSERT_ONLY)
                    .execute();

            log.info("读数记录保存成功，关联仪表 ID: {}", meterId);
            return null;

        } catch (Exception e) {
            log.error("保存读数记录时出错: {}", e.getMessage(), e);
            return "保存读数记录失败，请联系管理员";
        }
    }

    private Reading readMeter(String type, MultipartFile file) throws IOException {
        try {
            MultiValueMap<String, Object> body = createMultipartBody(file);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = createHttpEntity(body);

            MeterReadingStrategy strategy = strategyFactory.getStrategy(type);
            JSONObject response = strategy.readMeter(requestEntity);

            if (response == null) return null;

            log.info(response.toString());

            Integer code = response.getInteger("code");
            if (code != 200) return null;

            JSONObject dataObj = response.getJSONObject("data");
            BigDecimal reading = dataObj.getBigDecimal("reading");

            String imageUrl = uploadToMinio(file);
            return ReadingDraft.$.produce(draft -> {
                draft.setShotTime(LocalDateTime.now());
                draft.setValue(reading);
                draft.setImageUrl(imageUrl);
                draft.setCost(costCalculator.calculateCost(type, reading));
            });

        } catch (Exception e) {
            log.error("仪表读取过程中出现问题: {}", e.getMessage(), e);
            return null;
        }
    }

    private MultiValueMap<String, Object> createMultipartBody(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("image", resource);
        return body;
    }

    private HttpEntity<MultiValueMap<String, Object>> createHttpEntity(MultiValueMap<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(body, headers);
    }

    private String uploadToMinio(MultipartFile file) throws Exception {
        String imageName = Const.MINIO_READING + UUID.randomUUID().toString().replace("-", "");
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();
        minioClient.putObject(args);
        return imageName;
    }
}
