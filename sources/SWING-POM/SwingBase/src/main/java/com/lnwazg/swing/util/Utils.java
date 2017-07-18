package com.lnwazg.swing.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.FastDateFormat;

/**
 * 工具
 * @author  nan.li
 * @version 2013-12-26
 */
public class Utils
{
    public static final String UTF8_ENCODING = "UTF-8";
    
    public static String USER_DIR = null;
    
    public static boolean isAllEnglish(String src)
    {
        Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher m = p.matcher(src);
        return m.matches();
    }
    
    /**
     * 创建一个目录
     * @author Administrator
     * @param dir
     */
    public static void createDir(String dir)
    {
        File dirFile = new File(dir);
        if (!dirFile.exists())
        {
            dirFile.mkdirs();
        }
    }
    
    public static String getProjectPath()
    {
        java.net.URL url = Utils.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try
        {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar"))
        {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        java.io.File file = new java.io.File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }
    
    /**
     * 应用启动失败的日志
     * @author nan.li
     * @param appId
     */
    public static void startFailLog(int appId, String errMsg)
    {
        String logFilePath = String.format("%s\\swingFailLog%s.log", getProjectPath(), FastDateFormat.getInstance("yyyyMMddHHmmss").format(new Date()));
        System.out.println(logFilePath);
        try
        {
            FileUtils.writeStringToFile(new File(logFilePath),
                String.format("%s\r\nAPPID【%d】 started failed at %s", errMsg, appId, new Date().toLocaleString()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void startFailLog(String errMsg)
    {
        String logFilePath = String.format("%s\\swingFailLog%s.log", getProjectPath(), FastDateFormat.getInstance("yyyyMMddHHmmss").format(new Date()));
        System.out.println(logFilePath);
        try
        {
            FileUtils.writeStringToFile(new File(logFilePath), String.format("%s\r\n LocalUiLoader started failed at %s", errMsg, new Date().toLocaleString()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * JDK1.7输入法的兼容层
     * @author nan.li
     */
    public static void patchJDK17ImBug()
    {
        //        System.setProperty("java.awt.im.style", "below-the-spot");//JDK1.7中文输入法bug兼容
        System.setProperty("java.awt.im.style", "no-spot"); //经过测试，这样就可以神奇地修复中文输入法报错的问题！亲测有效！
        //以上这个配置仅在一种情况下不合适：GoogleTranslate。
        //在这种情况下，需要立即根据输入的内容进行实时翻译，那么很可能会因为网络延迟，而导致翻译中文的时候经常会出现过一会又翻译了拼音的这一尴尬局面！
        //还好以上这种尴尬局面的出现时机相当少（仅在我的那个人性化的软件中有可能出现），因此完全无需担心啦！因此以上就是最佳的配置啦！
        System.setProperty("sun.java2d.noddraw", "true");//JDK7的bug。   The problem doesn't seem to be with the IME specifically, but rather with the rendering calls that get made by the text field while the IME is active.
    }
}
