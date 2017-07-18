package com.lnwazg.swing.xmlbuilder.builder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.apache.commons.lang3.StringUtils;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.swing.util.WinMgr;
import com.lnwazg.swing.xmlbuilder.XmlLayoutBuilder;
import com.lnwazg.swing.xmlbuilder.builder.executor.GlobalAttrExecutor;
import com.lnwazg.swing.xmlbuilder.map.SmartHashMap;
import com.lnwazg.swing.xmlbuilder.util.NodexConstants;

/**
 * 全局的属性设置
 * @author nan.li
 * @version 2016年2月3日
 */
public class GlobalAttrBuilder
{
    //占位，用于强行执行类加载
    public static String a;
    
    /**
     * 目前支持的所有的属性构建器以及相应的回调函数
     */
    public static Map<String, GlobalAttrExecutor> supportedAttrNameBuilderMap = new SmartHashMap<String, GlobalAttrExecutor>();
    
    //将所有可支持的属性以及相应的构建工具都设置到这里，这样就可以随时动态扩展，一目了然！
    static
    {
        Logs.i("目前SwingXml框架支持的全局属性如下：\n");
        supportedAttrNameBuilderMap.put("iconImage", new GlobalAttrExecutor()
        {
            @Override
            public void exec(JFrame frame, String attrValue)
            {
                Image icon = Toolkit.getDefaultToolkit().createImage(GlobalAttrBuilder.class.getClassLoader().getResource(attrValue));
                frame.setIconImage(icon);
            }
            
            @Override
            public String getDescription()
            {
                return "设置frame的图标";
            }
        });
        supportedAttrNameBuilderMap.put("title", new GlobalAttrExecutor()
        {
            @Override
            public void exec(JFrame frame, String attrValue)
            {
                frame.setTitle(attrValue);
            }
            
            @Override
            public String getDescription()
            {
                return "设置frame的标题";
            }
        });
        supportedAttrNameBuilderMap.put("defaultCloseOperation", new GlobalAttrExecutor()
        {
            @Override
            public void exec(JFrame frame, String attrValue)
            {
                if ("EXIT_ON_CLOSE".equals(attrValue))
                {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
            
            @Override
            public String getDescription()
            {
                return "设置frame的默认的关闭操作";
            }
        });
        supportedAttrNameBuilderMap.put("visible", new GlobalAttrExecutor()
        {
            @Override
            public void exec(JFrame frame, String attrValue)
            {
                frame.setVisible(Boolean.valueOf(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "设置frame的可见性";
            }
        });
        supportedAttrNameBuilderMap.put("pack", new GlobalAttrExecutor()
        {
            @Override
            public void exec(JFrame frame, String attrValue)
            {
                boolean pack = Boolean.valueOf(attrValue);
                if (pack)
                {
                    frame.pack();
                }
            }
            
            @Override
            public String getDescription()
            {
                return "设置frame是否压缩打包尺寸";
            }
        });
        
        supportedAttrNameBuilderMap.put("resizable", new GlobalAttrExecutor()
        {
            @Override
            public void exec(JFrame frame, String attrValue)
            {
                boolean resizable = Boolean.valueOf(attrValue);
                frame.setResizable(resizable);
            }
            
            @Override
            public String getDescription()
            {
                return "设置frame是否可以调整尺寸";
            }
        });
        supportedAttrNameBuilderMap.put("splash", new GlobalAttrExecutor()
        {
            @Override
            public void exec(final JFrame frame, String attrValue)
            {
                //打开splash
                if ("true".equals(attrValue))
                {
                    //则采用默认值
                    attrValue = "common/default/splash/splash.png";
                }
                //否则，就用指定的启动画面图片
                
                //先隐藏主窗口
                frame.setVisible(false);
                final JWindow jWindow = new JWindow();
                Container container = jWindow.getContentPane(); // 得到容器  
                jWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // 设置光标  
                URL url = GlobalAttrBuilder.class.getClassLoader().getResource(attrValue); // 图片的位置  
                if (url != null)
                {
                    container.add(new JLabel(new ImageIcon(url)), BorderLayout.CENTER); // 增加图片  
                }
                final JProgressBar progress = new JProgressBar(1, 100); // 实例化进度条  
                progress.setStringPainted(true); // 描绘文字  
                progress.setString("加载程序中,请稍候......"); // 设置显示文字  
                progress.setBackground(Color.white); // 设置背景色  
                container.add(progress, BorderLayout.SOUTH); // 增加进度条到容器上  
                
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); // 得到屏幕尺寸  
                jWindow.pack(); // 窗口适应组件尺寸  
                jWindow.setLocation((screen.width - jWindow.getSize().width) / 2, (screen.height - jWindow.getSize().height) / 2); // 设置窗口位置  
                
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        jWindow.toFront(); // 窗口前端显示  
                        jWindow.setVisible(true); // 显示窗口  
                        try
                        {
                            for (int i = 0; i < 100; i++)
                            {
                                Thread.sleep(10); // 线程休眠  
                                progress.setValue(progress.getValue() + 1); // 设置进度条值  
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        jWindow.dispose(); // 释放窗口  
                        
                        //重新展示主窗口
                        //                        frame.setLocationRelativeTo(null);
                        //                        //WinMgr.win(mainFrameClass).setResizable(false);//关闭默认的不可调整大小，由各个JFrame自己去决定！
                        //                        //WinMgr.win(mainFrameClass).pack();//并不是所有的都要pack，是否pack，由具体的应用程序JFrame来决定
                        //                        //设置frame窗体被加载好之后的显示位置
                        //                        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
                        //                        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
                        //                        int left = (screenWidth - frame.getSize().width) / 2;
                        //                        int top = (screenHeight - frame.getSize().height) / 2;
                        //                        frame.setLocation(left, top);//设置窗口居中显示
                        //最后令其重新显示
                        frame.setVisible(true);
                    }
                }).start();
            }
            
            @Override
            public String getDescription()
            {
                return "设置frame的启动画面";
            }
        });
        
        supportedAttrNameBuilderMap.put("minToTray", new GlobalAttrExecutor()
        {
            @Override
            public void exec(final JFrame frame, String attrValue)
            {
                //这个配置的值形如以下两种方式：
                //icons/ss.ico|翻墙时代，已经来临！
                //翻墙时代，已经来临！
                String[] strings = StringUtils.split(attrValue, NodexConstants.REGEX_SEPERATOR_VERTICAL_LINE);
                //图标
                ImageIcon trayImageIcon = null;
                //图标提示信息
                String toolTip = null;
                if (strings.length == 2)
                {
                    trayImageIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(GlobalAttrBuilder.class.getClassLoader().getResource(strings[0])));
                    toolTip = strings[1];
                }
                else if (strings.length == 1)
                {
                    //使用默认的图标
                    trayImageIcon = new ImageIcon(Toolkit.getDefaultToolkit()
                        .createImage(GlobalAttrBuilder.class.getClassLoader().getResource("common/default/icon/default_frame_icon.png")));
                    toolTip = strings[0];
                }
                else
                {
                    return;
                }
                //设置最小化时候的动作
                frame.addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        frame.setExtendedState(JFrame.ICONIFIED);//最小化，并且不可见了
                    }
                    
                    @Override
                    public void windowIconified(WindowEvent e)
                    {
                        frame.setVisible(false);
                    }
                });
                //关闭时什么都不做
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                try
                {
                    if (SystemTray.isSupported())
                    {
                        // 判断当前平台是否支持系统托盘
                        SystemTray st = SystemTray.getSystemTray();
                        TrayIcon trayIcon = new TrayIcon(trayImageIcon.getImage());
                        trayIcon.setToolTip(toolTip);//托盘图标提示
                        
                        //左击该托盘图标，则打开窗体
                        trayIcon.addMouseListener(new MouseAdapter()
                        {
                            public void mouseClicked(MouseEvent e)
                            {
                                //当左击窗口时
                                if (e.getButton() == MouseEvent.BUTTON1)
                                {
                                    frame.setVisible(true);//设置窗口可见
                                    frame.setExtendedState(JFrame.NORMAL);//正常显示窗口
                                }
                            }
                        });
                        //设置托盘图标右击弹出的菜单
                        PopupMenu popupMenu = new PopupMenu();
                        MenuItem exitSubMenu = new MenuItem("Exit");
                        exitSubMenu.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                System.exit(0);
                            }
                        });
                        popupMenu.add(exitSubMenu);
                        trayIcon.setPopupMenu(popupMenu); // 为托盘添加右键弹出菜单
                        st.add(trayIcon);//将托盘图标加入到系统托盘中
                        WinMgr.trayIcon = trayIcon;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            
            @Override
            public String getDescription()
            {
                return "设置frame是否最小化到任务栏中";
            }
        });
        
        //对所支持的属性名进行排序后展示
        List<String> keys = new ArrayList<String>();
        keys.addAll(supportedAttrNameBuilderMap.keySet());
        Collections.sort(keys, new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                return o1.compareToIgnoreCase(o2);
            }
        });
        for (String key : keys)
        {
            Logs.i(String.format("%s——%s", key, supportedAttrNameBuilderMap.get(key).getDescription()));
        }
        System.out.println();
        //        Logs.i(String.format("当前SwingXml框架所支持的所有全局属性为：\n%s", keys));
    }
    
    /**
     * 构建JFrame的全局属性
     * @author nan.li
     * @param frame
     */
    public static void build(JFrame frame)
    {
        Logs.i("开始构建全局属性...");
        SmartHashMap<String, String> globalAttrsMap = XmlLayoutBuilder.globalAttrsMap;
        Logs.i("全局属性表的内容为：" + globalAttrsMap);
        checkConflicts(globalAttrsMap);
        for (String attrName : supportedAttrNameBuilderMap.keySet())
        {
            //还得检测当前的节点是否包含该attrName。如果当前节点包含了该attrName，则将其值取出来，执行。否则，忽略之
            if (globalAttrsMap.containsKey(attrName))
            {
                Logs.d(String.format("开始设置全局属性:%s【%s】", attrName, supportedAttrNameBuilderMap.get(attrName).getDescription()));
                String attrValue = globalAttrsMap.get(attrName);
                GlobalAttrExecutor attrExecutor = (GlobalAttrExecutor)supportedAttrNameBuilderMap.get(attrName);//获取相应的设置属性的执行器
                //接下来，就是利用执行器，执行相应的属性设置程序
                try
                {
                    attrExecutor.exec(frame, attrValue);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        checkUnusedAttrs(globalAttrsMap);
        Logs.i("全局属性构建完毕！");
    }
    
    /**
     * 检查存在性冲突
     * @author Administrator
     * @param globalAttrsMap
     */
    private static void checkConflicts(SmartHashMap<String, String> globalAttrsMap)
    {
        if (globalAttrsMap.containsKey("minToTray") && globalAttrsMap.containsKey("defaultCloseOperation"))
        {
            Logs.w("警告：minToTray和defaultCloseOperation两者冲突，只能同时存在一种，否则会出现无法正常工作的问题！");
        }
    }
    
    private static void checkUnusedAttrs(SmartHashMap<String, String> globalAttrsMap)
    {
        //最后，清算一下哪些属性从未被使用过，并给出提示
        Set<String> unusedKeys = globalAttrsMap.getUnUsedKeys();
        if (unusedKeys != null && unusedKeys.size() > 0)
        {
            Logs.w(String.format("以下全局属性:%s 从未被使用过，您是否写错了属性，or 您的框架需要在FrameGlobalBuilder里扩展新的属性支持了？", unusedKeys));
        }
    }
}
