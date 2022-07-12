package com.jwx.community.controll;

import com.jwx.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.Map;

@Controller
public class DataController {
    @Autowired
    private DataService dataService;

    //统计页面
    @RequestMapping(path = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage()
    {
        return "/site/admin/data";
    }

    //处理统计网站UV
    @RequestMapping(path = "/data/uv",method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model)
    {//传入的是日期的字符串 使用这个注解告诉服务器输入的格式
        long uv=dataService.calculateUV(start,end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);
        return "forward:/data";
        //使用跳转的方式 和直接return "/site/admin/data";效果一样 跳转到/data方法的访问路径
        //跳转就是当前的方法只处理了一半请求 还需要一个平级的方法继续处理 而不是直接跳到模板
        //请求在处理的过程中类型是不能变的 所以/data路径也需要支持post方法 才能被转发
    }

    //统计活跃用户DAU
    @RequestMapping(path = "/data/dau",method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model) {//传入的是日期的字符串 使用这个注解告诉服务器输入的格式
        long DAU = dataService.calcualteDAU(start, end);
        model.addAttribute("dauResult", DAU);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);
        return "forward:/data";
    }


}
