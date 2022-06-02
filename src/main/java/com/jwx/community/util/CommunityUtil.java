package com.jwx.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID(){
       return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5加密 hello+ 随机字符串->加密
     public static String md5(String key)
     {
         if(StringUtils.isBlank(key))
             return null;
         //加密成16进制的字符串返回 输入要是byte类型的
         return DigestUtils.md5DigestAsHex(key.getBytes());
     }
}
