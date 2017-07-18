package com.lnwazg.kit.js;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * 一个便携小巧的JS执行环境
 * @author nan.li
 * @version 2016年4月8日
 */
@SuppressWarnings("restriction")
public class JsUtils
{
    /**
     * 加载某个js，获取其可调用引擎<br>
     * 如果无法正确地获取到，那么返回null
     * @author nan.li
     * @param scriptPath
     * @return
     */
    public static Invocable loadJs(String scriptPath)
    {
        // 创建脚本引擎管理器
        ScriptEngineManager factory = new ScriptEngineManager();
        // 创建JavaScript引擎
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // 从字符串中赋值JavaScript脚本
        File scriptFile = new File(scriptPath);
        FileReader reader = null;
        try
        {
            reader = new FileReader(scriptFile);
            engine.eval(reader);
            if (engine instanceof Invocable)
            {
                Invocable invoke = (Invocable)engine;
                return invoke;
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(reader);
        }
        return null;
    }
    
    /**
     * 调用某个js的某个方法，传入可变参数
     * @author nan.li
     * @param scriptPath
     * @param methodName
     * @param args
     * @return
     */
    public static Object invoke(String scriptPath, String methodName, Object... args)
    {
        Invocable invocable = loadJs(scriptPath);
        if (invocable != null)
        {
            try
            {
                return invocable.invokeFunction("add", args);
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
            catch (ScriptException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static Object invoke(String scriptPath)
    {
        // 创建脚本引擎管理器
        ScriptEngineManager factory = new ScriptEngineManager();
        // 创建JavaScript引擎
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // 从字符串中赋值JavaScript脚本
        File scriptFile = new File(scriptPath);
        FileReader reader = null;
        try
        {
            reader = new FileReader(scriptFile);
            return engine.eval(reader);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(reader);
        }
        return null;
    }
    
    /**
     * 循环测试调用某个方法<br>
     * 调用间隔为1秒钟
     * @author nan.li
     * @param times
     * @param scriptPath
     * @param methodName
     * @param args
     */
    public static void cycleTestInvoke(int times, String scriptPath, String methodName, Object... args)
    {
        for (int i = 0; i < times; i++)
        {
            Object obj = JsUtils.invoke(scriptPath, methodName, args);
            if (obj instanceof ScriptObjectMirror)
            {
                ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror)obj;
                for (String key : scriptObjectMirror.keySet())
                {
                    System.out.println(String.format("key:%s   value:%s", key, scriptObjectMirror.get(key)));
                }
            }
            else
            {
                System.out.println(obj);
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static void cycleTestInvoke(String scriptPath, String methodName, Object... args)
    {
        while (true)
        {
            Object obj = JsUtils.invoke(scriptPath, methodName, args);
            if (obj instanceof ScriptObjectMirror)
            {
                ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror)obj;
                for (String key : scriptObjectMirror.keySet())
                {
                    System.out.println(String.format("key:%s   value:%s", key, scriptObjectMirror.get(key)));
                }
            }
            else
            {
                System.out.println(obj);
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static void cycleTestInvoke(String scriptPath)
    {
        while (true)
        {
            Object obj = JsUtils.invoke(scriptPath);
            if (obj instanceof ScriptObjectMirror)
            {
                ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror)obj;
                for (String key : scriptObjectMirror.keySet())
                {
                    System.out.println(String.format("key:%s   value:%s", key, scriptObjectMirror.get(key)));
                }
            }
            else
            {
                System.out.println(obj);
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
}
