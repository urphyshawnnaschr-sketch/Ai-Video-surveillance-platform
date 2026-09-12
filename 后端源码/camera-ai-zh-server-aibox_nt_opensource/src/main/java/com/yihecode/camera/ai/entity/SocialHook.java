package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
* Social Platform Push, Feishu, WeWork, DingTalk
*/
@Data
@TableName("tbl_biz_social_hook")
public class SocialHook {

    private Long id;

    private String name;

    private String webhook;

    private String signature;

    private String remark;

    private Integer state;

    //0- Alert,1- Box,2- Camera
private Integer type;

@TableField("app_id")
private String appId;

@TableField("app_secret")
private String appSecret;

@TableField("proxy_addr")
private String proxyAddr;
}
