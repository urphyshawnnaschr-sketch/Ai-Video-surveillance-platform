package com.yihecode.camera.ai.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.SmsPhone;
import com.yihecode.camera.ai.mapper.SmsPhoneMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* SMS Push Phone code
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class SmsPhoneServiceImpl extends ServiceImpl<SmsPhoneMapper, SmsPhone> implements SmsPhoneService {

    /**
* Query Phone code, Use Comma No Split
*
* @return
*/
    @Override
    @Cacheable(cacheNames = "phones", key = "#test")
    public String listPhoneStr(String test) {
        List<SmsPhone> smsPhoneList = this.list();
        if(smsPhoneList == null) {
            return "";
        }

        //
List<String> phones = new ArrayList<>();
for(SmsPhone smsPhone: smsPhoneList) {
phones.add(smsPhone.getPhone());
}
return String.join(",", phones);
}

/**
* Clear Divide Cache
*/
@Override
@CacheEvict(cacheNames ="phones", key ="#test")
public void evictPhoneStr(String test) {
listPhoneStr("test");
}

@CachePut(value ="registerSmsPhone", key ="#phone")
public String saveRegisterSmsCache(String phone, Integer num) {
System.out.println("Add in Cache -"+ phone +"-"+ num);
return num.toString();
}
public String getRegisterSmsCache(String phone) {
CacheManager manager = SpringUtil.getBean(CacheManager.class);
Cache cache = manager.getCache("registerSmsPhone");
assert cache!= null;
String code = cache.get(phone, String.class);
if (null == code) {
return"";
}
return code;
}
@CacheEvict(value ="registerSmsPhone", key ="#phone")
public void evictRegisterSmsCache(String phone) {
System.out.println("Clear Divide Cache -"+ phone);
}

@CachePut(value ="resetPswSmsPhone", key ="#phone")
public String saveResetPswSmsCache(String phone, Integer num) {
System.out.println("Add in Cache -"+ phone +"-"+ num);
return num.toString();
}
public String getResetPswSmsCache(String phone) {
CacheManager manager = SpringUtil.getBean(CacheManager.class);
Cache cache = manager.getCache("resetPswSmsPhone");
assert cache!= null;
String code = cache.get(phone, String.class);
if (null == code) {
return"";
}
return code;
}
@CacheEvict(value ="resetPswSmsPhone", key ="#phone")
public void evictResetPswSmsCache(String phone) {
System.out.println("Clear Divide Cache -"+ phone);
}

@Override
public List<SmsPhone> listByAccountId(Long accountId) {
LambdaQueryWrapper<SmsPhone> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(SmsPhone::getAccountId, accountId);
return this.list(queryWrapper);
}

@Override
public String listByAccountIdStr(Long accountId) {
LambdaQueryWrapper<SmsPhone> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(SmsPhone::getAccountId, accountId);
List<SmsPhone> smsPhoneList = this.list(queryWrapper);
List<String> phones = new ArrayList<>();
for(SmsPhone smsPhone: smsPhoneList) {
phones.add(smsPhone.getPhone());
}
return String.join(",", phones);
}
}