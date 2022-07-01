package com.jwx.community.event;

import com.alibaba.fastjson.JSONObject;
import com.jwx.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;


    //处理事件
    public void fireEvent(Event event)
    {
        //将事件发布到指定的主题 将event转换成json字符串发出去
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
