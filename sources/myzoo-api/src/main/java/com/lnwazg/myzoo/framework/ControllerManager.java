package com.lnwazg.myzoo.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.lnwazg.kit.controllerpattern.Controller;
import com.lnwazg.kit.list.Lists;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.reflect.ClassKit;
import com.lnwazg.myzoo.bean.Msg;

public class ControllerManager
{
    /**
     * 默认的控制器搜索包，可自定义
     */
    private static final String CONTROLLER_SEARCH_PACKAGE = "com.lnwazg.zooctrl";
    
    private static Map<String, Object> controllerNameObjectMap;
    
    static
    {
        initControllerNameObjectMap();
    }
    
    private static void initControllerNameObjectMap()
    {
        List<Class<?>> cList = ClassKit.getClasses(CONTROLLER_SEARCH_PACKAGE);
        controllerNameObjectMap = new HashMap<String, Object>();
        if (Lists.isNotEmpty(cList))
        {
            try
            {
                for (Class<?> clazz : cList)
                {
                    String classShortName = clazz.getSimpleName().toLowerCase();//默认值
                    if (clazz.isAnnotationPresent(Controller.class))
                    {
                        Controller controller = clazz.getAnnotation(Controller.class);
                        String value = controller.value();//   /news
                        if (StringUtils.isNotEmpty(value))
                        {
                            classShortName = value;
                            //自动去除掉斜杠
                            if (classShortName.indexOf("/") != -1)
                            {
                                classShortName = StringUtils.replace(classShortName, "/", "");
                            }
                        }
                    }
                    controllerNameObjectMap.put(classShortName, clazz.newInstance());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 调用指定类的指定方法，传入指定的paramMap参数
     * @author nan.li
     * @param classShortName
     * @param methodName
     * @param paramMap
     * @param connection 
     * @param server 
     * @param token 
     */
    public static void invokeServer(String classShortName, String methodName, Msg msg, Connection connection, Server server, String token)
    {
        //        Logs.i(String.format("服务端  调用  controller: /%s/%s", classShortName, methodName));
        Object object = controllerNameObjectMap.get(classShortName);
        if (object == null)
        {
            Logs.w("无法找到对象实例:" + classShortName + ", 将退出执行该controller！");
            return;
        }
        //设置字段(注入paramMap参数)
        ClassKit.setField(object, "request", msg.getMap());
        ClassKit.setField(object, "paramMap", msg.getMap());
        ClassKit.setField(object, "paramList", msg.getList());
        ClassKit.setField(object, "paramObj", msg.getObj());
        
        ClassKit.setField(object, "token", token);
        ClassKit.setField(object, "connection", connection);
        ClassKit.setField(object, "server", server);
        
        //调用方法
        ClassKit.invokeMethod(object, methodName);
    }
    
    public static void invokeClient(String classShortName, String methodName, Msg msg, Connection connection, Client client, String listenNode, String token)
    {
        //        Logs.i(String.format("客户端  调用  controller: /%s/%s", classShortName, methodName));
        Object object = controllerNameObjectMap.get(classShortName);
        if (object == null)
        {
            Logs.w("无法找到对象实例:" + classShortName + ", 将退出执行该controller！");
            return;
        }
        //设置字段(注入paramMap参数)
        ClassKit.setField(object, "request", msg.getMap());
        ClassKit.setField(object, "paramMap", msg.getMap());
        ClassKit.setField(object, "paramList", msg.getList());
        ClassKit.setField(object, "paramObj", msg.getObj());
        
        ClassKit.setField(object, "token", token);
        ClassKit.setField(object, "connection", connection);
        ClassKit.setField(object, "client", client);
        ClassKit.setField(object, "listenNode", listenNode);
        
        //调用方法
        ClassKit.invokeMethod(object, methodName);
    }
}
