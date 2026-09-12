package com.yihecode.camera.ai.utils;

/**
* JSON Result Set Package Install
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public class JsonResultUtils {

    /**
* Success
* @return
*/
    public static <T> JsonResult<T> success() {
        JsonResult<T> result = new JsonResult<>();
        result.setCode(0);
        result.setMsg("OK");
        result.setData(null);
        return result;
    }

    /**
* Success
* @return
*/
    public static <T> JsonResult<T> success(T data) {
        JsonResult<T> result = new JsonResult<>();
        result.setCode(0);
        result.setMsg("OK");
        result.setData(data);
        return result;
    }

    /**
* Success
* @return
*/
    public static <T> JsonResult<T> successMsg(String msg) {
        JsonResult<T> result = new JsonResult<>();
        result.setCode(0);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /**
* Failed
* @return
*/
    public static <T> JsonResult<T> fail() {
        JsonResult<T> result = new JsonResult<>();
        result.setCode(500);
        result.setMsg("System Busy, Please Retry Later");
        result.setData(null);
        return result;
    }

    /**
* Failed
* @return
*/
    public static <T> JsonResult<T> fail(String msg) {
        JsonResult<T> result = new JsonResult<>();
        result.setCode(500);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /**
* Failed
* @return
*/
    public static <T> JsonResult<T> fail(Integer code, String msg) {
        JsonResult<T> result = new JsonResult<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /**
* Failed
* @return
*/
    public static <T> JsonResult<T> fail(String msg, T data) {
        JsonResult<T> result = new JsonResult<>();
        result.setCode(500);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
