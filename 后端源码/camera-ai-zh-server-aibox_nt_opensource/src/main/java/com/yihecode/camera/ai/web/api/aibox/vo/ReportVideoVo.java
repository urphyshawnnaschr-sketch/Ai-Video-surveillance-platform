package com.yihecode.camera.ai.web.api.aibox.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiModel(value = "Edge Box Upload Video")
@Data
@ToString
public class ReportVideoVo {

    @ApiModelProperty(value = "Verify key", dataType = "string")
    private String key;

    @ApiModelProperty(value = "Alarm ID List", dataType = "long")
    private List<Long> reportIds;

    @ApiModelProperty(value = "Video File", dataType = "file")
    private MultipartFile file;
}
