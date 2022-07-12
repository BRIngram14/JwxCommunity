package com.jwx.community.controll;

import com.jwx.community.entity.*;
import com.jwx.community.event.EventProducer;
import com.jwx.community.service.*;
import com.jwx.community.util.CommunityConstant;
import com.jwx.community.util.CommunityUtil;
import com.jwx.community.util.HostHolder;
import com.jwx.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

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

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPost.getId()) ;
        eventProducer.fireEvent(event);

        //计算帖子分数
        String redisKey= RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,discussPost.getId());



        //报错的情况 将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }
    @RequestMapping(path="/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId")int discussPostId, Model model, Page page)
    {
        //传入帖子信息
        DiscussPost post = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("post",post);
        //传入作者信息
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount",likeCount);
        //点赞状态
        int likeStatus= hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,post.getId());
        model.addAttribute("likeStatus",likeStatus);

        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());
        //评论：给帖子的评论
        //回复：给评论的评论
        //返回评论列表
        List<Comment> commentsList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        //评论VO(view object显示的对象)列表
        List<Map<String,Object>> commentVoList =new ArrayList<>();
        if(commentsList!=null)
        {//将commentsList里每一个comment和对应的作者的信息输入一个map中
            for(Comment comment:commentsList)
            {
                HashMap<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount",likeCount);
                //点赞状态
                likeStatus= hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);

                //得到回复列表
                List<Comment> replyList=commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,
                        comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyVoList=new ArrayList<>();
                if(replyList!=null)
                {//将回复和回复的作者和回复的对象放在一个map中 保存到一个list中
                    for(Comment reply:replyList)
                    {
                        HashMap<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复的目标 回复既可以只回复一条评论 也可以针对评论下的某条回复进行回复 此时需要回复目标
                        User target =reply.getTargetId()==0?null:userService.findUserById(reply.getTargetId()) ;
                        replyVo.put("target", target);
                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount",likeCount);
                        //点赞状态
                        likeStatus= hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);

                        replyVoList.add(replyVo);

                    }
                }
                commentVo.put("replys", replyVoList);
                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }

        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }

    //置顶
    @RequestMapping(path = "/top",method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id)
    {
        discussPostService.updateType(id,1);
        //触发发帖事件 更新到elasticsearch中
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);

    }

    //加精
    @RequestMapping(path = "/wonderful",method = RequestMethod.POST)
    @ResponseBody
    public String setwonderful(int id)
    {
        discussPostService.updateStatus(id,1);
        //触发发帖事件 更新到elasticsearch中
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        //计算帖子分数
        String redisKey= RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,id);
        return CommunityUtil.getJSONString(0);

    }

    //删除
    @RequestMapping(path = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String setdelete(int id)
    {
        discussPostService.updateType(id,2);
        //触发删帖事件 更新到elasticsearch中
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);

    }
}
