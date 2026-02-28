package com.example.timetable.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String bucket;

    public MinioService(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.port:9000}") int port,
            @Value("${minio.use-ssl:false}") boolean useSsl,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket}") String bucket) {
        // Construct full endpoint with protocol and port
        String fullEndpoint = constructEndpoint(endpoint, port, useSsl);
        
        this.minioClient = MinioClient.builder()
                .endpoint(fullEndpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucket = bucket;
    }

    private String constructEndpoint(String endpoint, int port, boolean useSsl) {
        String protocol = useSsl ? "https" : "http";
        String defaultPort = (useSsl && port == 443) || (!useSsl && port == 80) ? "" : (":" + port);
        return protocol + "://" + endpoint + defaultPort;
    }

    public String upload(MultipartFile file, String objectName) throws Exception {
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
        // Return object identifier (objectName). URL exposure depends on your infra.
        return objectName;
    }

    public String presignPutUrl(String objectName, int expirySeconds) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucket)
                        .object(objectName)
                        .expiry(expirySeconds, TimeUnit.SECONDS)
                        .build()
        );
    }
}
