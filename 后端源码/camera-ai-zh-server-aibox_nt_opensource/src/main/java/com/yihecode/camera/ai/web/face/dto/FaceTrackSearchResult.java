package com.yihecode.camera.ai.web.face.dto;

import lombok.Builder;
import lombok.Data;

/**
* Face Search in between Result
*
*/
@Data
@Builder
public class FaceTrackSearchResult {

    private boolean success;

    private String msg;
}
