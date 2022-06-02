package com.jwx.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class AConfig {
    //生成一个叫simpleDateFormat的SimpleDateFormat类型的bean存储到容器中
    @Bean
    public SimpleDateFormat simpleDateFormat()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm::ss");
    }

}
