package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.ApFileDO;
import com.yihecode.camera.ai.exception.BizException;

/**
* @Author lichangliang
* @Date 2023/7/21 21:56
* @Describe
* @Version 1.0
*/
public interface ApFileService extends IService<ApFileDO> {
    Long saveFile(String adress, Integer type, Long projectId, Long userId) throws BizException;
}
