package com.aws.s3.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {

    String uploadFile(MultipartFile file) throws IOException;

    String downloadFile(String fileName);

    List<String> listFiles();

    String deleteFile(String fileName);

    String updateFile(String fileName, MultipartFile file) throws IOException;
}
