package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.face.FaceTrackSearch;
import com.yihecode.camera.ai.web.face.vo.FaceSearchPageVo;

/**
* Face Search Record table
*
* @author zhou
* @since 2025.6.30
*/
public interface FaceTrackSearchService extends IService<FaceTrackSearch> {

    /**
* Page Query
* @param pageVo
* @return
*/
    IPage<FaceTrackSearch> listPage(FaceSearchPageVo pageVo);

    /**
* Delete Search Record
* @param id
*/
    void removeData(Long id);
}
