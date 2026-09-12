package com.yihecode.camera.ai.enums.ap;

/**
* @Author lichangliang
* @Date 2023/7/20 20:22
* @Describe
* @Version 1.0
*/
public enum ProjectStatusEnum {
    DATA_LOADING(1,"Data Read"),
    AUTO_ANN(2,"Smart can Annotation in"),
    ANN(11,"Annotation in"),
    FINSH(99,"Deliver")
    ;
    private Integer code;
    private String dec;

    ProjectStatusEnum(Integer code, String dec) {
        this.code = code;
        this.dec = dec;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }
}
