package com.lnwazg.kit.property;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.lnwazg.kit.describe.DescribeUtils;
import com.lnwazg.kit.io.StreamUtils;
import com.lnwazg.kit.log.Logs;

/**
 * 完美版的属性读写工具
 * 
 * 让我的那个强大的property工具支持注释的扩展功能！
        支持注释的支持前缀，例如： #、//开头的
        对于以下开头的代码，全部归类为注释
        put的时候，普通的直接put（k,v）即可
        但是注释的，要put("#n",value)
        这样，是为了：1.n是自动生成的序号，可确保注释的key不重复，并且能有效地加入到linkedhashmap中
                2.#n这样的key往往能区别于非注释的key，保证不会对非注释的key产生影响
                      理论上只要n不重复即可，可采用uuid来生成。但实际上可采用i++的形式来生成，这样会更加简洁！
 * @author nan.li
 * @version 2015-10-13
 */
public class PropertyUtils
{
    private static final String DEFAULT_ENCODING = CharEncoding.UTF_8;
    
    /**
     * 注释的前缀
     */
    private static final String[] ANNO_PREFIXS = {"#", "//"};
    
    /**
     * 从网络加载配置信息
     * @author nan.li
     * @param url
     * @return
     */
    public static Map<String, String> load(URL url)
    {
        Map<String, String> result = null;
        if (url != null)
        {
            try
            {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                result = load(url.openConnection().getInputStream());
                Logs.i("Resolve remote url properties cost " + stopWatch.getTime() + " ms");
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 从指定的文件位置加载配置文件，不推荐使用，因为一旦项目达成一个可执行jar包，便不支持这种加载方式
     * @author nan.li
     * @param filePath
     * @param encoding
     * @return
     */
    @Deprecated
    public static Map<String, String> load(String filePath, String encoding)
    {
        return load(new File(filePath), encoding);
    }
    
    /**
     * 从指定的文件位置加载配置文件，不推荐使用，因为一旦项目达成一个可执行jar包，便不支持这种加载方式
     * @author nan.li
     * @param filePath
     * @param encoding
     * @return
     */
    @Deprecated
    public static Map<String, String> load(String filePath)
    {
        return load(filePath, DEFAULT_ENCODING);
    }
    
    /**
     * 从指定的文件位置加载配置文件，不推荐使用，因为一旦项目达成一个可执行jar包，便不支持这种加载方式
     * @author nan.li
     * @param filePath
     * @param encoding
     * @return
     */
    @Deprecated
    public static Map<String, String> load(File propertyFile)
    {
        return load(propertyFile, DEFAULT_ENCODING);
    }
    
    /**
     * 从指定的文件位置加载配置文件，不推荐使用，因为一旦项目达成一个可执行jar包，便不支持这种加载方式
     * @author nan.li
     * @param filePath
     * @param encoding
     * @return
     */
    @Deprecated
    public static Map<String, String> load(File propertyFile, String encoding)
    {
        Map<String, String> resMap = new LinkedHashMap<String, String>();//让读取出来的map保持插入顺序显示
        if (!propertyFile.exists())
        {
            Logs.e("加载属性文件失败！文件名:" + propertyFile.getName());
            return resMap;
        }
        try
        {
            List<String> list = FileUtils.readLines(propertyFile, encoding);
            int annoNo = 0;
            for (String line : list)
            {
                String lineTrim = StringUtils.trim(line);
                if (StringUtils.isBlank(lineTrim))
                {
                    continue;
                }
                if (isAnno(lineTrim))
                {
                    resMap.put("#" + annoNo++, lineTrim);
                }
                else
                {
                    //从第一个等于号开始作为分隔符
                    int equalIndex = StringUtils.indexOf(lineTrim, "=");
                    if (equalIndex != -1)
                    {
                        String key = StringUtils.substring(lineTrim, 0, equalIndex);
                        String value = StringUtils.substring(lineTrim, equalIndex + "=".length());
                        resMap.put(key, value);
                    }
                    //其他情况，则全部都是非法配置，直接忽略即可
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return resMap;
    }
    
    /**
     * 加载指定数据流处的配置文件
     * @author nan.li
     * @param inputStream
     * @return
     */
    public static Map<String, String> load(InputStream inputStream)
    {
        return load(inputStream, DEFAULT_ENCODING);
    }
    
    public static Map<String, String> load(InputStream inputStream, String encoding)
    {
        Map<String, String> resMap = new LinkedHashMap<String, String>();//让读取出来的map保持插入顺序显示
        if (inputStream == null)
        {
            Logs.e("inputStream为空！无法加载配置数据信息");
            return resMap;
        }
        try
        {
            List<String> list = IOUtils.readLines(inputStream, encoding);
            int annoNo = 0;
            for (String line : list)
            {
                String lineTrim = StringUtils.trim(line);
                if (StringUtils.isBlank(lineTrim))
                {
                    continue;
                }
                if (isAnno(lineTrim))
                {
                    resMap.put("#" + annoNo++, lineTrim);
                }
                else
                {
                    //从第一个等于号开始作为分隔符
                    int equalIndex = StringUtils.indexOf(lineTrim, "=");
                    if (equalIndex != -1)
                    {
                        String key = StringUtils.substring(lineTrim, 0, equalIndex);
                        String value = StringUtils.substring(lineTrim, equalIndex + "=".length());
                        resMap.put(key, value);
                    }
                    //其他情况，则全部都是非法配置，直接忽略即可
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            StreamUtils.close(inputStream);
        }
        return resMap;
    }
    
    /**
     * 是否是注释行
     * @author Administrator
     * @param lineTrim
     * @return
     */
    private static boolean isAnno(String lineTrim)
    {
        for (String prefix : ANNO_PREFIXS)
        {
            if (StringUtils.startsWith(lineTrim, prefix))
            {
                return true;
            }
        }
        return false;
    }
    
    public static void set(String filePath, String encoding, String key, String value)
    {
        set(new File(filePath), encoding, key, value);
    }
    
    public static void set(File propertyFile, String key, String value)
    {
        set(propertyFile, DEFAULT_ENCODING, key, value);
    }
    
    public static void set(File propertyFile, String encoding, String key, String value)
    {
        if (!propertyFile.exists())
        {
            Logs.e("加载属性文件失败！文件名:" + propertyFile.getName());
            return;
        }
        Map<String, String> map = load(propertyFile, encoding);
        //写入的过程，还附带了去除废值的功能！真是超棒的工具类！
        map.put(key, value);//替换新值
        StringBuilder sb = new StringBuilder();
        for (String mKey : map.keySet())
        {
            //判断是否是注释行
            if (StringUtils.startsWith(mKey, "#"))
            {
                sb.append(map.get(mKey)).append("\r\n");//直接取出注释的内容，重新填充进去
            }
            else
            {
                sb.append(mKey).append("=").append(map.get(mKey)).append("\r\n");
            }
        }
        //删除最后两位多余的\r\n
        if (sb.length() >= 2)
        {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }
        try
        {
            FileUtils.writeStringToFile(propertyFile, sb.toString(), encoding);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 将键值对写入到属性文件中去
     * @author Administrator
     * @param propFile
     * @param propMap
     * @param override true覆盖  false为追加
     */
    public static void set(File propertyFile, String encoding, Map<String, String> propMap, boolean override)
    {
        Map<String, String> toBeWriteMap = propMap;
        if (!override)
        {
            //如果是追加的方式的话
            Map<String, String> propMapOriginal = load(propertyFile);
            for (String key : propMap.keySet())
            {
                if (!propMapOriginal.containsKey(key))
                {
                    propMapOriginal.put(key, propMap.get(key));
                }
            }
            toBeWriteMap = propMapOriginal;
        }
        StringBuilder sb = new StringBuilder();
        for (String mKey : toBeWriteMap.keySet())
        {
            sb.append(mKey).append("=").append(ObjectUtils.toString(toBeWriteMap.get(mKey))).append("\r\n");
        }
        if (sb.length() >= 2)
        {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }
        try
        {
            FileUtils.writeStringToFile(propertyFile, sb.toString(), encoding);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void set(String filePath, String key, String value)
    {
        set(filePath, DEFAULT_ENCODING, key, value);
    }
    
    public static String get(String filePath, String key)
    {
        return get(filePath, DEFAULT_ENCODING, key);
    }
    
    public static String get(String filePath, String encoding, String key)
    {
        return get(new File(filePath), encoding, key);
    }
    
    public static String get(File propertyFile, String key)
    {
        return get(propertyFile, DEFAULT_ENCODING, key);
    }
    
    public static String get(File propertyFile, String encoding, String key)
    {
        return load(propertyFile, encoding).get(key);
    }
    
    public static void main(String[] args)
    {
        DescribeUtils.describeMap(load("D:\\2012\\trunk\\AutoShadow\\res\\config.properties"));
        set("D:\\2012\\trunk\\AutoShadow\\res\\config.properties", "aaa", "123");
        set("D:\\2012\\trunk\\AutoShadow\\res\\config.properties", "aaa", "234");
    }
    
}
