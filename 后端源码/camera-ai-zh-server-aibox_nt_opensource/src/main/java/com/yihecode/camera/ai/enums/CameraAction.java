package com.yihecode.camera.ai.enums;

/**
* Camera Action Enum Type, Integrate Algorithm for Camera Call in Line Start / Stop / Delete etc
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public enum CameraAction {
    ACTION_NULL(0, "not Operation"),
    ACTION_UPD(1, "Update Camera"),
    ACTION_DEL(2, "Delete Camera");

    private Integer type;

    private String text;

    CameraAction(Integer type, String text) {
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
}
