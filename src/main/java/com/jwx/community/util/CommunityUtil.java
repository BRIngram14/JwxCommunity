package com.jwx.community.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
     public static String getJSONString(int code, String msg, Map<String,Object> map)
     {//封装成json对象

         JSONObject json=new JSONObject();
         json.put("code",code);
         json.put("msg",msg);
         if(map!=null)
         {
             for(String key:map.keySet())
             {
                 json.put(key,map.get(key));
             }
         }
         return json.toJSONString();
     }
    public static String getJSONString(int code, String msg)
    {//重载
        return getJSONString(code, msg,null);

    }
    public static String getJSONString(int code)
    {//重载
        return getJSONString(code, null,null);
    }


}
