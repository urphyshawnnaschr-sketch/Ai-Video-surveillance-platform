package com.yihecode.camera.ai.web.ap.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yihecode.camera.ai.enums.ap.ReviewAction;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVo {

    @ApiModelProperty(value = "Image ID", dataType = "long", example = "1234567890", required = true)
    private Long imageId;

    @ApiModelProperty(value = "Submit ID", dataType = "long", example = "1234567890")
    private Long commitId;

    @ApiModelProperty(value = "Quality Check Result", dataType = "int", example = "1 Pass,3 Rejected,7 Modify")
    private Integer reviewAction;

    @ApiModelProperty(value = "Quality Check Meaning View", dataType = "string")
    private String comment;

    @ApiModelProperty(value = "Whether Valid, Modify Use", dataType = "int", example = "0 Invalid 1 Valid")
    private Byte isValid;

    @ApiModelProperty(value = "Annotation Box, Modify Use")
    @TableField(exist = false)
    private List<AnnotationVo> annotations;
}
