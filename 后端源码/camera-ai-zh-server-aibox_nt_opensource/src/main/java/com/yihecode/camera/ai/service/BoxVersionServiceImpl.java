package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.BoxVersion;
import com.yihecode.camera.ai.mapper.BoxVersionMapper;
import org.springframework.stereotype.Service;

/**
* Box Device Version Record table
*
* @author 465769438@qq.com
* @since 2025/4/19 15:51
*/
@Service
public class BoxVersionServiceImpl extends ServiceImpl<BoxVersionMapper, BoxVersion> implements BoxVersionService {

    /**
* By Box ID and Algorithm ID Query
*
* @param boxId
* @param algorithmId
* @return
*/
    @Override
    public BoxVersion getData(Long boxId, Long algorithmId) {
        LambdaQueryWrapper<BoxVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BoxVersion::getBoxId, boxId);
        queryWrapper.eq(BoxVersion::getAlgorithmId, algorithmId);
        return this.getOne(queryWrapper, false);
    }

    /**
* By Algorithm ID Delete
*
* @param algorithmId
*/
    @Override
    public void deleteByAlgo(Long algorithmId) {
        LambdaQueryWrapper<BoxVersion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BoxVersion::getAlgorithmId, algorithmId);
        this.remove(queryWrapper);
    }
}