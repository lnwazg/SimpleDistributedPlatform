package com.lnwazg.kit.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import com.lnwazg.kit.io.StreamUtils;

/**
 * 进阶版的工具类<br>
 * 支持https的使用
 * @author nan.li
 * @version 2016年9月20日
 */
public class HttpUtils
{
    private static final String DEFAULT_CHARSET = CharEncoding.UTF_8;
    
    private static final String METHOD_POST = "POST";
    
    private static final String METHOD_GET = "GET";
    
    public static final String XML_CONTENT_TYPE = "text/xml";
    
    public static final int CONNECT_TIME_OUT = 10 * 1000;
    
    public static final int READ_TIME_OUT = 10 * 1000;
    
    //    private static SSLContext ctx = null;
    //    
    //    static HostnameVerifier verifier = null;
    //    
    //    static SSLSocketFactory socketFactory = null;
    //    
    //    private static class DefaultTrustManager implements X509TrustManager
    //    {
    //        public X509Certificate[] getAcceptedIssuers()
    //        {
    //            return null;
    //        }
    //        
    //        public void checkClientTrusted(X509Certificate[] chain, String authType)
    //            throws CertificateException
    //        {
    //        }
    //        
    //        public void checkServerTrusted(X509Certificate[] chain, String authType)
    //            throws CertificateException
    //        {
    //        }
    //    }
    //    
    //    static
    //    {
    //        try
    //        {
    //            ctx = SSLContext.getInstance("TLS");
    //            ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
    //            ctx.getClientSessionContext().setSessionTimeout(15);
    //            ctx.getClientSessionContext().setSessionCacheSize(1000);
    //            socketFactory = ctx.getSocketFactory();
    //        }
    //        catch (Exception e)
    //        {
    //            //ignore
    //        }
    //        verifier = new HostnameVerifier()
    //        {
    //            public boolean verify(String urlHostName, SSLSession session)
    //            {
    //                System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
    //                return true;
    //            }
    //        };
    //    }
    //    
    //    static HostnameVerifier hv = new HostnameVerifier()
    //    {
    //        public boolean verify(String urlHostName, SSLSession session)
    //        {
    //            System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
    //            return true;
    //        }
    //    };
    //    
    //    static void trustAllHttpsCertificates()
    //    {
    //        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
    //        javax.net.ssl.TrustManager tm = new miTM();
    //        trustAllCerts[0] = tm;
    //        javax.net.ssl.SSLContext sc;
    //        try
    //        {
    //            sc = javax.net.ssl.SSLContext.getInstance("SSL");
    //            sc.init(null, trustAllCerts, null);
    //            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    //        }
    //        catch (NoSuchAlgorithmException e)
    //        {
    //            e.printStackTrace();
    //        }
    //        catch (KeyManagementException e)
    //        {
    //            e.printStackTrace();
    //        }
    //    }
    //    
    //    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager
    //    {
    //        public java.security.cert.X509Certificate[] getAcceptedIssuers()
    //        {
    //            return null;
    //        }
    //        
    //        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs)
    //        {
    //            return true;
    //        }
    //        
    //        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs)
    //        {
    //            return true;
    //        }
    //        
    //        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
    //            throws java.security.cert.CertificateException
    //        {
    //            return;
    //        }
    //        
    //        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
    //            throws java.security.cert.CertificateException
    //        {
    //            return;
    //        }
    //    }
    
    /**
     * 执行HTTP POST请求，采用默认的字符集、默认的超时设置
     * @author lnwazg@126.com
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, Map<String, String> params)
    {
        return doPost(url, params, DEFAULT_CHARSET, CONNECT_TIME_OUT, READ_TIME_OUT);
    }
    
    /**
     * 执行HTTP POST请求，采用默认的字符集
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应字符串
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params, int connectTimeout, int readTimeout)
    {
        return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
    }
    
    /**
     * 执行HTTP POST请求。
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout, int readTimeout)
    {
        String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        String query;
        try
        {
            query = buildQueryParamString(params, charset);
            byte[] content = {};
            if (query != null)
            {
                content = query.getBytes(charset);
            }
            return doPost(url, ctype, content, connectTimeout, readTimeout);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 执行HTTP POST请求，发送指定消息体内容，采用默认的编码方式，默认的超时时间
     * @author lnwazg@126.com
     * @param url
     * @param content
     * @return
     */
    public static String doPost(String url, String content)
    {
        try
        {
            return doPost(url, content.getBytes(DEFAULT_CHARSET));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String doPost(String url, String content, int connectTimeout, int readTimeout)
    {
        try
        {
            return doPost(url, content.getBytes(DEFAULT_CHARSET), connectTimeout, readTimeout);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 执行HTTP POST请求，发送指定消息体的字节码，采用默认的编码方式，默认的超时时间
     * @author lnwazg@126.com
     * @param url
     * @param bytes
     * @return
     */
    public static String doPost(String url, byte[] bytes)
    {
        return doPost(url, "application/x-www-form-urlencoded;charset=" + DEFAULT_CHARSET, bytes, CONNECT_TIME_OUT, READ_TIME_OUT);
    }
    
    public static String doPost(String url, byte[] bytes, int connectTimeout, int readTimeout)
    {
        return doPost(url, "application/x-www-form-urlencoded;charset=" + DEFAULT_CHARSET, bytes, connectTimeout, readTimeout);
    }
    
    /**
     * 执行HTTP POST请求。
     * @param url     请求地址
     * @param ctype   请求类型
     * @param content 请求字节数组
     * @return 响应字符串
     * @throws IOException
     */
    public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout)
    {
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try
        {
            conn = getConnection(new URL(url), METHOD_POST, ctype);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            out = conn.getOutputStream();
            out.write(content);
            rsp = getResponseAsString(conn);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            StreamUtils.close(out, conn);
        }
        return rsp;
    }
    
    /**
     * 执行Get请求
     * @author nan.li
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGet(String url)
    {
        return doGet(url, null);
    }
    
    /**
     * 执行HTTP GET请求。
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应字符串
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params)
    {
        return doGet(url, params, DEFAULT_CHARSET);
    }
    
    /**
     * 执行HTTP GET请求。
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static String doGet(String url, Map<String, String> params, String charset)
    {
        HttpURLConnection conn = null;
        String rsp = null;
        try
        {
            String ctype = "application/x-www-form-urlencoded;charset=" + charset;
            String queryParamString = buildQueryParamString(params, charset);
            conn = getConnection(buildGetUrl(url, queryParamString), METHOD_GET, ctype);
            rsp = getResponseAsString(conn);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            StreamUtils.close(conn);
        }
        return rsp;
    }
    
    /**
     * 获取一个http连接对象
     * @author nan.li
     * @param url
     * @param method
     * @param ctype
     * @return
     * @throws IOException
     */
    private static HttpURLConnection getConnection(URL url, String method, String ctype)
        throws IOException
    {
        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol()))
        {
            //            try
            //            {
            //                SslUtils.ignoreSsl();
            //            }
            //            catch (Exception e)
            //            {
            //                e.printStackTrace();
            //            }
            //到了JDK8,直接用自带的，即可！
            //详细情况可参考以下说明文件：https://blogs.oracle.com/java-platform-group/entry/diagnosing_tls_ssl_and_https
            HttpsURLConnection connHttps = (HttpsURLConnection)url.openConnection();
            //            connHttps.setSSLSocketFactory(socketFactory);
            //            connHttps.setHostnameVerifier(verifier);
            conn = connHttps;
        }
        else
        {
            conn = (HttpURLConnection)url.openConnection();
        }
        conn.setRequestMethod(method);
        if (METHOD_POST.equals(method))
        {
            conn.setDoInput(true);
            conn.setDoOutput(true);
        }
        //        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
        if (StringUtils.isNotEmpty(ctype))
        {
            conn.setRequestProperty("Content-Type", ctype);
        }
        return conn;
    }
    
    /**
     * 拼接GET请求的URL
     * @author nan.li
     * @param strUrl
     * @param queryParamString
     * @return
     * @throws IOException
     */
    private static URL buildGetUrl(String strUrl, String queryParamString)
        throws IOException
    {
        URL url = new URL(strUrl);
        if (StringUtils.isBlank(queryParamString))
        {
            return url;
        }
        if (StringUtils.isBlank(url.getQuery()))
        {
            //如果查询参数为空的话
            if (strUrl.endsWith("?"))
            {
                strUrl = String.format("%s%s", strUrl, queryParamString);
            }
            else
            {
                strUrl = String.format("%s?%s", strUrl, queryParamString);
            }
        }
        else
        {
            if (strUrl.endsWith("&"))
            {
                strUrl = String.format("%s%s", strUrl, queryParamString);
            }
            else
            {
                strUrl = String.format("%s&s", strUrl, queryParamString);
            }
        }
        return new URL(strUrl);
    }
    
    /**
     * 根据指定的字符集参数，去拼接查询参数的字符串<br>
     * 各个查询参数之间用&符号连接
     * @author nan.li
     * @param params
     * @param charset
     * @return            aaa=1&bbb=2&ccc=3
     * @throws UnsupportedEncodingException 
     * @throws IOException
     */
    public static String buildQueryParamString(Map<String, String> params, String charset)
        throws UnsupportedEncodingException
    {
        if (params == null || params.isEmpty())
        {
            return null;
        }
        StringBuilder query = new StringBuilder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;
        for (Map.Entry<String, String> entry : entries)
        {
            String name = entry.getKey();
            String value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value))
            {
                if (hasParam)
                {
                    query.append("&");
                }
                else
                {
                    hasParam = true;
                }
                query.append(name).append("=").append(URLEncoder.encode(value, charset));
            }
        }
        return query.toString();
    }
    
    /**
     * 从response中获取返回的字符串
     * @author nan.li
     * @param conn
     * @return
     * @throws IOException
     */
    protected static String getResponseAsString(HttpURLConnection conn)
        throws IOException
    {
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null)
        {
            InputStream inputStream;
            try
            {
                inputStream = conn.getInputStream();
                return getStreamAsString(inputStream, charset);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            //获取错误流
            String msg = getStreamAsString(es, charset);
            if (StringUtils.isBlank(msg))
            {
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
            }
            else
            {
                throw new IOException(msg);
            }
        }
    }
    
    /**
     * 从响应流中获取字符串
     * @author nan.li
     * @param inputStream
     * @param charset
     * @return
     * @throws IOException
     */
    private static String getStreamAsString(InputStream inputStream, String charset)
        throws IOException
    {
        try
        {
            return IOUtils.toString(inputStream, charset);
        }
        finally
        {
            StreamUtils.close(inputStream);
        }
    }
    
    /**
     * 获取响应的字符集
     * @author nan.li
     * @param ctype
     * @return
     */
    private static String getResponseCharset(String ctype)
    {
        String charset = DEFAULT_CHARSET;
        if (!StringUtils.isEmpty(ctype))
        {
            String[] params = ctype.split(";");
            for (String param : params)
            {
                param = param.trim();
                if (param.startsWith("charset"))
                {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2)
                    {
                        if (!StringUtils.isEmpty(pair[1]))
                        {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }
        return charset;
    }
    
    /**
     * 使用默认的UTF-8字符集反编码请求参数值。
     *
     * @param value 参数值
     * @return 反编码后的参数值
     */
    public static String decode(String value)
    {
        return decode(value, DEFAULT_CHARSET);
    }
    
    /**
     * 使用默认的UTF-8字符集编码请求参数值。
     *
     * @param value 参数值
     * @return 编码后的参数值
     */
    public static String encode(String value)
    {
        return encode(value, DEFAULT_CHARSET);
    }
    
    /**
     * 使用指定的字符集反编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 反编码后的参数值
     */
    public static String decode(String value, String charset)
    {
        String result = null;
        if (StringUtils.isNotBlank(value))
        {
            try
            {
                result = URLDecoder.decode(value, charset);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    
    /**
     * 使用指定的字符集编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 编码后的参数值
     */
    public static String encode(String value, String charset)
    {
        String result = null;
        if (StringUtils.isNotBlank(value))
        {
            try
            {
                result = URLEncoder.encode(value, charset);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
    
    public static Map<String, String> getParamsFromUrl(String url)
    {
        Map<String, String> map = null;
        if (url != null && url.indexOf('?') != -1)
        {
            map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
        }
        if (map == null)
        {
            map = new HashMap<String, String>();
        }
        return map;
    }
    
    /**
     * 从URL中提取所有的参数。
     *
     * @param query URL地址
     * @return 参数映射
     */
    public static Map<String, String> splitUrlQuery(String query)
    {
        Map<String, String> result = new HashMap<String, String>();
        String[] pairs = query.split("&");
        if (pairs != null && pairs.length > 0)
        {
            for (String pair : pairs)
            {
                String[] param = pair.split("=", 2);
                if (param != null && param.length == 2)
                {
                    result.put(param[0], param[1]);
                }
            }
        }
        return result;
    }
    
}
