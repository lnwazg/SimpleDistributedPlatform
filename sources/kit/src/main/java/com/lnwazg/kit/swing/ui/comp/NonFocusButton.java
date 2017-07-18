package com.lnwazg.kit.swing.ui.comp;

import javax.swing.JButton;

/**
 * 无聚焦效果的按钮
 * @author Administrator
 * @version 2016年2月14日
 */
public class NonFocusButton extends JButton
{
    private static final long serialVersionUID = -4212717402540583400L;
    
    public NonFocusButton()
    {
        super();
        doConfiguration();
    }
    
    public NonFocusButton(String text)
    {
        super(text);
        doConfiguration();
    }
    
    private void doConfiguration()
    {
        //当获得焦点的时候是否绘制“已获取焦点”的状态
        this.setFocusPainted(false);
    }
}
