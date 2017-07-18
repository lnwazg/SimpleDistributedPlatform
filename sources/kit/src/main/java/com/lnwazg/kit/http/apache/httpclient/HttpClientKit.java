package com.lnwazg.kit.http.apache.httpclient;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

@SuppressWarnings("deprecation")
public class HttpClientKit
{
    @SuppressWarnings("unused")
    private static final int CON_TIMEOUT = 5000;
    
    @SuppressWarnings("unused")
    private static final int SO_TIMEOUT = 5000;
    
    private static String UA_WINDOW7_CHROME = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1";
    
    /**
     * 获取默认的http client
     * @author nan.li
     * @return
     */
    public static DefaultHttpClient getDefaultHttpClient()
    {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        cm.setMaxTotal(500);
        cm.setDefaultMaxPerRoute(200);
        
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.connection.timeout", Integer.valueOf(5000));
        params.setParameter("http.socket.timeout", Integer.valueOf(5000));
        params.setParameter("http.useragent", UA_WINDOW7_CHROME);
        
        DefaultHttpClient client = new DefaultHttpClient(cm, params);
        return client;
    }
}
