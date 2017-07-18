package com.lnwazg.kit.executor.kit;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.lnwazg.kit.log.Logs;

/**
 * 拒绝执行任务的处理器
 * @author nan.li
 * @version 2017年3月16日
 */
public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler
{
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
    {
        Logs.w(r.toString() + " is rejected or ignored!");
    }
}
