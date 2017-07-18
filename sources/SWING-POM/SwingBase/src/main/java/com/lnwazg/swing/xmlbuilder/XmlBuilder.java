package com.lnwazg.swing.xmlbuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.swing.xmlbuilder.exception.InvalidXmlCfgException;
import com.lnwazg.swing.xmlbuilder.exception.InvalidXmlRefNodeException;
import com.lnwazg.swing.xmlbuilder.map.SmartHashMap;

/**
 * XML构建工具
 * @author nan.li
 * @version 2015-10-30
 */
public class XmlBuilder
{
    /**
      * 递归构建
      * @author nan.li
      * @param root
      * @param retNodeX
      */
    private static void build(Node root, NodeX rootNodeX, NodeX parent)
    {
        rootNodeX.setName(root.getNodeName());
        rootNodeX.setType(root.getNodeType());
        NodeList childList = root.getChildNodes();//子节点列表
        
        SmartHashMap<String, String> attrsMap = new SmartHashMap<String, String>();
        
        //root节点的属性信息
        NamedNodeMap attrs = root.getAttributes();
        //遍历元素的属性
        if (attrs != null && attrs.getLength() > 0)
        {
            int len = attrs.getLength();
            for (int i = 0; i < len; i++)
            {
                Node attr = attrs.item(i);//属性对象
                attrsMap.put(attr.getNodeName(), attr.getNodeValue());
            }
        }
        
        //检测节点是否有xmlRef属性 若有 则加载其节点，并用本节点的属性覆盖之
        if (attrsMap.containsKey("xmlRef"))
        {
            //加载该节点，并将其挂载到本树中
            String xmlRefFile = attrsMap.get("xmlRef");
            Node xmlRefNode = getRootNode(xmlRefFile);
            if (xmlRefNode == null)
            {
                throw new InvalidXmlCfgException(String.format("引用的xml文件：%s 格式非法或者不存在！", xmlRefFile));
            }
            Logs.i(String.format("检测到xmlRef定义文件：%s ，将其追加至节点树中 ...", xmlRefFile));
            //attrsMap需要追加
            attrsMap = overrideAttrs(attrsMap, xmlRefNode);
            
            //childList直接用新的覆盖老的
            if (childList.getLength() != 0)
            {
                throw new InvalidXmlRefNodeException("XMLRef节点定义错误：子节点数量应该为0！");
            }
            childList = xmlRefNode.getChildNodes();
        }
        
        if (childList.getLength() == 1 && childList.item(0).getNodeType() == Node.TEXT_NODE)
        {
            rootNodeX.setValue(childList.item(0).getTextContent());
        }
        
        rootNodeX.setAttrsMap(attrsMap);
        rootNodeX.setParent(parent);
        
        boolean ignoreTextNode = judgeIgnoreTextNode(childList);
        if (childList != null && childList.getLength() > 0)
        {
            List<NodeX> children = new ArrayList<NodeX>();
            int len = childList.getLength();
            for (int i = 0; i < len; i++)
            {
                Node item = childList.item(i);
                //如果解析出现“#text”，则说明xml文档内的节点间存在空白字符
                //加入如下条件判断，即可忽略掉处理“#text”这些节点
                //这一步的判断真的好重要啊！
                if (item.getNodeType() == Node.ELEMENT_NODE)
                {
                    //是一个结构化的节点
                    //那么，就继续往下构建
                    NodeX thisNodeX = new NodeX();
                    build(item, thisNodeX, rootNodeX);
                    //构建完毕，将其添加到children
                    children.add(thisNodeX);
                }
                else if (item.getNodeType() == Node.TEXT_NODE)
                {
                    if (ignoreTextNode)
                    {
                        //无意义的空白字符，直接忽略
                    }
                    else
                    {
                        //到达了根文本节点啦！
                        //                        NodeX thisNodeX = new NodeX();
                        //                        build(item, thisNodeX, rootNodeX);
                        //                        //构建完毕，将其添加到children
                        //                        children.add(thisNodeX);
                        //后来想了一下，文本节点直接构建并没有任何意义，因为在父节点中也可以直接获取到nodeValue这个值。那就是该节点的子文本节点的内容！
                    }
                }
            }
            rootNodeX.setChildren(children);
        }
    }
    
    /**
     * 覆盖追加属性
     * 将attrsMap覆盖追加到xmlRefNode的属性表中
     * @author nan.li
     * @param attrsMap
     * @param xmlRefNode
     */
    private static SmartHashMap<String, String> overrideAttrs(SmartHashMap<String, String> attrsMap, Node xmlRefNode)
    {
        SmartHashMap<String, String> newMap = new SmartHashMap<String, String>();
        //root节点的属性信息
        NamedNodeMap attrs = xmlRefNode.getAttributes();
        //遍历元素的属性
        if (attrs != null && attrs.getLength() > 0)
        {
            int len = attrs.getLength();
            for (int i = 0; i < len; i++)
            {
                Node attr = attrs.item(i);//属性对象
                newMap.put(attr.getNodeName(), attr.getNodeValue());
            }
        }
        //用attrsMap的属性将其覆盖
        newMap.putAll(attrsMap);
        return newMap;
    }
    
    /**
     * 判断这些子节点中是否应该剔除掉文本节点
     * 判定：一旦存在ELEMENT_NODE这种类型的节点，就应该忽略所出现的所有的文本节点。
     * 因为文本节点只应该单独出现
     * 所以，更简单的方式应该是：只要子节点超过了1个，那么就不是纯文本的节点，也就是不是根节点了，那么这个时候应该忽略所有的文本节点
     * 如果子节点只有1个，那么可能是文本节点也有可能不是文本节点。这个时候应该根据实际条件去判断，不应该主动去忽略
     * 如果子节点个数为0，那么什么节点也没有了，那么忽略不忽略也完全没有关系了
     * @author nan.li
     * @param childList
     * @return
     */
    private static boolean judgeIgnoreTextNode(NodeList childList)
    {
        return childList.getLength() > 1;
    }
    
    /**
     * 构建dom树，分析出层数以及每层的组数
     * 循环构建！
     * 但是看来层数与组数也不需要专门计算了，因为可以用递归的方式去自动完成！
     * @author nan.li
     * @param xmlFileName
     * @return
     */
    public static NodeX build(String xmlFileName)
    {
        NodeX retNodeX = new NodeX();
        Node root = getRootNode(xmlFileName);
        if (root == null)
        {
            throw new InvalidXmlCfgException(String.format("Fail to parse xml file: %s", xmlFileName));
        }
        build(root, retNodeX, null);
        return retNodeX;
    }
    
    /**
     * 根据XML文件，去获取其根节点Node对象
     * @author nan.li
     * @param xmlFileName
     * @return
     */
    private static Node getRootNode(String xmlFileName)
    {
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setIgnoringComments(true);
            DocumentBuilder dbd = dbf.newDocumentBuilder();
            Document doc = dbd.parse(XmlBuilder.class.getClassLoader().getResourceAsStream(xmlFileName));
            Node root = doc.getDocumentElement();//根节点
            //存储节点的类型、名称、属性键值对的列表、父信息、子信息等
            return root;
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
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
