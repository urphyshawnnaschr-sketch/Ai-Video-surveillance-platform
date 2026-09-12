package com.yihecode.camera.ai.web.dto;

import com.yihecode.camera.ai.entity.CameraGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
* Camera Group Tree result structure
*
* @author zhou
* @since 2025.6.16
*/
@Data
@Schema(description = "Group Tree Data")
public class CameraGroupTreeDTO {

    @Schema(description = "Primary Key", example = "1")
    private Long id;

    @Schema(description = "Group Name Name", example = "AAA Region")
    private String name;

    @Schema(description = "up Level Node", example = "0")
    private Long parentId;

    @Schema(description = "child Node")
    private List<CameraGroupTreeDTO> children;

    @Schema(description = "Hierarchy")
    private Integer level;


    public CameraGroupTreeDTO(CameraGroup cameraGroup) {
        this.id = cameraGroup.getId();
        this.name = cameraGroup.getName();
        this.parentId = cameraGroup.getParentId();
    }

    public void addChild(CameraGroupTreeDTO treeDTO) {
        if(this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(treeDTO);
    }
}
