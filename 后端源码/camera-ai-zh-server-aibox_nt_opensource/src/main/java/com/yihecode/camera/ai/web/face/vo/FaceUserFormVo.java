package com.yihecode.camera.ai.web.face.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* Person member Add transmit Param
*/
@ApiModel(value = "Person member Add transmit Param")
@Data
public class FaceUserFormVo {

    @ApiModelProperty(value = "id", example = "1", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "Name", example = "1", dataType = "string")
    private String name;

    @ApiModelProperty(value = "Contact Mode", example = "18888888888", dataType = "string")
    private String tel;

    @ApiModelProperty(value = "remark", example = "Remark", dataType = "string")
    private String remark;

    @ApiModelProperty(value = "Group ID", example = "1", dataType = "long")
    private Long groupId;

    @ApiModelProperty(value = "Upload File List", dataType = "list")
    private List<MultipartFile> files;

    @ApiModelProperty(value = "original Image list", example = "[\"a1.jpg\", \"a2.jpg\"]", dataType = "list")
    private List<String> filesStr;

    @ApiModelProperty(value = "Face Alarm ID", example = "1", dataType = "long")
    private Long reportId;
}
