package com.lnwazg.swing.xmlbuilder.builder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.swing.color.ColorUtils;
import com.lnwazg.kit.validate.Validates;
import com.lnwazg.swing.util.ui.IOS7SwitchButton;
import com.lnwazg.swing.xmlbuilder.NodeX;
import com.lnwazg.swing.xmlbuilder.builder.executor.AttrExecutor;
import com.lnwazg.swing.xmlbuilder.map.SmartHashMap;
import com.lnwazg.swing.xmlbuilder.util.NodexConstants;

/**
 * 属性构建器
 * @author nan.li
 * @version 2016年1月28日
 */
public class AttrBuilder
{
    //占位，用于强行执行类加载
    public static String a;
    
    /**
     * 目前支持的所有的属性构建器以及相应的回调函数
     */
    private static Map<String, AttrExecutor> supportedAttrNameBuilderMap = new SmartHashMap<String, AttrExecutor>();
    
    //将所有可支持的属性以及相应的构建工具都设置到这里，这样就可以随时动态扩展，一目了然！
    static
    {
        Logs.i("目前SwingXml框架支持的组件属性如下：\n");
        
        supportedAttrNameBuilderMap.put("rows", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JTextArea)cp).setRows(Integer.valueOf(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "设置JTextArea的行数";
            }
        });
        
        supportedAttrNameBuilderMap.put("columns", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                if (cp instanceof JTextArea)
                {
                    ((JTextArea)cp).setColumns(Integer.valueOf(attrValue));
                }
                else if (cp instanceof JTextField)
                {
                    ((JTextField)cp).setColumns(Integer.valueOf(attrValue));
                }
            }
            
            @Override
            public String getDescription()
            {
                return "设置JTextArea或JTextField的列数";
            }
        });
        
        supportedAttrNameBuilderMap.put("visible", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JComponent)cp).setVisible(Boolean.valueOf(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "该组件的可视性";
            }
        });
        
        supportedAttrNameBuilderMap.put("floatable", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JToolBar)cp).setFloatable(Boolean.valueOf(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "该JToolBar是否可以浮动移动";
            }
        });
        
        supportedAttrNameBuilderMap.put("lineWrap", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JTextArea)cp).setLineWrap(Boolean.valueOf(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "JTextArea设置在行过长的时候是否要自动换行";
            }
        });
        supportedAttrNameBuilderMap.put("wrapStyleWord", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JTextArea)cp).setWrapStyleWord(Boolean.valueOf(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "JTextArea设置在单词过长的时候是否要把长单词移到下一行";
            }
        });
        
        supportedAttrNameBuilderMap.put("minimum", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JSlider)cp).setMinimum(Integer.parseInt(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "JSlider的minimum值";
            }
        });
        supportedAttrNameBuilderMap.put("maximum", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JSlider)cp).setMaximum(Integer.parseInt(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "JSlider的maximum值";
            }
        });
        
        supportedAttrNameBuilderMap.put("orientation", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                int orientation = -1;
                if ("HORIZONTAL".equals(attrValue))
                {
                    orientation = JSlider.HORIZONTAL;
                }
                else if ("VERTICAL".equals(attrValue))
                {
                    orientation = JSlider.VERTICAL;
                }
                else
                {
                    orientation = Integer.parseInt(attrValue);
                }
                ((JSlider)cp).setOrientation(orientation);
            }
            
            @Override
            public String getDescription()
            {
                return "JSlider的orientation";
            }
        });
        
        supportedAttrNameBuilderMap.put("bounds", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                String[] bounds = StringUtils.split(attrValue, NodexConstants.REGEX_SEPERATOR_COMMA);
                int x = Integer.valueOf(StringUtils.trim(bounds[0]));
                int y = Integer.valueOf(StringUtils.trim(bounds[1]));
                int width = Integer.valueOf(StringUtils.trim(bounds[2]));
                int height = Integer.valueOf(StringUtils.trim(bounds[3]));
                ((JComponent)cp).setBounds(x, y, width, height);
            }
            
            @Override
            public String getDescription()
            {
                return "该组件的x坐标、y坐标、宽、高等界限信息";
            }
        });
        
        supportedAttrNameBuilderMap.put("location", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                String[] dimens = StringUtils.split(attrValue, NodexConstants.REGEX_SEPERATOR_COMMA);
                int width = Integer.valueOf(StringUtils.trim(dimens[0]));
                int height = Integer.valueOf(StringUtils.trim(dimens[1]));
                ((JComponent)cp).setLocation(width, height);
            }
            
            @Override
            public String getDescription()
            {
                return "该组件的位置";
            }
        });
        
        supportedAttrNameBuilderMap.put("text", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JTextComponent)cp).setText(attrValue);
            }
            
            @Override
            public String getDescription()
            {
                return "该组件的文本";
            }
        });
        supportedAttrNameBuilderMap.put("icon", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                Image image = Toolkit.getDefaultToolkit().createImage(AttrBuilder.class.getClassLoader().getResource(attrValue));
                if (cp instanceof JLabel)
                {
                    ((JLabel)cp).setIcon(new ImageIcon(image));
                }
                else if (cp instanceof AbstractButton)
                {
                    ((AbstractButton)cp).setIcon(new ImageIcon(image));
                }
            }
            
            @Override
            public String getDescription()
            {
                return "该标签或按钮的显示图片";
            }
        });
        supportedAttrNameBuilderMap.put("horizontalAlignment", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                int val = -1;
                if (cp instanceof JTextField)
                {
                    if ("left".equalsIgnoreCase(attrValue))
                    {
                        val = JTextField.LEFT;
                    }
                    else if ("center".equalsIgnoreCase(attrValue))
                    {
                        val = JTextField.CENTER;
                    }
                    else if ("right".equalsIgnoreCase(attrValue))
                    {
                        val = JTextField.RIGHT;
                    }
                    else if ("leading".equalsIgnoreCase(attrValue))
                    {
                        val = JTextField.LEADING;
                    }
                    else if ("trailing".equalsIgnoreCase(attrValue))
                    {
                        val = JTextField.TRAILING;
                    }
                    else
                    {
                        val = Integer.valueOf(attrValue);
                    }
                    ((JTextField)cp).setHorizontalAlignment(val);
                }
                else if (cp instanceof JLabel)
                {
                    if ("left".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.LEFT;
                    }
                    else if ("center".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.CENTER;
                    }
                    else if ("right".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.RIGHT;
                    }
                    else if ("leading".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.LEADING;
                    }
                    else if ("trailing".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.TRAILING;
                    }
                    else
                    {
                        val = Integer.valueOf(attrValue);
                    }
                    ((JLabel)cp).setHorizontalAlignment(val);
                }
                else if (cp instanceof AbstractButton)
                {
                    if ("left".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.LEFT;
                    }
                    else if ("center".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.CENTER;
                    }
                    else if ("right".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.RIGHT;
                    }
                    else if ("leading".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.LEADING;
                    }
                    else if ("trailing".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.TRAILING;
                    }
                    else
                    {
                        val = Integer.valueOf(attrValue);
                    }
                    ((AbstractButton)cp).setHorizontalAlignment(val);
                }
            }
            
            @Override
            public String getDescription()
            {
                return "该JTextField或JLabel或AbstractButton的水平对齐方式";
            }
        });
        
        supportedAttrNameBuilderMap.put("verticalAlignment", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                int val = -1;
                if (cp instanceof JLabel)
                {
                    if ("left".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.LEFT;
                    }
                    else if ("center".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.CENTER;
                    }
                    else if ("right".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.RIGHT;
                    }
                    else if ("leading".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.LEADING;
                    }
                    else if ("trailing".equalsIgnoreCase(attrValue))
                    {
                        val = JLabel.TRAILING;
                    }
                    else
                    {
                        val = Integer.valueOf(attrValue);
                    }
                    ((JLabel)cp).setVerticalAlignment(val);
                }
                else if (cp instanceof AbstractButton)
                {
                    if ("left".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.LEFT;
                    }
                    else if ("center".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.CENTER;
                    }
                    else if ("right".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.RIGHT;
                    }
                    else if ("leading".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.LEADING;
                    }
                    else if ("trailing".equalsIgnoreCase(attrValue))
                    {
                        val = SwingConstants.TRAILING;
                    }
                    else
                    {
                        val = Integer.valueOf(attrValue);
                    }
                    ((AbstractButton)cp).setVerticalAlignment(val);
                }
            }
            
            @Override
            public String getDescription()
            {
                return "该JLabel或AbstractButton的垂直对齐方式";
            }
        });
        supportedAttrNameBuilderMap.put("displayedMnemonic", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                if (cp instanceof JLabel)
                {
                    ((JLabel)cp).setDisplayedMnemonic(attrValue.toCharArray()[0]);
                }
            }
            
            @Override
            public String getDescription()
            {
                return "设置标签的访问键（下划线文字）";
            }
        });
        supportedAttrNameBuilderMap.put("echoChar", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JPasswordField)cp).setEchoChar(attrValue.toCharArray()[0]);
            }
            
            @Override
            public String getDescription()
            {
                return "设置每次字符输入时在JPasswordField中显示的字符";
            }
        });
        
        supportedAttrNameBuilderMap.put("editable", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JTextComponent)cp).setEditable(Boolean.valueOf(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "该文本组件是否可以编辑";
            }
        });
        supportedAttrNameBuilderMap.put("toolTipText", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JComponent)cp).setToolTipText(attrValue);
            }
            
            @Override
            public String getDescription()
            {
                return "该组件的文字提示信息";
            }
        });
        supportedAttrNameBuilderMap.put("autoScrolls", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JComponent)cp).setAutoscrolls(Boolean.valueOf(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "该组件是否设置为自动滚动";
            }
        });
        supportedAttrNameBuilderMap.put("preferredSize", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JComponent)cp).setPreferredSize(getDimension(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "该组件的宽高设置";
            }
        });
        supportedAttrNameBuilderMap.put("size", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JComponent)cp).setSize(getDimension(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "该容器（一般是最外层的，例如JFrame）的宽高设置";
            }
        });
        
        supportedAttrNameBuilderMap.put("horizontalScrollBarPolicy", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                if (Validates.isInteger(attrValue))
                {
                    ((JScrollPane)cp).setHorizontalScrollBarPolicy(Integer.valueOf(attrValue));
                }
                else
                {
                    switch (attrValue)
                    {
                        case "HORIZONTAL_SCROLLBAR_AS_NEEDED":
                            ((JScrollPane)cp).setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                            break;
                        case "HORIZONTAL_SCROLLBAR_NEVER":
                            ((JScrollPane)cp).setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                            break;
                        case "HORIZONTAL_SCROLLBAR_ALWAYS":
                            ((JScrollPane)cp).setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                            break;
                        default:
                            break;
                    }
                }
            }
            
            @Override
            public String getDescription()
            {
                return "该滚动面板的水平滚动条的策略";
            }
        });
        supportedAttrNameBuilderMap.put("verticalScrollBarPolicy", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                if (Validates.isInteger(attrValue))
                {
                    ((JScrollPane)cp).setVerticalScrollBarPolicy(Integer.valueOf(attrValue));
                }
                else
                {
                    switch (attrValue)
                    {
                        case "VERTICAL_SCROLLBAR_AS_NEEDED":
                            ((JScrollPane)cp).setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                            break;
                        case "VERTICAL_SCROLLBAR_NEVER":
                            ((JScrollPane)cp).setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                            break;
                        case "VERTICAL_SCROLLBAR_ALWAYS":
                            ((JScrollPane)cp).setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                            break;
                        default:
                            break;
                    }
                }
            }
            
            @Override
            public String getDescription()
            {
                return "该滚动面板的垂直滚动条的策略";
            }
        });
        
        supportedAttrNameBuilderMap.put("status", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((IOS7SwitchButton)cp).setStatus(Boolean.valueOf(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "按钮的开关状态";
            }
        });
        
        supportedAttrNameBuilderMap.put("caretColor", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JTextComponent)cp).setCaretColor(ColorUtils.str2Color(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "该文本组件的插入符号的颜色";
            }
        });
        
        supportedAttrNameBuilderMap.put("background", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JComponent)cp).setBackground(ColorUtils.str2Color(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "该组件的背景色";
            }
        });
        supportedAttrNameBuilderMap.put("foreground", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                ((JComponent)cp).setForeground(ColorUtils.str2Color(attrValue));
            }
            
            @Override
            public String getDescription()
            {
                return "该组件的前景色";
            }
        });
        
        supportedAttrNameBuilderMap.put("items", new AttrExecutor()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                String[] items = attrValue.split(NodexConstants.REGEX_SEPERATOR_VERTICAL_LINE);
                for (String itemName : items)
                {
                    ((JComboBox<String>)cp).addItem(StringUtils.trim(itemName));
                }
            }
            
            @Override
            public String getDescription()
            {
                return "设置该组合框所包含的文本元素（为了做到开箱即用）";
            }
        });
        
        supportedAttrNameBuilderMap.put("selectedIndex", new AttrExecutor()
        {
            @SuppressWarnings({"rawtypes"})
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                if (cp instanceof JComboBox)
                {
                    ((JComboBox)cp).setSelectedIndex(Integer.valueOf(attrValue));
                }
                else if (cp instanceof JList)
                {
                    ((JList)cp).setSelectedIndex(Integer.valueOf(attrValue));
                }
            }
            
            @Override
            public String getDescription()
            {
                return "设置该JComboBox或者JList选中的元素的索引（为了做到开箱即用）";
            }
        });
        
        supportedAttrNameBuilderMap.put("UI", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                //BEButtonUI|NormalColor.blue
                String[] uiParams = attrValue.split(NodexConstants.REGEX_SEPERATOR_VERTICAL_LINE);
                if ("BEButtonUI".equals(uiParams[0]))
                {
                    String colorName = uiParams[1].substring(uiParams[1].indexOf(".") + 1);
                    BEButtonUI.NormalColor color = null;
                    //  normal, green, red, lightBlue, blue;
                    if (colorName.equals(BEButtonUI.NormalColor.normal.toString()))
                    {
                        color = BEButtonUI.NormalColor.normal;
                    }
                    else if (colorName.equals(BEButtonUI.NormalColor.green.toString()))
                    {
                        color = BEButtonUI.NormalColor.green;
                    }
                    else if (colorName.equals(BEButtonUI.NormalColor.red.toString()))
                    {
                        color = BEButtonUI.NormalColor.red;
                    }
                    else if (colorName.equals(BEButtonUI.NormalColor.lightBlue.toString()))
                    {
                        color = BEButtonUI.NormalColor.lightBlue;
                    }
                    else if (colorName.equals(BEButtonUI.NormalColor.blue.toString()))
                    {
                        color = BEButtonUI.NormalColor.blue;
                    }
                    else
                    {
                        color = BEButtonUI.NormalColor.normal;//不识别，则选用默认的颜色
                    }
                    ((JButton)cp).setUI(new BEButtonUI().setNormalColor(color));
                }
            }
            
            @Override
            public String getDescription()
            {
                return "该按钮的UI样式外观";
            }
        });
        supportedAttrNameBuilderMap.put("border", new AttrExecutor()
        {
            @Override
            public void exec(NodeX nodeX, Object cp, String attrValue)
            {
                //CompoundBorder|0,7,0,7
                //TitledBorder|ss状态
                String[] params = attrValue.split(NodexConstants.REGEX_SEPERATOR_VERTICAL_LINE);
                if ("CompoundBorder".equals(params[0]))
                {
                    String[] borderParams = params[1].split(",");
                    int top = Integer.valueOf(borderParams[0].trim());
                    int left = Integer.valueOf(borderParams[1].trim());
                    int bottom = Integer.valueOf(borderParams[2].trim());
                    int right = Integer.valueOf(borderParams[3].trim());
                    ((JComponent)cp)
                        .setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(top, left, bottom, right), ((JComponent)cp).getBorder()));
                }
                else if ("TitledBorder".equals(params[0]))
                {
                    ((JComponent)cp).setBorder(BorderFactory.createTitledBorder(params[1]));
                }
            }
            
            @Override
            public String getDescription()
            {
                return "该组件的边框属性，CompoundBorder规则是：上左下右";
            }
        });
        
        //对所支持的属性名进行排序后展示
        List<String> keys = new ArrayList<String>();
        keys.addAll(supportedAttrNameBuilderMap.keySet());
        Collections.sort(keys, new Comparator<String>()
        {
            @Override
            public int compare(String o1, String o2)
            {
                return o1.compareToIgnoreCase(o2);
            }
        });
        
        for (String key : keys)
        {
            Logs.i(String.format("%s——%s", key, supportedAttrNameBuilderMap.get(key).getDescription()));
        }
        System.out.println();
        //        Logs.i(String.format("当前SwingXml框架所支持的所有属性为：\n%s", keys));
    }
    
    /**
     * 构建所有目前可以被支持的属性
     * @author nan.li
     * @param nodeX
     * @param cp
     */
    public static void buildAllAvail(NodeX nodeX, Object cp)
    {
        Logs.d(String.format("开始构建%s", cp.getClass().getCanonicalName()));
        //遍历所有的属性
        SmartHashMap<String, String> nodexAttrMap = nodeX.getAttrsMap();
        //所有可支持的属性设置器都在这里
        for (String attrName : supportedAttrNameBuilderMap.keySet())
        {
            //还得检测当前的节点是否包含该attrName。如果当前节点包含了该attrName，则将其值取出来，执行。否则，忽略之
            if (nodexAttrMap.containsKey(attrName))
            {
                Logs.d(String.format("开始设置属性:%s【%s】", attrName, supportedAttrNameBuilderMap.get(attrName).getDescription()));
                String attrValue = nodexAttrMap.get(attrName);//取出这个属性（取出的时候，这个属性就会被自动使用过）
                AttrExecutor attrExecutor = (AttrExecutor)supportedAttrNameBuilderMap.get(attrName);//获取相应的设置属性的执行器
                //接下来，就是利用执行器，执行相应的属性设置程序
                try
                {
                    attrExecutor.exec(nodeX, cp, attrValue);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        //特殊情况，双属性设置。单独处理之
        int width = 0, height = 0;//默认值
        if (nodexAttrMap.containsKey("width"))
        {
            width = Integer.valueOf(nodexAttrMap.get("width"));
        }
        if (nodexAttrMap.containsKey("height"))
        {
            height = Integer.valueOf(nodexAttrMap.get("height"));
        }
        if (width > 0 || height > 0)
        {
            //如果都不是默认值，那么就额外设置一下宽高
            ((Component)cp).setPreferredSize(new Dimension(width, height));
        }
        
        System.out.println();
    }
    
    /**
     * 获取尺寸属性
     * @author nan.li
     * @param attrValue
     * @return
     */
    protected static Dimension getDimension(String attrValue)
    {
        String[] dimens = StringUtils.split(attrValue, NodexConstants.REGEX_SEPERATOR_COMMA);
        int width = Integer.valueOf(StringUtils.trim(dimens[0]));
        int height = Integer.valueOf(StringUtils.trim(dimens[1]));
        return new Dimension(width, height);
    }
}
