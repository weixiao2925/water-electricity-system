package org.example.cvitme01.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

public interface ImageService {
    void fetchImageFromMinio(OutputStream stream, String image) throws Exception;
    String uploadAvatar(MultipartFile file, int id) throws IOException;
}
