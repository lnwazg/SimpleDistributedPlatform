package com.lnwazg.myzoo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.random.RandomStrUtils;
import com.lnwazg.myzoo.bean.Msg;
import com.lnwazg.myzoo.framework.ZooFramework;
import com.lnwazg.myzoo.util.KyroResigterClassKit;

public class ZooServer
{
    /**
     * 连接表
     */
    static Map<String, Connection> connMap = new ConcurrentHashMap<>();
    
    public static void main(String[] args)
        throws IOException
    {
        Server server = new Server();
        server.start();
        
        //一个监听器搞定监听即可
        //当获取到注册消息时，将对应的消息以及相应的connection对象注册到map里面
        //当解除连接时，根据connection对象，从map里面取出相应的消息，解除注册即可！
        //这样即可做到实时心跳注册连接，心跳解除则解除注册！
        server.addListener(new Listener()
        {
            public void connected(Connection connection)
            {
                System.out.println(connection + " connected!");
            }
            
            public void disconnected(Connection connection)
            {
                System.out.println(connection + " disconnected!");
            }
        });
        
        server.bind(Constant.serverPort);
        System.out.println("server start ok at port:" + Constant.serverPort + " !");
        Arrays.stream(KyroResigterClassKit.TO_BE_REGISTERED_CLASSES).forEach(server.getKryo()::register);
        server.addListener(new Listener()
        {
            public void received(Connection connection, Object object)
            {
                if (object instanceof Msg)
                {
                    Msg msg = (Msg)object;
                    Logs.i("服务端收到msg：" + msg + "\n");
                    
                    //统一处理msg信息即可
                    
                    //鉴权，并绑定uuid（如果没有，则返回出去）
                    String token = msg.getToken();//token就是标识完整的会话的重要信息
                    if (StringUtils.isEmpty(token))
                    {
                        //为空，则要新建一个token，并提供给客户端使用
                        token = RandomStrUtils.generateRandomString(64);
                        msg.setToken(token);
                    }
                    else
                    {
                        //维持原有的token不变
                    }
                    //将连接放置到connectionMap里面
                    //每次都调用，即可每次都能更新连接到最新的那个
                    connMap.put(token, connection);
                    
                    //                    ZooFramework.invokeServerController(msg, connection, server, token);
                    
                    //将token返回给客户端
                    connection.send(msg);
                    
                    // 当客户端重启时候，需要再次用户鉴权。当然如果已经有了uuid，那么可以认为会话未丢失，可以继续运行。
                }
            }
        });
    }
}
