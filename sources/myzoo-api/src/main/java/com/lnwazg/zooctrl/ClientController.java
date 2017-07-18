package com.lnwazg.zooctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.lnwazg.kit.controllerpattern.Controller;
import com.lnwazg.kit.describe.D;
import com.lnwazg.kit.list.Lists;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.map.Maps;
import com.lnwazg.myzoo.framework.ctrlbase.ClientBaseController;
import com.lnwazg.myzoo.framework.listener.OnlineServersChangeListener;

@Controller("/client")
public class ClientController extends ClientBaseController
{
    /**
     * 当前所使用的服务器的配置信息
     */
    static Map<String, String> currentConfigs;
    
    /**
     * 倒数计数器
     */
    public static CountDownLatch latch;
    
    /**
     * 获取到的服务器配置信息
     */
    public static Map<String, String> returnedServerConfigMap;
    
    /**
     * 获取到的服务器配置信息列表
     */
    public static List<Map<String, String>> returnedServerConfigMapList;
    
    /**
     * 服务列表发生变化的监听器列表<br>
     * 当有服务新上线的时候，或者有服务下线的时候，进行通知
     */
    public static List<OnlineServersChangeListener> serversChangeListeners = new ArrayList<>();
    
    void queryServiceConfigByNodeNameResult()
    {
        returnedServerConfigMap = paramMap;
        if (latch != null)
        {
            latch.countDown();
        }
    }
    
    void queryServiceConfigByNodeNameStartWithResult()
    {
        returnedServerConfigMapList = paramList;
        if (latch != null)
        {
            latch.countDown();
        }
    }
    
    void queryServiceConfigByNodeGroupNameResult()
    {
        returnedServerConfigMapList = paramList;
        if (latch != null)
        {
            latch.countDown();
        }
    }
    
    /**
     * 通知客户端检查更新
     * @author nan.li
     */
    void noticeCheckUpdate()
    {
        //        Logs.i("客户端收到noticeCheckUpdate通知。。。");
        //向zookeeper请求update
        connection.send(getTokenMsg().setPath("/server/checkUpdateConfig").setMap(Maps.asStrHashMap("listenNode", listenNode)));
    }
    
    /**
     * 检查配置信息是否有更新
     * @author nan.li
     */
    void checkConfigChanged()
    {
        Logs.i("当前最新的服务器配置信息：");
        D.d(paramMap);
        Logs.i("当前本地的服务器配置信息：");
        D.d(currentConfigs);
        
        //服务器配置是否发生了变更
        boolean changed = false;
        if (currentConfigs == null)
        {
            changed = true;
        }
        else
        {
            for (String key : currentConfigs.keySet())
            {
                if (!currentConfigs.get(key).equals(paramMap.get(key)))
                {
                    changed = true;
                    break;
                }
            }
        }
        if (changed)
        {
            Logs.i("服务器配置发生了变更！");
            currentConfigs = paramMap;
        }
    }
    
    /**
     * 服务端已经更新，客户端接收到通知之后，需要手动调用监听器列表执行
     * @author nan.li
     */
    void serversChanged()
    {
        //依次回调即可
        if (Lists.isNotEmpty(serversChangeListeners))
        {
            for (OnlineServersChangeListener onlineServersChangeListener : serversChangeListeners)
            {
                if (null != onlineServersChangeListener)
                {
                    onlineServersChangeListener.callback();
                }
            }
        }
    }
}
