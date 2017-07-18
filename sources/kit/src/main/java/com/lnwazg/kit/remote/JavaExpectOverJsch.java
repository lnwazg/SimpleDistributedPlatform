package com.lnwazg.kit.remote;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.nemo.javaexpect.shell.Shell;
import com.nemo.javaexpect.shell.driver.SshDriver;

/**
 * 最适合shell自动化测试的方式，但是不能有效地获取输出流的数据，并没有中文乱码的问题，并且可以有效地通过工具包解析出shell返回的结果<br>
 * 有待实现全自动化测试的功能，包括自动发布包的功能<br>
 * 这是一个很有潜力的好框架！<br>
 * 提供这几个功能： secure remote login, secure file transfer, and secure TCP/IP and X11 forwarding.<br>
 * http://blog.csdn.net/liuhenghui5201/article/details/50970492   解决sftp编码乱码问题的方案
 * @author nan.li
 * @version 2017年5月16日
 */
public class JavaExpectOverJsch
{
    public static void main(String[] args)
    {
        //        DefaultShellDriver.CONSOLE_LOG_SWITCH = false;
        
        SshDriver driver = new SshDriver("119.23.11.57", "root", "Lnwazg1989", "#");
        driver.setPort(22);
        
        //如果设置为false，可以过滤特殊字符，我自己找的github上的jar包关闭代码没有问题，但是cat到的中文日志会变为乱码
        //这边应该设置为true，则无法过滤ls -al命令的VT100控制字符串，但是却能保证中文不会乱码
        //VT100控制字符串主要是一些高亮字符，例如ls -al命令的输出结果，会将文件夹高亮显示为蓝色，而将普通文件正常显示。因此那些乱码字符其实是高亮颜色块的标记
        //综合考虑，应该设置skipVT100Filter为true，也就是说将可显示和不可显示的字符串全部显示出来（此时控制颜色显示的字符就会变成乱码，但是不会破坏掉正常字符显示）
        
        //        driver.setSkipVT100Filter(false);
        driver.setSkipVT100Filter(true);
        
        Shell shell = driver.open();
        
        shell.execute("pwd");
        
        //        shell.execute("ssh -p 22 root@119.23.11.57", "password");
        //        shell.execute("Lnwazg1989");
        
        //        String cmdResult = shell.execute("ls").getCommandResult();
        //        Logs.e(decode(cmdResult));
        //        Logs.e(decode(shell.execute("ls -al").getCommandResult()));
        //        Logs.e(decode(shell.execute("pwd").getCommandResult()));
        //        Logs.e(decode(shell.execute("whoami").getCommandResult()));
        
        //        System.out.println(shell.execute("ls").getPureCommandResult());
        //        System.out.println(shell.execute("ls -lh").getPureCommandResult());
        //        System.out.println(shell.execute("cd /opt/server/logs").getPureCommandResult());
        //        System.out.println(shell.execute("cat all.log").getPureCommandResult());
        
        shell.execute("ls");
        shell.execute("ls -al");
        shell.execute("pwd");
        shell.execute("whoami");
        
        //        shell.execute("cd /opt/server/logs && cat all.log");
        
        //        Logs.e(decode(shell.execute("cd /opt/server/logs && cat all.log").getCommandResult()));
        shell.close();
    }
    
    /**
     * 非常棒的解析执行结果内容的工具类
     * @author nan.li
     * @param source
     * @return
     */
    private static String decode(String source)
    {
        if (StringUtils.isNotEmpty(source))
        {
            if (source.split("\n").length >= 3)
            {
                String[] arrays = source.split("\n");
                List<String> contentList = new ArrayList<>();
                for (int i = 1; i < arrays.length - 1; i++)
                {
                    contentList.add(arrays[i]);
                }
                return String.join("\n", contentList);
            }
        }
        return null;
    }
    
}
