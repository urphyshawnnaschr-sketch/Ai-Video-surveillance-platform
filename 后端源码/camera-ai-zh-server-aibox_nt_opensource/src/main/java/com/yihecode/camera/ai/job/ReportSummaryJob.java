package com.yihecode.camera.ai.job;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.ReportSummary;
import com.yihecode.camera.ai.entity.ReportSummaryTask;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.ReportService;
import com.yihecode.camera.ai.service.ReportSummaryService;
import com.yihecode.camera.ai.service.ReportSummaryTaskService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
* Alarm Count Day Data
*
* @author zhou
* @since 2025-07-22
*/
@Slf4j
@Component
public class ReportSummaryJob {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ReportSummaryService reportSummaryService;

    @Autowired
    private ReportSummaryTaskService reportSummaryTaskService;

    @Autowired
    private ReportService reportService;

    /**
* Init Data
*/
    @SneakyThrows
    public void runInit() {
        String reportSummaryTag = configService.getByValTag("reportSummaryTag");

        //like Result Data not has Init, rule in Line Init Operation
if(StrUtil.isBlank(reportSummaryTag)) {
// pre Process Process Process Hour long, Add Field
reportService.updateAllHandleTime();

// Process Each day Count summary total
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
// most small Alarm Time
Date minDate = reportService.getMinDate();
if(minDate == null) {
return 0;
}

// most big Date
Date maxDate = DateUtil.offsetDay(new Date(), -1);

// Smooth order Execute Each Day Count Data
DateUtil.rangeToList(minDate, maxDate, DateField.DAY_OF_YEAR).forEach(date -> {
// Count Day Data
handleReportSummary(date, false);
});
return 0;
});

// Storage ID
configService.saveData("History Alarm Day Data Count Init ID","reportSummaryTag","true");

// Wait Execute Complete Finish
future.get();
}
}

/**
* By Date in Line Alarm Day Data Count, can Select Data Exist Hour Whether Replace
* @param date
* @param replace
*/
private boolean handleReportSummary(Date date, boolean replace) {
try {
Date startDate = DateUtil.truncate(date, DateField.DAY_OF_MONTH);
Date endDate = DateUtil.truncate(DateUtil.offsetDay(date, 1), DateField.DAY_OF_MONTH);

// Convert for LocalDate
LocalDate localDate = date.toInstant()
.atZone(ZoneId.systemDefault())
.toLocalDate();

int year = localDate.getYear();
int month = localDate.getMonthValue();
int day = localDate.getDayOfMonth();

// Query Data Whether Exist
ReportSummary reportSummaryDb = reportSummaryService.getData(year, month, day);

// like Result Data Not Exist, rule Create Count Data
if(reportSummaryDb == null) {
//int total = reportService.getSummaryCount(startDate.getTime(), endDate.getTime(), null);
int handled = reportService.getSummaryCount(startDate.getTime(), endDate.getTime(), 1);
int unhandle = reportService.getSummaryCount(startDate.getTime(), endDate.getTime(), 0);
int closed = reportService.getSummaryCount(startDate.getTime(), endDate.getTime(), 2);
int autoHandled = reportService.getSummaryCount(startDate.getTime(), endDate.getTime(), 3);
int handleTimeSecs = reportService.getSummaryHandleTime(startDate.getTime(), endDate.getTime()); // s
int handleTimeMins = 0;
if((handled + closed) > 0) {
handleTimeMins = Double.valueOf(handleTimeSecs * 1d / (handled + closed) / 60).intValue(); // Average Process min
}
int handleRate = 0; // Process Rate
int total = handled + unhandle + closed + autoHandled;
int x = total - autoHandled; // Need Manual Process Data
if(x > 0) {
// handleRate = Double.valueOf((handled + closed) * 100d / x).intValue(); // Process Rate
handleRate = Double.valueOf((total - unhandle) * 100d / total).intValue(); // Process Rate
}

ReportSummary reportSummary = new ReportSummary();
reportSummary.setYear(year);
reportSummary.setMonth(month);
reportSummary.setDay(day);
reportSummary.setReportTotal(total);
reportSummary.setReportClosed(closed);
reportSummary.setReportUnhandle(unhandle);
reportSummary.setReportHandled(handled + autoHandled);
reportSummary.setReportHandleTime(handleTimeMins);
reportSummary.setDate(Integer.valueOf(DateUtil.format(date,"yyyyMMdd")));
reportSummary.setReportHandleRate(handleRate);
reportSummary.setReportAutoHandled(autoHandled);
reportSummary.setUpdateAt(new Date());
reportSummaryService.save(reportSummary);
} else {
if(replace) {// Update Data
int handled = reportService.getSummaryCount(startDate.getTime(), endDate.getTime(), 1);
int unhandle = reportService.getSummaryCount(startDate.getTime(), endDate.getTime(), 0);
int closed = reportService.getSummaryCount(startDate.getTime(), endDate.getTime(), 2);
int autoHandled = reportService.getSummaryCount(startDate.getTime(), endDate.getTime(), 3);
int handleTimeSecs = reportService.getSummaryHandleTime(startDate.getTime(), endDate.getTime()); // s
int handleTimeMins = 0;
if((handled + closed) > 0) {
handleTimeMins = Double.valueOf(handleTimeSecs * 1d / (handled + closed) / 60).intValue(); // Average Process min
}
int handleRate = 0; // Process Rate
int x = reportSummaryDb.getReportTotal() - reportSummaryDb.getReportAutoHandled(); // Need Manual Process Data
int total = handled + unhandle + closed + autoHandled;

if(x > 0) {
// handleRate = Double.valueOf((handled + closed) * 100d / x).intValue(); // Process Rate
handleRate = Double.valueOf((total - unhandle) * 100d / x).intValue(); // Process Rate
}
reportSummaryDb.setReportTotal(total);
reportSummaryDb.setReportClosed(closed);
reportSummaryDb.setReportUnhandle(unhandle);
reportSummaryDb.setReportHandled(handled + autoHandled);
reportSummaryDb.setReportHandleTime(handleTimeMins);
reportSummaryDb.setReportHandleRate(handleRate);
reportSummaryDb.setUpdateAt(new Date());
reportSummaryService.saveOrUpdate(reportSummaryDb);
}
}
return true;
} catch (Exception e) {
log.error("Count Alarm Day Data Exception, date: {}", date, e);
}
return false;
}

/**
* for at front Surface Date Alarm not Process, when day Select Execute, Need Again Calculate Alarm Day Count Data
* - Each day Early Morning 2 Point Execute
*/
public void runSummaryTask() {
List<Integer> handled = new ArrayList<>();

List<ReportSummaryTask> reportSummaryTaskList = reportSummaryTaskService.list();
if(reportSummaryTaskList.isEmpty()) {
return;
}

for(ReportSummaryTask reportSummaryTask: reportSummaryTaskList) {
if(handled.contains(reportSummaryTask.getTaskDate())) {
// Delete
reportSummaryTaskService.removeById(reportSummaryTask.getId());
continue;
}
handled.add(reportSummaryTask.getTaskDate());

LocalDate localDate = LocalDate.of(reportSummaryTask.getTaskYear(), reportSummaryTask.getTaskMonth(), reportSummaryTask.getTaskDay());
Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
boolean handleOk = handleReportSummary(date, true);
if(handleOk) {
// Delete
reportSummaryTaskService.removeById(reportSummaryTask.getId());
}
}
}

/**
* Execute when Day Alarm Data Count
* - Each Separate 10 min Execute One sub
*/
public void runTodaySummary() {
handleReportSummary(new Date(), true);
}
}
