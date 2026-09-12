package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.SocialHook;
import com.yihecode.camera.ai.mapper.SocialHookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* Social Platform Push, Feishu, WeWork, DingTalk
*/
@Slf4j
@Service
public class SocialHookServiceImpl extends ServiceImpl<SocialHookMapper, SocialHook> implements SocialHookService {

    /**
* By Type Query
*
* @param type
* @return
*/
    @Override
    public List<SocialHook> listData(Integer type) {
        LambdaQueryWrapper<SocialHook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialHook::getType, type);
        List<SocialHook> socialHookList = this.list(queryWrapper);
        return socialHookList == null ? new ArrayList<>() : socialHookList;
    }

    /**
* Query All
*
* @return
*/
    @Override
    public List<SocialHook> listAll() {
        LambdaQueryWrapper<SocialHook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SocialHook::getState, 1);
        List<SocialHook> socialHookList = this.list();
        return socialHookList == null ? new ArrayList<>() : socialHookList;
    }
}
