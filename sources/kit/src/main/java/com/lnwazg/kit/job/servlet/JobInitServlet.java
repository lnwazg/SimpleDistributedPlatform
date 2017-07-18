package com.lnwazg.kit.job.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.lnwazg.kit.job.Scheduled;

/**
 * 初始化定时器的Servlet<br>
 * 使用方法：<br>
 * 1.在配置根目录的指定文件夹路径放置配置文件   /config/job.cfg  （配置文件的模板可以直接在当前包里面找到，拷贝一份过去）<br>
 * 2.在web.xml中增加如下配置信息即可:<br>
 *  {@code
 *  
 *   <!-- 定时器 -->
 *   <servlet>
 *       <servlet-name>quartzJobServlet</servlet-name>
 *       <servlet-class>com.kidswant.job.servlet.JobInitServlet</servlet-class>
 *       <load-on-startup>2</load-on-startup>
 *   </servlet>
 *   
 *   } 
 * @author nan.li
 * @version 2015年11月11日
 */
public class JobInitServlet extends HttpServlet
{
    private static final long serialVersionUID = 5855635948786829782L;
    
    private static StdSchedulerFactory factory = new StdSchedulerFactory();
    
    public void init()
        throws ServletException
    {
        System.out.println("定时器启动初始化...");
        // 1.加载配置文件，解析出class的列表
        // 2.依次加载每一个class文件
        List<Class<Job>> clazzList = getClassNameList();
        if (clazzList != null && clazzList.size() > 0)
        {
            for (Class<Job> jobClazz : clazzList)
            {
                try
                {
                    String jobName = jobClazz.getCanonicalName();
                    String cron = getCron(jobClazz);// 从注解中读取
                    if (StringUtils.isBlank(cron))
                    {
                        System.out.println(String.format("%s 未配置cron表达式，因此无法启动定时任务！请添加@Scheduled(cron='xxx')注解之后重试！", jobName));
                        continue;
                    }
                    Scheduler scheduler = factory.getScheduler();
                    JobDetail job = JobBuilder.newJob((Class<? extends Job>)jobClazz).withIdentity(jobName, Scheduler.DEFAULT_GROUP).build();
                    Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName + "trigger", Scheduler.DEFAULT_GROUP).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
                    scheduler.scheduleJob(job, trigger);
                    scheduler.start();
                    System.out.println(String.format("%s 【%s】已启动！", jobName, cron));
                }
                catch (SchedulerException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            System.out.println("定时器配置为空！忽略初始化...");
        }
        System.out.println("定时器启动完毕...");
    }
    
    /**
     * 获取cron表达式
     * 
     * @author nan.li
     * @param jobClazz
     * @return
     */
    private String getCron(Class<Job> jobClazz)
    {
        Method method = MethodUtils.getAccessibleMethod(jobClazz, "execute", JobExecutionContext.class);
        if (method != null)
        {
            if (method.isAnnotationPresent(Scheduled.class))
            {
                Scheduled scheduled = method.getAnnotation(Scheduled.class);
                String cron = scheduled.cron();
                return cron;
            }
        }
        return null;
    }
    
    /**
     * 获取注册的job类的列表
     * 
     * @author nan.li
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Class<Job>> getClassNameList()
    {
        String path = this.getClass().getClassLoader().getResource("").getFile() + "config/job.cfg";
        try
        {
            List<Class<Job>> ret = new ArrayList<Class<Job>>();
            List<String> classNameList = FileUtils.readLines(new File(path), CharEncoding.UTF_8);
            if (classNameList != null && classNameList.size() > 0)
            {
                for (String classpath : classNameList)
                {
                    String trimed = StringUtils.trimToEmpty(classpath);
                    if (StringUtils.isNotBlank(trimed))
                    {
                        if (trimed.startsWith("#") || trimed.startsWith("//"))
                        {
                            // 让定时器的配置文件支持注释
                            continue;
                        }
                        // 尝试加载这个类
                        Class<Job> jobClass = (Class<Job>)Class.forName(trimed);
                        if (jobClass != null)
                        {
                            ret.add(jobClass);
                        }
                        else
                        {
                            System.out.println(String.format("无法实例化定时器类: %s, 请检查类路径是否写错？", trimed));
                            continue;
                        }
                    }
                }
            }
            return ret;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public void destroy()
    {
        try
        {
            factory.getScheduler().shutdown(true);
            
            System.out.println("Scheduler stop success!");
        }
        catch (SchedulerException e)
        {
            System.out.println("Scheduler stop failed!");
            e.printStackTrace();
        }
        super.destroy();
    }
}
