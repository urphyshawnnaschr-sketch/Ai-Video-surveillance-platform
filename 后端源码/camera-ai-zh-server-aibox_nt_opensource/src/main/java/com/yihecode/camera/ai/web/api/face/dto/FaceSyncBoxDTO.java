package com.yihecode.camera.ai.web.api.face.dto;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
* Face Sync List Data
*/
@ApiIgnore
@Data
public class FaceSyncBoxDTO {

    private Long id;

    private Long userId;

    private Long groupId;

    private Long imageId;

    private String opType;
}
