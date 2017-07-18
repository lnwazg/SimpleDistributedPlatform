package com.lnwazg.swing.xmlbuilder;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;

import com.lnwazg.kit.log.Logs;

/**
 * XML组件的注册表
 * @author Administrator
 * @version 2015年10月31日
 */
public class CompRegistry
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map<String, Component> map = new LinkedHashMap();
    
    /**
     * 按名称去注册一个组件对象
     * @author Administrator
     * @param id
     * @param comp
     */
    public void reg(String id, Component comp)
    {
        if (map.containsKey(id))
        {
            Logs.warn("警告：组件ID名称：" + id + " 重复！当注册组件类型:" + comp.getClass().getSimpleName() + "的时候！同名称的旧组件将被覆盖！！！");
        }
        map.put(id, comp);
    }
    
    public Map<String, Component> getRegistryMap()
    {
        return map;
    }
    
    /**
     * 从注册表中获取当前注册的组件
     * @author nan.li
     * @param id
     * @return
     */
    public Component get(String id)
    {
        return map.get(id);
    }
}
