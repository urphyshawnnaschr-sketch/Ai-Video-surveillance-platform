package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.face.FaceSyncBox;
import com.yihecode.camera.ai.mapper.face.FaceSyncBoxMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* Face and Box Sync Object table
* @author zhou
* @since 2025-07-110
*/
@Service
public class FaceSyncBoxServiceImpl extends ServiceImpl<FaceSyncBoxMapper, FaceSyncBox> implements FaceSyncBoxService {

    /**
* By Box ID Query Sync Record Count
*
* @param boxId
* @return
*/
    @Override
    public int countByBox(Long boxId) {
        LambdaQueryWrapper<FaceSyncBox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceSyncBox::getBoxId, boxId);
        return this.count(queryWrapper);
    }

    /**
* Query Pending Sync Record
*
* @param boxId
* @return
*/
    @Override
    public List<FaceSyncBox> listSyncByBox(Long boxId) {
        LambdaQueryWrapper<FaceSyncBox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceSyncBox::getBoxId, boxId);
        queryWrapper.eq(FaceSyncBox::getSyncStatus, 0);
        queryWrapper.lt(FaceSyncBox::getSyncNum, 3);
        queryWrapper.orderByAsc(FaceSyncBox::getCreatedAt);
        return this.list(queryWrapper);
    }
}
