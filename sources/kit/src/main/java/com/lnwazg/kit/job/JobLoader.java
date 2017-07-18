package com.lnwazg.kit.job;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.reflect.ClassKit;

/**
 * 简易版的任务加载器
 * @author Administrator
 * @version 2016年4月24日
 */
public class JobLoader
{
    /**
     * 默认的任务扫描包
     */
    static String DEFAULT_JOB_SCAN_PACKAGE = "com.lnwazg.job";
    
    public static void main(String[] args)
    {
        JobLoader.loadDefaultPackageJob();
        JobLoader.loadPackageJob("com.lnwazg.job");
    }
    
    /**
     * 加载默认包下面的job
     * @author Administrator
     */
    public static void loadDefaultPackageJob()
    {
        loadPackageJob(DEFAULT_JOB_SCAN_PACKAGE);
    }
    
    /**
     * 加载指定包下面的job
     * @author Administrator
     * @param string
     */
    public static void loadPackageJob(String packageName)
    {
        //尝试取加载该包下面的所有配置信息
        List<Class<?>> classList = ClassKit.getClasses(packageName);
        if (classList.size() > 0)
        {
            Logs.i("根据JOB_SCAN_PACKAGE加载定时器配置...");
            for (Class<?> clazz : classList)
            {
                //如果它继承自Job
                //如果Job是它的父类
                if (Job.class.isAssignableFrom(clazz))
                {
                    //如果是一个合法的类路径，例如：com.lnwazg.job.AlarmPullOutUdiskJob
                    //那么，直接加载初始化这个类即可，默认采用jobType为1的方式进行解析
                    @SuppressWarnings("unchecked")
                    Class<? extends Job> jobClass = (Class<? extends Job>)clazz;
                    String jobName = jobClass.getCanonicalName();
                    //默认采用jobType为1的方式进行解析
                    String cron = "";
                    if (jobClass.isAnnotationPresent(Scheduled.class))
                    {
                        Scheduled scheduled = jobClass.getAnnotation(Scheduled.class);
                        cron = scheduled.cron();
                    }
                    if (StringUtils.isEmpty(cron))
                    {
                        Logs.w(String.format("%s 未提供注解cron表达式配置，因此忽略该JOB！", jobName));
                        continue;
                    }
                    try
                    {
                        StdSchedulerFactory factory = new StdSchedulerFactory();
                        Scheduler scheduler = factory.getScheduler();
                        JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, Scheduler.DEFAULT_GROUP).build();
                        Trigger trigger = TriggerBuilder.newTrigger()
                            .withIdentity(jobName + "trigger", Scheduler.DEFAULT_GROUP)
                            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                            .build();
                        scheduler.scheduleJob(job, trigger);
                        scheduler.start();
                        Logs.d(String.format("QUARTZ_JOB %s 【%s】已启动！", jobName, cron));
                    }
                    catch (SchedulerException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            Logs.i("定时器加载完毕！");
        }
    }
}
