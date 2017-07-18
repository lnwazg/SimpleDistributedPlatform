package com.lnwazg.kit.freemarker;

import com.lnwazg.kit.list.Lists;
import com.lnwazg.kit.map.Maps;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.TestCase;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TestFreemarker
{
    /**
     * 教程的位置：http://blog.csdn.net/chenghui0317/article/details/7832474
     * @author lnwazg@126.com
     * @param args
     */
    public static void main(String[] args)
    {
        TF.l(TestFreemarker.class);
    }
    
    @TestCase
    void testClassTemplateLoader()
    {
        try
        {
            //创建一个合适的Configration对象  
            Configuration configuration = FreeMkKit.getConfigurationByClass(TestFreemarker.class, "");
            Template template = configuration.getTemplate("1.ftl");
            String result = FreeMkKit.format(template, "student", Maps.asMap("studentName", "张三丰2", "studentSex", "男"), "description", "这是老子的描述信息", "nameList", Lists.asList("陈靖仇", "bbb", "ccc"), "weaponMap", Maps.asMap("ttt", "ttt", "sss", "sss"));
            System.out.println(result);
            //template.process(m, new OutputStreamWriter(new FileOutputStream("success.html"), "UTF-8"));
            //System.out.println("恭喜，生成成功~~");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //    @TestCase
    void test1()
    {
        try
        {
            //创建一个合适的Configration对象  
            Configuration configuration = FreeMkKit.getConfigurationByDirectory("O:\\2012\\mavenPrj\\kit\\src\\main\\java\\com\\lnwazg\\kit\\freemarker");
            Template template = configuration.getTemplate("1.ftl");
            //            Map<String, Object> m = Maps.asMap("student",
            //                Maps.asMap("studentName", "张三丰2", "studentSex", "男"),
            //                "description",
            //                "这是老子的描述信息",
            //                "nameList",
            //                Lists.asList("陈靖仇", "bbb", "ccc"),
            //                "weaponMap",
            //                Maps.asMap("ttt", "ttt", "sss", "sss"));
            //            String result = FreemarkerKit.format(template, m);
            String result = FreeMkKit.format(template, "student", Maps.asMap("studentName", "张三丰2", "studentSex", "男"), "description", "这是老子的描述信息", "nameList", Lists.asList("陈靖仇", "bbb", "ccc"), "weaponMap", Maps.asMap("ttt", "ttt", "sss", "sss"));
            System.out.println(result);
            
            //template.process(m, new OutputStreamWriter(new FileOutputStream("success.html"), "UTF-8"));
            //System.out.println("恭喜，生成成功~~");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
        @TestCase
    void test2()
    {
        String result = FreeMkKit.format("用户名：${user};\nURL：    ${url};\n姓名： 　${name}", "user", "lavasoft", "url", "http://www.baidu.com/", "name", "百度");
        System.out.println(result);
    }
}
