package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* Algorithm Management Entity
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Algorithm Management Entity")
@Data
@TableName(value = "tbl_biz_algorithm", autoResultMap = true)
public class Algorithm implements Serializable {

    @ApiModelProperty(value = "Primary Key", dataType = "long", example = "1234567890", required = true)
    private Long id;

    @ApiModelProperty(value = "Model Name", dataType = "string", example = "Fire Alarm", required = true)
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Model English Name", dataType = "string", example = "fire", required = true)
    @TableField("name_en")
    private String nameEn;

    @ApiModelProperty(value = "English Name", dataType = "string", example = "fire", required = true)
    @TableField("english_name")
    private String englishName;

    @ApiModelProperty(value = "Model Path", dataType = "string", example = "/data/models/")
    @TableField("model_path")
    private String modelPath;

    @ApiModelProperty(value = "Model Label Ids", dataType = "json", example = "[1,2]")
    @TableField(value="tag_ids", typeHandler = FastjsonTypeHandler.class)
    private Set<Long> tagIds;

    @ApiModelProperty(value = "Create Time", dataType = "date")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time", dataType = "date")
    @TableField("updated_at")
    private Date updatedAt;

    @ApiModelProperty(value = "demo Video uri")
    @TableField(exist = false)
    private String videoUri;

    @ApiModelProperty(value = "Share Type", dataType = "int", example = "", notes = "0- Share,1- Single Share")
    @TableField("share_mode")
    private Integer shareMode;

    @ApiModelProperty(value = "Zoom Put than Example (Deprecated)", dataType = "float", example = "", notes = "Deprecated")
    @TableField("scale_ratio")
    private Float scaleRatio;

    /**
* Relate Count Config Display
*/
    @ApiModelProperty(value = "Count Config Display", dataType = "int", example = "", notes = "0- not Display (Default) 1- Display")
    @TableField("statics_flag")
    private Integer staticsFlag;

    @ApiModelProperty(value = "Alert Level id", dataType = "long", notes = "Deprecated")
    @TableField("alarm_level_id")
    private Long alarmLevelId;

    @ApiModelProperty(value = "Hardware Platform", dataType = "string")
    @TableField("platform")
    private String platform;

    @ApiModelProperty(value = "Cover Image Address", dataType = "string")
    @TableField("image")
    private String image;

    @ApiModelProperty(value = "Simple Description", dataType = "string")
    @TableField("marks")
    private String marks;

    @ApiModelProperty(value = "Detail Detail Description", dataType = "string")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "Detail", dataType = "string")
    @TableField("detail")
    private String detail;

    @ApiModelProperty(value = "Sort", dataType = "int")
    @TableField("sort")
    private String sort;

    @ApiModelProperty(value = "Alert Audio File", dataType = "string")
    @TableField("sound_file")
    private String soundFile;

    @ApiModelProperty(value = "Third Party Algorithm Push, 0- not Push,1- Push")
    @TableField("push_enable")
    private Integer pushEnable;

    /**
* Relate Count Config Display, turn for checked or empty String
*/
    @TableField(exist = false)
    private String staticsFlagVal;

    /**
* Alert Level
*/
    @TableField(exist = false)
    private AlarmLevel alarmLevel;

    /**
* Alert Level Color Transparent Degree
*/
    @TableField(exist = false)
    private String alarmLevelColorTransparent;

    /**
* Detail page map
*/
    @TableField(exist = false)
    private Map<String, Object> detailMap;

    /**
* Detail page map
*/
    @TableField(exist = false)
    private List<String> platforms;

    /**
* Whether Exist Local File
*/
    @TableField(exist = false)
    private Boolean hasLocalFile;

    @TableField(exist = false)
    private Boolean hasGitFile;

    @TableField(exist = false)
    private Boolean hasUpdate;

    @TableField(exist = false)
    private MultipartFile imageFile;

    /**
* Download Status (Download in, Download not Enabled)
*/
    @TableField(exist = false)
    private String downloadState;

    @ApiModelProperty(value = "File Name Name (Deprecated)", dataType = "string", example = "", notes = "Deprecated")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "Image original start Width (Deprecated)", dataType = "int", example = "", notes = "Deprecated")
    @TableField("file_width")
    private Integer fileWidth;

    @ApiModelProperty(value = "Image original start high Degree (Deprecated)", dataType = "int", example = "", notes = "Deprecated")
    @TableField("file_height")
    private Integer fileHeight;

    @ApiModelProperty(value = "Canvas high Degree (Deprecated)", dataType = "int", example = "", notes = "Deprecated")
    @TableField("canvas_height")
    private Integer canvasHeight;

    @ApiModelProperty(value = "Canvas Width (Deprecated)", dataType = "int", example = "", notes = "Deprecated")
    @TableField("canvas_width")
    private Integer canvasWidth;

    @ApiModelProperty(value = "Snapshot Frequency (Deprecated)", dataType = "int", example = "", notes = "Deprecated")
    @TableField("frequency")
    private Integer frequency;

    @ApiModelProperty(value = "Alert Interval Time (Deprecated)", dataType = "int", example = "", notes = "Deprecated")
    @TableField("interval_time")
    private Integer intervalTime;

    @ApiModelProperty(value = "Region Mark Param (Deprecated)", dataType = "string", example = "", notes = "Deprecated")
    @TableField("params")
    private String params;

    @ApiModelProperty(value = "Collect ID,0- Close,1- Enable")
    @TableField("collect_flag")
    private Integer collectFlag;

    @ApiModelProperty(value = "Collect Confidence")
    @TableField("collect_confidence")
    private Float collectConfidence;

    @ApiModelProperty(value = "Data Collect Start Time")
    @TableField("collect_start_time")
    private Long collectStartTime;

    @ApiModelProperty(value = "Data Collect End Time")
    @TableField("collect_end_time")
    private Long collectEndTime;

    @ApiModelProperty(value = "amount outer Expand Param,json String")
    @TableField("extras")
    private String extras;

    @ApiModelProperty(value = "Camera Config Confidence")
    @TableField(exist = false)
    private Float cameraConfidence;

    @ApiModelProperty(value = "inner net / outer net Mode")
    @TableField(exist = false)
    private boolean ossNet;
}

