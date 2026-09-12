package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.MediaServer;
import com.yihecode.camera.ai.exception.BizException;

/**
* Stream Media Node Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface MediaServerService extends IService<MediaServer> {

    /**
* By IP and Port Query
* @param ip
* @param httpPort
* @return
*/
    MediaServer getByIpAndPort(String ip, Integer httpPort);

    /**
* Init Data
*/
    void initData();

    /**
* Save Data
* @param mediaServer
*/
    void saveData(MediaServer mediaServer) throws BizException;

    /**
* By Name Query
* @param name
* @return
*/
    MediaServer getByName(String name);

    /**
* Delete Data
* @param id
*/
    void removeData(Long id) throws BizException;
}