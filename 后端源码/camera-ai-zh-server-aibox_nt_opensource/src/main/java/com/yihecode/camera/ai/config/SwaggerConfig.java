package com.yihecode.camera.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
* Description:swagger Config
* <p>
* Date: 2023/6/1
* @author 465769438@qq.com
*/
@Configuration
public class SwaggerConfig {

    @Value("${swagger.enable:true}")
    private boolean enable;

    /**
* Create API should Use
* apiInfo() increase Add API Phase close Info
* Pass select() Function Back One ApiSelectorBuilder Instance, Use Incoming Control Which Some API Expose to Swagger Incoming show current,
* This Example Collect Use Refer Fixed Scan Package Path Incoming Fixed Meaning Refer Fixed need build Stand API Directory.
* swagger Test Address:http://127.0.0.1:8080/swagger-ui/index.html#/
* @return
*/
    @Bean
    public Docket createRestApi() {
        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("X-Token")
                .description("x-token")
                .required(true)
                .in(ParameterType.HEADER)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());

        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(enable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yihecode.camera.ai.web"))
                .paths(PathSelectors.any())
                .build().globalRequestParameters(parameters);
    }

    /**
* Create the API Basic Info
* Access Address:http:// Project real International Address /swagger-ui.html
* @return
*/
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("AI Video Monitor Management")
                .description("AI Video Monitor Management Platform")
                .contact(new Contact("zhoumingxing", "", "465769438@qq.com"))
                .version("1.0")
                .build();
    }

}
