package com.lnwazg.myzoo.bean;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 传输的消息对象
 * @author nan.li
 * @version 2016年10月28日
 */
public class Msg
{
    /**
    * 标识客户端连接的身份的令牌<br>
    * 如果没有token，那么下次传输需要带上；如果已经带上了，那么就不用管了
    */
    private String token;
    
    /**
     * 调用的目标controller的path<br>
     * 必须要指定，否则无法正常调用
     */
    private String path;
    
    /**
     * 参数表
     */
    private Map<String, String> map;
    
    /**
     * 参数表列表
     */
    private List<Map<String, String>> list;
    
    /**
     * 参数对象
     */
    private Object obj;
    
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
    public String getToken()
    {
        return token;
    }
    
    public Msg setToken(String token)
    {
        this.token = token;
        return this;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public Msg setPath(String path)
    {
        this.path = path;
        return this;
    }
    
    public Map<String, String> getMap()
    {
        return map;
    }
    
    public Msg setMap(Map<String, String> map)
    {
        this.map = map;
        return this;
    }
    
    public List<Map<String, String>> getList()
    {
        return list;
    }
    
    public Msg setList(List<Map<String, String>> list)
    {
        this.list = list;
        return this;
    }
    
    public Object getObj()
    {
        return obj;
    }
    
    public Msg setObj(Object obj)
    {
        this.obj = obj;
        return this;
    }
    
}
