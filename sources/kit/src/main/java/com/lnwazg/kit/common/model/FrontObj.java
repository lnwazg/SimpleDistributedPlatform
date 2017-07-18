package com.lnwazg.kit.common.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 返回给前端使用的对象<br>
 * 以一种统一的方式，作数据返回操作
 * @author nan.li
 * @version 2015-2-13
 */
public class FrontObj
{
    public static final Integer RESULTCODE_SUCCESS = 0;
    
    /**
     * 错误码<br>
     * 0表示成功，其他表示失败
     */
    private Integer errno;
    
    /**
     * 错误描述
     */
    private String errmsg;
    
    /**
     * 额外的数据<br>
     * 假如是分页查询，data的内容就需要是totalRecord(总记录数)和list（数据列表）
     */
    private Object data;
    
    public FrontObj(Integer errno, String errmsg, Object data)
    {
        super();
        this.errno = errno;
        this.errmsg = errmsg;
        this.data = data;
    }
    
    public FrontObj()
    {
        
    }
    
    /**
     * 返回成功的对象
     * 
     * @author nan.li
     * @return
     */
    public FrontObj success()
    {
        this.errno = RESULTCODE_SUCCESS;
        return this;
    }
    
    /**
     * 返回成功的对象
     * @author nan.li
     * @param msg
     * @return
     */
    public FrontObj success(String errmsg)
    {
        this.errno = RESULTCODE_SUCCESS;
        this.errmsg = errmsg;
        return this;
    }
    
    /**
     * 返回失败的对象，用默认的返回码：10001
     * 
     * @author nan.li
     * @return
     */
    public FrontObj fail()
    {
        return fail(10001, "系统异常");
    }
    
    /**
     * 返回失败的对象，用默认的返回码：10001，并且可以自定义失败信息
     * @author nan.li
     * @param errmsg
     * @return
     */
    public FrontObj fail(String errmsg)
    {
        return fail(10001, errmsg);
    }
    
    /**
     * 返回失败的对象，可以自定义返回码
     * 
     * @author nan.li
     * @param errno
     * @param errmsg
     * @return
     */
    public FrontObj fail(Integer errno, String errmsg)
    {
        this.errno = errno;
        this.errmsg = errmsg;
        return this;
    }
    
    public Object getData()
    {
        return data;
    }
    
    /**
     * 普通请求，设置返回的数据
     * @author nan.li
     * @param data
     * @return
     */
    public FrontObj setData(Object data)
    {
        this.data = data;
        return this;
    }
    
    /**
     * 分页请求，设置返回数据
     * @author nan.li
     * @param totalRecord
     * @param list
     * @return
     */
    public FrontObj setData(int totalRecord, List<?> list)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("totalRecord", totalRecord);
        map.put("list", list);
        this.data = map;
        return this;
    }
    
    public Integer getErrno()
    {
        return errno;
    }
    
    public FrontObj setErrno(Integer errno)
    {
        this.errno = errno;
        return this;
    }
    
    public String getErrmsg()
    {
        return errmsg;
    }
    
    public FrontObj setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
        return this;
    }
    
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}