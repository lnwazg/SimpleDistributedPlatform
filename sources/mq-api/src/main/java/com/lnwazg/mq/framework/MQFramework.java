package com.lnwazg.mq.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.TestCase;
import com.lnwazg.mq.api.MqRequest;
import com.lnwazg.mq.api.MqResponse;
import com.lnwazg.mq.constant.Constants;

/**
 * MQ的异步通讯框架
 * @author nan.li
 * @version 2016年9月27日
 */
public class MQFramework
{
    /**
     * 每次处理的消息数量限制
     */
    public static int eachTimeLimit = Constants.DEFAULT_HANDLE_MESSAGE_LIMIT;
    
    /**
     * 消息拉取的间隔时间
     */
    public static long pullIntervalSeconds = Constants.DEFAULT_PULL_INTERVAL_SECONDS;
    
    /**
     * 发件人地址，我自己的邮箱地址
     */
    public static String myselfAddress;
    
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();//输出的时候，进行格式美化
    
    /**
     * 计算并且设置轮询间隔时长
     * @author nan.li
     * @param hasMsg
     * @return
     */
    private static void calcAndSetPullIntervalSeconds(boolean hasMsg)
    {
        //默认是10秒钟，最大不可以超过60秒钟
        if (hasMsg)
        {
            //有消息，那么下次也要最快轮询，那么下次直接恢复到最初
            pullIntervalSeconds = Constants.DEFAULT_PULL_INTERVAL_SECONDS;
        }
        else
        {
            //没消息，那么下次轮询的间隔要增长5
            pullIntervalSeconds += 5;
            //若达到最大值，那么就守在最大值的位置
            if (pullIntervalSeconds > Constants.MAX_PULL_INTERVAL_SECONDS)
            {
                pullIntervalSeconds = Constants.MAX_PULL_INTERVAL_SECONDS;
            }
        }
    }
    
    /**
     * 监听我的收件箱并自动收件<br>
     * 初始化MQ异步通讯机制的Controller层<br>
     * 每次去心跳检测。如果检测到有数据，则心跳停止，顺序去获取并消费。直到消费不到数据之后，继续开启心跳检测
     * @author nan.li
     * @param channels  订阅的一个或多个主题内容
     */
    @Deprecated
    public static void initMqController(List<String> channelList)
    {
        //等待MQ连接初始化完毕之后再开始监听！
        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        //起一个定时器，0延迟，从常量表里读取默认的拉取间隔时间，定时执行远程消息拉取工作。
        //拉取到消息之后，执行相应的controller的相应的处理方法
        //每分钟执行一次收取操作，并交由框架做相应的处理
        ExecMgr.startDaemenThread(() -> {
            while (true)
            {
                //是否有消息收到了
                boolean hasMsg = doPull(channelList);
                //每次去心跳检测。如果检测到有数据，则心跳停止，顺序去获取并消费。直到消费不到数据之后，继续开启心跳检测
                if (hasMsg)
                {
                    Logs.i("有消息收到了,开始顺序收取完全部的消息...");
                    while (true)
                    {
                        Logs.i("开始收取下一条积压的消息...");
                        if (!doPull(channelList))
                        {
                            Logs.i("所有积压的消息已经全部收取完毕！");
                            break;
                        }
                    }
                }
                //间歇一定时间后再试
                try
                {
                    //此处可以做成变量的间隔时间，例如一开始10秒间隔，如果下一次还检测不到消息，那么下一次就15秒间隔，逐渐增加，最高间隔到60秒。
                    //可变长度的心跳检测，可以兼顾智能化和通讯浪费
                    calcAndSetPullIntervalSeconds(hasMsg);
                    Logs.i(String.format("消息池空了，拉取消息的服务休眠 %s 秒后再试...", pullIntervalSeconds));
                    Thread.sleep(pullIntervalSeconds * 1000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        
    }
    
    /**
     * 监听我的收件箱并自动收件<br>
     * 初始化MQ异步通讯机制的Controller层<br>
     * 每次去心跳检测。如果检测到有数据，则心跳停止，顺序去获取并消费。直到消费不到数据之后，继续开启心跳检测
     * @author nan.li
     * @param channels  订阅的一个或多个主题内容
     */
    public static void initMqController(String... channels)
    {
        //等待MQ连接初始化完毕之后再开始监听！
        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        //直接封装成多个Controller类，根据方法名进行匹配即可！
        //每个controller对应一个path，controller下面的多个方法对应着多个处理实例
        if (ArrayUtils.isEmpty(channels))
        {
            Logs.info("channels is null, will exit MQFramework.initMqController()! No channel msgs will be received!");
            return;
        }
        //起一个定时器，0延迟，从常量表里读取默认的拉取间隔时间，定时执行远程消息拉取工作。
        //拉取到消息之后，执行相应的controller的相应的处理方法
        //每分钟执行一次收取操作，并交由框架做相应的处理
        ExecMgr.startDaemenThread(() -> {
            while (true)
            {
                //同步收消息，会导致zeroMQ出问题，因此不再使用同步收消息的功能，而改用异步收消息       2017-7-9
                //并且，所有与MQ交互的都应该是异步的
                
                //                //是否有消息收到了
                //                boolean hasMsg = doPull(channels);
                //                //每次去心跳检测。如果检测到有数据，则心跳停止，顺序去获取并消费。直到消费不到数据之后，继续开启心跳检测
                //                if (hasMsg)
                //                {
                //                    Logs.i("有消息收到了,开始顺序收取完全部的消息...");
                //                    while (true)
                //                    {
                //                        Logs.i("开始收取下一条积压的消息...");
                //                        if (!doPull(channels))
                //                        {
                //                            Logs.i("所有积压的消息已经全部收取完毕！");
                //                            break;
                //                        }
                //                    }
                //                }
                //                
                //                //间歇一定时间后再试
                //                try
                //                {
                //                    //此处可以做成变量的间隔时间，例如一开始10秒间隔，如果下一次还检测不到消息，那么下一次就15秒间隔，逐渐增加，最高间隔到60秒。
                //                    //可变长度的心跳检测，可以兼顾智能化和通讯浪费
                //                    calcAndSetPullIntervalSeconds(hasMsg);
                //                    Logs.i(String.format("消息池空了，拉取消息的服务休眠 %s 秒后再试...", pullIntervalSeconds));
                //                    Thread.sleep(pullIntervalSeconds * 1000);
                //                }
                //                catch (Exception e)
                //                {
                //                    e.printStackTrace();
                //                }
                
                //异步拉取消息
                doPullAsync(channels);
                //停歇若干秒后继续
                try
                {
                    TimeUnit.SECONDS.sleep(Constants.DEFAULT_PULL_INTERVAL_SECONDS);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * 异步拉取消息
     * @author nan.li
     * @param channels
     */
    private static void doPullAsync(String... channels)
    {
        for (String node : channels)
        {
            //接收消息的内容并处理
            //客户端监听来自某个服务端的消息（即某个channel的消息），对不同的消息类型进行不同的处理逻辑
            new MqRequest("ReceiveMessage").addParam("node", node).addParam("limit", eachTimeLimit).sendAsync(
                response -> {
                    if (response != null && response.isOk())
                    {
                        JsonObject content = response.getContent();
                        JsonArray list = content.getAsJsonArray("list");
                        //循环处理每一条content
                        if (list != null && list.size() > 0)
                        {
                            Logs.i(String.format("拉取到订阅频道【%s】的消息", node));
                            //有消息收到了
                            for (int i = 0; i < list.size(); i++)
                            {
                                JsonObject contentObject = list.get(i).getAsJsonObject();
                                String realContent = contentObject.get("content").getAsString();
                                try
                                {
                                    JsonObject obj = new JsonParser().parse(realContent).getAsJsonObject();
                                    String path = obj.get("path").getAsString();//message/sendMsg
                                    JsonObject params = obj.get("params").getAsJsonObject();
                                    invokeController(path, params);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {
                            //                            Logs.i("订阅频道的消息为空！");
                        }
                    }
                    else
                    {
                        Logs.w("接收消息失败！");
                    }
                });
        }
    }
    
    /**
     * 同步消息拉取，不建议使用，因为会导致zeroMQ出现： zmq.core.error.ZMQError: Operation cannot be accomplished in current state
     * @author nan.li
     * @param channels
     * @return  是否拉取到了消息
     */
    @Deprecated
    private static boolean doPull(List<String> channelList)
    {
        boolean hasMsg = false;
        for (String node : channelList)
        {
            //接收消息的内容并处理
            //客户端监听来自某个服务端的消息（即某个channel的消息），对不同的消息类型进行不同的处理逻辑
            MqResponse response = new MqRequest("ReceiveMessage").addParam("node", node).addParam("limit", eachTimeLimit).send();
            if (response != null && response.isOk())
            {
                JsonObject content = response.getContent();
                JsonArray list = content.getAsJsonArray("list");
                //循环处理每一条content
                if (list != null && list.size() > 0)
                {
                    Logs.i(String.format("拉取到订阅频道【%s】的消息", node));
                    //有消息收到了
                    hasMsg = true;
                    for (int i = 0; i < list.size(); i++)
                    {
                        JsonObject contentObject = list.get(i).getAsJsonObject();
                        String realContent = contentObject.get("content").getAsString();
                        try
                        {
                            JsonObject obj = new JsonParser().parse(realContent).getAsJsonObject();
                            String path = obj.get("path").getAsString();//message/sendMsg
                            JsonObject params = obj.get("params").getAsJsonObject();
                            invokeController(path, params);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    Logs.i("订阅频道的消息为空！");
                }
            }
            else
            {
                Logs.w("接收消息失败！");
            }
        }
        return hasMsg;
    }
    
    /**
     * 同步消息拉取，不建议使用，因为会导致zeroMQ出现： zmq.core.error.ZMQError: Operation cannot be accomplished in current state
     * @author nan.li
     * @param channels
     * @return  是否拉取到了消息
     */
    @Deprecated
    private static boolean doPull(String... channels)
    {
        boolean hasMsg = false;
        for (String node : channels)
        {
            //接收消息的内容并处理
            //客户端监听来自某个服务端的消息（即某个channel的消息），对不同的消息类型进行不同的处理逻辑
            MqResponse response = new MqRequest("ReceiveMessage").addParam("node", node).addParam("limit", eachTimeLimit).send();
            if (response != null && response.isOk())
            {
                JsonObject content = response.getContent();
                JsonArray list = content.getAsJsonArray("list");
                //循环处理每一条content
                if (list != null && list.size() > 0)
                {
                    Logs.i(String.format("拉取到订阅频道【%s】的消息", node));
                    //有消息收到了
                    hasMsg = true;
                    for (int i = 0; i < list.size(); i++)
                    {
                        JsonObject contentObject = list.get(i).getAsJsonObject();
                        String realContent = contentObject.get("content").getAsString();
                        try
                        {
                            JsonObject obj = new JsonParser().parse(realContent).getAsJsonObject();
                            String path = obj.get("path").getAsString();//message/sendMsg
                            JsonObject params = obj.get("params").getAsJsonObject();
                            invokeController(path, params);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    Logs.i("订阅频道的消息为空！");
                }
            }
            else
            {
                Logs.w("接收消息失败！");
            }
        }
        return hasMsg;
    }
    
    /**
     * 调用指定path的方法，参数为params
     * @author nan.li
     * @param path
     * @param params
     */
    protected static void invokeController(String path, JsonObject params)
    {
        //path:  /message/sendMsg
        //params:   
        if (matchPath(path))
        {
            MutablePair<String, String> pair = resolvePath(path);
            String classShortName = pair.getLeft();//     news
            String methodName = pair.getRight();//        readNews
            Map<String, String> paramMap = getParamMap(params);
            
            //根据短名称，获得一个单例对象。
            //然后调用对应的方法即可。注入参数map
            ControllerManager.invoke(classShortName, methodName, paramMap);
        }
    }
    
    /**
     * 获取参数表
     * @author nan.li
     * @param params
     * @return
     */
    private static Map<String, String> getParamMap(JsonObject params)
    {
        Map<String, String> ret = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : params.entrySet())
        {
            ret.put(entry.getKey(), entry.getValue().getAsString());
        }
        return ret;
    }
    
    /**
     * 是否符合一个controller的请求结构
     * @author nan.li
     * @param key
     * @return
     */
    private static boolean matchPath(String key)
    {
        Pattern pattern = Pattern.compile("^\\/\\w+\\/\\w+$");
        Matcher matcher = pattern.matcher(key);
        return matcher.matches();
    }
    
    @TestCase
    void test1()
    {
        System.out.println(matchPath("/sdfsdf/sdfsdfs"));
        System.out.println(matchPath("/1/sdf56sdf/sdfsdfs"));
        System.out.println(matchPath("//sdfsdf/sdfsdfs"));
        System.out.println(matchPath("/sdfsdf/sdfsdfs/"));
        System.out.println(matchPath("/sdfsdf//sdfsdfs"));
        
        System.out.println(resolvePath("/aaa111/bbb222"));
    }
    
    public static void main(String[] args)
    {
        TF.l(MQFramework.class);
    }
    
    private static MutablePair<String, String> resolvePath(String key)
    {
        MutablePair<String, String> retPair = new MutablePair<>();
        List<String> ret = new ArrayList<>();
        Pattern pat = Pattern.compile("^\\/(\\w+)\\/(\\w+)$");
        Matcher mat = pat.matcher(key);
        while (mat.find())
        {
            for (int i = 1; i <= mat.groupCount(); i++)
            {
                String find = mat.group(i);
                ret.add(find);
            }
        }
        if (ret.size() == 2)
        {
            retPair.setLeft(ret.get(0));
            retPair.setRight(ret.get(1));
            return retPair;
        }
        return null;
    }
    
}
