package com.lnwazg.swing.xmlbuilder;

import javax.swing.JFrame;

import com.lnwazg.swing.xmlbuilder.iface.XmlBuildSupport;

/**
 * 需要实现XML构建支持的JFrame
 * @author Administrator
 * @version 2015年11月3日
 */
public abstract class XmlJFrame extends JFrame implements XmlBuildSupport
{
    private static final long serialVersionUID = -1915997321640851876L;
    
    /**
     * 当前的JFrame所绑定的注册表对象
     */
    protected CompRegistry $;
}
