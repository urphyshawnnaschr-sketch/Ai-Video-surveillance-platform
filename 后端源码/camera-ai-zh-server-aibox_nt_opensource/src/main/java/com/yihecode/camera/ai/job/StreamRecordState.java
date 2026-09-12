package com.yihecode.camera.ai.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* Stream Record make Status Record
*
* @author 465769438@qq.com
* @since 2025/3/7
*/
public class StreamRecordState {

    //Default Status
public static final Integer DEFAULT = 0;

// not Record make
public static final Integer UN_RECORD = 1;

// In Progress Record make
public static final Integer DO_RECORD = 2;

// Storage Box up sub Close Record make Status
private final Map<Long, Integer> closeState = new ConcurrentHashMap<>();

// Storage Box up sub Enable Record make Time, 10 min inner not Execute Phase same Operation
private final Map<Long, Long> recordState = new ConcurrentHashMap<>();

private static final StreamRecordState INST = new StreamRecordState();

private StreamRecordState() {}

public static StreamRecordState getInst() {
return INST;
}

// Set Close Recording Status
public void setCloseState(Long boxId, Integer state) {
closeState.put(boxId, state);
}

// Get Close Recording Status, Call One sub just Line
public boolean getCloseState(Long boxId) {
Integer state = closeState.get(boxId);
if(state == null) {
return true;
}
return state == 0;
}

// Set Enable Recording Time
public void setRecordState(Long boxId, Long time) {
recordState.put(boxId, time);
}

// Get Enable Recording Time, true- Enable Recording,false- not Operation
public boolean getRecordState(Long boxId) {
Long time = recordState.get(boxId);
if(time == null) {
return true;
}

// Distance up sub Operation super over 10 min, Again Notification Recording
return (System.currentTimeMillis() - time) >= 5 * 60 * 1000;
}
}
