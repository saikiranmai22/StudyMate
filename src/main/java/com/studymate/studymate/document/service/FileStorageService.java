package com.studymate.studymate.document.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String store(MultipartFile file) throws IOException;

    byte[] load(String storageKey) throws IOException;

    void delete(String storageKey) throws IOException;
}
//Today, we'll store PDFs locally.
//Later, we can switch to AWS S3 or another object storage provider without changing the document service's core logic.
//The interface hides the storage implementation from the rest of the application.