package com.lnwazg.kit.swing.screenshot;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * 屏幕的工具类
 * @author nan.li
 * @version 2016年12月29日
 */
public class ScreenKit
{
    public static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    
    /**
     * 屏幕宽
     */
    public static int screenWidth = (int)dimension.getWidth();
    
    /**
     * 屏幕高
     */
    public static int screenHeight = (int)dimension.getHeight();
    
    /**
     * 宽的一半
     */
    public static int screenWidthHalf = (int)screenWidth / 2;
    
    /**
     * 高的一半
     */
    public static int screenHeightHalf = (int)screenHeight / 2;
    
    /**
     * 宽的四分之一
     */
    public static int screenWidthQuarter = (int)screenWidthHalf / 2;
    
    /**
     * 宽的四分之一
     */
    public static int screenWidthHalfHalf = screenWidthQuarter;
    
    /**
     * 高的四分之一
     */
    public static int screenHeightQuarter = (int)screenHeightHalf / 2;
    
    /**
     * 高的四分之一
     */
    public static int screenHeightHalfHalf = screenHeightQuarter;
    
    /**
     * 宽一半的三分之一
     */
    public static int screenWidthHalfOneThird = (int)screenWidthHalf / 3;
    
    /**
     * 高一半的三分之一
     */
    public static int screenHeightHalfOneThird = (int)screenHeightHalf / 3;
    
    /**
     * 宽的三分之一
     */
    public static int screenWidthOneThird = (int)screenWidth / 3;
    
    /**
     * 高的三分之一
     */
    public static int screenHeightOneThird = (int)screenHeight / 3;
}
