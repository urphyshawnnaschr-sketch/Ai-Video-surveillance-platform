package com.yihecode.camera.ai.config;

import cn.hutool.core.util.StrUtil;
import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* minio Config
*/
@Slf4j
@Configuration
@Data
public class MinioConfig {

    @Value("${minio-conf.url:127.0.0.1:9000}")
    private String url;

    @Value("${minio-conf.key:}")
    private String accessKey;

    @Value("${minio-conf.secret:}")
    private String secretKey;

    @Bean
    public MinioClient getMinioClient() {
        if(StrUtil.isBlank(url) || StrUtil.isBlank(accessKey) || StrUtil.isBlank(secretKey)) {
            log.info("minio::: Param Config not Complete whole (url,accessKey, secretKey)");
            return null;
        }

        return MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
    }
}
