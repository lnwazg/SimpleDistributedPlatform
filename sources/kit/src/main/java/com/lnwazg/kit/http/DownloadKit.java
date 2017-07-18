package com.lnwazg.kit.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.mail.internet.MimeUtility;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lnwazg.kit.io.StreamUtils;

/**
 * http访问工具包
 * 
 * @author  Administrator
 * @version  [版本号, 2011-11-25]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class DownloadKit
{
    private static final Log logger = LogFactory.getLog(DownloadKit.class);
    
    public static void main(String[] args)
    {
        System.out.println(DownloadKit.toString("http://www.baidu.com"));
    }
    
    /** 
     * 远程文件下载
     * @param url
     * @param destFile
     * @throws Exception 
     * @see [类、类#方法、类#成员]
     */
    public static void downloadFile(String url, File destFile)
        throws Exception
    {
        if (StringUtils.isEmpty(url))
        {
            logger.warn("url is null or empty!", null);
            throw new Exception("url is null or empty!");
        }
        if (destFile == null)
        {
            logger.warn("destFile is null!", null);
            throw new Exception("destFile is null!");
        }
        InputStream remoteInputStream = null;
        FileOutputStream fOutputStream = null;
        try
        {
            URL remoteFileURL = new URL(url);
            remoteInputStream = remoteFileURL.openStream();
            if (remoteInputStream == null)
            {
                logger.warn("remoteInputStream is null!");
                throw new Exception("remoteInputStream is null!");
            }
            fOutputStream = new FileOutputStream(destFile);
            IOUtils.copy(remoteInputStream, fOutputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            StreamUtils.close(fOutputStream, remoteInputStream);
        }
    }
    
    /** 
     * 获取指定url的html代码
     * @param url
     * @see [类、类#方法、类#成员]
     */
    public static String toString(String url)
    {
        return toString(url, CharEncoding.UTF_8);
    }
    
    /** 
     * 按指定的编码获取指定url的html代码
     * @param urlStr
     * @param encoding
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String toString(String urlStr, String encoding)
    {
        String result = "";
        try
        {
            URL url = new URL(urlStr);
            URLConnection uRLconnection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)uRLconnection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                InputStream urlStream = httpConnection.getInputStream();
                result = IOUtils.toString(urlStream, encoding);
                StreamUtils.close(urlStream);
                httpConnection.disconnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 获取文件下载名称的响应头输出信息<br>
     * 自适应各种不同的浏览器
     * @author lnwazg@126.com
     * @param fileName
     * @param userAgent
     * @return
     */
    public static String getContentDispositionByNameAndUserAgent(String fileName, String userAgent)
    {
        try
        {
            if (StringUtils.isEmpty(fileName))
            {
                return null;
            }
            String newFileName = URLEncoder.encode(fileName, "UTF8");
            // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
            String rtn = "filename=\"" + newFileName + "\"";
            if (StringUtils.isNotEmpty(userAgent))
            {
                userAgent = userAgent.toLowerCase();
                // IE浏览器，只能采用URLEncoder编码
                if (userAgent.indexOf("msie") != -1)
                {
                    rtn = "filename=\"" + newFileName + "\"";
                }
                // Opera浏览器只能采用filename*
                else if (userAgent.indexOf("opera") != -1)
                {
                    rtn = "filename*=UTF-8''" + newFileName;
                }
                // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
                //将chrome的优先级调高，以便能够正常适配chrome浏览器文件名下载
                else if (userAgent.indexOf("applewebkit") != -1)
                {
                    newFileName = MimeUtility.encodeText(fileName, "UTF8", "B");
                    rtn = "filename=\"" + newFileName + "\"";
                }
                // Safari浏览器，只能采用ISO编码的中文输出
                else if (userAgent.indexOf("safari") != -1)
                {
                    rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
                }
                // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
                else if (userAgent.indexOf("mozilla") != -1)
                {
                    rtn = "filename*=UTF-8''" + newFileName;
                }
            }
            return String.format("attachment;%s", rtn);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
}
