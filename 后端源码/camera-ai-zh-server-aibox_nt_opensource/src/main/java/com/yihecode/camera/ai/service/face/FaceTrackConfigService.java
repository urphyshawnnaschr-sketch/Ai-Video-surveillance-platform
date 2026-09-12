package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.face.FaceTrackConfig;
import com.yihecode.camera.ai.web.face.vo.FaceTrackConfigModifyVo;

/**
* Person member Tracking Base image Config
*
* @author zhou
* @since 2025.6.20
*/
public interface FaceTrackConfigService extends IService<FaceTrackConfig> {

    /**
* Add / Modify Data
* @param faceTrackConfig
*/
    void saveData(FaceTrackConfig faceTrackConfig);

    /**
* By ID Delete Data
* @param id
*/
    void deleteData(Long id);

    /**
* Query Default Template
* @return
*/
    FaceTrackConfig getPrimary();
}
