package com.lnwazg.swing.xmlbuilder.builder;

import java.util.Map;

import javax.swing.JFrame;

import org.apache.commons.lang3.StringUtils;

import com.lnwazg.swing.xmlbuilder.NodeX;
import com.lnwazg.swing.xmlbuilder.builder.executor.CompExecutor;
import com.lnwazg.swing.xmlbuilder.map.SmartHashMap;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * JavaFx的专属组件
 * @author nan.li
 * @version 2016年5月11日
 */
@SuppressWarnings("restriction")
public class JavaFxComponentBuilder
{
    
    /**
     * 添加javaFx的组件支持
     * @author nan.li
     * @param supportedCompNameBuilderMap
     */
    public static void addJavaFxCompsSupport(Map<String, CompExecutor> supportedCompNameBuilderMap)
    {
        //        Logs.i(String.format("开始加入JavaFx的专属组件..."));
        supportedCompNameBuilderMap.put("WebView", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                JFXPanel jfxPanel = new JFXPanel();
                //                attrsMap.use("userAgent");
                //                attrsMap.use("url");
                //立即取出这些属性，并交由eventLoop以备其用
                String userAgent = attrsMap.get("userAgent");
                String url = attrsMap.get("url");
                //交给eventLoop延迟执行
                Platform.runLater(() -> {
                    WebView webView = new WebView();
                    final WebEngine webEngine = webView.getEngine();
                    //这边延迟获取了，就有可能造成报警提示！
                    //                    if (attrsMap.containsKey("userAgent"))
                    //                    {
                    //                        webEngine.setUserAgent(attrsMap.get("userAgent"));
                    //                    }
                    //                    if (attrsMap.containsKey("url"))
                    //                    {
                    //                        webEngine.load(attrsMap.get("url"));
                    //                    }
                    if (StringUtils.isNotEmpty(userAgent))
                    {
                        webEngine.setUserAgent(userAgent);
                    }
                    if (StringUtils.isNotEmpty(url))
                    {
                        webEngine.load(url);
                    }
                    jfxPanel.setScene(new Scene(webView));
                });
                return jfxPanel;
            }
            
            @Override
            public String getDescription()
            {
                return "WebView控件，内嵌的浏览器组件";
            }
        });
        //        Logs.i(String.format("JavaFx的专属组件加入完毕！"));
    }
    
}
