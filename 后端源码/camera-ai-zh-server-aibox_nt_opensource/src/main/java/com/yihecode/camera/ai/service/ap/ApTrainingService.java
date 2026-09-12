package com.yihecode.camera.ai.service.ap;

import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.web.ap.vo.*;

import java.util.List;

public interface ApTrainingService {

    int count(TrainingListRequestVo request);

    List<ReportAggVo> list(TrainingListRequestVo request);

    Long[] range(TrainingBaseVo request) throws BizException;

    int count(TrainingBaseVo request, Long[] range) throws BizException;

    List<Report> list(TrainingBaseVo request,  Long[] range, Integer page, Integer limit);

    Long createProject(TrainingCreateProjectRequestVo requestVo, List<Report> reportList) throws Exception;

    int delete(TrainingBaseVo request, Long[] range);

    Integer delete(TrainingDeleteRequestVo request);

}
