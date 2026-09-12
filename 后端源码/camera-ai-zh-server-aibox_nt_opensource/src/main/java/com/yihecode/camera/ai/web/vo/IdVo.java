package com.yihecode.camera.ai.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Common Primary Key Param")
public class IdVo {

    @Schema(description = "Primary Key", example = "1", required = true)
    private Long id;
}
