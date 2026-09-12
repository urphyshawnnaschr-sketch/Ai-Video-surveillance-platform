package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.Config;

/**
* System Config Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface ConfigService extends IService<Config> {

    /**
*
* @param tag
* @return
*/
    String getByValTag(String tag);

    /**
* Clear Divide Cache
* @param tag
*/
    void evictByTag(String tag);

    /**
* Init Data
*/
    void initData(String hostIp , String hostIpFile);

    /**
* By tag Query
* @param tag
* @return
*/
    Config getByTag(String tag);

    /**
* Save Data
* @param name
* @param tag
* @param val
*/
    void saveData(String name, String tag, String val);

    /**
* Get outer net IP
* @return
*/
    String getOutIp();
}