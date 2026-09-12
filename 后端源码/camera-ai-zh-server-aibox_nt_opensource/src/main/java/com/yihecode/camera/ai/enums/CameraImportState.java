package com.yihecode.camera.ai.enums;

/**
* Camera Batch Import Enum Type Status
* @author Abyss
* @date 2024/3/6 12:06
*/
public enum CameraImportState {
    UNCHECK(0, "not Validate"),
    CHECKING(1, "Validate in"),
    CHECKFAIL(2, "Validation failed"),
    CHECKSUCCESS(3, "Validation success"),
    UNIMPORT(0, "not Import"),
    IMPORTING(1, "Import in"),
    IMPORTFAIL(2, "Import failed"),
    IMPORTSUCCESS(3, "Import success");

    private Integer type;
    private String text;

    CameraImportState(Integer type, String text) {
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

    public static String getText(Integer type) {
        CameraImportState[] accountStates = values();
        for (CameraImportState accountState : accountStates) {
            if (accountState.getType() == type) {
                return accountState.getText();
            }
        }
        return "Unknow";
    }

}
