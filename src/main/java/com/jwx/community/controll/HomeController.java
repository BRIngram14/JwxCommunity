package com.jwx.community.controll;

import com.jwx.community.entity.DiscussPost;
import com.jwx.community.entity.Page;
import com.jwx.community.entity.User;
import com.jwx.community.service.DiscussPostService;
import com.jwx.community.service.LikeService;
import com.jwx.community.service.UserService;
import com.jwx.community.util.CommunityConstant;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,@RequestParam(name="orderMode",defaultValue = "0") int orderMode)
    {
        //方法调用前 springmvc会自动实例化model和page对象，并将page注入model中
        //所以 在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(),orderMode);
        //一张帖子对应一个map，map中除了有帖子的信息 还有发帖人的信息
        List<Map<String ,Object>> discussPosts =new ArrayList<>();
        if(list !=null)
        {
            for(DiscussPost post:list)
            {
                Map<String ,Object> map=new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                long likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "index";
    }

//    @RequestMapping(value = "/index1",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation(value = "查询主页数据")
//    public List<Map<String ,Object>> getIndexPage1(Model model, Page page)
//    {
//        //方法调用前 springmvc会自动实例化model和page对象，并将page注入model中
//        //所以 在thymeleaf中可以直接访问Page对象中的数据
//        page.setRows(discussPostService.findDiscussPostRows(0));
//        page.setPath("/index");
//
//
//        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getoffset(), page.getLimit());
//        List<Map<String ,Object>> discussPosts =new ArrayList<>();
//        if(list !=null)
//        {
//            for(DiscussPost post:list)
//            {
//                Map<String ,Object> map=new HashMap<>();
//                map.put("post",post);
//                User user = userService.findUserById(post.getUserId());
//                map.put("user",user);
//                discussPosts.add(map);
//            }
//        }
//        return discussPosts;
////     model.addAttribute("discussPosts",discussPosts);
////        return "index";
//    }


    @RequestMapping(path="/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
    //拒绝访问时的页面
    @RequestMapping(path="/denied",method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }
}
