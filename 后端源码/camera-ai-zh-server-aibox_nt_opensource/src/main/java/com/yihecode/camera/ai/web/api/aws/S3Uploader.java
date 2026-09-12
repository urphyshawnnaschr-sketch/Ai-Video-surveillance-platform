package com.yihecode.camera.ai.web.api.aws;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class S3Uploader {
    private final S3Client s3Client;
    private final String bucketName;

    public S3Uploader(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public boolean uploadFile(String key, String filePath) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest,
                    RequestBody.fromFile(new File(filePath)));

            System.out.println("File Upload success,ETag:" + response.eTag());
            return true;
        } catch (S3Exception e) {
            System.err.println("S3 Error:" + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Upload failed:" + e.getMessage());
            return false;
        }
    }

    //Simple Change part Segment Upload Example
public void uploadLargeFile(String bucketName, String key, String filePath) {
// Init part Segment Upload
CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(
CreateMultipartUploadRequest.builder()
.bucket(bucketName)
.key(key)
.build());

String uploadId = createResponse.uploadId();

// Split File and Upload each part part (real International real current Need Process File Split Logic)
List<CompletedPart> completedParts = new ArrayList<>();
//... Upload each part part and receive Set PartETag

// Complete Complete part Segment Upload
CompletedMultipartUpload completedUpload = CompletedMultipartUpload.builder()
.parts(completedParts)
.build();

s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
.bucket(bucketName)
.key(key)
.uploadId(uploadId)
.multipartUpload(completedUpload)
.build());
}
}
