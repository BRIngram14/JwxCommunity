package com.jwx.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(myApiInfo())
                .select()
                // 指定哪个包下面生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.jwx.community.controll"))
                .paths(PathSelectors.any())
                .build();

    }
    private ApiInfo myApiInfo(){
        return new ApiInfoBuilder()
                .title("接口文档")
                .description("接口文档")
                .contact(new Contact("jwx","http:localhost:8080/community/doc.html","1299945454@qq.com"))
                .version("1.0")
                .build();
    }




}
