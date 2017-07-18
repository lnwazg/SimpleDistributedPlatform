package com.lnwazg.kit.task.result;

import java.util.Date;

/**
 * 任务的执行结果的基础类
 * @author nan.li
 * @version 2017年5月2日
 */
public class TaskBaseResult
{
    /**
     * 该任务执行成功了还是失败了
     */
    private boolean success = false;
    
    /**
     * 报错时候的时间，或者是任务执行完毕时候的时间
     */
    private Date time;
    
    /**
     * 如果失败了，那么这里就有具体的失败信息
     */
    private String errMsg;
    
    public boolean isSuccess()
    {
        return success;
    }
    
    public void setSuccess(boolean success)
    {
        this.success = success;
    }
    
    public Date getTime()
    {
        return time;
    }
    
    public void setTime(Date time)
    {
        this.time = time;
    }
    
    public String getErrMsg()
    {
        return errMsg;
    }
    
    public void setErrMsg(String errMsg)
    {
        this.errMsg = errMsg;
    }
    
}
