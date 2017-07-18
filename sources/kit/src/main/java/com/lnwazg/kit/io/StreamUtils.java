package com.lnwazg.kit.io;

import java.io.Closeable;
import java.net.HttpURLConnection;

import org.apache.commons.io.IOUtils;

import com.lnwazg.kit.log.Logs;

/**
 * I/O stream的帮助类
 * 
 * @author lKF20528
 * @version C02 2010-7-8
 * @since OpenEye TAPS V100R001C02
 */
public final class StreamUtils
{
    /**
     * 通用的关闭方法
     * @author lnwazg@126.com
     * @param objects
     */
    public static void close(Object... objects)
    {
        if ((null == objects) || (objects.length == 0))
        {
            return;
        }
        for (Object object : objects)
        {
            if (object instanceof Closeable)
            {
                close((Closeable)object);
            }
            else if (object instanceof HttpURLConnection)
            {
                close((HttpURLConnection)object);
            }
            else
            {
                Logs.w(String.format("无法识别的待关闭对象类型:%s， 忽略之！", object));
            }
        }
    }
    
    /** 
     * 关闭流对象
     */
    public static void close(Closeable... streams)
    {
        if ((null == streams) || (streams.length == 0))
        {
            return;
        }
        for (Closeable stream : streams)
        {
            IOUtils.closeQuietly(stream);
        }
    }
    
    /**
     *  关闭http连接对象
     * @author nan.li
     * @param urlConnections
     */
    public static void close(HttpURLConnection... urlConnections)
    {
        if ((null == urlConnections) || (urlConnections.length == 0))
        {
            return;
        }
        for (HttpURLConnection httpURLConnection : urlConnections)
        {
            disconnect(httpURLConnection);
        }
    }
    
    private static void disconnect(HttpURLConnection urlConnection)
    {
        if (null != urlConnection)
        {
            urlConnection.disconnect();
        }
    }
}