package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Stream Media Node Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Stream Media Node Management")
@Data
@TableName("tbl_biz_media_server")
public class MediaServer {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "ip Address")
    @TableField("ip")
    private String ip;

    @ApiModelProperty(value = "http Port")
    @TableField("http_port")
    private Integer httpPort;

    @ApiModelProperty(value = "rtsp Port")
    @TableField("rtsp_port")
    private Integer rtspPort;

    @ApiModelProperty(value = "rtc Port")
    @TableField("rtc_port")
    private Integer rtcPort;

    @ApiModelProperty(value = "rtp Receive Port Range")
    @TableField("rtp_port_range")
    private String rtpPortRange;

    @ApiModelProperty(value = "rtp Send Port Range")
    @TableField("send_rtp_port_range")
    private String sendRtpPortRange;

    @ApiModelProperty(value = "Password")
    @TableField("secret")
    private String secret;
}