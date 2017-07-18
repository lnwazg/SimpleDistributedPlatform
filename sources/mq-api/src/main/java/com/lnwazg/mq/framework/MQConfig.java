package com.lnwazg.mq.framework;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.lnwazg.kit.io.StreamUtils;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.property.PropertyUtils;
import com.lnwazg.mq.api.MqRequest;

/**
 * MQ配置信息对象
 * @author nan.li
 * @version 2016年10月14日
 */
public class MQConfig
{
    /**
    * 默认的配置文件路径
    */
    public static final String DEFAULT_CONFIG_FILEPATH = "mq.properties";
    
    /**
     * MQ服务器地址
     */
    public static String SERVER = "";
    
    /**
     * MQ端口号
     */
    public static String PORT = "";
    
    /**
     * 当前自己的收件箱
     */
    public static String MYSELF_ADDRESS = "";
    
    /**
     * 订阅的收件箱列表，用逗号分隔
     */
    public static String CHANNELS = "";
    
    /**
     * 根据默认的配置文件进行初始化
     * @author nan.li
     */
    public static boolean initDefaultMqConfig()
    {
        return initMqConfig(DEFAULT_CONFIG_FILEPATH);
    }
    
    /**
     * 根据指定的配置文件去初始化MQ客户端系统
     * @author nan.li
     * @param configPath
     */
    private static boolean initMqConfig(String configPath)
    {
        //打成jar包之后，就不能采用getResource(path)的方式去获取绝对url了，因为无法正常地去获取！
        //        Map<String, String> configs = PropertyUtils.load(MQConfig.class.getClassLoader().getResource(configPath).getFile(), CharEncoding.UTF_8);
        //        if (configs.isEmpty())
        //        {
        //            Logs.e(String.format("配置文件%s不存在！因此无法初始化MQ config！", configPath));
        //            return;
        //        }
        
        Map<String, String> configs = null;
        InputStream inputStream = MQConfig.class.getClassLoader().getResourceAsStream(configPath);
        if (inputStream != null)
        {
            try
            {
                configs = PropertyUtils.load(inputStream);
                if (configs.isEmpty())
                {
                    Logs.e(String.format("配置文件%s内容为空！因此无法初始化MQ config！", configPath));
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
            Logs.e(String.format("配置文件%s不存在！因此无法初始化MQ config！", configPath));
            return false;
        }
        SERVER = configs.get("SERVER");
        PORT = configs.get("PORT");
        MYSELF_ADDRESS = configs.get("MYSELF_ADDRESS");
        CHANNELS = configs.get("CHANNELS");
        return initMq(SERVER, Integer.valueOf(PORT), MYSELF_ADDRESS, CHANNELS.split(","));
    }
    
    /**
     * 初始化MQ客户端并设置消息收件箱监听服务
     * @author nan.li
     * @param server
     * @param port
     * @param myselfAddress  发件人地址
     * @param channels  监听的邮箱地址
     */
    public static boolean initMq(String server, int port, String myselfAddress, String... channels)
    {
        //初始化mq连接
        //设置单点的MQ连接
        MqRequest.setSingletonServerAddrAndPort(server, port);
        MqRequest.initSingletonServerAddrAndPort();
        
        //接收来自MQ的异步消息，最终显示到根路径上
        MQFramework.myselfAddress = myselfAddress;//指定我自己的邮箱地址
        
        MQFramework.initMqController(channels);//监听我的收件箱并自动收件
        
        return true;
    }
    
    /**
     * 初始化MQ客户端并设置消息收件箱监听服务
     * @author nan.li
     * @param server
     * @param port
     * @param myselfAddress  发件人地址
     * @param channels  监听的邮箱地址
     */
    public static boolean initMq(String server, Integer port, String myselfAddress, List<String> channelList)
    {
        //初始化mq连接
        //设置单点的MQ连接
        MqRequest.setSingletonServerAddrAndPort(server, port);
        MqRequest.initSingletonServerAddrAndPort();
        
        //接收来自MQ的异步消息，最终显示到根路径上
        MQFramework.myselfAddress = myselfAddress;//指定我自己的邮箱地址
        
        MQFramework.initMqController(channelList);//监听我的收件箱并自动收件
        
        return true;
    }
    
    /**
     * 集群初始化mq
     * @author nan.li
     * @param clusterConfigInfo   127.0.0.1:1700,127.0.0.1:1600
     * @param myselfAddress  发件人地址
     * @param channels   监听的邮箱地址
     */
    public static boolean initMqCluster(String clusterConfigInfo, String myselfAddress, String... channels)
    {
        //初始化mq连接
        //设置集群的MQ连接
        MqRequest.setClusterConfigInfo(clusterConfigInfo);
        MqRequest.initClusterConfigInfo();
        
        //接收来自MQ的异步消息，最终显示到根路径上
        MQFramework.myselfAddress = myselfAddress;//指定我自己的邮箱地址
        MQFramework.initMqController(channels);//监听我的收件箱并自动收件
        
        return true;
    }
    
}
