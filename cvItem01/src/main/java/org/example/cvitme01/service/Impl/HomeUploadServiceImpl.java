package org.example.cvitme01.service.Impl;

import com.alibaba.fastjson2.JSONObject;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.example.cvitme01.entity.dto.Reading;
import org.example.cvitme01.entity.dto.ReadingDraft;
import org.example.cvitme01.service.HomeUploadService;
import org.example.cvitme01.utils.Const;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
            case "watter" -> water(file);
            case "electricity" ->
                // Implement electricity handling here
                    null;
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };
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

        // 将data作为JSONObject而不是String
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
                Reading readingToSave = ReadingDraft.$.produce(draft -> {
                    draft.setShotTime(LocalDateTime.now());
                    draft.setValue(reading);
                    draft.setImageUrl(finalImageName);
                });
                sqlClient.getEntities().saveCommand(readingToSave)
                        .setMode(SaveMode.INSERT_ONLY)
                        .execute();
//                homeUploadRepository.save(readingToSave);
                return readingToSave;
            } catch (Exception e) {
                log.error("图片上传出现问题: {}", e.getMessage(), e);
                return null;
            }
        }

        return null;
    }
}
