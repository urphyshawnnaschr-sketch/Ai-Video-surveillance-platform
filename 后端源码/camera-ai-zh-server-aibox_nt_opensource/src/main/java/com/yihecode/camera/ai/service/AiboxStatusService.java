package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.AiboxStatus;

import java.util.List;

/**
* Edge Box Resource Monitor Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface AiboxStatusService extends IService<AiboxStatus> {

    /**
* Get most after One Resource Status Record
* @param aiboxId
* @return
*/
    AiboxStatus getLast(Long aiboxId);

    /**
* Query Recent N Data
* @param aiboxId
* @return
*/
    List<AiboxStatus> listLast(Long aiboxId);
}