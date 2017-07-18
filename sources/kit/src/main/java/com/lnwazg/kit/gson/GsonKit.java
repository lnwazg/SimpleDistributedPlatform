package com.lnwazg.kit.gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Gson转换用的帮助类
 * @author nan.li
 * @version 2016年10月13日
 */
public class GsonKit
{
    //常用方法总结：
    //若含这个字段，则尝试进行转换
    //根据field的字段类型信息（包括了泛型的信息）进行转换
    //return gson.fromJson(jsonElement, TypeToken.get(field.getGenericType()).getType());
    //return gson.fromJson(jsonElement, field.getGenericType());//更简单而有效的写法！！！！modified @2013-07-13 01:12:02
    //无论该field是一个参数化的类型，还是一个普通的class类型，均可使用field.getGenericType()去获取它的实际的类型
    
    /**
     * gson.toJson(obj)                 Object转json 、JsonElement转json、<br>
     * gson.toJsonTree(src)             Object转JsonElement             <br>
     * gson.fromJson(json, typeOfT)     String转Object<T>,需要使用TypeToken来表示泛型对象的类型 <br>
     * 无须操作，直接转                                                JsonElement转Object，因为JsonElement本来就是一个Object
     */
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    
    /**
     * 格式化输出版本的gson
     */
    public static Gson prettyGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();
    
    /**
     * jsonParser.parse(jsonStr)  String转JsonElement
     */
    public static JsonParser jsonParser = new JsonParser();
    
    //String转为Obj ========================================================================================================
    /**
     * 创建JsonObject
     * @return JSONObject
     */
    public static JsonObject createJsonObj()
    {
        return new JsonObject();
    }
    
    /**
     * 创建 JsonArray
     * @return JSONArray
     */
    public static JsonArray createJsonArray()
    {
        return new JsonArray();
    }
    
    /**
     * 将JSON字符串转换为具体类型的Java对象
     * @author nan.li
     * @param json
     * @param classOfT   参数是具体的Class即可
     * @return
     * @throws JsonSyntaxException
     */
    public <T> T parseJsonToObj(String json, Class<T> classOfT)
        throws JsonSyntaxException
    {
        return gson.fromJson(json, classOfT);
    }
    
    /**
     * 将JSON字符串转换为具体类型的带有泛型的JAVA对象，详细用法如下：<br>
     * List<T> list = GsonKit.gson.fromJson((String)cacheDataObj, new TypeReference<List<T>>(){}.getType());<br>
     * <br>
     * 获得一个泛型类型的具体方法如下：<br>
     * new TypeReference<List<T>>(){}.getType()
     * @author nan.li
     * @param json
     * @param typeOfT   参数是一个抽象的Type
     * @return
     * @throws JsonSyntaxException
     */
    public <T> T parseJsonToObj(String json, Type typeOfT)
        throws JsonSyntaxException
    {
        return gson.fromJson(json, typeOfT);
    }
    
    public static JsonElement parseJsonElement(Object obj)
    {
        return gson.toJsonTree(obj);
    }
    
    /**
     * JSON字符串转JSONObject对象
     * @param jsonStr JSON字符串
     * @return JSONObject
     */
    public static JsonObject parseJsonObj(String jsonStr)
    {
        return jsonParser.parse(jsonStr).getAsJsonObject();
    }
    
    /**
     * JSON字符串转JSONArray数组
     * @param jsonStr JSON字符串
     * @return JSONArray
     */
    public static JsonArray parseJsonArray(String jsonStr)
    {
        return jsonParser.parse(jsonStr).getAsJsonArray();
    }
    
    /**
     * 任意对象转为JSONObject对象
     * @param obj Bean对象或者Map
     * @return JSONObject
     */
    public static JsonObject parseJsonObj(Object obj)
    {
        return parseJsonObj(gson.toJson(obj));
    }
    
    /**
     * 文件转JSONObject对象
     * @param file JSON文件
     * @param charset 编码
     * @return JSONObject
     * @throws IORuntimeException
     */
    public static JsonObject parseJsonObj(File file, Charset charset)
        throws IOException
    {
        return parseJsonObj(FileUtils.readFileToString(file, charset));
    }
    
    /**
     * 文件转JSONObject对象
     * @param file JSON文件
     * @param charset 编码
     * @return JSONObject
     * @throws IORuntimeException
     */
    public static JsonObject parseJsonObj(File file, String charset)
        throws IOException
    {
        return parseJsonObj(FileUtils.readFileToString(file, charset));
    }
    
    /**
     * 文件转JSONArray数组
     * @param file JSON文件
     * @param charset 编码
     * @return JSONArray
     * @throws IORuntimeException
     */
    public static JsonArray parseJsonArray(File file, Charset charset)
        throws IOException
    {
        return parseJsonArray(FileUtils.readFileToString(file, charset));
    }
    
    /**
     * 文件转JSONArray数组
     * @param file JSON文件
     * @param charset 编码
     * @return JSONArray
     * @throws IORuntimeException
     */
    public static JsonArray parseJsonArray(File file, String charset)
        throws IOException
    {
        return parseJsonArray(FileUtils.readFileToString(file, charset));
    }
    
    //Obj转为String ========================================================================================================
    /**
     * 转换为JSON字符串
     * @param obj 被转为JSON的对象
     * @return JSON字符串
     */
    public static String toJsonStr(Object obj)
    {
        return gson.toJson(obj);
    }
    
    /**
     * 转换为JSON字符串
     * @param jsonElement 被转为JSON的JsonElement对象
     * @return JSON字符串
     */
    public static String toJsonStr(JsonElement jsonElement)
    {
        return gson.toJson(jsonElement);
    }
    
    /**
     * 转换为格式化后的JSON字符串
     * @param obj Bean对象
     * @return JSON字符串
     */
    public static String toPrettyJsonStr(Object obj)
    {
        return prettyGson.toJson(obj);
    }
}
