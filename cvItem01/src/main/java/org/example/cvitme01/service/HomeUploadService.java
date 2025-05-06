package org.example.cvitme01.service;

import org.example.cvitme01.entity.dto.Reading;
import org.springframework.web.multipart.MultipartFile;

public interface HomeUploadService {
    Reading uploadImage(MultipartFile file, String type, int id) throws Exception;
}
