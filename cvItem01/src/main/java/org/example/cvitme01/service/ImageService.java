package org.example.cvitme01.service;

import java.io.OutputStream;

public interface ImageService {
    void fetchImageFromMinio(OutputStream stream, String image) throws Exception;
}
