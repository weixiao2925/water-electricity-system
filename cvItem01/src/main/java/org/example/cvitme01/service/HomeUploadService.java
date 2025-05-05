package org.example.cvitme01.service;

import org.springframework.web.multipart.MultipartFile;

public interface HomeUploadService {
    String uploadImage(MultipartFile file, String type, int id) throws Exception;
}
