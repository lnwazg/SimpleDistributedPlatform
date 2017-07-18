package com.lnwazg.kit.str;

/**
 * 抓取结果二元组
 * 
 * @author  lnwazg
 * @version  [版本号, 2011-12-21]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class TwoTuples
{
    private String firstString;
    
    private String secondString;
    
    public TwoTuples()
    {
        firstString = "";
        secondString = "";
    }
    
    public String getFirstString()
    {
        return firstString;
    }
    
    public void setFirstString(String firstString)
    {
        this.firstString = firstString;
    }
    
    public String getSecondString()
    {
        return secondString;
    }
    
    public void setSecondString(String secondString)
    {
        this.secondString = secondString;
    }
}
