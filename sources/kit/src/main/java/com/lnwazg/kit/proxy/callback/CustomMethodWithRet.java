package com.lnwazg.kit.proxy.callback;

import java.lang.reflect.Method;

/**
 * 自定义的方法，带有返回值
 * @author nan.li
 * @version 2016年4月28日
 */
@FunctionalInterface
public interface CustomMethodWithRet
{
    boolean execute(Object obj, Method method, Object[] args);
}
