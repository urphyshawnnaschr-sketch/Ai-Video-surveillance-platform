package com.yihecode.camera.ai.web.ap.vo;

import com.yihecode.camera.ai.entity.ap.Commit;
import com.yihecode.camera.ai.enums.ap.ReviewAction;
import io.swagger.annotations.ApiModel;
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
@ApiModel("Explain Put Request")
public class ReleaseRequestVo {

    @ApiModelProperty(value = "Project id", dataType = "long", example = "1234567890", required = true)
    private Long projectId;

    @ApiModelProperty(value = "Image ID", required = true)
    private List<Long> imageIds;

}
