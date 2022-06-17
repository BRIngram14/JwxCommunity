package com.jwx.community.controll;

import com.jwx.community.entity.DiscussPost;
import com.jwx.community.entity.User;
import com.jwx.community.service.DiscussPostService;
import com.jwx.community.service.UserService;
import com.jwx.community.util.CommunityUtil;
import com.jwx.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @RequestMapping(path="add",method = RequestMethod.POST)
    @ResponseBody
    public String  addDiscussPost(String title,String content)
    {
        User user = hostHolder.getUser();
        if(user==null)
        {
            return CommunityUtil.getJSONString(403,"你还没有登录！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setCreateTime(new Date());
        discussPost.setUserId(user.getId());
        discussPostService.addDiscussPost(discussPost);
        //报错的情况 将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }
    @RequestMapping(path="/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId")int discussPostId, Model model)
    {
        //传入帖子信息
        DiscussPost post = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("post",post);
        //传入作者信息
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        return "/site/discuss-detail";
    }
}
