package test.lnwazg;

import com.lnwazg.kit.cache.FileCache;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.TestCase;

public class CacheTest
{
    static FileCache bucketCache = new FileCache("myredis");
    
    @TestCase
    void test1()
    {
        //       bucketCache.put("aaa", "hhhhhhhhhhhhhhhhhhhh");
        //       bucketCache.put("bbb", "hhhhhhhhhhhhhhhhhhhh");
        //       bucketCache.put("ccc", "hhhhhhhhhhhhhhhhhhhh");
        bucketCache.put("ddd", "ttt");
        System.out.println(bucketCache.get("ddd"));
        System.exit(0);
        
    }
    
    public static void main(String[] args)
    {
        TF.l(CacheTest.class);
    }
}
