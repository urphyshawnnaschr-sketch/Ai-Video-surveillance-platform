package com.yihecode.camera.ai.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
* JSON Result
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Response")
public class JsonResult<T> implements Serializable {

    /**
* Status Code
*/
    @ApiModelProperty(name = "code", value = "Response Code", example = "200", notes = "200-OK 500- Exception xxx- Other Custom")
    private Integer code;

    /**
* Message
*/
    @ApiModelProperty(name = "msg", value = "Response Message", example = "OK", notes = "")
    private String msg;

    /**
* Data
*/
    @ApiModelProperty(name = "data", value = "Response Data")
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
