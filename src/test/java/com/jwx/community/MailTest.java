package com.jwx.community;

import com.jwx.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;


    @Test
    public void testMail1()
    {
        mailClient.sendMail("1299945454@qq.com","发给自己","自己");
    }
    @Test
    public void testMail2()
    {
        Context context=new Context();//thymeleaf包下的context
        context.setVariable("username","sunday");
        //模板引擎中输入一个html和数据context
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("1299945454@qq.com","HTML",content);
    }

}
