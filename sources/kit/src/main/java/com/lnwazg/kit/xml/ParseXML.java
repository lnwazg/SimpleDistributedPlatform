package com.lnwazg.kit.xml;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 解析XML工具类
 */
public final class ParseXML
{
    /**
    * Logger for this class
    */
    private static final Log logger = LogFactory.getLog(ParseXML.class);
    
    /**
     * 解析xml字符串生成对应的Document对象
     * 
     * @param xmlStr
     *            xml字符串
     * @return Document对象
     * @throws Exception
     *             TAPS通用异常
     */
    public static Document parseXml2Docment(String xmlStr)
        throws Exception
    {
        if (StringUtils.isBlank(xmlStr))
        {
            logger.info("The xml to be parsed to document object is blank, will exit parseXml2Docment() now!");
            return null;
        }
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setIgnoringComments(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            // 将获取的字符串解析成一个doc
            return db.parse(new InputSource(new StringReader(xmlStr)));
        }
        catch (Exception e)
        {
            logger.error("Fail to parse the xml[" + xmlStr + "] to document object.", e);
            throw new Exception("Fail to parse the xml[" + xmlStr + "] to document object.", e);
        }
    }
    
    /**
     * 将指定路径上的xml文件转换成Document对象
     * @param filePath
     *            输入文件
     * @return Document对象
     * @throws Exception
     *             TAPS通用异常
     */
    public static Document parseFile2Document(String filePath)
        throws Exception
    {
        Document xmlDoc = null;
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setIgnoringComments(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            xmlDoc = db.parse(new File(filePath));
        }
        catch (Exception e)
        {
            logger.error("Parse xml file['" + filePath + "'] is fail.", e);
            throw new Exception("Parse xml file is fail.", e);
        }
        return xmlDoc;
    }
    
    /**
     * 根据xml标签来获取相应的值(文档中仅存在一个该标签的情况下)
     * 
     * @param document
     *            文本对象
     * @param tagName
     *            指定的标签名称
     * @return 标签值
     */
    public static String getValueByTag(Document document, String tagName)
    {
        if (null == document)
        {
            logger.error("The document object is null.");
        }
        else
        {
            NodeList nodes = document.getElementsByTagName(tagName);
            if (null != nodes && nodes.getLength() == 1)
            {
                return nodes.item(0).getNodeValue();
            }
        }
        return null;
    }
    
    /**
     * 根据xml标签获取值的字符串数组
     * 
     * @param document
     *            Document对象
     * @param tagName
     *            tag
     * @return 对应的值
     */
    public static String[] getValuesByTag(Document document, String tagName)
    {
        if (null == document)
        {
            logger.error("The document object is null.");
        }
        else
        {
            String[] str = new String[0];
            NodeList nodes = document.getElementsByTagName(tagName);
            if (null != nodes)
            {
                int lenth = nodes.getLength();
                str = new String[lenth];
                for (int i = 0; i < lenth; i++)
                {
                    str[i] = nodes.item(i).getNodeValue();
                }
            }
            return str;
        }
        return new String[0];
    }
    
    /**
     * 获取指定标签下指定属性的值(仅当该tagName在xml上下文中只出现一次时可以正确获取)
     * 
     * @param document
     *            文本对象
     * @param tagName
     *            标签名称
     * @param attributeName
     *            属性名称
     * @return 属性值
     */
    public static String getAtributeValueByName(Document document, String tagName, String attributeName)
    {
        if (null == document)
        {
            logger.error("The document object is null.");
        }
        else
        {
            NodeList nodes = document.getElementsByTagName(tagName);
            if (null != nodes && nodes.getLength() == 1)
            {
                Node node = nodes.item(0).getAttributes().getNamedItem(attributeName);
                if (null != node)
                {
                    return node.getNodeValue();
                }
                
            }
        }
        return null;
    }
    
    /**
     * 
     * 获取节点下所有子节点的值
     * 
     * @param document
     *            document对象
     * @param noteName
     *            父结点的名称
     * @return 子结点的所有值
     */
    public static List<Map<String, String>> parseChildNode(Document document, String noteName)
    {
        if (null == document)
        {
            logger.error("The document object is null.");
            return null;
        }
        
        // 获取所有结点
        NodeList nodeList = document.getElementsByTagName(noteName);
        
        if (nodeList != null && nodeList.getLength() > 0)
        {
            List<Map<String, String>> result = new ArrayList<Map<String, String>>();
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Map<String, String> map = new HashMap<String, String>();
                NodeList childList = nodeList.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++)
                {
                    String key = childList.item(j).getNodeName();
                    String value = childList.item(j).getNodeValue();
                    map.put(key, value);
                }
                result.add(map);
            }
            return result;
        }
        return null;
    }
}
