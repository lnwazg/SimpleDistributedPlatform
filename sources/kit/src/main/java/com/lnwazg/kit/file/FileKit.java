package com.lnwazg.kit.file;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

/**
 * 文件工具
 * @author Administrator
 * @version 2016年4月23日
 */
public class FileKit
{
    /**
     * Mac下面，直接用相对路径，反而可以用起来！
     * 这是一种简便易行的方法！
     */
    private static final String CONFIG_BASEPATH_MAC = "JAVA_APP/";
    
    /**
     * 直接用相对路径，反而可以用起来！
     */
    private static final String CONFIG_BASEPATH_LINUX = "JAVA_APP/";
    
    /**
     * 检测某个目录是否已经存在，或者可写成功
     * @author Administrator
     * @param dir
     * @return
     */
    public static boolean testCanWriteDirOrExists(String dir)
    {
        File dirFile = new File(dir);
        return dirFile.exists() || dirFile.mkdirs();
    }
    
    /**
     * 获取我的配置文件的基准目录<br>
     * 用此法获得的基准目录，即使不是管理员登录，也可以让应用顺利执行！
     * @author Administrator
     * @return
     */
    public static String getMyConfigBasePathForWindows()
    {
        String ret = "C:/Windows/LNWAZG/";
        //尝试是否有权限写入文件夹
        if (!FileKit.testCanWriteDirOrExists(ret))
        {
            ret = "JAVA_APP/";//直接采用相对目录
        }
        if (!FileKit.testCanWriteDirOrExists(ret))
        {
            ret = "C:/Program Files/LNWAZG";
        }
        if (!FileKit.testCanWriteDirOrExists(ret))
        {
            ret = "D:/Program Files/LNWAZG";
        }
        if (!FileKit.testCanWriteDirOrExists(ret))
        {
            ret = "C:/JAVA_APPS/";
        }
        if (!FileKit.testCanWriteDirOrExists(ret))
        {
            ret = "D:/JAVA_APPS/";
        }
        if (!FileKit.testCanWriteDirOrExists(ret))
        {
            ret = "C:/LNWAZG/";
        }
        if (!FileKit.testCanWriteDirOrExists(ret))
        {
            ret = "D:/LNWAZG/";
        }
        return ret;
    }
    
    /**
     * 获得全平台的基础路径<br>
     * 真正的跨平台！
     * @author Administrator
     * @return
     */
    public static String getConfigBasePathForAll()
    {
        String ret = "";
        //根据操作系统决定真正的配置路径
        if (SystemUtils.IS_OS_WINDOWS)
        {
            ret = FileKit.getMyConfigBasePathForWindows();
        }
        else if (SystemUtils.IS_OS_LINUX)
        {
            ret = CONFIG_BASEPATH_LINUX;
        }
        else if (SystemUtils.IS_OS_MAC)
        {
            ret = CONFIG_BASEPATH_MAC;
        }
        else
        {
            //其他情况下，都是类Unix的系统，因此均采用Linux的路径设置
            ret = CONFIG_BASEPATH_LINUX;
        }
        return ret;
    }
    
}
