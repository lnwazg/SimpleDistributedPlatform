package com.lnwazg.kit.db;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Sql以及分页的工具类
 * @author nan.li
 * @version 2016年7月5日
 */
public class SqlUtils
{
    /**
     * 分页起始点
     * 
     * @author Administrator
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public static String calPageStart(String pageIndex, String pageSize)
    {
        return (Integer.parseInt(pageIndex) - 1) * (Integer.parseInt(pageSize)) + "";
    }
    
    /**
     * 初始化分页的参数<br>
     * 有这个参数才分页；否则不分页 
     * @author nan.li
     * @param paramMap
     */
    public static void initPageParams(Map<String, String> paramMap)
    {
        if (StringUtils.isNotEmpty(paramMap.get("pageIndex")) && StringUtils.isNotEmpty(paramMap.get("pageSize")))
        {
            String start = calPageStart(paramMap.get("pageIndex"), paramMap.get("pageSize"));// 起始的偏移量
            String limit = paramMap.get("pageSize");// 获取的条数
            paramMap.put("start", start);
            paramMap.put("limit", limit);
        }
    }
    
    public static String getSqljoinStr(String ids)
    {
        StringBuilder idsStr = new StringBuilder();
        if (StringUtils.isNotEmpty(ids))
        {
            String[] idsArray = ids.split(",");
            if (idsArray.length > 0)
            {
                for (String id : idsArray)
                {
                    idsStr.append("'").append(id).append("'").append(",");
                }
                idsStr.deleteCharAt(idsStr.length() - 1);// 删除掉最后一个逗号
                return idsStr.toString();
            }
        }
        return null;
    }
    
    public static String getSqljoinStr(String[] source)
    {
        if (source == null || source.length == 0)
        {
            return "''";
        }
        List<String> list = Arrays.asList(source);
        return getSqljoinStr(list);
    }
    
    public static String getSqljoinStr(List<?> list)
    {
        if (list == null || list.size() == 0)
        {
            return "''";
        }
        StringBuilder idsStr = new StringBuilder();
        for (Object id : list)
        {
            idsStr.append("'").append(id).append("'").append(",");
        }
        idsStr.deleteCharAt(idsStr.length() - 1);// 删除掉最后一个逗号
        return idsStr.toString();
    }
}
