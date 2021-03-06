package com.jwx.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {
    private static final Logger logger= LoggerFactory.getLogger(ThreadPoolTest.class);
    //spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    //spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    //jdk普通线程池
    private ExecutorService executorService= Executors.newFixedThreadPool(5);

    //jdk可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService=Executors.newScheduledThreadPool(5);
    //junit测试和main不一样 main如果启动的线程不挂掉 main会等他执行 不会立刻结束
    //junit启动的线程和当前线程并发 test方法如果后面没逻辑就会直接结束 要让某个线程sleep一会

    private void sleep(long m)
    {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //jdk普通线程池
    @Test
    public void testExecutorService()
    {
        Runnable task= new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testExecutorService");
            }
        };
        for (int i = 0; i <10 ; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }
    //jdk定时任务线程池
    @Test
    public void testScheduledExecutorService()
    {
        Runnable task= new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testScheduledExecutorService");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task,10000,1000, TimeUnit.MILLISECONDS);
        sleep(30000);
    }

    //3.Spring普通线程池
    @Test
    public void testThreadPoolTaskExecutor()
    {
        Runnable task= new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testThreadPoolTaskExecutor");
            }
        };
        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }
    //4.spring定时任务线程池
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task= new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testThreadPoolTaskScheduler");
            }
        };
        Date startTime = new Date(System.currentTimeMillis()+10000);
        taskScheduler.scheduleAtFixedRate(task,startTime,1000);
        sleep(30000);
    }

}
