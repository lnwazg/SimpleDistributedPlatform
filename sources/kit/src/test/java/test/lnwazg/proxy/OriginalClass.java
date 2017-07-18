package test.lnwazg.proxy;

/**
 * 一个普通的将要被代理的类
 * 
 * @author  lnwazg
 * @version  [版本号, 2012-1-4]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class OriginalClass
{
    /** 
     * 一个普通的方法
     * @see [类、类#方法、类#成员]
     */
    public void addBook()
    {
        System.out.println("增加一本图书...");
    }
    
    /** 
     * 删除图书的方法
     * @see [类、类#方法、类#成员]
     */
    public void delBook()
    {
        System.out.println("删除一本图书...");
    }
}
