package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.SmsPhone;

import java.util.List;

/**
* SMS Push Phone code
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface SmsPhoneService extends IService<SmsPhone> {

    /**
* Query Phone code, Use Comma No Split
* @return
*/
    String listPhoneStr(String test);

    /**
* Clear Divide Cache
*/
    void evictPhoneStr(String test);

    /**
* Get Register Verify code Cache
* @author Abyss
* @date 2023/11/27 21:05
*/
    String getRegisterSmsCache(String phone);

    /**
* Save Register Verify code Cache
* @author Abyss
* @date 2023/11/27 21:05
*/
    String saveRegisterSmsCache(String phone, Integer num);

    /**
* Get Reset Password Verify code Cache
* @author Abyss
* @date 2023/11/27 21:06
*/
    String getResetPswSmsCache(String phone);

    /**
* Save Reset Password Verify code Cache
* @author Abyss
* @date 2023/11/27 21:07
*/
    String saveResetPswSmsCache(String phone, Integer num);

    List<SmsPhone> listByAccountId(Long accountId);

    String listByAccountIdStr(Long accountId);

}