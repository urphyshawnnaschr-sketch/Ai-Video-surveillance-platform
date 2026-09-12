package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.face.FaceTrackCamera;
import com.yihecode.camera.ai.mapper.face.FaceTrackCameraMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* Person member Tracking Camera Config
*
* @author zhou
* @since 2025.6.20
*/
@Service
public class FaceTrackCameraServiceImpl extends ServiceImpl<FaceTrackCameraMapper, FaceTrackCamera> implements FaceTrackCameraService {

    /**
* By Config ID Delete Record
*
* @param configId
*/
    @Override
    public void deleteByConfig(Long configId) {
        LambdaQueryWrapper<FaceTrackCamera> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceTrackCamera::getConfigId, configId);
        this.remove(queryWrapper);
    }

    /**
* By Config ID Query Record
*
* @param configId
* @return
*/
    @Override
    public List<FaceTrackCamera> listByConfig(Long configId) {
        LambdaQueryWrapper<FaceTrackCamera> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceTrackCamera::getConfigId, configId);
        return this.list(queryWrapper);
    }
}
