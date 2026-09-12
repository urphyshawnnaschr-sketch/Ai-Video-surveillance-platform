package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
* Camera Relate Social Platform Push, Feishu, WeWork, DingTalk
*/
@Data
@TableName("tbl_biz_social_config")
public class SocialConfig {

    private Long id;

    @TableField("camera_id")
    private Long cameraId;

    @TableField("social_id")
    private Long socialId;

    @TableField("algorithm_id")
    private Long algorithmId;
}
