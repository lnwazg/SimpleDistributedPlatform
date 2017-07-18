package com.lnwazg.kit.describe;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mchange.lang.ByteUtils;

/**
 * 一个用于描述对象信息的工具类
 * @author nan.li
 * @version 2016年4月21日
 */
public class DescribeUtils
{
    /**
     * 描述一个Map的内容
     * @author nan.li
     * @param map
     * @param describes 
     */
    public static void describeMap(Map<?, ?> map, String... describes)
    {
        System.out.println(joinDescribesHead(describes));
        for (Object key : map.keySet())
        {
            System.out.println(String.format("key:%s   value:%s", key, map.get(key)));
        }
    }
    
    /**
     * 描述任意List的内容
     * @author nan.li
     * @param listMap
     * @param describes
     */
    @SuppressWarnings("rawtypes")
    public static void describeList(List<?> listMap, String... describes)
    {
        System.out.println(joinDescribesHead(describes));
        int num = 1;
        for (Object obj : listMap)
        {
            System.out.println(String.format("第%s条数据：", num++));
            if (obj instanceof Map)
            {
                for (Object key : ((Map)obj).keySet())
                {
                    System.out.println(String.format("key:%s   value:%s", key, ((Map)obj).get(key)));
                }
            }
            else
            {
                System.out.println(String.format("%s", obj));
            }
        }
    }
    
    /**
     * 描述任意数组的内容
     * @author nan.li
     * @param array
     * @param describes
     */
    public static void describeArray(Object[] array, String... describes)
    {
        System.out.println(joinDescribesHead(describes));
        if (array != null && array.length > 0)
        {
            for (int i = 0; i < array.length; i++)
            {
                System.out.println(String.format("第%s条数据：%s", i, array[i]));
            }
        }
        else
        {
            System.out.println("param array is null or empty!");
        }
    }
    
    /**
     * 描述字节数组的内容
     * @author nan.li
     * @param o
     * @param describes
     */
    public static void describeByteArray(byte[] o, String... describes)
    {
        System.out.println(String.format("%s: %s", joinDescribesHead(describes), ByteUtils.toHexAscii(o)));
    }
    
    /**
     * 描述任意对象的内容
     * @author nan.li
     * @param object
     * @param describes
     */
    public static void describe(Object object, String... describes)
    {
        System.out.println(String.format("%s: %s", joinDescribesHead(describes), object));
    }
    
    /**
     * 拼接描述头
     * @author nan.li
     * @param describes
     * @return
     */
    private static String joinDescribesHead(String... describes)
    {
        return (describes != null ? StringUtils.join(describes, " ") : "");
    }
}
