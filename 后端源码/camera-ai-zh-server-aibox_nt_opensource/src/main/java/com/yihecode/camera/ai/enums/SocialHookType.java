package com.yihecode.camera.ai.enums;

import java.util.*;

/**
* Alert Type Enum Type
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public enum SocialHookType {

    REPORT(0, "Monitor Alert"),
    BOX(1, "Box"),
    CAMERA(2, "Camera"),
    ;

    private Integer type;
    private String text;

    SocialHookType(Integer type, String text) {
        this.type = type;
        this.text = text;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
* get text
* @param type
* @return
*/
    public static String getText(Integer type) {
        SocialHookType[] reportTypes = values();
        for (SocialHookType reportType : reportTypes) {
            if (Objects.equals(reportType.getType(), type)) {
                return reportType.getText();
            }
        }
        return "Unknow";
    }

    /**
* to list
* @return
*/
    public static List<Map<String, Object>> toList() {
        List<Map<String, Object>> dataList = new ArrayList<>();
        SocialHookType[] values = values();
        for (SocialHookType reportType : values) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", reportType.type);
            dataMap.put("name", reportType.text);
            dataList.add(dataMap);
        }
        return dataList;
    }
}