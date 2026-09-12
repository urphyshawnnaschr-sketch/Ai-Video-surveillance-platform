package com.yihecode.camera.ai.web.ap.vo;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class CommitVo {

    @ApiModelProperty(value = "Image ID", dataType = "long", example = "1234567890", required = true)
    private Long imageId;

    @ApiModelProperty(value = "Whether Valid", dataType = "int", example = "0 Invalid 1 Valid")
    private Byte isValid;

    @ApiModelProperty(value = "Annotation Box")
    @TableField(exist = false)
    private List<AnnotationVo> annotations;
}
