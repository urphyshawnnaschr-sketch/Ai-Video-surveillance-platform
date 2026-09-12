package com.yihecode.camera.ai.web.api.aws;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3Lister {
    private final S3Client s3Client;

    public S3Lister(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void listBucketObjects(String bucketName) {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            System.out.println("Storage Bucket" + bucketName + "in Object:");
            for (S3Object object : listResponse.contents()) {
                System.out.println("-" + object.key() + "(big small:" + object.size() + "bytes)");
            }
        } catch (S3Exception e) {
            System.err.println("Column out Object Failed:" + e.getMessage());
        }
    }
}
