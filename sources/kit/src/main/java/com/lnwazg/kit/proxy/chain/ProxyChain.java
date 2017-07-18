package com.lnwazg.kit.proxy.chain;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理链<br>
 * 先判断当前指针有没有走完所有的 proxyList，若没有走完，则从 proxyList 中获取一个 Proxy，
 * 同时让指针往后走一步（指向下一个 Proxy），然后调用 Proxy 的 doProxy() 方法，此时需要将 this（也就是 ProxyChain 实例）传递到该方法中；<br>
 * 若已经走完了，使用 CGLib API 调用目标方法，并初始化方法返回值。
 * <br>
 * 之所以采用这个类，是因为Cglib框架无法支持多重代理（在代理之上的代理）
 * @author Administrator
 * @version 2016年5月2日
 */
public class ProxyChain
{
    /**
     * 用于封装所有的 Proxy，它就是 ProxyChain 的数据载体。
     */
    private List<Proxy> proxyList;
    
    /**
     * 相当于 proxyList 的当前指针，后面可以看到，它是可以往后走动的
     */
    private int currentProxyIndex = 0;
    
    private Class<?> targetClass;
    
    private Object targetObject;
    
    private Method targetMethod;
    
    private Object[] methodParams;
    
    private MethodProxy methodProxy;
    
    private Object methodResult;
    
    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, Object[] methodParams, MethodProxy methodProxy, List<Proxy> proxyList)
    {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodParams = methodParams;
        this.methodProxy = methodProxy;
        this.proxyList = proxyList;
    }
    
    public Class<?> getTargetClass()
    {
        return targetClass;
    }
    
    public Object getTargetObject()
    {
        return targetObject;
    }
    
    public Method getTargetMethod()
    {
        return targetMethod;
    }
    
    public Object[] getMethodParams()
    {
        return methodParams;
    }
    
    public MethodProxy getMethodProxy()
    {
        return methodProxy;
    }
    
    public Object getMethodResult()
    {
        return methodResult;
    }
    
    /**
     * 执行代理链<br>
     * 这里其实是一个递归调用<br>
     * 想理解透彻，就将其想象成一个异步递归算法即可
     * @author Administrator
     */
    public void doProxyChain()
    {
        if (proxyList == null || proxyList.size() == 0)
        {
            return;
        }
        if (currentProxyIndex < proxyList.size())
        {
            //如果还没到头，就执行当前位置的代理
            proxyList.get(currentProxyIndex++).doProxy(this);
        }
        else
        {
            //如果已经到头了，则执行最终的被代理对象的方法，并记录下方法的返回值
            try
            {
                methodResult = methodProxy.invokeSuper(targetObject, methodParams);
            }
            catch (Throwable throwable)
            {
                throw new RuntimeException(throwable);
            }
        }
    }
}