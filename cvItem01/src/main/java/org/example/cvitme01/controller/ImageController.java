package org.example.cvitme01.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.service.ImageService;
import io.minio.errors.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
