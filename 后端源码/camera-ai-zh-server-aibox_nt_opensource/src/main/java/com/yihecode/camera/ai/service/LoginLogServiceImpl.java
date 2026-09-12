package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.LoginLog;
import com.yihecode.camera.ai.mapper.LoginLogMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* Login Log
*/
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    /**
* By IP Address Query Login History
*
* @param ipAddr
* @param startTimeMills
* @param endTimeMills
* @return
*/
    @Override
    public List<LoginLog> listData(String ipAddr, long startTimeMills, long endTimeMills) {
        LambdaQueryWrapper<LoginLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginLog::getLoginIp, ipAddr);
        queryWrapper.gt(LoginLog::getLoginMills, startTimeMills);
        queryWrapper.lt(LoginLog::getLoginMills, endTimeMills);
        queryWrapper.orderByDesc(LoginLog::getLoginMills);
        return this.list(queryWrapper);
    }

    /**
* Save Data
*
* @param loginIp
* @param account
* @param loginError
*/
    @Override
    public void saveData(String loginIp, String account, Long accountId, String loginError, Integer loginState) {
        LoginLog loginLog = new LoginLog();
        loginLog.setAccount(account);
        loginLog.setAccountId(accountId == null ? 0L : accountId);
        loginLog.setLoginAt(new Date());
        loginLog.setLoginMills(System.currentTimeMillis());
        loginLog.setLoginIp(loginIp);
        loginLog.setLoginState(loginState);
        loginLog.setLoginError(loginError);
        save(loginLog);
    }
}
