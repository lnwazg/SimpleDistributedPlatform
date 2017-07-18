package com.lnwazg.kit.singleton;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.lnwazg.kit.cache.JvmMemCacheLite;
import com.lnwazg.kit.cache.key.JvmMemCacheKey;

/**
 * 单例管理器<br>
 * 即开即用，最简化<br>
 * 注入的工具包
 * @author Administrator
 * @version 2016年4月15日
 */
public class BeanMgr
{
    private static Map<Class<?>, Object> SingletonMap = new HashMap<>();
    
    /**
     * 存储一个类
     * @author Administrator
     * @param t
     */
    public static <T> void put(T t)
    {
        SingletonMap.put(t.getClass(), t);
    }
    
    public static <T> void put(Class<T> clazz, T t)
    {
        SingletonMap.put(clazz, t);
    }
    
    public static void putNative(Class<?> clazz, Object daoObject)
    {
        SingletonMap.put(clazz, daoObject);
    }
    
    /**
     * 仅查询某个key clazz是否有值，不做额外的实例化操作
     * @author nan.li
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T query(Class<T> clazz)
    {
        return (T)SingletonMap.get(clazz);
    }
    
    /**
     * 取出一个类的实例<br>
     * 假如查不到这个类的实例，则会返回一个默认的实例
     * @author Administrator
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz)
    {
        T t = (T)SingletonMap.get(clazz);
        if (t == null)
        {
            //为空，则返回默认的实例
            try
            {
                t = clazz.newInstance();
                put(t);
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return (T)SingletonMap.get(clazz);
    }
    
    /**
     * 为某种类型注入一堆对象
     * @author nan.li
     * @param clazz
     * @param objects
     */
    public static <T> void inject(Class<T> clazz, Object... objects)
    {
        try
        {
            T t = clazz.newInstance();
            injectByTypeAndAnno(t, objects);
            put(t);
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        
    }
    
    /**
     * 为某个对象注入一堆对象
     * @author nan.li
     * @param t
     * @param objects
     */
    public static <T> void inject(T t, Object... objects)
    {
        injectByTypeAndAnno(t, objects);
        put(t);
    }
    
    /**
     * 根据类型以及注解，进行注入操作
     * @author nan.li
     * @param t
     * @param objects
     */
    private static <T> void injectByTypeAndAnno(T t, Object... objects)
    {
        if (objects == null || objects.length == 0)
        {
            return;
        }
        //        @Resource
        //        UserDao userDao;
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>)t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(Resource.class))
            {
                //标明该字段需要被注入
                for (Object object : objects)
                {
                    Class<?> fieldType = field.getType();
                    //如果加了@Resource注解的字段是某个object的接口，那么就将该object注入到该字段里面
                    if (fieldType.isAssignableFrom(object.getClass()))
                    {
                        //两者一致，则执行注入操作！
                        try
                        {
                            field.setAccessible(true);
                            field.set(t, object);
                        }
                        catch (IllegalArgumentException | IllegalAccessException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 获取某个包下面的某个类的实例对象
     * @author Administrator
     * @param packageName
     * @param clazzName
     * @return
     */
    public static Object getBeanByClassName(String packageName, String clazzName)
    {
        String clazzFullName = String.format("%s.%s", packageName, clazzName);
        if (JvmMemCacheLite.get(clazzFullName) != null)
        {
            //此处做了缓存处理，避免了反射造成的性能瓶颈
            return JvmMemCacheLite.get(JvmMemCacheKey.classFullName.name() + clazzFullName);
        }
        Class<?> clazz;
        try
        {
            clazz = Class.forName(clazzFullName);
            Object o = get(clazz);
            JvmMemCacheLite.put(JvmMemCacheKey.classFullName.name() + clazzFullName, o);
            return o;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
