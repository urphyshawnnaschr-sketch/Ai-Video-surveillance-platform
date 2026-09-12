package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.SocialConfig;
import com.yihecode.camera.ai.mapper.SocialConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* Camera Relate Social Platform Push, Feishu, WeWork, DingTalk
*/
@Slf4j
@Service
public class SocialConfigServiceImpl extends ServiceImpl<SocialConfigMapper, SocialConfig> implements SocialConfigService {

    /**
* By Camera ID Delete
*
* @param cameraId
*/
    @Override
    public void deleteByCamera(Long cameraId, Long algorithmId) {
        LambdaQueryWrapper<SocialConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialConfig::getCameraId, cameraId);
        queryWrapper.eq(SocialConfig::getAlgorithmId, algorithmId);
        this.remove(queryWrapper);
    }

    /**
* By Camera ID and Algorithm ID Query Data
*
* @param cameraId
* @param algorithmId
* @return
*/
    @Override
    public List<SocialConfig> listData(Long cameraId, Long algorithmId) {
        LambdaQueryWrapper<SocialConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialConfig::getCameraId, cameraId);
        queryWrapper.eq(SocialConfig::getAlgorithmId, algorithmId);
        List<SocialConfig> socialConfigList = this.list(queryWrapper);
        return socialConfigList == null ? new ArrayList<>() : socialConfigList;
    }

    /**
* By socialId Delete
*
* @param socialId
*/
    @Override
    public void deleteBySocial(Long socialId) {
        LambdaQueryWrapper<SocialConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialConfig::getSocialId, socialId);
        this.remove(queryWrapper);
    }
}
