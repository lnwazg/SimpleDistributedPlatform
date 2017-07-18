package com.lnwazg.swing.util.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lnwazg.kit.date.DateUtils;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.swing.util.WinMgr;

/**
 * 可控制的Job<br>
 * 可以根据全局的总开关进行开关操作
 * @author Administrator
 * @version 2016年2月12日
 */
public abstract class ControlableJob implements Job
{
    @Override
    public void execute(JobExecutionContext context)
        throws JobExecutionException
    {
        if (WinMgr.jobExecSwitch)
        {
            Logs.i(String.format("调用JOB任务【%s】在：%s", getClass().getCanonicalName(), DateUtils.getCurStandardDateTimeStr()));
            executeCustom(context);
        }
        else
        {
            Logs.i(String.format("Job开关已经被关闭，忽略JOB任务【%s】在：", getClass().getCanonicalName(), DateUtils.getCurStandardDateTimeStr()));
        }
    }
    
    /**
     * 执行我的自定义方法
     * @author Administrator
     * @param context
     */
    public abstract void executeCustom(JobExecutionContext context);
    
}
