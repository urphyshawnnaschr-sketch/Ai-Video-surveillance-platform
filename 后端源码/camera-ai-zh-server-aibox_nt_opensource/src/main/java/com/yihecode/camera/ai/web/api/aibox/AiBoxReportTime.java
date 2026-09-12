package com.yihecode.camera.ai.web.api.aibox;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* Camera most after Alert Time Record
*
* @author zhoumingxing
* @mailto 465769438@qq.com
*/
public class AiBoxReportTime {

    //form Example
private static AiBoxReportTime INST = new AiBoxReportTime();

// Storage (Camera ID- Algorithm ID) and most after Alert Time
private Map<String, Long> timeMap = new ConcurrentHashMap<>();

// form Example
private AiBoxReportTime() {}

// Get Instance
public static AiBoxReportTime getInst() {
return INST;
}

// Storage most after Alert Time
public void put(String cameraId) {
timeMap.put(cameraId, System.currentTimeMillis());
}

// Storage most after Alert Time
public void put(Long cameraId, Long algorithmId) {
timeMap.put(cameraId +"-"+ algorithmId, System.currentTimeMillis());
}

// Get most after Alert Time
public Long get(String cameraId) {
Long time = timeMap.get(cameraId);
return time == null? -1L: time;
}
}
