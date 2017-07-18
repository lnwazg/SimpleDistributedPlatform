package com.lnwazg.kit.swing.ui.comp;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * 自定义的无边框填充的按钮
 * @author nan.li
 * @version 2015-10-14
 */
public class NonBorderButton extends JButton
{
    private static final long serialVersionUID = -1424498987672758435L;
    
    private Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
    
    public NonBorderButton()
    {
        super();
        doConfiguration();
    }
    
    public NonBorderButton(Icon icon)
    {
        super(icon);
        doConfiguration();
    }
    
    private void doConfiguration()
    {
        this.setContentAreaFilled(false);
        this.setBorder(emptyBorder);
        this.setOpaque(false);
        this.setFocusPainted(false);//当获得焦点的时候是否绘制“已获取焦点”的状态
        this.setRolloverEnabled(true);
    }
}
