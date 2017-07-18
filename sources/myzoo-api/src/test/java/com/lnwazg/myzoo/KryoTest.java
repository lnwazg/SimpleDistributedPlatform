package com.lnwazg.myzoo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.lnwazg.myzoo.bean.Msg;

/**
 * @author zhangwei
 */
public class KryoTest
{
    /**
     * @param args
     */
    public static void main(String[] args)
        throws FileNotFoundException
    {
        Kryo kryo = new Kryo();
        Output output = new Output(new FileOutputStream("file.bin"));
        
        Msg people = new Msg();
//        people.setName("zhangsan");
        kryo.writeObject(output, people);
        output.close();
        
        Input input = new Input(new FileInputStream("file.bin"));
        Msg people1 = kryo.readObject(input, Msg.class);
        input.close();
        
//        System.out.println(people1.getName());
    }
    
}
