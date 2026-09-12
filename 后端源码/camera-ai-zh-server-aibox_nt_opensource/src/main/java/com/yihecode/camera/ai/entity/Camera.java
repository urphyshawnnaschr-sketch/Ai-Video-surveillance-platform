package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* Camera Entity
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
@ApiModel(value = "Camera Entity")
@TableName("tbl_biz_camera")
public class Camera {

    @ApiModelProperty(value = "Data ID")
    private Long id;

    @ApiModelProperty(value = "Camera Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Camera RTSP Address", example = "rtsp://user:pass@ip:554")
    @TableField("rtsp_url")
    private String rtspUrl;

    @ApiModelProperty(value = "Camera RTSP Address 2", example = "rtsp://user:pass@ip:554")
    @TableField("rtsp_url2")
    private String rtspUrl2;

    @ApiModelProperty(value = "Action Status", example = "0- not Operation 1- Add / Update 2- Delete")
    @TableField("action")
    private Integer action;

    @ApiModelProperty(value = "Whether Run", example = "0- No 1- is")
    @TableField("running")
    private Integer running;

    @ApiModelProperty(value = "Camera Name")
    @TableField("state")
    private Integer state;

    @ApiModelProperty(value = "Create Time")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "Update Time")
    @TableField("updated_at")
    private Date updatedAt;

    @ApiModelProperty(value = "Frame Extract Interval (Discard Abandon)")
    @TableField("frequency")
    private Integer frequency;

    @ApiModelProperty(value = "Algorithm Recognition Interval (s)")
    @TableField("interval_time")
    private Float intervalTime;

    @ApiModelProperty(value = "Alert Interval (s)")
    @TableField("alarm_interval")
    private Float alarmInterval;

    @ApiModelProperty(value = "Image Name")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "Image Width")
    @TableField("file_width")
    private Integer fileWidth;

    @ApiModelProperty(value = "Image high Degree")
    @TableField("file_height")
    private Integer fileHeight;

    @ApiModelProperty(value = "Canvas Width")
    @TableField("canvas_width")
    private Integer canvasWidth;

    @ApiModelProperty(value = "Canvas high Degree")
    @TableField("canvas_height")
    private Integer canvasHeight;

    @ApiModelProperty(value = "roi Region")
    @TableField("params")
    private String params;

    @ApiModelProperty(value = "roi Region")
    @TableField("api_params")
    private String apiParams;

    @ApiModelProperty(value = "Zoom Put than Example")
    @TableField("scale_ratio")
    private Float scaleRatio;

    @ApiModelProperty(value = "Warehouse database ID(Discard Abandon)")
    @TableField("warehouse_id")
    private Long wareHouseId;

    @ApiModelProperty(value = "Stream Type", example = "0- Real-time Stream 1- Image")
    @TableField("rtsp_type")
    private Integer rtspType;

    @ApiModelProperty(value = "Relate Region id")
    @TableField("location_id")
    private Long locationId;

    @ApiModelProperty(value = "Relate Region Name")
    @TableField(exist = false)
    private String locationName;

    @ApiModelProperty(value = "Management up Level Region ids", example = "1,2,3")
    @TableField("location_ids")
    private String locationIds;

    @ApiModelProperty(value = "Whether Play Put")
    @TableField("video_play")
    private Integer videoPlay;

    @ApiModelProperty(value = "Discard Abandon")
    @TableField(exist = false)
    private String algorithmNames;

    @ApiModelProperty(value = "Discard Abandon")
    @TableField(exist = false)
    private List<Algorithm> algorithms;

    @ApiModelProperty(value = "Data Source 1 Camera Management 2 Box Management")
    @TableField("location_type")
    private String locationType;

    @ApiModelProperty(value = "Camera Execute Status (1000 Inference in 2000 Download Algorithm in 3000 not make Use, 8000 Wait Wake Wake, 9000 pre Process)")
    @TableField("aibox_exec_status")
    private Integer aiboxExecStatus;

    @ApiModelProperty(value = "Camera Execute Status Push Time")
    @TableField("aibox_exec_time")
    private Date aiboxExecTime;

    @ApiModelProperty(value = "Edge Box change more Plan Number")
    @TableField("action_counter")
    private Integer actionCounter;

    @ApiModelProperty(value = "Video Stream Code Format, H264, H265, empty String")
    @TableField("video_codec")
    private String videoCodec;

    @ApiModelProperty(value = "Video Stream Frame Rate")
    @TableField("video_fps")
    private Integer videoFps;

    @ApiModelProperty(value = "Video Stream Width")
    @TableField("video_width")
    private Integer videoWidth;

    @ApiModelProperty(value = "Video Stream high")
    @TableField("video_height")
    private Integer videoHeight;

    @ApiModelProperty(value = "Ip Speaker Pole id")
    @TableField(value = "sound_column_id")
    private Long soundColumnId;

    @ApiModelProperty(value = "Query Box ids")
    @TableField(exist = false)
    private List<Long> queryLocationIds;

    @ApiModelProperty(value = "Run Status")
    @TableField(exist = false)
    private Integer execStatus;

    @ApiModelProperty(value = "most after Play Put Timestamp, 30s inner no Update, rule Call super Star Box Stop Play Put")
    @TableField("playing_time")
    private Long playingTime;

    @ApiModelProperty(value = "Algorithm Status Result")
    @TableField("aibox_exec_msg")
    private String aiboxExecMsg;

    @ApiModelProperty(value = "Algorithm Call Send Timestamp")
    @TableField("aibox_exec_send")
    private Long aiboxExecSend;

    @ApiModelProperty(value = "GB Standard Channel ID")
    @TableField("gb_id")
    private Long gbId;

    @ApiModelProperty(value = "Source Type,0- Manual Add,1- GB Standard Channel")
    @TableField("source_type")
    private Integer sourceType;

    @ApiModelProperty(value = "Media Node ID")
    @TableField("media_server_id")
    private Long mediaServerId;

    @ApiModelProperty(value = "Relate Camera Count")
    @TableField("algo_count")
    private Integer algoCount;

    @ApiModelProperty("Whether Need from External Get rtsp Stream,0- No,1- is")
    @TableField("is_external_rtsp")
    private Integer isExternalRtsp;
    @ApiModelProperty("Device No")
    @TableField("equipment_code")
    private String equipmentCode;
    @ApiModelProperty("code Stream Type 0- main code Stream,1- child code Stream")
    @TableField("bitstream_type")
    private Integer bitstreamType;
}
