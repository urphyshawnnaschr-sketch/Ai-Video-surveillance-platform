package com.yihecode.camera.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* Face Search Record table
*
* @author zhou
* @since 2025.6.30
*/
@Data
@TableName(value = "tbl_biz_face_track_search", autoResultMap = true)
public class FaceTrackSearch {

    private Long id;

    @TableField("filename")
    private String filename;

    @TableField(value = "group_ids", typeHandler = FastjsonTypeHandler.class)
    private List<Long> groupIds;

    @TableField("start_date")
    private Date startDate;

    @TableField("end_date")
    private Date endDate;

    @TableField("created_at")
    private Date createdAt;

    //Search Status, 0- not Search, 1- In Progress Search,2- Search Success,3- Search Failed
@TableField("status")
private Integer status;

@TableField("result_msg")
private String resultMsg;

@TableField("result_json")
private String resultJson;

@TableField("result_user_id")
private Long resultUserId;

@TableField("result_at")
private Date resultAt;

@TableField("result_num")
private Integer resultNum;

@TableField("result_group_id")
private Long resultGroupId;

@TableField("result_image_id")
private Long resultImageId;

@TableField("result_start_date")
private Date resultStartDate;

@TableField("result_end_date")
private Date resultEndDate;

@TableField("result_is_stranger")
private Integer resultIsStranger;

@TableField("result_similarity")
private Float resultSimilarity;

@TableField("result_start_camera_id")
private Long resultStartCameraId;

@TableField("result_end_camera_id")
private Long resultEndCameraId;
}
