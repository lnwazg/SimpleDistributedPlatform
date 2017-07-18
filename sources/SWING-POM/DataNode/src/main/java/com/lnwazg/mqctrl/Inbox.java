package com.lnwazg.mqctrl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lnwazg.kit.controllerpattern.Controller;
import com.lnwazg.kit.reflect.RemoteJarKit;
import com.lnwazg.mq.framework.BaseController;
import com.lnwazg.swing.util.WinMgr;
import com.lnwazg.ui.MainFrame;

/**
 * 消息收件箱
 * @author nan.li
 * @version 2017年7月9日
 */
@Controller("/inbox")
public class Inbox extends BaseController
{
    /**
     * 运行分布式任务
     * @author nan.li
     */
    void runDistrJarTask()
    {
        //当前需要运行的jar包任务的url
        String jarUrl = paramMap.get("jarUrl");
        //该客户端不为空，则将其加入到上线列表中
        if (StringUtils.isNotEmpty(jarUrl))
        {
            //加载远程jar包，并执行分布式任务。
            //什么时候任务执行结束，由具体的执行任务类自己去处理
            //根据该URL进行加载jar包，并执行事先约定好的方法名称，该执行的时候执行，该上报的时候上报。当完毕之后，要发送一条end()指令。
            
            //http://10.13.69.28:45555/jartask/20170712105135.jar
            String jarName = jarUrl.substring(jarUrl.lastIndexOf("/") + 1);
            WinMgr.win(MainFrame.class).showStatus("开始执行" + jarName);
            
            //jar包中的属性表配置文件
            //            Map<String, String> propMap = RemoteJarKit.loadRemotePropertyMap(jarUrl, "MainClass.properties");
            //            if (propMap == null || propMap.isEmpty())
            //            {
            //                Logs.e("远程jar包中的入口类配置文件不存在或者信息为空！因此无法执行远程jar包任务！");
            //                return;
            //            }
            //            String mainClassFullPath = propMap.get("Main");
            //            if (StringUtils.isEmpty(mainClassFullPath))
            //            {
            //                Logs.e("远程jar包中的入口类配置文件中缺少主类配置信息！因此无法执行远程jar包任务！");
            //                return;
            //            }
            //            RemoteJarKit.invokeRemoteObject(jarUrl, mainClassFullPath, "execute", new Class[] {Map.class}, paramMap);
            
            //调用远程jar包的指定配置文件的主类的指定方法，传入指定的参数表
            RemoteJarKit.invokeRemoteObjectByPropertyFile(jarUrl, "MainClass.properties", "execute", new Class[] {Map.class}, paramMap);
            
            WinMgr.win(MainFrame.class).showStatus(jarName + "执行完毕");
        }
    }
    
    public static void main(String[] args)
    {
        //远程执行jar包有以下几种方式：
        //        System.out.println((Object)RemoteJarKit.invokeRemoteObject("file:\\c:\\1.jar", "com.lnwazg.api.Task", "getTaskDescription"));
        //        System.out.println((Object)RemoteJarKit.invokeRemoteObject("file:\\c:\\1.jar", "com.lnwazg.api.Task", "execute"));
        //        RemoteJarKit.loadRemoteClass("file:\\c:\\2.jar", "com.lnwazg.api.Dep");
        //        System.out.println((Object)RemoteJarKit.invokeRemoteObject("file:\\c:\\2.jar", "com.lnwazg.api.Task", "getTaskDescription"));
        //        System.out.println((Object)RemoteJarKit.invokeRemoteObject("http://10.18.18.148:45555/jartask/20170709122253.jar", "com.lnwazg.api.Task", "getTaskDescription"));
        //        System.out.println((Object)RemoteJarKit.invokeRemoteObject("file:\\c:\\2.jar", "com.lnwazg.api.Task", "execute"));
        
        //具体的测试代码如下：
        //        System.out.println((Object)RemoteJarKit.invokeRemoteObject("file:\\D:\\Documents\\002.jar", "com.lnwazg.Task001", "getTaskDescription"));
        //        System.out.println((Object)RemoteJarKit.invokeRemoteObject("file:\\D:\\Documents\\002.jar", "com.lnwazg.Task002", "getTaskDescription"));
        
        //        String resourceContent = RemoteJarKit.loadRemoteResourceContent("file:\\D:\\Documents\\002.jar", "MainClass.properties");
        //        String resourceContent = RemoteJarKit.loadRemoteResourceContent("file:\\D:\\Documents\\003.jar", "more/2.properties");
        //        System.out.println(resourceContent);
        
        //        Map<String, String> map = RemoteJarKit.loadRemotePropertyMap("file:\\D:\\Documents\\002.jar", "MainClass.properties");
        //        String resourceContent = RemoteJarKit.loadRemoteResourceContent("file:\\D:\\Documents\\003.jar", "more/2.properties");
        //        D.d(map);
    }
}
