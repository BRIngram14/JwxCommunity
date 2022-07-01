package com.jwx.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString()
    {
        String rediskey="test:s";
        redisTemplate.opsForValue().set(rediskey,5);
        System.out.println(redisTemplate.opsForValue().get(rediskey));
        System.out.println(redisTemplate.opsForValue().increment(rediskey));
        System.out.println(redisTemplate.opsForValue().decrement(rediskey));

        String rediskey2="test:h";
        redisTemplate.opsForHash().put(rediskey2,"id",1);
        System.out.println(redisTemplate.opsForHash().get(rediskey2,"id"));

        String rediskey3="test:l";
        redisTemplate.opsForList().leftPush(rediskey3,101);
        redisTemplate.opsForList().leftPush(rediskey3,102);
        redisTemplate.opsForList().leftPush(rediskey3,103);
        System.out.println(redisTemplate.opsForList().size(rediskey3));
        System.out.println(redisTemplate.opsForList().index(rediskey3,1));
        System.out.println(redisTemplate.opsForList().range(rediskey3,1,2));
        System.out.println(redisTemplate.opsForList().leftPop(rediskey3));

        //多次访问一个key
        BoundValueOperations operations=redisTemplate.boundValueOps(rediskey);
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());

    }
    @Test
    public void transactional()
    {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations Operations) throws DataAccessException {
               String rediskey="test:tx";
               Operations.multi();//事务的开始

                Operations.opsForSet().add(rediskey,"aaaa");
                Operations.opsForSet().add(rediskey,"aabb");
                Operations.opsForSet().add(rediskey,"aabbcc");
                System.out.println(Operations.opsForSet().members(rediskey));
                return Operations.exec();//提交事务
            }
        });
        System.out.println(obj);
        //[1, 1, 1, [aaaa, aabbcc, aabb]] 1是前面每一行代码影响的行数
    }
}
