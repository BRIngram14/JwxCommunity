package com.jwx.community.service;

import com.jwx.community.dao.MessageMapper;
import com.jwx.community.entity.Message;
import com.jwx.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId,int offset,int limit)
    {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId)
    {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId,int offset,int limit)
    {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId)
    {
        return  messageMapper.selectLetterCount(conversationId);
    }
    //conversationId填上就代表是和某人的未读消息 不填就是全部的未读消息
    public int findLetterUnreadCount(int userId,String conversationId)
    {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }
    //增加一条消息
    public int addMessage(Message message)
    {
        //过滤标签 敏感词
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }
    //修改状态为已读
    public int readMessage(List<Integer> ids)
    {
        return messageMapper.updateStatus(ids,1);
    }

    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
       return messageMapper.selectNotices(userId, topic, offset, limit);
   }
}
