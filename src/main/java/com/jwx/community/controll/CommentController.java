package com.jwx.community.controll;


import com.jwx.community.entity.Comment;
import com.jwx.community.entity.DiscussPost;
import com.jwx.community.entity.Event;
import com.jwx.community.event.EventProducer;
import com.jwx.community.service.CommentService;
import com.jwx.community.service.DiscussPostService;
import com.jwx.community.util.CommunityConstant;
import com.jwx.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author jwx
 * @create 2022-06-19-20:15
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;


    @RequestMapping(path="/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment)
    {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event().setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData("postId",discussPostId);

        if(comment.getEntityType()==ENTITY_TYPE_POST)
        {
            //如果是对帖子的评论 就根据评论对于帖子的id找到帖子 再找到帖子的发起人
            DiscussPost target = discussPostService.findDiscussPost(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            //对评论的回复 就根据回复的对象的id找到评论 再找到评论的作者
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        return "redirect:/discuss/detail/"+discussPostId;
    }
}
