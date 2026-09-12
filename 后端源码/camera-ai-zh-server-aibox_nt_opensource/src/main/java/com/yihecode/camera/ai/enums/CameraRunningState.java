package com.yihecode.camera.ai.enums;

import lombok.Data;

/**
* Camera Run Status Enum Type
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public enum CameraRunningState {

    CLOSED(0, "Close"),
    RUNNING(1, "Run");

    private Integer type;
    private String text;

    CameraRunningState(Integer type, String text) {
        this.type = type;
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static String getText(Integer type) {
        CameraRunningState[] runningStates = values();
        for (CameraRunningState runningState : runningStates) {
            if (runningState.getType() == type) {
                return runningState.getText();
            }
        }
        return "Unknow";
    }

}
