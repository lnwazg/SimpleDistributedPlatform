package com.lnwazg.ws;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.handler.AbstractHandler;
import org.jdom.Element;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lnwazg.kit.file.FileKit;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.swing.util.Utils;
import com.lnwazg.ws.sim.LocalSimulator;

public class WsRequest
{
    private static final String SERVICE_URL = "http://lnwazg.no-ip.org/services/XFireServlet/IRelease?wsdl";
    
    private static final String TOKEN_STR = "whosyourdaddy";
    
    private static final String QUERY_METHOD = "service";
    
    /**
     * 是否采用本地化模拟访问模式（不连服务器）
     */
    private static boolean LOCAL_SIMULATOR_MODE = false;
    
    Gson gson = new Gson();
    
    static
    {
        configKeystoreOnce();
    }
    
    /**
     * 切换到测试环境
     * 该方法供客户端测试时候使用
     * @author nan.li
     */
    public static void switchToTestEnv()
    {
        System.setProperty("SERVICE_URL", "http://localhost:8080/ROOT/services/XFireServlet/IRelease?wsdl");
    }
    
    /**
     * 切换到本地配置的环境
     * @author nan.li
     */
    public static void switchToLocalConfigEnv()
    {
        LOCAL_SIMULATOR_MODE = true;
    }
    
    /**
     * 清空本地配置目录
     * @author nan.li
     * @param dir
     */
    public static void clearLocalConfigDir(String dir)
    {
        try
        {
            FileUtils.deleteDirectory(new File(dir));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private JsonObject jsonObject;
    
    public WsRequest(String serviceCode)
    {
        jsonObject = new JsonObject();
        jsonObject.addProperty("SERVICE_CODE", serviceCode);
    }
    
    /**
     * 添加所需的参数
     * @author nan.li
     * @param key
     * @param value
     * @return
     */
    public WsRequest addParam(String key, String value)
    {
        jsonObject.addProperty(key, value);
        return this;
    }
    
    public WsRequest addParam(String key, Number value)
    {
        jsonObject.addProperty(key, value);
        return this;
    }
    
    public WsRequest addParam(String key, Boolean value)
    {
        jsonObject.addProperty(key, value);
        return this;
    }
    
    public WsRequest addParam(String key, Character value)
    {
        jsonObject.addProperty(key, value);
        return this;
    }
    
    /**
     * 添加所需的参数
     * @author nan.li
     * @param key
     * @param value
     * @return
     */
    public WsRequest addParam(String key, JsonElement value)
    {
        jsonObject.add(key, value);
        return this;
    }
    
    public WsRequest addParam(String key, Object obj)
    {
        return addParam(key, gson.toJsonTree(obj));
    }
    
    public WsResponse send()
    {
        WsResponse wsResponse = new WsResponse();
        if (LOCAL_SIMULATOR_MODE)
        {
            String resultStr = LocalSimulator.invokeLocal(jsonObject);
            if (StringUtils.isNotEmpty(resultStr))
            {
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(resultStr).getAsJsonObject();
                wsResponse.setContent(jsonObject);
            }
        }
        else
        {
            Client client = null;
            try
            {
                URL url = configServiceUrl();
                client = new Client(url);
                addEncryptHead(client);
                Object[] objArray = new Object[1];
                objArray[0] = jsonObject.toString();
                Logs.d(String.format("Sending request: %s", jsonObject.toString()));
                Object[] results = client.invoke(QUERY_METHOD, objArray);
                if (results != null && results.length == 1)
                {
                    String resultStr = (String)results[0];
                    if (StringUtils.isNotEmpty(resultStr))
                    {
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(resultStr).getAsJsonObject();
                        wsResponse.setContent(jsonObject);
                    }
                }
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (client != null)
                {
                    client.close();
                    client = null;
                }
            }
        }
        return wsResponse;
    }
    
    /**
     * keystore恢复
     * 只需要执行一次即可！
     * @author nan.li
     */
    private static void configKeystoreOnce()
    {
        //        String basePath = "C:\\Windows\\LNWAZG\\WsClient\\";
        String basePath = FileKit.getConfigBasePathForAll() + "WsClient";
        Utils.createDir(basePath);
        String fileName = "server.keystore";
        File keyStoreFile = new File(basePath, fileName);
        if (!keyStoreFile.exists())
        {
            InputStream inputStream = WsRequest.class.getResourceAsStream(fileName);
            try
            {
                IOUtils.copy(inputStream, new FileOutputStream(keyStoreFile));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        System.setProperty("javax.net.ssl.trustStore", keyStoreFile.getPath());
    }
    
    /**
     *根据实际情况，决定是否采用默认的ws地址
     *优先取用户自定义的服务地址。如果取不到，才使用默认的服务地址
     * @author nan.li
     * @return
     * @throws MalformedURLException
     */
    private URL configServiceUrl()
        throws MalformedURLException
    {
        URL url = null;
        if (StringUtils.isNotEmpty(System.getProperty("SERVICE_URL")))
        {
            url = new URL(System.getProperty("SERVICE_URL"));
        }
        else
        {
            url = new URL(SERVICE_URL);
        }
        return url;
    }
    
    /**
     * 设置ws地址
     * @author nan.li
     * @param url
     */
    public static void setServiceUrl(String url)
    {
        System.setProperty("SERVICE_URL", url);
    }
    
    /** 
     * 对webservice请求头添加加密信息数据
     * @param client
     * @see [类、类#方法、类#成员]
     */
    private static void addEncryptHead(Client client)
    {
        client.addOutHandler(new AbstractHandler()
        {
            public void invoke(MessageContext context)
                throws Exception
            {
                Element el = new Element("header");
                context.getOutMessage().setHeader(el);
                Element auth = new Element("authenticationToken");
                el.addContent(auth);
                Element token = new Element("token");
                token.addContent(TOKEN_STR);
                auth.addContent(token);
            }
        });
    }
    
}
