package test.lnwazg;

import javax.script.ScriptException;

import com.lnwazg.kit.js.JsUtils;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.TestCase;

/**
 * 测试JS工具
 * @author nan.li
 * @version 2016年4月14日
 */
public class JsUtilsTest
{
    //    @TestIt
    void test1()
    {
        try
        {
            System.out.println(JsUtils.loadJs("c:\\1.js").invokeFunction("add", 12134.2134234));
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
        }
    }
    
    //        @TestIt
    void test2()
    {
        System.out.println(JsUtils.invoke("c:\\1.js", "add", 12134.2134234));
    }
    
    //    @TestIt
    void test3()
    {
        //循环测试100次调用，调用期间，可动态修改脚本哦，也不会报错！
        //真正的热加载、热替换！
        for (int i = 0; i < 100; i++)
        {
            System.out.println(JsUtils.invoke("c:\\1.js", "add", 12134.2134234));
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    //    @TestIt
    void test4()
    {
        //命令式的编程，循环调用100次某个方法，观察其输出结果
        JsUtils.cycleTestInvoke(100, "c:\\1.js", "add", 8774);
    }
    
    //    @TestIt
    void test5()
    {
        //命令式的编程，无限循环调用，观察其输出结果
        JsUtils.cycleTestInvoke("c:\\1.js", "add", 8774);
    }
    
    //    @TestIt
    void test6()
    {
        System.out.println(JsUtils.invoke("c:\\1.js"));
    }
    
    @TestCase
    void test7()
    {
        JsUtils.cycleTestInvoke("c:\\1.js");
    }
    
    public static void main(String[] args)
    {
        //        TestFramework.loadTest(JsUtilsTest.class);
        TF.l(JsUtilsTest.class);
    }
}
