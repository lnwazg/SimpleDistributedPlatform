package com.lnwazg.kit.cache;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.lnwazg.kit.gson.GsonKit;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 缓存类，当缓存文件过大的时候自动溢出到磁盘中,避免oom的问题！<br>
 * 适合存储大量的数据，但是对存取速度要求不那么高的情况<br>
 * 如果你不确定到底应该使用FileCacheLite还是MemCacheLite，那么此时应该直接选择FileCacheLite（对象必须实现Serializable接口），这是一个安全有效的选择！<br>
 * 实测，可稳定高速缓存100G的本地数据！惊喜吧！<br>
 * 不能序列化的对象可以使用除磁盘存储外Ehcache的所有功能。<br>
 * 此处存储的数据必须用到磁盘存储，否则就没有意义了。因此此处存储的数据必须都要实现Serializable接口！<br>
 * 使用该缓存对象存储的对象数量不宜过多（不超过1000w个double键值对，否则会吃光OLD GENERATION，默认OLD GENERATION最大为4G），但是每个对象的数量可以很大！<br>
 * 为了保证这个FileCache的持久稳定性，因此牺牲掉recordTimestampMap（这个map是基于内存的，因此引入的这个map虽然方便了按时间获取的功能，却引入了OOM的隐患）<br>
 * 若坚持采用recordTimestampMap，可将时间戳塞入到Element对象中。但是这样存储的对象变相地变大了，并且还会占用更大的磁盘空间。存取的吞吐量也受到了牵制<br>
 * 因此，后期可以引入一个开关变量，可以根据实际需要来决定是否记录recordTimestamp信息。一键开闭，爽快使用！
 * @author nan.li
 * @version 2016年4月26日
 */
public class FileCache
{
    private Cache cacheObj;
    
    //    /**
    //     * 记录的放入时间的map
    //     */
    //    Map<Serializable, Long> recordTimestampMap = new ConcurrentHashMap<>();
    //既然是文件缓存，那么必定需要存储大量的数据（千万级以上），那么ConcurrentHashMap这类的内存表肯定会被撑爆了！
    
    /**
     * 是否记录时间戳
     */
    private boolean recordTimestamp = false;
    
    /**
     * 构造函数 <br>
     * 采用随机的缓存文件名
     */
    public FileCache()
    {
        this(String.format("c_%s", System.currentTimeMillis()));//cache对象的名称。做到每次新建的时候均不重复
    }
    
    public FileCache(boolean recordTimestamp)
    {
        this(String.format("c_%s", System.currentTimeMillis()), recordTimestamp);
    }
    
    /**
     * 构造函数<br>
     * 采用指定的缓存文件名<br>
     * 默认不记录recordTimestamp 
     * @param cacheName
     */
    public FileCache(String cacheName)
    {
        this(cacheName, false);
    }
    
    /**
     * 构造函数<br>
     * 采用指定的缓存文件名，并且可以指定是否记录时间戳
     * @param cacheName
     */
    public FileCache(String cacheName, boolean recordTimestamp)
    {
        System.setProperty(net.sf.ehcache.CacheManager.ENABLE_SHUTDOWN_HOOK_PROPERTY, "true");
        String cache_name = cacheName;
        final CacheManager manager = CacheManager.getInstance();
        cacheObj = manager.getCache(cache_name);
        this.recordTimestamp = recordTimestamp;
        if (cacheObj == null)
        {
            //缓存若为空，则新建一个
            //内存中留的缓存数量很少，大部分的缓存都要写入到磁盘里！
            
            //            name - 缓存的名称，default 保留为默认缓存名称；
            //            maxElementsInMemory - 内存中的最大同时缓存元素个数；
            //            overflowToDisk - 是否持久化（使用磁盘）；
            //            eternal - 对象是否永久有效（永不过期）；
            //            timeToLiveSeconds - 对象从其创建开始计算的生存时间（秒）；
            //            timeToIdleSeconds - 对象从其最后一次被访问开始计算的生存时间（秒）。
            
            //            ehcache.xml配置参数说明：
            //
            //            name：缓存名称。
            //            maxElementsInMemory：缓存最大个数。
            //            eternal：缓存中对象是否为永久的，如果是，超时设置将被忽略，对象从不过期。
            //            timeToIdleSeconds：设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
            //            timeToLiveSeconds：缓存数据的生存时间（TTL），也就是一个元素从构建到消亡的最大时间间隔值，这只能在元素不是永久驻留时有效，如果该值是0就意味着元素可以停顿无穷长的时间。
            //            maxEntriesLocalDisk：当内存中对象数量达到maxElementsInMemory时，Ehcache将会对象写到磁盘中。
            //            overflowToDisk：内存不足时，是否启用磁盘缓存。
            //            diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。
            //            maxElementsOnDisk：硬盘最大缓存个数。
            //            diskPersistent：是否在VM重启时存储硬盘的缓存数据。默认值是false。
            //            diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。
            //            memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）。
            //            clearOnFlush：内存数量最大时是否清除。
            
            Cache cache = new Cache(cache_name, 768, true, true, 0, 0);
            manager.addCache(cache);
        }
        else
        {
            //否则，便清空缓存。防止脏数据的存在！
            cacheObj.removeAll();
        }
        //增加缓存系统关闭钩子 add by linan 2012-09-08
        //  必须调用System.exit(0);才能正常触发关闭钩子！否则钩子无效！
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            public void run()
            {
                //                manager.shutdown();
                List<CacheManager> knownCacheManagers = CacheManager.ALL_CACHE_MANAGERS;
                while (!knownCacheManagers.isEmpty())
                {
                    // 关闭的时候 刷到磁盘  
                    ((CacheManager)CacheManager.ALL_CACHE_MANAGERS.get(0)).shutdown();
                }
            }
        }));
        cacheObj = manager.getCache(cache_name);
    }
    
    public Cache getCacheObj()
    {
        return cacheObj;
    }
    
    /**
     * 将对象放入缓存，如果是对象类型，那么该对象必须是可序列化的<br>
     * EhCache在put对象时，该对象必须是可序列化（Serializable）的类型
     * @param key
     * @param value
     * @see [类、类#方法、类#成员]
     */
    public void put(Serializable key, Serializable value)
    {
        if (recordTimestamp)
        {
            cacheObj.put(new Element(key, new ImmutablePair<Long, Serializable>(System.currentTimeMillis(), value)));
        }
        else
        {
            cacheObj.put(new Element(key, value));
        }
    }
    
    /**
     * 将对象放入缓存<br>
     * 如果该Object是不可序列化的，那么会自动将其转为json String
     * @author nan.li
     * @param key
     * @param value
     */
    public void put(Serializable key, Object value)
    {
        if (recordTimestamp)
        {
            cacheObj.put(new Element(key, new ImmutablePair<Long, Serializable>(System.currentTimeMillis(), serial(value))));
        }
        else
        {
            cacheObj.put(new Element(key, serial(value)));
        }
    }
    
    /**
     * 将具体对象序列化为内部可序列化的对象ObjJsonContainer
     * @author nan.li
     * @param value
     * @return
     */
    private Serializable serial(Object obj)
    {
        return new ObjJsonContainer(obj);
    }
    
    /**
     * 将ObjJsonContainer反序列化为具体对象
     * @author nan.li
     * @param container
     * @return
     */
    private Object deserial(ObjJsonContainer container)
    {
        return container.getObject();
    }
    
    /**
     * 内部专用容器，仅用于存放序列化后的对象数据，以及根据数据还原成原始对象<br>
     * 该容器的作用，是将一个不可序列化的对象做相互转换
     * @author nan.li
     * @version 2017年4月8日
     */
    private static class ObjJsonContainer implements Serializable
    {
        private static final long serialVersionUID = 1L;
        
        /**
         * 对象json化后的数据
         */
        private String objStr;
        
        /**
         * 对象所属的clazz信息
         */
        private Class<?> objClazz;
        
        /**
         * 构造函数 
         * @param obj
         */
        public ObjJsonContainer(Object obj)
        {
            this.objStr = GsonKit.gson.toJson(obj);
            this.objClazz = obj.getClass();
        }
        
        /**
         * 还原对象信息
         * @author nan.li
         * @return
         */
        public Object getObject()
        {
            return GsonKit.gson.fromJson(objStr, objClazz);
        }
    }
    
    /**
     * 即使过期了，也不想删掉。因为还可能不按照过期时间来取
     * @author nan.li
     * @param key
     * @param failTime
     * @param timeUnit
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object get(Serializable key, int failTime, TimeUnit timeUnit)
    {
        if (recordTimestamp)
        {
            // 如果缓存表中压根就没有，那么忽略之
            Element element = cacheObj.get(key);
            if (element == null)
            {
                return null;
            }
            
            // 当前时间
            long now = System.currentTimeMillis();
            // 放入的时间
            long putTime = ((ImmutablePair<Long, Serializable>)element.getObjectValue()).getLeft();
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
                Object thatObj = ((ImmutablePair<Long, Serializable>)element.getObjectValue()).getRight();
                if (thatObj instanceof ObjJsonContainer)
                {
                    thatObj = deserial((ObjJsonContainer)thatObj);
                }
                return thatObj;
            }
        }
        else
        {
            throw new CacheMethodNotSupportedException("recordTimestamp变量未打开，因此不提供根据过期时间去获取缓存对象的方法");
        }
    }
    
    /**
     * 从缓存中读取所需数据
     * 
     * @param key
     * @return 键对应的数据
     */
    @SuppressWarnings("unchecked")
    public Object get(Serializable key)
    {
        Element element = cacheObj.get(key);
        if (null != element)
        {
            Object thatObj = null;
            if (recordTimestamp)
            {
                thatObj = ((ImmutablePair<Long, Serializable>)element.getObjectValue()).getRight();
            }
            else
            {
                thatObj = element.getObjectValue();
            }
            if (thatObj instanceof ObjJsonContainer)
            {
                thatObj = deserial((ObjJsonContainer)thatObj);
            }
            return thatObj;
        }
        return null;
    }
    
    /**
     * 检查缓存对象中是否有某个键
     * 
     * @author nan.li
     * @param key
     * @return
     */
    public boolean containsKey(Serializable key)
    {
        return get(key) != null;
    }
    
    public boolean remove(Serializable key)
    {
        return cacheObj.remove(key);
    }
    
    public Element removeAndReturnElement(Serializable key)
    {
        return cacheObj.removeAndReturnElement(key);
    }
    
    /**
     * 缓存方法不支持异常
     * @author nan.li
     * @version 2016年11月17日
     */
    static class CacheMethodNotSupportedException extends RuntimeException
    {
        private static final long serialVersionUID = -1;
        
        public CacheMethodNotSupportedException(String message)
        {
            super(message);
        }
    }
}
