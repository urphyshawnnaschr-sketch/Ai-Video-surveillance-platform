package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.web.face.vo.FaceReportPageVo;
import com.yihecode.camera.ai.web.face.vo.FaceReportUserPageVo;

import java.util.Date;
import java.util.List;

/**
* Face Recognition Result - Face Recognition System
*
* @Author 465769438@qq.com
*/
public interface FaceReportService extends IService<FaceReport> {

    /**
* Page Query
* @param page
* @param limit
* @param hasStranger
* @param cameraId
* @return
*/
    IPage<FaceReport> listPage(Integer page, Integer limit, Integer hasStranger, Long cameraId, Long groupId);

    IPage<FaceReport> listPage2(Integer page, Integer limit, Integer hasStranger, Long cameraId, Long groupId, String startTime, String endTime, String name, String phone, Integer desc);

    void updateGroupIdByUserId(Long groupId, Long userId);

    void removeByUser(Long userId);

    Integer countByGroupId(Long groupId);

    void clearReport();

    /**
* Query Person member Trajectory
* @param userId
* @param startDate
* @param endDate
*/
    List<FaceReport> listByUser(Long userId, Date startDate, Date endDate, Integer hasStranger, List<Long> cameraIds);

    /**
* Query Temp near Record, type=0, by Time up order up One, type=1, by Time down order down One
* @param pageVo
* @return
*/
    FaceReport getNearly(FaceReportPageVo pageVo);

    /**
* Page Query
* @param pageVo
* @return
*/
    IPage<FaceReport> listPageV5(FaceReportPageVo pageVo);

    /**
* By User ID Query
* @param pageVo
* @return
*/
    List<FaceReport> listUserAllV2(FaceReportUserPageVo pageVo);

    /**
* By User ID Query
* @param pageVo
* @return
*/
    IPage<FaceReport> listUserPageV2(FaceReportUserPageVo pageVo);

    /**
* By User ID Delete
* @param userId
*/
    void deleteByUser(Long userId);
}
