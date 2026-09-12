package com.yihecode.camera.ai.comm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.yihecode.camera.ai.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
* Common Control
*
* @author 465769438@qq.com
* @since 2025/3/7
*/
@Slf4j
@Component
public class CommService {

    @Autowired
    private ConfigService configService;

    /**
* Whether Enable Record make
*
* @return
*/
    public boolean isRecordEnable() {
        boolean isRecord = getIsRecord();
        boolean isRecordTime = getIsRecordTime();
        boolean isIsRecordDay = getIsRecordDay();
        return isRecord && isRecordTime && isIsRecordDay;
    }

    /**
* Recording open close Whether Open
*
* @return
*/
    private boolean getIsRecord() {
        String isRecord = configService.getByValTag("isRecord");
        return "true".equalsIgnoreCase(isRecord);
    }

    /**
* Whether In Recording Hour Segment
*
* @return
*/
    private boolean getIsRecordTime() {
        String recordTimes = configService.getByValTag("recordTimes");
        if (StrUtil.isBlank(recordTimes)) {
            return false;
        }
        try {
            String[] times = recordTimes.split(",");
            int s1 = Integer.parseInt(times[0]);
            int s2 = Integer.parseInt(times[1]);
            int curr = Integer.parseInt(DateUtil.format(new Date(), "HHmmss"));
            return (s1 <= curr && curr <= s2);
        } catch (Exception e) {
            log.error("Parse Recording Hour Segment Error: {}", recordTimes);
        }
        return false;
    }

    /**
* Whether In Recording Date
*
* @return
*/
    private boolean getIsRecordDay() {
        String recordDates = configService.getByValTag("recordDates");
        if (StrUtil.isBlank(recordDates)) {
            return false;
        }

        //10,20,30.40,50,60,70, Star Period One to Star Period Day
int week = DateUtil.dayOfWeekEnum(new Date()).getValue() - 1;
String weekStr ="70";
if (week > 0) {
weekStr = week +"0";
}
return recordDates.contains(weekStr);
}

/**
* Local key Value
*
* @return
*/
public boolean checkKey(String key) {
String aiboxKey = configService.getByValTag("aiboxKey");
String localKey = SecureUtil.md5(StrUtil.isBlank(aiboxKey)?"-1024-": aiboxKey);
return localKey.equalsIgnoreCase(key);
}

}
