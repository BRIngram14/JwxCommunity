package com.jwx.community;

import com.jwx.community.util.SensitiveFilter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSen()
    {
        String text="这里可以/赌/博/，可以嫖/娼，可以吸/毒，可以开票！！！";
        System.out.println(sensitiveFilter.filter(text));
    }
}
