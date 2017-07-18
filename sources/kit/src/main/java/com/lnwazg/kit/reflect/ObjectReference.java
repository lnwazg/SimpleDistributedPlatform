package com.lnwazg.kit.reflect;

/**
 * 对象的引用
 * @author nan.li
 * @version 2016年10月11日
 */
public class ObjectReference<T>
{
    private T obj;
    
    public ObjectReference()
    {
    }
    
    public ObjectReference(T obj)
    {
        super();
        this.obj = obj;
    }
    
    public T get()
    {
        return obj;
    }
    
    public void set(T o)
    {
        this.obj = o;
    }
    
    @Override
    public String toString()
    {
        return "ObjectReference [obj=" + obj + "]";
    }
    
    public static void main(String[] args)
    {
        String aString = "abc";
        System.out.println(aString);
        ObjectReference<String> objectReference = new ObjectReference<>(aString);
        System.out.println(objectReference);
        System.out.println(objectReference.get());
    }
}
