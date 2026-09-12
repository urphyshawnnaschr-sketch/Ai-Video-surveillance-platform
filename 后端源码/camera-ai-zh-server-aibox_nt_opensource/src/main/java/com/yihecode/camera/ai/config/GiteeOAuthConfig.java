package com.yihecode.camera.ai.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
* Gitee Third Party Login oauth Config
*
* @auhtor 465769438@qq.com
* @since 2026/1/14
*/
@Component
@ConfigurationProperties(prefix = "gitee-oauth-conf")
@Getter
@Setter
public class GiteeOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
}