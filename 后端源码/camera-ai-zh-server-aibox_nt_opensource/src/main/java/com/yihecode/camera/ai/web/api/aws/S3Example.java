package com.yihecode.camera.ai.web.api.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Example {
    public static void main(String[] args) {
        //Config Client
S3Client s3Client = S3ClientFactory.createS3Client(
"BABB0C183E64D085FB05",
"siLShXyOOZOsZagW0XgnmUphnZgAAAGZPmTQhcCQ",
Region.US_EAST_1);

String bucketName ="aisecbuckt01";

// Upload File
S3Uploader uploader = new S3Uploader(s3Client, bucketName);
uploader.uploadFile("224a155c88a541819101a5f1f458c8b3_draw","d:/Users/test20/Desktop/224a155c88a541819101a5f1f458c8b3_draw.jpg");

// Column out File
S3Lister lister = new S3Lister(s3Client);
lister.listBucketObjects(bucketName);
//
// // Download File
// S3Downloader downloader = new S3Downloader(s3Client, bucketName);
// downloader.downloadFile("documents/report.pdf","/local/path/downloaded-report.pdf");
//
// // Delete File
// S3Deleter deleter = new S3Deleter(s3Client);
// deleter.deleteObject(bucketName,"documents/report.pdf");

// Close Client
s3Client.close();
}
}
