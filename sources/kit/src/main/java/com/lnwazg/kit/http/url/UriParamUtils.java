package com.lnwazg.kit.http.url;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 解析URI参数的工具类
 * @author nan.li
 * @version 2016年11月25日
 */
public class UriParamUtils
{
    /**
     * 解析出url参数中的键值对<br>
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param url  url地址
     * @return  url请求参数部分
     */
    public static Map<String, String> resolveUrlParamMap(String url)
    {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = getParamPartOfUrl(url);
        if (strUrlParam == null)
        {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit)
        {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if (arrSplitEqual.length > 1)
            {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }
            else
            {
                if (arrSplitEqual[0] != "")
                {
                    //只有参数没有值，也加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }
    
    /**
     * 解析出url请求的路径，包括页面
     * @param url url地址
     * @return url路径
     */
    public static String getUrlPartOfUrl(String url)
    {
        url = url.trim();
        if (url.length() > 0)
        {
            String[] arrSplit = url.split("[?]");
            if (arrSplit.length > 0)
            {
                if (StringUtils.isNotEmpty(arrSplit[0]))
                {
                    return arrSplit[0];
                }
            }
        }
        return null;
    }
    
    /**
     * 去掉url中的路径，留下请求参数部分
     * @param url url地址
     * @return url请求参数部分
     */
    private static String getParamPartOfUrl(String url)
    {
        url = url.trim();
        if (url.length() > 1)
        {
            String[] arrSplit = url.split("[?]");
            if (arrSplit.length > 1)
            {
                if (StringUtils.isNotEmpty(arrSplit[1]))
                {
                    return arrSplit[1];
                }
            }
        }
        return null;
    }
    
    /**
     * 移除uri中的参数
     * @author nan.li
     * @param uri
     * @return
     */
    public static String removeParams(String uri)
    {
        if (StringUtils.isNotEmpty(uri))
        {
            if (uri.indexOf("?") != -1)
            {
                uri = uri.substring(0, uri.indexOf("?"));
            }
            uri = uri.trim();
        }
        return uri;
    }
    
}