package com.yihecode.camera.ai.web.api.aibox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Alarm Third Party Push Result
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmPushResultDTO {

    //Whether Success
private boolean success;

// Error Description
private String msg;
}
