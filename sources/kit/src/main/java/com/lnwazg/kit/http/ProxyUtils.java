package com.lnwazg.kit.http;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.lnwazg.kit.io.StreamUtils;
import com.lnwazg.kit.log.Logs;

/**
 * Http代理工具
 * @author  lnwazg
 * @version  [版本号, 2012-10-30]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ProxyUtils
{
    /**
     * 读数据超时时间，毫秒数
     */
    private static final int READ_TIMEOUT_MILLS = 1500;
    
    /**
     * 连接超时时间，毫秒数
     */
    private static final int CONNECTION_TIMEOUT_MILLS = 1500;
    
    /**
     * 测试连接用的url地址
     */
    private static final String DEFAULT_PING_URL = "http://cn.epochtimes.com/assets/themes/djy/images/DJY-Web-Logo.png";
    
    /**
     * 自定义的测试连接用的url地址
     */
    public static String CUSTOM_PING_URL = null;
    
    /**
     * 检测某个代理访问某个url是否能够成功
     * @author nan.li
     * @param host
     * @param port
     * @param testUrl
     * @return
     */
    public static ProxyState testProxyDetail(String host, int port, String testUrl)
    {
        InputStream in = null;
        try
        {
            Logs.i(String.format("准备使用如下地址测试代理的连通性:%s", testUrl));
            URL url = new URL(testUrl);
            long startMills = System.currentTimeMillis();
            // 创建代理服务器            
            trustAllHttpsCerts();
            InetSocketAddress addr = new InetSocketAddress(host, port);
            // http代理            
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
            // 如果我们知道代理server的名字, 可以直接使用          
            URLConnection conn = url.openConnection(proxy);
            conn.setConnectTimeout(CONNECTION_TIMEOUT_MILLS);
            conn.setReadTimeout(READ_TIMEOUT_MILLS);
            in = conn.getInputStream();
            byte[] bytes = IOUtils.toByteArray(in);
            long endMills = System.currentTimeMillis();
            if (bytes != null && bytes.length > 0)
            {
                return new ProxyState(true, (endMills - startMills));
            }
            else
            {
                return new ProxyState(false, -1);
            }
        }
        catch (Exception e)
        {
            System.err.println("出现异常" + e.getMessage());
            return new ProxyState(false, -1);
        }
        finally
        {
            StreamUtils.close(in);
        }
    }
    
    /**
     * 信任所有的https证书
     * @author nan.li
     */
    private static void trustAllHttpsCerts()
    {
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }
            
            public void checkClientTrusted(X509Certificate[] certs, String authType)
            {
            }
            
            public void checkServerTrusted(X509Certificate[] certs, String authType)
            {
            }
        }};
        // Install the all-trusting trust manager
        try
        {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static ProxyState checkLocalSSEazyDetail()
    {
        return testProxyDetail("127.0.0.1", 1080, getTestURL());
    }
    
    /**
     * 获取测试连通情况的url地址
     * @author nan.li
     * @return
     */
    private static String getTestURL()
    {
        //若自定义的测试地址不为空，那么就用自定义地址
        if (StringUtils.isNotEmpty(CUSTOM_PING_URL))
        {
            return CUSTOM_PING_URL;
        }
        return DEFAULT_PING_URL;
    }
    
    public static void main(String[] args)
    {
        System.out.println(checkLocalSSEazyDetail());
    }
    
    private static final String PROXY_HOST = "127.0.0.1";
    
    private static final String PROXY_PORT = "1080";
    
    /** 
     * 设置系统代理
     * @see [类、类#方法、类#成员]
     */
    public static void setJavaSystemLevelProxy()
    {
        System.getProperties().setProperty("proxySet", "true");
        // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以  
        System.getProperties().setProperty("http.proxyHost", PROXY_HOST);
        System.getProperties().setProperty("http.proxyPort", PROXY_PORT);
    }
    
    /** 
     * 取消系统代理
     * @see [类、类#方法、类#成员]
     */
    public static void cancelJavaSystemLevelProxy()
    {
        System.getProperties().setProperty("proxySet", "false");
    }
}
