package com.lnwazg.swing.util.uiloader;

import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.eva.epc.common.util.OS;
import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.json.GsonCfgMgr;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.property.PropertyUtils;
import com.lnwazg.swing.util.Utils;
import com.lnwazg.swing.util.WinMgr;
import com.lnwazg.swing.util.cfg.ConfigTool;
import com.lnwazg.swing.util.quartz.QuartzJobLoader;
import com.lnwazg.swing.xmlbuilder.XmlLayoutBuilder;
import com.lnwazg.swing.xmlbuilder.anno.XmlBuild;
import com.lnwazg.swing.xmlbuilder.builder.AttrBuilder;
import com.lnwazg.swing.xmlbuilder.builder.ComponentBuilder;
import com.lnwazg.swing.xmlbuilder.builder.GlobalAttrBuilder;
import com.lnwazg.swing.xmlbuilder.util.NodexConstants;
import com.lnwazg.ws.WsRequest;

/**
 * 本地属性文件的ui加载器
 * 是NetUiLoader的本地版
 * @author nan.li
 * @version 2015-10-12
 */
public class LocalUiLoader
{
    /**
     * 真正的配置文件将要被安装的路径，例如   C:\Windows\LNWAZG\66ad1685-f766-4980-ba0d-f4b2c6bce55b
     */
    public static String CONFIG_FILE_DIR;
    
    /**
     * 要安装的额外的文件的目录，例如:  files
     */
    public static String NECESSARY_FILES_DIR;
    
    /**
     * 安装路径的配置文件名称
     */
    private static final String INSTALL_PATH_CFG = "installPath.cfg";
    
    /**
     * 真正的配置文件的名称
     */
    public static final String CONFIG_FILE_NAME = "config.properties";
    
    /**
     * 默认的必要的文件的路径
     */
    private static final String DEFAULT_NECESSARY_FILES_DIR = "files";
    
    public LocalUiLoader()
    {
        printPowerInfo();
        
        //生成项目配置文件目录。如果已经生成了，则会忽略执行
        //1.准备配置文件，根据实际情况，生成项目所需要的配置项
        //检查项目必要的文件夹
        NECESSARY_FILES_DIR = DEFAULT_NECESSARY_FILES_DIR;
        
        if (ConfigTool.genPrjConfigs(LocalUiLoader.class))
        {
            Logs.i("刚刚新生成了项目配置信息，请刷新项目文件夹后重新运行Main程序！");
            return;
        }
        
        //调节到本地化配置环境，直接读取本地properties文件(webservice本地模拟器)
        WsRequest.switchToLocalConfigEnv();
        
        //JDK1.7输入法bug的兼容层解决方案
        Utils.patchJDK17ImBug();
        
        //安装配置文件到本地（如果本地不存在配置文件的话）
        //2.copy安装配置文件
        if (!initConfigFile())
        {
            String errMsg = "配置信息初始化失败，请检查配置文件！";
            Logs.e(errMsg);
            //            Utils.startFailLog(errMsg);
            return;
        }
        
        //设置gson配置文件的输出路径
        GsonCfgMgr.USER_DIR = CONFIG_FILE_DIR;
        
        //加载本地的配置文件信息到一个map里
        //3.读取本地配置文件信息到内存
        WinMgr.configs = loadConfigFromLocal();
        
        //配置信息初始化状态是否OK的检查
        if (WinMgr.configs == null)
        {
            String errMsg = "properties初始化失败，请检查配置文件！";
            Logs.e(errMsg);
            //            Utils.startFailLog(errMsg);
            return;
        }
        
        //将必要的工作用到的文件全部拷贝到CONFIG_FILE_DIR
        //4.根据配置文件中的信息，执行文件拷贝工作（从jar包中copy到目的地）
        copyNecessaryFiles();
        
        //加载美化版的皮肤
        initUserInterface();
        
        //真正开始初始化并加载frame类
        ExecMgr.guiExec.execute(new Runnable()
        {
            @Override
            public void run()
            {
                init();
            }
        });
    }
    
    private void printPowerInfo()
    {
        try
        {
            List<String> list = IOUtils.readLines(LocalUiLoader.class.getClassLoader().getResourceAsStream("legal/powerfile.txt"), CharEncoding.UTF_8);
            for (String line : list)
            {
                System.out.println(line);
            }
            
            //主动触发类加载
            //            ClassKit.forName("com.lnwazg.swing.xmlbuilder.builder.executor.ComponentBuilder");
            //            ClassKit.forName("com.lnwazg.swing.xmlbuilder.builder.executor.GlobalAttrBuilder");
            //            ClassKit.forName("com.lnwazg.swing.xmlbuilder.builder.executor.AttrBuilder");
            
            //强行执行类加载
            ComponentBuilder.a = "";
            GlobalAttrBuilder.a = "";
            AttrBuilder.a = "";
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 将必要的工作用到的文件全部拷贝到CONFIG_FILE_DIR
     * @author Administrator
     */
    private void copyNecessaryFiles()
    {
        String userFiles = WinMgr.configs.get("USER_FILES");
        if (StringUtils.isNotEmpty(userFiles))
        {
            Logs.i("开始同步USER_FILES...");
            String[] userFileSplits = StringUtils.split(userFiles, NodexConstants.REGEX_SEPERATOR_VERTICAL_LINE);
            for (String name : userFileSplits)
            {
                InputStream inputStream = null;
                try
                {
                    //往上一步读取到的安装目录的文件夹内拷贝一份jar包里面的配置文件
                    inputStream = getClass().getClassLoader().getResourceAsStream(NECESSARY_FILES_DIR + "/" + name);//getResourceAsStream里面的分隔符必须用"/"
                    File targetFile = new File(CONFIG_FILE_DIR + File.separator + NECESSARY_FILES_DIR, name);
                    if (!targetFile.exists())
                    {
                        Logs.i(String.format("准备拷贝应用必备的软件到用户目录: %s", targetFile.getCanonicalPath()));
                        targetFile.getParentFile().mkdirs();
                        IOUtils.copy(inputStream, new FileOutputStream(targetFile));
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    IOUtils.closeQuietly(inputStream);
                }
            }
            Logs.i("完成USER_FILES文件夹同步！");
        }
    }
    
    /**
     * 初始化配置文件
     * 拷贝到指定目录，若已经存在，则忽略
     * @author nan.li
     */
    public static boolean initConfigFile()
    {
        InputStream inputStream = null;
        try
        {
            //从本地jar包的配置文件中读取出项目配置文件将要被安装到的目录
            inputStream = LocalUiLoader.class.getClassLoader().getResourceAsStream(INSTALL_PATH_CFG);
            CONFIG_FILE_DIR = StringUtils.trim(IOUtils.toString(inputStream));//dec6b34e-7ef4-463d-b4c9-803eaade88e0
            if (StringUtils.isEmpty(CONFIG_FILE_DIR))
            {
                return false;
            }
            IOUtils.closeQuietly(inputStream);
            
            //检查CONFIG_FILE_DIR是否是完整的路径，如果不是，则拼接
            
            //CONFIG_FILE_DIR当前可能出现的值为：
            //dec6b34e-7ef4-463d-b4c9-803eaade88e0
            //D:/autoSS/
            ///home/abc/ccc
            File checkFile = new File(CONFIG_FILE_DIR);
            if (!checkFile.exists())
            {
                //不存在
                //则判断该路径是相对路径还是绝对路径
                //如果是相对路径，则要拼接完整；否则就是绝对路径，保持不变即可
                if (isRelativePath(CONFIG_FILE_DIR))
                {
                    CONFIG_FILE_DIR = (ConfigTool.CONFIG_BASEPATH + CONFIG_FILE_DIR + File.separator);//路径补全（各个操作系统补全后的结果各不相同）
                }
            }
            
            //在框架层，初始化错误日志的输出目录
            Logs.LOG_FILE_BASE_DIR = CONFIG_FILE_DIR;
            
            //往上一步读取到的安装目录的文件夹内拷贝一份jar包里面的配置文件
            inputStream = LocalUiLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
            File configFile = new File(CONFIG_FILE_DIR, CONFIG_FILE_NAME);
            if (!configFile.exists())
            {
                configFile.getParentFile().mkdirs();
                IOUtils.copy(inputStream, new FileOutputStream(configFile));
            }
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, String.format("初始化应用配置目录%s失败！请以管理员身份登录！", CONFIG_FILE_DIR));
            return false;
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    /**
     * 判断某个path是否是相对路径<br>
     //CONFIG_FILE_DIR当前可能出现的值为：
     //dec6b34e-7ef4-463d-b4c9-803eaade88e0
     //D:/autoSS/
     ///home/abc/ccc
     * @author Administrator
     * @param cONFIG_FILE_DIR2
     * @return
     */
    private static boolean isRelativePath(String path)
    {
        //D:/autoSS/   windows绝对路径都是有冒号的
        if (path.indexOf(":") != -1)
        {
            return false;
        }
        ///home/abc/ccc     linux的全路径，是以“/”开头的
        if (path.startsWith("/"))
        {
            return false;
        }
        return true;
    }
    
    /**
     * 从安装路径的配置文件中读取本地最新的配置信息
     * @author Administrator
     * @return
     */
    private Map<String, String> loadConfigFromLocal()
    {
        File propertyFile = new File(CONFIG_FILE_DIR, CONFIG_FILE_NAME);
        return PropertyUtils.load(propertyFile);
    }
    
    @SuppressWarnings("unchecked")
    private void init()
    {
        JFrame mainFrame = null;
        //从配置文件中读取出frame的核心配置信息
        String className = WinMgr.configs.get("MAIN_FRAME_CLASS");
        String title = WinMgr.configs.get("MAIN_FRAME_TITLE");
        Class<? extends JFrame> mainFrameClass = null;
        try
        {
            //根据读取到的核心配置信息，利用反射的方式，实例化frame的类
            mainFrameClass = (Class<? extends JFrame>)Class.forName(className);
            mainFrame = mainFrameClass.newInstance();//此处即将这个JFrame的类实例化了！
            
            //设置JFrame的几个很基本的默认的属性
            mainFrame.setTitle(title);
            
            //要能够允许实际app override这些默认的措施！
            //            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //此处如果直接设置默认关闭的行为，就显得太武断了！因为那些未采用@XmlBuild注解的类将总是会被关闭！关闭行为总是会被override！
            //因此，关闭行为应该：
            /**
             * 1. 未声明@XmlBuild，则全部交由每个JFrame自己去处理，这样才是最好的归宿
             * 2. 声明了@XmlBuild，则将其托管给XmlLayoutBuilder框架去处理：
             *    2.a 若没设置minToTray属性（默认情况下），则将其设置为关闭
             *    2.b 若在xml中设置了minToTray属性，则框架会自动更新关闭行为（将其更新为啥都不做）
             *    2.c afterUIBind()回调方法中同样有机会去改变默认关闭行为
             */
            //假如有注解信息的话，那么将进行注解式初始化
            if (mainFrameClass.isAnnotationPresent(XmlBuild.class))
            {
                //获取注解信息
                XmlBuild xmlBuildInfo = mainFrameClass.getAnnotation(XmlBuild.class);
                Logs.i(String.format("检测到XmlBuild注解声明，开始构建XML界面【%s】...", xmlBuildInfo.value()));
                
                //解析注解里指定的xml文件，根据xml树结构去往上面实例化好的frame中添砖加瓦
                XmlLayoutBuilder.startBuild(xmlBuildInfo, mainFrame);//进行注解式的初始化
                //注意：此处的注解初始化的过程，可以改变默认的窗口关闭行为！
                Logs.i("构建XML界面完毕！");
            }
            
            //设置其显示位置
            mainFrame.setLocationRelativeTo(null);
            //WinMgr.win(mainFrameClass).setResizable(false);//关闭默认的不可调整大小，由各个JFrame自己去决定！
            //WinMgr.win(mainFrameClass).pack();//并不是所有的都要pack，是否pack，由具体的应用程序JFrame来决定
            //设置frame窗体被加载好之后的显示位置
            int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
            int left = (screenWidth - mainFrame.getSize().width) / 2;
            int top = (screenHeight - mainFrame.getSize().height) / 2;
            mainFrame.setLocation(left, top);//设置窗口居中显示
            
            //令其展示，此操作应该是在设置好其余所有属性之后再做
            mainFrame.setVisible(true);
            
            //尝试启动定时器（如果有定时器配置的话）
            tryStartQuartz();
            
            //将这个frame加入到窗体注册表中，方便后续根据JFrame的类名去获取对应的实例（Spring的单例思想）
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
    }
    
    /**
     * 尝试启动定时器（如果有定时器配置的话）
     * @author nan.li
     */
    private void tryStartQuartz()
    {
        QuartzJobLoader.tryLoadAllJobs();
    }
    
    /**
     * 初始化用户界面
     * 比美化版的UI更进一步！
     * @author nan.li
     */
    private void initUserInterface()
    {
        System.setProperty("apple.laf.useScreenMenuBar", "true");//可以打开“关于”上下文
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MobileIMSDK4jDemo");//关于的内容
        try
        {
            UIManager.put("RootPane.setupButtonVisible", Boolean.valueOf(false));//关闭设置按钮
            //            BeautyEyeLNFHelper.translucencyAtFrameInactive = false;//窗体静止时透明关闭
            BeautyEyeLNFHelper.launchBeautyEyeLNF();//加载美化版的皮肤
            
            //XP和2003不提供微软雅黑字体的切换
            if ((OS.isWindowsXP()) || (OS.isWindows2003()))
            {
                return;
            }
            
            //否则，就可以改变系统字体样式
            //改为采用美化版的字体样式
            String[] DEFAULT_FONT = {"Table.font", "TableHeader.font", "CheckBox.font", "Tree.font", "Viewport.font", "ProgressBar.font",
                "RadioButtonMenuItem.font", "ToolBar.font", "ColorChooser.font", "ToggleButton.font", "Panel.font", "TextArea.font", "Menu.font",
                "TableHeader.font", "OptionPane.font", "MenuBar.font", "Button.font", "Label.font", "PasswordField.font", "ScrollPane.font", "MenuItem.font",
                "ToolTip.font", "List.font", "EditorPane.font", "Table.font", "TabbedPane.font", "RadioButton.font", "CheckBoxMenuItem.font", "TextPane.font",
                "PopupMenu.font", "TitledBorder.font", "ComboBox.font"};
            
            for (int i = 0; i < DEFAULT_FONT.length; ++i)
            {
                UIManager.put(DEFAULT_FONT[i], new Font("微软雅黑", 0, 12));
            }
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
        catch (UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
