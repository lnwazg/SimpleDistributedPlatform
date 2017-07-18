package com.lnwazg.kit.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lnwazg.kit.date.DateUtils;
import com.lnwazg.kit.log.Logs;

/**
 * 带提示信息的Job
 * @author Administrator
 * @version 2016年2月12日
 */
public abstract class PromptJob implements Job
{
    @Override
    public void execute(JobExecutionContext context)
        throws JobExecutionException
    {
        Logs.i(String.format("调用JOB任务【%s】在：%s", getClass().getCanonicalName(), DateUtils.getCurStandardDateTimeStr()));
        executeCustom(context);
    }
    
    /**
     * 执行我的自定义方法
     * @author Administrator
     * @param context
     */
    public abstract void executeCustom(JobExecutionContext context);
    
}
