package com.yihecode.camera.ai.service.wvp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.wvp.GBDeviceChannel;

/**
* GB Standard Device Channel Info Management
*/
public interface GBDeviceChannelService extends IService<GBDeviceChannel> {

    /**
* Page Query
* @param page
* @param limit
* @param deviceId
* @param cameraStatus
* @param boxStatus
* @return
*/
    IPage<GBDeviceChannel> listPage(Integer page, Integer limit, String deviceId, Integer cameraStatus, Integer boxStatus);

    /**
* Update Device Channel Relate Camera
* @param channelId
* @param cameraId
*/
    void updateAccessCameraId(Long channelId, Long cameraId);

    /**
* Query Device Channel Count
* @param deviceId
* @return
*/
    int getCountByDeviceId(String deviceId);

    /**
* Delete Bind Camera ID
* @param accessCameraId
*/
    void deleteCameraId(Long accessCameraId);

    /**
* By Access Camera Query Channel Info
* @param cameraId
*/
    GBDeviceChannel getByAccessCameraId(Long cameraId);

    /**
* By Device ID and Channel ID Query
* @param deviceId
* @param channelId
* @return
*/
    GBDeviceChannel getByDeviceIdAndChannelId(String deviceId, String channelId);

    /**
* By Status Count
* @param status
* @return
*/
    Integer countByStatus(int status);

    /**
* By Box Count Channel Count
* @param locationId
* @return
*/
    int countByLocation(Long locationId);
}
