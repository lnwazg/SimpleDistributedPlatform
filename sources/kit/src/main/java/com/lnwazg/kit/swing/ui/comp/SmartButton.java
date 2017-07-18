package com.lnwazg.kit.swing.ui.comp;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * 小巧而灵活的窄边框无焦点痕迹的button
 * @author Administrator
 * @version 2016年2月14日
 */
public class SmartButton extends NonFocusButton
{
    private static final long serialVersionUID = 3762383673066743833L;
    
    /**
     * 窄边框，为了让按钮看起来更灵巧！
     */
    private Border smallBorder = BorderFactory.createEmptyBorder(5, 6, 5, 6);
    
    public SmartButton()
    {
        super();
        doConfiguration();
    }
    
    public SmartButton(String text)
    {
       super(text);
       doConfiguration();
    }

    private void doConfiguration()
    {
        this.setBorder(smallBorder);
    }
}
