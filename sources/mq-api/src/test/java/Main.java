import java.sql.SQLException;

import org.apache.commons.lang3.RandomStringUtils;

import com.lnwazg.dbkit.jdbc.MyJdbc;
import com.lnwazg.dbkit.utils.DbKit;
import com.lnwazg.kit.singleton.BeanMgr;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.TestCase;
import com.lnwazg.mq.entity.Message;

public class Main
{
    @TestCase
    void test1()
    {
        MyJdbc myjdbc = DbKit.getJdbc("jdbc:sqlite://C:/Windows/LNWAZG/db49ec2c-08d4-4482-8c7b-f7c6a07b2a83/myMQ.db", "", "");
        BeanMgr.put(MyJdbc.class, myjdbc);
        for (int i = 0; i < 100000; i++)
        {
            try
            {
                myjdbc.insert(new Message().setNode("hhh").setContent(RandomStringUtils.random(100)));
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args)
    {
        TF.l(Main.class);
        
    }
}
