package com.yihecode.camera.ai.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.yihecode.camera.ai.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Objects;

/**
* all Bureau Exception Process
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
* Exception Process
* @param request
* @param response
* @param e
* @return
*/
    @ExceptionHandler(value = Exception.class)
    public ModelAndView errorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        //Non Login Error, Record Error Log
if(!(e instanceof NotLoginException)) {
log.error("Catch Exception {}", request.getRequestURI(), e);
}
//
int errorCode = 500;
String errorMsg ="";
if(e instanceof NotLoginException) {
errorCode = 403;
errorMsg ="User not Login";
} else if(e instanceof NotPermissionException) {
errorCode = 500;
errorMsg ="You not has Operation Permission";
} else if(e instanceof BindException) {
BindException bindException = (BindException) e;
String[] str = Objects.requireNonNull(bindException.getBindingResult().getAllErrors().get(0).getCodes())[1].split("\\.");
String message = bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage();
String msg ="cannot be empty";
if (msg.equals(message)) {
errorMsg = str[1] +":"+ message;
} else {
errorMsg = str[1];
}
} else if(e instanceof HttpMessageNotReadableException) {
errorMsg ="Request Param Error, or json Parse Error";
} else if(e instanceof UndeclaredThrowableException) {
errorMsg = ((UndeclaredThrowableException) e).getUndeclaredThrowable().getMessage();
} else {
errorMsg = e.getMessage();
}

// Send json Request
ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
mav.addObject("code", errorCode);
mav.addObject("msg", errorMsg);
return mav;
}
}
