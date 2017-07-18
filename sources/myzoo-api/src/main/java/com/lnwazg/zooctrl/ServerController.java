package com.lnwazg.zooctrl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JTextPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.esotericsoftware.kryonet.Connection;
import com.lnwazg.dbkit.jdbc.MyJdbc;
import com.lnwazg.kit.controllerpattern.Controller;
import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.gson.GsonKit;
import com.lnwazg.kit.json.GsonCfgMgr;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.singleton.B;
import com.lnwazg.myzoo.bean.Msg;
import com.lnwazg.myzoo.entity.NodeInvokeInfo;
import com.lnwazg.myzoo.framework.ctrlbase.ServerBaseController;
import com.lnwazg.myzoo.util.ZooServers;

/**
 * 单线联系是指在地下工作时，为了防止工作人员被俘后背叛导致一大批人员暴露，就采用单线联系的方式。<br>
 * 单线联系就是一个人只有一个上级和一个下级，不与其他人发生工作联系。<br>
 * 这样，即使他被捕，需要转移的也只有2个人而已。<br>
 * 所有的单线最终汇总到一个最大的头目。<br>
 * @author nan.li
 * @version 2016年10月8日
 */
@Controller("/server")
public class ServerController extends ServerBaseController
{
    /**
     * 在线服务器信息表
     */
    public static Map<String, Map<String, String>> onlineServerInfoMap = new TreeMap<>((a, b) -> {
        return a.compareTo(b);
    });
    
    public static Map<String, List<String>> onlineGroupNodeInfoMap = new TreeMap<>((a, b) -> {
        return a.compareTo(b);
    });
    
    /**
     * 连接、节点、组信息表
     */
    public static Map<Connection, ImmutablePair<String, String>> connectionNodeGroupMap = new ConcurrentHashMap<>();
    
    /**
     * 节点名称与连接对象的对应关系表
     */
    private static Map<String, Connection> nodeConnectionMap = new ConcurrentHashMap<>();
    
    public static JTextPane serverConfigInfo;
    
    /**
     * 服务端所维持的所有的连接表
     */
    public static List<Connection> ALL_CONNECTIONS = Collections.synchronizedList(new LinkedList<>());//写性能较好  读性能（采用了synchronized关键字的方式）较差
    
    static
    {
        //只有设置了这个属性，才进行配置信息本地存取
        if (StringUtils.isNotEmpty(GsonCfgMgr.USER_DIR))
        {
            //加载本地配置信息到内存中
            ZooServers zooServers = GsonCfgMgr.readObject(ZooServers.class);
            if (zooServers == null)
            {
                zooServers = new ZooServers();
                Map<String, Map<String, String>> onlineServerInfoMap = new HashMap<>();
                Map<String, List<String>> onlineGroupNodeInfoMap = new HashMap<>();
                zooServers.setOnlineServerInfoMap(onlineServerInfoMap);
                zooServers.setOnlineGroupNodeInfoMap(onlineGroupNodeInfoMap);
            }
            Map<String, Map<String, String>> serverMap = zooServers.getOnlineServerInfoMap();
            if (serverMap == null)
            {
                serverMap = new HashMap<>();
            }
            onlineServerInfoMap.putAll(serverMap);
            
            Map<String, List<String>> groupMap = zooServers.getOnlineGroupNodeInfoMap();
            if (groupMap == null)
            {
                groupMap = new HashMap<>();
            }
            onlineGroupNodeInfoMap.putAll(groupMap);
        }
    }
    
    /**
     * 更新本地的配置信息文件
     * @author nan.li
     */
    public static void updateServerConfig()
    {
        ZooServers zooServers = new ZooServers();
        zooServers.setOnlineServerInfoMap(onlineServerInfoMap);
        zooServers.setOnlineGroupNodeInfoMap(onlineGroupNodeInfoMap);
        GsonCfgMgr.writeObject(zooServers);
        
        //然后还要刷新公告板的信息
        String configJson = GsonKit.prettyGson.toJson(zooServers);
        if (serverConfigInfo != null)
        {
            ExecMgr.guiExec.execute(() -> {
                serverConfigInfo.setText(configJson);
                serverConfigInfo.setCaretPosition(0);
            });
        }
    }
    
    /**
     * 更新自定义的服务器的信息到当前的服务器中，并写入到磁盘中
     * @author lnwazg@126.com
     * @param zooServers
     */
    public static void updateCustomServerInfo(ZooServers zooServers)
    {
        onlineServerInfoMap = new TreeMap<>((a, b) -> {
            return a.compareTo(b);
        });
        onlineServerInfoMap.putAll(zooServers.getOnlineServerInfoMap());
        
        onlineGroupNodeInfoMap = new TreeMap<>((a, b) -> {
            return a.compareTo(b);
        });
        onlineGroupNodeInfoMap.putAll(zooServers.getOnlineGroupNodeInfoMap());
        
        //将这个最新的配置信息写入到磁盘中
        GsonCfgMgr.writeObject(zooServers);
    }
    
    /**
     * 注册自己的服务到zookeeper服务器<br>
     * 上线某个新节点<br>
     * 上线的时候需要给大家做通知
     * @author nan.li
     */
    void registerSelf()
    {
        //MyZooKeeper.registerService(Maps.asStrMap("node", "remoteCache-187f3d60c361c8cebc5dfd46856b5a91", "ipAndPorts", "127.0.0.1:13000,127.0.0.1:1700")).setToken(token).setPath("/server/registerSelf");
        //        String ipAndPorts = paramMap.get("ipAndPorts");
        //        String ip = paramMap.get("ip");
        //        String port = paramMap.get("port");
        
        /**
            "remoteCache-187f3d60c361c8cebc5dfd46856b5a91": {
              "node": "remoteCache-187f3d60c361c8cebc5dfd46856b5a91",
              "server": "192.168.0.55",
              "port": "11112",
              "group": "remoteCache"
            }
         */
        String node = paramMap.get("node");//该node的值是名称+hash值的组合，可以最大限度保证同一个服务的相同server和port的对象不会被重复注册到注册中心内
        if (StringUtils.isNotEmpty(node))
        {
            //将节点信息更新到本地内存
            onlineServerInfoMap.put(node, paramMap);
            String group = paramMap.get("group");//该服务所属的服务群组信息
            if (StringUtils.isNotEmpty(group))
            {
                List<String> nodeList = onlineGroupNodeInfoMap.get(group);
                if (nodeList == null)
                {
                    nodeList = new ArrayList<>();
                    onlineGroupNodeInfoMap.put(group, nodeList);
                }
                if (!nodeList.contains(node))
                {
                    nodeList.add(node);
                    onlineGroupNodeInfoMap.put(group, nodeList);
                }
            }
            
            //除了当前的连接之外，其余的全部通知一遍
            //当某个客户端变更的时候，要推送通知所有连接上来的client，统一更新一下配置数据!
            //当然，如果只是想通知给指定的连接，也完美没问题！
            //那个异步controller框架真的很强大！灰常实用！可以做成通用的组件了！
            //            server.sendToAllExcept(connection.getID(), new Msg().setPath("/client/noticeCheckUpdate"));
            //服务器信息发生变更的时候，客户端并不能做到实时热重启。因此此处通知客户端变更的想法是好的，但是很难实施！
            
            //记录下node、group与对应的connection的对应关系
            //当connection下线的时候，要主动通知相应的node与group也同时下线
            connectionNodeGroupMap.put(connection, new ImmutablePair<String, String>(node, group));
            
            //上线的时候，需要将该节点对应的连接对象记录下来
            nodeConnectionMap.put(node, connection);
            
            noticeClientServersChanged(connection);
            
            updateServerConfig();
        }
    }
    
    /**
     * 通知客户端：服务器列表已经变咯
     * @author nan.li
     * @param exceptConnection   从通知列表中需要排除掉的连接
     */
    private static void noticeClientServersChanged(Connection exceptConnection)
    {
        //依次通知每一个在线的客户端： 服务器列表已经变咯
        for (Connection connection : ALL_CONNECTIONS)
        {
            if (null != connection && connection != exceptConnection)
            {
                //连接非空，并且不是那个被排除掉的连接的时候，就给他发送变更通知
                connection.send(new Msg().setPath("/client/serversChanged"));
            }
        }
    }
    
    /**
     * 将某个节点下线掉<br>
     * 下线的时候同样需要给大家做通知
     * @author nan.li
     * @param node
     * @param group
     */
    private static void down(String node, String group)
    {
        //先从配置表中删掉该节点
        onlineServerInfoMap.remove(node);
        
        //如果有组信息，那么也要将该节点从组中移除走
        if (StringUtils.isNotEmpty(group))
        {
            List<String> nodeList = onlineGroupNodeInfoMap.get(group);
            if (nodeList == null)
            {
                nodeList = new ArrayList<>();
                onlineGroupNodeInfoMap.put(group, nodeList);
            }
            if (nodeList.contains(node))
            {
                nodeList.remove(node);
                onlineGroupNodeInfoMap.put(group, nodeList);
            }
        }
        
        noticeClientServersChanged(null);
        
        updateServerConfig();
    }
    
    /**
     * 手动下线
     * @author nan.li
     * @param immutablePair
     */
    public static void customDown(ImmutablePair<String, String> immutablePair)
    {
        String node = immutablePair.getLeft();
        String group = immutablePair.getRight();
        down(node, group);
    }
    
    /**
     * 解除自身的注册信息
     * @author nan.li
     */
    void unregisterSelf()
    {
        notifyDownSelf();
    }
    
    /**
     * 主动通知自己下线了<br>
     * 这种情况一般用得很少。一般是不可能调用的<br>
     * 即便调用了这个方法，那么也不会通知指定的客户端消费者去清除其本地内存中的配置信息
     * @author nan.li
     */
    void notifyDownSelf()
    {
        //    MyZooKeeper.unregisterService(Maps.asStrHashMap("node", nodeName, "group", groupName));   
        String node = paramMap.get("node");
        String group = paramMap.get("group");
        down(node, group);
    }
    
    /**
     * 根据节点参数去检查更新
     * @author nan.li
     */
    void checkUpdateConfig()
    {
        String listenNode = paramMap.get("listenNode");
        //客户端不一定都设置了监听的节点。
        //当客户端设置了监听节点的时候，才会尝试查找配置信息并返回给客户端！
        if (StringUtils.isNotEmpty(listenNode))
        {
            Map<String, String> queryResult = onlineServerInfoMap.get(listenNode);
            if (queryResult != null && !queryResult.isEmpty())
            {
                connection.send(getTokenMsg().setMap(queryResult).setPath("/client/checkConfigChanged"));
            }
        }
    }
    
    /**
     * 根据节点名称，去查询节点信息
     * @author lnwazg@126.com
     */
    void queryServiceConfigByNodeName()
    {
        Map<String, String> map = onlineServerInfoMap.get(paramMap.get("nodeName"));
        if (map != null)
        {
            connection.send(getTokenMsg().setMap(map).setPath("/client/queryServiceConfigByNodeNameResult"));
        }
    }
    
    /**
     * 根据某个字符串开头，去查询节点信息列表
     * @author nan.li
     */
    void queryServiceConfigByNodeNameStartWith()
    {
        String nodeNameStartWith = paramMap.get("nodeNameStartWith");
        if (StringUtils.isNotEmpty(nodeNameStartWith))
        {
            List<Map<String, String>> resultList = new ArrayList<>();
            onlineServerInfoMap.forEach((key, value) -> {
                if (key.startsWith(nodeNameStartWith))
                {
                    resultList.add(value);
                }
            });
            connection.send(getTokenMsg().setList(resultList).setPath("/client/queryServiceConfigByNodeNameStartWithResult"));
        }
    }
    
    /**
     * 根据节点组名称，去查询节点信息列表
     * @author nan.li
     */
    void queryServiceConfigByNodeGroupName()
    {
        String groupName = paramMap.get("groupName");
        if (StringUtils.isNotEmpty(groupName))
        {
            //结果列表
            List<Map<String, String>> resultList = new ArrayList<>();
            //该组下面的节点名称列表
            List<String> nodeList = onlineGroupNodeInfoMap.get(groupName);
            onlineServerInfoMap.forEach((key, value) -> {
                if (nodeList.contains(key))
                {
                    resultList.add(value);
                }
            });
            connection.send(getTokenMsg().setList(resultList).setPath("/client/queryServiceConfigByNodeGroupNameResult"));
        }
    }
    
    /**
     * 上报服务器调用次数（最近1分钟内的），并将其记录到数据库中，以备后续查询
     * @author nan.li
     */
    void reportNodeInvokeTimes()
    {
        //paramMap的内容:    {node1:580, node2:90, node3: 120}
        //内容是各个节点的调用次数信息。可能为空
        //如果不为空，那么应该将其记录到数据库空。表名称：NodeInvokeInfo
        if (!paramMap.isEmpty())
        {
            Logs.i("开始记录node节点调用次数信息到数据库...");
            
            List<NodeInvokeInfo> list = new ArrayList<>();
            for (String nodeName : paramMap.keySet())
            {
                NodeInvokeInfo nodeInvokeInfo =
                    new NodeInvokeInfo().setNodeName(nodeName)
                        .setInvokeTimes(Integer.valueOf(paramMap.get(nodeName)))
                        .setCreateTime(new Date());
                list.add(nodeInvokeInfo);
            }
            //批量插入数据到数据库
            try
            {
                B.q(MyJdbc.class).insertBatch(list);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            Logs.i("记录node节点调用次数信息到数据库完毕！");
        }
    }
}
