package com.lnwazg.kit.proxy.chain;

import java.lang.reflect.Method;

/**
 * 代理类接口的实用模板
 * @author Administrator
 * @version 2016年5月2日
 */
public abstract class AbstractProxy implements Proxy
{
    @Override
    public final void doProxy(ProxyChain proxyChain)
    {
        Class<?> cls = proxyChain.getTargetClass();
        Method method = proxyChain.getTargetMethod();
        Object[] params = proxyChain.getMethodParams();
        //开场白
        begin();
        try
        {
            //此处为过滤器模式的核心代码
            /**
             * 最终的调用方式将形如( >代表before(), < 代表after()):
             * >
             *  >
             *   >
             *    >
             *     >
             *      >真正的方法
             *     <
             *    <
             *   <
             *  <
             * < 
             */
            if (filter(cls, method, params))
            {
                //若需要包裹，则执行本代理链
                //首先作权限验证
                if (!validPrivilege(cls, method, params))
                {
                    return;
                }
                //本轮开始
                before(cls, method, params);
                //本轮正式内容
                proxyChain.doProxyChain();
                //本轮结束
                after(cls, method, params);
            }
            else
            {
                //若无须包裹，则直接执行代理链的下一环即可！
                proxyChain.doProxyChain();
            }
        }
        catch (Throwable e)
        {
            error(cls, method, params, e);
        }
        finally
        {
            //结束辞
            end();
        }
    }
    
    /**
     * 验证访问者是否有权限访问该方法<br>
     * 默认情况下是有权访问的<br>
     * 可以根据实际需要进行权限过滤定制
     * @author nan.li
     * @param cls
     * @param method
     * @param params
     * @return
     */
    public boolean validPrivilege(Class<?> cls, Method method, Object[] params)
    {
        return true;
    }
    
    /**
     * 开始调用链式接口
     * @author Administrator
     */
    public void begin()
    {
    }
    
    /**
     * 该回调是否需要进行包裹。<br>
     * 默认是需要包裹的<br>
     * 若需要包裹，则会先执行before()，再执行after()
     * @author Administrator
     * @param cls
     * @param method
     * @param params
     * @return
     */
    public boolean filter(Class<?> cls, Method method, Object[] params)
    {
        return true;
    }
    
    /**
     * 执行前
     * @author Administrator
     * @param cls
     * @param method
     * @param params
     */
    public void before(Class<?> cls, Method method, Object[] params)
    {
    }
    
    /**
     * 执行后
     * @author Administrator
     * @param cls
     * @param method
     * @param params
     */
    public void after(Class<?> cls, Method method, Object[] params)
    {
    }
    
    /**
     * 出错的时候
     * @author Administrator
     * @param cls
     * @param method
     * @param params
     * @param e
     */
    public void error(Class<?> cls, Method method, Object[] params, Throwable e)
    {
    }
    
    /**
     * 最终完成链式回调
     * @author Administrator
     */
    public void end()
    {
    }
}