package test.lnwazg;

import com.lnwazg.kit.http.server.JettyServerKit;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.TestCase;

public class JettyTest
{
    @TestCase
    void testServer()
    {
        JettyServerKit.startLocalResourceServer(9999, "/images", "C:\\Windows");
    }
    
    public static void main(String[] args)
    {
        TF.l(JettyTest.class);
    }
}
