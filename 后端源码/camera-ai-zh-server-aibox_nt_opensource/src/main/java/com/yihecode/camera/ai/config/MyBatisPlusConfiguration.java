package com.yihecode.camera.ai.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* mybatis-plus Config, Pagination / All Delete etc Control
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Configuration
public class MyBatisPlusConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //self Dynamic Pagination
PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
paginationInnerInterceptor.setMaxLimit(99L); // Set Per Page most big Value
interceptor.addInnerInterceptor(paginationInnerInterceptor);

// Prevent all table Update and Delete
interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

// multi Tenant: TenantLineInnerInterceptor
// Dynamic state table Name: DynamicTableNameInnerInterceptor
// Happy View Lock: OptimisticLockerInnerInterceptor
interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

// sql Property can Rule Scope: IllegalSQLInnerInterceptor
return interceptor;
}

}
