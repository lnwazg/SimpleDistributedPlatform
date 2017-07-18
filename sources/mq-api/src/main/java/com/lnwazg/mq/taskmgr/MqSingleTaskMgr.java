package com.lnwazg.mq.taskmgr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lnwazg.kit.cache.FileCache;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.mq.api.MqRequest;
import com.lnwazg.mq.api.MqRequest.ResultCallback;
import com.lnwazg.mq.api.MqResponse;

import net.sf.ehcache.Element;

/**
 * MQ的单线程的任务管理器<br>
 * 用于替代默认的单线程任务队列，以提供对队列里面的积压数据智能批量提交的能力<br>
 * 智能数据缓存，最大化MQ系统的战斗力！<br>
 * 按优先级发送请求的算法，实在是太明智了！
 * @author Administrator
 * @version 2016年8月6日
 */
public class MqSingleTaskMgr
{
    //    static CopyOnWriteArrayList<Pair<MqRequest, ResultCallback>> vector = new CopyOnWriteArrayList<>();//写性能较差 读性能较好
    static List<Pair<MqRequest, ResultCallback>> vector = Collections.synchronizedList(new LinkedList<>());//写性能较好  读性能（采用了synchronized关键字的方式）较差
    //因为当前的场景里写入次数较多，而读取次数相对较少（因为都是批量读），所以采用Collections.synchronizedList
    
    /**
     * 不可以序列化的请求，都放入到这个队列<br>
     * 例如来自Swing UI的请求<br>
     * 这个队列里的请求被处理的优先级更高<br>
     * 因为是不可序列化的，因此这个队列的请求数一般不会太多，所以无须写入到本地的文件缓存内
     */
    static List<Pair<MqRequest, ResultCallback>> vectorNonSerial = Collections.synchronizedList(new LinkedList<>());
    
    /**
     * gson.toJson(requestStrs)  JsonElement转String、Object转String <br>
     * gson.toJsonTree(src)     Object转JsonElement         <br>
     * gson.fromJson(json, typeOfT)   String转Object<T>,需要使用TypeToken来表示泛型对象的类型 <br>
     * 无须操作，直接转                             JsonElement转Object
     */
    static Gson gson = new Gson();
    
    /**
     * jsonParser.parse(jsonStr)  String转JsonElement
     */
    static JsonParser jsonParser = new JsonParser();
    
    /**
     * 当请求数量达到一个阈值的时候，批量提交
     */
    //    static int[] BATCH_SUBMIT_SIZE = new int[] {2500, 2000, 1800, 1500, 1200, 1000, 800, 600, 500, 300, 200, 100, 50, 30, 20, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    
    /**
     * 最大的单次批量提交数<br>
     * 这个值如果设置的过大，则不安全；过小，则会降低传输效率
     */
    static int MAX_BATCH_SIZE = 250;
    
    /**
     * 桶的大小和打次批量提交的大小一致<br>
     * 如果有桶存在，则每次提交一桶，提交完就清空掉这个桶
     */
    static int BUCKET_SIZE = MAX_BATCH_SIZE;
    
    /**
     * 存储桶数据的缓存对象
     */
    static FileCache bucketCache = new FileCache();
    
    //    public static void main(String[] args)
    //    {
    //        List<ResultCallback> list = new ArrayList<>();
    //        
    //        list.add(new ResultCallbackSerializable()
    //        {
    //            private static final long serialVersionUID = 1L;
    //            
    //            @Override
    //            public void call(MqResponse response)
    //            {
    //                System.out.println("aaa");
    //                System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbb");
    //            }
    //        });
    //        list.add(new ResultCallbackSerializable()
    //        {
    //            private static final long serialVersionUID = 1L;
    //            
    //            @Override
    //            public void call(MqResponse response)
    //            {
    //                System.out.println("nnn");
    //                System.out.println("uuuuuuuuuuuuuuuuuuuu");
    //            }
    //        });
    //        
    //        Pair<String, List<ResultCallback>> bucketVectorSerializable = new ImmutablePair<String, List<ResultCallback>>("abc", list);
    //        bucketCache.put("aaaa", bucketVectorSerializable);
    //        ((Pair<String, List<ResultCallback>>)bucketCache.get("aaaa")).getRight().get(0).call(null);
    //        ((Pair<String, List<ResultCallback>>)bucketCache.get("aaaa")).getRight().get(1).call(null);
    //    }
    
    /**
     * 提交任务以及回调函数到队列中，酌情打包发送 
     * @author Administrator
     * @param mqRequest
     * @param callback
     */
    public static void submit(MqRequest mqRequest, ResultCallback callback)
    {
        if (callback instanceof Serializable || callback == null)
        {
            //可序列化的，加入到可分桶的队列里
            //null，无须序列化，因此也划归为可序列化的！
            vector.add(new ImmutablePair<MqRequest, MqRequest.ResultCallback>(mqRequest, callback));
        }
        else
        {
            //以下是不可序列化的请求
            vectorNonSerial.add(new ImmutablePair<MqRequest, MqRequest.ResultCallback>(mqRequest, callback));
        }
    }
    
    //有一个守护线程，一直监控vector的内容，并发送数据
    
    static
    {
        startMonitorThread();
    }
    
    /**
     * 计算最接近的批量大小<br>
     * 假如参数是1501（>=250）的话，那么返回250<br>
     * 假如参数是31（2-249之间）的话，那么是多少就返回多少，本次返回31<br>
     * 假如参数是0或者1，那么就没必要批量提交了，直接返回0，代表不批量提交
     * @author Administrator
     * @param vectorSize
     * @return
     */
    private static int calcNearestBatchSize(int vectorSize)
    {
//        System.out.println(String.format("vectorSize: %s", vectorSize));
        
        //默认不批量提交，也就是retBatchSize为0
        int retBatchSize = 0;
        
        //超过MAX_BATCH_SIZE的，以MAX_BATCH_SIZE为单元，批量提交
        if (vectorSize >= MAX_BATCH_SIZE)
        {
            retBatchSize = MAX_BATCH_SIZE;
        }
        //MAX_BATCH_SIZE-2之间的，有多少提交多少，也属于批量提交
        else if (vectorSize >= 2)
        {
            retBatchSize = vectorSize;
        }
        
        //======================================
        //低于2的，即只有1个的，则普通提交
        return retBatchSize;
    }
    
    static AtomicInteger times = new AtomicInteger(1);
    
    //写指针
    static AtomicInteger writePoint = new AtomicInteger(0);
    
    //读指针
    static AtomicInteger readPoint = new AtomicInteger(0);
    
    /**
     * 启动一个监控线程，用于监控vector的变化情况
     * @author Administrator
     */
    private static void startMonitorThread()
    {
        Thread daemonThread = new Thread(() -> {
            while (true)
            {
                //一旦发现vector到达了BUCKET_SIZE，就将其分割到一个小桶里面。
                //可以存在多个桶
                //如果有桶存在，则每次提交一桶。提交完就清空掉。
                //while循环将消息群按桶打包，再逐桶（批量）提交，是一种nb的思想！
                
                //二话不说先对积存的消息进行分桶，以便为后面的消息发送做准备
                if (vector.size() >= BUCKET_SIZE)
                {
                    while (true)
                    {
                        Logs.i(String.format("队列内有%s条数据，满足分桶条件，开始分桶...", vector.size()));
                        //分好一个桶
                        List<Pair<MqRequest, ResultCallback>> bucketVector = new LinkedList<>();
                        for (int i = 0; i < BUCKET_SIZE; i++)
                        {
                            bucketVector.add(vector.remove(0));
                        }
                        
                        //分好了一个桶
                        //bucketCache.put(String.valueOf(writePoint), bucketVector);//直接扔进去无法序列化，因此必须将数据序列化
                        
                        List<MqRequest> requests = new ArrayList<>();
                        List<ResultCallback> callbacks = new ArrayList<>();
                        for (Pair<MqRequest, ResultCallback> pair : bucketVector)
                        {
                            requests.add(pair.getLeft());
                            callbacks.add(pair.getRight());
                        }
                        List<String> requestStrs = new ArrayList<>();
                        for (int i = 0; i < requests.size(); i++)
                        {
                            requestStrs.add(requests.get(i).jsonObject.toString());
                        }
                        Pair<String, List<ResultCallback>> bucketVectorSerializable = new ImmutablePair<>(gson.toJson(requestStrs), callbacks);
                        
                        //这个是Ehcache缓存，数据量大的时候可以缓存到本地的，防止内存被撑爆了！
                        bucketCache.put(String.valueOf(writePoint), bucketVectorSerializable);
                        Logs.i(String.format("将桶%s注入缓存", writePoint));
                        writePoint.incrementAndGet();
                        if (vector.size() < BUCKET_SIZE)
                        {
                            Logs.i(String.format("vector.size()已经不够分桶了，因此分桶完毕！"));
                            break;
                        }
                    }
                }
                
                //为第2步准备数据
                Element element = bucketCache.getCacheObj().get(String.valueOf(readPoint));
                Pair<String, List<ResultCallback>> bucketVectorSerializable = null;
                if (null != element)
                {
                    bucketVectorSerializable = (Pair<String, List<ResultCallback>>)element.getObjectValue();
                }
                
                //1.先处理不可序列化的请求（这些是来自UI界面的请求，优先级最高）
                if (vectorNonSerial.size() > 0)
                {
                    //首先处理不可序列化的请求
//                    Logs.i("处理不可序列化的请求...");
                    int batchSize = calcNearestBatchSize(vectorNonSerial.size());
                    if (batchSize > 0)
                    {
                        //进行批量同步提交
                        List<Pair<MqRequest, ResultCallback>> thisTimeRequests = new ArrayList<>();
                        for (int i = 0; i < batchSize; i++)
                        {
                            thisTimeRequests.add(vectorNonSerial.remove(0));
                        }
                        //即将批量提交请求
                        batchSend(thisTimeRequests);
                    }
                    else
                    {
                        //取出一个，进行同步提交
                        Pair<MqRequest, ResultCallback> pair = vectorNonSerial.remove(0);
                        MqRequest mqRequest = pair.getLeft();
                        ResultCallback callback = pair.getRight();
                        MqResponse response = mqRequest.send();
                        //如果有回调函数，那么就调用回调函数
                        if (callback != null)
                        {
                            callback.call(response);
                        }
                    }
                    //                    System.out.println(String.format("第%s次调用！batchSize: %s\n", times.getAndIncrement(), batchSize));
                }
                //2.用桶（桶就是本地ehcache）
                //每次发送一整桶数据，发完本地循环结束，开始下次循环
                else if (bucketVectorSerializable != null && bucketVectorSerializable.getRight().size() == BUCKET_SIZE)
                {
                    //桶读取成功了
                    Logs.i(String.format("读取桶%s的数据成功！", readPoint));
                    //即将批量提交请求
                    batchSend(bucketVectorSerializable);
                    //                    System.out.println(String.format("第%s次调用！batchSize: %s\n", times.getAndIncrement(), BUCKET_SIZE));
                    //读取完毕，将对象删除，指针后移
                    bucketCache.getCacheObj().remove(String.valueOf(readPoint));
                    readPoint.incrementAndGet();
                }
                //3.用内存的临时缓存队列
                else if (vector.size() > 0)
                {
                    //没读取到桶，则桶都读取完毕了，那么应该尝试读取小缓存的数据
                    //进行发送处理
                    int batchSize = calcNearestBatchSize(vector.size());
                    if (batchSize > 0)
                    {
                        //进行批量同步提交
                        List<Pair<MqRequest, ResultCallback>> thisTimeRequests = new ArrayList<>();
                        for (int i = 0; i < batchSize; i++)
                        {
                            thisTimeRequests.add(vector.remove(0));
                        }
                        //即将批量提交请求
                        batchSend(thisTimeRequests);
                    }
                    else
                    {
                        //取出一个，进行同步提交
                        Pair<MqRequest, ResultCallback> pair = vector.remove(0);
                        MqRequest mqRequest = pair.getLeft();
                        ResultCallback callback = pair.getRight();
                        MqResponse response = mqRequest.send();
                        //如果有回调函数，那么就调用回调函数
                        if (callback != null)
                        {
                            callback.call(response);
                        }
                    }
                    //                    System.out.println(String.format("第%s次调用！batchSize: %s\n", times.getAndIncrement(), batchSize));
                }
                //4.休眠一段时间
                try
                {
                    Thread.sleep(1);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        daemonThread.setDaemon(true);
        daemonThread.start();
    }
    
    private static void batchSend(List<Pair<MqRequest, ResultCallback>> thisTimeRequests)
    {
        List<MqRequest> requests = new ArrayList<>();
        List<ResultCallback> callbacks = new ArrayList<>();
        for (Pair<MqRequest, ResultCallback> pair : thisTimeRequests)
        {
            requests.add(pair.getLeft());
            callbacks.add(pair.getRight());
        }
        List<String> requestStrs = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++)
        {
            requestStrs.add(requests.get(i).jsonObject.toString());
        }
        batchSend(new ImmutablePair<String, List<ResultCallback>>(gson.toJson(requestStrs), callbacks));
    }
    
    private static void batchSend(Pair<String, List<ResultCallback>> pair)
    {
        //这个批量接口，只能在这里由线程池自主调用，不可以自己随便调用！
        System.out.println("开始批量提交！");
        List<ResultCallback> callbacks = pair.getRight();
        String jsonStr = pair.getLeft();
        MqResponse response = new MqRequest("BatchCall").addParam("list", jsonParser.parse(jsonStr)).send();
        if (response.isOk())
        {
            List<String> responses = gson.fromJson(response.get("list"), new TypeToken<List<String>>()
            {
            }.getType());
            for (int i = 0; i < responses.size(); i++)
            {
                String resultStr = responses.get(i);
                if (StringUtils.isNotEmpty(resultStr))
                {
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(resultStr).getAsJsonObject();
                    MqResponse mqResponse = new MqResponse();
                    mqResponse.setContent(jsonObject);
                    ResultCallback callback = callbacks.get(i);
                    if (callback != null)
                    {
                        callback.call(mqResponse);
                    }
                }
            }
        }
        else
        {
            Logs.e("BatchCall失败！");
        }
    }
}
