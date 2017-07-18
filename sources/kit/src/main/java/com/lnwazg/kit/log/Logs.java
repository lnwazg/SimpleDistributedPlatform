package com.lnwazg.kit.log;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.CharEncoding;

import com.lnwazg.kit.date.DateUtils;
import com.lnwazg.kit.exception.StackTraceKit;
import com.lnwazg.kit.executor.ExecMgr;

/**
 * 通用的简单日志的实现<br>
 * 不依赖任何的第三方组件<br>
 * 只在控制台输出<br>
 * 优势：既能满足差异化的logger.error类似的需求，还可以不必被log4j.properties所拖累<br>
 * 还能满足一般的日志要求！<br>
 * 简单，就是最美的！<br>
 * 可根据需要来决定是否输出到错误文件中（大部分情况下应该是无须生成日志文件的）<br>
 * @author nan.li
 * @version 2015-9-5
 */
public class Logs
{
    /**
     * 日志中是否加入时间戳的开关
     */
    public static boolean TIMESTAMP_LOG_SWITCH = false;
    
    /**
     * 是否生成文件日志的开关
     */
    public static boolean FILE_LOG_SWITCH = false;
    
    /**
     * 调用堆栈的打印开关
     */
    public static boolean CLASS_DETAIL_SWITCH = false;
    
    /**
     * 日志文件的编码
     */
    private static final String LOG_FILE_ENCODING = CharEncoding.UTF_8;
    
    private static final String LOG_LEVEL_DEBUG = "D";
    
    private static final String LOG_LEVEL_INFO = "I";
    
    private static final String LOG_LEVEL_WARN = "W";
    
    private static final String LOG_LEVEL_ERROR = "E";
    
    /**
     * 没有日志级别的日志
     */
    private static final String LOG_LEVEL_NONE = " ";
    
    /**
     * 日志文件的基础目录<br>
     * 该目录需要从外部进行设置进去！否则，无法写日志文件
     */
    public static String LOG_FILE_BASE_DIR = null;
    
    /**
     * 日志的目的地
     */
    static List<JTextPane> jTextPaneDests = new ArrayList<>();
    
    /**
     * 日志的目的地
     */
    static List<JTextArea> jTextAreaDests = new ArrayList<>();
    
    /**
     * 增加日志输出的目的地
     * @author nan.li
     * @param dest
     */
    public static void addLogDest(Object dest)
    {
        if ((dest instanceof JTextPane))
        {
            jTextPaneDests.add((JTextPane)dest);
        }
        else if ((dest instanceof JTextArea))
        {
            jTextAreaDests.add((JTextArea)dest);
        }
        else
        {
            System.err.println("请注意：不支持的日志输出目的地!" + dest);
        }
    }
    
    /**
     * 记日志，想记就记
     * 没有级别，那么就等同于System.out.println
     * @author nan.li
     * @param s
     */
    public static void log(Object s)
    {
        log(s, LOG_LEVEL_NONE, null);
    }
    
    public static void log(String... strs)
    {
        log(String.join("", strs), LOG_LEVEL_NONE);
    }
    
    public static void debug(Object s)
    {
        d(s);
    }
    
    public static void d(Object s)
    {
        log(s, LOG_LEVEL_DEBUG, null);
    }
    
    public static void info(Object s)
    {
        i(s);
    }
    
    public static void i(Object s)
    {
        log(s, LOG_LEVEL_INFO, null);
    }
    
    public static void warn(Object s)
    {
        w(s);
    }
    
    public static void w(Object s)
    {
        log(s, LOG_LEVEL_WARN, null);
    }
    
    public static void error(Object s)
    {
        e(s);
    }
    
    public static void e(Object s)
    {
        //入口1
        log(s, LOG_LEVEL_ERROR, null);
    }
    
    public static void e(Throwable e)
    {
        //入口1
        e(null, e);
    }
    
    public static void e(String logMessage, boolean writeToFile)
    {
        e(logMessage, null, writeToFile);
    }
    
    public static void e(String logMessage, Throwable e)
    {
        error(logMessage, e);
    }
    
    public static void error(String logMessage, Throwable e)
    {
        //入口2
        error(logMessage, e, false);
    }
    
    public static void e(String logMessage, Throwable e, boolean writeToFile)
    {
        //入口2
        error(logMessage, e, writeToFile);
    }
    
    /**
     * 最终入口
     * @author nan.li
     * @param logMessage
     * @param e
     * @param writeToFile
     */
    public static void error(final String logMessage, final Throwable e, boolean writeToFile)
    {
        log(logMessage, LOG_LEVEL_ERROR, e);
        if (e != null)
        {
            e.printStackTrace();
        }
        if (writeToFile)
        {
            //启用线程执行，不阻塞性能
            if (StringUtils.isNotEmpty(LOG_FILE_BASE_DIR))
            {
                ExecMgr.singleExec.execute(new Runnable()
                {
                    public void run()
                    {
                        writeErrorToFile(logMessage, e);
                    }
                });
            }
            else
            {
                System.err.println("警告：LOG_FILE_BASE_DIR尚未被初始化，因此错误日志文件无法正确生成！");
            }
        }
    }
    
    /**
     * 将错误信息输出到日志文件中
     * @author nan.li
     * @param logMessage
     * @param e
     */
    private static void writeErrorToFile(String logMessage, Throwable e)
    {
        try
        {
            //这个文件需要根据实际情况输出到用户的日志目录中
            //            String LOG_FILE = LocalUiLoader.CONFIG_FILE_DIR + File.separator + "logs" + File.separator + "error.log";
            String LOG_FILE = LOG_FILE_BASE_DIR + File.separator + "logs" + File.separator + "error.log";//令其摆脱对外的依赖
            File file = new File(LOG_FILE);
            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            String logContent = String.format("%s %s %s %s\r\n", genPrefixInfo(), logMessage, e != null ? e.getMessage() : "", e != null ? e.getCause() : "");
            FileUtils.write(file, logContent, LOG_FILE_ENCODING, true);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    /** 
     * 生成日志的前缀
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static String genPrefixInfo()
    {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("[").append(DateUtils.getFormattedTimeStr(DateUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN)).append("]");
        return sBuilder.toString();
    }
    
    private static void log(Object content, String logLevel, Throwable e)
    {
        String logMessage = String.format("[%s]%s %s%s",
            logLevel,
            (TIMESTAMP_LOG_SWITCH ? " [" + DateUtils.getCurStandardDateTimeStr() + "]" : ""),
            //            (CLASS_DETAIL_SWITCH ? String.format("[%s %s %s]", RunKit.getClassName(), RunKit.getMethodName(), RunKit.getLineNum()) : ""),
            content == null ? "" : content.toString() + " ",
            e != null ? e.getMessage() : "");
        //1.本地的输出
        if (logLevel == LOG_LEVEL_DEBUG || logLevel == LOG_LEVEL_INFO)
        {
            System.out.println(logMessage);
        }
        else if (logLevel == LOG_LEVEL_WARN)
        {
            System.err.println(logMessage);
        }
        else if (logLevel == LOG_LEVEL_ERROR)
        {
            if (content instanceof Throwable)
            {
                ((Throwable)content).printStackTrace();
            }
            else
            {
                System.err.println(logMessage);
            }
        }
        else if (logLevel == LOG_LEVEL_NONE)
        {
            System.out.println(content);
        }
        
        //2.文件日志的输出
        //必须按顺序输出
        if (FILE_LOG_SWITCH)
        {
            //需要生成日志的时候，才去生成
            ExecMgr.singleExec.execute(new Runnable()
            {
                public void run()
                {
                    //logs/all.log           任何日志都输出  包含所有种类的日志
                    //debug\info\warn\error  只记录对应种类的日志
                    //这样可以保持日志文件的纯粹性，更方便定位问题
                    switch (logLevel)
                    {
                        case LOG_LEVEL_NONE:
                            appendLogToFile("all", logMessage, e);
                            break;
                        case LOG_LEVEL_DEBUG:
                            appendLogToFile("all", logMessage, e);
                            appendLogToFile("debug", logMessage, e);
                            break;
                        case LOG_LEVEL_INFO:
                            appendLogToFile("all", logMessage, e);
                            //                        appendLogToFile("debug", logMessage, e);
                            appendLogToFile("info", logMessage, e);
                            break;
                        case LOG_LEVEL_WARN:
                            appendLogToFile("all", logMessage, e);
                            //                        appendLogToFile("debug", logMessage, e);
                            //                        appendLogToFile("info", logMessage, e);
                            appendLogToFile("warn", logMessage, e);
                            break;
                        case LOG_LEVEL_ERROR:
                            appendLogToFile("all", logMessage, e);
                            //                        appendLogToFile("debug", logMessage, e);
                            //                        appendLogToFile("info", logMessage, e);
                            //                        appendLogToFile("warn", logMessage, e);
                            appendLogToFile("error", logMessage, e);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        
        //3.其他位置的输出
        //准备颜色
        Color localColor = Color.black;
        switch (logLevel)
        {
            case LOG_LEVEL_DEBUG:
                localColor = new Color(255, 204, 153);//暗金色
                break;
            case LOG_LEVEL_INFO:
                localColor = new Color(153, 204, 0);//翠绿
                break;
            case LOG_LEVEL_WARN:
                localColor = Color.pink;//粉红
                break;
            case LOG_LEVEL_ERROR:
                localColor = Color.red;//大红
                break;
            case LOG_LEVEL_NONE:
                localColor = Color.green;//绿色
                break;
        }
        //开始输出
        if (jTextAreaDests.size() > 0)
        {
            for (JTextArea jTextArea : jTextAreaDests)
            {
                ExecMgr.guiExec.execute(() -> {
                    if (jTextArea.getText().length() >= 10000)
                    {
                        jTextArea.setText("");
                    }
                    jTextArea.append(logMessage + "\r\n");
                    jTextArea.setCaretPosition(jTextArea.getText().length());
                });
            }
        }
        if (jTextPaneDests.size() > 0)
        {
            for (JTextPane jTextPane : jTextPaneDests)
            {
                final Color color = localColor;
                ExecMgr.guiExec.execute(() -> {
                    Document document = jTextPane.getDocument();
                    if (document.getLength() >= 10000)
                    {
                        //及时清空，防止日志容器爆掉
                        jTextPane.setText(null);
                    }
                    try
                    {
                        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
                        StyleConstants.setForeground(attributeSet, color);
                        document.insertString(document.getLength(), logMessage + "\r\n", attributeSet);
                        jTextPane.setCaretPosition(jTextPane.getDocument().getLength());
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                });
            }
        }
    }
    
    /**
     * 追加日志到文件中
     * @author nan.li
     * @param logFileName
     * @param logMessage
     * @param e
     */
    private static void appendLogToFile(String logFileName, String logMessage, Throwable e)
    {
        try
        {
            String name = "logs" + File.separator + logFileName + ".log";
            File logFile = new File(name);
            if (!logFile.exists())
            {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }
            FileUtils.write(logFile, logMessage + "\r\n", LOG_FILE_ENCODING, true);
            if (e != null)
            {
                String stackTrace = StackTraceKit.getStackTraceString(e);
                FileUtils.write(logFile, stackTrace, LOG_FILE_ENCODING, true);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    //                        if (e != null)
    //                        {
    //                            //将错误堆栈打印到日志文件中
    //                            PrintWriter p = new PrintWriter(new FileOutputStream(all, true));
    //                            e.printStackTrace(p);
    //                            p.flush();
    //                            StreamUtils.close(p);
    //                            
    //                            p = new PrintWriter(new FileOutputStream(err, true));
    //                            e.printStackTrace(p);
    //                            p.flush();
    //                            StreamUtils.close(p);
    //                        }
}
