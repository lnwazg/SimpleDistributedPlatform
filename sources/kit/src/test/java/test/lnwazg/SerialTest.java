package test.lnwazg;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SerializationUtils;

import com.lnwazg.kit.serialization.protostuff.ProtostuffUtil;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.BenchmarkLow;
import com.lnwazg.kit.testframework.anno.TestCase;

public class SerialTest
{
    static Person person = new Person("张三", "14", "男", "江宁区竹山路江宁区竹山路江宁区竹山路江宁区竹山路江宁区竹山路江宁区竹山路江宁区竹山路江宁区竹山路江宁区竹山路江宁区竹山路江宁区竹山路江宁区竹山路");
    
    //    static Person person = new Person("aaaa", 14, "m", "bbbbbbbbbb");
    static int runTimes = 5000000;
    
    //    @TestCase
    void testOriginal()
    {
        for (int i = 0; i < runTimes; i++)
        {
            //            byte[] keyBytes = SerializationUtils.serialize(person);
            //            Person person2 = SerializationUtils.deserialize(keyBytes);
            //            System.out.println(person2);
        }
    }
    
    //    @TestCase
    void testNew()
    {
        for (int i = 0; i < runTimes; i++)
        {
            byte[] keyBytes = ProtostuffUtil.serialize(person);
            Person person2 = ProtostuffUtil.deserialize(keyBytes, Person.class);
            //            System.out.println(person2);
        }
    }
    
    @BenchmarkLow
    @TestCase
    void test3()
    {
        System.out.println(SerializationUtils.serialize(person).length);
        System.out.println(ProtostuffUtil.serialize(person).length);
    }
    
    //    @TestCase
    void testNew2()
    {
        for (int i = 0; i < runTimes; i++)
        {
            byte[] keyBytes = ProtostuffUtil.serialize(person);
            Person person2 = ProtostuffUtil.deserialize(keyBytes, Person.class);
            System.out.println(person2);
            try
            {
                //睡眠的这1ms真的至关重要！因为它可以完美解决System.out来不及输出太多文字，从而导致的输出的控制台中文乱码的问题！
                //所以，控制台中文乱码，就是因为输出太密集，导致控制台来不及处理从而导致的！
                TimeUnit.MILLISECONDS.sleep(1);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            //            person2 = null;
            //            try
            //            {
            //                FileUtils.writeStringToFile(new File("c:\\3.txt"), person2.toString() + "\n", true);
            //            }
            //            catch (IOException e)
            //            {
            //                e.printStackTrace();
            //            }
        }
    }
    
    public static void main(String[] args)
    {
        TF.l(SerialTest.class);
    }
    
    public static class Person implements Serializable
    {
        private static final long serialVersionUID = 1L;
        
        String name;
        
        String age;
        
        String sex;
        
        String address;
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public String getAge()
        {
            return age;
        }
        
        public void setAge(String age)
        {
            this.age = age;
        }
        
        public String getSex()
        {
            return sex;
        }
        
        public void setSex(String sex)
        {
            this.sex = sex;
        }
        
        public String getAddress()
        {
            return address;
        }
        
        public void setAddress(String address)
        {
            this.address = address;
        }
        
        public Person()
        {
        }
        
        public Person(String name, String age, String sex, String address)
        {
            super();
            this.name = name;
            this.age = age;
            this.sex = sex;
            this.address = address;
        }
        
        @Override
        public String toString()
        {
            return "Person [name=" + name + ", age=" + age + ", sex=" + sex + ", address=" + address + "]";
        }
    }
}
