package com.yihecode.camera.ai.config;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN"); //Prevent Point Click Hijack
httpResponse.setHeader("X-XSS-Protection","1; mode=block"); // Enabled XSS Filter and Block Detection to XSS Attack

chain.doFilter(request, response);
}

@Override
public void init(FilterConfig filterConfig) throws ServletException {}

@Override
public void destroy() {}
}