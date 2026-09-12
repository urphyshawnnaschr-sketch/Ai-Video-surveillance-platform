package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.dto.ApProjectQueryDTO;
import com.yihecode.camera.ai.dto.ApProjectStatisticsQueryDTO;
import com.yihecode.camera.ai.entity.ap.ApProjectDO;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.vo.ap.*;

import java.util.List;

/**
* pProject Service
*/
public interface ApProjectService extends IService<ApProjectDO> {

    /**
* Add Object, Back Add Object ID
*/
    Long savepProject(ApProjectDTO project) throws BizException;

    /**
* By Primary Key in Line Physical Delete, Cautious Use
*/
    void deletepProjectById(Long projectId);



    /**
* By Primary Key Modify Object
*/
    void updatepProjectById(ApProjectDTO project) throws BizException;





    /**
* By Primary Key Query
*/
    ApProjectDTO getpProjectById(Long projectId);


    /**
* Get Object List
*/
    List<ApProjectDTO> findpProjectList(ApProjectQueryDTO projectQuery);

    /**
* Get Object Page List
*/
    IPage<ApProjectDTO> findpProjectPage(ApProjectQueryDTO pProjectQuery);
    /** with down supplement Charge its It Method*/
    /**
* Project Deliver
* @param projectId
*/
    void submitProject(Long projectId) throws BizException;

    /**
* Get down One id
* @return
*/
    Long getNextId();

    /**
* Init id
*/
    void initProject();

    /**
* Count Annotation in Degree
* @param projectId
* @param type
* @return
*/
    ComputeAnnotationVO computeAnnotation(Long projectId, Integer type);

    /**
* Update Project Status and Annotation Count
* @param projectId
*/
    void updataProjectStatusLableNum(Long projectId) throws BizException;

    /**
* Count Annotation Detail Detail Info
* @param projectId
* @return
*/
    List<ComputeAnnotationLableVO> computeAnnotationLableList(Long projectId);

    /**
* Count Quality Check Situation
* @param projectId
* @return
*/
    ComputeAnnotationReviewVO annotationReview(Long projectId);


    /**
* Project Latitude Count
* @param apProjectStatisticsQueryDTO
* @return
*/
    ProjectEfficiencyVO projectEfficiency(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException;

    /**
* Team Latitude Count
* @param apProjectStatisticsQueryDTO
* @return
*/
    ProjectEfficiencyVO teamEfficiency(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException;


    IPage<ProjectStatisticsVO> projectStatistics(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException;
    IPage<ProjectStatisticsVO> teamStatistics(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException;


}