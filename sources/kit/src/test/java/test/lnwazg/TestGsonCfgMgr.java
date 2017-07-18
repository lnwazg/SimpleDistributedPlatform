package test.lnwazg;

import java.util.Arrays;

import com.lnwazg.kit.json.GsonCfgMgr;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.TestCase;

public class TestGsonCfgMgr
{
    @TestCase
    void test1()
    {
        GsonCfgMgr.USER_DIR = "c:/abc";
        System.out.println(GsonCfgMgr.readObject(User.class));
        //        System.out.println(GsonCfgMgr.readObject(User.class));
        System.out.println(GsonCfgMgr.readObjectProp(User.class, "name"));
        System.out.println(GsonCfgMgr.readObjectProp(User.class, "age"));
        GsonCfgMgr.saveObjectProp(User.class, "name", "Linan");
        GsonCfgMgr.saveObjectProp(User.class, "age", 28);
        GsonCfgMgr.saveObjectProp(User.class, "age", "29");
        System.out.println(GsonCfgMgr.readObject(User.class));
        System.out.println(GsonCfgMgr.readObjectProp(User.class, "hobbies"));
        System.out.println(GsonCfgMgr.readObjectProp(User.class, "hobbies[2]"));
        GsonCfgMgr.saveObjectProp(User.class, "hobbies", Arrays.asList("wwww", "aaa", "bbb", "cccc"));
        System.out.println(GsonCfgMgr.readObjectProp(User.class, "hobbies"));
        //        List<String> hobbies = Arrays.asList("Pingpong", "hiking", "swimming", "wfsdf");
        //                GsonCfgMgr.saveObjectProp(User.class, "hobbies", hobbies);
        //        System.out.println(GsonCfgMgr.getObjectProp(User.class, "hobbies"));
        //        System.out.println(GsonCfgMgr.readObjectProp(User.class, "hobbies"));
        //        System.out.println(GsonCfgMgr.readObject(User.class));
        //                GsonCfgMgr.saveObjectProp(User.class, "hobbies[1]", "aaaaa");
        //                System.out.println(GsonCfgMgr.readObjectProp(User.class,"hobbies[1]"));
    }
    
    public static void main(String[] args)
    {
        TF.l(TestGsonCfgMgr.class);
    }
}
