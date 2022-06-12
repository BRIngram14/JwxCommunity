package com.jwx.community.service;

import com.jwx.community.dao.DiscussPostMapper;
import com.jwx.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userID,int offset,int limit)
    {
        return discussPostMapper.selectDiscussPosts(userID, offset, limit);
    }
    public int findDiscussPostRows(int userId)
    {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
