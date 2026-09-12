package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* Person member Add transmit Param
*/
@ApiModel(value = "Stranger produce Person member Add to has Person member transmit Param")
@Data
public class FaceUserAddToExistVo {

    @ApiModelProperty(value = "User ID", example = "1", dataType = "long")
    private Long userId;

    @ApiModelProperty(value = "Face Alarm ID", example = "1", dataType = "long")
    private Long reportId;
}
