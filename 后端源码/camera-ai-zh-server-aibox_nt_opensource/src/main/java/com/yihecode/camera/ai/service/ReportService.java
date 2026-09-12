package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.web.map.dto.ReportDTO;
import com.yihecode.camera.ai.web.vo.ReportCollectExportVo;
import com.yihecode.camera.ai.web.vo.ReportNearlyVo;
import com.yihecode.camera.ai.web.vo.ReportSummaryVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* Alarm Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface ReportService extends IService<Report> {
    /**
* Save (Person build Data)
* @param report
* @return
*/
    boolean saveReport(Report report);
    /**
*
* @param pageObj
* @param report
* @return
*/
    IPage<Report> listPage(IPage<Report> pageObj, Report report);
    List<Report> listPageGroupByCamera(IPage<Report> pageObj, Report report);

    /**
*
* @param startDate
* @param endDate
* @return
*/
    List<Map<String, Object>> findAlgorithmRatio(Date startDate, Date endDate);

    /**
*
* @param startDate
* @param endDate
* @return
*/
    List<Map<String, Object>> findCamera(Date startDate, Date endDate);

    /**
*
* @param startDate
* @param endDate
* @return
*/
    List<Map<String, Object>> findCameraAlgorithm(Date startDate, Date endDate);

    /**
*
* @param id
* @param display
*/
    void updateDisplay(Long id, Integer display);

    /**
* Review
* @param id
* @param result
*/
    void updateAudit(Long id, Integer result);

    /**
* By Algorithm id, Start and End ms Value Query Total
* @param algorithmId
* @param startMills
* @param endMills
* @return
*/
    Integer getAlgorithmCounter(Long algorithmId, long startMills, long endMills);

    /**
* Start and End ms Value Query Total
* @param startMills
* @param endMills
* @return
*/
    int getCounter(long startMills, long endMills, List<Long> departIds);

    /**
* Page Query
* @param objectPage
* @param cameraId
* @param algorithmId
* @param startMills
* @param endMills
* @return
*/
    IPage<Report> listByPage(IPage<Report> objectPage, Long cameraId, Long algorithmId, Integer type, Long startMills, Long endMills, Long alarmLevelId, List<Long> queryDepartIds, Integer display, List<Integer> auditResults, List<Integer> markList);

    IPage<Report> listByPageApp(IPage<Report> objectPage, String cameraName, Long algorithmId, Integer type, Long startMills, Long endMills, Long alarmLevelId, Integer auditState);

    /**
* Query Recent 3 day Record
* @param nums
* @return
*/
    List<Report> listNewly(int nums);

    /**
* Count Total
* @param startMills
* @param endMills
* @return
*/
    Integer getCount(Long startMills, Long endMills, Long cameraId, Long algorithmId, Integer type);

    /**
* Count Total
* @param startMills
* @param endMills
* @return
*/
    Integer getMarkCount(Long startMills, Long endMills, Long cameraId, Long algorithmId);

    /**
* By Algorithm Count Count
* @param startMills
* @param endMills
* @return
*/
    Map<Long, Integer> getCountByAlgorithm(Long startMills, Long endMills);

    /**
* By Algorithm Count Count (List Back)
* @author Abyss
* @date 2023/11/6 16:33
* @param startMills
* @param endMills
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    List<Map<String, Object>> getCountByAlgorithmReList(Long startMills, Long endMills);

    /**
* Get Today Day Basic Count Data
* @author Abyss
* @date 2023/12/20 14:57
* @return java.util.Map<java.lang.String,java.lang.Object>
*/
    Map<String, Object> selectTodayCount();

    /**
* Get near Seven Day Basic Count Data
* @author Abyss
* @date 2023/12/20 15:04
* @return java.util.Map<java.lang.String,java.lang.Object>
*/
    Map<String, Object> select7DayCount();

    /**
* Get This Month Basic Count Data
* @author Abyss
* @date 2023/12/20 15:04
* @return java.util.Map<java.lang.String,java.lang.Object>
*/
    Map<String, Object> selectMonthCount();

    /**
* Get Alert Type Row Line (Today Day)
* @author Abyss
* @date 2023/12/20 15:23
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    List<Map<String, Object>> selectReportTypeRankingToday();

    /**
* Get Alert Type Row Line (near 7 Day)
* @author Abyss
* @date 2023/12/20 15:23
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    List<Map<String, Object>> selectReportTypeRanking7Day();

    /**
* Get Alert Type Row Line (This Month)
* @author Abyss
* @date 2023/12/20 15:23
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    List<Map<String, Object>> selectReportTypeRankingMonth();

    /**
* Get Alert Type - Count Count (This Day, Each Two h One Count)
* @author Abyss
* @date 2023/12/20 16:43
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    Map<String, Object> selectReportTypeCountToday();

    /**
* Get Alert Type - Count Count (near Seven Day, Each day One Count)
* @author Abyss
* @date 2023/12/20 17:36
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    Map<String, Object> selectReportTypeCount7Day();

    /**
* Get Alert Type - Count Count (This Month, Each day One Count)
* @author Abyss
* @date 2023/12/20 18:38
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    Map<String, Object> selectReportTypeCountMonth();

    /**
* Get total View Data
* @author Abyss
* @date 2023/12/23 14:54
* @return java.util.Map<java.lang.String,java.lang.Object>
*/
    Map<String, Object> selectOverview();

    void clearReport();

    /**
* by Algorithm id Group, Count Count
* @param startMills
* @param endMills
*/
    List<Map<String, Object>> countAlgorithmGroupBy(long startMills, long endMills);

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
    List<Report> listExport(Long algorithmId, Long cameraId, long startMills, long endMills, Integer state, List<Integer> markList);

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
    List<Report> listExportByDeparts(Long algorithmId, Long cameraId, long startMills, long endMills, Integer state, List<Long> queryDepartIds, List<Integer> markList);

    /**
* Query Recent not has Recording Alarm
* @param mills
* @return
*/
    List<Report> listRecentByMills(long mills);

    /**
* Query Recording
* @param limit
* @return
*/
    List<Report> listRecordByLimit(Integer limit);

    /**
* Query Collect Data
* @param exportVo
* @return
*/
    List<Report> listCollect(ReportCollectExportVo exportVo);

    /**
* Delete Collect Data
*/
    void removeCollect();

    /**
* Query Collect Data Total
* @return
*/
    Integer getCollectCount();

    /**
* Count Process Count
* @param cameraId
* @param algorithmId
* @param startMills
* @param endMills
* @param queryDepartIds
* @param auditResult
* @return
*/
    int getAuditResultStatics(Long cameraId, Long algorithmId, Long startMills, Long endMills, List<Long> queryDepartIds, Integer auditResult);

    /**
* Query Pagination Data
* @param algorithmId
* @param page
* @param limit
* @return
*/
    IPage<Report> listMisData(Long algorithmId, Integer page, Integer limit);

    /**
* By Camera and Time Area between Query Alarm
* @param cameraId
* @param startTime
* @param endTime
* @return
*/
    List<Report> listByCameraAndTimeBetween(Long cameraId, Long startTime, Long endTime);

    /**
* By Box ID summary total
* @param boxId
* @param startMills
* @param endMills
* @return
*/
    int countByBox(Long boxId, Long startMills, Long endMills);

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
    int countByCamera(Long cameraId, Long startMills, Long endMills, List<Long> algorithmIds, String isShowStatus);

    /**
* Query Recent Record
* @param nearlyVo
* @return
*/
    Report getNearlyRecord(ReportNearlyVo nearlyVo);

    /**
* Count Count
* @param startMills
* @param endMills
* @param auditResult
* @return
*/
    int countByAudit(long startMills, long endMills, int auditResult);

    /**
* Count Process Hour long
* @param startMills
* @param endMills
* @return
*/
    long sumByAuditTimeLen(long startMills, long endMills);

    /**
* By Box ID and Process Status Count Data
* @param boxId
* @param auditResult
* @param startMills
* @param endMills
* @return
*/
    int countByBoxAndAudit(Long boxId, Integer auditResult, long startMills, long endMills);

    /**
* Update Push Result Info
* @param report
* @param pushStatus
* @param pushMsg
*/
    void updatePushResult(Report report, int pushStatus, String pushMsg);

    /**
* by Algorithm id and Process Status in Line Group, Count Count
* @param startMills
* @param endMills
* @return
*/
    List<Map<String, Object>> countAlgorithmAuditGroupBy(long startMills, long endMills);

    /**
* Count Alarm Process Hour long and Alarm Count
* @param startMills
* @param endMills
* @return
*/
    List<Map<String, Object>> sumAndCountAlarmAuditTime(long startMills, long endMills);

    /**
* By Batch Camera Count Data
*
* @param cameraIds
* @param startMillsBox
* @param endMillsBox
* @param isShowStatus
* @return
*/
    int countByBatchCameras(List<Long> cameraIds, long startMillsBox, long endMillsBox, List<Long> algorithmIds, String isShowStatus);
    /**
* By Batch Camera and Check Algorithm Count Data
* @param cameraIds
* @param startMillsBox
* @param endMillsBox
* @return
*/
    /**
* By Timestamp Delete Data
* @param mills
*/
    void deleteData(long mills);

    /**
* By Timestamp Delete Image and Hide Data
* @param mills
*/
    void deleteDataImage(long mills);

    /**
* Query Count Data
* @param startMills
* @param endMills
* @param auditResult
* @return
*/
    int getSummaryCount(long startMills, long endMills, Integer auditResult);

    /**
* Temp Hour Method - Update All Alarm Data Process Hour long
*/
    void updateAllHandleTime();

    /**
* Temp Hour Method - Get most small Alarm Time
*/
    Date getMinDate();

    /**
* Query Count Process Hour long Data
* @param startMills
* @param endMills
* @return
*/
    int getSummaryHandleTime(long startMills, long endMills);

    List<ReportDTO> selectByBatchCameras(List<Long> cameraIds, long startMillsBox, long endMillsBox, String isShowStatus);

    /**
* By pre Alarm Region Count Alert Count
* @param startMills
* @param endMills
* @return
*/
    List<Map<String, Integer>> countGroupByArea(long startMills, long endMills);

    void deleteReport(ReportDTO reportDTO);

    void markReport(ReportDTO reportDTO);

    /**
* By summary total Condition Count Data
* @param vo
* @return
*/
    int countForSummary(ReportSummaryVo vo);

    /**
* By Recording id Query All Alarm id
* @param recordId
* @return
*/
    List<Long> getIdsByRecordId(Long recordId);
}
