package com.jwx.community.config;

import com.jwx.community.quartz.AlphaJob;
import com.jwx.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//配置到数据库 只用一次 以后quartz访问数据库调度任务
@Configuration
public class QuartzConfig {

    //FactoryBean可简化Bean的实例化过程
    //1.通过FactoryBean封装了Bean的实例化过程
    //2.将FactoryBean装配到Spring容器里
    //3.将FactoryBean注入给其他的Bean
    //4.该bean得到的是FactoryBean所管理的对象实例


    //配置JobDetail
    //@Bean 先注释掉 不然每次启动还会初始化生成job 和trigger
    public JobDetailFactoryBean alphaJobDetail()
    {
        JobDetailFactoryBean factoryBean=new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);

        return factoryBean;
    }
    //配置Trigger(SimpleTriggerFactoryBean ,CronTriggerFactoryBean这个是处理复杂的定时任务)
    //@Bean 先注释掉 不然每次启动还会初始化生成job 和trigger
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail)
    {
        SimpleTriggerFactoryBean factoryBean=new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    //刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail()
    {
        JobDetailFactoryBean factoryBean=new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);

        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail)
    {
        SimpleTriggerFactoryBean factoryBean=new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000*60*5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}
