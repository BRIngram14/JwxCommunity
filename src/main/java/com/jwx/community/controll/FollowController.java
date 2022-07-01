package com.jwx.community.controll;

import com.jwx.community.entity.Event;
import com.jwx.community.entity.Page;
import com.jwx.community.entity.User;
import com.jwx.community.event.EventProducer;
import com.jwx.community.service.FollowService;
import com.jwx.community.service.UserService;
import com.jwx.community.util.CommunityConstant;
import com.jwx.community.util.CommunityUtil;
import com.jwx.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId)
    {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType,entityId);
        //触发关注事件 目前只能关注人EntityUserId就是entityId
        // 关注人的通知会链接到人 不会跳到帖子 所以不用传入postId
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW).setUserId(user.getId())
                .setEntityType(entityType).setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0,"已关注");
    }

    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId)
    {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType,entityId);
        return CommunityUtil.getJSONString(0,"已取消关注");
    }

    @RequestMapping(path="/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Page page, Model model)
    {
        User user  = userService.findUserById(userId);
        if(user == null)
        {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        page.setPath("/followees/"+userId);
        page.setLimit(5);
        page.setRows((int)followService.findFolloweeCount(userId,ENTITY_TYPE_USER));
        List<Map<String,Object>> userlist =followService.findFollowees(userId,page.getOffset(),page.getLimit());
        if(userlist!=null)
        {
            for(Map<String,Object> map:userlist)
            {
                User u= (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userlist);
        return "/site/followee";

    }

    @RequestMapping(path="/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId")int userId, Page page, Model model)
    {
        User user  = userService.findUserById(userId);
        if(user == null)
        {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        page.setPath("/followers/"+userId);
        page.setLimit(5);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER,userId));
        List<Map<String,Object>> userlist =followService.findFollowers(userId,page.getOffset(),page.getLimit());
        if(userlist!=null)
        {
            for(Map<String,Object> map:userlist)
            {
                User u= (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userlist);
        return "/site/follower";

    }

    private boolean hasFollowed(int userId)
    {
        if(hostHolder.getUser()==null)
        {
            return  false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }
}