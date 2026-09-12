package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ReportTarget;
import com.yihecode.camera.ai.mapper.ReportTargetMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* Alarm Month Degree Target Value Config table
*
* @author zhou
* @since 2025.6.26
*/
@Service
public class ReportTargetServiceImpl extends ServiceImpl<ReportTargetMapper, ReportTarget> implements ReportTargetService {

    /**
* Get MAP result structure Data
*
* @return
*/
    @Override
    public Map<Integer, Integer> getDataMap() {
        List<ReportTarget> reportTargetList = this.list();
        return reportTargetList.stream().collect(Collectors.toMap(ReportTarget::getMonth, ReportTarget::getValue, (s1, s2) -> s1));
    }
}
