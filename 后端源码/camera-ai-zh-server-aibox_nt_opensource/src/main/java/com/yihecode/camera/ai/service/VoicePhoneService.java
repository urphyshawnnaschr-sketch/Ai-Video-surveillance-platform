package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.VoicePhone;

import java.util.List;

/**
* SMS Push Phone code
*
*/
public interface VoicePhoneService extends IService<VoicePhone> {

    /**
* By Account Query Voice Phone
* @param accountId
* @return
*/
    List<VoicePhone> listPhones(Long accountId);
}