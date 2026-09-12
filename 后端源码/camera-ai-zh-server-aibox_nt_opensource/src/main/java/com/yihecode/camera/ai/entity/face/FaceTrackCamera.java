package com.yihecode.camera.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.util.List;

/**
* Person member Tracking Camera Config
*
* @author zhou
* @since 2025.6.20
*/
@Data
@TableName(value = "tbl_biz_face_track_camera", autoResultMap = true)
public class FaceTrackCamera {

    /**
* Primary Key
*/
    private Long id;

    /**
* Config ID
*/
    private Long configId;

    /**
* Camera ID
*/
    private Long cameraId;

    /**
* Insert Point Bit set
*/
    @TableField(value = "position", typeHandler= FastjsonTypeHandler.class)
    private List<Integer> position;
}
