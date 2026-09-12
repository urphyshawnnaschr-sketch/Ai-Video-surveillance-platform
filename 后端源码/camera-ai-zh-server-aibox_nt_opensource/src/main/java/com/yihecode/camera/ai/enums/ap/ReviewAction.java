package com.yihecode.camera.ai.enums.ap;

public enum ReviewAction {
    PASS(1, "Pass"),
    REJECT(3, "Rejected"),
    EDIT(7, "Modify");

    private Integer type;

    private String text;

    ReviewAction(Integer type, String text) {
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
