package com.yihecode.camera.ai.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Alert Type show show Enum Type
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public enum AlarmShowType {

    //
// H5("h5","H5 Push"),
WE_WORK("we_work","WeWork Group Push"),
DINGDING("dingding","DingTalk Group Push"),
SMS("sms","SMS Push"),
VOICE("voice","Voice Push");

//
private String code;
private String text;

AlarmShowType(String code, String text) {
this.code = code;
this.text = text;
}

public String getCode() {
return code;
}

public void setCode(String code) {
this.code = code;
}

public String getText() {
return text;
}

public void setText(String text) {
this.text = text;
}

/**
* get text
*
* @param code
* @return
*/
public static String getText(String code) {
AlarmShowType[] alarmShowTypes = values();
for (AlarmShowType alarmShowType: alarmShowTypes) {
if (alarmShowType.getCode().equals(code)) {
return alarmShowType.getText();
}
}
return"Unknown";
}

/**
* to list
*
* @return
*/
public static List<Map<String, Object>> toList() {
List<Map<String, Object>> dataList = new ArrayList<>();
AlarmShowType[] values = values();
for (AlarmShowType alarmShowType: values) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("code", alarmShowType.code);
dataMap.put("text", alarmShowType.text);
dataList.add(dataMap);
}
return dataList;
}
}