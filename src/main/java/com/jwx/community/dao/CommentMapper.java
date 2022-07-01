package com.jwx.community.dao;

import com.jwx.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import javax.mail.MailSessionDefinition;
import java.util.List;

/**
 * @author jwx
 * @create 2022-06-19-11:47
 */
@Mapper
public interface CommentMapper {
    List<Comment> selectCommentByEntity(int entityType ,int entityId,int offset,int limit);
    int selectCountByEntity(int entityType ,int entityId);
    int insertComment(Comment comment);
    Comment selectCommentById(int id);
}
