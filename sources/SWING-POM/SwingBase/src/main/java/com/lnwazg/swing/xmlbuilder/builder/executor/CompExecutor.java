package com.lnwazg.swing.xmlbuilder.builder.executor;

import javax.swing.JFrame;

import com.lnwazg.swing.xmlbuilder.NodeX;
import com.lnwazg.swing.xmlbuilder.builder.executor.parent.Describable;
import com.lnwazg.swing.xmlbuilder.map.SmartHashMap;

/**
 * 组件生成器
 * @author nan.li
 * @version 2016年2月2日
 */
public interface CompExecutor extends Describable
{
    /**
     * 获得该节点对应的组件的实例
     * @author nan.li
     * @param nodeX  该节点对象
     * @param frame
     * @param attrsMap 该节点上面的属性表
     * @param value 该节点的值
     * @return
     */
    Object getInstance(NodeX nodeX, JFrame frame, SmartHashMap<String, String> attrsMap, String value);
}
