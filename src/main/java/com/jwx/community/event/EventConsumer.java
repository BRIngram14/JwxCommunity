package com.jwx.community.event;

import com.alibaba.fastjson.JSONObject;
import com.jwx.community.entity.Event;
import com.jwx.community.entity.Message;
import com.jwx.community.service.MessageService;
import com.jwx.community.util.CommunityConstant;
import io.swagger.annotations.ApiOperation;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record)
    {
        if(record==null||record.value()==null)
        {
            logger.error("消息内容为空");
            return;
        }
        //将监听到的消息转回event格式
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null)
        {
            logger.error("消息格式有误");
            return;
        }
        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USERID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        //将内容存到map中
        Map<String,Object> content=new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityId",event.getEntityId());
        content.put("entityType",event.getEntityType());
        if(!event.getData().isEmpty())
        {
            //把额外的数据也存入content中
            for(Map.Entry<String,Object> entry:event.getData().entrySet())
            {
                content.put(entry.getKey(),entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

    }

}
