package com.yihecode.camera.ai.https.face;

import lombok.Data;
import java.util.List;

@Data
public class HttpFaceRecognize {

//private Double probability;

private String liveness;

private Double similiarity;

private List<Integer> bbox;

private Long faceGroupId;

private Long faceUserId;

private Long faceImageId;

}
