package com.diu.lostfinder.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {

    List<String> uploadImages(List<MultipartFile> files) throws IOException;

    String uploadSingleImage(MultipartFile file) throws IOException;

    void deleteImage(String imageUrl) throws IOException;
}