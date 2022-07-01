package com.jwx.community.service;

import com.jwx.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    public void like(int userId,int entityType,int entityId,int entityUserId)
    {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations Operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMember=Operations.opsForSet().isMember(entityLikeKey,userId);

                Operations.multi();

                if(isMember){
                    Operations.opsForSet().remove(entityLikeKey,userId);
                    Operations.opsForValue().decrement(userLikeKey);
                }else{
                    Operations.opsForSet().add(entityLikeKey,userId);
                    Operations.opsForValue().increment(userLikeKey);
                }

                return Operations.exec();
            }
        });


    }
    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType,int entityId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }
    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId)
    {
        String entityLikeKey=RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId)?1:0;
    }
    //查询某个用户获得的赞总数
    public int findUserLikeCount(int userId)
    {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count= (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count==null?0:count.intValue();
    }

}
