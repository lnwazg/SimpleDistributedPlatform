package com.lnwazg.swing.xmlbuilder.builder;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;

import com.lnwazg.kit.swing.ui.comp.ImageScroller;
import com.lnwazg.swing.xmlbuilder.NodeX;
import com.lnwazg.swing.xmlbuilder.namedcomp.TitledLineSeparator;

/**
 * 值构建器
 * @author nan.li
 * @version 2016年1月28日
 */
public class ValBuilder
{
    /**
     * 所有的值都采用以下的方式进行构建
     * @author nan.li
     * @param nodeX
     * @param cp
     */
    public static void buildAllAvail(NodeX nodeX, Object cp)
    {
        /**
         * 值构建
         */
        if (StringUtils.isNotEmpty(nodeX.getValue()))
        {
            String value = nodeX.getValue();
            if (cp instanceof JTextComponent)
            {
                ((JTextComponent)cp).setText(nodeX.getValue());
            }
            else if (cp instanceof JLabel)
            {
                ((JLabel)cp).setText(nodeX.getValue());
            }
            else if (cp instanceof AbstractButton)
            {
                ((AbstractButton)cp).setText(nodeX.getValue());
            }
            else if (cp instanceof TitledLineSeparator)
            {
                ((TitledLineSeparator)cp).setTitle(value);
            }
            else if (cp instanceof ImageScroller)
            {
                Image icon = Toolkit.getDefaultToolkit().createImage(GlobalAttrBuilder.class.getClassLoader().getResource(value));
                ((ImageScroller)cp).setImageContent(new ImageIcon(icon));
            }
        }
    }
}
