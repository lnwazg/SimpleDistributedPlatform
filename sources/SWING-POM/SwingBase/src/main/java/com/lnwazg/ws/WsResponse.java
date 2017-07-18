package com.lnwazg.ws;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class WsResponse
{
    static String RESULT_CODE = "RESULT_CODE";
    
    public static final int SC_OK = 200;
    
    private JsonObject content;
    
    /**
     * 判断当前的响应结果是否算是正常
     * @author nan.li
     * @param response
     * @return
     */
    public boolean isOk()
    {
        if (null != content && null != content.get(RESULT_CODE) && SC_OK == content.get(RESULT_CODE).getAsInt())
        {
            return true;
        }
        return false;
    }
    
    /**
     * 获取响应中的键名称列表
     * @author nan.li
     * @return
     */
    public List<String> getKeys()
    {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> entry : content.entrySet())
        {
            list.add(entry.getKey());
        }
        return list;
    }
    
    @Override
    public String toString()
    {
        return "WsResponse [content=" + content + "]";
    }
    
    /**
     * 获取某个key对应的JsonElement
     * @author nan.li
     * @param key
     * @return
     */
    public JsonElement get(String key)
    {
        return content.get(key);
    }
    
    /**
     * 获取某个key对应的JsonElement，然后将其转换成typeOfT类型的对象
     * @author nan.li
     * @param key
     * @param typeOfT
     * @return
     */
    public <T> T getAs(String key, Type typeOfT)
    {
        Gson gson = new Gson();
        return gson.fromJson(content.get(key), typeOfT);
    }
    
    public JsonObject getAsJsonObject(String key)
    {
        return get(key).getAsJsonObject();
    }
    
    public JsonArray getAsJsonArray(String key)
    {
        return get(key).getAsJsonArray();
    }
    
    public JsonPrimitive getAsJsonPrimitive(String key)
    {
        return get(key).getAsJsonPrimitive();
    }
    
    public JsonNull getAsJsonNull(String key)
    {
        return get(key).getAsJsonNull();
    }
    
    public boolean getAsBoolean(String key)
    {
        return get(key).getAsBoolean();
    }
    
    public Number getAsNumber(String key)
    {
        return get(key).getAsNumber();
    }
    
    public String getAsString(String key)
    {
        return get(key).getAsString();
    }
    
    public double getAsDouble(String key)
    {
        return get(key).getAsDouble();
    }
    
    public float getAsFloat(String key)
    {
        return get(key).getAsFloat();
    }
    
    public long getAsLong(String key)
    {
        return get(key).getAsLong();
    }
    
    public int getAsInt(String key)
    {
        return get(key).getAsInt();
    }
    
    public byte getAsByte(String key)
    {
        return get(key).getAsByte();
    }
    
    public char getAsCharacter(String key)
    {
        return get(key).getAsCharacter();
    }
    
    public BigDecimal getAsBigDecimal(String key)
    {
        return get(key).getAsBigDecimal();
    }
    
    public BigInteger getAsBigInteger(String key)
    {
        return get(key).getAsBigInteger();
    }
    
    public short getAsShort(String key)
    {
        return get(key).getAsShort();
    }
    
    //=========================================
    /**
     * 设置响应对象
     * @author nan.li
     * @param content
     */
    public void setContent(JsonObject content)
    {
        this.content = content;
    }
    
    /**
     * 获得响应对象的内容，该内容是一个JsonObject
     * @author nan.li
     * @return
     */
    public JsonObject getContent()
    {
        return content;
    }
    //content只可能是JsonObject类型（这是由server的代码决定的！）
}
