package com.lnwazg.swing.xmlbuilder.map.caseinsensitive;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.map.CaseInsensitiveMap;

/**
 * 智能的大小写不敏感的HashMap<br>
 * 能够记住哪些key被获取过，并且能获取到从未使用过的key列表<br>
 * 当重复put已经存在的key的时候，会给出警告提示
 * @author nan.li
 * @version 2016年1月29日
 */
@SuppressWarnings("unchecked")
public class SmartCaseInsensitiveHashMap<K, V> extends CaseInsensitiveMap
{
    private static final long serialVersionUID = 1884796459202628607L;
    
    private Set<K> usedKeys = new HashSet<K>();
    
    /**
     * 需要忽略检查的属性
     * 因为这些属性往往会有特殊用途
     */
    private Set<K> ignoredKeys = new HashSet<K>();
    
    {
        ignoredKeys.add((K)"position");//用于在BorderLayout里面指定布局的位置
        ignoredKeys.add((K)"w");//指定该组件所占用的宽度
        ignoredKeys.add((K)"xmlRef");//该属性用于引用其他的xml文件
    }
    
    @Override
    public V get(Object key)
    {
        usedKeys.add((K)key);
        return (V)super.get(key);
    }
    
    @Override
    public Object put(Object key, Object value)
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
    
    public static void main(String[] args)
    {
        SmartCaseInsensitiveHashMap<String, String> map = new SmartCaseInsensitiveHashMap<String, String>();
        map.put("aaa", "sdfsdf");
        map.put("AAA", "ttttt");
        System.out.println(map.getUnUsedKeys());
        System.out.println(map.get("aaa"));
        System.out.println(map.getUnUsedKeys());
    }
}
