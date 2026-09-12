package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.VoicePhone;
import com.yihecode.camera.ai.mapper.VoicePhoneMapper;

import java.util.ArrayList;
import java.util.List;

/**
* Voice Push Phone code
*/
@Service
public class VoicePhoneServiceImpl extends ServiceImpl<VoicePhoneMapper, VoicePhone> implements VoicePhoneService {

    /**
* By Account Query Voice Phone
*
* @param accountId
* @return
*/
    @Override
    public List<VoicePhone> listPhones(Long accountId) {
        LambdaQueryWrapper<VoicePhone> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoicePhone::getAccountId, accountId);
        List<VoicePhone> voicePhoneList = this.list(queryWrapper);
        return voicePhoneList == null ? new ArrayList<>() : voicePhoneList;
    }
}