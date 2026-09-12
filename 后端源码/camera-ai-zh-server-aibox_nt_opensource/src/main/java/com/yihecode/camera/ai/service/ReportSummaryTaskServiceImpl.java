package com.yihecode.camera.ai.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ReportSummaryTask;
import com.yihecode.camera.ai.mapper.ReportSummaryTaskMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
* Alarm Count Day Data Task
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class ReportSummaryTaskServiceImpl extends ServiceImpl<ReportSummaryTaskMapper, ReportSummaryTask> implements ReportSummaryTaskService {

    /**
* Create Record
*/
    @Override
    public void addData(Date reportDate) {
        //like Result Alarm Time and Process Time for when day Time, rule not Need Insert in Update Record
long t = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime();
if(reportDate.getTime() > t) {
return;
}

//
String date = DateUtil.format(reportDate,"yyyyMMdd");

// Query Update Record Whether Exist, like Result Exist rule Back
LambdaQueryWrapper<ReportSummaryTask> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(ReportSummaryTask::getTaskDate, date);

int count = this.count(queryWrapper);
if(count > 0) {
return;
}

// Create Update Record
LocalDate localDate = reportDate.toInstant()
.atZone(ZoneId.systemDefault())
.toLocalDate();

ReportSummaryTask reportSummaryTask = new ReportSummaryTask();
reportSummaryTask.setTaskDate(Integer.parseInt(date));
reportSummaryTask.setTaskYear(localDate.getYear());
reportSummaryTask.setTaskMonth(localDate.getMonthValue());
reportSummaryTask.setTaskDay(localDate.getDayOfMonth());
this.save(reportSummaryTask);
}
}