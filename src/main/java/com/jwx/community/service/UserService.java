package com.jwx.community.service;

import com.jwx.community.dao.LoginTicketMapper;
import com.jwx.community.dao.UserMapper;
import com.jwx.community.entity.LoginTicket;
import com.jwx.community.entity.User;
import com.jwx.community.util.CommunityConstant;
import com.jwx.community.util.CommunityUtil;
import com.jwx.community.util.MailClient;
import com.jwx.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${community.path.domain}")//注入域名
    private String domain;
    @Value("${server.servlet.context-path}")//注入项目主路径
    private String contextPath;

    public User findUserById(int id)
    {
        //return userMapper.selectById(id);
        User user = getCache(id);
        if(user==null)
        {
            user = initCache(id);
        }
        return user;
    }

    public Map<String,Object> register(User user)
    {
        Map<String, Object> map = new HashMap<>();
        //空值处理
       if(user==null)
       {
           throw new IllegalArgumentException("参数不能为空");
       }
       if(StringUtils.isBlank(user.getUsername()))
       {
           map.put("usernameMsg","账号不能为空");
           return map;
       }
       if(StringUtils.isBlank(user.getPassword()))
       {
           map.put("passwordMsg","密码不能为空");
           return map;
       }
       if(StringUtils.isBlank(user.getEmail()))
       {
           map.put("emailMsg","邮箱不能为空");
           return map;
       }


       //验证账号 邮箱是否存在
        User u = userMapper.selectByName(user.getUsername());
       if(u!=null)
       {
           map.put("usernameMsg","账号已存在");
           return map;
       }
        User u1 = userMapper.selectByEmail(user.getEmail());
       if(u1!=null)
       {
           map.put("emailMsg","邮箱已存在");
           return map;
       }
       //前面都没问题才注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));//生成随机的uuid并只取前五位
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
            //牛客网随机头像的地址是images.nowcoder.com/head/?t.png 序号为0-1000都行
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);


        //激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
            //http://localhost:8080/community/activation/101/ActivationCode 激活的url
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
            //将activation.html中需要的参数保存到thymeleaf模板下的Context中并通过模板引擎的process方法传入，返回一个html
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);


        return map;
    }
    //激活的链接
    public int activation(int userId,String code)
    {
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1)
        {
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code))
        {
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }
        else
        {
            return ACTIVATION_FAILURE;
        }
    }
    public Map<String ,Object> login(String username,String password,int expiredSeconds)
    {
        //判空
        Map<String,Object> map=new HashMap<>();
        if(StringUtils.isBlank(username))
        {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证账号合法性
        User user = userMapper.selectByName(username);
        if(user==null)
        {
            map.put("usernameMsg","账号不存在");
            return map;
        }
        if(user.getStatus()==0)
        {
            map.put("usernameMsg","账号未激活！");
            return map;
        }
        //验证密码
        password =CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password))
        {
            map.put("passwordMsg","密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());//随机生成的身份码
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
       // loginTicketMapper.insertLoginTicket(loginTicket);
        //存入redis中
        String rediskey= RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(rediskey,loginTicket);
        map.put("ticket",loginTicket.getTicket());

        return map;
    }
        public void logout(String ticket)
        {
            //loginTicketMapper.updateStatus(ticket,1);
            //取出loginTicket 将状态改变为1后放回去
            String rediskey= RedisKeyUtil.getTicketKey(ticket);
           LoginTicket loginTicket= (LoginTicket) redisTemplate.opsForValue().get(rediskey);
           loginTicket.setStatus(1);
           redisTemplate.opsForValue().set(rediskey,loginTicket);
        }
        public LoginTicket findLoginTicket(String ticket)
        {
            //return loginTicketMapper.selectByTicket(ticket);
            String rediskey= RedisKeyUtil.getTicketKey(ticket);
            return (LoginTicket) redisTemplate.opsForValue().get(rediskey);
        }

        public int updateHeader(int userId,String headUrl)
        {

            //return userMapper.updateHeader(userId,headUrl);
            int rows = userMapper.updateHeader(userId, headUrl);
            clearCache(userId);
            return  rows;
        }
        public int updatePassword(int userId,String password){

        return userMapper.updatePassword(userId,password); }

        public User findUserByName(String username){
        return userMapper.selectByName(username); }

         //1. 优先从缓存中取值
         private User getCache(int userId)
         {
             String redisKey = RedisKeyUtil.getUserKey(userId);
             return (User) redisTemplate.opsForValue().get(redisKey);
         }
         //2. 取不到时初始化缓存数据
        private User initCache(int userId)
        {
            User user=userMapper.selectById(userId);
            String redisKey = RedisKeyUtil.getUserKey(userId);
            redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
            return user;
        }
         //3. 数据变更时 清除缓存数据
        private void clearCache(int userId)
        {
            String redisKey = RedisKeyUtil.getUserKey(userId);
            redisTemplate.delete(redisKey);
        }
}
