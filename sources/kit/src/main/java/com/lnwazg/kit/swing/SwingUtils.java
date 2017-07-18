package com.lnwazg.kit.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.lnwazg.kit.executor.ExecMgr;

/**
 * 基础工具类
 * @author Administrator
 * @version 2016年4月16日
 */
public class SwingUtils
{
    public static final String INITIAL_VALUE = "";
    
    public static final String INPUT_DIALOG_TITLE = "请输入";
    
    /**
     * 针对字典应用，修复Swing的兼容性问题
     * @author Administrator
     */
    public static void patchJdkDictAppImeBug()
    {
        //        System.setProperty("java.awt.im.style", "on-the-spot");
        System.setProperty("java.awt.im.style", "below-the-spot");//JDK1.7中文输入法bug兼容
        //        System.setProperty("java.awt.im.style", "no-spot"); //经过测试，这样就可以神奇地修复中文输入法报错的问题！亲测有效！真是神方法！但是副作用同样明显，就是在查询中文的时候会导致不必要的pinyin翻译
        System.setProperty("sun.java2d.noddraw", "true");//JDK7的bug。   The problem doesn't seem to be with the IME specifically, but rather with the rendering calls that get made by the text field while the IME is active.
    }
    
    /**
     * 显示当前的工作环境
     * @author Administrator
     */
    public static void showEnv()
    {
        System.out.println(String.format("java.library.path的路径为：\n%s", System.getProperty("java.library.path")));
    }
    
    /**
    * 追加显示的文本
    * @param textArea
    * @param appendText
    */
    public static void appendText(final JTextPane stateTextPane, String s, Color color)
    {
        ExecMgr.guiExec.execute(new Runnable()
        {
            public void run()
            {
                SimpleAttributeSet attrSet = new SimpleAttributeSet();
                StyleConstants.setForeground(attrSet, color);
                StyleConstants.setFontSize(attrSet, 12);//字体
                Document doc = stateTextPane.getDocument();
                try
                {
                    doc.insertString(doc.getLength(), s, attrSet);
                }
                catch (BadLocationException e)
                {
                    e.printStackTrace();
                }
                stateTextPane.setSelectionStart(stateTextPane.getText().length());//滚动到最后
            }
        });
    }
    
    /**
     * 弹出警告提示
     * @param component
     * @param alertContent
     */
    public static void alert(final Component component, String alertContent)
    {
        ExecMgr.guiExec.execute(new Runnable()
        {
            public void run()
            {
                JOptionPane.showMessageDialog(component, alertContent);
            }
        });
    }
    
    /**
     * 美化按钮<br>
     * 去除获得焦点时候的那个虚线
     * @author Administrator
     * @param btn
     */
    public static void beautyBtn(AbstractButton btn)
    {
        btn.setFocusPainted(false);
    }
    
    /**
     * 打开一个输入对话框，默认标题为INPUT_DIALOG_TITLE所定义的内容，提示信息为参数message
     * @param message
     * @return
     */
    public static String ShowDialogAndGetValue(String message)
    {
        return (String)JOptionPane.showInputDialog(null, message, INPUT_DIALOG_TITLE, JOptionPane.PLAIN_MESSAGE, null, null, null);
    }
    
    /**
     * 打开一个输入对话框，默认标题为INPUT_DIALOG_TITLE所定义的内容，提示信息为空
     * @return
     */
    public static String getPopupInputDialogString()
    {
        return ShowDialogAndGetValue(INITIAL_VALUE);
    }
    
    /**
     * 选择文件
     * @author nan.li
     * @param parentComponent
     * @param title
     * @return
     */
    public static File chooseFile(Component parentComponent, String title)
    {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.showDialog(parentComponent, title);
        return jfc.getSelectedFile();
    }
    
    /**
     * 根据扩展名过滤去选取文件
     * @author nan.li
     * @param parentComponent
     * @param title
     * @param suffix
     * @return
     */
    public static File chooseFile(Component parentComponent, String title, String suffix)
    {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(new FileNameExtensionFilter("." + suffix, suffix));
        jfc.showDialog(parentComponent, title);
        return jfc.getSelectedFile();
    }
    
    public static File chooseFile(Component parentComponent, String title, File curDir, String suffix)
    {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(new FileNameExtensionFilter("." + suffix, suffix));
        if (curDir != null)
        {
            jfc.setCurrentDirectory(curDir);
        }
        jfc.showDialog(parentComponent, title);
        return jfc.getSelectedFile();
    }
    
    /**
     * 选择一个目录
     * @author nan.li
     * @param parentComponent
     * @param title
     * @return
     */
    public static File chooseDirectory(Component parentComponent, String title)
    {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.showDialog(parentComponent, title);
        return jfc.getSelectedFile();
    }
    
    /** 
     * 启动标准swing窗体
     * @param targetFrame  传入的JFrame对象
     * @param width  欲展示的窗体的宽度
     * @param height  欲展示的窗体的高度
     * @see [类、类#方法、类#成员]
     */
    public static void runJframe(final JFrame targetFrame, final int width, final int height)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                int windowWidth = width; //获得窗口宽
                int windowHeight = height; //获得窗口高
                Toolkit kit = Toolkit.getDefaultToolkit(); //定义工具包
                Dimension screenSize = kit.getScreenSize(); //获取屏幕的尺寸
                int screenWidth = screenSize.width; //获取屏幕的宽
                int screenHeight = screenSize.height; //获取屏幕的高
                //重设窗体的位置（居中显示）
                targetFrame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);//设置窗口居中显示
                targetFrame.setTitle(targetFrame.getClass().getSimpleName());
                targetFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                targetFrame.setSize(width, height);
                //                JFrame.setDefaultLookAndFeelDecorated(true);
                //                f.pack();//自动收缩JFrame的大小
                targetFrame.setVisible(true);
            }
        });
    }
    
}
