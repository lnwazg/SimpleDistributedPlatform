package com.lnwazg.kit.proxy.callback;

import java.lang.reflect.Method;

/**
 * 自定义的方法，用于插入被代理方法执行的前后
 * @author nan.li
 * @version 2016年4月28日
 */
@FunctionalInterface
public interface CustomMethod
{
    void execute(Object obj, Method method, Object[] args);
}
