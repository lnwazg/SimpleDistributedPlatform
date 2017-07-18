package test.lnwazg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.lnwazg.kit.cache.FileCacheLite;
import com.lnwazg.kit.describe.D;
import com.lnwazg.kit.describe.DescribeUtils;
import com.lnwazg.kit.random.RandomStrUtils;
import com.lnwazg.kit.testframework.TestFramework;
import com.lnwazg.kit.testframework.anno.TestCase;

/**
 * 一个最简单的单元测试用例的写法！<br>
 * 造轮子的过程，真是充满乐趣啊！
 * @author nan.li
 * @version 2016年4月14日
 */
public class TestSomething
{
    @TestCase
    private void testLoadComments()
        throws InterruptedException
    {
        D.dSystem();
        FileCacheLite.put("aaa", "tttttt");
        D.d("put begin!");
        for (int i = 0; i < 1000000; i++)
        {
            if (i % 1000 == 0)
            {
                System.out.print(">");
            }
            if (i % 100000 == 0)
            {
                System.out.print("\n");
            }
            FileCacheLite.put(("message" + i), new Message().setNode("小燕").setContent("love" + RandomStrUtils.generateRandomString(30)).setCreateTime(new java.util.Date()));
        }
        D.d("put ok!");
        DescribeUtils.describe(FileCacheLite.get("message100"));
        
        //看来全文搜索的性能确实太差！
        String query = "4DR";
        System.out.println("begin query..");
        long start = System.currentTimeMillis();
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 1000000; i++)
        {
            Message message = (Message)FileCacheLite.get(("message" + i));
            if (message.getContent().indexOf(query) != -1)
            {
                messages.add(message);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("query cost " + (end - start) + " ms");
        
        System.out.println(messages);
    }
    
    public static void main(String[] args)
    {
        TestFramework.loadTest(TestSomething.class);// 测试驱动
    }
    
    public static class Message implements Serializable
    {
        private static final long serialVersionUID = -488428323878347643L;
        
        String node;
        
        String content;
        
        java.util.Date createTime;
        
        public String getNode()
        {
            return node;
        }
        
        public Message setNode(String node)
        {
            this.node = node;
            return this;
        }
        
        public String getContent()
        {
            return content;
        }
        
        public Message setContent(String content)
        {
            this.content = content;
            return this;
        }
        
        public java.util.Date getCreateTime()
        {
            return createTime;
        }
        
        public Message setCreateTime(java.util.Date createTime)
        {
            this.createTime = createTime;
            return this;
        }
        
        @Override
        public String toString()
        {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
        
    }
}
