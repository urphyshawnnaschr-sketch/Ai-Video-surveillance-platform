package com.yihecode.camera.ai.web.api.aws;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.FileOutputStream;
import java.io.IOException;

public class S3Downloader {
    private final S3Client s3Client;
    private final String bucketName;

    public S3Downloader(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public boolean downloadFile(String key, String destinationPath) {
        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build())) {

            try (FileOutputStream fos = new FileOutputStream(destinationPath)) {
                byte[] readBuf = new byte[1024 * 34];
                int readLen;
                while ((readLen = s3Object.read(readBuf)) > 0) {
                    fos.write(readBuf, 0, readLen);
                }
            }

            System.out.println("File Download success:" + destinationPath);
            return true;
        } catch (S3Exception e) {
            System.err.println("S3 Error:" + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("File Operation Error:" + e.getMessage());
            return false;
        }
    }
}