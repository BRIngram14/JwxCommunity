package com.jwx.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;


@SpringBootApplication()
@EnableSwagger2
public class CommunityApplication {
	@PostConstruct
	public void init()
	{
		//解决netty启动冲突问题 Netty4Utils的setAvailableProcessors方法 令这个值为false
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
