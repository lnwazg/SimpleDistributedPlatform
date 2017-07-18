package test.lnwazg.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.lnwazg.kit.proxy.CglibProxyUtils;
import com.lnwazg.kit.proxy.chain.AbstractProxy;
import com.lnwazg.kit.proxy.chain.Proxy;
import com.lnwazg.kit.proxy.constant.ProxyFlag;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.PrepareStartOnce;
import com.lnwazg.kit.testframework.anno.TestCase;

import net.sf.cglib.proxy.CallbackFilter;

/**
 * 最终测试动态代理所采用的方法
 * 
 * @author  lnwazg
 * @version  [版本号, 2012-1-4]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class TestCglib
{
    OriginalClass originalClass = null;
    
    Dog dog = null;
    
    @PrepareStartOnce
    void prepareStartOnce()
    {
        originalClass = new OriginalClass();
        dog = new Dog();
    }
    
    @TestCase
    void test1()
    {
        OriginalClass proxyWrapper = CglibProxyUtils.proxySurround(originalClass, (obj, method, argz) -> {
            System.out.println(String.format("开始调用%s...", method.getName()));
        } , (obj, method, argz) -> {
            System.out.println(String.format("调用%s结束！", method.getName()));
        } , new CallbackFilter()
        {
            @Override
            public int accept(Method paramMethod)
            {
                if ("addBook".equals(paramMethod.getName()))
                {
                    return ProxyFlag.DO_NOT_PROXY.get();
                }
                return ProxyFlag.DO_PROXY.get();
            }
        });
        proxyWrapper.addBook();
        proxyWrapper.delBook();
    }
    
    @TestCase
    void test2()
    {
        OriginalClass proxyWrapper = CglibProxyUtils.proxySurround(originalClass, (obj, method, argz) -> {
            System.out.println(String.format("开始调用%s...", method.getName()));
        } , (obj, method, argz) -> {
            System.out.println(String.format("调用%s结束！", method.getName()));
        });
        proxyWrapper.addBook();
        proxyWrapper.delBook();
    }
    
    @TestCase
    void test3()
    {
        OriginalClass proxyWrapper = CglibProxyUtils.proxySurroundIncludes(originalClass, (obj, method, argz) -> {
            System.out.println(String.format("开始调用%s...", method.getName()));
        } , (obj, method, argz) -> {
            System.out.println(String.format("调用%s结束！", method.getName()));
        } , "addBook");
        proxyWrapper.addBook();
        proxyWrapper.delBook();
    }
    
    @TestCase
    void test4()
    {
        OriginalClass proxyWrapper = CglibProxyUtils.proxySurroundExcludes(originalClass, (obj, method, argz) -> {
            System.out.println(String.format("开始调用%s...", method.getName()));
        } , (obj, method, argz) -> {
            System.out.println(String.format("调用%s结束！", method.getName()));
        } , "addBook");
        proxyWrapper.addBook();
        proxyWrapper.delBook();
    }
    
    @TestCase
    void test5()
    {
        OriginalClass proxyWrapper2 = CglibProxyUtils.proxyByPrivilege(originalClass, (obj, method, argz) -> {
            if ("addBook".equals(method.getName()))
            {
                //令addBook无权被调用
                return false;
            }
            return true;
        });
        proxyWrapper2.addBook();
        proxyWrapper2.delBook();
    }
    
    @TestCase
    void test6()
    {
        List<Proxy> proxyList = new ArrayList<Proxy>();
        proxyList.add(new AbstractProxy()
        {
            @Override
            public void before(Class<?> cls, Method method, Object[] params)
            {
                super.before(cls, method, params);
                System.out.println("this is before method");
            }
            
        });
        proxyList.add(new AbstractProxy()
        {
            
            @Override
            public void after(Class<?> cls, Method method, Object[] params)
            {
                super.after(cls, method, params);
                System.out.println("this is after method");
            }
        });
        
        //        OriginalClass proxyObj = ProxyManager.createProxy(originalClass.getClass(), proxyList);
        //        OriginalClass proxyObj = CglibProxy.proxySurroundByChain(originalClass, proxyList);
        //多重过滤器+权限控制！
        OriginalClass proxyObj = CglibProxyUtils.proxySurroundByChain(originalClass,
            new AbstractProxy()
            {
                @Override
                public void before(Class<?> cls, Method method, Object[] params)
                {
                    super.before(cls, method, params);
                    System.out.println("this is AAA before method");
                }
                
                @Override
                public boolean validPrivilege(Class<?> cls, Method method, Object[] params)
                {
                    if ("addBook".equals(method.getName()))
                    {
                        return true;
                    }
                    System.out.println("对不起，您无权访问" + method.getName() + "！");
                    return false;
                }
                
                @Override
                public void after(Class<?> cls, Method method, Object[] params)
                {
                    super.after(cls, method, params);
                    System.out.println("this is AAA after method");
                }
            },
            new AbstractProxy()
            {
                @Override
                public void before(Class<?> cls, Method method, Object[] params)
                {
                    super.before(cls, method, params);
                    System.out.println("this is BBB before method");
                }
                
                @Override
                public void after(Class<?> cls, Method method, Object[] params)
                {
                    super.after(cls, method, params);
                    System.out.println("this is BBB after method");
                }
            },
            
            new AbstractProxy()
            {
                @Override
                public void before(Class<?> cls, Method method, Object[] params)
                {
                    super.before(cls, method, params);
                    System.out.println("this is CCC before method");
                }
                
                @Override
                public void after(Class<?> cls, Method method, Object[] params)
                {
                    super.after(cls, method, params);
                    System.out.println("this is CCC after method");
                }
            });
        proxyObj.addBook();
        proxyObj.delBook();
    }
    
    public static void main(String[] args)
    {
        TF.l(TestCglib.class);
    }
}
