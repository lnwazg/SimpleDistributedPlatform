package com.lnwazg.kit.serialization.protostuff;

import java.io.Serializable;

import com.lnwazg.kit.gson.GsonKit;

/**
 * 内部专用容器，仅用于存放序列化后的对象数据，以及根据数据还原成原始对象<br>
 * 该容器的作用，是将一个不可序列化的对象做相互转换
 * @author nan.li
 * @version 2017年4月8日
 */
public class ObjectJsonStrContainer implements Serializable
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
    public ObjectJsonStrContainer(Object obj)
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
    
    /**
     * 获取JSON字符串
     * @author nan.li
     * @return
     */
    public String getJsonStr()
    {
        return objStr;
    }
}