package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.face.FaceImage;
import com.yihecode.camera.ai.mapper.face.FaceImageMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* Person member Image - Face Recognition System
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class FaceImageServiceImpl extends ServiceImpl<FaceImageMapper, FaceImage> implements FaceImageService {

    /**
* By Person member ID Query
* @param userId
* @return
*/
    @Override
    public List<FaceImage> listByUser(Long userId) {
        LambdaQueryWrapper<FaceImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceImage::getUserId, userId);
        List<FaceImage> faceImages = this.list(queryWrapper);
        if(faceImages == null) {
            return new ArrayList<>();
        }
        return faceImages;
    }

    /**
* By Person member ID Delete
*
* @param userId
*/
    @Override
    public void removeByUser(Long userId) {
        LambdaQueryWrapper<FaceImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceImage::getUserId, userId);
        this.remove(queryWrapper);
    }

    /**
* Get Avatar
*
* @param userId
*/
    @Override
    public FaceImage getAvatar(Long userId) {
        LambdaQueryWrapper<FaceImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceImage::getUserId, userId);
        queryWrapper.last("limit 0, 1");
        return this.getOne(queryWrapper);
    }
}