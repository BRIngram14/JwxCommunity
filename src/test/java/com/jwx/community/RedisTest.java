package com.jwx.community;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
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
    //统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog()
    {
        String redisKey="test:h11:01";
        for(int i=0;i<=100000;i++){
        redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        for(int i=0;i<=100000;i++){
            int r=(int)(Math.random()*100000+1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }
        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);
    }

    // 将3组数据合并, 再统计合并后的重复数据的独立总数.
    @Test
    public void testHyperLogLogUnion() {
        String redisKey2 = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }

        String redisKey3 = "test:hll:03";
        for (int i = 5001; i <= 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }

        String redisKey4 = "test:hll:04";
        for (int i = 10001; i <= 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4, i);
        }

        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey2, redisKey3, redisKey4);

        long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }

    // 统计一组数据的布尔值
    @Test
    public void testBitMap() {
        String redisKey = "test:bm:01";

        // 记录
        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);

        // 查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));

        // 统计
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });

        System.out.println(obj);
    }

    // 统计3组数据的布尔值, 并对这3组数据做OR运算.
    @Test
    public void testBitMapOperation() {
        String redisKey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey2, 0, true);
        redisTemplate.opsForValue().setBit(redisKey2, 1, true);
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);

        String redisKey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey3, 2, true);
        redisTemplate.opsForValue().setBit(redisKey3, 3, true);
        redisTemplate.opsForValue().setBit(redisKey3, 4, true);

        String redisKey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey4, 4, true);
        redisTemplate.opsForValue().setBit(redisKey4, 5, true);
        redisTemplate.opsForValue().setBit(redisKey4, 6, true);

        String redisKey = "test:bm:or";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), redisKey2.getBytes(), redisKey3.getBytes(), redisKey4.getBytes());
                return connection.bitCount(redisKey.getBytes());
            }
        });

        System.out.println(obj);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 6));
    }

}
