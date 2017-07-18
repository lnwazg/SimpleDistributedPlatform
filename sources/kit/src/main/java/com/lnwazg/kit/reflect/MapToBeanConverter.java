package com.lnwazg.kit.reflect;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 将map转换成Bean的实用工具
 * @author nan.li
 * @version 2016年4月15日
 */
public class MapToBeanConverter
{
    
    /**
     * 将一个map转换为一个bean对象
     * @author nan.li
     * @param targetClass  待被转换的目标类
     * @param paramMap    参数map
     * @param arrays  映射数组，只有在此声明的属性将会被映射转换。可映射多个映射关系对,例如 new String[] {"categoryId", "category_id" }, new String[] { "categoryName", "category_name"}
     * @return
     */
    public static <T> T convert(Class<T> targetClass, Map<?, ?> paramMap, String[]... arrays)
    {
        try
        {
            T t = targetClass.newInstance();
            if (arrays.length > 0)
            {
                for (String[] mappingPair : arrays)
                {
                    if (mappingPair.length == 2)
                    {
                        String beanPropName = mappingPair[0];//左边是java Bean的属性的name
                        String mapKeyName = mappingPair[1];//右边是map中的key的名称
                        if (StringUtils.isNotEmpty(beanPropName) && StringUtils.isNoneEmpty(mapKeyName))
                        {
                            BeanUtils.setProperty(t, beanPropName, paramMap.get(mapKeyName));
                        }
                    }
                }
            }
            return t;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将指定的参数进行转换<br>
     * map中的key名称和java Bean的字段名称完全一致。只有声明了的key会被转换
     * @author nan.li
     * @param targetClass  被转换的目标类
     * @param paramMap   参数map
     * @param toBeConvertedNames  待转换的参数的名称数组
     * @return
     */
    public static <T> T convertByNameArray(Class<T> targetClass, Map<?, ?> paramMap, String... toBeConvertedNames)
    {
        try
        {
            T t = targetClass.newInstance();
            if (toBeConvertedNames.length > 0)
            {
                for (String keyName : toBeConvertedNames)
                {
                    if (StringUtils.isNotEmpty(keyName))
                    {
                        BeanUtils.setProperty(t, keyName, paramMap.get(keyName));
                    }
                }
            }
            return t;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * map中的key名称和java Bean的字段名称完全一致。此时会将map中的所有属性转换到java bean里
     * @author nan.li
     * @param targetClass  被转换的目标类
     * @param paramMap   参数map
     * @param toBeConvertedNames  待转换的参数的名称数组
     * @return
     */
    public static <T> T convertAllByMap(Class<T> targetClass, Map<?, ?> paramMap)
    {
        try
        {
            T t = targetClass.newInstance();
            if (paramMap != null && !paramMap.isEmpty())
            {
                Set<?> keyset = paramMap.keySet();
                for (Object key : keyset)
                {
                    if (key != null)
                    {
                        String keyStr = key.toString();
                        BeanUtils.setProperty(t, keyStr, paramMap.get(key));
                    }
                }
            }
            return t;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
}
