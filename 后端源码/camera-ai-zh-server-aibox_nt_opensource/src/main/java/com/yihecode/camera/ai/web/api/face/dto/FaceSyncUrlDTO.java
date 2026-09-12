package com.yihecode.camera.ai.web.api.face.dto;

import lombok.Data;

/**
* Face Service IP and Index
*/
@Data
public class FaceSyncUrlDTO {

    //Face Service IP
private String faceHttpIp;

// Face Service SN
private String faceSn;

// Whether change more
private boolean changed;
}
