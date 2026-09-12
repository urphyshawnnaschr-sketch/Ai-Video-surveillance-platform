package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.SocialHook;

import java.util.List;

/**
* Social Platform Push, Feishu, WeWork, DingTalk
*/
public interface SocialHookService extends IService<SocialHook> {

    /**
* By Type Query
* @param type
* @return
*/
    List<SocialHook> listData(Integer type);

    /**
* Query All
* @return
*/
    List<SocialHook> listAll();
}
