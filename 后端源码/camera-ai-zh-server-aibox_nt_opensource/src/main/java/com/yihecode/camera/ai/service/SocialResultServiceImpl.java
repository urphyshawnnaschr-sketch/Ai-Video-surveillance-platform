package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.SocialResult;
import com.yihecode.camera.ai.mapper.SocialResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* Social Platform Push, Feishu, WeWork, DingTalk
*/
@Slf4j
@Service
public class SocialResultServiceImpl extends ServiceImpl<SocialResultMapper, SocialResult> implements SocialResultService {

}
