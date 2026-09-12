package com.yihecode.camera.ai.service.wvp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.wvp.GBDevice;
import com.yihecode.camera.ai.mapper.wvp.GBDeviceMapper;
import org.springframework.stereotype.Service;

/**
* GB Standard Device Info Management
*/
@Service
public class GBDeviceServiceImpl extends ServiceImpl<GBDeviceMapper, GBDevice> implements GBDeviceService {

    /**
* Page Query
*
* @param page
* @param limit
* @return
*/
    @Override
    public IPage<GBDevice> listPage(Integer page, Integer limit) {
        IPage<GBDevice> pageInfo = new Page<>(page, limit);
        return this.page(pageInfo);
    }
}
