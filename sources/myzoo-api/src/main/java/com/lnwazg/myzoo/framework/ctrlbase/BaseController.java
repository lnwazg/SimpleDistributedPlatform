package com.lnwazg.myzoo.framework.ctrlbase;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.esotericsoftware.kryonet.Connection;
import com.lnwazg.myzoo.bean.Msg;

public class BaseController
{
    /**
     * 请求的map
     */
    protected Map<String, String> request;
    
    /**
     * 请求的map,等价于request
     */
    protected Map<String, String> paramMap;
    
    /**
     * 请求的map的list
     */
    protected List<Map<String, String>> paramList;
    
    /**
     * 请求的参数对象
     */
    protected Object paramObj;
    
    /**
     * 当前的连接对象
     */
    protected Connection connection;
    
    /**
     * 当前连接的客户端的令牌token
     */
    protected String token;
    
    protected Msg getTokenMsg()
    {
        return new Msg().setToken(token);
    }
    
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
