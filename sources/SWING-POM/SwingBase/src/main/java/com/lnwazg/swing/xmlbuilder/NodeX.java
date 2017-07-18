package com.lnwazg.swing.xmlbuilder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.eva.epc.widget.HardLayoutPane;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.swing.xmlbuilder.builder.AttrBuilder;
import com.lnwazg.swing.xmlbuilder.builder.ComponentBuilder;
import com.lnwazg.swing.xmlbuilder.builder.GlobalAttrBuilder;
import com.lnwazg.swing.xmlbuilder.builder.ValBuilder;
import com.lnwazg.swing.xmlbuilder.map.SmartHashMap;
import com.lnwazg.swing.xmlbuilder.namedcomp.NextLine;
import com.lnwazg.swing.xmlbuilder.namedcomp.TitledLineSeparator;
import com.lnwazg.swing.xmlbuilder.util.NodexConstants;

/**
 * 某一个Swing XML节点
 * @author Administrator
 * @version 2016年1月24日
 */
public class NodeX
{
    /**
     * 该节点的名称<br>
     * 例如：Container、HardLayoutPane、JButton等等
     */
    private String name;
    
    /**
     * 该节点的值<br>
     * 仅仅在叶子节点使用到该属性，其余的情况一般不会读取该属性。<br>
     * 例如有一个叶子节点为： <TitledLineSeparator>美女图</TitledLineSeparator>,那么读取到的value为：美女图
     */
    private String value;
    
    /**
     * 该XML节点的类型<br>
     * 标识该节点是一个文本节点还是一个结构节点等等属性<br>
     * 该属性是仅对xml解析器有帮助，在实际解析组件的时候没有用处
     */
    private short type;
    
    /**
     * 该属性表上面挂载的属性表<br>
     * 例如layout="BoxLayout|Y_AXIS" iconImage="icons/ss.ico"<br>
     * 那么属性表中key:layout对应的值为BoxLayout|Y_AXIS
     */
    private SmartHashMap<String, String> attrsMap;
    
    /**
     * 父节点。<br>和xml结构一致。用于辅助组件定位
     */
    private NodeX parent;
    
    /**
     * 子节点列表。<br>和xml结构一致。用于辅助组件定位
     */
    private List<NodeX> children;
    
    public SmartHashMap<String, String> getAttrsMap()
    {
        return attrsMap;
    }
    
    public void setAttrsMap(SmartHashMap<String, String> attrsMap)
    {
        this.attrsMap = attrsMap;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public NodeX getParent()
    {
        return parent;
    }
    
    public void setParent(NodeX parent)
    {
        this.parent = parent;
    }
    
    public List<NodeX> getChildren()
    {
        return children;
    }
    
    public void setChildren(List<NodeX> children)
    {
        this.children = children;
    }
    
    public short getType()
    {
        return type;
    }
    
    public void setType(short type)
    {
        this.type = type;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
    //==========================================================以下是组件的实用方法=================================
    //以下代码其实是一个CompParser 组件解析器
    
    /**
     * 实例化新组件，或者获取已经存在的组件
     * 同事需要注册组件的ID信息到Map里面
     * @author Administrator
     * @param targetFrame
     * @return
     */
    public Object createComponent(JFrame frame)
    {
        Object ret = ComponentBuilder.build(this, frame);
        //ID信息取出来，并注册好
        if (attrsMap.containsKey("id"))
        {
            String id = attrsMap.get("id");
            //根据这个id，将组建注册起来
            XS.get(frame).reg(id, (Component)ret);
        }
        return ret;
    }
    
    /**
     * 设置组件的布局信息（假如有必要设置的话）
     * @author Administrator
     * @param comp
     */
    public void setComponentLayoutIfNeed(Object comp)
    {
        //假如能找到该布局属性，那么就需要设置相应的布局属性
        if (attrsMap.containsKey("layout"))
        {
            Container containerComp = (Container)comp;
            //找到了布局属性这样的属性信息
            //那么下一步就是需要为其设置信息了！
            //根据该布局的属性信息，从map中查询并实例化出实际需要的布局对象！
            String layout = attrsMap.get("layout");
            if ("BorderLayout".equals(layout))
            {
                containerComp.setLayout(new BorderLayout());
            }
            else if ("FlowLayout".equals(layout))
            {
                containerComp.setLayout(new FlowLayout());
            }
            else if ("GridBagLayout".equals(layout))
            {
                containerComp.setLayout(new GridBagLayout());
            }
            else if (layout.startsWith("GridLayout"))
            {
                String[] layoutParams = layout.split(NodexConstants.REGEX_SEPERATOR_VERTICAL_LINE);
                String rowCol = layoutParams[1];
                String[] rowColParams = rowCol.split(NodexConstants.REGEX_SEPERATOR_COMMA);
                containerComp.setLayout(new GridLayout(Integer.valueOf(rowColParams[0].trim()), Integer.valueOf(rowColParams[1].trim())));
            }
            else if (layout.startsWith("BoxLayout"))
            {
                String[] layoutParams = layout.split(NodexConstants.REGEX_SEPERATOR_VERTICAL_LINE);
                int axis = -1;
                String axisStr = layoutParams[1];
                if ("X_AXIS".equals(axisStr) || "0".equals(axisStr))
                {
                    axis = BoxLayout.X_AXIS;
                }
                else if ("Y_AXIS".equals(axisStr) || "1".equals(axisStr))
                {
                    axis = BoxLayout.Y_AXIS;
                }
                else if ("LINE_AXIS".equals(axisStr) || "2".equals(axisStr))
                {
                    axis = BoxLayout.LINE_AXIS;
                }
                else if ("PAGE_AXIS".equals(axisStr) || "3".equals(axisStr))
                {
                    axis = BoxLayout.PAGE_AXIS;
                }
                containerComp.setLayout(new BoxLayout(containerComp, axis));
            }
            else
            {
                Logs.w(String.format("无法识别的layout名称：%s, 也许你该扩展组件了？", layout));
            }
        }
    }
    
    /**
     * 设置组件的属性信息
     * @author Administrator
     * @param comp
     * @return
     */
    public void setComponentAttrs(Object cp)
    {
        AttrBuilder.buildAllAvail(this, cp);
        ValBuilder.buildAllAvail(this, cp);
    }
    
    /**
     * 将组建添加到上层容器中去
     * @author Administrator
     * @param comp
     * @param parent2
     */
    public void addComponentToParent(Object comp, Object parent, JFrame frame)
    {
        if (attrsMap.containsKey("position"))
        {
            String position = attrsMap.get("position");
            ((Container)parent).add((Component)comp, position);
        }
        else if (parent instanceof JScrollPane)
        { //属性表里面没有position字段
          //假如是JScrollPane，那么需要这样设置
            JScrollPane sc = (JScrollPane)parent;
            sc.setViewportView((Component)comp);
        }
        else if (parent instanceof JTabbedPane)
        {
            String tabText = attrsMap.get("tabText");
            ((JTabbedPane)parent).addTab(tabText, (Component)comp);
        }
        else if (parent instanceof HardLayoutPane)
        {
            HardLayoutPane hardLayoutPaneParent = (HardLayoutPane)parent;
            if (comp instanceof TitledLineSeparator)
            {
                TitledLineSeparator titledLineSeparatorComp = (TitledLineSeparator)comp;
                hardLayoutPaneParent.addTitledLineSeparator(titledLineSeparatorComp.getTitle());
            }
            else if (comp instanceof NextLine)
            {
                hardLayoutPaneParent.nextLine();
            }
            else
            {
                Component component = (Component)comp;
                if (attrsMap.containsKey("w"))
                {
                    String w = attrsMap.get("w");
                    int gridWidth = Integer.valueOf(w);
                    hardLayoutPaneParent.addTo(component, gridWidth, true);
                }
                else
                {
                    //若未指定w，则采用默认值：1
                    hardLayoutPaneParent.addTo(component, true);
                }
            }
        }
        else
        {
            if (parent instanceof Container)
            {
                //默认的，就直接添加进去即可！
                if (comp instanceof JMenuBar)
                {
                    frame.setJMenuBar((JMenuBar)comp);
                }
                else if (comp instanceof Component)
                {
                    //添加到父节点下面
                    ((Container)parent).add((Component)comp);
                }
                else if (comp instanceof ButtonGroup)
                {
                    Logs.d("检测到ButtonGroup，框架自适应特殊处理...");
                }
            }
            else if (parent instanceof ButtonGroup)
            {
                ((ButtonGroup)parent).add((AbstractButton)comp);//ButtonGroup可以直接添加即可
                Object grandFather = XmlLayoutBuilder.parentNodeMap.get(parent);//获得父亲的父亲，才是真实的应该被添加到的节点
                ((Container)grandFather).add((AbstractButton)comp);//将其添加到上上层节点中！就可以成功了！
            }
        }
    }
    
    /**
     * 检查哪些属性从未被使用过<br>
     * 如果某些属性在创建组件的时候就已经使用过了，并且想关闭掉恼人的【未使用】提示，那么直接在线程外面使用之即可！<br>
     * 具体可参考JavaFxComponentBuilder.WebView    attrsMap.use("userAgent");    注意，为了使其生效，必须立即使用！而不是在线程内延迟使用！
     * @author nan.li
     */
    public void checkUnusedAttrs()
    {
        //最后，清算一下哪些属性从未被使用过，并给出提示
        Set<String> unusedKeys = attrsMap.getUnUsedKeys();
        
        //如果是顶级节点，则需要特殊处理（因为顶级节点的属性可能是全局属性了，所以这些属性并不是从未使用过的，因此不该警报！）
        if (attrsMap == XmlLayoutBuilder.globalAttrsMap)
        {
            //如果是全局属性表，那么要另外处理
            for (String key : GlobalAttrBuilder.supportedAttrNameBuilderMap.keySet())
            {
                //如果全局属性处理器会对该闲置属性进行处理，那么显然该属性便不再是闲置属性了
                if (unusedKeys.contains(key))
                {
                    unusedKeys.remove(key);
                }
            }
        }
        if (unusedKeys != null && unusedKeys.size() > 0)
        {
            Logs.w(String.format("组件 %s【ID: %s】的以下属性:%s 从未被使用过，您是否写错了属性，or 您的框架需要在AttrBuilder里扩展新的属性支持了？", name, attrsMap.get("id"), unusedKeys));
        }
    }
}
