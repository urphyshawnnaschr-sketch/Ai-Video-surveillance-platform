package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
* Camera Group Detail
*
* @author zhou
* @since 2025.6.16
*/
@Data
@TableName("tbl_biz_camera_group_item")
public class CameraGroupItem {

    private Long id;

    private Long cameraId;

    private Long groupId;
}
