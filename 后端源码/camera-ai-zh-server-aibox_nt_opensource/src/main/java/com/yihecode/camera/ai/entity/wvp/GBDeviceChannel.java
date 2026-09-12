package com.yihecode.camera.ai.entity.wvp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(value = "GB Standard Device Channel Info")
@Data
@TableName("wvp_device_channel")
public class GBDeviceChannel {

    private Long id;

    @TableField("channel_id")
    private String channelId;

    @TableField("name")
    private String name;

    @TableField("custom_name")
    private String customName;

    @TableField("manufacture")
    private String manufacture;

    @TableField("model")
    private String model;

    @TableField("owner")
    private String owner;

    @TableField("civil_code")
    private String civilCode;

    @TableField("block")
    private String block;

    @TableField("address")
    private String address;

    @TableField("parent_id")
    private String parentId;

    @TableField("safety_way")
    private Integer safetyWay;

    @TableField("register_way")
    private Integer registerWay;

    @TableField("cert_num")
    private String certNum;

    @TableField("certifiable")
    private Integer certifiable;

    @TableField("err_code")
    private Integer errCode;

    @TableField("end_time")
    private String endTime;

    @TableField("secrecy")
    private String secrecy;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("port")
    private Integer port;

    @TableField("password")
    private String password;

    @TableField("ptz_type")
    private Integer ptzType;

    @TableField("custom_ptz_type")
    private Integer customPtzType;

    @TableField("status")
    private Integer status;

    @TableField("longitude")
    private Double longitude;

    @TableField("custom_longitude")
    private Double customLongitude;

    @TableField("latitude")
    private Double latitude;

    @TableField("custom_latitude")
    private Double customLatitude;

    @TableField("stream_id")
    private String streamId;

    @TableField("device_id")
    private String deviceId;

    @TableField("parental")
    private String parental;

    @TableField("has_audio")
    private Integer hasAudio;

    @TableField("create_time")
    private String createTime;

    @TableField("update_time")
    private String updateTime;

    @TableField("sub_count")
    private Integer subCount;

    @TableField("longitude_gcj02")
    private Double longitudeGcj02;

    @TableField("latitude_gcj02")
    private Double latitudeGcj02;

    @TableField("longitude_wgs84")
    private Double longitudeWgs84;

    @TableField("latitude_wgs84")
    private Double latitudeWgs84;

    @TableField("business_group_id")
    private String businessGroupId;

    @TableField("gps_time")
    private String gpsTime;

    @TableField("stream_identification")
    private String streamIdentification;

    @TableField("camera_id")
    private Long cameraId;

    @TableField("location_id")
    private Long locationId;

    @TableField(exist = false)
    private String deviceName;
}
