package org.example.cvitme01.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.service.ImageService;
import io.minio.errors.ErrorResponseException;
import org.example.cvitme01.utils.Const;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/avatar/**")
    public void imageFetch(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "image/jpeg");
        this.fetchImage(request, response);
    }

    @PostMapping("/avatar-upload")
    public RestBean<String> uploadAvatar(@RequestParam("file")MultipartFile file,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id) throws IOException {
//        if (file.getSize() >1024*100)
//            return  RestBean.failure(400,"头像图片不能大于100kb");
        log.info("正在进行头像上传操作");
        String url=imageService.uploadAvatar(file, id);
        if (url != null) {
            log.info("头像上传成功，大小：{}", file.getSize());
            return RestBean.success(url);
        }else {
            return RestBean.failure(400,"头像上传失败，请联系管理员");
        }

    }

    private void fetchImage(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        String imagePath = request.getServletPath();
        imagePath = imagePath.substring("/api/image/avatar/".length());
        ServletOutputStream stream = response.getOutputStream();

        if (imagePath.isEmpty()) {
            response.setStatus(404);
            stream.println(RestBean.failure(404, "Not found").toString());
        } else {
            try {
                // 传入完整的Minio路径
                imageService.fetchImageFromMinio(stream, "avatar/" + imagePath);
                response.setHeader("Cache-Control", "max-age=2592000");
            } catch (ErrorResponseException e) {
                if (e.response().code() == 404) {
                    response.setStatus(404);
                    stream.println(RestBean.failure(404, "Not found").toString());
                } else {
                    log.error("从Minio获取图片失败：{}", e.getMessage(), e);
                }
            }
        }
    }
}
