package com.yihecode.camera.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
* Camera Group
*
* @author zhou
* @since 2025.6.16
*/
@Data
@Schema(description = "Camera Group Form Param")
@TableName("tbl_biz_camera_group")
public class CameraGroup {

    @Schema(description = "Primary Key", example = "1", required = true)
    private Long id;

    @Schema(description = "Group Name Name", example = "AAA Region", required = true)
    private String name;

    @Schema(description = "up Level ID", example = "0", required = true)
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "Sort Value", example = "1", required = false)
    private Integer sort;

    @Schema(hidden = true)
    private Integer level;
}
