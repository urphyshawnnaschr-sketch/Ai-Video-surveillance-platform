package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Alarm Management Entity
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
@ApiModel(value = "Alarm Management Entity")
@TableName("tbl_biz_report")
public class Report {

    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Camera id")
    @TableField("camera_id")
    private Long cameraId;

    @ApiModelProperty(value = "Algorithm id")
    @TableField("algorithm_id")
    private Long algorithmId;

    @ApiModelProperty(value = "Alert File Path")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "Alert ROI")
    @TableField("params")
    private String params;

    @ApiModelProperty(value = "Type")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "Display Mark show 0- Display 1- not Display")
    @TableField("display")
    private Integer display;

    @ApiModelProperty(value = "Create Time")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "Create Time (ms)")
    @TableField("created_mills")
    private Long createdMills;

    @ApiModelProperty(value = "Review Status, 0 Not Reviewed / not Process,1 Reviewed (Discard Abandon)/ Process,5 Create Annotation Project, 11 Deleted")
    @TableField("audit_state")
    private Integer auditState;

    @ApiModelProperty(value = "Annotation Project ID")
    @TableField("project_id")
    private Long projectId;

    @ApiModelProperty(value = "Review Result Status 0- no 1- Process 2- Closed 3- self Dynamic Process")
    @TableField("audit_result")
    private Integer auditResult;

    @ApiModelProperty(value = "Create Time String")
    @TableField(exist = false)
    private String createdStr;

    @ApiModelProperty(value = "Camera Name")
    @TableField(exist = false)
    private String cameraName;

    @ApiModelProperty(value = "Algorithm Name")
    @TableField(exist = false)
    private String algorithmName;

    @ApiModelProperty(value = "Type Name")
    @TableField(exist = false)
    private String typeName;

    @ApiModelProperty(value = "Confidence")
    @TableField(exist = false)
    private String conf;

    @ApiModelProperty(value = "Alert Level")
    @TableField(exist = false)
    private AlarmLevel alarmLevel;

    @ApiModelProperty(value = "rois Frame")
    @TableField("rois")
    private String rois;

    @ApiModelProperty(value = "lines Frame")
    @TableField("`lines`")
    private String lines;

    @ApiModelProperty(value = "Process Time")
    @TableField("audit_at")
    private Date auditAt;

    @ApiModelProperty(value = "Video File Path")
    @TableField("video_path")
    private String videoPath;

    @ApiModelProperty(value = "Box ID")
    @TableField("box_id")
    private Long boxId;

    @ApiModelProperty(value = "belong belong Department ID")
    @TableField("depart_id")
    private Long departId;

    @ApiModelProperty(value = "Recording ID")
    @TableField("record_id")
    private Long recordId;

    @ApiModelProperty(value = "Whether via Push")
    @TableField("pushed")
    private Integer pushed;

    @ApiModelProperty(value = "Push Result Info")
    @TableField("push_msg")
    private String pushMsg;

    @ApiModelProperty(value = "Push Time")
    @TableField("push_time")
    private Date pushTime;

    @ApiModelProperty(value = "Review Type, 0- self Dynamic Review,1- Manual Review")
    @TableField("audit_type")
    private Integer auditType;

    @ApiModelProperty(value = "Review Remark")
    @TableField("audit_remark")
    private String auditRemark;

    @ApiModelProperty(value = "Whether Manual Push")
    @TableField(exist = false)
    private boolean mustPush;

    @ApiModelProperty(value = "Whether Manual Process")
    @TableField(exist = false)
    private boolean mustAudit;

    @ApiModelProperty(value = "Process Hour long, like 3min,30s")
    @TableField(exist = false)
    private String auditTimeLen;

    @ApiModelProperty(value = "The belong Group")
    @TableField(exist = false)
    private String groupNames;

    @ApiModelProperty(value = "The belong Calculate Device")
    @TableField(exist = false)
    private String boxName;

    @ApiModelProperty(value = "Process Hour long (s)")
    @TableField("handle_time")
    private Long handleTime;

    @ApiModelProperty(value = "Annotation 0- Pending Fixed, 1- Correct Report, 2- wrong Report")
    @TableField("mark")
    private Integer mark;
}
