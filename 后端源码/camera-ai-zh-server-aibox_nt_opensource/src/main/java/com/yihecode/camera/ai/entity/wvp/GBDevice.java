package com.yihecode.camera.ai.entity.wvp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(value = "GB Standard Device Info")
@Data
@TableName("wvp_device")
public class GBDevice {

    private Long id;

    @TableField("device_id")
    private String deviceId;

    @TableField("name")
    private String name;

    @TableField("manufacturer")
    private String manufacturer;

    @TableField("model")
    private String model;

    @TableField("firmware")
    private String firmware;

    @TableField("transport")
    private String transport;

    @TableField("stream_mode")
    private String streamMode;

    //DEFAULT'0'
@TableField("on_line")
private Integer onLine;

@TableField("register_time")
private String registerTime;

@TableField("keepalive_time")
private String keepaliveTime;

@TableField("ip")
private String ip;

@TableField("create_time")
private String createTime;

@TableField("update_time")
private String updateTime;

@TableField("port")
private Integer port;

@TableField("expires")
private Integer expires;

@TableField("subscribe_cycle_for_catalog")
private Integer subscribeCycleForCatalog;

@TableField("mobile_position_submission_interval")
private Integer mobilePositionSubmissionInterval;

@TableField("subscribe_cycle_for_alarm")
private Integer subscribeCycleForAlarm;

@TableField("host_address")
private String hostAddress;

@TableField("charset")
private String charset;

// DEFAULT'0'
@TableField("ssrc_check")
private Integer ssrcCheck;

@TableField("geo_coord_sys")
private String geoCoordSys;

@TableField("media_server_id")
private String mediaServerId;

@TableField("custom_name")
private String customName;

@TableField("sdp_ip")
private String sdpIp;

@TableField("local_ip")
private String localIp;

@TableField("password")
private String password;

// DEFAULT'0',
@TableField("as_message_channel")
private Integer asMessageChannel;

@TableField("keepalive_interval_time")
private Integer keepaliveIntervalTime;

@TableField("broadcast_push_after_ack")
private Integer broadcastPushAfterAck;

@TableField(exist = false)
private Integer channelNums;

}
