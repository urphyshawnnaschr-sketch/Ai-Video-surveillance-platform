package com.yihecode.camera.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
* Face Search and Alarm Relate table
*
* @author zhou
* @since 2025.6.30
*/
@Data
@TableName("tbl_biz_face_track_flow")
public class FaceTrackFlow {

    private Long id;

    @TableField("report_id")
    private Long reportId;

    @TableField("search_id")
    private Long searchId;

    @TableField("camera_id")
    private Long cameraId;

    @TableField("report_at")
    private Date reportAt;

    @TableField("similarity")
    private Float similarity;

    @TableField("user_id")
    private Long userId;

    @TableField("group_id")
    private Long groupId;

}
