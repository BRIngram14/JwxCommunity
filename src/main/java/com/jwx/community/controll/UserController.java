package com.jwx.community.controll;

import com.jwx.community.annotation.LoginRequired;
import com.jwx.community.dao.UserMapper;
import com.jwx.community.entity.User;
import com.jwx.community.service.FollowService;
import com.jwx.community.service.LikeService;
import com.jwx.community.service.UserService;
import com.jwx.community.util.CommunityConstant;
import com.jwx.community.util.CommunityUtil;
import com.jwx.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

//显示user个人信息
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage()
    {

        return "/site/setting";
    }

    //废弃
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model)
    {//实现图片的上传和修改用户头像的访问路径
       if(headerImage==null)
       {
           model.addAttribute("error","您还没有选择图片!");
           return "/site/setting";
       }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));//获取文件后缀
        if(StringUtils.isBlank(suffix))
        {
            model.addAttribute("error","图片格式有问题(无后缀)");
            return "/site/setting";
        }
        //生成随机文件名
        fileName= CommunityUtil.generateUUID()+suffix;
        //确定文件存放的路径
        File dest=new File(uploadPath+"/"+fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败");
        }
        //更新当前用户的头像路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        String headUrl=domain+contextPath+"/user/header/"+fileName;
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(),headUrl);
        return "redirect:/index";

    }

    //废弃
    @RequestMapping(path="header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename")String filename, HttpServletResponse response)
    {//获取头像
        //服务器存放路径
        filename=uploadPath+"/"+filename;
        //文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //响应图片(图片的固定格式"image/"+后缀)
        response.setContentType("image/"+suffix);
        try (//新特性 放在这里不用final的时候手动释放资源
             FileInputStream fis = new FileInputStream(filename);
             OutputStream os = response.getOutputStream();)
        {

            byte[] buffer = new byte[1024];
            int b=0;
            while((b= fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }

        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }
    }
    @LoginRequired
    @RequestMapping(path="updatePassword",method = RequestMethod.POST)
    public String updatePassword(String oldpassword,String newpassword,Model model)
    {
        User user = hostHolder.getUser();
        if(StringUtils.isBlank(newpassword))
        {
            model.addAttribute("newpasswordMsg","新密码不能为空");
            return "/site/setting";
        }
        oldpassword=CommunityUtil.md5(oldpassword+user.getSalt());
        if(!user.getPassword().equals(oldpassword))
        {
            model.addAttribute("oldpasswordMsg","原密码输入错误");
            return "/site/setting";
        }
        newpassword=CommunityUtil.md5(newpassword+user.getSalt());
        if(!newpassword.equals(oldpassword))
        {
            userService.updatePassword(user.getId(),newpassword);
        }
        return "redirect:/index";
    }
    //个人主页
    @RequestMapping(path="/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId")int userId,Model model)
    {
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user",user);
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);

        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed =false;
        if(hostHolder.getUser()!=null)
        {
            //userId是访问的那个主页的人的id
            hasFollowed=followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);


        return "/site/profile";
    }

}
