package com.lnwazg.kit.swing.image;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.apache.commons.lang3.StringUtils;

/**
 * 图像工具类
 * @author nan.li
 * @version 2017年4月22日
 */
public class GraphicsKit
{
    static BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    
    static Graphics g = image.getGraphics();
    
    /**
     * 获取某个字符串在某个字体下的真实宽度
     * @author nan.li
     * @param font
     * @param str
     * @return
     */
    public static int getStrWidthByFontAndStr(Font font, String str)
    {
        if (StringUtils.isEmpty(str))
        {
            return 0;
        }
        g.setFont(font);
        return g.getFontMetrics().stringWidth(str);
    }
    
}
