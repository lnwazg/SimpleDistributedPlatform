package com.lnwazg.ws.sim;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.lnwazg.kit.reflect.ClassKit;
import com.lnwazg.ws.sim.anno.Component;
import com.lnwazg.ws.sim.anno.WsRequestParam;

/**
 * 本地化服务模拟器
 * @author nan.li
 * @version 2015-10-13
 */
public class LocalSimulator
{
    private static final Log logger = LogFactory.getLog(LocalSimulator.class);
    
    private static final String WS_MODEL_SEARCH_PACKAGE = "com.lnwazg.ws.module.impl";
    
    private static final String WS_SERVICE_SEARCH_PACKAGE = "com.lnwazg.ws.service";
    
    private static Map<String, IService> modelMap;
    
    private static Map<String, Object> serviceMap;
    
    private static IService getFromModelMap(String serviceCode)
    {
        if (modelMap == null)
        {
            initModelMap();
        }
        return modelMap.get(serviceCode);
    }
    
    /**
     * 初始化A1000X的对象数组
     * @author nan.li
     */
    private static void initModelMap()
    {
        List<Class<?>> cList = ClassKit.getClasses(WS_MODEL_SEARCH_PACKAGE);
        modelMap = new HashMap<String, IService>();
        try
        {
            for (Class<?> clazz : cList)
            {
                modelMap.put(clazz.getAnnotation(Component.class).value(), (IService)clazz.newInstance());//根据Component注解类的值进行注入
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private static Object getFromServiceMap(String clazzName)
    {
        if (serviceMap == null)
        {
            initServiceMap();
        }
        return serviceMap.get(clazzName);
    }
    
    private static void initServiceMap()
    {
        List<Class<?>> cList = ClassKit.getClasses(WS_SERVICE_SEARCH_PACKAGE);
        serviceMap = new HashMap<String, Object>();
        try
        {
            for (Class<?> clazz : cList)
            {
                serviceMap.put(clazz.getName(), clazz.newInstance());//类名全称，作为key
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 本地调用的适配器
     * @author nan.li
     * @param requestJsonObject
     * @return
     */
    public static String invokeLocal(JsonObject requestJsonObject)
    {
        /**
         * 从请求map中取出服务码,然后执行服务
         */
        try
        {
            JsonElement serviceCodeJsonElement = requestJsonObject.get(WSConstants.SERVICE_CODE);
            if (null == serviceCodeJsonElement)
            {
                return WsUtils.errorMsg(WsStatusCode.SC_BAD_REQUEST, "invalid request: serviceCodeJsonElement is null!");
            }
            String serviceCode = serviceCodeJsonElement.getAsString();
            if (StringUtils.isEmpty(serviceCode))
            {
                return WsUtils.errorMsg(WsStatusCode.SC_BAD_REQUEST,
                    "invalid request: serviceCode should not be empty!");
            }
            //根据服务码取出相应的服务类
            IService serviceObj = (IService)getFromModelMap(serviceCode);
            
            if (null == serviceObj)
            {
                return WsUtils.errorMsg(WsStatusCode.SC_BAD_REQUEST,
                    "invalid request: the service '"
                        + serviceCode
                        + "' is not available now! The 【SIMULATOR spring applicationContext】 could not find a bean which is named: "
                        + serviceCode + "!");
            }
            
            //验证service参数
            Class<?> serviceClass = serviceObj.getClass();
            
            //验证必传参数并将结果填充到相应的字段中
            Field[] fields = serviceClass.getDeclaredFields();
            if (fields != null && fields.length > 0)
            {
                for (Field field : fields)
                {
                    //该注解直接加在字段上面，因此可以直接通过字段去获取注解对象
                    //将所有的必传参数（加入了RequiredParam注解的参数）的信息（这些信息也是对象，数据源为json字符串所还原得到的对象）填充到serviceObj中
                    WsRequestParam wsRequestParam = field.getAnnotation(WsRequestParam.class);
                    if (wsRequestParam != null)
                    {
                        //则该字段是一个必传参数
                        String paramNameAnno = wsRequestParam.value();//该注解的value值一般都没有设置，默认为“”
                        //                        requiredParam.desc()
                        //如果该必传字段的字段名注解为空，则采用该字段的声明名称
                        if (StringUtils.isEmpty(paramNameAnno))
                        {
                            paramNameAnno = field.getName();
                        }
                        try
                        {
                            field.setAccessible(true);
                            //利用字段的名称，字段的Class所属的Type类型（field对象提供了该属性），requestJsonObject数据对象，以及Gson框架，去还原出一个对象
                            Object inflatedObj = inflateField(paramNameAnno, requestJsonObject, field);
                            //利用json中的字段参数信息还原得到的该字段对应的对象
                            if (inflatedObj == null && wsRequestParam.required() == true)
                            {
                                return WsUtils.errorMsg(WsStatusCode.SC_BAD_REQUEST,
                                    "invalid request: missing param element: " + paramNameAnno);
                            }
                            //妙就妙在，上面这段代码配合以下的代码，实际上消除了（显式）对象强制转换的语句声明！
                            field.set(serviceObj, inflatedObj);//将还原出的对象直接注入到service对象的该参数字段中
                        }
                        catch (JsonSyntaxException e)
                        {
                            //字段填充失败
                            logger.error("inflating the required param failed! The param name is: " + paramNameAnno, e);
                            return WsUtils.errorMsg(WsStatusCode.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        }
                        catch (Exception e)
                        {
                            //字段填充失败
                            logger.error("inflating the required param failed! The param name is: " + paramNameAnno, e);
                            return WsUtils.errorMsg(WsStatusCode.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        }
                    }
                    
                    Resource resource = field.getAnnotation(Resource.class);
                    if (resource != null)
                    {
                        field.setAccessible(true);
                        field.set(serviceObj, getFromServiceMap(field.getType().getName()));
                    }
                }
            }
            //事先构造出需要返回回去的对象
            JsonObject responseJsonObject = new JsonObject();
            //执行webService服务，将待返回的对象作为参数传递给待执行的方法体，以便进行返回参数的“搭车式”填充
            //“搭车式”填充的好处是：可以在这个方法体内统一初始化，而具体的业务类仅需专注于业务即可，省去了一步初始化返回对象的操作
            serviceObj.execute(responseJsonObject);
            
            //统一增加一个默认的成功状态值！以最大化地降低业务代码的累赘性
            JsonElement resultCodeElement = responseJsonObject.get(WSConstants.RESULT_CODE);
            if (resultCodeElement == null || StringUtils.isEmpty(resultCodeElement.getAsString()))
            { //如果在响应中不声明RESULT_CODE字段
              //那么默认该字段的结果为：WsStatusCode.SC_OK
                responseJsonObject.addProperty(WSConstants.RESULT_CODE, WsStatusCode.SC_OK);
            }
            return responseJsonObject.toString();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据字段的类型，去填充该字段
     * @param paramName
     * @param requestJsonObject
     * @param field
     * @return
     */
    private static Object inflateField(String paramNameAnno, JsonObject requestJsonObject, Field field)
        throws Exception
    {
        Gson gson = new Gson();
        JsonElement jsonElement = requestJsonObject.get(paramNameAnno);
        if (jsonElement == null)
        {
            //json中不含该字段，则返回null
            return null;
        }
        //若含这个字段，则尝试进行转换
        //根据field的字段类型信息（包括了泛型的信息）进行转换
        //return gson.fromJson(jsonElement, TypeToken.get(field.getGenericType()).getType());
        return gson.fromJson(jsonElement, field.getGenericType());//更简单而有效的写法！！！！modified @2013-07-13 01:12:02
        //无论该field是一个参数化的类型，还是一个普通的class类型，均可使用field.getGenericType()去获取它的实际的类型
    }
    
}
