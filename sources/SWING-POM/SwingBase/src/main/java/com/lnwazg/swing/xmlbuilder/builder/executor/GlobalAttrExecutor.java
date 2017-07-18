package com.lnwazg.swing.xmlbuilder.builder.executor;

import javax.swing.JFrame;

import com.lnwazg.swing.xmlbuilder.builder.executor.parent.Describable;

/**
 * 设置属性的执行器
 * @author nan.li
 * @version 2016年1月28日
 */
public interface GlobalAttrExecutor extends Describable
{
    /**
     * 执行这个属性设置的过程
     * @author nan.li
     * @param nodeX
     * @param cp
     * @param attrValue
     */
    void exec(JFrame frame, String attrValue);
}
