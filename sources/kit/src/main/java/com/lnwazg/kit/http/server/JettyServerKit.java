package com.lnwazg.kit.http.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * 内嵌式服务器
 * @author Administrator
 * @version 2016年4月17日
 */
public class JettyServerKit
{
    /**
     * 启动本地的http资源服务器
     * @author Administrator
     * @param port    8080
     * @param contextPath   /image
     * @param resourceBase    c:/images
     */
    public static void startLocalResourceServer(int port, String contextPath, String resourceBase)
    {
        try
        {
            Server server = new Server(port);
            //            server.setAttribute("useFileMappedBuffer", false);
            WebAppContext context = new WebAppContext();
            //            context.setAttribute("useFileMappedBuffer", false);
            context.setContextPath(contextPath);
            context.setResourceBase(resourceBase);
            //            ResourceHandler resourceHandler = new ResourceHandler();
            //            resourceHandler.setDirectoriesListed(true);
            //            resourceHandler.setResourceBase(resourceBase);
            //            resourceHandler.setStylesheet("");
            //            HandlerList handlers = new HandlerList();
            //            handlers.setHandlers(new Handler[] {resourceHandler, new DefaultHandler()});
            //            server.setHandler(handlers);
            server.setHandler(context);
            server.start();
            System.out.println(String.format("Http Server has started, please click the following link to visit:\nhttp://127.0.0.1:%d%s\n", port, contextPath));
            //            server.join();
            //            如果server没有起来，这里面join()函数起到的作用就是使线程阻塞， 这里join()函数实质上调用的jetty的线程池。(这里和Thread中的join函数相似)
            //            如果没有join()函数，jetty服务器也能正常启动或运行正常，是因为jetty比较小，启动速度非常快。
            //            然而如果你的application比较重的话， 调用join函数，能够保证你的server真正的起来。
            /**
             * PS： Thread的join方法的作用
            thread1.Join()//这样写是告诉大家它不是静态方法
            * 三个重载方法，基本功能相同，都是阻塞掉当前代码运行的线程，只有当thread1线程执行完毕，阻塞掉的线程才重新进入运行状态
            * ①thread1.join()，无参无返回值，如果调用该方法时，thread1终止，则返回，否则，被阻塞的线程就永远阻塞下去
            * ②thread1.join(int) 参数是超时时间（单位ms）,返回bool，如果在超时时间内thread1终止，则返回true，同时被阻塞的线程进入运行状态，
            * 否则，返回false，同时被阻塞的线程和thread1都处于运行状态，并且交替执行
            * ③thread1.join(timespan)同②。
            *线程中设计join方法的意义(个人理解)：实现了线程的顺序执行，而线程的顺序执行就有同步的意义，所以这里也可以看做线程同步的一种方式
                                    在网上看到有人说“将两个线程合并”。这样解释我觉得理解起来还更麻烦。不如就借鉴下API里的说法：
                “等待该线程终止。”
                                    解释一下，是主线程(我在“一”里已经命名过了)等待子线程的终止。也就是在子线程调用了join()方法后面的代码，只有等到子线程结束了才能执行。(Waits for this thread to die.)
             */
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
