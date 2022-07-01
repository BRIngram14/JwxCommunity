package com.jwx.community.dao;

import com.jwx.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    //主键自增并赋给id属性
    @Insert({"insert into login_ticket(user_id,ticket,status,expired)",
    "values(#{userId},#{ticket},#{status},#{expired})"})
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    @Select({"select id,user_id,ticket,status,expired",
            "from login_ticket where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);



    //如果要加if的话必须得加上<script>标签
        // @Update({"<script>",
        // "update login_ticket set status=#{status} where ticket=#{ticket}",
        // "<if test=\"ticket!=null\">",
        // "and 1=1",
        // "</if>",
        // "</scritp>"
        // })
    @Update({"update login_ticket set status=#{status} where ticket=#{ticket}"})
    int updateStatus(String ticket,int status);

}
