package com.lnwazg.kit.map;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.lnwazg.kit.log.Logs;

public class Maps
{
    /**
     * 快速生成一个map的工具类
     * @author nan.li
     * @param objects
     * @return
     */
    public static Map<String, Object> asMap(Object... objects)
    {
        Map<String, Object> ret = new HashMap<>();
        if (objects != null && objects.length > 0 && objects.length % 2 == 0)
        {
            for (int i = 0; i + 1 < objects.length; i += 2)
            {
                ret.put(ObjectUtils.toString(objects[i]), objects[i + 1]);
            }
        }
        else
        {
            Logs.w("参数个数非法！");
        }
        return ret;
    }
    
    /**
     * 返回一个String Map
     * @author nan.li
     * @param objects
     * @return
     */
    public static Map<String, String> asStrMap(Object... objects)
    {
        Map<String, String> ret = new HashMap<>();
        if (objects != null && objects.length > 0 && objects.length % 2 == 0)
        {
            for (int i = 0; i + 1 < objects.length; i += 2)
            {
                ret.put(ObjectUtils.toString(objects[i]), ObjectUtils.toString(objects[i + 1]));
            }
        }
        else
        {
            Logs.w("参数个数非法！");
        }
        return ret;
    }
    
    public static HashMap<String, String> asStrHashMap(Object... objects)
    {
        return (HashMap<String, String>)asStrMap(objects);
    }
    
    public static boolean isNotEmpty(Map<?, ?> map)
    {
        return !isEmpty(map);
    }
    
    public static boolean isEmpty(Map<?, ?> map)
    {
        if (map == null || map.size() == 0)
        {
            return true;
        }
        return false;
    }
}
