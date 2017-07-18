package com.lnwazg.kit.swing.ui.comp;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * ScrollPane class that demonstrates how to set the various column and row headersand corners.<br>
 * 可卷动的面板的子类，内含一个图片，并对图片进行滚动
 * @author nan.li
 * @version 2016年4月21日
 */
public class ImageScroller extends JScrollPane
{
    private static final long serialVersionUID = 3595940912428715981L;
    
    JLabel jLabel;
    
    public ImageScroller(Icon icon)
    {
        super();
        // Panel to hold the icon image
        JPanel jPanel = new JPanel(new BorderLayout());//用于展示的那个图片panel
        jLabel = new JLabel(icon);
        jPanel.add(jLabel, BorderLayout.CENTER);
        getViewport().add(jPanel);
        //        JScrollBar vsb = getVerticalScrollBar();
        //        JScrollBar hsb = getHorizontalScrollBar();
        //        
        //        vsb.setValue(icon.getIconHeight());
        //        hsb.setValue(icon.getIconWidth() / 10);
    }
    
    public ImageScroller()
    {
        super();
        JPanel jPanel = new JPanel(new BorderLayout());//用于展示的那个图片panel
        jLabel = new JLabel();
        jPanel.add(jLabel, BorderLayout.CENTER);
        getViewport().add(jPanel);
    }
    
    /**
     * 改变这个卷动面板的图片内容
     * @author nan.li
     * @param icon
     */
    public void setImageContent(Icon icon)
    {
        jLabel.setIcon(icon);
    }
}