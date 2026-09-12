package com.yihecode.camera.ai.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
* web Project Config
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    /**
* springboot
* Solve Decide long,bigint turn json Lose Missing Fine Degree
*/
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(new JsonNullBeanSerializerModifier()));

        /**
* order Column change Complete json Hour, will All long change Complete string
* Because for js in Get Number Char Type not can Contain All java long Value
*/
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(jackson2HttpMessageConverter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");

        //Solve Decide swagger no Method Access
registry.addResourceHandler("/swagger-ui/**")
.addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
.resourceChain(false);

// Solve Decide swagger js File no Method Access
registry.addResourceHandler("/webjars/**")
.addResourceLocations("classpath:/META-INF/resources/webjars/")
.resourceChain(false);

super.addResourceHandlers(registry);
}

@Override
protected void addInterceptors(InterceptorRegistry registry) {
super.addInterceptors(registry);
// Fixed Fixed token Interceptor
// registry.addInterceptor(new TempTokenInterceptor()).excludePathPatterns("/swagger-ui/**","/swagger-resources/**").addPathPatterns("/**");
// Login Validation Interceptor
// registry.addInterceptor(new SaInterceptor()).excludePathPatterns("/swagger-ui/**","/swagger-resources/**").addPathPatterns("/**");
registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin())).addPathPatterns("/swagger-ui/**","/swagger-resources/**","/v2/api-docs","/**").excludePathPatterns("/login","/aibox/upload/**","/gitee/oauth/login/**");
}

/**
* Cross Domain Config
* @return
*/
private CorsConfiguration corsConfig() {
CorsConfiguration corsConfiguration = new CorsConfiguration();
// Request Often Use Three kind Config,* instead table Allow All, when Hour You Also can with Custom Property (For example header only can Belt What, only can is post Mode etc etc)
corsConfiguration.addAllowedOrigin("*");
corsConfiguration.addAllowedHeader("*");
corsConfiguration.addAllowedMethod("*");
corsConfiguration.setAllowCredentials(false);
corsConfiguration.setMaxAge(3600L);
return corsConfiguration;
}

/**
* Cross Domain Config
* @return
*/
@Bean
public CorsFilter corsFilter() {
UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
source.registerCorsConfiguration("/**", corsConfig());
return new CorsFilter(source);
}

}
