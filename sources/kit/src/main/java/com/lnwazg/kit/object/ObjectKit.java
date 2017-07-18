package com.lnwazg.kit.object;

/**
 * 对象工具类
 * @author nan.li
 * @version 2017年6月29日
 */
public class ObjectKit
{
    /**
     * 如果参数对象为空，则将其转为0；否则返回原有对象
     * @author nan.li
     * @param obj
     * @return
     */
    public static Object null2Zero(Object obj)
    {
        if (obj == null)
        {
            return 0;
        }
        return obj;
    }
    
    /**
     * 如果参数对象为空，则将其转为"0"；否则返回原有对象
     * @author nan.li
     * @param obj
     * @return
     */
    public static Object null2ZeroStr(Object obj)
    {
        if (obj == null)
        {
            return "0";
        }
        return obj;
    }
    
    /**
     * 如果参数对象为空，则将其转为空字符串；否则返回原有对象
     * @author nan.li
     * @param obj
     * @return
     */
    public static Object null2EmptyStr(Object obj)
    {
        if (obj == null)
        {
            return "";
        }
        return obj;
    }
    
}
