package com.lnwazg.kit.http;

/**
 * 当前被测试的http代理服务器的状态
 * @author nan.li
 * @version 2016年12月8日
 */
public class ProxyState
{
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 测试消耗的时间
     */
    private long costTime;
    
    public ProxyState(boolean success, long costTime)
    {
        this.success = success;
        this.costTime = costTime;
    }
    
    public boolean isSuccess()
    {
        return success;
    }
    
    public void setSuccess(boolean success)
    {
        this.success = success;
    }
    
    public long getCostTime()
    {
        return costTime;
    }
    
    public void setCostTime(long costTime)
    {
        this.costTime = costTime;
    }
    
    @Override
    public String toString()
    {
        return "ProxyState [success=" + success + ", costTime=" + costTime + " ms]";
    }
}
