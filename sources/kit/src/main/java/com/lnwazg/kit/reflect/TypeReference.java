package com.lnwazg.kit.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 类型的引用<br>
 * 因为泛型的类型无法直接表示出来，因此必须“曲线救国”，用先构造一个类型引用的对象，然后从该对象中去获取实际的类型<br>
 * 借助于该类，可以表示任意类型的Type实例
 * @author nan.li
 * @version 2016年10月11日
 */
public class TypeReference<T>
{
    private final Type type;
    
    /**
     * 构造函数<br>
     * 该类型信息必须要从一个具体的子类中去获取。如果从不从子类里面去获取类型信息，那么就只能获得到TypeReference这个当前类的信息。因此将构造函数设置为protected，以确保实例化的都是子类
     */
    protected TypeReference()
    {
        //获取参数化类型类
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>)
        {
            //不是一个参数化的类型
            throw new RuntimeException("Missing type parameter.");
        }
        //获取参数化类型类中的泛型类型Type数据
        type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
    }
    
    /**
     * 获取泛型参数里面的具体类型的信息
     * @author nan.li
     * @return
     */
    public Type getType()
    {
        return type;
    }
    
    public final static Type LIST_STRING = new TypeReference<List<String>>()
    {
    }.getType();
    
    public static void main(String[] args)
    {
        System.out.println(LIST_STRING);
        System.out.println(new TypeReference<List<Map<String, String>>>()
        {
        }.getType());
    }
}
