package com.aws.s3.controller;

import com.aws.s3.service.S3Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/download/{fileName}")
    public String downloadFile(@PathVariable String fileName) {
        return this.s3Service.downloadFile(fileName);
    }

    @GetMapping
    public List<String> listFiles() {
        return this.s3Service.listFiles();
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam MultipartFile file) throws IOException {
        return this.s3Service.uploadFile(file);
    }

    @DeleteMapping("/{fileName}")
    public String deleteFile(@PathVariable String fileName) {
        return this.s3Service.deleteFile(fileName);
    }

    @PutMapping("/{fileName}")
    public String updateFile(@PathVariable String fileName, @RequestParam MultipartFile file) throws IOException {
        return this.s3Service.updateFile(fileName, file);
    }
}
