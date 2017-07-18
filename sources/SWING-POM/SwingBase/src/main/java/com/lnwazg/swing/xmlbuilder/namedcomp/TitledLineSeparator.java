package com.lnwazg.swing.xmlbuilder.namedcomp;

/**
 * 用名称代表的组件，命名的组件
 * @author Administrator
 * @version 2015年10月31日
 */
public class TitledLineSeparator
{
    private String title;
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public TitledLineSeparator(String title)
    {
        super();
        this.title = title;
    }
    
    public TitledLineSeparator()
    {
        super();
    }
}
