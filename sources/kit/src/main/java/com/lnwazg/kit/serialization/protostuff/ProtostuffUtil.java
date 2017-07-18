package com.lnwazg.kit.serialization.protostuff;

import java.util.HashMap;
import java.util.Map;

import com.lnwazg.kit.reflect.ClassKit;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Protostuff序列化与反序列化的工具类<br>
 * 作为对比，可比较jdk自带的序列化工具：SerializationUtils<br>
 * Protostuff的优势：速度极快、跨平台、无须实现序列化接口<br>
 * 使用注意项:被序列化的类必须是public的，且必须要有默认的构造方法<br>
 * 实测结果:序列化再反序列化一个对象500w次，jdk自带的需要27秒，而ProtostuffUtil仅需3秒。ProtostuffUtil的速度是jdk自带的9倍多！<br>
 * 并且Protostuff不再依赖JavaBean必须实现Serializable接口，这一点，必定是一个革命性的进步！<br>
 * jdk自带的反序列化可以不用指定类型信息，因为类型信息就在字节码里面！<br>
 * 而Protostuff在反序列化的时候必须要明确指定类型信息，否则便无法正确地反序列化！(但是可以正常地序列化)<br>
 * 两者总体来说各有优势，需要根据实际情况进行选择！<br>
 * jdk原生的序列化的例子：<br>
 * byte[] bytes = SerializationUtils.serialize(object);<br>
 * <br>
 * ProtostuffUtil的例子：<br>
 *          byte[] bytes = ProtostuffUtil.serializer(person);<br>
 *          Person person2 = ProtostuffUtil.deserializer(bytes, Person.class);<br>
 * @author nan.li
 * @version 2017年1月11日
 */
public class ProtostuffUtil
{
    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj)
    {
        Class<T> clazz = (Class<T>)obj.getClass();
        //分配默认的缓冲区大小
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try
        {
            //获取参数类型的schema对象
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            buffer.clear();
        }
    }
    
    /**
     * 反序列化，必须明确地指定类型信息<br>
     * 支持原生类型的反序列化<br>
     * 任何接口类型、抽象对象类型、Object.class这种抽象类型都是不支持的！
     * @param data
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data, Class<T> clazz)
    {
        try
        {
            //实例化一个空对象
            T obj = ClassKit.newInstance(clazz);
            //将原生类型转为包裹类型，以防getSchema()方法出错
            clazz = (Class<T>)ClassKit.transferPrimitiveTypeToWrappedType(clazz);
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(data, obj, schema);
            return obj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Object deserialize(byte[] data, Object obj)
    {
        try
        {
            @SuppressWarnings("rawtypes")
            Schema schema = getSchema(obj.getClass());
            ProtostuffIOUtil.mergeFrom(data, obj, schema);
            return obj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 缓存的schema表
     */
    private static Map<Class<?>, Schema<?>> cachedSchema = new HashMap<>();
    
    /**
     * 获取指定类型的schema信息
     * @author nan.li
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz)
    {
        Schema<T> schema = (Schema<T>)cachedSchema.get(clazz);
        if (schema == null)
        {
            schema = RuntimeSchema.getSchema(clazz);
            if (schema != null)
            {
                cachedSchema.put(clazz, schema);
            }
        }
        return schema;
    }
    
    public static void main(String[] args)
    {
        byte[] bs = ProtostuffUtil.serialize(356435.4343534535D);
        //        System.out.println(ProtostuffUtil.deserialize(bs, 0D));
        System.out.println(ProtostuffUtil.deserialize(bs, double.class));
        //        System.out.println(ProtostuffUtil.deserialize(bs, Double.class));
        
        bs = ProtostuffUtil.serialize('我');
        //        System.out.println(ProtostuffUtil.deserialize(bs, 0D));
        System.out.println(ProtostuffUtil.deserialize(bs, char.class));
    }
}