package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.LoginLog;

import java.util.List;

/**
* Login Log
*/
public interface LoginLogService extends IService<LoginLog> {

    /**
* By IP Address Query Login History
* @param ipAddr
* @param startTimeMills
* @param endTimeMills
* @return
*/
    List<LoginLog> listData(String ipAddr, long startTimeMills, long endTimeMills);

    /**
* Save Data
* @param loginIp
* @param account
* @param accountId
* @param loginError
* @param loginState
*/
    void saveData(String loginIp, String account, Long accountId, String loginError, Integer loginState);
}
