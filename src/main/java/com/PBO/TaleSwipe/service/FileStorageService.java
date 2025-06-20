package com.PBO.TaleSwipe.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String filename);
    Resource loadFile(String filename);
}
