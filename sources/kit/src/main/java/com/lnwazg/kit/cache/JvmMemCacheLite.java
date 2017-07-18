package com.lnwazg.kit.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 简易版的缓存工具，数据全部存储在内存中，存取速度很快，但是写入太多数据就会有OOM的问题<br>
 * 适合存储少量需要快速存取的数据<br>
 * 可指定缓存的失效时间<br>
 * 如果想安全地存入任意大小的数据，可使用FileCacheLite，无OOM问题，但是存取速度稍慢
 * @author nan.li
 * @version 2016年4月14日
 */
public class JvmMemCacheLite
{
    /**
     * 记录的对象的map
     */
    static Map<String, Object> objMap = new ConcurrentHashMap<>();
    
    /**
     * 记录的放入时间的map
     */
    static Map<String, Long> recordTimestampMap = new ConcurrentHashMap<>();
    
    private JvmMemCacheLite()
    {
    }
    
    /**
     * 从JVM缓存中获取数据，给定指定的失效时间参数
     * 
     * @author nan.li
     * @param key
     * @param queryAllGoodsCacheMinutes
     * @param minutes
     * @return
     */
    public static Object get(String key, int failTime, TimeUnit timeUnit)
    {
        // 如果缓存表中压根就没有，那么忽略之
        if (!objMap.containsKey(key) || !recordTimestampMap.containsKey(key))
        {
            return null;
        }
        
        // 当前时间
        long now = System.currentTimeMillis();
        // 放入的时间
        long putTime = recordTimestampMap.get(key);
        // 时间差
        long deltaTimeInMills = now - putTime; // 时间差
        
        // 超过如下毫秒数则认为缓存已经过期
        long failTimeInMills = 0;
        switch (timeUnit)
        {
            case DAYS:
                failTimeInMills = failTime * 24 * 60 * 60 * 1000;
                break;
            case HOURS:
                failTimeInMills = failTime * 60 * 60 * 1000;
                break;
            case MINUTES:
                failTimeInMills = failTime * 60 * 1000;
                break;
            case SECONDS:
                failTimeInMills = failTime * 1000;
                break;
            default:
                break;
        }
        if (deltaTimeInMills > failTimeInMills)
        {
            return null;
        }
        else
        {
            return objMap.get(key);
        }
    }
    
    public static Object get(String key)
    {
        return objMap.get(key);
    }
    
    /**
     * 存入一个对象到JVM的缓存中
     * 
     * @author nan.li
     * @param key
     * @param obj
     */
    public static void put(String key, Object obj)
    {
        objMap.put(key, obj);
        recordTimestampMap.put(key, System.currentTimeMillis());
    }
    
    /**
     * 检查缓存对象中是否有某个键
     * 
     * @author nan.li
     * @param key
     * @return
     */
    public static boolean containsKey(String key)
    {
        return objMap.containsKey(key);
    }
}
