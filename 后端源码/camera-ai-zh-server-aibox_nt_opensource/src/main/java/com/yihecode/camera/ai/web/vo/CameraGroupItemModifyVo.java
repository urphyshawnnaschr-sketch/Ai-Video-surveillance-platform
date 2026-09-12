package com.yihecode.camera.ai.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
* Camera Group Detail Add / Modify
*/
@Data
@Schema(description = "Add / Edit Param")
public class CameraGroupItemModifyVo {

    @Schema(description = "Primary Key", example = "1")
    private Long id;

    @Schema(description = "Group ID", example = "1", required = true)
    private Long groupId;

    @Schema(description = "Camera Ids", example = "[1, 2, 3]", required = true)
    private List<Long> cameraIds;
}
