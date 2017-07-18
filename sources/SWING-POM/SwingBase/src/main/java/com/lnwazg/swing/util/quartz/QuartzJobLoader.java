package com.lnwazg.swing.util.quartz;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.lnwazg.kit.job.Scheduled;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.reflect.ClassKit;
import com.lnwazg.swing.util.WinMgr;

/**
 * 定时器加载器<br>
 * 检查当前工程的配置中是否存在定时器的配置信息。如果有，则自动加载相应的定时器<br>
 * 2016-3-24 进一步简化配置：优先从默认的包【com.lnwazg.job】中读取定时器的配置信息
 * @author Administrator
 * @version 2016年2月12日
 */
public class QuartzJobLoader
{
    /**
     * 尝试加载定时器任务
     * @author Administrator
     */
    @SuppressWarnings("unchecked")
    public static void tryLoadAllJobs()
    {
        //确定定时器的加载顺序
        //1.（最高优先级）优先从配置文件中读取JOB_CONFIG，然后根据这个配置去加载配置
        //2.如果1失败，则尝试从配置文件中读取JOB_SCAN_PACKAGE，如果获取不到，则用默认的配置值去读取。然后根据这个配置去加载配置
        String jobConfig = WinMgr.configs.get("JOB_CONFIG");
        if (StringUtils.isBlank(jobConfig))
        {
            //            Logs.i("JOB_CONFIG配置项不存在，因此系统未设定任何定时器，无需启动！");
            //            return;
            //尝试采用第2种方式加载
            String jobScanPackage = WinMgr.configs.get("JOB_SCAN_PACKAGE");
            if (StringUtils.isEmpty(jobScanPackage))
            {
                jobScanPackage = WinMgr.DEFAULT_JOB_SCAN_PACKAGE;
                //                Logs.i(String.format("当前的JOB_SCAN_PACKAGE是默认的搜索包【%s】", jobScanPackage));
            }
            else
            {
                //                Logs.i(String.format("当前的JOB_SCAN_PACKAGE是【%s】", jobScanPackage));
            }
            //尝试取加载该包下面的所有配置信息
            List<Class<?>> classList = ClassKit.getClasses(jobScanPackage);
            if (classList.size() > 0)
            {
                Logs.i(String.format("当前的JOB_SCAN_PACKAGE是【%s】", jobScanPackage));
                Logs.i("根据JOB_SCAN_PACKAGE加载定时器配置...");
                for (Class<?> clazz : classList)
                {
                    //如果它继承自Job
                    //如果Job是它的父类
                    if (Job.class.isAssignableFrom(clazz))
                    {
                        //如果是一个合法的类路径，例如：com.lnwazg.job.AlarmPullOutUdiskJob
                        //那么，直接加载初始化这个类即可，默认采用jobType为1的方式进行解析
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
                            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName + "trigger", Scheduler.DEFAULT_GROUP).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
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
        else
        {
            //第1种加载方式
            Logs.i("根据JOB_CONFIG加载定时器配置...");
            String[] jobsArray = StringUtils.trim(jobConfig).split(",");//JOB1,JOB2,com.lnwazg.job.AlarmPullOutUdiskJob   支持工作的短名称与长名称一起使用，充分发挥配置的灵活性
            for (int i = 0; i < jobsArray.length; i++)
            {
                String jobName = StringUtils.trim(jobsArray[i]);
                if (ClassKit.isValidClass(jobName) || ClassKit.isValidClass(WinMgr.DEFAULT_JOB_SCAN_PACKAGE + "." + jobName))
                {
                    //如果是一个合法的类路径 或者 合法类路径的简写，例如：com.lnwazg.job.AlarmPullOutUdiskJob 或者简写的 AlarmPullOutUdiskJob
                    //那么，直接加载初始化这个类即可，默认采用jobType为1的方式进行解析
                    String jobClassPath = jobName;
                    //如果是简写，那么一定要补全
                    if (ClassKit.isValidClass(WinMgr.DEFAULT_JOB_SCAN_PACKAGE + "." + jobName))
                    {
                        jobClassPath = WinMgr.DEFAULT_JOB_SCAN_PACKAGE + "." + jobName;
                    }
                    Class<? extends Job> jobClass = null;
                    try
                    {
                        jobClass = (Class<? extends Job>)Class.forName(jobClassPath);
                    }
                    catch (ClassNotFoundException e1)
                    {
                        e1.printStackTrace();
                    }
                    //默认采用jobType为1的方式进行解析
                    String cron = "";
                    if (jobClass.isAnnotationPresent(Scheduled.class))
                    {
                        Scheduled scheduled = jobClass.getAnnotation(Scheduled.class);
                        cron = scheduled.cron();
                    }
                    if (StringUtils.isEmpty(cron))
                    {
                        Logs.w(String.format("%s未提供注解cron表达式配置，因此忽略该JOB！", jobName));
                        continue;
                    }
                    try
                    {
                        StdSchedulerFactory factory = new StdSchedulerFactory();
                        Scheduler scheduler = factory.getScheduler();
                        JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, Scheduler.DEFAULT_GROUP).build();
                        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName + "trigger", Scheduler.DEFAULT_GROUP).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
                        scheduler.scheduleJob(job, trigger);
                        scheduler.start();
                        Logs.d(String.format("QUARTZ_JOB %s 【%s】已启动！", jobName, cron));
                    }
                    catch (SchedulerException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    //否则，就是一个类的别名，例如：JOB1
                    String jobClassPath = StringUtils.trim(WinMgr.configs.get(jobName + ".CLASSPATH"));//那么，首先得根据jobName找到这个类的全路径
                    String jobType = StringUtils.trim(WinMgr.configs.get(jobName + ".TYPE"));//然后尝试去找到这个job的TYPE
                    //鉴于1这种方式（cron表达式的方式）用得最多，因此将1作为默认值来使用
                    if (StringUtils.isEmpty(jobType))
                    {
                        //如果不配置这个jobType，那么就按默认类型来使用
                        jobType = "1";
                    }
                    if (StringUtils.isEmpty(jobType) || StringUtils.isEmpty(jobClassPath))
                    {
                        Logs.w(String.format("JOB【%s】配置信息不完整，忽略之！ ", jobName));
                        continue;
                    }
                    Class<? extends Job> jobClass;
                    try
                    {
                        jobClass = (Class<? extends Job>)Class.forName(jobClassPath);
                    }
                    catch (ClassNotFoundException e1)
                    {
                        Logs.e(String.format("JOB CLASS【%s】无法被加载实例化，忽略之！ ", jobClassPath));
                        e1.printStackTrace();
                        continue;
                    }
                    if ("1".equals(jobType))
                    {
                        //cron类型的
                        String cron = WinMgr.configs.get(jobName + ".CRON");//优先从配置信息里面取cron表达式配置
                        if (StringUtils.isEmpty(cron))
                        {
                            //如果没有，则尝试从Class的注解上面获取
                            if (jobClass.isAnnotationPresent(Scheduled.class))
                            {
                                Scheduled scheduled = jobClass.getAnnotation(Scheduled.class);
                                cron = scheduled.cron();
                            }
                        }
                        if (StringUtils.isEmpty(cron))
                        {
                            Logs.w(String.format("%s的jobType配置为1，但是未能提供有效的jobName.CRON或者Annotation类型的表达式配置，因此忽略该JOB！", jobName));
                            continue;
                        }
                        try
                        {
                            StdSchedulerFactory factory = new StdSchedulerFactory();
                            Scheduler scheduler = factory.getScheduler();
                            JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, Scheduler.DEFAULT_GROUP).build();
                            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName + "trigger", Scheduler.DEFAULT_GROUP).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
                            scheduler.scheduleJob(job, trigger);
                            scheduler.start();
                            Logs.d(String.format("QUARTZ_JOB %s 【%s】已启动！", jobName, cron));
                        }
                        catch (SchedulerException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if ("2".equals(jobType))
                    {
                        //永远重复
                        String intervalSecsStr = WinMgr.configs.get(jobName + ".INTERVALSECS");
                        if (StringUtils.isEmpty(intervalSecsStr))
                        {
                            Logs.w(String.format("%s的jobType配置为2，但是未能提供有效的jobName.INTERVALSECS间隔时间秒数的配置，因此忽略该JOB！", jobName));
                            continue;
                        }
                        int intervalSecs = Integer.valueOf(intervalSecsStr);
                        try
                        {
                            StdSchedulerFactory factory = new StdSchedulerFactory();
                            Scheduler scheduler = factory.getScheduler();
                            JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, Scheduler.DEFAULT_GROUP).build();
                            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName + "trigger", Scheduler.DEFAULT_GROUP).startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(intervalSecs).repeatForever()).build();
                            scheduler.scheduleJob(job, trigger);
                            scheduler.start();
                            Logs.d(String.format("INTERVALSECS_JOB %s 【%d】已启动！", jobName, intervalSecs));
                        }
                        catch (SchedulerException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Logs.i("定时器加载完毕！");
        }
    }
}
