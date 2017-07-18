package com.lnwazg.kit.file;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取路径的工具包
 * 
 * @author nan.li
 * @version 2015年12月21日
 */
public class PathKit
{
    
    /**
     * 获取上下文路径<br>
     * 例如:front_insurance
     * 
     * @author nan.li
     * @param req
     * @return
     */
    public static String getContextpath(HttpServletRequest request)
    {
        return request.getContextPath();
    }
    
    /**
     * 获取基准路径
     * 
     * @author nan.li
     * @param request
     * @return
     */
    public static String getBasePath(HttpServletRequest request)
    {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
    }
    
    /**
     * 获取真实的路径
     * 
     * @author nan.li
     * @param req
     * @return
     */
    public static String getRealPath(HttpServletRequest request)
    {
        return request.getSession().getServletContext().getRealPath("");
    }
    
}
