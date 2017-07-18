package com.lnwazg.swing.xmlbuilder.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.JTree;

import org.apache.commons.lang3.StringUtils;

import com.eva.epc.widget.HardLayoutPane;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.swing.ui.comp.ImageScroller;
import com.lnwazg.kit.swing.ui.comp.NonBorderButton;
import com.lnwazg.kit.swing.ui.comp.SmartButton;
import com.lnwazg.swing.util.ui.IOS7SwitchButton;
import com.lnwazg.swing.xmlbuilder.NodeX;
import com.lnwazg.swing.xmlbuilder.XmlLayoutBuilder;
import com.lnwazg.swing.xmlbuilder.builder.executor.CompExecutor;
import com.lnwazg.swing.xmlbuilder.map.SmartHashMap;
import com.lnwazg.swing.xmlbuilder.namedcomp.NextLine;
import com.lnwazg.swing.xmlbuilder.namedcomp.TitledLineSeparator;

/**
 * 组件构建器<br>
 * 根据组件名称创造相应的组件
 * @author Administrator
 * @version 2016年1月31日
 */
public class ComponentBuilder
{
    //占位，用于强行执行类加载
    public static String a;
    
    /**
     * 支持的组件名称以及相应的组件构建器的表
     */
    private static Map<String, CompExecutor> supportedCompNameBuilderMap = new SmartHashMap<String, CompExecutor>();
    
    static
    {
        Logs.i("目前SwingXml框架支持的组件如下：\n");
        //初始化各个组件的构建过程
        supportedCompNameBuilderMap.put("Container", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                XmlLayoutBuilder.globalAttrsMap = attrsMap;//根节点上面的属性一律视为全局属性
                //假如是根节点，那么就不需要初始化组件，直接获取现成的就可以了
                return frame.getContentPane();
            }
            
            @Override
            public String getDescription()
            {
                return "容器，通常是一个顶层容器，例如JFrame";
            }
        });
        supportedCompNameBuilderMap.put("JPanel", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JPanel();
            }
            
            @Override
            public String getDescription()
            {
                return "JPanel，一种最常见的容器";
            }
        });
        
        supportedCompNameBuilderMap.put("JSlider", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JSlider();
            }
            
            @Override
            public String getDescription()
            {
                return "JSlider，滑动块";
            }
        });
        supportedCompNameBuilderMap.put("JSpinner", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JSpinner();
            }
            
            @Override
            public String getDescription()
            {
                return "JSpinner类是一个组件，它可以让用户选择一个数字或者一个对象值从一个有序的序列使用一个输入字段中";
            }
        });
        supportedCompNameBuilderMap.put("JToolBar", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JToolBar();
            }
            
            @Override
            public String getDescription()
            {
                return "工具栏";
            }
        });
        
        supportedCompNameBuilderMap.put("ImageScroller", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new ImageScroller();
            }
            
            @Override
            public String getDescription()
            {
                return "图像滚动查看器";
            }
        });
        
        supportedCompNameBuilderMap.put("JToolTip", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JToolTip();
            }
            
            @Override
            public String getDescription()
            {
                return "工具提示";
            }
        });
        
        supportedCompNameBuilderMap.put("JOptionPane", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JOptionPane();
            }
            
            @Override
            public String getDescription()
            {
                return "选项框";
            }
        });
        
        supportedCompNameBuilderMap.put("JScrollPane", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JScrollPane();
            }
            
            @Override
            public String getDescription()
            {
                return "滑动块容器";
            }
        });
        
        supportedCompNameBuilderMap.put("JComboBox", new CompExecutor()
        {
            @SuppressWarnings("rawtypes")
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JComboBox();
            }
            
            @Override
            public String getDescription()
            {
                return "组合框，用于展示下拉列表";
            }
        });
        
        supportedCompNameBuilderMap.put("JList", new CompExecutor()
        {
            @SuppressWarnings("rawtypes")
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JList();
            }
            
            @Override
            public String getDescription()
            {
                return "用于展示一个可多选的列表";
            }
        });
        
        supportedCompNameBuilderMap.put("JTable", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JTable();
            }
            
            @Override
            public String getDescription()
            {
                return "swing表格";
            }
        });
        supportedCompNameBuilderMap.put("JTree", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JTree();
            }
            
            @Override
            public String getDescription()
            {
                return "树控件";
            }
        });
        
        supportedCompNameBuilderMap.put("JTextPane", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JTextPane();
            }
            
            @Override
            public String getDescription()
            {
                return "文本框";
            }
        });
        
        supportedCompNameBuilderMap.put("JEditorPane", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JEditorPane();
            }
            
            @Override
            public String getDescription()
            {
                return "文本编辑框";
            }
        });
        supportedCompNameBuilderMap.put("JTextArea", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JTextArea();
            }
            
            @Override
            public String getDescription()
            {
                return "文本区域";
            }
        });
        
        supportedCompNameBuilderMap.put("JTextField", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JTextField();
            }
            
            @Override
            public String getDescription()
            {
                return "文字框";
            }
        });
        
        supportedCompNameBuilderMap.put("JPasswordField", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JPasswordField();
            }
            
            @Override
            public String getDescription()
            {
                return "密码文字框";
            }
        });
        supportedCompNameBuilderMap.put("HardLayoutPane", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new HardLayoutPane();
            }
            
            @Override
            public String getDescription()
            {
                return "一种布局容器";
            }
        });
        supportedCompNameBuilderMap.put("JLabel", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JLabel();
            }
            
            @Override
            public String getDescription()
            {
                return "标签文本";
            }
        });
        supportedCompNameBuilderMap.put("JButton", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JButton();
            }
            
            @Override
            public String getDescription()
            {
                return "按钮";
            }
        });
        supportedCompNameBuilderMap.put("ButtonGroup", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new ButtonGroup();
            }
            
            @Override
            public String getDescription()
            {
                return "按钮组";
            }
        });
        supportedCompNameBuilderMap.put("JTabbedPane", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JTabbedPane();
            }
            
            @Override
            public String getDescription()
            {
                return "标签栏控件";
            }
        });
        supportedCompNameBuilderMap.put("JCheckBox", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JCheckBox();
            }
            
            @Override
            public String getDescription()
            {
                return "多选框";
            }
        });
        supportedCompNameBuilderMap.put("JRadioButton", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JRadioButton();
            }
            
            @Override
            public String getDescription()
            {
                return "单选框";
            }
        });
        supportedCompNameBuilderMap.put("NonBorderButton", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new NonBorderButton();
            }
            
            @Override
            public String getDescription()
            {
                return "无边框按钮";
            }
        });
        supportedCompNameBuilderMap.put("SmartButton", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new SmartButton();
            }
            
            @Override
            public String getDescription()
            {
                return "瘦身美化版的按钮";
            }
        });
        supportedCompNameBuilderMap.put("IOS7SwitchButton", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new IOS7SwitchButton();
            }
            
            @Override
            public String getDescription()
            {
                return "IOS7风格的开关按钮";
            }
        });
        
        supportedCompNameBuilderMap.put("JMenuBar", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JMenuBar();
            }
            
            @Override
            public String getDescription()
            {
                return "菜单栏";
            }
        });
        supportedCompNameBuilderMap.put("JMenu", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new JMenu();
            }
            
            @Override
            public String getDescription()
            {
                return "菜单";
            }
        });
        
        supportedCompNameBuilderMap.put("TitledLineSeparator", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                Object ret = null;
                if (StringUtils.isNotEmpty(value))
                {
                    ret = new TitledLineSeparator(value);
                }
                else
                {
                    ret = new TitledLineSeparator();
                }
                return ret;
            }
            
            @Override
            public String getDescription()
            {
                return "分隔线";
            }
        });
        
        supportedCompNameBuilderMap.put("NextLine", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return new NextLine();
            }
            
            @Override
            public String getDescription()
            {
                return "换行";
            }
        });
        supportedCompNameBuilderMap.put("glue", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return Box.createGlue();
            }
            
            @Override
            public String getDescription()
            {
                return "胶水，填充所有空白空间";
            }
        });
        supportedCompNameBuilderMap.put("horizontalGlue", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return Box.createHorizontalGlue();
            }
            
            @Override
            public String getDescription()
            {
                return "水平方向的胶水";
            }
        });
        
        supportedCompNameBuilderMap.put("verticalGlue", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return Box.createVerticalGlue();
            }
            
            @Override
            public String getDescription()
            {
                return "垂直方向的胶水";
            }
        });
        supportedCompNameBuilderMap.put("horizontalBox", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return Box.createHorizontalBox();
            }
            
            @Override
            public String getDescription()
            {
                return "水平方向的盒子";
            }
        });
        
        supportedCompNameBuilderMap.put("verticalBox", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return Box.createVerticalBox();
            }
            
            @Override
            public String getDescription()
            {
                return "垂直方向的盒子";
            }
        });
        supportedCompNameBuilderMap.put("horizontalStrut", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return Box.createHorizontalStrut(Integer.valueOf(value));
            }
            
            @Override
            public String getDescription()
            {
                return "水平方向的承重墙";
            }
        });
        supportedCompNameBuilderMap.put("verticalStrut", new CompExecutor()
        {
            @Override
            public Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value)
            {
                return Box.createVerticalStrut(Integer.valueOf(value));
            }
            
            @Override
            public String getDescription()
            {
                return "垂直方向的承重墙";
            }
        });
        
        JavaFxComponentBuilder.addJavaFxCompsSupport(supportedCompNameBuilderMap);
        
        //对所支持的属性名进行排序后展示
        List<String> keys = new ArrayList<String>();
        keys.addAll(supportedCompNameBuilderMap.keySet());
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
            Logs.i(String.format("%s——%s", key, supportedCompNameBuilderMap.get(key).getDescription()));
        }
        System.out.println();
        //        Logs.i(String.format("当前SwingXml框架所支持的所有组件为：\n%s", keys));
    }
    
    /**
     * 根据名称构建组件
     * @author Administrator
     * @param nodeX
     * @param frame
     * @return
     */
    public static Object build(NodeX nodeX, JFrame frame)
    {
        Object ret = null;//待返回的组件对象
        String name = nodeX.getName();
        String value = nodeX.getValue();
        SmartHashMap<String, String> attrsMap = nodeX.getAttrsMap();
        
        if (supportedCompNameBuilderMap.containsKey(name))
        {
            CompExecutor compExecutor = (CompExecutor)supportedCompNameBuilderMap.get(name);
            ret = compExecutor.getInstance(nodeX, frame, attrsMap, value);
        }
        else
        {
            Logs.w(String.format("无法识别的组件名称：%s, 也许你该扩展组件了？", name));
        }
        return ret;
    }
}
