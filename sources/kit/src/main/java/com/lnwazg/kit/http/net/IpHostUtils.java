package com.lnwazg.kit.http.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/** 
 * 获取本机IP的程序 
 * @author hanchao 
 */
public class IpHostUtils
{
    
    /** 
     * InetAddress 继承自 java.lang.Object类 
     * 它有两个子类：Inet4Address 和 Inet6Address 
     * 此类表示互联网协议 (IP) 地址。  
     *  
     * IP 地址是 IP 使用的 32 位或 128 位无符号数字， 
     * 它是一种低级协议，UDP 和 TCP 协议都是在它的基础上构建的。 
     *  
     * ************************************************ 
     * 主机名就是计算机的名字（计算机名），网上邻居就是根据主机名来识别的。 
     * 这个名字可以随时更改，从我的电脑属性的计算机名就可更改。 
     *  用户登陆时候用的是操作系统的个人用户帐号，这个也可以更改， 
     *  从控制面板的用户界面里改就可以了。这个用户名和计算机名无关。 
     */
    
    /** 
     * 获取本机的IP 
     * @return Ip地址 
     */
    public static String getLocalHostIP()
    {
        String ip;
        try
        {
            /**返回本地主机。*/
            InetAddress addr = InetAddress.getLocalHost();
            /**返回 IP 地址字符串（以文本表现形式）*/
            ip = addr.getHostAddress();
        }
        catch (Exception ex)
        {
            ip = "";
        }
        return ip;
    }
    
    /** 
     * 获得本机的主机名 
     * @return 
     */
    public static String getLocalHostName()
    {
        String hostName;
        try
        {
            /**返回本地主机。*/
            InetAddress addr = InetAddress.getLocalHost();
            /**获取此 IP 地址的主机名。*/
            hostName = addr.getHostName();
        }
        catch (Exception ex)
        {
            hostName = "";
        }
        return hostName;
    }
    
    /**
     * 获取本地网卡的mac地址
     * @author nan.li
     * @return
     */
    public static String getLocalMACAddress()
    {
        try
        {
            byte[] mac;
            mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            //下面代码是把mac地址拼装成String  
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++)
            {
                if (i != 0)
                {
                    sb.append("-");
                }
                //mac[i] & 0xFF 是为了把byte转化为正整数  
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            //把字符串所有小写字母改为大写成为正规的mac地址并返回  
            return sb.toString().toUpperCase();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取远程host的真实ip地址
     * @author lnwazg@126.com
     * @param host
     * @return
     */
    public static String getHostIPByHostName(String host)
    {
        InetAddress inetAddress;
        try
        {
            inetAddress = InetAddress.getByName(host);
            return inetAddress.getHostAddress();
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /** 
     * 获得本地所有的IP地址 
     * @return 
     */
    public static String[] getAllLocalHostIP()
    {
        String[] ret = null;
        try
        {
            /**获得主机名*/
            String hostName = getLocalHostName();
            if (hostName.length() > 0)
            {
                /**在给定主机名的情况下，根据系统上配置的名称服务返回其 IP 地址所组成的数组。*/
                InetAddress[] addrs = InetAddress.getAllByName(hostName);
                if (addrs.length > 0)
                {
                    ret = new String[addrs.length];
                    for (int i = 0; i < addrs.length; i++)
                    {
                        /**.getHostAddress()   返回 IP 地址字符串（以文本表现形式）。*/
                        ret[i] = addrs[i].getHostAddress();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ret = null;
        }
        return ret;
    }
    
    public static void main(String[] args)
    {
        System.out.println("本机IP：" + getLocalHostIP());
        System.out.println("本地主机名字为：" + getLocalHostName());
        System.out.println("本地MAC地址为：" + getLocalMACAddress());
        System.out.println("指定域名的IP：" + getHostIPByHostName("www.baidu.com"));
        System.out.println("指定域名的IP：" + getHostIPByHostName("www.qq.com"));
        System.out.println("指定域名的IP：" + getHostIPByHostName("www.sina.com"));
        
        String[] localIP = getAllLocalHostIP();
        for (int i = 0; i < localIP.length; i++)
        {
            System.out.println(localIP[i]);
        }
        
        InetAddress baidu;
        try
        {
            baidu = InetAddress.getByName("www.baidu.com");
            System.out.println("baidu : " + baidu);
            System.out.println("baidu IP: " + baidu.getHostAddress());
            System.out.println("baidu HostName: " + baidu.getHostName());
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }
    
}