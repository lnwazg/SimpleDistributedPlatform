package com.lnwazg.swing.util.cfg;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import com.lnwazg.kit.file.FileKit;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.property.PropertyUtils;
import com.lnwazg.kit.reflect.ClassKit;
import com.lnwazg.swing.util.WinMgr;
import com.lnwazg.swing.util.uiloader.LocalUiLoader;

/**
 * 自动生成配置文件的工具
 * @author nan.li
 * @version 2015-10-30
 */
public class ConfigTool
{
    /**
     * 默认的主类的class全路径
     */
    private static final String DEFAULT_MAIN_FRAME_CLASS = "com.lnwazg.ui.MainFrame";
    
    /**
     * 配置文件的基本路径，默认初始化为windows版本的基本路径：C:/Windows/LNWAZG/
     */
    public static String CONFIG_BASEPATH;
    
    static
    {
        CONFIG_BASEPATH = FileKit.getConfigBasePathForAll();
    }
    
    /**
     * 生成项目的配置信息文件
     * @author Administrator
     * @param clazz 
     * @param mainFrameTitle 
     * @param mainFrameClass
     * @return 
     */
    public static boolean genPrjConfigs(Class<?> clazz, String mainFrameTitle, String mainFrameClass)
    {
        URL url = clazz.getClassLoader().getResource("");
        if (url == null)
        {
            Logs.w("当前运行环境在jar包中，无须检查项目配置文件（即便检查了也无法在jar包中动态生成配置文件）!");
            return false;
        }
        File installPathFile = null;
        File configFile = null;
        boolean ret = false;
        try
        {
            Logs.i("检查项目所需的配置文件...");
            installPathFile = new File(new URL(clazz.getClassLoader().getResource("") + "../../src/main/resources/installPath.cfg").getFile());
            configFile = new File(new URL(clazz.getClassLoader().getResource("") + "../../src/main/resources/config.properties").getFile());
            if (!installPathFile.exists())
            {
                Logs.w("项目路径配置文件不存在，开始生成项目路径配置文件...");
                installPathFile.getParentFile().mkdirs();
                String data = String.format("%s", UUID.randomUUID());
                FileUtils.writeStringToFile(installPathFile, data, CharEncoding.UTF_8);
                ret = true;
            }
            else
            {
                Logs.i("项目路径配置文件已存在，跳过...");
            }
            
            if (!configFile.exists())
            {
                Logs.w("项目配置信息文件不存在，开始生成项目配置信息文件...");
                configFile.getParentFile().mkdirs();
                String data = String.format("MAIN_FRAME_TITLE=%s\r\nMAIN_FRAME_CLASS=%s\r\n", mainFrameTitle, mainFrameClass);
                FileUtils.writeStringToFile(configFile, data, CharEncoding.UTF_8);
                ret = true;
            }
            else
            {
                Logs.i("项目配置信息文件已存在，跳过...");
            }
            
            //根据配置文件检查那个files文件夹是否存在。如果存在，则需要检查并列出其内所有的文件，到那个files配置项中。供代码读取
            URL filesDirUrl = ConfigTool.class.getClassLoader().getResource(LocalUiLoader.NECESSARY_FILES_DIR + "/");
            if (filesDirUrl != null)
            {
                Logs.i(String.format("检测到用户自定义文件夹:%s", LocalUiLoader.NECESSARY_FILES_DIR));
                File filesDir;
                try
                {
                    //获得了jar包的那个目录的文件目录对象
                    filesDir = new File(filesDirUrl.toURI());
                    //列出所有的子文件
                    File[] neceFiles = filesDir.listFiles();
                    StringBuilder sBuilder = new StringBuilder();
                    for (File f : neceFiles)
                    {
                        sBuilder.append(f.getName()).append("|");
                    }
                    if (StringUtils.isNotEmpty(sBuilder.toString()))
                    {
                        //若存在，则截取掉最后一个|
                        sBuilder.deleteCharAt(sBuilder.length() - 1);
                        if (!sBuilder.toString().equals(PropertyUtils.get(configFile, "USER_FILES")))
                        {
                            Logs.i("用户自定义文件夹内的文件列表发生变化，将其更新到项目配置文件中...");
                            //然后将信息写入到项目的配置文件中
                            PropertyUtils.set(configFile, "USER_FILES", sBuilder.toString());
                            ret = true;
                        }
                    }
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
            }
            
            //适时添加定时器的配置项信息
            if (StringUtils.isNotEmpty(PropertyUtils.get(configFile, "JOB_CONFIG")) || StringUtils.isNotEmpty(PropertyUtils.get(configFile, "JOB_SCAN_PACKAGE")) || ClassKit.getClasses(WinMgr.DEFAULT_JOB_SCAN_PACKAGE).size() > 0)
            {
                if (StringUtils.isEmpty(PropertyUtils.get(configFile, "JOB_SWITCH")))
                {
                    Logs.i(String.format("检测到【配置项JOB_CONFIG】或【配置项JOB_SCAN_PACKAGE】或【 默认的job scan package下面的类列表】非空，并且欠缺JOB_SWITCH配置项，因此增加定时器开关配置项JOB_SWITCH..."));
                    PropertyUtils.set(configFile, "JOB_SWITCH", "true");
                    ret = true;
                }
            }
            
            Logs.i("项目所需的配置文件检查全部OK!");
            System.out.println();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ret;
    }
    
    /**
     * 生成项目的配置文件
     * @author nan.li
     * @param clazz
     * @param mainFrameTitle
     */
    public static boolean genPrjConfigs(Class<?> clazz, String mainFrameTitle)
    {
        return genPrjConfigs(clazz, mainFrameTitle, DEFAULT_MAIN_FRAME_CLASS);
    }
    
    /**
     * 生成项目的配置文件<br>
     * 返回true，代表新生成了项目配置文件;false则从未新生成项目配置文件
     * @author Administrator
     * @param clazz
     * @return 
     */
    public static boolean genPrjConfigs(Class<?> clazz)
    {
        return genPrjConfigs(clazz, "JAVA_APP");
    }
}
