package com.yihecode.camera.ai.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Country International Change Language Enum
*
* @author wanghaoyu
*/
public enum LanguageEnum {

    CHINESE("zh-CN", "in Text"),
    ENGLISH("en-US", "English Language"),
    ;

    //
private String code;
private String text;

LanguageEnum(String code, String text) {
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
LanguageEnum[] alarmShowTypes = values();
for (LanguageEnum alarmShowType: alarmShowTypes) {
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
LanguageEnum[] values = values();
for (LanguageEnum alarmShowType: values) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("code", alarmShowType.code);
dataMap.put("text", alarmShowType.text);
dataList.add(dataMap);
}
return dataList;
}
}