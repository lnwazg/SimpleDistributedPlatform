package com.lnwazg.myzoo.framework;

import org.apache.commons.lang3.tuple.MutablePair;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.lnwazg.kit.controllerpattern.ControllerKit;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.myzoo.bean.Msg;

/**
 * 管理框架
 * @author nan.li
 * @version 2017年5月31日
 */
public class ZooFramework
{
    /**
     * 调用服务端的控制器<br>
     * ServerController
     * @author nan.li
     * @param msg
     * @param connection
     * @param server
     * @param token
     */
    public static void invokeServerController(Msg msg, Connection connection, Server server, String token)
    {
        String path = msg.getPath();
        path = ControllerKit.fixPath(path);
        //path:  /message/sendMsg
        //params:   
        if (ControllerKit.matchPath(path))
        {
            MutablePair<String, String> pair = ControllerKit.resolvePath(path);
            String classShortName = pair.getLeft();//     news
            String methodName = pair.getRight();//        readNews
            //然后调用对应的方法即可。注入参数map
            ControllerManager.invokeServer(classShortName, methodName, msg, connection, server, token);
        }
        else
        {
            Logs.e(String.format("Controller path: 【%s】  格式不正确，无法调用！", path));
        }
    }
    
    /**
     * 调用客户端的控制器<br>
     * ClientController
     * @author nan.li
     * @param msg
     * @param connection
     * @param client
     * @param listenNode
     * @param token
     */
    public static void invokeClientController(Msg msg, Connection connection, Client client, String listenNode, String token)
    {
        String path = msg.getPath();
        path = ControllerKit.fixPath(path);
        //path:  /message/sendMsg
        //params:   
        if (ControllerKit.matchPath(path))
        {
            MutablePair<String, String> pair = ControllerKit.resolvePath(path);
            String classShortName = pair.getLeft();//     news
            String methodName = pair.getRight();//        readNews
            //然后调用对应的方法即可。注入参数map
            ControllerManager.invokeClient(classShortName, methodName, msg, connection, client, listenNode, token);
        }
        else
        {
            Logs.e(String.format("Controller path: 【%s】  格式不正确，无法调用！", path));
        }
    }
    
}
