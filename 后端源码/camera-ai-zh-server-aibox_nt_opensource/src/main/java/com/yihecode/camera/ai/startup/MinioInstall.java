package com.yihecode.camera.ai.startup;

import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.SetBucketPolicyArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* minio Init
*/
@Slf4j
@Component
public class MinioInstall {

    @Autowired
    private MinioConfig minioConfig;

    public void install() {
        try {
            if(minioConfig.getMinioClient() == null) {
                return ;
            }

            boolean bucketExists = minioConfig.getMinioClient().bucketExists(BucketExistsArgs.builder().bucket("record").build());
            //like Result bucket does not exist, rule Create, and Create Access Strategy
if(!bucketExists) {
// Create bucket
minioConfig.getMinioClient().makeBucket(MakeBucketArgs.builder().bucket("record").build());

// Create Default Strategy
String policy ="{\n"+
"\"Version\": \"2012-10-17\",\n"+
"\"Statement\": [\n"+
"{\n"+
"\"Effect\": \"Allow\",\n"+
"\"Principal\": {\n"+
"\"AWS\": [\n"+
"\"*\"\n"+
"]\n"+
"},\n"+
"\"Action\": [\n"+
"\"s3:GetBucketLocation\",\n"+
"\"s3:ListBucket\"\n"+
"],\n"+
"\"Resource\": [\n"+
"\"arn:aws:s3:::record\"\n"+
"]\n"+
"},\n"+
"{\n"+
"\"Effect\": \"Allow\",\n"+
"\"Principal\": {\n"+
"\"AWS\": [\n"+
"\"*\"\n"+
"]\n"+
"},\n"+
"\"Action\": [\n"+
"\"s3:GetObject\"\n"+
"],\n"+
"\"Resource\": [\n"+
"\"arn:aws:s3:::record/*\"\n"+
"]\n"+
"}\n"+
"]\n"+
"}";
minioConfig.getMinioClient().setBucketPolicy(SetBucketPolicyArgs.builder().bucket("record").config(policy).build());
log.info("minio::: Connection success, Create Bucket [record], and Set policy, Success!!!");
} else {
log.info("minio::: Connection Complete, Bucket [record] Exist, no Need Set, Success!!!");
}
} catch (Exception e) {
log.error("minio::: Connection failed or Init failed", e);
}
}
}
