package com.lnwazg.kit.testframework;

/**
 * 测试框架的简写法<br>
 * 思想同jQuery的$
 * @author Administrator
 * @version 2016年4月17日
 */
public class TF
{
    public static void l(Class<?> targetClass)
    {
        TestFramework.loadTest(targetClass);
    }
}
