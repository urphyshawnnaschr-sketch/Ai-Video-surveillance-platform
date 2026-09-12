package com.yihecode.camera.ai.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.enums.ReportType;
import com.yihecode.camera.ai.mapper.ReportMapper;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.utils.TimeUtils;
import com.yihecode.camera.ai.web.map.dto.ReportDTO;
import com.yihecode.camera.ai.web.vo.ReportCollectExportVo;
import com.yihecode.camera.ai.web.vo.ReportNearlyVo;
import com.yihecode.camera.ai.web.vo.ReportSummaryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
* Alarm Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    //
@Autowired
private AlgorithmService algorithmService;
@Autowired
private ReportMapper reportMapper;
@Autowired
private CameraService cameraService;
@Autowired
private ConfigService configService;
@Autowired
private AccountService accountService;
@Autowired
@Lazy
private LocationService locationService;
@Autowired
private RecordService recordService;

/**
* Save Report Object to Database
*
* @param report need Save Report Object
* @return Save success Back true, No rule Back false
*/
public boolean saveReport(Report report) {
return this.save(report);
}

/**
*
* @param pageObj
* @param report
* @return
*/
@Override
public IPage<Report> listPage(IPage<Report> pageObj, Report report) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getDisplay, 0);
if(report.getCameraId()!= null) {
queryWrapper.eq(Report::getCameraId, report.getCameraId());
}
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null &&!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
}
if(report.getAlgorithmId()!= null) {
queryWrapper.eq(Report::getAlgorithmId, report.getAlgorithmId());
}
if(report.getType()!= null) {
queryWrapper.eq(Report::getType, report.getType());
}
if(report.getAuditResult()!= null) {
queryWrapper.eq(Report::getAuditResult, report.getAuditResult());
}
if(report.getAuditState()!= null) {
queryWrapper.eq(Report::getAuditState, report.getAuditState());
}
queryWrapper.orderByDesc(Report::getCreatedMills);
return this.page(pageObj, queryWrapper);
}
@Override
public List<Report> listPageGroupByCamera(IPage<Report> pageObj, Report report) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Report::getCameraId);
queryWrapper.eq(Report::getDisplay, 0);
queryWrapper.groupBy(Report::getCameraId);
return this.list(queryWrapper);
}


/**
*
* @param startDate
* @param endDate
* @return
*/
@Override
public List<Map<String, Object>> findAlgorithmRatio(Date startDate, Date endDate) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.gt(Report::getCreatedAt, startDate);
queryWrapper.lt(Report::getCreatedAt, endDate);
queryWrapper.eq(Report::getDisplay, 0);
queryWrapper.eq(Report::getType, ReportType.AI.getType());
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
queryWrapper.last("group by algorithm_id");
return this.getBaseMapper().selectAlgorithmRatio(queryWrapper);
}

/**
*
* @param startDate
* @param endDate
* @return
*/
@Override
public List<Map<String, Object>> findCamera(Date startDate, Date endDate) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.gt(Report::getCreatedAt, startDate);
queryWrapper.lt(Report::getCreatedAt, endDate);
queryWrapper.eq(Report::getDisplay, 0);
queryWrapper.eq(Report::getType, ReportType.AI.getType());
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
queryWrapper.last("group by camera_id");
return this.getBaseMapper().selectCamera(queryWrapper);
}

/**
*
* @param startDate
* @param endDate
* @return
*/
@Override
public List<Map<String, Object>> findCameraAlgorithm(Date startDate, Date endDate) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.gt(Report::getCreatedAt, startDate);
queryWrapper.lt(Report::getCreatedAt, endDate);
queryWrapper.eq(Report::getDisplay, 0);
queryWrapper.eq(Report::getType, ReportType.AI.getType());
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
queryWrapper.last("group by camera_id, algorithm_id");
return this.getBaseMapper().selectCameraAlgorithm(queryWrapper);
}

/**
*
* @param id
* @param display
*/
@Override
public void updateDisplay(Long id, Integer display) {
LambdaUpdateWrapper<Report> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(Report::getDisplay, display)
.eq(Report::getId, id);
this.getBaseMapper().update(null, updateWrapper);
}

/**
* Review
*
* @param id
* @param result
*/
@Override
public void updateAudit(Long id, Integer result) {
LambdaUpdateWrapper<Report> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(Report::getAuditState, 1)
.set(Report::getAuditResult, result)
.eq(Report::getId, id);
this.getBaseMapper().update(null, updateWrapper);
}

/**
* By Algorithm id, Start and End ms Value Query Total
*
* @param algorithmId
* @param startMills
* @param endMills
* @return
*/
@Override
public Integer getAlgorithmCounter(Long algorithmId, long startMills, long endMills) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getAlgorithmId, algorithmId);
queryWrapper.gt(Report::getCreatedMills, startMills);
queryWrapper.lt(Report::getCreatedMills, endMills);
queryWrapper.eq(Report::getDisplay, 0);
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
return this.count(queryWrapper);
}

/**
* Start and End ms Value Query Total
*
* @param startMills
* @param endMills
* @return
*/
@Override
public int getCounter(long startMills, long endMills, List<Long> departIds) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
if(ObjectUtil.isNotEmpty(departIds)) {
queryWrapper.in(Report::getDepartId, departIds);
}
queryWrapper.ge(Report::getCreatedMills, startMills);
queryWrapper.lt(Report::getCreatedMills, endMills);
queryWrapper.eq(Report::getDisplay, 0);
return this.count(queryWrapper);
}

/**
* Page Query
*
* @param objectPage
* @param cameraId
* @param algorithmId
* @param startMills
* @param endMills
* @return
*/
@Override
public IPage<Report> listByPage(IPage<Report> objectPage, Long cameraId, Long algorithmId, Integer type, Long startMills, Long endMills, Long alarmLevelId, List<Long> queryDepartIds, Integer display, List<Integer> auditResults, List<Integer> markList) {
// like Result Alert Level id not Is Empty, Need find out All Algorithm id
List<Long> algorithmIds = new ArrayList<>();
if(alarmLevelId!= null) {
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList!= null) {
for(Algorithm algorithm: algorithmList) {
if(null!= algorithm.getAlarmLevelId() && algorithm.getAlarmLevelId().equals(alarmLevelId)) {
algorithmIds.add(algorithm.getId());
}
}
}
// like Result not has Relate Algorithm, Direct connect supplement Charge 0 Make for Condition
if(algorithmIds.isEmpty()) {
algorithmIds.add(0L);
}
}

//
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
// queryWrapper.eq(Report::getDisplay, 0);
if(cameraId!= null) {
queryWrapper.eq(Report::getCameraId, cameraId);
}
List<Long> cameraIdList = cameraIdListByLogin();
log.info("Alert Data Query, Department Filter after cameraIdList:{}", cameraIdList);
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
if(algorithmId!= null) {
queryWrapper.eq(Report::getAlgorithmId, algorithmId);
}
if(!algorithmIds.isEmpty()) {
queryWrapper.in(Report::getAlgorithmId, algorithmIds);
}
if(startMills!= null) {
queryWrapper.ge(Report::getCreatedMills, startMills);
}
if(endMills!= null) {
queryWrapper.lt(Report::getCreatedMills, endMills);
}
if(type!= null) {
queryWrapper.eq(Report::getType, type);
}
// belong belong Department
if(queryDepartIds!= null &&!queryDepartIds.isEmpty()) {
queryWrapper.in(Report::getDepartId, queryDepartIds);
}
if(CollUtil.isNotEmpty(markList)){
queryWrapper.in(Report::getMark, markList);
}
// Display Type
log.info("-------------------------------display:{}", display);
if(display!= null) {
queryWrapper.eq(Report::getDisplay, display);
}
// Process Status
if(auditResults!= null &&!auditResults.isEmpty()) {
queryWrapper.in(Report::getAuditResult, auditResults);
}
queryWrapper.orderByDesc(Report::getCreatedMills);
return this.page(objectPage, queryWrapper);
}

@Override
public IPage<Report> listByPageApp(IPage<Report> objectPage, String cameraName, Long algorithmId, Integer type, Long startMills, Long endMills, Long alarmLevelId, Integer auditState) {
// like Result Alert Level id not Is Empty, Need find out All Algorithm id
List<Long> algorithmIds = new ArrayList<>();
if(alarmLevelId!= null) {
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList!= null) {
for(Algorithm algorithm: algorithmList) {
if(algorithm.getAlarmLevelId().equals(alarmLevelId)) {
algorithmIds.add(algorithm.getId());
}
}
}
// like Result not has Relate Algorithm, Direct connect supplement Charge 0 Make for Condition
if(algorithmIds.isEmpty()) {
algorithmIds.add(0L);
}
}

//
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getDisplay, 0);

if(StringUtils.isNotBlank(cameraName)) {
LambdaQueryWrapper<Camera> cameraQuery = new LambdaQueryWrapper<>();
cameraQuery.like(Camera::getName, cameraName);
List<Camera> cameraList = cameraService.list(cameraQuery);
List<Long> cameraIds = new ArrayList<>();
for (Camera camera: cameraList) {
cameraIds.add(camera.getId());
}
queryWrapper.in(Report::getCameraId, cameraIds);
}
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
if(algorithmId!= null) {
queryWrapper.eq(Report::getAlgorithmId, algorithmId);
}
if(!algorithmIds.isEmpty()) {
queryWrapper.in(Report::getAlgorithmId, algorithmIds);
}
if(startMills!= null) {
queryWrapper.ge(Report::getCreatedMills, startMills);
}
if(endMills!= null) {
queryWrapper.lt(Report::getCreatedMills, endMills);
}
if(type!= null) {
queryWrapper.eq(Report::getType, type);
}
if(auditState!= null) {
queryWrapper.eq(Report::getAuditState, auditState);
}
queryWrapper.orderByDesc(Report::getCreatedMills);
return this.page(objectPage, queryWrapper);
}

/**
* Query Recent 3 day Record
*
* @param nums
* @return
*/
@Override
public List<Report> listNewly(int nums) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getDisplay, 0);
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
queryWrapper.orderByDesc(Report::getCreatedMills);
queryWrapper.last("limit 0,"+ nums +"");
return this.list(queryWrapper);
}

/**
* Count Total
*
* @param startMills
* @param endMills
* @param cameraId
* @param algorithmId
* @return
*/
@Override
public Integer getCount(Long startMills, Long endMills, Long cameraId, Long algorithmId, Integer type) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
//
if(startMills!= null) {
queryWrapper.gt(Report::getCreatedMills, startMills);
}
//
if(endMills!= null) {
queryWrapper.lt(Report::getCreatedMills, endMills);
}
//
if(cameraId!= null) {
queryWrapper.eq(Report::getCameraId, cameraId);
}
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
//
if(algorithmId!= null) {
queryWrapper.eq(Report::getAlgorithmId, algorithmId);
}
//
if(type!= null) {
queryWrapper.eq(Report::getType, type);
}
queryWrapper.eq(Report::getDisplay, 0);
return this.count(queryWrapper);
}

/**
* Count Total
*
* @param startMills
* @param endMills
* @param cameraId
* @param algorithmId
* @return
*/
@Override
public Integer getMarkCount(Long startMills, Long endMills, Long cameraId, Long algorithmId) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
//
if(startMills!= null) {
queryWrapper.gt(Report::getCreatedMills, startMills);
}
//
if(endMills!= null) {
queryWrapper.lt(Report::getCreatedMills, endMills);
}
//
if(cameraId!= null) {
queryWrapper.eq(Report::getCameraId, cameraId);
}
//
if(algorithmId!= null) {
queryWrapper.eq(Report::getAlgorithmId, algorithmId);
}
queryWrapper.eq(Report::getDisplay, 0);
queryWrapper.eq(Report::getAuditState, 1);
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
return this.count(queryWrapper);
}

/**
* By Algorithm Count Count
*
* @param startMills
* @param endMills
* @return
*/
@Override
public Map<Long, Integer> getCountByAlgorithm(Long startMills, Long endMills) {
Map<String, Object> params = new HashMap<>();
//
if(startMills!= null) {
params.put("startMills", startMills);
}
//
if(endMills!= null) {
params.put("endMills", endMills);
}
//
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
params.put("cameraIdList", cameraIdList);
}

//
List<Map<String, Object>> datas = this.getBaseMapper().selectAlgorithmStatics(params);
if(datas == null) {
return new HashMap<>();
}
//
Map<Long, Integer> resMap = new HashMap<>();
//
for(Map<String, Object> data: datas) {
Long algorithmId = Convert.toLong(data.get("algorithm_id"), 0l);
Integer count = Convert.toInt(data.get("ct"), 0);
//
resMap.put(algorithmId, count);
}
return resMap;
}

/**
* By Algorithm Count Count (List Back)
* @author Abyss
* @date 2023/11/6 16:32
* @param startMills
* @param endMills
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
@Override
public List<Map<String, Object>> getCountByAlgorithmReList(Long startMills, Long endMills) {
Map<String, Object> params = new HashMap<>();
if(startMills!= null) {
params.put("startMills", startMills);
}
if(endMills!= null) {
params.put("endMills", endMills);
}
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
params.put("cameraIdList", cameraIdList);
}
List<Map<String, Object>> datas = this.getBaseMapper().selectAlgorithmStatics(params);
return datas;
}

@Override
public Map<String, Object> selectTodayCount() {
Map<String, Object> map = new HashMap<>();
map.put("reportCount", reportMapper.countToday());
map.put("auditCount", reportMapper.countTodayByAuditAt());
map.put("auditTime", reportMapper.selectAverageProcessingTimeToday());
map.put("mostReportType", reportMapper.selectNameMostToday());
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
map.put("cameraIdList", cameraIdList);
}
return map;
}

@Override
public Map<String, Object> select7DayCount() {
Map<String, Object> map = new HashMap<>();
map.put("reportCount", reportMapper.count7Day());
map.put("auditCount", reportMapper.count7DayByAuditAt());
map.put("auditTime", reportMapper.selectAverageProcessingTime7Day());
map.put("mostReportType", reportMapper.selectNameMost7Day());
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
map.put("cameraIdList", cameraIdList);
}
return map;
}

@Override
public Map<String, Object> selectMonthCount() {
Map<String, Object> map = new HashMap<>();
map.put("reportCount", reportMapper.countMonth());
map.put("auditCount", reportMapper.countMonthByAuditAt());
map.put("auditTime", reportMapper.selectAverageProcessingTimeMonth());
map.put("mostReportType", reportMapper.selectNameMostMonth());
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
map.put("cameraIdList", cameraIdList);
}
return map;
}

@Override
public List<Map<String, Object>> selectReportTypeRankingToday() {
return reportMapper.selectReportTypeRankingToday(cameraIdListByLogin());
}

@Override
public List<Map<String, Object>> selectReportTypeRanking7Day() {
return reportMapper.selectReportTypeRanking7Day(cameraIdListByLogin());
}

@Override
public List<Map<String, Object>> selectReportTypeRankingMonth() {
return reportMapper.selectReportTypeRankingMonth(cameraIdListByLogin());
}

@Override
public Map<String, Object> selectReportTypeCountToday() {
List<Map<String, Object>> result = new ArrayList<>();
List<Map<String, Object>> sqlMap = reportMapper.selectReportTypeCountToday(cameraIdListByLogin());
List<String> nameList = new ArrayList<>();
for (Map<String, Object> map: sqlMap) {
if (null == map.get("name")) continue;
String name = map.get("name").toString();
if (!nameList.contains(name)) {
nameList.add(name);
}
}
List<String> xData = new ArrayList<>();
for (int time = 0; time < 24; time++) {
xData.add(String.format("%d:00", time));
}
for (String name: nameList) {
Map<String, Object> map = new HashMap<>();
map.put("name", name);
List<Integer> data = new ArrayList<>();
for (int time = 0; time < 24; time++) {
Integer dd = 0;
for (Map<String, Object> mm: sqlMap) {
if (Integer.valueOf(mm.get("time_hour").toString()).equals(time) && mm.get("name").equals(name)) {
dd = Integer.valueOf(mm.get("count").toString());
sqlMap.remove(mm);
break;
}
}
data.add(dd);
}
map.put("data", data);
result.add(map);
}
Map<String, Object> resultMap = new HashMap<>();
resultMap.put("data", result);
resultMap.put("xdata", xData);
return resultMap;
}

@Override
public Map<String, Object> selectReportTypeCount7Day() {
List<Map<String, Object>> result = new ArrayList<>();
Date todayStart = TimeUtils.getZero();
Date startTime = TimeUtils.addDay(todayStart, -6);
Date todayEnd = TimeUtils.addDay(todayStart, 1);
List<Long> typeList = reportMapper.selectReportType(startTime, todayEnd, cameraIdListByLogin());
List<String> xData = new ArrayList<>();
Date start = startTime;
Date end = TimeUtils.addDay(start, 1);
while (end.before(todayEnd) || end.equals(todayEnd)) {
xData.add(DateUtil.format(start,"MM Month dd"));
start = end;
end = TimeUtils.addDay(end, 1);
}
for (Long id: typeList) {
Date startT = startTime;
Date endT = TimeUtils.addDay(startT, 1);
Map<String, Object> map = new HashMap<>();
Algorithm algorithm = algorithmService.getById(id);
map.put("name", algorithm.getName());
List<Integer> data = new ArrayList<>();
while (endT.before(todayEnd) || endT.equals(todayEnd)) {
Integer count = reportMapper.countReport(id, startT, endT, cameraIdListByLogin());
data.add(count);
startT = endT;
endT = TimeUtils.addDay(endT, 1);
}
map.put("data", data);
result.add(map);
}
Map<String, Object> resultMap = new HashMap<>();
resultMap.put("data", result);
resultMap.put("xdata", xData);
return resultMap;
}

// @Override
// public List<Map<String, Object>> selectReportTypeCountMonth() {
// List<Map<String, Object>> result = new ArrayList<>();
// Date todayStart = TimeUtils.getZero();
// Date startTime = TimeUtils.getZero(TimeUtils.getWeekStart(TimeUtils.getMonthStart()));
// Date endTime = TimeUtils.addDay(TimeUtils.getZero(TimeUtils.getWeekEnd(TimeUtils.getMonthEnd())), 1);
// List<Long> typeList = reportMapper.selectReportType(todayStart, endTime);
// for (Long id: typeList) {
// Date startT = startTime;
// Date endT = TimeUtils.addDay(startTime, 7);
// Map<String, Object> map = new HashMap<>();
// Algorithm algorithm = algorithmService.getById(id);
// map.put("name", algorithm.getName());
// List<Integer> data = new ArrayList<>();
// while (endT.before(endTime) || endT.equals(endTime)) {
// Integer count = reportMapper.countReport(id, startT, endT);
// data.add(count);
// startT = endT;
// endT = TimeUtils.addDay(endT, 7);
//}
// map.put("data", data);
// result.add(map);
//}
// return result;
//}
@Override
public Map<String, Object> selectReportTypeCountMonth() {
List<Map<String, Object>> result = new ArrayList<>();
List<Map<String, Object>> sqlMap = reportMapper.selectReportTypeCountMonth(cameraIdListByLogin());
List<String> nameList = new ArrayList<>();
for (Map<String, Object> map: sqlMap) {
if (null == map.get("name")) continue;
String name = map.get("name").toString();
if (!nameList.contains(name)) {
nameList.add(name);
}
}
List<String> xData = new ArrayList<>();
for (int day = 1; day < TimeUtils.getMaxDays(); day++) {
xData.add(String.format("%s Month %d", DateUtil.format(new Date(),"MM"), day));
}
for (String name: nameList) {
Map<String, Object> map = new HashMap<>();
map.put("name", name);
List<Integer> data = new ArrayList<>();
for (int day = 1; day < TimeUtils.getMaxDays(); day++) {
Integer dd = 0;
for (Map<String, Object> mm: sqlMap) {
if (Integer.valueOf(mm.get("time_day").toString()).equals(day) && mm.get("name").equals(name)) {
dd = Integer.valueOf(mm.get("count").toString());
sqlMap.remove(mm);
break;
}
}
data.add(dd);
}
map.put("data", data);
result.add(map);
}
Map<String, Object> resultMap = new HashMap<>();
resultMap.put("data", result);
resultMap.put("xdata", xData);
return resultMap;
}

@Override
public Map<String, Object> selectOverview() {
List<Long> cameraIdList = cameraIdListByLogin();
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}
LambdaQueryWrapper<Report> queryWrapper_0 = new LambdaQueryWrapper<>();
queryWrapper_0.eq(Report::getAuditState, 0);
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper_0.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper_0.eq(Report::getCameraId, 0L);
}
}
LambdaQueryWrapper<Report> queryWrapper_1 = new LambdaQueryWrapper<>();
queryWrapper_1.eq(Report::getAuditState, 1);
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper_1.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper_1.eq(Report::getCameraId, 0L);
}
}
Map<String, Object> map = new HashMap<>();
map.put("all", this.count(queryWrapper));
map.put("untreated", this.count(queryWrapper_0));
map.put("processed", this.count(queryWrapper_1));
return map;
}

/**
* Clear Divide Alert Record, Default 30 day
* @author Abyss
* @date 2024/2/18 18:50
*/
@Override
public void clearReport() {
String day = configService.getByValTag("clearReportDay");
if (StringUtils.isBlank(day)) {
configService.saveData("Scheduled Task Clear Divide Alert Info Keep day Number","clearReportDay","1");
day ="1";
}
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
if (day.equals("0")) {
queryWrapper.lt(Report::getCreatedAt, TimeUtils.getZero(new Date()));
} else {
queryWrapper.lt(Report::getCreatedAt, DateUtils.addDays(new Date(), -Integer.parseInt(day)));
}
List<Report> reportList = this.list(queryWrapper);
for (Report report: reportList) {
// FileUtil.del(report.getFileName());
// Delete Recording
recordService.deleteRecord(report.getRecordId());
}
this.remove(queryWrapper);
}

/**
* by Algorithm id Group, Count Count
*
* @param startMills
* @param endMills
*/
@Override
public List<Map<String, Object>> countAlgorithmGroupBy(long startMills, long endMills) {
List<Long> cameraIdList = null; // cameraIdListByLogin();
QueryWrapper<Report> queryWrapper;
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper = new QueryWrapper<Report>()
.select("algorithm_id, count(0) as cnt")
.ge("created_mills", startMills)
.lt("created_mills", endMills)
.in("camera_id", cameraIdList)
.eq("display", 0)
.groupBy("algorithm_id");
} else {
queryWrapper = new QueryWrapper<Report>()
.select("algorithm_id, count(0) as cnt")
.ge("created_mills", startMills)
.lt("created_mills", endMills)
.eq("camera_id", 0L)
.eq("display", 0)
.groupBy("algorithm_id");
}
} else {
queryWrapper = new QueryWrapper<Report>()
.select("algorithm_id, count(0) as cnt")
.ge("created_mills", startMills)
.lt("created_mills", endMills)
.eq("display", 0)
.groupBy("algorithm_id");
}


return this.listMaps(queryWrapper);
}

/**
* Query Export Data
*
* @param algorithmId
* @param cameraId
* @param startMills
* @param endMills
* @param state
* @param markList
* @return
*/
@Override
public List<Report> listExport(Long algorithmId, Long cameraId, long startMills, long endMills, Integer state, List<Integer> markList) {
//
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Report::getFileName, Report::getId); // only Query File Address
if(algorithmId!= null) {
queryWrapper.eq(Report::getAlgorithmId, algorithmId);
}
if(cameraId!= null) {
queryWrapper.eq(Report::getCameraId, cameraId);
}
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}

queryWrapper.ge(Report::getCreatedMills, startMills);
queryWrapper.lt(Report::getCreatedMills, endMills);
if(state!= null && (state == 1 || state == 2)) {
queryWrapper.eq(Report::getAuditState, 1); // Reviewed
queryWrapper.eq(Report::getAuditResult, state); // 1- Normal Alarm 2- wrong Report Alarm
}
if(CollUtil.isNotEmpty(markList)){
queryWrapper.in(Report::getMark, markList);
}
List<Report> reportList = this.list(queryWrapper);
if(reportList == null) {
return new ArrayList<>();
}
return reportList;
}

@Autowired
private ApDepartService apDepartService;

//
private List<Long> cameraIdListByLogin() {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null || account.getIsSuper() == null || account.getIsSuper().equals(1)) {
return null;
}
List<Long> cameraIdList = new ArrayList<>();
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
List<Long> departIdsByAccount = apDepartService.getCurrentAndChildIds(account.getDepartId());
queryWrapper.in(Location::getDepartId,departIdsByAccount);
List<Location> locationList = locationService.list(queryWrapper);
for (Location location: locationList) {
List<Camera> cameraList = cameraService.listByBoxId(location.getId());
for (Camera camera: cameraList) {
if (!cameraIdList.contains(camera.getId())) cameraIdList.add(camera.getId());
}
}
return cameraIdList;
}

/**
* By Department Export
*
* @param algorithmId
* @param cameraId
* @param startMills
* @param endMills
* @param state
* @param queryDepartIds
* @param markList
* @return
*/
@Override
public List<Report> listExportByDeparts(Long algorithmId, Long cameraId, long startMills, long endMills, Integer state, List<Long> queryDepartIds, List<Integer> markList) {
//
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
if(algorithmId!= null) {
queryWrapper.eq(Report::getAlgorithmId, algorithmId);
}
if(cameraId!= null) {
queryWrapper.eq(Report::getCameraId, cameraId);
}
List<Long> cameraIdList = cameraIdListByLogin();
if (cameraIdList!= null) {
if (!cameraIdList.isEmpty()) {
queryWrapper.in(Report::getCameraId, cameraIdList);
} else {
queryWrapper.eq(Report::getCameraId, 0L);
}
}

queryWrapper.ge(Report::getCreatedMills, startMills);
queryWrapper.lt(Report::getCreatedMills, endMills);
if(state!= null && (state == 1 || state == 2)) {
queryWrapper.eq(Report::getAuditState, 1); // Reviewed
queryWrapper.eq(Report::getAuditResult, state); // 1- Normal Alarm 2- wrong Report Alarm
}
// Determine Department
if(!queryDepartIds.isEmpty()) {
queryWrapper.in(Report::getDepartId, queryDepartIds);
}
if(CollUtil.isNotEmpty(markList)){
queryWrapper.in(Report::getMark, markList);
}
List<Report> reportList = this.list(queryWrapper);
if(reportList == null) {
return new ArrayList<>();
}
return reportList;
}

/**
* Query Recent not has Recording Alarm
*
* @param mills
* @return
*/
@Override
public List<Report> listRecentByMills(long mills) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getRecordId, 0);
queryWrapper.gt(Report::getCreatedMills, mills);
queryWrapper.eq(Report::getDisplay, 0);
queryWrapper.orderByDesc(Report::getCreatedMills);
return this.list(queryWrapper);
}

/**
* Query Recording
*
* @param limit
* @return
*/
@Override
public List<Report> listRecordByLimit(Integer limit) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Report::getId, Report::getRecordId);
queryWrapper.gt(Report::getRecordId, 0);
queryWrapper.orderByDesc(Report::getCreatedMills);
return this.list(queryWrapper);
}

/**
* Query Collect Data
*
* @param exportVo
* @return
*/
@Override
public List<Report> listCollect(ReportCollectExportVo exportVo) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Report::getId, Report::getFileName);
queryWrapper.eq(Report::getDisplay, 8);
if(exportVo.getCameraId()!= null) {
queryWrapper.eq(Report::getCameraId, exportVo.getCameraId());
}
if(exportVo.getAlgorithmId()!= null) {
queryWrapper.eq(Report::getAlgorithmId, exportVo.getAlgorithmId());
}
if(exportVo.getStartTime()!= null) {
queryWrapper.gt(Report::getCreatedMills, exportVo.getStartTime().getTime());
}
if(exportVo.getEndTime()!= null) {
queryWrapper.lt(Report::getCreatedMills, exportVo.getEndTime().getTime());
}
if(exportVo.getReportIds()!= null &&!exportVo.getReportIds().isEmpty()) {
queryWrapper.in(Report::getId, exportVo.getReportIds());
}
if(exportVo.getDepartIds()!= null &&!exportVo.getDepartIds().isEmpty()) {
queryWrapper.in(Report::getDepartId, exportVo.getDepartIds());
}
return this.list(queryWrapper);
}

/**
* Delete Collect Data
*/
@Override
public void removeCollect() {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getDisplay, 8);
this.remove(queryWrapper);
}

/**
* Query Collect Data Total
*
* @return
*/
@Override
public Integer getCollectCount() {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getDisplay, 8);
return this.count(queryWrapper);
}

/**
* Count Process Count
*
* @param cameraId
* @param algorithmId
* @param startMills
* @param endMills
* @param queryDepartIds
* @param auditResult
* @return
*/
@Override
public int getAuditResultStatics(Long cameraId, Long algorithmId, Long startMills, Long endMills, List<Long> queryDepartIds, Integer auditResult) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getDisplay, 0);
if(cameraId!= null) {
queryWrapper.eq(Report::getCameraId, cameraId);
}
if(algorithmId!= null) {
queryWrapper.eq(Report::getAlgorithmId, algorithmId);
}
if(queryDepartIds!= null &&!queryDepartIds.isEmpty()) {
queryWrapper.in(Report::getAlgorithmId, queryDepartIds);
}
if(auditResult!= null) {
queryWrapper.eq(Report::getAuditResult, auditResult);
}
if(startMills!= null) {
queryWrapper.gt(Report::getCreatedMills, startMills);
}
if(endMills!= null) {
queryWrapper.lt(Report::getCreatedMills, endMills);
}
return this.count(queryWrapper);
}

/**
* Query Pagination Data
* @param algorithmId
* @param page
* @param limit
* @return
*/
@Override
public IPage<Report> listMisData(Long algorithmId, Integer page, Integer limit) {
IPage<Report> pageInfo = new Page<>(page, limit);
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
if(algorithmId!= null) {
queryWrapper.eq(Report::getAlgorithmId, algorithmId);
}
queryWrapper.orderByDesc(Report::getCreatedMills);
return this.page(pageInfo, queryWrapper);
}

/**
* By Camera and Time Area between Query Alarm
*
* @param cameraId
* @param startTime
* @param endTime
* @return
*/
@Override
public List<Report> listByCameraAndTimeBetween(Long cameraId, Long startTime, Long endTime) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Report::getId);
queryWrapper.eq(Report::getCameraId, cameraId);
queryWrapper.ge(Report::getCreatedMills, startTime);
queryWrapper.le(Report::getCreatedMills, endTime);
queryWrapper.eq(Report::getDisplay, 0);
queryWrapper.eq(Report::getRecordId, 0);
List<Report> datas = this.list(queryWrapper);
return datas == null? new ArrayList<>(): datas;
}

/**
* By Box ID summary total
*
* @param boxId
* @param startMills
* @param endMills
* @return
*/
@Override
public int countByBox(Long boxId, Long startMills, Long endMills) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getBoxId, boxId);
queryWrapper.gt(Report::getCreatedMills, startMills);
queryWrapper.lt(Report::getCreatedMills, endMills);
queryWrapper.eq(Report::getDisplay, 0);
return this.count(queryWrapper);
}

/**
* Camera ID
*
* @param cameraId
* @param startMills
* @param endMills
* @param algorithmIds
* @param isShowStatus
* @return
*/
@Override
public int countByCamera(Long cameraId, Long startMills, Long endMills, List<Long> algorithmIds, String isShowStatus) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getCameraId, cameraId);
queryWrapper.gt(Report::getCreatedMills, startMills);
queryWrapper.lt(Report::getCreatedMills, endMills);
queryWrapper.eq(Report::getDisplay, 0);
if(StringUtils.equalsIgnoreCase("1", isShowStatus)) {
queryWrapper.eq(Report::getAuditResult, 0);
}
queryWrapper.in(CollUtil.isNotEmpty(algorithmIds), Report::getAlgorithmId, algorithmIds);
return this.count(queryWrapper);
}

/**
* By Batch Camera Count Data
*
* @param cameraIds
* @param startMillsBox
* @param endMillsBox
* @param isShowStatus
* @return
*/
@Override
public int countByBatchCameras(List<Long> cameraIds, long startMillsBox, long endMillsBox, List<Long> algorithmIds, String isShowStatus) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getDisplay, 0);
if(StringUtils.equalsIgnoreCase("1", isShowStatus)) {
queryWrapper.eq(Report::getAuditResult, 0);
}
queryWrapper.gt(Report::getCreatedMills, startMillsBox);
queryWrapper.lt(Report::getCreatedMills, endMillsBox);
queryWrapper.in(Report::getCameraId, cameraIds);
queryWrapper.in(CollUtil.isNotEmpty(algorithmIds), Report::getAlgorithmId, algorithmIds);
return this.count(queryWrapper);
}


/**
* Query Recent Record
*
* @param nearlyVo
* @return
*/
@Override
public Report getNearlyRecord(ReportNearlyVo nearlyVo) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Report::getId);
if(nearlyVo.getCameraId()!= null) {
queryWrapper.eq(Report::getCameraId, nearlyVo.getCameraId());
}
if(nearlyVo.getAlgorithmId()!= null) {
queryWrapper.eq(Report::getAlgorithmId, nearlyVo.getAlgorithmId());
}
if(StrUtil.isNotBlank(nearlyVo.getStartDate())) {
long startMills = DateUtil.parse(nearlyVo.getStartDate(),"yyyy-MM-dd HH:mm:ss").getTime();
queryWrapper.gt(Report::getCreatedMills, startMills);
}
if(StrUtil.isNotBlank(nearlyVo.getEndDate())) {
long endMills = DateUtil.parse(nearlyVo.getEndDate(),"yyyy-MM-dd HH:mm:ss").getTime();
queryWrapper.lt(Report::getCreatedMills, endMills);
}
if(nearlyVo.getAuditResult()!= null &&!nearlyVo.getAuditResult().isEmpty()) {
queryWrapper.in(Report::getAuditResult, nearlyVo.getAuditResult());
}
if(nearlyVo.getDepartIds()!= null &&!nearlyVo.getDepartIds().isEmpty()) {
queryWrapper.in(Report::getDepartId, nearlyVo.getDepartIds());
}
if(CollUtil.isNotEmpty(nearlyVo.getMarkList())){
queryWrapper.in(Report::getMark, nearlyVo.getMarkList());
}
if(nearlyVo.getType() == 0) {// left Side
queryWrapper.gt(Report::getId, nearlyVo.getId());
queryWrapper.orderByAsc(Report::getId);
}
if(nearlyVo.getType() == 1) {
queryWrapper.lt(Report::getId, nearlyVo.getId());
queryWrapper.orderByDesc(Report::getId);
}

//queryWrapper.last("limit 0, 1");
return this.getOne(queryWrapper, false);
}

/**
* Count Count
*
* @param startMills
* @param endMills
* @param auditResult
* @return
*/
@Override
public int countByAudit(long startMills, long endMills, int auditResult) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.ge(Report::getCreatedMills, startMills);
queryWrapper.lt(Report::getCreatedMills, endMills);
queryWrapper.eq(Report::getAuditResult, auditResult);
//queryWrapper.eq(Report::getDisplay, 0);
return this.count(queryWrapper);
}

/**
* Count Process Hour long
*
* @param startMills
* @param endMills
* @return
*/
@Override
public long sumByAuditTimeLen(long startMills, long endMills) {
return this.baseMapper.selectSumAuditTimeLen(startMills, endMills);
}

/**
* By Box ID and Process Status Count Data
*
* @param boxId
* @param auditResult
* @param startMills
* @param endMills
* @return
*/
@Override
public int countByBoxAndAudit(Long boxId, Integer auditResult, long startMills, long endMills) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getBoxId, boxId);
queryWrapper.eq(Report::getAuditResult, auditResult);
queryWrapper.gt(Report::getCreatedMills, startMills);
queryWrapper.lt(Report::getCreatedMills, endMills);
queryWrapper.eq(Report::getDisplay, 0);
return this.count(queryWrapper);
}

/**
* Update Push Result Info
*
* @param report
* @param pushStatus
* @param pushMsg
*/
@Override
public void updatePushResult(Report report, int pushStatus, String pushMsg) {
Report updateReport = new Report();
updateReport.setId(report.getId());
updateReport.setPushed(pushStatus);
updateReport.setPushMsg(pushMsg);
Date date = new Date();
Date createdAt = report.getCreatedAt();
if(createdAt.after(date)){
int randomSeconds = 5 + new java.util.Random().nextInt(11); // Generate 5-15 Random Number
date = DateUtil.offsetSecond(createdAt, randomSeconds);
}
updateReport.setAuditAt(date);
this.updateById(updateReport);
}

/**
* by Algorithm id and Process Status in Line Group, Count Count
*
* @param startMills
* @param endMills
*/
@Override
public List<Map<String, Object>> countAlgorithmAuditGroupBy(long startMills, long endMills) {
QueryWrapper<Report> queryWrapper;
queryWrapper = new QueryWrapper<Report>()
.select("algorithm_id, audit_result, count(0) as cnt")
.ge("created_mills", startMills)
.lt("created_mills", endMills)
.groupBy("algorithm_id, audit_result");
return this.listMaps(queryWrapper);
}

/**
* Count Alarm Process Hour long and Alarm Count
*
* @param startMills
* @param endMills
* @return
*/
@Override
public List<Map<String, Object>> sumAndCountAlarmAuditTime(long startMills, long endMills) {
QueryWrapper<Report> queryWrapper;
queryWrapper = new QueryWrapper<Report>()
.select("sum(TIMESTAMPDIFF(SECOND, created_at, audit_at)) as times, count(1) cnt")
.ge("created_mills", startMills)
.lt("created_mills", endMills)
.eq("display", 0)
.and(wrapper -> wrapper
.eq("audit_result", 1)
.or()
.eq("audit_result", 2)
.or()
.eq("audit_result", 3));
return this.listMaps(queryWrapper);
}


/**
* By Timestamp Delete Data
*
* @param mills
*/
@Override
public void deleteData(long mills) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.lt(Report::getCreatedMills, mills);
this.remove(queryWrapper);
}

/**
* By Timestamp Delete Image and Hide Data
*
* @param mills
*/
@Transactional(rollbackFor = Exception.class)
@Override
public void deleteDataImage(long mills) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Report::getId, Report::getFileName);
queryWrapper.lt(Report::getCreatedMills, mills);
queryWrapper.eq(Report::getDisplay, 0);

List<Report> reportList = this.list(queryWrapper);
if(reportList.isEmpty()) {
return;
}

List<Report> saveList = new ArrayList<>();
int c = 0;
for(Report report: reportList) {
// Delete Image
FileUtil.del(report.getFileName());

// Pending Delete Data
Report report1 = new Report();
report1.setId(report.getId());
report1.setDisplay(1);
saveList.add(report1);

c++;

// super over 500 Execute Save
if(c >= 500) {
this.saveOrUpdateBatch(saveList);

c = 0;
saveList.clear();
}
}

// most after Execute One sub Save
this.saveOrUpdateBatch(saveList);
}

/**
* Query Count Data
*
* @param startMills
* @param endMills
* @param auditResult
* @return
*/
@Override
public int getSummaryCount(long startMills, long endMills, Integer auditResult) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.ge(Report::getCreatedMills, startMills);
queryWrapper.lt(Report::getCreatedMills, endMills);
if(auditResult!= null) {
queryWrapper.eq(Report::getAuditResult, auditResult);
}
return this.count(queryWrapper);
}

/**
* Temp Hour Method - Update All Alarm Data Process Hour long
*/
@Override
public void updateAllHandleTime() {
baseMapper.updateAllHandleTime();
}

/**
* Temp Hour Method - Get most small Alarm Time
*/
@Override
public Date getMinDate() {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.orderByAsc(Report::getCreatedMills);
queryWrapper.last("limit 0, 1");
Report report = this.getOne(queryWrapper);
if(report == null) {
return null;
}
return report.getCreatedAt();
}

/**
* Query Count Process Hour long Data
*
* @param startMills
* @param endMills
* @return
*/
@Override
public int getSummaryHandleTime(long startMills, long endMills) {
QueryWrapper<Report> queryWrapper = new QueryWrapper<Report>()
.select("ifnull(sum(handle_time), 0) as cnt")
.ge("created_mills", startMills)
.lt("created_mills", endMills)
.and(wrapper -> wrapper
.eq("audit_result", 1)
.or()
.eq("audit_result", 2));
List<Map<String, Object>> dataList = this.listMaps(queryWrapper);
if(ObjectUtil.isEmpty(dataList)) {
return 0;
}
Object v = dataList.get(0).get("cnt");
return Convert.toInt(v, 0);
}

@Override
public List<ReportDTO> selectByBatchCameras(List<Long> cameraIds, long startMillsBox, long endMillsBox, String isShowStatus) {
if(ObjectUtil.isEmpty(cameraIds)) {
return Collections.emptyList();
}
String auditResult ="";
if(StringUtils.equalsIgnoreCase("1", isShowStatus)){
auditResult ="1";
}
return baseMapper.selectByBatchCameras(cameraIds, startMillsBox, endMillsBox, auditResult);
}

@Override
public List<Map<String, Integer>> countGroupByArea(long startMills, long endMills) {
return baseMapper.countGroupByArea(startMills, endMills);
}

@Override
public void deleteReport(ReportDTO reportDTO) {
baseMapper.deleteReport(reportDTO.getAlgorithmName(), reportDTO.getCreatedAt(), reportDTO.getLimit());
}

@Override
public void markReport(ReportDTO reportDTO) {
Report report = new Report();
report.setId(reportDTO.getId());
report.setMark(reportDTO.getMark());
this.updateById(report);
}

/**
* By summary total Condition Count Data
* @param vo
* @return
*/
@Override
public int countForSummary(ReportSummaryVo vo) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(vo.getCameraId()!= null, Report::getCameraId, vo.getCameraId());
queryWrapper.eq(vo.getAuditResult()!= null, Report::getAuditResult, vo.getAuditResult());
queryWrapper.eq(vo.getAppendResult()!= null, Report::getAuditResult, vo.getAppendResult());
queryWrapper.in(ObjectUtil.isNotEmpty(vo.getMark()), Report::getMark, vo.getMark());
queryWrapper.ge(vo.getStartTime()!= null, Report::getCreatedAt, vo.getStartTime());
queryWrapper.le(vo.getEndTime()!= null, Report::getCreatedAt, vo.getEndTime());
return this.count(queryWrapper);
}

/**
* By Recording id Query All Alarm id
*
* @param recordId
* @return
*/
@Override
public List<Long> getIdsByRecordId(Long recordId) {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Report::getId);
queryWrapper.eq(Report::getRecordId, recordId);
List<Report> results = this.list(queryWrapper);
List<Long> ids = results.stream()
.map(Report::getId)
.collect(Collectors.toCollection(() -> new ArrayList<>(results.size())));
return ids;
}
}
