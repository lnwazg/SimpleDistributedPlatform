package com.lnwazg.mq.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lnwazg.kit.compress.GzipBytesUtils;
import com.lnwazg.kit.describe.DescribeUtils;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.security.PasswordKit;
import com.lnwazg.kit.security.SecurityUtils;
import com.lnwazg.kit.validate.Validates;
import com.lnwazg.mq.taskmgr.MqSingleTaskMgr;

/**
 * MQ请求的简单易用的客户端
 * @author nan.li
 * @version 2016年8月1日
 */
public class MqRequest
{
    /**
     * 发送消息的模式
     * @author nan.li
     * @version 2016年8月2日
     */
    static enum MsgMode
    {
        ZEROMQ, NETTY
    }
    
    /**
     * 当前所使用的发送消息的模式
     */
    private static MsgMode curMsgMode = MsgMode.ZEROMQ;//默认为netty模式
    
    Gson gson = new Gson();
    
    /**
     * 可以通过此方法设置消息发送的模式
     * @author nan.li
     * @param paramMsgNode
     */
    public static void switchMode(MsgMode paramMsgNode)
    {
        curMsgMode = paramMsgNode;
    }
    
    public JsonObject jsonObject;
    
    public MqRequest(String serviceCode)
    {
        jsonObject = new JsonObject();
        jsonObject.addProperty("SERVICE_CODE", serviceCode);
    }
    
    /**
     * 添加所需的参数
     * @author nan.li
     * @param key
     * @param value
     * @return
     */
    public MqRequest addParam(String key, String value)
    {
        jsonObject.addProperty(key, value);
        return this;
    }
    
    public MqRequest addParam(String key, Number value)
    {
        jsonObject.addProperty(key, value);
        return this;
    }
    
    public MqRequest addParam(String key, Boolean value)
    {
        jsonObject.addProperty(key, value);
        return this;
    }
    
    public MqRequest addParam(String key, Character value)
    {
        jsonObject.addProperty(key, value);
        return this;
    }
    
    /**
     * 添加所需的参数
     * @author nan.li
     * @param key
     * @param value
     * @return
     */
    public MqRequest addParam(String key, JsonElement value)
    {
        jsonObject.add(key, value);
        return this;
    }
    
    public MqRequest addParam(String key, Object obj)
    {
        return addParam(key, gson.toJsonTree(obj));
    }
    
    static ZMQ.Context context = null;
    
    static ZMQ.Socket socket = null;
    
    static String currentServerAddr = "127.0.0.1";
    
    static int currentPort = 11111;
    
    /**
     * 当前是集群状态
     */
    private static boolean isCluster = false;
    
    //    static
    //    {
    //        initZeroMq();
    //    }
    
    /**
     * 调整单机配置的服务地址以及端口号
     * @author Administrator
     * @param currentServerAddr
     * @param currentPort
     */
    public static void setSingletonServerAddrAndPort(String currentServerAddr, int currentPort)
    {
        MqRequest.currentServerAddr = currentServerAddr;
        MqRequest.currentPort = currentPort;
    }
    
    public static void initSingletonServerAddrAndPort()
    {
        //先关闭单机和老的集群信息
        closeSingletonServerAddrAndPort();
        closeClusterConfigInfo();
        
        isCluster = false;
        
        context = ZMQ.context(1); //创建一个I/O线程的上下文
        socket = context.socket(ZMQ.REQ); //创建一个request类型的socket，这里可以将其简单的理解为客户端，用于向response端发送数据
        socket.connect(String.format("tcp://%s:%d", currentServerAddr, currentPort)); //默认连接的地址以及端口号
    }
    
    public static void closeSingletonServerAddrAndPort()
    {
        if (socket != null)
        {
            socket.close();
            socket = null;
        }
        if (context != null)
        {
            context.term();
            context = null;
        }
    }
    
    /**
     * 集群列表
     */
    static List<Triple<ZMQ.Context, ZMQ.Socket, String>> clusterList = new ArrayList<>();
    
    static String configInfo;
    
    /**
     * 设置集群配置信息
     * @author nan.li
     * @param currentServerAddr2
     */
    public static void setClusterConfigInfo(String configInfo)
    {
        MqRequest.configInfo = configInfo;
    }
    
    public static void initClusterConfigInfo()
    {
        //先关闭单机和老的集群信息
        closeSingletonServerAddrAndPort();
        closeClusterConfigInfo();
        
        isCluster = true;
        
        //集群配置信息
        //获取服务器二元组信息
        List<Pair<String, String>> serverList = new ArrayList<>();
        String[] configs = StringUtils.split(configInfo, ",");
        for (String config : configs)
        {
            if (StringUtils.isNotBlank(config) && config.indexOf(":") != -1)
            {
                int index = config.indexOf(":");
                String server = config.substring(0, index).trim();
                String port = config.substring(index + 1).trim();
                if (Validates.isInteger(port))
                {
                    serverList.add(new ImmutablePair<String, String>(server, port));
                }
            }
        }
        DescribeUtils.describeList(serverList);
        for (Pair<String, String> configPair : serverList)
        {
            String server = configPair.getLeft();
            String port = configPair.getRight();
            //初始化服务器列表，并存起来
            ZMQ.Context context = null;
            ZMQ.Socket socket = null;
            context = ZMQ.context(1); //创建一个I/O线程的上下文
            socket = context.socket(ZMQ.REQ); //创建一个request类型的socket，这里可以将其简单的理解为客户端，用于向response端发送数据
            socket.connect(String.format("tcp://%s:%s", server, port));
            clusterList.add(new ImmutableTriple<ZMQ.Context, ZMQ.Socket, String>(context, socket, String.format("tcp://%s:%s", server, port)));
        }
    }
    
    public static void closeClusterConfigInfo()
    {
        //依次关闭
        for (Triple<Context, Socket, String> triple : clusterList)
        {
            Context context = triple.getLeft();
            Socket socket = triple.getMiddle();
            if (socket != null)
            {
                socket.close();
                socket = null;
            }
            if (context != null)
            {
                context.term();
                context = null;
            }
        }
        clusterList.clear();
    }
    
    /**
     * 设置请求超时时间
     */
    long REQUEST_TIMEOUT = 10000;
    
    /**
     * 当前消息是否已经发送过的标记
     */
    boolean hasSentRequest = false;
    
    /**
     * 同步发送消息，有可能会导致如下异常：zmq.core.error.ZMQError: Operation cannot be accomplished in current state<br>
     * Any attempt to send another message to the socket (zmq.REQ/zmq.REP), without having received a reply/request will result in an error:zmq.core.error.ZMQError: Operation cannot be accomplished in current state<br>
     * 所以，该方法仅供框架内部调用，以保障方法被有序调用<br>
     * 一直发送，直到发送成功为止<br>
     * 如果发送失败，则会返回null<br>
     * 
     *   Any attempt to send another message to the socket (zmq.REQ/zmq.REP), without having received a reply/request will result in an error:
         ....
         socket.send ("Hello")
         socket.send ("Hello1")
         ....
         Error: zmq.core.error.ZMQError: Operation cannot be accomplished in current state
     * 
     * @author nan.li
     * @return
     */
    @Deprecated
    public MqResponse send()
    {
        while (true)
        {
            MqResponse mqResponse = send0();
            if (mqResponse != null)
            {
                return mqResponse;
            }
            else
            {
                try
                {
                    TimeUnit.SECONDS.sleep(1);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                Logs.i("MqRequest send0() return null, retry...");
            }
        }
    }
    
    private MqResponse send0()
    {
        /**
         * 我们也在未启动 hwserver2 的情况下运行 hwclient2，发现程序直接报错“Connection refused”的连接错误（如图2-2），说明连接不上服务器，这显然是符合常理的。
         * 相比之下，ZeroMQ 的表现就显得比较怪异了。因为我们在未启动 hwserver 的情况下运行 hwclient，发现程序并没有报连接错误，反而是在发送过一条消息之后阻塞住了
         * （如图2-1）；接着我们尝试启动 hwserver，发现 hwclient 又继续运行下去了，直至把 10 条消息发送完毕。
         * 
         * 从以上现象可以看出，ZeroMQ 的 zmq_connect 方法其实只是建立了一个“虚连接”，和 Socket 的 connect 方法完全不同；
         * 实际上，从 ZeroMQ 的源码中也可以看出这点。起初我也感觉这个逻辑很奇怪，但实际上正因为有了这个特性，当我们使用 
         * ZeroMQ 构建分布式系统的时候就不需要关心节点启动先后顺序的问题，为我们提供了不少便捷。但是，如果不善用这个特
         * 性极有可能导致严重的问题。比如，我们想使用 ZeroMQ 进行无状态模式发送，即类似于 HTTP 的“发送-接收-结束”的模式；
         * 假如在发送的过程中网络断线了，就会导致大量请求被阻塞住，严重者可导致服务器资源被耗尽！
         * 
         * 如果要解决以上问题，一般的思路是设置超时，ZeroMQ 可以通过使用 zmq_poll 方法或者设置 ZMQ_LINGER 参数来设置请求
         * 超时，但是这也可能导致一些问题。超时时间设置太小容易丢失数据，设置太长又会影响运行效率，我们需要的是一个更可靠
         * 的网络通信方案。一种简单直接的方式就是对客户端程序进行改造，使之在不稳定的网络环境中也可以稳定运行。
         */
        MqResponse mqResponse = new MqResponse();
        String resultStr = "";
        switch (curMsgMode)
        {
            case ZEROMQ:
                Socket sc = null;
                if (isCluster)
                {
                    //当前正在集群状态
                    //从集群中随机取出一台机器进行使用
                    Triple<ZMQ.Context, ZMQ.Socket, String> triple = clusterList.get(org.apache.commons.lang.math.RandomUtils.nextInt(clusterList.size()));
                    sc = triple.getMiddle();
                    Logs.i(String.format("正在使用集群单元:%s", triple.getRight()));
                }
                else
                {
                    sc = socket;
                }
                try
                {
                    if (sc == null)
                    {
                        return null;
                    }
                    if (hasSentRequest)
                    {
                        Logs.i("该请求已经发送过，不再二次发送！");
                        return mqResponse;
                    }
                    
                    //gzip数据压缩很重要！因为这样可以大幅度降低传输的数据量！
                    long now = System.currentTimeMillis();
                    //                    System.out.println("==========begin to send " + jsonObject);
                    byte[] request = jsonObject.toString().getBytes(CharEncoding.UTF_8);
                    int len1 = request.length;
                    //                    Logs.d(String.format("发送数据...压缩前%d字节", len1));
                    request = GzipBytesUtils.zip(request);//对请求的数据进行压缩处理，减少传输的数据量
                    request = SecurityUtils.aesEncode(request, PasswordKit.PASSWORD);//加密
                    int len2 = request.length;
                    //                    Logs.d(String.format("发送数据...压缩后%d字节", len2));
                    sc.send(request, 0);
                    
                    //已发送
                    hasSentRequest = true;
                    //                    System.out.println(">>>>>>>>>>After send " + jsonObject);
                    
                    //向reponse端发送数据
                    //接收response发送回来的数据  
                    //在request/response模型中，send之后必须要recv之后才能继续send，这可能是为了保证整个request/response的流程走完
                    
                    //此处的错误恢复参考代码：http://zguide.zeromq.org/java:lpclient   http://zguide.zeromq.org/java:lpserver
                    
                    PollItem items[] = {new PollItem(sc, ZMQ.Poller.POLLIN)};
                    int rc = ZMQ.poll(items, REQUEST_TIMEOUT);
                    if (rc == -1)
                    {
                        //这边这个功能也是上面的while(true)循环的核心！
                        return null;
                        //                        break;          //  Interrupted
                    }
                    //  Here we process a server reply and exit our loop if the
                    //  reply is valid. If we didn't a reply we close the client
                    //  socket and resend the request. We try a number of times
                    //  before finally abandoning:
                    
                    if (items[0].isReadable())
                    {
                        //  We got a reply from the server
                        byte[] response = sc.recv();
                        int len3 = response.length;
                        //                    Logs.d(String.format("接收数据...解压前%d字节", len3));
                        long after = System.currentTimeMillis();
                        response = SecurityUtils.aesDecode(response, PasswordKit.PASSWORD);//解密
                        response = GzipBytesUtils.unzip(response);//对收到的数据进行解压缩操作
                        int len4 = response.length;
                        //                    Logs.d(String.format("接收数据...解压后%d字节", len4));
                        resultStr = new String(response, CharEncoding.UTF_8);
                        
                        //                        Logs.i(String.format("本次总计减少传输量%d字节", (len1 + len4 - (len2 + len3))));
                        //                        Logs.i((String.format("MqRequest cost %d ms", after - now)));
                        
                    }
                    else
                    {
                        Logs.w("no response from server, retrying! 服务器可能挂了，因此重启客户端！");
                        //Old socket is confused; close it and open a new one
                        //此处重启相当重要，尤其是当客户端积压的数据量很大的时候，此时非常有必要重启！
                        Logs.w("ReInit ZMQ client now!");
                        if (isCluster)
                        {
                            initClusterConfigInfo();
                        }
                        else
                        {
                            initSingletonServerAddrAndPort();
                        }
                        return null;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    //此处重启相当重要，尤其是当客户端积压的数据量很大的时候，此时非常有必要重启！
                    Logs.w("ReInit ZMQ client now!");
                    if (isCluster)
                    {
                        initClusterConfigInfo();
                    }
                    else
                    {
                        initSingletonServerAddrAndPort();
                    }
                    return null;
                }
                //                finally
                //                {
                //                    socket.close();
                //                    context.term();
                //                }
                break;
            case NETTY:
                //netty模式，待实现
                break;
            default:
                break;
        }
        if (StringUtils.isNotEmpty(resultStr))
        {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(resultStr).getAsJsonObject();
            mqResponse.setContent(jsonObject);
        }
        return mqResponse;
    }
    
    /**
     * 异步发送，有效保障了zeromq的安全性
     * @author nan.li
     */
    public void sendAsync()
    {
        sendAsync(null);
    }
    
    /**
     * 异步发送，带有回调方法
     * @author nan.li
     * @param callback
     */
    public void sendAsync(ResultCallback callback)
    {
        //客户端  累积的任务要排队  失败重试的时候，要按照质数的间隔进行重试！
        
        //zeromq如果无法及时发送出消息，则会自动排队累积消息，直到服务器可用为止！一旦可用，就会进行有效地消息传递！
        //这样可以有效地保证消息不会丢失，并且对客户端以及服务器启动的先后顺序没有要求！
        //这一点，保证了zeromq可以有效且可靠地通信！
        //让客户端的请求按顺序依次发送，既可以保证zeromq的socket的正确性，又能保证消息是有序的！
        
        //过程：1.发送本对象的消息        2.对消息响应进行处理
        //保证：1.一次只发送一个消息   2.顺序执行
        
        //        ExecMgr.singleExec.execute(() -> {
        //            //静静地等待发送完毕后收到回复
        //            MqResponse response = send();
        //            //如果有回调函数，那么就调用回调函数
        //            if (callback != null)
        //            {
        //                callback.call(response);
        //            }
        //        });
        
        //提交任务以及回调函数到队列中，酌情打包发送
        //现在已经实现了消息智能打包发送的功能！
        //消息拉取（非异步通知）功能可以做成质数分钟间隔重试，这种更加高效有趣的尝试方式！
        //也可以这样，定时器定时查询。  当有消息时就收取，直到没有消息，就继续1分钟心跳一次！
        MqSingleTaskMgr.submit(MqRequest.this, callback);
    }
    
    /**
     * 通用的回调接口<br>
     * 作为父类来使用的时候，可能可以序列化，也可能无法序列化。<br>
     * 但如果这个对象是纯粹的ResultCallback接口类型的话，那么就是不可以序列化的
     * @author nan.li
     * @version 2016年10月13日
     */
    public static interface ResultCallback
    {
        void call(MqResponse response);
    }
    
    /**
     * 可序列化的回调接口
     * @author nan.li
     * @version 2016年10月13日
     */
    public static interface ResultCallbackSerializable extends ResultCallback, Serializable
    {
    
    }
}
