package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
* Description: Camera Region Node < Tree result structure > Entity, Fit allocate new Version Camera Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
@ApiModel(value = "Camera Region Node Entity")
@TableName("tbl_biz_location")
public class Location {

    /**
* Primary Key
*/
    @ApiModelProperty(value = "Data ID")
    private Long id;

    /**
* Bit set Name
*/
    @ApiModelProperty(value = "Name",required = true)
    @TableField("name")
    //@NotNull(message ="Name cannot be empty")
private String name;

/**
* Sort Value
*/
@ApiModelProperty(value ="Sort Value")
@TableField("sort")
private Integer sort;

/**
* up Level Bit set Node
*/
@ApiModelProperty(value ="up Level id")
@TableField("parent_id")
//@NotNull(message ="up Level id cannot be empty")
private Long parentId;

/**
* up Level Bit set Name, from Root Node to up Level Node
*/
@ApiModelProperty(value ="up Level Param Name", example ="n1,n2,n3")
@TableField("parent_names")
private String parentNames;

/**
* up Level Region Name
*/
@ApiModelProperty(value ="up Level Region Name")
@TableField(exist = false)
private String parentName;

/**
* up Level Region ids
*/
private String parentIds;

/**
* Latitude
*/
@ApiModelProperty(value ="Longitude")
@TableField("latitude")
private Float latitude;

/**
* Longitude
*/
@ApiModelProperty(value ="Latitude")
@TableField("longitude")
private Float longitude;

@ApiModelProperty(value ="ip Address")
//@NotNull(message ="ip Address cannot be empty")
private String ipAddr;

@ApiModelProperty(value ="1 Region 2 Box")
private String type ="2";

@ApiModelProperty(value ="Whether Online 1 Online 0 Offline")
@TableField(exist = false)
private String online;

@ApiModelProperty(value ="Data Source 1 Camera Management 2 Box Management")
//@NotNull(message ="Data Source cannot be empty")
private String locationType;

@ApiModelProperty(value ="Box No")
@TableField("box_no")
private String boxNo;

@ApiModelProperty(value ="Box most after Heartbeat Time")
@TableField("box_heart_time")
private Long boxHeartTime;

@ApiModelProperty(value ="Box Version")
@TableField("box_version")
private String boxVersion;

@ApiModelProperty(value ="Box Version File Address")
@TableField("box_file")
private String boxFile;

@ApiModelProperty(value ="Whether Default 0- No 1- is")
@TableField("is_def")
private Integer isDef;

@ApiModelProperty(value ="make build Factory merchant")
@TableField("makers")
private String makers;

@ApiModelProperty(value ="Device Mode")
@TableField("device_mode")
private String deviceMode;

@ApiModelProperty(value ="cpu type No")
@TableField("cpu_version")
private String cpuVersion;

@ApiModelProperty(value ="inner Core Version")
@TableField("kernel_version")
private String kernelVersion;

@ApiModelProperty(value ="Operation System Version")
@TableField("os_version")
private String osVersion;

@ApiModelProperty(value ="Disk total Quantity")
@TableField("disk_total")
private Long diskTotal;

@ApiModelProperty(value ="inner Store total Quantity")
@TableField("memory_total")
private Long memoryTotal;

@ApiModelProperty(value ="Lingxi Drive Dynamic Version")
@TableField("lyndriver_version")
private String lyndriverVersion;

@ApiModelProperty(value ="Lingxi sdk Version")
@TableField("lynsdk_version")
private String lynsdkVersion;

@ApiModelProperty(value ="belong belong Organization")
@TableField("depart_id")
private Long departId;

@ApiModelProperty(value ="Machine code")
@TableField("active_sn")
private String activeSn;

@ApiModelProperty(value ="Activate code")
@TableField("active_code")
private String activeCode;

@ApiModelProperty(value ="Activate Status")
@TableField("active_status")
private Integer activeStatus;

@ApiModelProperty(value ="Activate Time")
@TableField("active_date")
private Date activeDate;

@ApiModelProperty(value ="Hardware Type")
@TableField("platform")
private String platform;

@ApiModelProperty(value ="make Use Use Path")
@TableField("use_type")
private Integer useType;

@ApiModelProperty(value ="make Use sub Number")
@TableField("use_num")
private Integer useNum;

@ApiModelProperty(value ="Disk total Quantity (Format Data)")
@TableField(exist = false)
private String diskTotalStr;

@ApiModelProperty(value ="Disk can Use total Quantity (Format Data)")
@TableField(exist = false)
private String diskFreeTotalStr;

@ApiModelProperty(value ="inner Store total Quantity (Format Data)")
@TableField(exist = false)
private String memoryTotalStr;

@ApiModelProperty(value ="Relate Camera Count")
@TableField(exist = false)
private Integer cameraNum;

@ApiModelProperty(value ="belong belong Organization Name")
@TableField(exist = false)
private String departName;

@ApiModelProperty(value ="Whether Check select", dataType ="boolean")
@TableField(exist = false)
private boolean checked = false;
}
