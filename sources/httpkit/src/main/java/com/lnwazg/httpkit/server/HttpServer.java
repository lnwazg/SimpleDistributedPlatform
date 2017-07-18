package com.lnwazg.httpkit.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.lnwazg.httpkit.controller.BaseController;
import com.lnwazg.httpkit.exchange.ExchangeFactory;
import com.lnwazg.httpkit.exchange.SocketExchangeFactory;
import com.lnwazg.httpkit.exchange.exchangehandler.HttpExchangeHandler;
import com.lnwazg.httpkit.filter.CtrlFilter;
import com.lnwazg.httpkit.filter.CtrlFilterChain;
import com.lnwazg.httpkit.handler.route.Router;
import com.lnwazg.httpkit.proxy.ControllerProxy;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.reflect.ClassKit;
import com.lnwazg.kit.singleton.B;

/**
 * HttpServer对象<br>
 * 支持多个服务器实例一起运行
 */
public class HttpServer extends Server
{
    /**
     * 默认的http版本号
     */
    public static final String VERSION = "HTTP/1.1";
    
    /**
     * 服务器版本号
     */
    public static final String SERVER_NAME = "LiNan/1.0.3";
    
    /**
     * web资源目录的基准路径
     */
    public static String DEFAULT_WEB_RESOURCE_BASE_PATH = "static/";
    
    /**
     * 服务器启动计时器
     */
    private StopWatch stopWatch = new StopWatch();
    
    /**
     * controller的结尾，一般是xxx.do结尾，这个值一般是do
     */
    private String controllerSuffix;
    
    /**
     * 基础路径
     */
    private String basePath = "";
    
    /**
     * 路由器对象
     */
    private final Router router;
    
    /**
     * 端口号
     */
    private final int port;
    
    /**
     * 搜索的磁盘表
     */
    private String[] searchDisks =
        {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        
    /**
     * 是否初始化过FreeMarker的root目录<br>
     * 仅需初始化一次即可
     */
    private boolean initFreemarkerRoot;
    
    /**
     * 绑定端口号，并获得一个HttpServer的实例
     * @author nan.li
     * @param port
     * @return
     * @throws IOException
     */
    public static HttpServer bind(int port)
        throws IOException
    {
        return new HttpServer(new SocketExchangeFactory(new ServerSocket(port)), new Router(), port);
    }
    
    /**
     * 构造函数 
     * @param factory
     * @param router
     * @param port
     */
    private HttpServer(ExchangeFactory factory, Router router, int port)
    {
        super(factory, new HttpExchangeHandler(router));
        stopWatch.start();
        Logs.i("HttpServer start generating route table...");
        this.router = router;
        this.port = port;
    }
    
    /**
     * 获取当前的端口号
     * @author nan.li
     * @return
     */
    public int getPort()
    {
        return port;
    }
    
    /**
     * 设置上下文路径
     * @author nan.li
     * @param contextPath
     */
    public void setContextPath(String contextPath)
    {
        setBasePath(contextPath);
    }
    
    /**
     * 设置上下文路径
     * @author lnwazg@126.com
     * @param basePath
     */
    public void setBasePath(String basePath)
    {
        if (StringUtils.isNotEmpty(basePath))
        {
            if (!basePath.startsWith("/"))
            {
                basePath = String.format("/%s", basePath);
            }
            this.basePath = basePath;
        }
    }
    
    public String getBasePath()
    {
        return basePath;
    }
    
    public void listen()
    {
        super.listen(this);
        Logs.i("Server started OK at port " + port + ", which cost " + stopWatch.getTime() + " ms! Please visit:  http://127.0.0.1:" + port + getBasePath()
            + "/list\n");
    }
    
    public void shutdown()
    {
        try
        {
            close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //============================================================================================================================================================
    //以下是具体的增加路由的一些机制
    
    /**
     * 映射具体的Controller class<br>
     * 根据类型增加路由器
     * @author lnwazg@126.com
     * @param c
     * @return
     */
    public void addControllerRoute(Class<BaseController> c)
    {
        Router.addControllerRoutes(c, router, this);
    }
    
    //    /**
    //     * 映射具体的Controller object<br>
    //     * 根据对象增加路由器
    //     * @author lnwazg@126.com
    //     * @param controller
    //     * @return
    //     */
    //    public void addControllerRoute(Controller controller)
    //    {
    //        Router.addControllerRoutes(controller, router);
    //    }
    
    /**
     * 增加资源映射器<br>
     * 将资源文件夹映射到指定的docBasePath位置
     * @author lnwazg@126.com
     * @param docBasePath
     * @param file
     */
    public void addWatchResourceDirRoute(String docBasePath, File file)
    {
        Router.addDocumentRootRoutes(docBasePath, file, router, this);
    }
    
    /**
     * 将资源文件夹resourcePath映射到docBasePath<br>
     * 即使打成jar包，依然可以无障碍访问里面的页面以及资源！
     * @author lnwazg@126.com
     * @param docBasePath
     * @param resourcePath
     */
    public void addFreemarkerPageDirRoute(String docBasePath, String resourcePath)
    {
        Router.addFreemarkerRootRoutes(docBasePath, resourcePath, router, this);
    }
    
    /**
     * 自动搜索并添加搜索目录
     * @author nan.li
     */
    public void autoSearchThenAddWatchResourceDirRoute()
    {
        for (String disk : searchDisks)
        {
            disk = disk.toLowerCase();
            File f = new File(String.format("%s:\\", disk));
            if (f.exists())
            {
                addWatchResourceDirRoute(disk, f);
            }
        }
    }
    
    /**
     * 搜素某个包下面的所有类，然后依次根据具体的类增加路由器
     * @author nan.li
     * @param packageName
     * @param ctrlFilterChain 
     */
    @SuppressWarnings("unchecked")
    public HttpServer packageSearchAndInit(String packageName, CtrlFilterChain ctrlFilterChain)
    {
        List<Class<?>> cList = ClassKit.getClasses(packageName.trim());
        try
        {
            for (Class<?> clazz : cList)
            {
                if (BaseController.class.isAssignableFrom(clazz))
                {
                    //如果Controller是clazz的父类的话
                    //首先，先为这个类生成动态代理，然后将该代理类注入到单例表中
                    BaseController controllerProxy = ControllerProxy.proxyControllerWithFilterChain((Class<BaseController>)clazz, ctrlFilterChain);//根据接口生成动态代理类
                    B.s2(clazz, controllerProxy);
                    addControllerRoute((Class<BaseController>)clazz);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }
    
    /**
     * 初始化过滤器链条以及过滤器
     * @author lnwazg@126.com
     */
    public CtrlFilterChain initFilterConfigs()
    {
        try
        {
            List<String> classNameList = null;
            if (getClass().getClassLoader().getResource("filters.cfg") != null)
            {
                classNameList = IOUtils.readLines(getClass().getClassLoader().getResourceAsStream("filters.cfg"), CharEncoding.UTF_8);
            }
            //过滤器类列表
            List<Class<CtrlFilter>> filterClassList = new ArrayList<>();
            if (classNameList != null && classNameList.size() > 0)
            {
                for (String classpath : classNameList)
                {
                    String trimed = StringUtils.trimToEmpty(classpath);
                    if (StringUtils.isNotBlank(trimed))
                    {
                        if (trimed.startsWith("#") || trimed.startsWith("//"))
                        {
                            //配置文件支持注释
                            continue;
                        }
                        // 尝试加载这个类
                        @SuppressWarnings("unchecked")
                        Class<CtrlFilter> filterClass = (Class<CtrlFilter>)Class.forName(trimed);
                        if (filterClass != null)
                        {
                            filterClassList.add(filterClass);
                        }
                        else
                        {
                            System.out.println(String.format("无法加载类: %s, 请检查类路径是否写错？", trimed));
                            continue;
                        }
                    }
                }
            }
            //过滤器类加载完毕了，接下来要挨个初始化了
            //先初始化过滤器链条
            CtrlFilterChain ctrlFilterChain = new CtrlFilterChain();
            //然后依次初始化各个过滤器
            if (filterClassList.size() > 0)
            {
                Logs.i("加载到的filterClassList为:" + filterClassList);
                for (Class<CtrlFilter> filterClazz : filterClassList)
                {
                    //单例实例化过滤器
                    CtrlFilter ctrlFilter = B.g(filterClazz);
                    //将实例入链
                    ctrlFilterChain.addToChain(ctrlFilter);
                }
            }
            return ctrlFilterChain;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 额外的响应头信息表
     */
    Map<String, String> extraResponseHeaders = new HashMap<>();
    
    /**
     * 添加额外的响应头表
     * @author nan.li
     * @param extraResponseHeaders
     */
    public void addExtraResponseHeaders(Map<String, String> extraResponseHeaders)
    {
        this.extraResponseHeaders.putAll(extraResponseHeaders);
    }
    
    public Map<String, String> getExtraResponseHeaders()
    {
        return extraResponseHeaders;
    }
    
    public String getControllerSuffix()
    {
        return controllerSuffix;
    }
    
    public void setControllerSuffix(String controllerSuffix)
    {
        this.controllerSuffix = controllerSuffix;
    }
    
    public boolean isInitFreemarkerRoot()
    {
        return initFreemarkerRoot;
    }
    
    public void setInitFreemarkerRoot(boolean initFreemarkerRoot)
    {
        this.initFreemarkerRoot = initFreemarkerRoot;
    }
    
    /**
     * /web/
     */
    private String fkBasePath;
    
    /**
     * static/<br>
     * HttpServer.DEFAULT_WEB_RESOURCE_BASE_PATH<br>
     */
    private String fkResourcePath;
    
    public void setFkBasePath(String fkBasePath)
    {
        this.fkBasePath = fkBasePath;
    }
    
    public String getFkBasePath()
    {
        return fkBasePath;
    }
    
    public void setFkResourcePath(String fkResourcePath)
    {
        this.fkResourcePath = fkResourcePath;
    }
    
    public String getFkResourcePath()
    {
        return fkResourcePath;
    }
}
