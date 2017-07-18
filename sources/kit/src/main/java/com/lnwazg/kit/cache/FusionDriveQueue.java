package com.lnwazg.kit.cache;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.lnwazg.kit.gson.GsonKit;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.reflect.TypeReference;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.TestCase;

/**
 * 融合了内存的快速和文件缓存的大容量的混合存储器<br>
 * 超强的存取速度，超大的存储容量，超大满足，智能提取数据<br>
 * 可用于大批量操作队列的场景，例如MQ批量发消息
 * @author nan.li
 * @version 2017年3月29日
 */
public class FusionDriveQueue<T>
{
    /**
     * 默认的分桶大小
     */
    public static final int DEFAULT_BUCKET_SIZE = 250;
    
    /**
     * 当前使用的实例的分桶大小
     */
    private int bucketSize = DEFAULT_BUCKET_SIZE;
    
    /**
     * 内存队列表
     */
    List<T> vector = Collections.synchronizedList(new LinkedList<>());//写性能较好  读性能（采用了synchronized关键字的方式）较差
    
    /**
     * 存储桶数据的缓存对象
     */
    FileCache bucketCache = new FileCache();
    
    //写指针
    static AtomicInteger writePoint = new AtomicInteger(0);
    
    //读指针
    static AtomicInteger readPoint = new AtomicInteger(0);
    
    /**
     * 参考：MqSingleTaskMgr
     * 如何实现高效批量发送？
     * 首先内存存有一个队列，然后Ehcache缓存中也存有一个队列。
     * 有一个while循环：1.分桶，入文件缓存  2.取出一桶，开始享用   3.没有任何桶，则取出最后的散包，扫尾享用之
     * 
     * 那么，可以做一个FusionDrive。这个Drive就是一个容器，提供几个接口：
     * add          添加对象到队列里，并且智能融合自动分桶
     * getBatch     批量获取一桶。如果没有一个完整的桶，就获取最后的散包。如果散包都没有，则返回空
     * isEmpty      判断该FusionDrive是否为空
     */
    public FusionDriveQueue(int bucketSize)
    {
        if (bucketSize <= 0)
        {
            throw new IllegalStateException("bucketSize：" + bucketSize + "非法！请设置一个大于0的整数！");
        }
        this.bucketSize = bucketSize;
    }
    
    /**
     * 构造函数<br>
     * 采用默认的 DEFAULT_BUCKET_SIZE
     */
    public FusionDriveQueue()
    {
    }
    
    /**
     * 添加对象到队列里，并且智能融合自动分桶
     * @author nan.li
     * @param object
     */
    public void add(T t)
    {
        vector.add(t);
        
        //添加后立即尝试分桶
        if (vector.size() >= bucketSize)
        {
            List<T> bucketVector = new LinkedList<>();
            for (int i = 0; i < bucketSize; i++)
            {
                bucketVector.add(vector.remove(0));
            }
            //这个是Ehcache缓存，数据量大的时候可以缓存到本地的，防止内存被撑爆了！
            bucketCache.put(String.valueOf(writePoint), GsonKit.gson.toJson(bucketVector));
            //写指针向前移动
            writePoint.incrementAndGet();
        }
        
        //那么，此时要么分桶了，要么没分桶（零散的）
    }
    
    /**
     * 批量获取一桶。如果没有一个完整的桶，就获取最后的散包。如果散包都没有，则返回空
     * @author nan.li
     * @return
     */
    public List<T> getBatch()
    {
        Object cacheDataObj = bucketCache.get(String.valueOf(readPoint));
        if (cacheDataObj != null)
        {
            //桶读取成功了
            Logs.i(String.format("读取桶%s的数据成功！", readPoint));
            //读取完毕，将对象删除，指针后移
            bucketCache.remove(String.valueOf(readPoint));
            readPoint.incrementAndGet();
            //将该桶的信息返回
            List<T> list = GsonKit.gson.fromJson((String)cacheDataObj, new TypeReference<List<T>>()
            {
            }.getType());
            return list;
        }
        //3.用内存的临时缓存队列
        else if (vector.size() > 0)
        {
            List<T> bucketVector = new LinkedList<>();
            bucketVector.addAll(vector);
            vector.clear();
            return bucketVector;
        }
        return null;
    }
    
    /**
     * 判断该FusionDrive是否为空
     * @author nan.li
     * @return
     */
    public boolean isEmpty()
    {
        if (bucketCache.get(String.valueOf(readPoint)) != null)
        {
            return true;
        }
        else if (vector.size() > 0)
        {
            return true;
        }
        return false;
    }
    
    /**
     * 测试混合驱动的读写
     * @author nan.li
     */
    @TestCase
    void test1()
    {
        FusionDriveQueue<String> fusionDriveQueue = new FusionDriveQueue<String>();
        for (int i = 0; i < 2038; i++)
        {
            fusionDriveQueue.add(i + "");
        }
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
        System.out.println(fusionDriveQueue.isEmpty());
        System.out.println(fusionDriveQueue.getBatch());
    }
    
    public static void main(String[] args)
    {
        TF.l(FusionDriveQueue.class);
    }
}
