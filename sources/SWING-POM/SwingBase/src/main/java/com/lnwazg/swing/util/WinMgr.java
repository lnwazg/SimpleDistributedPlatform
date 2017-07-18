package com.lnwazg.swing.util;

import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.property.PropertyUtils;
import com.lnwazg.swing.util.uiloader.LocalUiLoader;

/**
 * 窗口管理器
 * @author nan.li
 * @version 2014-11-6
 */
public class WinMgr
{
    /**
     * 所有的配置属性信息的Map对象
     */
    public static Map<String, String> configs = new HashMap<String, String>();
    
    private static HashMap<Class<?>, Object> winInstanceMap = new HashMap<Class<?>, Object>();
    
    public static int appId = -100;
    
    /**
     * 工作执行的开关<br>
     * 默认为true，表示执行。
     * 可将其变成false，则不执行
     */
    public static boolean jobExecSwitch = true;
    
    /**
     * 默认的定时任务搜索的包
     */
    public static final String DEFAULT_JOB_SCAN_PACKAGE = "com.lnwazg.job";
    
    /**
     * 任务栏图标
     */
    public static TrayIcon trayIcon;
    
    /**
     * 任务栏图标提示消息的标题
     */
    public static String trayMsgCaption;
    
    /**
     * 注册
     * @author nan.li
     * @param t
     */
    public static <T> void reg(T t)
    {
        winInstanceMap.put(t.getClass(), t);
    }
    
    /**
     * 取回
     * @author nan.li
     * @param frameClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T win(Class<T> frameClass)
    {
        return (T)winInstanceMap.get(frameClass);
    }
    
    /**
     * 获取用户目录下面的指定文件的全路径
     * @author Administrator
     * @param string
     * @return
     */
    public static String getUserDirFilePath(String fileName)
    {
        try
        {
            return new File(LocalUiLoader.CONFIG_FILE_DIR + File.separator + LocalUiLoader.NECESSARY_FILES_DIR, fileName).getCanonicalPath();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 将配置信息保存到用户本地配置中
     * @author nan.li
     * @param key
     * @param value
     */
    public static void saveConfig(String key, String value)
    {
        PropertyUtils.set(new File(LocalUiLoader.CONFIG_FILE_DIR, LocalUiLoader.CONFIG_FILE_NAME), key, value);
        configs.put(key, value);
    }
    
    /**
     * 读配置信息
     * @author Administrator
     * @param key
     * @return
     */
    public static String readConfig(String key)
    {
        return configs.get(key);
    }
    
    /**
     * 取配置
     * @author nan.li
     * @param key
     * @return
     */
    public static String getConfig(String key)
    {
        return readConfig(key);
    }
    
    /**
     * 设置配置
     * @author nan.li
     * @param key
     * @param value
     */
    public static void setConfig(String key, String value)
    {
        saveConfig(key, value);
    }
    
    /**
     * 取配置
     * @author nan.li
     * @param key
     * @return
     */
    public static String cfg(String key)
    {
        return readConfig(key);
    }
    
    public static String config(String key)
    {
        return readConfig(key);
    }
    
    /**
     * 设置配置
     * @author nan.li
     * @param key
     * @param value
     */
    public static void cfg(String key, String value)
    {
        saveConfig(key, value);
    }
    
    public static void config(String key, String value)
    {
        saveConfig(key, value);
    }
    
    /**
     * 显示任务栏图标弹出信息
     * @author nan.li
     * @param caption
     * @param text
     * @param messageType
     */
    public static void showTrayMessage(String caption, String text, MessageType messageType)
    {
        Logs.d(text);
        if (trayIcon != null)
        {
            ExecMgr.guiExec.execute(() -> {
                trayIcon.displayMessage(caption, text, messageType);
            });
        }
    }
    
    public static void showTrayMessage(String caption, String text)
    {
        showTrayMessage(caption, text, MessageType.INFO);
    }
    
    public static void showTrayMessage(String text)
    {
        showTrayMessage(trayMsgCaption, text);
    }
    
}