package com.lnwazg.kit.servlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求的工具类
 * @author nan.li
 * @version 2016年7月5日
 */
public class ReqUtils
{
    /**
     * 将请求的对象转换成一个map对象
     * 
     * @author Administrator
     * @param request
     * @return
     */
    public static Map<String, String> getParamMap(HttpServletRequest request)
    {
        Enumeration<String> s = request.getParameterNames();
        Map<String, String> result = new HashMap<String, String>();
        if (s != null)
        {
            while (s.hasMoreElements())
            {
                String key = (String)s.nextElement();
                String value = request.getParameter(key);
                result.put(key, value);
            }
        }
        return result;
    }
    
}
