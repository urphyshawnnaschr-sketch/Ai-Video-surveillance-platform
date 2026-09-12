package com.yihecode.camera.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
* Person member Tracking Base image Config
*
* @author zhou
* @since 2025.6.20
*/
@Data
@TableName("tbl_biz_face_track_config")
public class FaceTrackConfig {

    /**
* Primary Key
*/
    private Long id;

    /**
* Config Name
*/
    private String name;

    /**
* Base image File Name
*/
    private String filename;

    /**
* Valid Status,0- Invalid,1- Valid
*/
    private Integer state;
}
