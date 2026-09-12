package com.yihecode.camera.ai.service.wvp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.wvp.GBDevice;

/**
* GB Standard Device Info Management
*/
public interface GBDeviceService extends IService<GBDevice> {

    /**
* Page Query
* @param page
* @param limit
* @return
*/
    IPage<GBDevice> listPage(Integer page, Integer limit);
}
