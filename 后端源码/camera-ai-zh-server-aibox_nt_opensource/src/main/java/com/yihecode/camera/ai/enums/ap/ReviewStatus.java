package com.yihecode.camera.ai.enums.ap;

public enum ReviewStatus {
    WAIT(1, "Pending Quality Check"),
    ING(2, "Quality Check in"),
    PASSED(3, "Pass"),
    REJECTED(4, "Rejected");

    private Integer type;

    private String text;

    ReviewStatus(Integer type, String text) {
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
