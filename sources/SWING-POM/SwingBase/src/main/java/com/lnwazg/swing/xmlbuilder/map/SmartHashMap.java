package com.lnwazg.swing.xmlbuilder.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 智能的HashMap<br>
 * 能够记住哪些key被获取过，并且能获取到从未使用过的key列表<br>
 * 当重复put已经存在的key的时候，会给出警告提示
 * @author nan.li
 * @version 2016年1月29日
 */
@SuppressWarnings("unchecked")
public class SmartHashMap<K, V> extends HashMap<K, V>
{
    private static final long serialVersionUID = 1884796459202628607L;
    
    /**
     * 已经使用过了的key的列表
     */
    private Set<K> usedKeys = new HashSet<>();
    
    /**
     * 需要忽略检查的属性<br>
     * 因为这些属性往往会有特殊用途<br>
     * 例如引用第三方的布局文件：xmlRef、 特殊的布局属性等
     */
    private Set<K> ignoredKeys = new HashSet<K>();
    
    {
        ignoredKeys.add((K)"position");//用于在BorderLayout里面指定布局的位置
        ignoredKeys.add((K)"w");//指定该组件所占用的宽度
        ignoredKeys.add((K)"xmlRef");//该属性用于引用其他的xml文件
    }
    
    /**
     * 当取出了某个key之后，就代表着该key被使用过了！<br>
     * {@inheritDoc}
     */
    @Override
    public V get(Object key)
    {
        usedKeys.add((K)key);
        return super.get(key);
    }
    
    /**
     * 主动使用某个key
     * @author nan.li
     * @param key
     */
    public void use(Object key)
    {
        usedKeys.add((K)key);
    }
    
    @Override
    public V put(K key, V value)
    {
        if (this.containsKey(key))
        {
            System.err.println(String.format("警告：已经存在的key【%s】被重复填充数据！", key));
        }
        return super.put(key, value);
    }
    
    /**
     * 获取从未被使用过的key的集合
     * @author nan.li
     * @return
     */
    public Set<K> getUnUsedKeys()
    {
        Set<K> unusedKeys = new HashSet<K>();
        Set<K> allSet = keySet();
        for (K k : allSet)
        {
            if (!usedKeys.contains(k) && !ignoredKeys.contains(k))
            {
                unusedKeys.add(k);
            }
        }
        return unusedKeys;
    }
    
}
