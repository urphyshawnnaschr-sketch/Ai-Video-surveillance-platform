package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.face.FaceTrackFlow;
import com.yihecode.camera.ai.web.face.vo.FaceTrackFlowPageListVo;
import com.yihecode.camera.ai.web.face.vo.FaceTrackFlowPathListVo;

import java.util.List;

/**
* Face Search and Alarm Relate table
*
* @author zhou
* @since 2025.6.30
*/
public interface FaceTrackFlowService extends IService<FaceTrackFlow> {

    /**
* By Search ID Delete
* @param searchId
*/
    void removeDataBySearch(Long searchId);

    /**
* Page Query
* @param listVo
* @return
*/
    IPage<FaceTrackFlow> listPage(FaceTrackFlowPageListVo listVo);

    /**
* List Query
* @param listVo
* @return
*/
    List<FaceTrackFlow> listData(FaceTrackFlowPathListVo listVo);

    /**
* Query up One set or down One
* @param searchId
* @param reportId
* @param type
* @return
*/
    FaceTrackFlow getNearly(Long searchId, Long reportId, Integer type);
}
