package com.yihecode.camera.ai.web.api.aws;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

public class S3ClientFactory {
    public static S3Client createS3Client(String accessKey, String secretKey, Region region) {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(region)
                .endpointOverride(URI.create("http://10.150.242.80:5080"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))

                .build();
    }
}
