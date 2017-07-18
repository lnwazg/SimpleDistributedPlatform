package com.lnwazg.swing.xmlbuilder.iface;

/**
 * 采用xml的方式构建，就需要实现该接口
 * @author nan.li
 * @version 2015-10-30
 */
public interface XmlBuildSupport
{
    /**
     * 界面元素自动绑定完成之后，需要继续完成剩余的初始化工作。
     * 例如事件绑定等
     * @author nan.li
     */
    void afterUIBind();
}
