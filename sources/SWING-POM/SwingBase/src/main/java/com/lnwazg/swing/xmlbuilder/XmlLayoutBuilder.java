package com.lnwazg.swing.xmlbuilder;

import java.awt.Component;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.commons.lang.reflect.FieldUtils;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.swing.xmlbuilder.anno.XmlBuild;
import com.lnwazg.swing.xmlbuilder.builder.GlobalAttrBuilder;
import com.lnwazg.swing.xmlbuilder.map.SmartHashMap;

/**
 * xml布局构建工具
 * @author nan.li
 * @version 2015-10-30
 */
public class XmlLayoutBuilder
{
    /**
     * 某个节点的父节点的表
     */
    public static Map<Object, Object> parentNodeMap = new HashMap<Object, Object>();
    
    /**
     * 全局属性表<br>
     * 适用于JFrame的一些全局属性设置
     */
    public static SmartHashMap<String, String> globalAttrsMap = new SmartHashMap<String, String>();
    
    /**
     * 根据xml信息自动构建窗体<br>
     * 这是构建过程的主入口
     * @author nan.li
     * @param xmlBuildInfo
     * @param targetFrame
     */
    public static void startBuild(XmlBuild xmlBuildInfo, JFrame targetFrame)
    {
        //2.a 设置大部分采用了这个注解的框架的默认的关闭行为
        targetFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        String xmlFileName = xmlBuildInfo.value();
        URL url = XmlBuilder.class.getClassLoader().getResource(xmlFileName);
        if (url == null)
        {
            Logs.e(String.format("资源文件【%s】不存在，退出构建...", xmlFileName));
            System.exit(0);
            return;
        }
        
        NodeX rootNodeX = XmlBuilder.build(xmlFileName);//获得清洗后的节点数据信息
        
        //依次构建每层的信息
        buildNode(rootNodeX, targetFrame, null);
        //界面构建完毕！
        
        //构建JFrame的某些全局属性
        //其中，这里的minToTray属性，可以改变窗口的默认的关闭行为
        //2.b 允许通过xml的全局配置，改变默认的关闭行为
        buildGlobalAttrs(targetFrame);
        
        //将有用的信息注入到targetFrame的相应的字段里（按照名称进行注入即可，无须担心重复！因为重复的字段名肯定编译不过去！）
        //注入的过程中需要缕出未能正常匹配到的ID的列表
        injectToFields(targetFrame);
        
        //全部构建完成之后，需要完成接下来的初始化工作
        if (targetFrame instanceof XmlJFrame)
        {
            XmlJFrame xmlJFrame = (XmlJFrame)targetFrame;
            xmlJFrame.$ = XS.get(targetFrame);//注入注册表对象的实例
            //              CompRegistry $ = XS.get(MainFrame.class);
            //2.c 当UI构建完毕后，依然还有机会改变关闭行为
            xmlJFrame.afterUIBind();
        }
        else
        {
            Logs.w("警告：采用XmlBuild构建方式的窗体未实现XmlBuildSupport接口，可能会导致监听器无法被正常初始化！");
        }
    }
    
    /**
     * 注入并双向绑定好组件
     * @author Administrator
     * @param targetFrame
     */
    private static void injectToFields(JFrame frame)
    {
        //        Map<String, Component> m = CompRegistry.getRegistryMap();
        Map<String, Component> m = XS.get(frame).getRegistryMap();
        Logs.i(String.format("注册表中的ID列表为: %s", describeKeysSorted(m)));
        StringBuilder code = new StringBuilder("[\r\n\r\n");
        boolean showCode = false;
        for (Map.Entry<String, Component> entry : m.entrySet())
        {
            String key = entry.getKey();
            Component value = entry.getValue();
            try
            {
                Field f = frame.getClass().getDeclaredField(key);
                if (f != null)
                {
                    FieldUtils.writeField(f, frame, value, true);
                }
            }
            catch (NoSuchFieldException e)
            {
                Logs.w(String.format("JFrame中未声明字段：%s ，忽略注入！", key));
                //                e.printStackTrace();
                code.append(String.format("\tprivate %s %s;\r\n", value.getClass().getSimpleName(), key));
                showCode = true;
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        code.append("\r\n]");
        if (showCode)
        {
            Logs.i(String.format("存在未声明过的组件Id，建议的组件声明代码为(您可以直接将其复制到代码中)：%s", code.toString()));
        }
    }
    
    private static String describeKeysSorted(Map<String, Component> m)
    {
        List<String> keys = new ArrayList<String>();
        for (String key : m.keySet())
        {
            keys.add(key);
        }
        Collections.sort(keys, new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                return o1.compareTo(o2);
            }
        });
        StringBuilder sb = new StringBuilder("\r\n[\r\n\t");
        for (int i = 0; i < keys.size(); i++)
        {
            if (i != 0 && i % 10 == 0)
            {
                sb.append("\r\n\t");
            }
            sb.append(keys.get(i)).append(", ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\r\n]");
        return sb.toString();
    }
    
    /**
     * 核心的构建步骤，分为以下三步：
     * 1.获取or构建  当前节点的组件
     * 2.设置节点属性
     * 3.获取上层节点的容器信息，并根据容器的特征，将本节点组件添加到父容器里面<br>
     * 该构建过程是递归进行的，从根部一直递归到每一块叶子节点
     * @author nan.li
     * @param rootNodeX
     * @param targetFrame
     */
    private static void buildNode(NodeX nodeX, JFrame targetFrame, Object parent)
    {
        //1.初始化节点组件
        Object comp = nodeX.createComponent(targetFrame);
        parentNodeMap.put(comp, parent);//存储组件与其父亲的对应关系
        
        //获取该节点的布局类型，并为有需要的组件（假如该组件是一个容器的话）设置布局类型
        nodeX.setComponentLayoutIfNeed(comp);
        
        //2.设置其他属性信息
        nodeX.setComponentAttrs(comp);//设置属性信息
        
        //3.获取上层节点的容器信息，并根据容器的特征，将本节点组件添加到父容器里面
        if (parent != null)
        {
            //将其添加到上层容器中
            nodeX.addComponentToParent(comp, parent, targetFrame);
        }
        
        //检测该节点有哪些从未被使用过的属性
        nodeX.checkUnusedAttrs();
        
        //4.假如其还有子节点，那么每一个子节点也需要进行这样的设置
        List<NodeX> children = nodeX.getChildren();
        if (children != null && children.size() > 0)
        {
            for (NodeX child : children)
            {
                //假如有子节点，那么富节点必然是一个容器
                buildNode(child, targetFrame, (Object)comp);
            }
        }
    }
    
    /**
     * 构建全局属性
     * @author nan.li
     */
    private static void buildGlobalAttrs(JFrame frame)
    {
        GlobalAttrBuilder.build(frame);
    }
}
