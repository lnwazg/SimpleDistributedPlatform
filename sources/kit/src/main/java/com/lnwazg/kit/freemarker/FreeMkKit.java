package com.lnwazg.kit.freemarker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Map;

import org.apache.commons.lang3.CharEncoding;

import com.lnwazg.kit.map.Maps;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Freemarker的工具类
 * @author lnwazg@126.com
 * @version 2016年12月3日
 */
public class FreeMkKit
{
    /**
     * 根据根路径的类，获取配置文件
     * @author nan.li
     * @param paramClass
     * @param prefix
     * @return
     */
    public static Configuration getConfigurationByClass(Class<?> paramClass, String prefix)
    {
        try
        {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_25);
            configuration.setClassForTemplateLoading(paramClass, prefix);
            //等价于下面这种方法
            //            configuration.setTemplateLoader( new ClassTemplateLoader(paramClass,prefix));
            configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_25));
            configuration.setDefaultEncoding(CharEncoding.UTF_8);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25).build());
            return configuration;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Configuration getConfigurationByClassLoader(ClassLoader classLoader, String prefix)
    {
        try
        {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_25);
            configuration.setClassLoaderForTemplateLoading(classLoader, prefix);
            //等价于下面这种方法
            //            configuration.setTemplateLoader( new ClassTemplateLoader(paramClass,prefix));
            configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_25));
            configuration.setDefaultEncoding(CharEncoding.UTF_8);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25).build());
            return configuration;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据模板的路径，获取一个配置文件
     * @author lnwazg@126.com
     * @param templateLoadingPath
     * @return
     */
    public static Configuration getConfigurationByDirectory(String templateLoadingPath)
    {
        File f = new File(templateLoadingPath);
        if (!f.exists())
        {
            return null;
        }
        return getConfigurationByDirectory(f);
    }
    
    /**
     * 根据模板的路径，获取一个配置文件
     * @author lnwazg@126.com
     * @param templateLoadingPath
     * @return
     */
    public static Configuration getConfigurationByDirectory(File templateLoadingPath)
    {
        try
        {
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_25);
            
            //以下这两种设置方式是等价的
            //            configuration.setDirectoryForTemplateLoading(templateLoadingPath);
            configuration.setTemplateLoader(new FileTemplateLoader(templateLoadingPath));
            
            configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_25));
            configuration.setDefaultEncoding(CharEncoding.UTF_8);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25).build());
            return configuration;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取一个简单的纯文本的模板
     * @author lnwazg@126.com
     * @param string
     * @return
     */
    public static Template getSimpleStrTemplate(String str)
    {
        try
        {
            return new Template(null, new StringReader(str), null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 将模板翻译成结果
     * @author lnwazg@126.com
     * @param templateStr
     * @param p
     * @return
     * @throws TemplateException 
     */
    public static String format(String templateStr, Object... p)
    {
        return format(templateStr, Maps.asMap(p));
    }
    
    /**
     * 将模板翻译成结果
     * @author lnwazg@126.com
     * @param templateStr
     * @param paramMap
     * @return
     * @throws TemplateException 
     */
    public static String format(String templateStr, Map<String, Object> paramMap)
    {
        Template template = FreeMkKit.getSimpleStrTemplate(templateStr);
        return format(template, paramMap);
    }
    
    /**
     * 将模板翻译成结果
     * @author lnwazg@126.com
     * @param template
     * @param paramMap
     * @return
     * @throws TemplateException 
     */
    public static String format(Template template, Object... p)
    {
        return format(template, Maps.asMap(p));
    }
    
    /**
     * 将模板翻译成结果
     * @author lnwazg@126.com
     * @param template
     * @param paramMap
     * @return
     * @throws TemplateException 
     * @throws Exception 
     */
    public static String format(Template template, Map<String, Object> paramMap)
    {
        byte[] bs = null;
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            template.process(paramMap, new OutputStreamWriter(byteArrayOutputStream, CharEncoding.UTF_8));
            bs = byteArrayOutputStream.toByteArray();
            if (bs != null && bs.length > 0)
            {
                return new String(bs, CharEncoding.UTF_8);
            }
        }
        catch (TemplateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
