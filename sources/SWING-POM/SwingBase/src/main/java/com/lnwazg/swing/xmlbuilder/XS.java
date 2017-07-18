package com.lnwazg.swing.xmlbuilder;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

/**
 * 类似于Jquery的$
 * $(this) 变成了$.get(clazz)
 * @author nan.li
 * @version 2015-11-1
 */
public class XS
{
    /**
     * 类-注册表对象，关联表
     * 每个类绑定唯一的注册表对象
     */
    static Map<Class<?>, CompRegistry> m = new HashMap<Class<?>, CompRegistry>();
    
    /**
     * 根据类名称，去获取某个窗体所绑定的注册表对象
     * @author nan.li
     * @param clazz
     * @return
     */
    public static CompRegistry get(Class<?> clazz)
    {
        if (m.get(clazz) == null)
        {
            CompRegistry compRegistry = new CompRegistry();
            m.put(clazz, compRegistry);
        }
        return m.get(clazz);
    }
    
    /**
     * 根据实例对象取出相应的注册表类
     * @author Administrator
     * @param frame
     * @return
     */
    public static CompRegistry get(JFrame frame)
    {
        return get(frame.getClass());
    }
}
