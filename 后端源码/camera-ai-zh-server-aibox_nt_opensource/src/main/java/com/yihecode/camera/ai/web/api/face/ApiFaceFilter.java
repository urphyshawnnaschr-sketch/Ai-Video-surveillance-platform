package com.yihecode.camera.ai.web.api.face;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
* Face Alarm Filter
*/
public class ApiFaceFilter {

    //Hour long Threshold, Two Alarm Interval Time, same user_id, same Similarity, same Camera
private int threshold = 30;

// key=camera_id + user_id + similiarity, value=time_mills
private final Map<String, Long> cacheMap = new ConcurrentHashMap<>();

//
private final Lock lock = new ReentrantLock();

private static final ApiFaceFilter INST = new ApiFaceFilter();

private ApiFaceFilter() {}

public static ApiFaceFilter getInst() {
return INST;
}

/**
* Add in Cache, and Back Whether can with Save Alarm
* @param cameraId
* @param userId
* @param similiarity
* @return
*/
public synchronized boolean putAndReport(Long cameraId, Long userId, Float similiarity) {
lock.lock();
try {
String key = String.format("%s_%s", cameraId, userId);
if (!cacheMap.containsKey(key)) {
cacheMap.put(key, System.currentTimeMillis() + threshold * 1000L);
return true;
}
long time = cacheMap.get(key);
if (System.currentTimeMillis() < time) {
return false;
}
cacheMap.replace(key, System.currentTimeMillis() + threshold * 1000L);
return true;
} finally {
lock.unlock();
}
}

/**
* Set Threshold
* @param threshold
*/
public void setThreshold(int threshold) {
lock.lock();
try {
this.threshold = threshold;
cacheMap.clear();
} finally {
lock.unlock();
}
}
}
