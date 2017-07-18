package com.lnwazg.kit.swing.color;

import java.awt.Color;

import org.apache.commons.lang.StringUtils;

/**
 * 颜色工具类
 * @author Administrator
 * @version 2016年1月30日
 */
public class ColorUtils
{
    /**
     * 将颜色值转换成具体的颜色类型
     * 汇集了常用的颜色集，后期将支持rgb颜色！
     * @author Administrator
     * @param colorName
     * @return
     */
    public static Color str2Color(String colorName)
    {
        if ("black".equals(colorName) || "BLACK".equals(colorName))
        {
            return Color.black;
        }
        else if ("white".equals(colorName) || "WHITE".equals(colorName))
        {
            return Color.white;
        }
        else if ("lightGray".equals(colorName) || "LIGHT_GRAY".equals(colorName))
        {
            return Color.lightGray;
        }
        else if ("gray".equals(colorName) || "GRAY".equals(colorName))
        {
            return Color.gray;
        }
        else if ("darkGray".equals(colorName) || "DARK_GRAY".equals(colorName))
        {
            return Color.darkGray;
        }
        else if ("red".equals(colorName) || "RED".equals(colorName))
        {
            return Color.red;
        }
        else if ("pink".equals(colorName) || "PINK".equals(colorName))
        {
            return Color.pink;
        }
        else if ("orange".equals(colorName) || "ORANGE".equals(colorName))
        {
            return Color.orange;
        }
        else if ("yellow".equals(colorName) || "YELLOW".equals(colorName))
        {
            return Color.yellow;
        }
        else if ("green".equals(colorName) || "GREEN".equals(colorName))
        {
            return Color.green;
        }
        else if ("magenta".equals(colorName) || "MAGENTA".equals(colorName))
        {
            return Color.magenta;
        }
        else if ("cyan".equals(colorName) || "CYAN".equals(colorName))
        {
            return Color.cyan;
        }
        else if ("blue".equals(colorName) || "BLUE".equals(colorName))
        {
            return Color.blue;
        }
        else if ("darkgold".equals(colorName))
        {
            //暗金色，自己扩展的颜色
            return hexStr2Color("#9e7e67");
        }
        else if ("lightgold".equals(colorName))
        {
            //亮金色，自己扩展的颜色
            return hexStr2Color("#ac9c85");
        }
        else if (StringUtils.startsWith(colorName, "$"))
        {
            //具体的颜色，以$开头的（如果以#开头则无法接收参数，因此只能以$开头）
            //此种适用于网页请求参数中传递颜色编码
            //http get param patch 
            return hexStr2Color("#" + colorName.substring(1));
        }
        else if (StringUtils.startsWith(colorName, "#"))
        {
            return hexStr2Color(colorName);
        }
        else
        {
            //rgb值
            //new Color(13, 148, 252)
            //            String[] rgb = colorName.split(", ");
            String[] rgb = StringUtils.split(colorName, ",");
            if (rgb.length == 3)
            {
                int r = Integer.valueOf(StringUtils.trim(rgb[0]));
                int g = Integer.valueOf(StringUtils.trim(rgb[1]));
                int b = Integer.valueOf(StringUtils.trim(rgb[2]));
                return new Color(r, g, b);
            }
            else
            {
                return null;
            }
        }
    }
    
    /**
     * 例如，将Color对象转换成“#fcf6d6”
     * @author nan.li
     * @param color
     * @return
     */
    public static String color2HexStr(Color color)
    {
        String R = Integer.toHexString(color.getRed());
        R = R.length() < 2 ? ('0' + R) : R;
        String G = Integer.toHexString(color.getGreen());
        G = G.length() < 2 ? ('0' + G) : G;
        String B = Integer.toHexString(color.getBlue());
        B = B.length() < 2 ? ('0' + B) : B;
        return ('#' + R + G + B);
    }
    
    /**
     * 例如，将“#fcf6d6”转换成Color对象
     * @author nan.li
     * @param hexStr
     * @return
     */
    public static Color hexStr2Color(String hexStr)
    {
        int i = Integer.parseInt(hexStr.substring(1), 16);
        return new Color(i);
    }
    
    public static void main(String[] args)
    {
        Color color = ColorUtils.str2Color("13, 148, 252");
        System.out.println(color);
        String colorStr = ColorUtils.color2HexStr(color);
        System.out.println(colorStr);
        Color color2 = ColorUtils.hexStr2Color(colorStr);
        System.out.println(color2);
    }
}
