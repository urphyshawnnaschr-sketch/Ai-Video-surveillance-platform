package com.yihecode.camera.ai.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
* Pagination JSON Result
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel("Pagination Data")
public class PageResult<T> implements Serializable {

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
* Total Count
*/
    @ApiModelProperty(name = "msg", value = "Data Total", example = "123", notes = "")
    private Long count;

    /**
* Data
*/
    @ApiModelProperty(name = "msg", value = "Data List", notes = "")
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
