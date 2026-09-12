package com.yihecode.camera.ai.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.web.map.dto.ReportDTO;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
* Alarm Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface ReportMapper extends BaseMapper<Report> {

    List<Map<String, Object>> selectAlgorithmRatio(@Param("ew") Wrapper<Report> wrapper);

    List<Map<String, Object>> selectCamera(@Param("ew") Wrapper<Report> wrapper);

    List<Map<String, Object>> selectCameraAlgorithm(@Param("ew") Wrapper<Report> wrapper);

    List<Map<String, Object>> selectAlgorithmStatics(Map<String, Object> params);

    /**
* Count This Day Alert sub Number
* @author Abyss
* @date 2023/12/20 14:23
* @return int
*/
    int countToday();
    /**
* Count 7 Day inner Alert sub Number
* @author Abyss
* @date 2023/12/20 14:23
* @return int
*/
    int count7Day();
    /**
* Count This Month Alert sub Number
* @author Abyss
* @date 2023/12/20 14:24
* @return int
*/
    int countMonth();
    /**
* Count This Day Alert Process sub Number
* @author Abyss
* @date 2023/12/20 14:24
* @return int
*/
    int countTodayByAuditAt();
    /**
* Count 7 Day inner Alert Process sub Number
* @author Abyss
* @date 2023/12/20 14:24
* @return int
*/
    int count7DayByAuditAt();
    /**
* Count This Month Alert Process sub Number
* @author Abyss
* @date 2023/12/20 14:24
* @return int
*/
    int countMonthByAuditAt();
    /**
* Count This Day Average Alert Process Time
* @author Abyss
* @date 2023/12/20 14:25
* @return double
*/
    double selectAverageProcessingTimeToday();
    /**
* Count 7 Day inner Average Alert Process Time
* @author Abyss
* @date 2023/12/20 14:25
* @return double
*/
    double selectAverageProcessingTime7Day();
    /**
* Count This Month Average Alert Process Time
* @author Abyss
* @date 2023/12/20 14:25
* @return double
*/
    double selectAverageProcessingTimeMonth();
    /**
* Query This Day Alert sub Number most multi Algorithm Name
* @author Abyss
* @date 2023/12/20 14:52
* @return java.lang.String
*/
    String selectNameMostToday();
    /**
* Query near Seven Day Alert sub Number most multi Algorithm Name
* @author Abyss
* @date 2023/12/20 14:52
* @return java.lang.String
*/
    String selectNameMost7Day();
    /**
* Query This Month Alert sub Number most multi Algorithm Name
* @author Abyss
* @date 2023/12/20 14:52
* @return java.lang.String
*/
    String selectNameMostMonth();

    /**
* Alert Type Row Line (Today Day)
* @author Abyss
* @date 2023/12/20 15:14
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    List<Map<String, Object>> selectReportTypeRankingToday(@Param("cameraIdList") List<Long> cameraIdList);
    /**
* Alert Type Row Line (near 7 Day)
* @author Abyss
* @date 2023/12/20 15:14
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    List<Map<String, Object>> selectReportTypeRanking7Day(@Param("cameraIdList") List<Long> cameraIdList);
    /**
* Alert Type Row Line (This Month)
* @author Abyss
* @date 2023/12/20 15:14
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    List<Map<String, Object>> selectReportTypeRankingMonth(@Param("cameraIdList") List<Long> cameraIdList);

    /**
* Get Alert Type - Count Count (This Day, Each h One Count)
* @author Abyss
* @date 2023/12/20 16:27
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    List<Map<String, Object>> selectReportTypeCountToday(@Param("cameraIdList") List<Long> cameraIdList);

    /**
* Get Alert Type - Count Count (This Month, Each day One Count)
* @author Abyss
* @date 2023/12/20 19:48
* @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
*/
    List<Map<String, Object>> selectReportTypeCountMonth(@Param("cameraIdList") List<Long> cameraIdList);

    /**
* Get Alert Type
* @author Abyss
* @date 2023/12/20 17:44
* @return java.util.List<java.lang.Long>
*/
    List<Long> selectReportType(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("cameraIdList") List<Long> cameraIdList);

    /**
* Count Alert Data
* @author Abyss
* @date 2023/12/20 17:53
* @param algorithmId
* @param startTime
* @param endTime
* @return int
*/
    int countReport(@Param("algorithmId")Long algorithmId, @Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("cameraIdList") List<Long> cameraIdList);

    /**
* By Start Time and End Time Query total Process Hour long
* @param startMills
* @param endMills
* @return
*/
    long selectSumAuditTimeLen(@Param("startMills") long startMills, @Param("endMills") long endMills);

    /**
* Temp Hour Method - Update All Alarm Data Process Hour long
*/
    void updateAllHandleTime();

    List<ReportDTO> selectByBatchCameras(@Param("cameraIds") List<Long> cameraIds, @Param("startMillsBox") long startMillsBox, @Param("endMillsBox") long endMillsBox, @Param("auditResult") String auditResult);

    List<Map<String, Integer>> countGroupByArea(@Param("startMills") long startMills, @Param("endMills") long endMills);

    void deleteReport(@Param("algorithmName") String algorithmName, @Param("createdAt") Date createdAt, @Param("limit") int limit);
}