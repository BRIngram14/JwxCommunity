package com.jwx.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class wkConfig {
    private static final Logger logger= LoggerFactory.getLogger(wkConfig.class);
    @Value("${wk.image.storage}")
    private String wkImageStorage;

    //服务启动前创建wk长图路径文件夹
    @PostConstruct
    public void init()
    {
        //创建WK图片目录
        File file=new File(wkImageStorage);
        if(!file.exists()){
            file.mkdir();
            logger.info("创建wk图片目录"+wkImageStorage);
        }
    }
}
