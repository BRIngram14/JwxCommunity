package com.jwx.community.controll;

import com.jwx.community.dao.MessageMapper;
import com.jwx.community.entity.Message;
import com.jwx.community.entity.Page;
import com.jwx.community.entity.User;
import com.jwx.community.service.MessageService;
import com.jwx.community.service.UserService;
import com.jwx.community.util.CommunityUtil;
import com.jwx.community.util.HostHolder;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    //私信列表
    @RequestMapping(path="/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page)
    {

        User user=hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> conversationsList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations=new ArrayList<>();
        if(conversationsList!=null)
        {
            for(Message message:conversationsList)
            {//每一个会话需要conversation 消息条数 未读消息条数 发来信息的人的头像
                HashMap<String, Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                //如果当前用户是消息的发起者  targetId就是目标人 此时就是message.getToId() 否则message.getFromId()
                int targetId = user.getId()==message.getFromId()? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        //查询总的未读消息数量
        int letterUnreadCount= messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        return "/site/letter";
    }
    //私信详情
    @RequestMapping(path="/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId")String conversationId,Page page,Model model )
    {
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        //和某人的私信的详细内容
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters=new ArrayList<>();
        if(letterList!=null)
        {
            for(Message message:letterList)
            {
                Map<String,Object> map=new HashMap<>();
                map.put("letter",message);
                //谁发的信息 头像就放在前面
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        //私信目标
        model.addAttribute("target",getLetterTarget(conversationId));
        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty())
        {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";

    }
    //用于页面上 来自某人的私信 这个某人一定不是登录的用户
    private  User getLetterTarget(String conversationId)
    {
        String[] ids = conversationId.split("_");
        int id0=Integer.parseInt(ids[0]);
        int id1=Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId()==id0)
        {
            return userService.findUserById(id1);
        }
        else return userService.findUserById(id0);

    }
    //点击到私信详情列表时将未读的消息变为已读
    private List<Integer> getLetterIds(List<Message> letterList)
    {
        List<Integer> ids=new ArrayList<>();
        if(letterList!=null)
        {
            for(Message message:letterList)
            {
                //当前用户是接受者的时候才会读消息
                if(hostHolder.getUser().getId()==message.getToId()&&message.getStatus()==0)
                {
                    ids.add(message.getId());
                }

            }
        }
        return ids;
    }

    @RequestMapping(path="/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content)
    {

        User target = userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJSONString(1,"目标用户不存在！");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId()<message.getToId())
        {
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }
        else {
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

}