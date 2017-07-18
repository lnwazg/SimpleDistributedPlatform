/*
 * 文 件 名：HttpUrlUtil.java
 * 版    权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描    述：HTTP URL 工具类
 * 修 改 人：c00126000
 * 修改时间：2009-8-15
 * 修改内容：新增
 */
package com.lnwazg.kit.str;

import org.apache.commons.lang.StringUtils;

/**
 * HTTP URL 工具类
 * 
 * @author c00126000
 * @version C03 2009-8-15
 * @since OpenEye WIDGET_PLT V100R002C03
 */
public final class StringUrlUtils
{
    
    /**
     * 从URL中获取IP和端口
     * 
     * @param httpUrl
     *            URL
     * @return IP和端口
     */
    public static String[] getIpAndPortFromHttpUrl(String httpUrl)
    {
        if (StringUtils.isEmpty(httpUrl))
        {
            return null;
        }
        
        // 获取IP和端口的起始位置(分别处理http和https这两种链接方式)
        int start = httpUrl.indexOf("http://");
        if (start >= 0)
        {
            start += "http://".length();
        }
        else
        {
            start = httpUrl.indexOf("https://");
            if (start >= 0)
            {
                start += "https://".length();
            }
        }
        
        //如果没有"http"或者"https"的开头,则将起始就作为IP地址
        if (start < 0)
        {
            start = 0;
        }
        
        // 获取IP和端口的结束位置
        int end = httpUrl.indexOf("/", start);
        //若没有"/",则认为结束的位置就是端口号
        if (end < 0)
        {
            end = httpUrl.length();
        }
        //ip和端口号组合成的字符串
        String ipport = httpUrl.substring(start, end);
        
        // 解析IP和端口
        int index = ipport.indexOf(':');
        String host = null;
        String port = null;
        //取默认端口号
        if (index < 0)
        {
            host = ipport;
            port = "80";
        }
        else
        {
            host = ipport.substring(0, index);
            port = ipport.substring(index + 1);
        }
        
        final int resultSize = 2;
        String[] result = new String[resultSize];
        result[0] = host;
        result[1] = port;
        return result;
    }
    
    public static void main(String[] args)
    {
        analysis(getIpAndPortFromHttpUrl("192.168.1.1"));
        analysis(getIpAndPortFromHttpUrl("192.168.1.1:7001"));
        
        analysis(getIpAndPortFromHttpUrl("http://192.168.1.1"));
        analysis(getIpAndPortFromHttpUrl("https://192.168.1.1"));
        
        analysis(getIpAndPortFromHttpUrl("http://192.168.1.1:7001"));
        analysis(getIpAndPortFromHttpUrl("https://192.168.1.1:7001"));
        
        analysis(getIpAndPortFromHttpUrl("http://192.168.1.1:7001/test1"));
        analysis(getIpAndPortFromHttpUrl("https://192.168.1.1:7001/test2"));
    }
    
    private static void analysis(String[] ipport)
    {
        System.out.println("ip:" + ipport[0] + " ,port:" + ipport[1]);
    }
}
