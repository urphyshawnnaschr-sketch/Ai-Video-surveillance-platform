package com.yihecode.camera.ai.web.dto;

import com.yihecode.camera.ai.entity.Camera;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
* Camera Simple form Info, Use at Camera Group Hour Through Shuttle Box make Use
*/
@Data
@Schema(description = "Group Camera Detail Data")
public class CameraGroupCameraDTO {

    @Schema(description = "Camera Primary Key", example = "1")
    private Long id;

    @Schema(description = "Camera Name", example = "Guard Room")
    private String name;

    @Schema(description = "Camera Cover image", example = "aaa.jpg")
    private String filename;

    @Schema(description = "Group Name Name", example = "AAA Region")
    private String groupName;

    @Schema(description = "The belong Device", example = "front Door")
    private String boxName;

    @Schema(description = "Relate Algorithm Name List", example = "[Line Person Recognition, Fire Smoke Recognition]")
    private List<String> algorithmNames;

    @Schema(description = "Run Status Note", example = "Recognition in")
    private String execMsg;

    @Schema(description = "Run Status, 0- not Run,1- Run in,2- Run Exception", example = "0")
    private Integer status;

    @Schema(description = "Video Width", example = "1280")
    private Integer videoWidth;

    @Schema(description = "Video high Degree", example = "720")
    private Integer videoHeight;

    @Schema(description = "Video Code", example = "H264")
    private String videoCodec;

    public CameraGroupCameraDTO(Camera camera) {
        this.id = camera.getId();
        this.name = camera.getName();
    }
}
