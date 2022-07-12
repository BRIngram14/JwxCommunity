package com.jwx.community;

import com.jwx.community.entity.DiscussPost;
import com.jwx.community.service.DiscussPostService;
import org.checkerframework.checker.units.qual.A;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTests {

    @Autowired
    private DiscussPostService discussPostService;
    private DiscussPost data;

    @BeforeClass
    public static void beforeClass()
    {//和类有关的用static
        System.out.println("beforeClass");
    }
    @AfterClass
    public static void AfterClass()
    {//和类有关的用static
        System.out.println("AfterClass");
    }
    @Before
    public  void before()
    {
        //初始化测试数据
        data=new DiscussPost();
        data.setUserId(111);
        data.setTitle("testtitle");
        data.setContent("test content");
        data.setCreateTime(new Date());
        discussPostService.addDiscussPost(data);
    }
    @After
    public  void after()
    {
        //删除测试数据
        System.out.println("after");
        discussPostService.updateStatus(data.getId(), 2);
    }

    @Test
    public void test1()
    {
        System.out.println("test1");
    }
    @Test
    public void test2()
    {
        System.out.println("test2");
    }

    @Test
    public void testFindById()
    {
        DiscussPost post = discussPostService.findDiscussPost(data.getId());
        Assert.assertNotNull(post);
        Assert.assertEquals(data.getTitle(),post.getTitle());
        Assert.assertEquals(data.getContent(),post.getContent());

    }
}
