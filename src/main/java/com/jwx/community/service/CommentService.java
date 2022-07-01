package com.jwx.community.service;

import com.jwx.community.dao.CommentMapper;
import com.jwx.community.dao.DiscussPostMapper;
import com.jwx.community.entity.Comment;
import com.jwx.community.util.CommunityConstant;
import com.jwx.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author jwx
 * @create 2022-06-19-14:04
 */
@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit)
    {
        return commentMapper.selectCommentByEntity(entityType,entityId,offset,limit);
    }
    public int findCommentCount(int entityType,int entityId )
    {
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment)
    {
        if(comment==null)throw new IllegalArgumentException("参数不能为空");
        //过滤标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        //添加评论
        int rows=commentMapper.insertComment(comment);
        //更新帖子的评论数量(不考虑评论的评论)
        if(comment.getEntityType()==ENTITY_TYPE_POST)
        {
            //从评论表中得到评论数
            int count=commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            //更新给discusspost中的CommentCount
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return rows;
    }
        public Comment findCommentById(int id)
        {
            return commentMapper.selectCommentById(id);
        }
}
