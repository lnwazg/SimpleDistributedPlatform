package com.lnwazg.kit.json;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonSyntaxException;
import com.lnwazg.kit.gson.GsonKit;
import com.lnwazg.kit.security.SecurityUtils;

/**
 * 配置管理器<br>
 * 方便地在对象、配置文件两者中转换
 * @author Administrator
 * @version 2016年4月17日
 */
public class GsonCfgMgr
{
    public static final String UTF8_ENCODING = "UTF-8";
    
    public static String AES_KEY = "whosyourdaddy!#@";
    
    public static String USER_DIR = "";
    
    /**
     * 检查用户文件夹是否正确初始化了
     * @author Administrator
     * @return
     */
    private static boolean checkUserDir()
    {
        if (StringUtils.isBlank(USER_DIR))
        {
            System.err.println("GsonCfgMgr.USER_DIR 尚未初始化，无法读写配置文件！");
            return false;
        }
        return true;
    }
    
    /**
     * 写入配置信息
     * 开箱即用的典范！强力的基础设施！
     * @param object
     */
    public static void writeObject(Object object)
    {
        if (!checkUserDir())
        {
            return;
        }
        File dir = new File(USER_DIR);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File userFile = new File(dir.getPath() + File.separator + object.getClass().getCanonicalName());
        //输出的时候，进行格式美化
        try
        {
            FileUtils.writeStringToFile(userFile, GsonKit.prettyGson.toJson(object), UTF8_ENCODING);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void saveObject(Object object)
    {
        writeObject(object);
    }
    
    public static <T> void writeObjectProp(Class<T> clazz, String name, Object value)
    {
        try
        {
            T t = readObject(clazz);
            if (t == null)
            {
                t = clazz.newInstance();
            }
            //            PropertyUtils.setProperty(t, name, value);//会严格检查类型，如果类型不匹配，则失败
            BeanUtils.setProperty(t, name, value);//会自动作类型转换，使用起来更灵活
            writeObject(t);
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }
    
    public static <T> void saveObjectProp(Class<T> clazz, String name, Object value)
    {
        writeObjectProp(clazz, name, value);
    }
    
    public static <T> void writeObjectPropAES(Class<T> clazz, String name, Object value)
    {
        try
        {
            T t = readObjectAES(clazz);
            if (t == null)
            {
                t = clazz.newInstance();
            }
            //            PropertyUtils.setProperty(t, name, value);//会严格检查类型，如果类型不匹配，则失败
            BeanUtils.setProperty(t, name, value);//会自动作类型转换，使用起来更灵活
            writeObjectAES(t);
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }
    
    public static <T> void saveObjectPropAES(Class<T> clazz, String name, Object value)
    {
        writeObjectPropAES(clazz, name, value);
    }
    
    public static void writeObjectAES(Object object)
    {
        if (!checkUserDir())
        {
            return;
        }
        File dir = new File(USER_DIR);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File userFile = new File(dir.getPath() + File.separator + object.getClass().getCanonicalName());
        //此处输出为一个AES加密后的字符串，因此gson自身采用默认的紧凑输出，是没有问题的！
        try
        {
            FileUtils.writeStringToFile(userFile, SecurityUtils.aesEncode(GsonKit.gson.toJson(object), AES_KEY), UTF8_ENCODING);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void saveObjectAES(Object object)
    {
        writeObjectAES(object);
    }
    
    /**
     * 从配置信息中读取到对象
     * 开箱即用的典范！强力的基础设施！
     * @param <T>
     * @param class1
     * @return
     */
    public static <T> T readObject(Class<T> clazz)
    {
        if (!checkUserDir())
        {
            return null;
        }
        File dir = new File(USER_DIR);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File toReadFile = new File(dir.getPath() + File.separator + clazz.getCanonicalName());
        T instance = null;
        if (toReadFile.exists())
        {
            try
            {
                instance = GsonKit.gson.fromJson(FileUtils.readFileToString(toReadFile, UTF8_ENCODING), clazz);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("文件读取异常！");
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();
                //此处还原成失败了，则应该将该配置文件删除掉！
                System.out.println("配置文件已损坏，重建配置文件！");
                delObject(clazz);
            }
        }
        return instance;
    }
    
    public static <T> T getObject(Class<T> clazz)
    {
        return readObject(clazz);
    }
    
    public static <T> T readObjectAES(Class<T> clazz)
    {
        if (!checkUserDir())
        {
            return null;
        }
        File dir = new File(USER_DIR);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File toReadFile = new File(dir.getPath() + File.separator + clazz.getCanonicalName());
        T instance = null;
        if (toReadFile.exists())
        {
            try
            {
                instance = GsonKit.gson.fromJson(SecurityUtils.aesDecode(FileUtils.readFileToString(toReadFile, UTF8_ENCODING), AES_KEY), clazz);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.out.println("文件读取异常！");
            }
            catch (JsonSyntaxException e)
            {
                e.printStackTrace();
                //此处还原成失败了，则应该将该配置文件删除掉！
                System.out.println("配置文件已损坏，重建配置文件！");
                delObject(clazz);
            }
        }
        return instance;
    }
    
    public static <T> T getObjectAES(Class<T> clazz)
    {
        return readObjectAES(clazz);
    }
    
    public static <T> Object readObjectProp(Class<T> clazz, String name)
    {
        try
        {
            T t = readObject(clazz);
            if (t != null)
            {
                //                return BeanUtils.getProperty(t, name);//如果是一个list，则自动返回list中的第一个元素，而不会返回完整的list对象
                return PropertyUtils.getProperty(t, name);//会返回完整的对象
            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static <T> Object getObjectProp(Class<T> clazz, String name)
    {
        return readObjectProp(clazz, name);
    }
    
    public static <T> Object readObjectPropAES(Class<T> clazz, String name)
    {
        try
        {
            T t = readObjectAES(clazz);
            if (t != null)
            {
                //                return BeanUtils.getProperty(t, name);//如果是一个list，则自动返回list中的第一个元素，而不会返回完整的list对象
                return PropertyUtils.getProperty(t, name);//会返回完整的对象
            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static <T> Object getObjectPropAES(Class<T> clazz, String name)
    {
        return readObjectPropAES(clazz, name);
    }
    
    /**
     * 删除配置文件
     * @author nan.li
     * @param clazz
     */
    public static <T> void delObject(Class<T> clazz)
    {
        if (!checkUserDir())
        {
            return;
        }
        File dir = new File(USER_DIR);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File toDelFile = new File(dir.getPath() + File.separator + clazz.getCanonicalName());
        try
        {
            if (toDelFile.exists())
            {
                toDelFile.delete();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static <T> void delObjectAES(Class<T> clazz)
    {
        delObject(clazz);
    }
}
