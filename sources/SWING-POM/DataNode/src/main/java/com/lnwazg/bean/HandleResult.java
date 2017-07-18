package com.lnwazg.bean;

import java.util.Map;

/**
 * 单步或者最终的处理结果对象
 * @author nan.li
 * @version 2017年7月15日
 */
public class HandleResult
{
    /**
     * 节点号
     */
    private String nodeNum;
    
    /**
     * 节点请求序号
     */
    private String nodeNumReqNum;
    
    /**
     * 单步请求的参数表
     */
    private Map<String, String> paramMap;
    
    /**
     * 汇总结果对象
     */
    private Object result;
    
    public String getNodeNum()
    {
        return nodeNum;
    }
    
    public HandleResult setNodeNum(String nodeNum)
    {
        this.nodeNum = nodeNum;
        return this;
    }
    
    public String getNodeNumReqNum()
    {
        return nodeNumReqNum;
    }
    
    public HandleResult setNodeNumReqNum(String nodeNumReqNum)
    {
        this.nodeNumReqNum = nodeNumReqNum;
        return this;
    }
    
    public Map<String, String> getParamMap()
    {
        return paramMap;
    }
    
    public HandleResult setParamMap(Map<String, String> paramMap)
    {
        this.paramMap = paramMap;
        return this;
    }
    
    public Object getResult()
    {
        return result;
    }
    
    public HandleResult setResult(Object result)
    {
        this.result = result;
        return this;
    }
    
    @Override
    public String toString()
    {
        return "HandleResult [nodeNum=" + nodeNum + ", nodeNumReqNum=" + nodeNumReqNum + ", paramMap=" + paramMap + ", result=" + result + "]";
    }
}
