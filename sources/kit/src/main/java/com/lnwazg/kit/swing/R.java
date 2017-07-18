package com.lnwazg.kit.swing;

import java.awt.Image;

import javax.swing.Icon;

/**
 * 资源管理器
 * @author nan.li
 * @version 2016年4月22日
 */
public class R
{
    /**
     * 快速获取到一个icon对象
     * @author nan.li
     * @param path
     * @return
     */
    public static Icon icon(String path)
    {
        return ImageUtil.getIcon(path);
    }
    
    /**
     * 快速获取到一个image对象
     * @author nan.li
     * @param path
     * @return
     */
    public static Image image(String path)
    {
        return ImageUtil.image(path);
    }
    
}
