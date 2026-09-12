package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ReportTarget;

import java.util.Map;

/**
* Alarm Month Degree Target Value Config table
*
* @author zhou
* @since 2025.6.26
*/
public interface ReportTargetService extends IService<ReportTarget> {

    /**
* Get MAP result structure Data
* @return
*/
    Map<Integer, Integer> getDataMap();
}
