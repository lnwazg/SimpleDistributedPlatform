package com.lnwazg.kit.shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.lnwazg.kit.log.Logs;

/**
 * 命令行工具
 * @author Administrator
 * @version 2016年2月13日
 */
public class CmdUtils
{
    /**
     * 默认的退出值
     */
    private static final int DEFAULT_EXIT_VALUE = 0;
    
    /**
     * 执行某条命令<br>
     * 采用默认的exitValue：0
     * @author Administrator
     * @param cmd
     */
    public static void execute(String cmd)
    {
        execute(cmd, DEFAULT_EXIT_VALUE);
    }
    
    /**
     * 执行某条命令<br>
     * 指定exitValue
     * @author Administrator
     * @param cmd
     * @param exitValue
     */
    public static void execute(String cmd, int exitValue)
    {
        execute(cmd, exitValue, null, null);
    }
    
    /**
     * 执行某条命令<br>
     * 指定一批exitValue
     * @author nan.li
     * @param cmd
     * @param exitValues
     */
    public static void execute(String cmd, int[] exitValues)
    {
        execute(cmd, exitValues, null, null);
    }
    
    /**
     * 执行某条命令<br>
     * 指定exitValue<br>
     * 附加成功时候的回调函数 
     * @author nan.li
     * @param cmd
     * @param exitValue
     * @param successCallback
     */
    public static void execute(String cmd, int exitValue, SuccessCallback successCallback)
    {
        execute(cmd, exitValue, successCallback, null);
    }
    
    /**
     * 执行某条命令<br>
     * 指定一批exitValue<br>
     * 附加成功时候的回调函数 
     * @author nan.li
     * @param cmd
     * @param exitValues
     * @param successCallback
     */
    public static void execute(String cmd, int[] exitValues, SuccessCallback successCallback)
    {
        execute(cmd, exitValues, successCallback, null);
    }
    
    public static void execute(String cmd, int exitValue, FailCallback failCallback)
    {
        execute(cmd, exitValue, null, failCallback);
    }
    
    public static void execute(String cmd, int[] exitValues, FailCallback failCallback)
    {
        execute(cmd, exitValues, null, failCallback);
    }
    
    /**
     * 执行某个方法,成功时候回调某个函数，失败的时候回调另一个函数
     * @author Administrator
     * @param line
     * @param exitValue
     * @param successCallback
     * @param failCallback
     */
    public static void execute(String cmd, int exitValue, SuccessCallback successCallback, FailCallback failCallback)
    {
        execute(cmd, new int[] {exitValue}, successCallback, failCallback);
    }
    
    public static void execute(String cmd, int[] exitValues, SuccessCallback successCallback, FailCallback failCallback)
    {
        execute(cmd, exitValues, successCallback, failCallback, null);
    }
    
    public static void execute(String cmd, int[] exitValues, SuccessCallback successCallback, FailCallback failCallback, ErrorStreamCallback errorStreamCallback)
    {
        if (StringUtils.isEmpty(cmd))
        {
            return;
        }
        
        //假如是E:/path/to/foo/foo.bat这样的命令
        if (cmd.endsWith(".bat") && cmd.indexOf("cmd.exe") == -1)
        {
            //最终执行得是这样的完整命令            cmd.exe /C start /b foo.bat
            cmd = String.format("cmd.exe /C start /b %s", cmd);
        }
        Logs.i(String.format("即将执行命令:%s", cmd));
        CommandLine cmdLine = CommandLine.parse(cmd);
        try
        {
            DefaultExecutor executor = new DefaultExecutor();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            executor.setStreamHandler(streamHandler);
            executor.setExitValues(exitValues);
            int ret = executor.execute(cmdLine);
            
            //控制台编码
            String outputEncoding = "";
            if (SystemUtils.IS_OS_WINDOWS)
            {
                outputEncoding = "GBK";
            }
            else if (SystemUtils.IS_OS_UNIX)
            {
                outputEncoding = CharEncoding.UTF_8;
            }
            else
            {
                outputEncoding = CharEncoding.ISO_8859_1;
            }
            //执行过程中的控制台输出
            String outMsg = outputStream.toString(outputEncoding);
            //报错信息
            String errorMsg = errorStream.toString(outputEncoding);
            
            Logs.i("outputStream content: " + outMsg);
            Logs.i("errorStream content: " + errorMsg);
            
            //'svn' 不是内部或外部命令，也不是可运行的程序或批处理文件。
            
            //执行安装程序
            Logs.i(String.format("Cmd exitValue: %s", ret));
            if (successCallback != null)
            {
                successCallback.execute();
            }
            
            //对报错信息的处理
            if (errorStreamCallback != null)
            {
                errorStreamCallback.execute(errorMsg);
            }
        }
        catch (ExecuteException e1)
        {
            e1.printStackTrace();
            if (failCallback != null)
            {
                failCallback.execute(e1);
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            if (failCallback != null)
            {
                failCallback.execute(e1);
            }
        }
    }
    
    /**
     * 成功时候的回调函数
     * @author Administrator
     * @version 2016年2月13日
     */
    public static interface SuccessCallback
    {
        void execute();
    }
    
    /**
     * 失败时刻的回调函数
     * @author Administrator
     * @version 2016年2月13日
     */
    public static interface FailCallback
    {
        void execute(final Exception e);
    }
    
    /**
     * 报错信息的回调
     * @author nan.li
     * @version 2017年5月9日
     */
    public static interface ErrorStreamCallback
    {
        void execute(String errorMsg);
    }
}
