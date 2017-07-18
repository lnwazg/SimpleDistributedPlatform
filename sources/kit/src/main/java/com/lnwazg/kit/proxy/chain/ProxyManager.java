package com.lnwazg.kit.proxy.chain;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 代理管理器<br>
 * 在 ProxyManager 中，定义了两个成员变量，targetClass 表示目标类，proxyList 也就是代理列表了。<br>
 * 通过一个简单的构造器，将这两个成员变量进行初始化。<br>
 * 
 * 
 * 最后提供一个 createProxy() 方法，创建代理对象。<br>
 * 在该方法中，封装了 CGLib 的 Enhancer 类，只需提供两个参数：目标类与拦截器。<br>
 * 后者在 CGLib 中称为 Callback。<br>
 * 特别要注意第二个参数，这里使用了匿名内部类的方式进行实现。<br>
 * 通过一个匿名内部类来实现 CGLib 的 MethodInterceptor 接口，并填充 intercept() 方法。<br>
 * 将该方法的所有入口参数都传递到创建的 ProxyChain 对象中，外加该类的两个成员变量：targetClass 与 proxyList。<br>
 * 然后调用 ProxyChain 的 doProxyChain() 方法，可以想象，调用是一连串的，当调用完毕后，可直接获取方法返回值。
 * @author Administrator
 * @version 2016年5月2日
 */
public class ProxyManager
{
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<?> targetClass, List<Proxy> proxyList)
    {
        return (T)Enhancer.create(targetClass, new MethodInterceptor()
        {
            @Override
            public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy)
                throws Throwable
            {
                ProxyChain proxyChain = new ProxyChain(targetClass, target, method, args, proxy, proxyList);
                proxyChain.doProxyChain();
                return proxyChain.getMethodResult();
            }
        });
    }
}