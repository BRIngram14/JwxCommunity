package com.jwx.community.controll;

import com.google.code.kaptcha.Producer;
import com.jwx.community.entity.User;
import com.jwx.community.service.UserService;
import com.jwx.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterPage()
    {
        return "/site/register";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(){return "/site/login";}



    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(Model model, User user)
    {
        Map<String, Object> map = userService.register(user);
        if(map==null ||map.isEmpty())
        {
            model.addAttribute("msg","注册成功，我们向您的邮件发送了一封激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));

            return "/site/register";
        }

    }
    //http://localhost:8080/community/activation/101/code
    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code")String code)
    {
        int result = userService.activation(userId, code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号已经可以登录了");
            model.addAttribute("target","/login");
        }else if(result==ACTIVATION_REPEAT)
        {
            model.addAttribute("msg","无效操作，该账号已经激活过了，");
            model.addAttribute("target","/index");
        }
        else {
            model.addAttribute("msg","激活失败，您的激活码不正确");
            model.addAttribute("target","/index");
        }
         return "/site/operate-result";
    }
        //生成验证码的方法
    @RequestMapping(path="/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session)
    {//服务端需要记住生成的验证码，在访问时验证，作为敏感信息不能存在浏览器端，所以存放在session
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //将验证码存入session
        session.setAttribute("kaptcha",text);
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os =response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败："+e.getMessage());
        }
    }

    @RequestMapping(path="/login",method = RequestMethod.POST)
    public String login(String username,String password,String code, boolean rememberme,
                        Model model,HttpSession session,HttpServletResponse response)
    {
       String kaptcha= (String) session.getAttribute("kaptcha");
       //检查验证码
       if(StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code))
       {
           model.addAttribute("codeMsg","验证码不正确");
           return "site/login";
       }
       //检查账号密码
        int expiredSeconds=rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket"))
        {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }
        else
        {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));

            return "/site/login";
        }
    }
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket)
    {
        //使用cookievalue注解找到叫ticket的cookie 并保存到形参ticket中
        userService.logout(ticket);
        return "redirect:/login";
    }
}
