package com.lnwazg.swing.xmlbuilder.map;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.swing.xmlbuilder.builder.executor.parent.Describable;

/**
 * 当属性被放入时，会自描述
 * @author nan.li
 * @version 2016年2月1日
 */
public class SmartDescribleHashMap<K, V> extends SmartHashMap<String, Describable>
{
    private static final long serialVersionUID = -1097570206196636388L;
    
    @Override
    public Describable put(String key, Describable value)
    {
        Logs.i(String.format("%s——%s", key, value.getDescription()));
        return super.put(key, value);
    }
}
