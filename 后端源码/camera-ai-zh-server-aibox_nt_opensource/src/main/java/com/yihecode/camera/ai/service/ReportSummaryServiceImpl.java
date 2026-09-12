package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ReportSummary;
import com.yihecode.camera.ai.mapper.ReportSummaryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* Alarm Count Day Data
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class ReportSummaryServiceImpl extends ServiceImpl<ReportSummaryMapper, ReportSummary> implements ReportSummaryService {

    /**
* By Year Month Day Query Data
*
* @param year
* @param month
* @param day
* @return
*/
    @Override
    public ReportSummary getData(int year, int month, int day) {
        LambdaQueryWrapper<ReportSummary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportSummary::getYear, year);
        queryWrapper.eq(ReportSummary::getMonth, month);
        queryWrapper.eq(ReportSummary::getDay, day);
        return this.getOne(queryWrapper);
    }

    /**
* Query All Data
*
* @return
*/
    @Override
    public List<ReportSummary> listAll() {
        LambdaQueryWrapper<ReportSummary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ReportSummary::getDate);
        return this.list(queryWrapper);
    }

    @Override
    public List<ReportSummary> listData(int startDate) {
        LambdaQueryWrapper<ReportSummary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(ReportSummary::getDate, startDate);
        queryWrapper.orderByAsc(ReportSummary::getDate);
        return this.list(queryWrapper);
    }
}