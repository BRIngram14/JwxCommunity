package com.jwx.community.service;

import com.jwx.community.dao.DiscussPostMapper;
import com.jwx.community.entity.DiscussPost;
import com.jwx.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userID,int offset,int limit)
    {
        return discussPostMapper.selectDiscussPosts(userID, offset, limit);
    }
    public int findDiscussPostRows(int userId)
    {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
    public int addDiscussPost(DiscussPost post)
    {
        if(post==null)
        {
            throw new IllegalArgumentException("参数不能为空！");
        }
        //转义HTML标记 防止用户手动输入html的一些标签生成格式 因此要把标签格式符号转变成转义字符
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertDiscussPost(post);
    }
    public DiscussPost findDiscussPost(int id)
    {
        return discussPostMapper.selectDiscussPostById(id);
    }
    public int updateCommentCount(int id,int commentCount)
    {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}
