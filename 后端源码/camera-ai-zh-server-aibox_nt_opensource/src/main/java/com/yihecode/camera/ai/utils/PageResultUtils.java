package com.yihecode.camera.ai.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* Pagination JSON Result Set Package Install
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public class PageResultUtils {

    public static <T> PageResult<T> success(Long total, T data) {
        PageResult<T> result = new PageResult<>();
        result.setCode(0);
        result.setMsg("OK");
        result.setCount(total);
        result.setData(data);
        return result;
    }

    /**
* Failed
* @return
*/
    public static <T> PageResult<T> fail() {
        PageResult<T> result = new PageResult<>();
        result.setCode(500);
        result.setMsg("System Busy, Please Retry Later");
        result.setCount(0L);
        return result;
    }

    /**
* Failed
* @return
*/
    public static <T> PageResult<T> fail(String msg) {
        PageResult<T> result = new PageResult<>();
        result.setCode(500);
        result.setCount(0L);
        result.setMsg(msg);
        return result;
    }

    /**
* Failed
* @return
*/
    public static <T> PageResult<T> fail(Integer code, String msg) {
        PageResult<T> result = new PageResult<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setCount(0L);
        return result;
    }
}
