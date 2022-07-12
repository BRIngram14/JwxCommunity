package com.jwx.community.actuator;

import com.jwx.community.util.CommunityUtil;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id="database")
public class DatabaseEndpoint {
    private  static final Logger logger= LoggerFactory.getLogger(DatabaseEndpoint.class);
    @Autowired
    private DataSource dataSource;

    //@ReadOperation说明这个方法通过get请求访问
    @ReadOperation
    public String checkConnection(){
        try(Connection conn = dataSource.getConnection();
        )
        {
            return CommunityUtil.getJSONString(0,"获取连接成功！");
        } catch (SQLException e) {
          logger.error("获取连接失败",e);
          return CommunityUtil.getJSONString(1,"获取连接失败");
        }
    }
}
