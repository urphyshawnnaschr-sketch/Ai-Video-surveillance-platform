package com.yihecode.camera.ai.web.api.face.dto;

import lombok.Data;

/**
* Face all Quantity Data
*
* @auhtor 465769438@qq.com
* @since 2026/3/2
*/
@Data
public class FaceFullDataDTO {

    private String rowId; //group_id + user_id + face_id

private Long groupId;

private Long userId;

private Long faceId;
}
