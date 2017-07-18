package com.lnwazg.myzoo.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.http.net.IpHostUtils;
import com.lnwazg.kit.io.StreamUtils;
import com.lnwazg.kit.list.Lists;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.map.Maps;
import com.lnwazg.kit.property.PropertyUtils;
import com.lnwazg.myzoo.bean.Msg;
import com.lnwazg.myzoo.framework.listener.OnlineServersChangeListener;
import com.lnwazg.myzoo.util.Constants;
import com.lnwazg.myzoo.util.KyroResigterClassKit;
import com.lnwazg.zooctrl.ClientController;

/**
 * 我的zookeeper的客户端
 * @author nan.li
 * @version 2016年10月28日
 */
public class MyZooClient
{
    /**
     * 默认的配置文件路径
     */
    public static final String DEFAULT_CONFIG_FILEPATH = "myzoo.properties";
    
    private static String SERVER;
    
    private static String PORT;
    
    private static String listenNode;
    
    /**
     * 本地会话的令牌
     */
    private static String token;
    
    private static Client client;
    
    /**
     * 加载默认的zookeeper配置文件
     * @author nan.li
     * @return 成功true，失败false
     */
    public static boolean initDefaultConfig()
    {
        return initConfig(DEFAULT_CONFIG_FILEPATH);
    }
    
    public static Client getClient()
    {
        return client;
    }
    
    private static boolean initConfig(String configPath)
    {
        //打成jar包之后，就不能采用getResource(path)的方式去获取绝对url了，因为无法正常地去获取！
        //        URL url = MyZooKeeper.class.getClassLoader().getResource(configPath);
        //        if (url == null)
        //        {
        //            Logs.e(String.format("配置文件%s不存在！因此无法初始化MyZoo config！", configPath));
        //            return false;
        //        }
        //        String realFilePath = url.getFile();
        //        if (StringUtils.isEmpty(realFilePath))
        //        {
        //            Logs.e(String.format("配置文件%s不存在！因此无法初始化MyZoo config！", configPath));
        //            return false;
        //        }
        //        Map<String, String> configs = PropertyUtils.load(realFilePath, CharEncoding.UTF_8);
        Map<String, String> configs = null;
        InputStream inputStream = MyZooClient.class.getClassLoader().getResourceAsStream(configPath);
        if (inputStream != null)
        {
            try
            {
                configs = PropertyUtils.load(inputStream);
                if (configs.isEmpty())
                {
                    Logs.e(String.format("配置文件%s内容为空！因此无法初始化MyZoo config！", configPath));
                    return false;
                }
            }
            finally
            {
                StreamUtils.close(inputStream);
            }
        }
        else
        {
            Logs.e(String.format("配置文件%s不存在！因此无法初始化MyZoo config！", configPath));
            return false;
        }
        SERVER = configs.get("SERVER");
        PORT = configs.get("PORT");
        listenNode = configs.get("listenNode");
        return initConfig(SERVER, Integer.valueOf(PORT), listenNode);
    }
    
    private static boolean initConfig(String server, Integer port, String listenNode)
    {
        try
        {
            client = new Client();
            client.start();
            Arrays.stream(KyroResigterClassKit.TO_BE_REGISTERED_CLASSES).forEach(client.getKryo()::register);
            client.addListener(new Listener()
            {
                public void received(Connection connection, Object object)
                {
                    if (object instanceof Msg)
                    {
                        Msg msg = (Msg)object;
                        Logs.i("客户端收到 msg:" + msg + "\n");
                        
                        //如果是服务器广播通知到所有的客户端去检查更新，那么msg内肯定是没有token的
                        //相反，其余的情况，每次服务端都会带上token回来（要么是新生成的，要么是从客户端带过去的）
                        if (StringUtils.isNotEmpty(msg.getToken()))
                        {
                            token = msg.getToken();
                        }
                        ZooFramework.invokeClientController(msg, connection, client, listenNode, token);
                    }
                }
            });
            String serverIp = IpHostUtils.getHostIPByHostName(server);
            if (StringUtils.isEmpty(serverIp))
            {
                Logs.e(String.format("无法解析配置文件中的server: %s 的真实地址，请确保在本地hosts文件中配置了 %s 的地址，或确保上层的DNS服务器解析了 %s ！", server, server, server));
                return false;
            }
            client.connect(Constants.CONNECTION_TIMEOUT_MILLSECONDS, server, port);
            Logs.i("已成功连接MyZooKeeper服务器！");
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Logs.e("连接MyZooKeeper服务器失败！");
            return false;
        }
    }
    
    /**
     * 注册一个的服务<br>
     * 指定node以及其他所需的参数<br>
     * 该接口是异步的
     * @author nan.li
     * @param asStrHashMap
     */
    public static void registerService(HashMap<String, String> pMap)
    {
        Logs.i("开始注册服务...");
        client.send(new Msg().setMap(pMap).setToken(token).setPath("/server/registerSelf"));
    }
    
    public static void unregisterService(HashMap<String, String> pMap)
    {
        Logs.i("开始解除注册服务...");
        client.send(new Msg().setMap(pMap).setToken(token).setPath("/server/unregisterSelf"));
    }
    
    /**
     * 根据node名称查询服务器端的配置信息<br>
     * 此处用到了“异步转同步”大法<br>
     * 通过 CountDownLatch的巧妙应用，成功地将异步请求完美转换成了同步请求
     * @author lnwazg@126.com
     * @param nodeName
     * @param consumer
     */
    public static void queryServiceConfigByNodeNameAsync(String nodeName, Consumer<Map<String, String>> consumer)
    {
        //倒数计数器
        final CountDownLatch latch = new CountDownLatch(1);
        ClientController.latch = latch;
        client.send(new Msg().setMap(Maps.asStrHashMap("nodeName", nodeName)).setToken(token).setPath("/server/queryServiceConfigByNodeName"));
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        Map<String, String> returnedServerConfigMap = ClientController.returnedServerConfigMap;
        Logs.i(String.format("读取到MyZooKeeper配置信息:%s", returnedServerConfigMap));
        if (returnedServerConfigMap != null && !returnedServerConfigMap.isEmpty())
        {
            if (consumer != null)
            {
                consumer.accept(returnedServerConfigMap);
            }
        }
        else
        {
            Logs.e("从myZooKeeper查询到的单个配置信息为空！因此无法进一步操作！");
        }
    }
    
    public static Map<String, String> queryServiceConfigByNodeName(String nodeName)
    {
        //倒数计数器
        final CountDownLatch latch = new CountDownLatch(1);
        ClientController.latch = latch;
        client.send(new Msg().setMap(Maps.asStrHashMap("nodeName", nodeName)).setToken(token).setPath("/server/queryServiceConfigByNodeName"));
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        Map<String, String> returnedServerConfigMap = ClientController.returnedServerConfigMap;
        Logs.i(String.format("读取到MyZooKeeper配置信息:%s", returnedServerConfigMap));
        Validate.notNull(returnedServerConfigMap, "从myZooKeeper查询到的单个配置信息为空！因此无法进一步操作！");
        return returnedServerConfigMap;
    }
    
    /**
     * 随机读取一个可用的服务
     * @author nan.li
     * @param nodeNameStartWith
     * @return
     */
    public static Map<String, String> queryServiceConfigByNodeNameStartWithThenChooseOne(String nodeNameStartWith)
    {
        //倒数计数器
        final CountDownLatch latch = new CountDownLatch(1);
        ClientController.latch = latch;
        client.send(new Msg().setMap(Maps.asStrHashMap("nodeNameStartWith", nodeNameStartWith))
            .setToken(token)
            .setPath("/server/queryServiceConfigByNodeNameStartWith"));
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        List<Map<String, String>> list = ClientController.returnedServerConfigMapList;
        Logs.i(String.format("读取到MyZooKeeper配置信息列表:%s", list));
        Validate.notNull(list, "从myZooKeeper查询到的配置信息列表为空！因此无法进一步操作！");
        if (Lists.isEmpty(list))
        {
            Logs.w("从myZooKeeper查询到的配置信息列表为空！因此无法进一步操作！");
            return null;
        }
        //随机取出一个
        int randomValue = RandomUtils.nextInt(0, list.size());
        Map<String, String> returnedServerConfigMap = list.get(randomValue);
        Logs.i(String.format("列表总计%s条数据，随机挑选出的第%s条MyZooKeeper配置信息:%s", list.size(), randomValue, returnedServerConfigMap));
        Validate.notNull(returnedServerConfigMap, "挑选出的第0条配置信息为空！因此无法进一步操作！");
        return returnedServerConfigMap;
    }
    
    /**
     * 通配符查询服务器配置信息列表
     * @author nan.li
     * @param nodeNameStartWith
     * @param consumer
     */
    public static void queryServiceConfigByNodeNameStartWithAsync(String nodeNameStartWith, Consumer<List<Map<String, String>>> consumer)
    {
        //倒数计数器
        final CountDownLatch latch = new CountDownLatch(1);
        ClientController.latch = latch;
        client.send(new Msg().setMap(Maps.asStrHashMap("nodeNameStartWith", nodeNameStartWith))
            .setToken(token)
            .setPath("/server/queryServiceConfigByNodeNameStartWith"));
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        List<Map<String, String>> list = ClientController.returnedServerConfigMapList;
        Logs.i(String.format("读取到MyZooKeeper配置信息列表:%s", list));
        if (Lists.isNotEmpty(list))
        {
            if (consumer != null)
            {
                consumer.accept(list);
            }
        }
        else
        {
            Logs.e("从myZooKeeper查询到的配置信息列表为空！因此无法进一步操作！");
        }
    }
    
    public static List<Map<String, String>> queryServiceConfigByNodeNameStartWith(String nodeNameStartWith)
    {
        //倒数计数器
        final CountDownLatch latch = new CountDownLatch(1);
        ClientController.latch = latch;
        client.send(new Msg().setMap(Maps.asStrHashMap("nodeNameStartWith", nodeNameStartWith))
            .setToken(token)
            .setPath("/server/queryServiceConfigByNodeNameStartWith"));
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        List<Map<String, String>> list = ClientController.returnedServerConfigMapList;
        Logs.i(String.format("读取到MyZooKeeper配置信息列表:%s", list));
        Validate.notNull(list, "从myZooKeeper查询到的配置信息列表为空！因此无法进一步操作！");
        return list;
    }
    
    /**
     * 根据组信息查询服务器配置信息列表
     * @author nan.li
     * @param nodeNameStartWith
     * @param consumer
     */
    public static void queryServiceConfigByNodeGroupNameAsync(String groupName, Consumer<List<Map<String, String>>> consumer)
    {
        //倒数计数器
        final CountDownLatch latch = new CountDownLatch(1);
        ClientController.latch = latch;
        client.send(new Msg().setMap(Maps.asStrHashMap("groupName", groupName)).setToken(token).setPath("/server/queryServiceConfigByNodeGroupName"));
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        List<Map<String, String>> list = ClientController.returnedServerConfigMapList;
        Logs.i(String.format("读取到MyZooKeeper配置信息列表:%s", list));
        if (Lists.isNotEmpty(list))
        {
            if (consumer != null)
            {
                consumer.accept(list);
            }
        }
        else
        {
            Logs.e("从myZooKeeper查询到的配置信息列表为空！因此无法进一步操作！");
        }
    }
    
    public static List<Map<String, String>> queryServiceConfigByNodeGroupName(String groupName)
    {
        //倒数计数器
        final CountDownLatch latch = new CountDownLatch(1);
        ClientController.latch = latch;
        client.send(new Msg().setMap(Maps.asStrHashMap("groupName", groupName)).setToken(token).setPath("/server/queryServiceConfigByNodeGroupName"));
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        List<Map<String, String>> list = ClientController.returnedServerConfigMapList;
        Logs.i(String.format("读取到MyZooKeeper配置信息列表:%s", list));
        Validate.notNull(list, "从myZooKeeper查询到的配置信息列表为空！因此无法进一步操作！");
        return list;
    }
    
    /**
     * 随机读取一个可用的服务
     * @author nan.li
     * @param nodeNameStartWith
     * @return
     */
    public static Map<String, String> queryServiceConfigByNodeGroupNameThenChooseOne(String groupName)
    {
        //倒数计数器
        final CountDownLatch latch = new CountDownLatch(1);
        ClientController.latch = latch;
        client.send(new Msg().setMap(Maps.asStrHashMap("groupName", groupName)).setToken(token).setPath("/server/queryServiceConfigByNodeGroupName"));
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        List<Map<String, String>> list = ClientController.returnedServerConfigMapList;
        Logs.i(String.format("读取到MyZooKeeper配置信息列表:%s", list));
        Validate.notNull(list, "从myZooKeeper查询到的配置信息列表为空！因此无法进一步操作！");
        if (Lists.isEmpty(list))
        {
            Logs.w("从myZooKeeper查询到的配置信息列表为空！因此无法进一步操作！");
            return null;
        }
        //随机取出一个
        int randomValue = RandomUtils.nextInt(0, list.size());
        Map<String, String> returnedServerConfigMap = list.get(randomValue);
        Logs.i(String.format("列表总计%s条数据，随机挑选出的第%s条MyZooKeeper配置信息:%s", list.size(), randomValue, returnedServerConfigMap));
        Validate.notNull(returnedServerConfigMap, "挑选出的第0条配置信息为空！因此无法进一步操作！");
        return returnedServerConfigMap;
    }
    
    /**
     * 节点调用次数表<br>
     * 每隔1分钟，守护线程会定时将节点的调用次数上报上去
     */
    static Map<String, AtomicInteger> nodeInvokeTimesMap = new ConcurrentHashMap<>();
    
    //此为守护线程
    static
    {
        //启动一个守护进程，每隔1分钟上报一次各节点的调用情况，然后清空数据
        ExecMgr.startDaemenThread(() -> {
            while (true)
            {
                //上报一次数据，清空，然后睡眠60s后再次上报
                
                //如果有数据，那么立即上报，并清空
                if (!nodeInvokeTimesMap.isEmpty())
                {
                    //上报上去,再清空数据
                    Map<String, String> paramStrMap = new HashMap<>();
                    for (String key : nodeInvokeTimesMap.keySet())
                    {
                        paramStrMap.put(key, nodeInvokeTimesMap.get(key).toString());
                    }
                    
                    //清空节点调用次数表
                    nodeInvokeTimesMap = new ConcurrentHashMap<>();
                    
                    //发送调用次数报告
                    client.send(new Msg().setMap(paramStrMap).setToken(token).setPath("/server/reportNodeInvokeTimes"));
                }
                else
                {
                    //没有数据，那么就啥都不需要做，欧耶
                }
                
                //然后要休眠60秒之后再战
                try
                {
                    TimeUnit.SECONDS.sleep(60);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * 监控某个节点调用了一次<br>
     * 将该节点的调用次数+1<br>
     * 注意，这个方法必须由各个node手动上报！
     * @author nan.li
     * @param nodeName
     */
    public static void monitorInvokeOnce(String nodeName)
    {
        if (!nodeInvokeTimesMap.containsKey(nodeName))
        {
            //初始化为0
            nodeInvokeTimesMap.put(nodeName, new AtomicInteger(0));
        }
        AtomicInteger atomicInteger = nodeInvokeTimesMap.get(nodeName);
        //将原始值+1
        atomicInteger.incrementAndGet();
        nodeInvokeTimesMap.put(nodeName, atomicInteger);
    }
    
    /**
     * 增加一个监听器<br>
     * 在线服务列表已更新
     * @author nan.li
     * @param onlineServersChangeListener
     */
    public static void addOnlineServersChangeListener(OnlineServersChangeListener onlineServersChangeListener)
    {
        ClientController.serversChangeListeners.add(onlineServersChangeListener);
    }
}
