package com.jwx.community;

import com.jwx.community.dao.DiscussPostMapper;
import com.jwx.community.dao.LoginTicketMapper;
import com.jwx.community.dao.UserMapper;
import com.jwx.community.entity.DiscussPost;
import com.jwx.community.entity.LoginTicket;
import com.jwx.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void test1()
    {
        User user = userMapper.selectById(101);
        System.out.println(user);
        User user1 = userMapper.selectByEmail("nowcoder111@sina.com");
        System.out.println(user1);
        User user2 = userMapper.selectByName("ddd");
        System.out.println(user2);
//        User user = new User();
//        user.setUsername("kobe");
//        user.setPassword("824");
//        user.setSalt("laker");
//        user.setEmail("824@qq.com");
//        user.setHeaderUrl("kobe.png");
//        user.setCreateTime(new Date());
//        int count = userMapper.insertUser(user);
//        System.out.println(count);
//        int i = userMapper.updateStatus(150, 1);
//        System.out.println(i);
//        int i1 = userMapper.updateHeader(150, "KobeBryant");
//        System.out.println(i1);
//        int i2 = userMapper.updatePassword(150, "248");
//        System.out.println(i2);
    }

    @Test
    public void test2()
    {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for(DiscussPost list:discussPosts)
            System.out.println(list);

        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }
    @Test
    public void test3()
    {
//        LoginTicket loginTicket = new LoginTicket();
//        loginTicket.setUserId(200);
//        loginTicket.setTicket("abc");
//        loginTicket.setStatus(0);
//        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
//        System.out.println(loginTicketMapper.insertLoginTicket(loginTicket));
        System.out.println(loginTicketMapper.selectByTicket("abc"));
        System.out.println(loginTicketMapper.updateStatus("abc", 0));
    }
}
