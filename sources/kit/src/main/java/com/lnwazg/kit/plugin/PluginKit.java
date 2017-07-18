package com.lnwazg.kit.plugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 插件化工具包
 * @author nan.li
 * @version 2017年2月21日
 */
public class PluginKit
{
    /**
     * 加载某个jar包的模块<br>
     * 该jar包的启动入口必须实现IPlugin接口
     * @author nan.li
     * @param jarPath  
     * @param entryClassFullPath  入口类必须实现IPlugin接口
     */
    public static void loadModule(String jarPath, String entryClassFullPath)
    {
        //        ClassLoader loader = new URLClassLoader(new URL[]{new URL("file://jar路径\plugin.jar")});
        ClassLoader loader;
        try
        {
            loader = new URLClassLoader(new URL[] {new URL(jarPath)});
            //        Class<?> pluginCls = loader.loadClass("com.test.TestPlugin");
            Class<?> pluginCls = loader.loadClass(entryClassFullPath);
            IPlugin plugin = (IPlugin)pluginCls.newInstance();
            plugin.init();
            plugin.load();
            plugin.unload();
            plugin.destroy();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args)
    {
        loadModule("file:/D:/workspace_x64/plugin1/target/plugin1-0.0.1-SNAPSHOT.jar", "com.lnwazg.App");
        loadModule("file:/D:/workspace_x64/plugin2/target/plugin2-0.0.1-SNAPSHOT.jar", "com.lnwazg.App");
    }
    
}
