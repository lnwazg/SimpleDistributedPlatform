package com.lnwazg.bean;

/**
 * MQ消息对象
 * @author nan.li
 * @version 2017年7月10日
 */
public class MqMsg
{
    /**
     * 消息目的地的路由方法
     */
    private String targetInvokeMethodFullPath;
    
    /**
     * 消息参数
     */
    private String[] targetInvokeMethodParams;
    
    public String[] getTargetInvokeMethodParams()
    {
        return targetInvokeMethodParams;
    }
    
    public MqMsg setTargetInvokeMethodParams(String[] targetInvokeMethodParams)
    {
        this.targetInvokeMethodParams = targetInvokeMethodParams;
        return this;
    }
    
    public String getTargetInvokeMethodFullPath()
    {
        return targetInvokeMethodFullPath;
    }
    
    public MqMsg setTargetInvokeMethodFullPath(String targetInvokeMethodFullPath)
    {
        this.targetInvokeMethodFullPath = targetInvokeMethodFullPath;
        return this;
    }
}
