package com.lnwazg.kit.testframework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;

import com.lnwazg.kit.testframework.anno.AfterEach;
import com.lnwazg.kit.testframework.anno.AfterFinalOnce;
import com.lnwazg.kit.testframework.anno.Benchmark;
import com.lnwazg.kit.testframework.anno.BenchmarkHigh;
import com.lnwazg.kit.testframework.anno.BenchmarkLow;
import com.lnwazg.kit.testframework.anno.BenchmarkMiddle;
import com.lnwazg.kit.testframework.anno.PrepareEach;
import com.lnwazg.kit.testframework.anno.PrepareStartOnce;
import com.lnwazg.kit.testframework.anno.TestCase;

/**
 * 微型测试框架<br>
 * 一个五脏俱全的极简型测试框架<br>
 * 无须依赖JUnit，避免引入Junit导致的maven编译报错<br>
 * 简单就是力量，简单就是核心竞争力，简单，就是sqlite能干掉access的重要原因！<br>
 * 使用各种各样的annotation loader，就可以加载各种各样的精巧的代码格式，达到简单化编码过程的目的！变相地增加了简单性<br>
 * 因此，可以这么说：Annotation就是简单化的驱动力！
 * @author nan.li
 * @version 2016年3月28日
 */
public class TestFramework
{
    /**
     * 加载目标类的所有标记了指定注解的方法
     * 
     * @author nan.li
     * @param targetClass
     */
    public static void loadTest(Class<?> targetClass)
    {
        try
        {
            Object obj = targetClass.newInstance();// 测试对象的实例
            Method[] methods = targetClass.getDeclaredMethods();// 获得所有的方法
            boolean hasTestMethods = false;
            
            //仅仅全局准备一次的
            List<Method> prepareOnceMethods = new ArrayList<Method>();
            for (Method method : methods)
            {
                if (method.isAnnotationPresent(PrepareStartOnce.class))
                {
                    method.setAccessible(true);
                    prepareOnceMethods.add(method);
                }
            }
            
            //执行全局方法
            if (prepareOnceMethods != null && prepareOnceMethods.size() > 0)
            {
                System.out.println(String.format(">>>>>>>>>>>>>>>>>>>>>  开始全局准备  >>>>>>>>>>>>>>>>>>>>>>>"));
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                for (Method m : prepareOnceMethods)
                {
                    System.out.println(String.format(">>>执行@PrepareStartOnce方法： %s  >>>", m.getName()));
                    m.invoke(obj);
                }
                System.out.println(String.format("【测试共计耗时 %s 毫秒】", stopWatch.getTime()));
                System.out.println(String.format("<<<<<<<<<<<<<<<<<<<<<  全局准备结束！   <<<<<<<<<<<<<<<<<<<<<<<\n"));
            }
            
            //每个方法执行前都要准备的
            List<Method> prepareEachMethods = new ArrayList<Method>();
            for (Method method : methods)
            {
                if (method.isAnnotationPresent(PrepareEach.class))
                {
                    method.setAccessible(true);
                    prepareEachMethods.add(method);
                }
            }
            
            List<Method> afterEachMethods = new ArrayList<Method>();
            for (Method method : methods)
            {
                if (method.isAnnotationPresent(AfterEach.class))
                {
                    method.setAccessible(true);
                    afterEachMethods.add(method);
                }
            }
            
            for (Method method : methods)
            {
                if (method.isAnnotationPresent(TestCase.class))
                {
                    hasTestMethods = true;
                    System.out.println(String.format(">>>>>>>>>>>>>>>>>>>>>  开始测试方法： %s  >>>>>>>>>>>>>>>>>>>>>>>", method.getName()));
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    
                    int times = 1;//总运行次数。默认为1次，即不运行性能测试
                    if (method.isAnnotationPresent(BenchmarkLow.class))
                    {
                        //低
                        times = 1000;
                        System.out.println(String.format("【准备开始benchmark，循环运行该方法  %s 次...】", times));
                    }
                    else if (method.isAnnotationPresent(BenchmarkMiddle.class))
                    {
                        //中
                        times = 1000 * 100;
                        System.out.println(String.format("【准备开始benchmark，循环运行该方法  %s 次...】", times));
                    }
                    else if (method.isAnnotationPresent(BenchmarkHigh.class))
                    {
                        //高
                        times = 1000 * 100 * 100;
                        System.out.println(String.format("【准备开始benchmark，循环运行该方法  %s 次...】", times));
                    }
                    else if (method.isAnnotationPresent(Benchmark.class))
                    {
                        //自定义次数
                        times = method.getAnnotation(Benchmark.class).value();
                        System.out.println(String.format("【准备开始benchmark，循环运行该方法  %s 次...】", times));
                    }
                    
                    for (int i = 0; i < times; i++)
                    {
                        method.setAccessible(true);
                        if (prepareEachMethods != null && prepareEachMethods.size() > 0)
                        {
                            for (Method m : prepareEachMethods)
                            {
                                System.out.println(String.format(">>>>>>>>>执行@PrepareEach方法： %s ", m.getName()));
                                m.invoke(obj);
                            }
                        }
                        
                        //如果该测试的方法有参数，那么全部传空
                        int count = method.getParameterCount();
                        Object[] args = new Object[count];
                        method.invoke(obj, args);
                        
                        if (afterEachMethods != null && afterEachMethods.size() > 0)
                        {
                            for (Method m : afterEachMethods)
                            {
                                System.out.println(String.format(">>>>>>>>>执行@AfterEach方法： %s ", m.getName()));
                                m.invoke(obj);
                            }
                        }
                    }
                    long costTime = stopWatch.getTime();
                    System.out.println(String.format("【总计测试了 %s 次， 总计耗时 %s 毫秒，平均每次运行耗时 %s 毫秒，方法调用速度为 %.2f 次/秒 (TPS)】",
                        times,
                        costTime,
                        costTime * 1.0D / times,
                        1000 / (costTime * 1.0D / times)));
                    System.out.println(String.format("<<<<<<<<<<<<<<<<<<<<<  %s 方法测试结束！   <<<<<<<<<<<<<<<<<<<<<<<\n", method.getName()));
                }
            }
            if (!hasTestMethods)
            {
                System.out.println("未能找到任何加了@TestCase注解的方法，忽略测试");
            }
            
            //最终结束的时候执行一次
            List<Method> afterFinalOnceMethods = new ArrayList<Method>();
            for (Method method : methods)
            {
                if (method.isAnnotationPresent(AfterFinalOnce.class))
                {
                    method.setAccessible(true);
                    afterFinalOnceMethods.add(method);
                }
            }
            
            //执行全局方法
            
            if (afterFinalOnceMethods != null && afterFinalOnceMethods.size() > 0)
            {
                System.out.println(String.format(">>>>>>>>>>>>>>>>>>>>>  开始全局Finalize  >>>>>>>>>>>>>>>>>>>>>>>"));
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                for (Method m : afterFinalOnceMethods)
                {
                    System.out.println(String.format(">>>执行@AfterFinalOnce方法： %s  >>>", m.getName()));
                    m.invoke(obj);
                }
                System.out.println(String.format("【测试共计耗时 %s 毫秒】", stopWatch.getTime()));
                System.out.println(String.format("<<<<<<<<<<<<<<<<<<<<<  全局Finalize结束！   <<<<<<<<<<<<<<<<<<<<<<<\n"));
            }
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }
}
