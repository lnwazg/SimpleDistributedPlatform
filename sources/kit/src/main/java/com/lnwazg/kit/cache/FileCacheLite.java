package com.lnwazg.kit.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * 缓存类，当缓存文件过大的时候自动溢出到磁盘中,避免oom的问题！<br>
 * 适合存储大量的数据，但是对存取速度要求不那么高的情况<br>
 * 如果你不确定到底应该使用FileCacheLite还是MemCacheLite，那么此时应该直接选择FileCacheLite（对象必须实现Serializable接口），这是一个安全有效的选择！<br>
 * 实测，可稳定高速缓存100G的本地数据！惊喜吧！
 * @author nan.li
 * @version 2016年4月26日
 */
public class FileCacheLite
{
    /**
     * 系统全局缓存
     * 是所有其他缓存的缓存容器
     * 缓存的值是一个ehcache对象
     * 整个系统中只管理一个cache块，简单快捷
     */
    public static final String SYSTEM_CACHE = "LDK_CACHE";
    
    public static final FileCache fileCacheProxy = new FileCache(SYSTEM_CACHE);
    
    private FileCacheLite()
    {
    }
    
    /**
     * 将对象放入缓存，如果是对象类型，那么该对象必须是可序列化的<br>
     * EhCache在put对象时，该对象必须是可序列化（Serializable）的类型
     * @param key
     * @param value
     * @see [类、类#方法、类#成员]
     */
    public static void put(Serializable key, Serializable value)
    {
        fileCacheProxy.put(key, value);
    }
    
    public void put(Serializable key, Object value)
    {
        fileCacheProxy.put(key, value);
    }
    
    public static Cache getCacheObj()
    {
        return fileCacheProxy.getCacheObj();
    }
    
    /**
     * 即使过期了，也不想删掉。因为还可能不按照过期时间来取
     * @author nan.li
     * @param key
     * @param failTime
     * @param timeUnit
     * @return
     */
    public static Object get(Serializable key, int failTime, TimeUnit timeUnit)
    {
        return fileCacheProxy.get(key, failTime, timeUnit);
    }
    
    /**
     * 从缓存中读取所需数据
     * 
     * @param key
     * @return 键对应的数据
     */
    public static Object get(Serializable key)
    {
        return fileCacheProxy.get(key);
    }
    
    /**
     * 检查缓存对象中是否有某个键
     * 
     * @author nan.li
     * @param key
     * @return
     */
    public static boolean containsKey(Serializable key)
    {
        return fileCacheProxy.containsKey(key);
    }
    
    public boolean remove(Serializable key)
    {
        return fileCacheProxy.remove(key);
    }
    
    public Element removeAndReturnElement(Serializable key)
    {
        return fileCacheProxy.removeAndReturnElement(key);
    }
}
