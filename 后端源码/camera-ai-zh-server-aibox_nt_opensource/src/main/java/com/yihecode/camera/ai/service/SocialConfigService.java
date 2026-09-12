package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.SocialConfig;

import java.util.List;

/**
* Camera Relate Social Platform Push, Feishu, WeWork, DingTalk
*/
public interface SocialConfigService extends IService<SocialConfig> {

    /**
* By Camera ID Delete
* @param cameraId
* @param algorithmId
*/
    void deleteByCamera(Long cameraId, Long algorithmId);

    /**
* By Camera ID and Algorithm ID Query Data
* @param cameraId
* @param algorithmId
* @return
*/
    List<SocialConfig> listData(Long cameraId, Long algorithmId);

    /**
* By socialId Delete
* @param socialId
*/
    void deleteBySocial(Long socialId);
}
