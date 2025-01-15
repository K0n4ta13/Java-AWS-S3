package com.aws.s3.service.impl;

import com.aws.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class S3ServiceImpl implements S3Service {

    @Value("${aws.bucket.name}")
    private String bucketName;
    @Value("${aws.local.download.path}")
    private String localDownloadPath;

    private final S3Client s3Client;

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        this.s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return "Successfully uploaded file: ".concat(Objects.requireNonNull(fileName));
    }

    @Override
    public String downloadFile(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try (FileOutputStream fos = new FileOutputStream(localDownloadPath.concat(fileName))) {
            ResponseInputStream<GetObjectResponse> result = this.s3Client.getObject(getObjectRequest);
            fos.write(result.readAllBytes());
            return "Successfully downloaded file: ".concat(fileName);
        } catch (IOException | S3Exception e) {
            return "Error while downloading file: ".concat(fileName);
        }
    }

    @Override
    public List<String> listFiles() {
         ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                 .bucket(bucketName)
                 .build();

         List<S3Object> s3Objects = this.s3Client.listObjects(listObjectsRequest).contents();
         return s3Objects.stream().map(S3Object::key).toList();
    }

    @Override
    public String deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try {
            this.s3Client.deleteObject(deleteObjectRequest);
            return "Successfully deleted file: ".concat(fileName);
        } catch (S3Exception e) {
            return "Error while deleting file: ".concat(fileName);
        }
    }

    @Override
    public String updateFile(String fileName, MultipartFile file) throws IOException {
        deleteFile(fileName);
        uploadFile(file);

        return "Successfully updated file: ".concat(fileName);
    }

    private boolean thisObjectDoesNotExists(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            this.s3Client.headObject(headObjectRequest);

            return false;
        } catch (S3Exception e) {
            return true;
        }
    }
}
