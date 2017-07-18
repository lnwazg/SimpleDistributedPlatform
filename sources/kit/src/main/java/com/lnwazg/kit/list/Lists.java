package com.lnwazg.kit.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List的常用工具类<br>
 * 仿scala的用法
 * @author nan.li
 * @version 2016年7月21日
 */
public class Lists
{
    public static List<Integer> asList(int beginNum, int endNum)
    {
        List<Integer> list = new ArrayList<>();
        for (int i = beginNum; i <= endNum; i++)
        {
            list.add(i);
        }
        return list;
    }
    
    /**
     * 快速生成一个List，支持泛型操作
     * @author nan.li
     * @param ts
     * @return
     */
    @SafeVarargs
    public static <T> List<T> asList(T... ts)
    {
        return Arrays.asList(ts);
    }
    
    public static boolean isNotEmpty(List<?> list)
    {
        if (list != null && list.size() > 0)
        {
            return true;
        }
        return false;
    }
    
    public static boolean isEmpty(List<?> list)
    {
        return !isNotEmpty(list);
    }
}
