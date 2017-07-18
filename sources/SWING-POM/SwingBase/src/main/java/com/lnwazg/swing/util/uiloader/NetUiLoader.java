package com.lnwazg.swing.util.uiloader;

import java.awt.Toolkit;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.swing.util.Utils;
import com.lnwazg.swing.util.WinMgr;
import com.lnwazg.ws.WsRequest;
import com.lnwazg.ws.WsResponse;

/**
 * 网络UI加载器
 * @author nan.li
 * @version 2015-9-3
 */
public class NetUiLoader
{
    public NetUiLoader(int appId)
    {
        Utils.patchJDK17ImBug();
        WinMgr.appId = appId;
        WinMgr.configs = loadConfigFromNet(appId);
        if (WinMgr.configs == null)
        {
            String errMsg = "WinMgr.configs is null ,please check appId:【 " + appId + "】 configs or check your network!";
            Logs.e(errMsg);
            Utils.startFailLog(appId, errMsg);
            return;
        }
        ExecMgr.guiExec.execute(new Runnable()
        {
            @Override
            public void run()
            {
                init();
            }
        });
    }
    
    /**
     * 从远程加载所有的配置信息
     * @author nan.li
     * @param appId 
     * @return
     */
    private Map<String, String> loadConfigFromNet(int appId)
    {
        WsResponse response = new WsRequest("S00001").addParam("appId", appId).addParam("handle", 1).send();
        Logs.d(response);
        if (response.isOk())
        {
            JsonElement element = response.get("resultMap");
            Map<String, String> result = new Gson().fromJson(element, new TypeToken<Map<String, String>>()
            {
            }.getType());
            return result;
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private void init()
    {
        loadBeautyUI();
        JFrame mainFrame = null;
        String className = WinMgr.configs.get("MAIN_FRAME_CLASS");
        String title = WinMgr.configs.get("MAIN_FRAME_TITLE");
        Class<? extends JFrame> mainFrameClass = null;
        try
        {
            mainFrameClass = (Class<? extends JFrame>)Class.forName(className);
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
        catch (ClassNotFoundException e)
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
