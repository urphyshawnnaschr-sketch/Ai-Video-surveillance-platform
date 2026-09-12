package com.yihecode.camera.ai.web.api;

import cn.hutool.crypto.SecureUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* Alert Lose Abandon Management, for same One Alert re reply Push in Line Control Filter
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public class ReportDiscard {

    /**
* private inst
*/
    private static final ReportDiscard INST = new ReportDiscard();

    /**
* key: camera_id#algorithm_id
* value: params MD5 Value
*/
    private final ConcurrentHashMap<String, String> md5Map = new ConcurrentHashMap();

    /**
* key: camera_id#algorithm_id
* value: most after Record Timestamp, when super over 1 h, rule Delete the Value
*/
    private final ConcurrentHashMap<String, Long> timeMap = new ConcurrentHashMap<>();

    /**
* key: camera_id#algorithm_id
* value: sub Number Cumulative
*/
    private final ConcurrentHashMap<String, Integer> countMap = new ConcurrentHashMap<>();

    /**
* private
*/
    private ReportDiscard() {}

    /**
* get inst
* @return
*/
    public static ReportDiscard getInst() {
        return INST;
    }

    /**
* Whether Filter the Alert Info
* @param cameraId
* @param algorithmId
* @param params
* @return
*/
    public boolean isFilter(Long cameraId, Long algorithmId, String params) {
        String key = cameraId + "#" + algorithmId;
        String md5 = SecureUtil.md5(params);
        if(!md5Map.containsKey(key)) { //still not has Record over, not Filter
md5Map.put(key, md5);
timeMap.put(key, System.currentTimeMillis());
countMap.put(key, 1);
return false;
}

String oldMd5 = md5Map.get(key);
if(md5.equals(oldMd5)) {// via Record over same One Value
Integer count = countMap.get(key);
if(count == null) {
count = 1;
}

//
if(count > 2) {// super over 2 sub, Filter Drop
return true;
}
countMap.put(key, count + 1);
timeMap.put(key, System.currentTimeMillis());
return false;
} else {
md5Map.put(key, md5);
timeMap.put(key, System.currentTimeMillis());
countMap.put(key, 1);
return false;
}
}

/**
* Delete super over 1 h Record
*/
public void remove() {
long current = System.currentTimeMillis();
for(Iterator<Map.Entry<String, Long>> it = timeMap.entrySet().iterator(); it.hasNext();) {
Map.Entry<String, Long> item = it.next();
String key = item.getKey();
Long errorTime = item.getValue(); // Record Error Time Point

if((current - errorTime) > 60 * 60 * 1000) {// super over 5 min, Clear Divide
it.remove();

//
md5Map.remove(key);
countMap.remove(key);
}
}
}
}
