package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.BoxVersion;

/**
* Box Device Version Record table
*
* @author 465769438@qq.com
* @since 2025/4/19 15:51
*/
public interface BoxVersionService extends IService<BoxVersion> {

    /**
* By Box ID and Algorithm ID Query
* @param boxId
* @param algorithmId
* @return
*/
    BoxVersion getData(Long boxId, Long algorithmId);

    /**
* By Algorithm ID Delete
* @param algorithmId
*/
    void deleteByAlgo(Long algorithmId);
}
