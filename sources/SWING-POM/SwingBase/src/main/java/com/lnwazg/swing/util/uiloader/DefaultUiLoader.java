package com.lnwazg.swing.util.uiloader;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.swing.util.Utils;
import com.lnwazg.swing.util.WinMgr;

/**
 * 默认的UI加载器，仅支持本地配置文件
 * @author nan.li
 * @version 2015-9-1
 */
public class DefaultUiLoader
{
    protected static final String UTF8_ENCODING = "UTF-8";
    
    private String[] defaultConfigKeys;
    
    private String[] defaultConfigValues;
    
    private String configFilePath;
    
    private Class<? extends JFrame> mainFrameClass;
    
    private String title;
    
    public DefaultUiLoader(String[] defaultConfigKeys, String[] defaultConfigValues, String configFilePath, Class<? extends JFrame> mainFrameClass,
        String title)
    {
        Utils.patchJDK17ImBug();
        this.defaultConfigKeys = defaultConfigKeys;
        this.defaultConfigValues = defaultConfigValues;
        this.configFilePath = configFilePath;
        this.mainFrameClass = mainFrameClass;
        this.title = title;
        WinMgr.configs = loadConfig();
        ExecMgr.guiExec.execute(new Runnable()
        {
            @Override
            public void run()
            {
                init();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, String> loadConfig()
    {
        Map<String, String> configs = new HashMap<String, String>();
        if (StringUtils.isEmpty(configFilePath))
        {
            System.out.println("Warning! Param configFilePath not specified! Will not create config file!");
            return configs;
        }
        
        File configFile = new File(configFilePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();//输出的时候，进行格式美化
        if (!configFile.exists())
        {
            System.out.println("load from defaults!");
            if (defaultConfigKeys == null || defaultConfigKeys.length == 0 || defaultConfigValues == null || defaultConfigValues.length == 0)
            {
                defaultConfigKeys = new String[] {"sampleKey"};
                defaultConfigValues = new String[] {"sampleValue"};
            }
            for (int i = 0; i < defaultConfigKeys.length; i++)
            {
                configs.put(defaultConfigKeys[i], defaultConfigValues[i]);
            }
            try
            {
                FileUtils.writeStringToFile(configFile, gson.toJson(configs), UTF8_ENCODING);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("load from configFile!");
            try
            {
                configs = gson.fromJson(FileUtils.readFileToString(configFile, UTF8_ENCODING), HashMap.class);
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        describeConfigs(configs);
        return configs;
    }
    
    /**
     * 描述配置信息
     * @author nan.li
     * @param configs
     */
    private void describeConfigs(Map<String, String> configs)
    {
        System.out.println("配置信息如下：\n============================");
        for (Map.Entry<String, String> entry : configs.entrySet())
        {
            System.out.println(String.format("[key]: %s, [value]:%s", entry.getKey(), entry.getValue()));
        }
    }
    
    private void init()
    {
        loadBeautyUI();
        JFrame mainFrame = null;
        try
        {
            mainFrame = mainFrameClass.newInstance();
            WinMgr.reg(mainFrame);
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        WinMgr.win(mainFrameClass).setTitle(title);
        WinMgr.win(mainFrameClass).setVisible(true);
        WinMgr.win(mainFrameClass).setResizable(false);
        WinMgr.win(mainFrameClass).pack();
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int left = (screenWidth - WinMgr.win(mainFrameClass).getSize().width) / 2;
        int top = (screenHeight - WinMgr.win(mainFrameClass).getSize().height) / 2;
        WinMgr.win(mainFrameClass).setLocation(left, top);//设置窗口居中显示
    }
    
    private void loadBeautyUI()
    {
        setLookAndFeel();
    }
    
    public void setLookAndFeel()
    {
        try
        {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.put("RootPane.setupButtonVisible", false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
