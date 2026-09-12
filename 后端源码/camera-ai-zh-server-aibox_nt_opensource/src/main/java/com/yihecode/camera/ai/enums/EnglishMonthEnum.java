package com.yihecode.camera.ai.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* English Month copy
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public enum EnglishMonthEnum {

    JAN(1, "Jan."),
    FEB(2, "Feb."),
    MAR(3, "Mar."),
    APR(4, "Apr."),
    MAY(5, "May"),
    JUN(6, "Jun."),
    JUL(7, "Jul."),
    AUG(8, "Aug."),
    SEP(9, "Sep."),
    OCT(10, "Oct."),
    NOV(11, "Nov."),
    DEC(12, "Dec."),

    ;

    //
private Integer code;
private String text;

EnglishMonthEnum(Integer code, String text) {
this.code = code;
this.text = text;
}

public Integer getCode() {
return code;
}

public void setCode(Integer code) {
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
public static String getText(Integer code) {
EnglishMonthEnum[] alarmShowTypes = values();
for (EnglishMonthEnum alarmShowType: alarmShowTypes) {
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
EnglishMonthEnum[] values = values();
for (EnglishMonthEnum alarmShowType: values) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("code", alarmShowType.code);
dataMap.put("text", alarmShowType.text);
dataList.add(dataMap);
}
return dataList;
}
}