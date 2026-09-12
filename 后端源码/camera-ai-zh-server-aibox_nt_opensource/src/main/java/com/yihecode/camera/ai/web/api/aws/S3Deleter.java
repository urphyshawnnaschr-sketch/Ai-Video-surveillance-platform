package com.yihecode.camera.ai.web.api.aws;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3Deleter {
    private final S3Client s3Client;

    public S3Deleter(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public boolean deleteObject(String bucketName, String key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            System.out.println("Object Delete success:" + key);
            return true;
        } catch (S3Exception e) {
            System.err.println("Delete failed:" + e.getMessage());
            return false;
        }
    }
}
