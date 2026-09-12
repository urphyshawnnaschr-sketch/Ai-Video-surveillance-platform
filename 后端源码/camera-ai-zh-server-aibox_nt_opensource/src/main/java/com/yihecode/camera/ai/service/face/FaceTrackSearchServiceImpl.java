package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.face.FaceTrackSearch;
import com.yihecode.camera.ai.mapper.face.FaceTrackSearchMapper;
import com.yihecode.camera.ai.web.face.vo.FaceSearchPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* Face Search Record table
*
* @author zhou
* @since 2025.6.30
*/
@Service
public class FaceTrackSearchServiceImpl extends ServiceImpl<FaceTrackSearchMapper, FaceTrackSearch> implements FaceTrackSearchService {

    @Autowired
    private FaceTrackFlowService faceTrackFlowService;

    /**
* Page Query
*
* @param pageVo
* @return
*/
    @Override
    public IPage<FaceTrackSearch> listPage(FaceSearchPageVo pageVo) {
        IPage<FaceTrackSearch> iPage = new Page<>(pageVo.getPage(), pageVo.getLimit());
        LambdaQueryWrapper<FaceTrackSearch> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(FaceTrackSearch::getCreatedAt);
        return this.page(iPage, queryWrapper);
    }

    /**
* Delete Search Record
*
* @param id
*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeData(Long id) {
        //
this.removeById(id);

//
faceTrackFlowService.removeDataBySearch(id);
}
}
