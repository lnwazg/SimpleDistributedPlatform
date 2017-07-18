package com.lnwazg.myzoo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.lnwazg.myzoo.bean.Msg;

/**
 * 要注册的kryo传输Class的列表
 * @author nan.li
 * @version 2016年10月28日
 */
public class KyroResigterClassKit
{
    /**
     * 基础的待注册的类列表
     */
    private static Class<?>[] BASE_REGISTERED_CLASSES = new Class[] {char[].class, HashMap.class, HashSet.class,
        ArrayList.class, String.class, Object.class, StackTraceElement.class, StackTraceElement[].class,
        UnsupportedOperationException.class, ClassCastException.class};
        
    /**
     * 扩展的代注册的类列表
     */
    private static Class<?>[] EXTRA_REGISTERED_CLASSES = new Class[] {Msg.class, LinkedTreeMap.class};
    
    //    kryo.register(Object.class); // Needed for Object#toString, hashCode, etc.
    //    kryo.register(TestObject.class);
    //    kryo.register(MessageWithTestObject.class);
    //    kryo.register(StackTraceElement.class);
    //    kryo.register(StackTraceElement[].class);
    //    kryo.register(UnsupportedOperationException.class);
    //    kryo.setReferences(true); // Needed for UnsupportedOperationException, which has a circular reference in the cause field.
    //    ObjectSpace.registerClasses(kryo);
    
    /**
     * 待被注册的类列表大全
     */
    public static Class<?>[] TO_BE_REGISTERED_CLASSES = ArrayUtils.addAll(BASE_REGISTERED_CLASSES, EXTRA_REGISTERED_CLASSES);
    
    public static void main(String[] args)
    {
        for (Class<?> clazz : TO_BE_REGISTERED_CLASSES)
        {
            System.out.println(clazz);
        }
    }
}
