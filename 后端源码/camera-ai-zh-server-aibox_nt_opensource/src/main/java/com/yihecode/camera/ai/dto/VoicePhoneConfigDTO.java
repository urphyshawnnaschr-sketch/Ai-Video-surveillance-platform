package com.yihecode.camera.ai.dto;

import lombok.Data;

/**
* Voice Alert Config DTO
*/
@Data
public class VoicePhoneConfigDTO {

    private String voiceEnable;
    private String voiceAppId;
    private String voiceAppSecret;
    private String voiceTemplateId;

}
