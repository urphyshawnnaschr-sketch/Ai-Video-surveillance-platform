package com.yihecode.camera.ai.service.wvp;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.wvp.GBDeviceChannel;
import com.yihecode.camera.ai.mapper.wvp.GBDeviceChannelMapper;
import org.springframework.stereotype.Service;

/**
* GB Standard Device Channel Info Management
*/
@Service
public class GBDeviceChannelServiceImpl extends ServiceImpl<GBDeviceChannelMapper, GBDeviceChannel> implements GBDeviceChannelService {

    /**
* Page Query
*
* @param page
* @param limit
* @param deviceId
* @param cameraStatus
* @param boxStatus
* @return
*/
    @Override
    public IPage<GBDeviceChannel> listPage(Integer page, Integer limit, String deviceId, Integer cameraStatus, Integer boxStatus) {
        IPage<GBDeviceChannel> pageInfo = new Page<>(page, limit);
        LambdaQueryWrapper<GBDeviceChannel> queryWrapper = new LambdaQueryWrapper<>();
        if(StrUtil.isNotBlank(deviceId)) {
            queryWrapper.eq(GBDeviceChannel::getDeviceId, deviceId);
        }
        if(cameraStatus != null) {
            if(cameraStatus == 0) {
                queryWrapper.eq(GBDeviceChannel::getCameraId, 0L);
            }
            if(cameraStatus == 1) {
                queryWrapper.gt(GBDeviceChannel::getCameraId, 0L);
            }
        }
        if(boxStatus != null) {
            if(boxStatus == 0) {
                queryWrapper.eq(GBDeviceChannel::getLocationId, 0L);
            }
            if(boxStatus == 1) {
                queryWrapper.gt(GBDeviceChannel::getLocationId, 0L);
            }
        }
        return this.page(pageInfo, queryWrapper);
    }

    /**
* Update Device Channel Relate Camera
*
* @param channelId
* @param cameraId
*/
    @Override
    public void updateAccessCameraId(Long channelId, Long cameraId) {
//LambdaUpdateWrapper<GBDeviceChannel> updateWrapper = new LambdaUpdateWrapper<>();
// updateWrapper.set(GBDeviceChannel::getAccessCameraId, cameraId);
// updateWrapper.eq(GBDeviceChannel::getId, channelId);
// this.update(updateWrapper);
}

/**
* Query Device Channel Count
*
* @param deviceId
* @return
*/
@Override
public int getCountByDeviceId(String deviceId) {
LambdaQueryWrapper<GBDeviceChannel> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(GBDeviceChannel::getDeviceId, deviceId);
return this.count(queryWrapper);
}

/**
* Delete Bind Camera ID
*
* @param accessCameraId
*/
@Override
public void deleteCameraId(Long accessCameraId) {
// LambdaUpdateWrapper<GBDeviceChannel> updateWrapper = new LambdaUpdateWrapper<>();
// updateWrapper.set(GBDeviceChannel::getAccessCameraId, 0L);
// updateWrapper.eq(GBDeviceChannel::getAccessCameraId, accessCameraId);
// this.update(updateWrapper);
}

/**
* By Access Camera Query Channel Info
*
* @param cameraId
*/
@Override
public GBDeviceChannel getByAccessCameraId(Long cameraId) {
LambdaQueryWrapper<GBDeviceChannel> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(GBDeviceChannel::getCameraId, cameraId);
return this.getOne(queryWrapper);
}

/**
* By Device ID and Channel ID Query
*
* @param deviceId
* @param channelId
* @return
*/
@Override
public GBDeviceChannel getByDeviceIdAndChannelId(String deviceId, String channelId) {
LambdaQueryWrapper<GBDeviceChannel> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(GBDeviceChannel::getDeviceId, deviceId);
queryWrapper.eq(GBDeviceChannel::getChannelId, channelId);
return this.getOne(queryWrapper, false);
}

/**
* By Status Count
*
* @param status
* @return
*/
@Override
public Integer countByStatus(int status) {
LambdaQueryWrapper<GBDeviceChannel> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(GBDeviceChannel::getStatus, status);
return this.count(queryWrapper);
}

/**
* By Box Count Channel Count
*
* @param locationId
* @return
*/
@Override
public int countByLocation(Long locationId) {
LambdaQueryWrapper<GBDeviceChannel> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(GBDeviceChannel::getLocationId, locationId);
return this.count(queryWrapper);
}
}
