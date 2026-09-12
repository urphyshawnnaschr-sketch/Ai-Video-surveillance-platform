package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
* Camera Relate Social Platform Push, Feishu, WeWork, DingTalk
*/
@Data
@TableName("tbl_biz_social_result")
public class SocialResult {

    private Long id;

    @TableField("social_id")
    private Long socialId;

    @TableField("state")
    private Integer state;

    @TableField("created_at")
    private Date createdAt;

    @TableField("camera_name")
    private String cameraName;

    @TableField("algorithm_name")
    private String algorithmName;

    @TableField("send_text")
    private String sendText;

    @TableField("img_url")
    private String imgUrl;

    @TableField("error_detail")
    private String errorDetail;

    @TableField("report_id")
    private Long reportId;

    @TableField("resend_num")
    private Long resendNum;

    /**
* Business Type (1- Alarm,2- Box,3- Camera)
*/
    @TableField("business_type")
    private Integer businessType;
}
