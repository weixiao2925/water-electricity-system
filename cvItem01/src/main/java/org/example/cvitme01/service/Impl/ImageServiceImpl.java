package org.example.cvitme01.service.Impl;

import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.example.cvitme01.entity.dto.Account;
import org.example.cvitme01.entity.dto.AccountFetcher;
import org.example.cvitme01.entity.dto.AccountTable;
import org.example.cvitme01.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final JSqlClient sqlClient;

    @Override
    public void fetchImageFromMinio(OutputStream stream, String image) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("study")
                .object(image)
                .build();
        GetObjectResponse response = minioClient.getObject(args);
        IOUtils.copy(response, stream);
    }

    @Override
    @Transactional
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName= UUID.randomUUID().toString().replace("-","");
        imageName="/avatar/"+imageName;
        AccountTable table = AccountTable.$;
        AccountFetcher fetcher = AccountFetcher.$
                .avatar();
        PutObjectArgs args=PutObjectArgs.builder()
                .bucket("study")
                .stream(file.getInputStream(),file.getSize(),-1)
                .object(imageName)
                .build();
        try{
            minioClient.putObject(args);

            Account account = sqlClient.createQuery(table)
                    .where(table.id().eq((long)id))
                    .select(table.fetch(fetcher))
                    .fetchOneOrNull();
            if (account == null) return null;
            String avatar = account.avatar();

            this.deleteOldAvatar(avatar);

            if (sqlClient.createUpdate(table)
                    .set(table.avatar(), imageName)
                    .where(table.id().eq(Long.valueOf(id)))
                    .execute() > 0) {
                return imageName;
            }

            return null;

        }catch (Exception e){
            log.error("图片上传出现问题: {}", e.getMessage(), e);
            return null;
        }
    }

    private void deleteOldAvatar(String avatar) throws Exception{
        if (avatar==null || avatar.isEmpty()) return;
        RemoveObjectArgs remove=RemoveObjectArgs.builder()
                .bucket("study")
                .object(avatar)
                .build();
        minioClient.removeObject(remove);
    }
}
