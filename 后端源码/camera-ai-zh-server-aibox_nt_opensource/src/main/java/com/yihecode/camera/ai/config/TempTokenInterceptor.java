package com.yihecode.camera.ai.config;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
* Fixed Fixed token Need Request
*/
public class TempTokenInterceptor implements HandlerInterceptor {

    //Temp Hour token
private List<String> TEMP_TOKENS = Arrays.asList("QD_q8wY93JyGHXPZl");

@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
String headerToken = request.getHeader("X-Token");
String paramToken = request.getParameter("token");
if((StrUtil.isNotBlank(headerToken) || StrUtil.isNotBlank(paramToken)) && (TEMP_TOKENS.contains(headerToken) || TEMP_TOKENS.contains(paramToken))) {
boolean isLogin = StpUtil.isLogin();
if (!isLogin) {
StpUtil.login(8349L);
}
}
return true;
}
}