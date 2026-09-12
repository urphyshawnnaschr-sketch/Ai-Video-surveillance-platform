package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.face.FaceImage;

import java.util.List;

/**
* Face Group - Face Recognition System
*
* @Author 465769438@qq.com
*/
public interface FaceImageService extends IService<FaceImage> {

    /**
* By Person member ID Query
* @param userId
* @return
*/
    List<FaceImage> listByUser(Long userId);

    /**
* By Person member ID Delete
* @param userId
*/
    void removeByUser(Long userId);

    /**
* Get Avatar
* @param userId
*/
    FaceImage getAvatar(Long userId);

}
