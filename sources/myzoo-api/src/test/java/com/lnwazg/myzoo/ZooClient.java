package com.lnwazg.myzoo;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.map.Maps;
import com.lnwazg.myzoo.bean.Msg;
import com.lnwazg.myzoo.framework.ZooFramework;
import com.lnwazg.myzoo.util.Constants;
import com.lnwazg.myzoo.util.KyroResigterClassKit;

public class ZooClient
{
    /**
     * 本地会话的令牌
     */
    private static String token;
    
    /**
     * 查询的节点信息<br>
     * 监听该节点上的配置属性表，如果有变更，则通知本地应用更新
     */
    private static String listenNode = "/com/lnwazg/MQ";
    
    public static void main(String[] args)
        throws IOException
    {
        Client client = new Client();
        client.start();
        client.connect(Constants.CONNECTION_TIMEOUT_MILLSECONDS, "127.0.0.1", Constant.serverPort);
        Arrays.stream(KyroResigterClassKit.TO_BE_REGISTERED_CLASSES).forEach(client.getKryo()::register);
        client.addListener(new Listener()
        {
            public void received(Connection connection, Object object)
            {
                if (object instanceof Msg)
                {
                    Msg msg = (Msg)object;
                    Logs.i("客户端收到 msg" + msg + "\n");
                    
                    //如果是服务器广播通知到所有的客户端去检查更新，那么msg内肯定是没有token的
                    //相反，其余的情况，每次服务端都会带上token回来（要么是新生成的，要么是从客户端带过去的）
                    if (StringUtils.isNotEmpty(msg.getToken()))
                    {
                        token = msg.getToken();
                    }
//                    ZooFramework.invokeClientController(msg, connection, client, listenNode, token);
                }
            }
        });
        
        Msg msg = new Msg().setMap(Maps.asStrHashMap("node", "/com/lnwazg/MQ", "ipAndPorts", "127.0.0.1:13000,127.0.0.1:1700")).setToken(token).setPath("/server/registerSelf");
        client.send(msg);
        try
        {
            TimeUnit.SECONDS.sleep(2);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        msg = new Msg().setMap(Maps.asStrHashMap("node", "/com/lnwazg/MQ", "ipAndPorts", "127.0.0.1:13000,127.0.0.1:1700")).setToken(token).setPath("/server/registerSelf");
        client.send(msg);
        
        //        client.send(new Msg().setToken(token).setPath("/server/news").setMap(Maps.asStrHashMap("news", "今日新闻:fffff")));
    }
    
}
