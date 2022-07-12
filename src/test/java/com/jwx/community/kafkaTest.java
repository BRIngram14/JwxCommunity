package com.jwx.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class kafkaTest {
    @Autowired
    private KafkaProduce kafkaProduce;


    @Test
    public void testKafka()
    {
        kafkaProduce.sendMessage("test","111111");
        kafkaProduce.sendMessage("test","312313111");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
@Component
class KafkaProduce{
    @Autowired
    private KafkaTemplate kafkaTemplate;
    public void sendMessage(String topic,String content)
    {
        kafkaTemplate.send(topic,content);
    }
}
@Component
class KafkaConsumer{
    //spring会自动监听test这个主题 一个线程阻塞在那 一直试图去读取test主题下的消息 是阻塞的状态
    //如果读到消息 就传入下面修饰的方法中的record中
    @KafkaListener(topics={"test"})
    public void handleMessage(ConsumerRecord record)
    {
        System.out.println(record.value());
    }
}