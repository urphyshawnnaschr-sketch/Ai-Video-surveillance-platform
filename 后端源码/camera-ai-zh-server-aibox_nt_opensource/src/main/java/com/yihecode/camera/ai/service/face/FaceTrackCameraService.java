package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.face.FaceTrackCamera;

import java.util.List;

/**
* Person member Tracking Camera Config
*
* @author zhou
* @since 2025.6.20
*/
public interface FaceTrackCameraService extends IService<FaceTrackCamera> {

    /**
* By Config ID Delete Record
* @param configId
*/
    void deleteByConfig(Long configId);

    /**
* By Config ID Query Record
* @param configId
* @return
*/
    List<FaceTrackCamera> listByConfig(Long configId);
}
