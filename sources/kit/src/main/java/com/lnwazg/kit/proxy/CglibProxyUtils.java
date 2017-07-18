package com.lnwazg.kit.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.lnwazg.kit.proxy.callback.CustomMethod;
import com.lnwazg.kit.proxy.callback.CustomMethodWithRet;
import com.lnwazg.kit.proxy.chain.Proxy;
import com.lnwazg.kit.proxy.chain.ProxyManager;
import com.lnwazg.kit.proxy.constant.ProxyFlag;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

/**
 * 动态字节码生成器的代理工具<br>
 * 可代理任意对象，生成一个代理对象，并且给这个被代理对象的的所有方法上都环绕一前一后两个方法<br>
 * CGLIB动态代理的原理就是用Enhancer生成一个原有类的子类，并且设置好callback到proxy， 则原有类的每个方法调用都会转为调用实现了MethodInterceptor接口的proxy的intercept() 函数<br>
 * cglib很强大，但最重要的是使用场景！<br>
 * 注解很强大，注解很多情况下是一种超棒的程序粘合剂，可大大改观程序的臃肿度！
 * @author nan.li
 * @version 2016年4月28日
 */
public class CglibProxyUtils
{
    /**
     * 在某个类的前后增加代理方法
     * @author Administrator
     * @param t
     * @param before
     * @param after
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxySurround(T t, CustomMethod before, CustomMethod after)
    {
        MethodInterceptor methodInterceptor = new MethodInterceptor()
        {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                throws Throwable
            {
                before.execute(obj, method, args);
                Object result = null;
                try
                {
                    result = proxy.invokeSuper(obj, args);
                }
                finally
                {
                    after.execute(obj, method, args);
                }
                return result;
            }
        };
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        // 回调方法
        enhancer.setCallback(methodInterceptor);
        // 创建代理对象
        return (T)enhancer.create();
    }
    
    /**
     * 链式代理
     * @author Administrator
     * @param originalClass
     * @param proxyList
     * @return
     */
    public static <T> T proxySurroundByChain(T t, Proxy... proxys)
    {
        return ProxyManager.createProxy(t.getClass(), Arrays.asList(proxys));
    }
    
    /**
     * 链式代理
     * @author Administrator
     * @param t
     * @param proxyList
     * @return
     */
    public static <T> T proxySurroundByChain(T t, List<Proxy> proxyList)
    {
        return ProxyManager.createProxy(t.getClass(), proxyList);
    }
    
    /**
     * 代理某一个对象指定方法，并在这个方法执行前后加入新方法，可指定过滤掉非代理的方法
     * @author nan.li
     * @param t
     * @param before 执行目标方法前要执行的方法
     * @param after  执行目标方法后要执行的方法
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxySurround(T t, CustomMethod before, CustomMethod after, CallbackFilter callbackFilter)
    {
        MethodInterceptor methodInterceptor = new MethodInterceptor()
        {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                throws Throwable
            {
                before.execute(obj, method, args);
                Object result = null;
                try
                {
                    result = proxy.invokeSuper(obj, args);
                }
                finally
                {
                    after.execute(obj, method, args);
                }
                return result;
            }
        };
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        // 回调方法
        //        enhancer.setCallback(methodInterceptor);
        enhancer.setCallbacks(new Callback[] {methodInterceptor, NoOp.INSTANCE});
        //NoOp回调把对方法的调用直接委派到这个方法在父类中的实现。
        //Methods using this Enhancer callback ( NoOp.INSTANCE ) will delegate directly to the default (super) implementation in the base class.
        //setCallbacks中定义了所使用的拦截器，其中NoOp.INSTANCE是CGlib所提供的实际是一个没有任何操作的拦截器， 他们是有序的。一定要和CallbackFilter里面的顺序一致。
        enhancer.setCallbackFilter(callbackFilter);
        // 创建代理对象
        return (T)enhancer.create();
    }
    
    /**
     * 代理一个对象<br>
     * 仅代理其列出的几个方法
     * @author Administrator
     * @param t
     * @param before
     * @param after
     * @param methodNames
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxySurroundIncludes(T t, CustomMethod before, CustomMethod after, String... methodNames)
    {
        MethodInterceptor methodInterceptor = new MethodInterceptor()
        {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                throws Throwable
            {
                before.execute(obj, method, args);
                Object result = null;
                try
                {
                    result = proxy.invokeSuper(obj, args);
                }
                finally
                {
                    after.execute(obj, method, args);
                }
                return result;
            }
        };
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        enhancer.setCallbacks(new Callback[] {methodInterceptor, NoOp.INSTANCE});
        enhancer.setCallbackFilter(new CallbackFilter()
        {
            @Override
            public int accept(Method paramMethod)
            {
                //仅判断代理的情况
                String name = paramMethod.getName();
                if (ArrayUtils.contains(methodNames, name))
                {
                    return ProxyFlag.DO_PROXY.get();
                }
                //默认不代理
                return ProxyFlag.DO_NOT_PROXY.get();
            }
        });
        // 创建代理对象
        return (T)enhancer.create();
    }
    
    /**
     * 代理一个对象<br>
     * 过滤掉几个不想代理的方法
     * @author Administrator
     * @param t
     * @param before
     * @param after
     * @param methodNames
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxySurroundExcludes(T t, CustomMethod before, CustomMethod after, String... methodNames)
    {
        MethodInterceptor methodInterceptor = new MethodInterceptor()
        {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                throws Throwable
            {
                before.execute(obj, method, args);
                Object result = null;
                try
                {
                    result = proxy.invokeSuper(obj, args);
                }
                finally
                {
                    after.execute(obj, method, args);
                }
                return result;
            }
        };
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        enhancer.setCallbacks(new Callback[] {methodInterceptor, NoOp.INSTANCE});//设置多个代理对象。NoOp.INSTANCE是一个空代理
        enhancer.setCallbackFilter(new CallbackFilter()
        {
            @Override
            public int accept(Method paramMethod)
            {
                //CallbackFilter可以实现不同的方法使用不同的回调方法。所以CallbackFilter称为"回调选择器"更合适一些。
                //CallbackFilter中的accept方法，根据不同的method返回不同的值i，这个值是在callbacks中callback对象的序号，就是调用了callbacks[i]。
                String name = paramMethod.getName();
                if (ArrayUtils.contains(methodNames, name))
                {
                    return ProxyFlag.DO_NOT_PROXY.get();
                }
                return ProxyFlag.DO_PROXY.get();
            }
        });
        // 创建代理对象
        return (T)enhancer.create();
    }
    
    /**
     * 根据权限验证来设置代理
     * @author Administrator
     * @param t
     * @param checkPrivilege
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxyByPrivilege(T t, CustomMethodWithRet checkPrivilege)
    {
        MethodInterceptor methodInterceptor = new MethodInterceptor()
        {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                throws Throwable
            {
                boolean checkResult = checkPrivilege.execute(obj, method, args);//权限验证的结果
                if (checkResult)
                {
                    //验证通过，才执行这个方法
                    Object result = null;
                    result = proxy.invokeSuper(obj, args);
                    return result;
                }
                else
                {
                    return null;
                }
            }
        };
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        // 回调方法
        enhancer.setCallback(methodInterceptor);
        // 创建代理对象
        return (T)enhancer.create();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T proxyByPrivilegeIncludes(T t, CustomMethodWithRet checkPrivilege, String... methodNames)
    {
        MethodInterceptor methodInterceptor = new MethodInterceptor()
        {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                throws Throwable
            {
                boolean checkResult = checkPrivilege.execute(obj, method, args);//权限验证的结果
                if (checkResult)
                {
                    //验证通过，才执行这个方法
                    Object result = null;
                    result = proxy.invokeSuper(obj, args);
                    return result;
                }
                else
                {
                    return null;
                }
            }
        };
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        enhancer.setCallbacks(new Callback[] {methodInterceptor, NoOp.INSTANCE});
        enhancer.setCallbackFilter(new CallbackFilter()
        {
            @Override
            public int accept(Method paramMethod)
            {
                //仅判断代理的情况
                String name = paramMethod.getName();
                if (ArrayUtils.contains(methodNames, name))
                {
                    return ProxyFlag.DO_PROXY.get();
                }
                //默认不代理
                return ProxyFlag.DO_NOT_PROXY.get();
            }
        });
        // 创建代理对象
        return (T)enhancer.create();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T proxyByPrivilegeExcludes(T t, CustomMethodWithRet checkPrivilege, String... methodNames)
    {
        MethodInterceptor methodInterceptor = new MethodInterceptor()
        {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
                throws Throwable
            {
                boolean checkResult = checkPrivilege.execute(obj, method, args);//权限验证的结果
                if (checkResult)
                {
                    //验证通过，才执行这个方法
                    Object result = null;
                    result = proxy.invokeSuper(obj, args);
                    return result;
                }
                else
                {
                    return null;
                }
            }
        };
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        enhancer.setCallbacks(new Callback[] {methodInterceptor, NoOp.INSTANCE});
        enhancer.setCallbackFilter(new CallbackFilter()
        {
            @Override
            public int accept(Method paramMethod)
            {
                //仅判断代理的情况
                String name = paramMethod.getName();
                if (ArrayUtils.contains(methodNames, name))
                {
                    return ProxyFlag.DO_NOT_PROXY.get();
                }
                //默认不代理
                return ProxyFlag.DO_PROXY.get();
            }
        });
        // 创建代理对象
        return (T)enhancer.create();
    }
    
}
