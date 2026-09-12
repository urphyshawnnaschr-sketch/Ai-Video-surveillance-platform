package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.face.FaceTrackFlow;
import com.yihecode.camera.ai.mapper.face.FaceTrackFlowMapper;
import com.yihecode.camera.ai.web.face.vo.FaceTrackFlowPageListVo;
import com.yihecode.camera.ai.web.face.vo.FaceTrackFlowPathListVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* Face Search and Alarm Relate table
*
* @author zhou
* @since 2025.6.30
*/
@Service
public class FaceTrackFlowServiceImpl extends ServiceImpl<FaceTrackFlowMapper, FaceTrackFlow> implements FaceTrackFlowService {


    /**
* By Search ID Delete
*
* @param searchId
*/
    @Override
    public void removeDataBySearch(Long searchId) {
        LambdaQueryWrapper<FaceTrackFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceTrackFlow::getSearchId, searchId);
        this.remove(queryWrapper);
    }

    /**
* Page Query
*
* @param listVo
* @return
*/
    @Override
    public IPage<FaceTrackFlow> listPage(FaceTrackFlowPageListVo listVo) {
        IPage<FaceTrackFlow> iPage = new Page<>(listVo.getPage(), listVo.getLimit());
        LambdaQueryWrapper<FaceTrackFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceTrackFlow::getSearchId, listVo.getSearchId());
        if(listVo.getCameraId() != null) {
            queryWrapper.eq(FaceTrackFlow::getCameraId, listVo.getCameraId());
        }
        if(listVo.getStartDate() != null) {
            queryWrapper.gt(FaceTrackFlow::getReportAt, listVo.getStartDate());
        }
        if(listVo.getEndDate() != null) {
            queryWrapper.lt(FaceTrackFlow::getReportAt, listVo.getEndDate());
        }
        queryWrapper.orderByAsc(FaceTrackFlow::getReportAt);
        return this.page(iPage, queryWrapper);
    }

    /**
* List Query
*
* @param queryVo
* @return
*/
    @Override
    public List<FaceTrackFlow> listData(FaceTrackFlowPathListVo queryVo) {
        LambdaQueryWrapper<FaceTrackFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceTrackFlow::getSearchId, queryVo.getSearchId());
        if(queryVo.getCameraId() != null) {
            queryWrapper.eq(FaceTrackFlow::getCameraId, queryVo.getCameraId());
        }
        if(queryVo.getStartDate() != null) {
            queryWrapper.gt(FaceTrackFlow::getReportAt, queryVo.getStartDate());
        }
        if(queryVo.getEndDate() != null) {
            queryWrapper.lt(FaceTrackFlow::getReportAt, queryVo.getEndDate());
        }
        queryWrapper.orderByAsc(FaceTrackFlow::getReportAt);
        return this.list(queryWrapper);
    }

    /**
* Query up One set or down One
*
* @param searchId
* @param reportId
* @param type
* @return
*/
    @Override
    public FaceTrackFlow getNearly(Long searchId, Long reportId, Integer type) {
        LambdaQueryWrapper<FaceTrackFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceTrackFlow::getSearchId, searchId);
        if(type == 0) {
            queryWrapper.lt(FaceTrackFlow::getReportId, reportId);
            queryWrapper.orderByDesc(FaceTrackFlow::getReportAt);
        }

        if(type == 1) {
            queryWrapper.gt(FaceTrackFlow::getReportId, reportId);
            queryWrapper.orderByAsc(FaceTrackFlow::getReportAt);
        }

        queryWrapper.last("limit 0, 1");
        return this.getOne(queryWrapper);
    }
}
