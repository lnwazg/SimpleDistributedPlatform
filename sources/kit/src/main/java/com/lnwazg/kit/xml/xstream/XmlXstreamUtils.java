package com.lnwazg.kit.xml.xstream;

import java.io.File;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;

/**
 * 一个方便的工具类，在java Object与XML文本之间来回转换的工具
 * @author nan.li
 * @version 2016年7月5日
 */
public class XmlXstreamUtils
{
    public static String getXml(Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        XStream xs = new XStream();
        xs.autodetectAnnotations(true);
        return xs.toXML(obj);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T fromXml(String xml, Class<T> clazz)
    {
        if (xml == null)
        {
            return null;
        }
        XStream xs = new XStream();
        xs.processAnnotations(clazz);
        return (T)xs.fromXML(xml);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T fromXml(File xmlFile, Class<T> clazz)
    {
        if (xmlFile == null)
        {
            return null;
        }
        XStream xs = new XStream();
        xs.processAnnotations(clazz);
        return (T)xs.fromXML(xmlFile);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T fromXml(InputStream inputStream, Class<T> clazz)
    {
        if (inputStream == null)
        {
            return null;
        }
        XStream xs = new XStream();
        xs.processAnnotations(clazz);
        return (T)xs.fromXML(inputStream);
    }
    
}
