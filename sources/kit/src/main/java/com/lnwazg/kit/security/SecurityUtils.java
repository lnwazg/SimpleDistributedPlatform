package com.lnwazg.kit.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.CharEncoding;

import com.lnwazg.kit.log.Logs;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 安全工具
 * @author nan.li
 * @version 2014-11-12
 */
@SuppressWarnings("restriction")
public class SecurityUtils
{
    public static final String UTF_8 = "UTF-8";
    
    /** 
     * md5加密     返回32位加密的结果
     * @param src
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String md5Encode(String src)
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes(UTF_8));
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        byte b[] = md.digest();
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++)
        {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }
    
    /** 
     * base64加密
     * @param bytes
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String base64Encode(byte[] bytes)
    {
        String originalEncoded = new BASE64Encoder().encode(bytes);
        String replaced = originalEncoded.replace("\r", "").replace("\n", "");//解决unix和windows下换行符不一致的问题
        return replaced;
    }
    
    public static String base64Encode(String param)
    {
        try
        {
            return base64Encode(param.getBytes(CharEncoding.UTF_8));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /** 
     * base64解密
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static byte[] base64DecodeToBytes(String param)
    {
        try
        {
            return new BASE64Decoder().decodeBuffer(param);
        }
        catch (IOException e)
        {
            System.err.println("decode failed!");
            e.printStackTrace();
            return null;
        }
    }
    
    public static String base64DecodeToStr(String param)
    {
        byte[] bs = base64DecodeToBytes(param);
        if (bs != null && bs.length > 0)
        {
            try
            {
                return new String(bs, CharEncoding.UTF_8);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /** 
     * AES加密
     * @param src  明文
     * @param key  密钥
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String aesEncode(String src, String password)
    {
        if (!checkDesPasswordLength(password))
        {
            return null;
        }
        byte[] encrypted;
        try
        {
            byte[] raw = password.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(src.getBytes("UTF-8"));
            return base64Encode(encrypted);
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    public static byte[] aesEncode(byte[] src, String password)
    {
        if (!checkDesPasswordLength(password))
        {
            return null;
        }
        byte[] encrypted;
        try
        {
            byte[] raw = password.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(src);
            return encrypted;
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /** 
     * AES解密
     * @param enString  密文
     * @param key  密钥
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static String aesDecode(String enString, String password)
    {
        if (!checkDesPasswordLength(password))
        {
            return null;
        }
        try
        {
            byte[] raw = password.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            // 将结果进行base64解码,获得二进制数组
            byte[] encrypted1 = base64DecodeToBytes(enString);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "UTF-8");
            return originalString;
        }
        catch (Exception e)
        {
            System.err.println("解密失败！");
            e.printStackTrace();
        }
        return "";
    }
    
    public static byte[] aesDecode(byte[] encodedBytes, String password)
    {
        if (!checkDesPasswordLength(password))
        {
            return null;
        }
        try
        {
            byte[] raw = password.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            // 将结果进行base64解码,获得二进制数组
            byte[] original = cipher.doFinal(encodedBytes);
            return original;
        }
        catch (Exception e)
        {
            System.err.println("解密失败！");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * DES加密算法<br>
     * password的长度必须是8的倍数
     * @author nan.li
     * @param srcBytes
     * @param password
     * @return
     */
    public static byte[] desEncode(byte[] srcBytes, String password)
    {
        if (!checkDesPasswordLength(password))
        {
            return null;
        }
        try
        {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes(UTF_8));
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(srcBytes);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * DES解密算法<br>
     * password的长度必须是8的倍数
     * @author nan.li
     * @param encodedBytes
     * @param password
     * @return
     * @throws Exception
     */
    public static byte[] desDecode(byte[] encodedBytes, String password)
        throws Exception
    {
        if (!checkDesPasswordLength(password))
        {
            return null;
        }
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes(UTF_8));
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(encodedBytes);
    }
    
    /**
     * 检查秘钥长度是否合法
     * @author nan.li
     * @param password
     * @return
     */
    private static boolean checkDesPasswordLength(String password)
    {
        if (isEmpty(password))
        {
            System.err.println("password不能为空！");
            return false;
        }
        int length = password.length();
        if (length != 16 && length != 24 && length != 32)
        {
            System.err.println("password的长度必须是16、24或32！当前的长度为：" + length);
            return false;
        }
        if (length == 24 || length == 32)
        {
            //AES algorithm allows 128, 192 or 256 bit key length. which is 16, 24 or 32 byte. 
            //your keys length should be 16 , 24 or 32 bytes.
            //默认 Java 中仅支持 128 位密钥，当使用 256 位密钥的时候，会报告密钥长度错误
            // 你需要下载一个支持更长密钥的包。这个包叫做 Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6
            // 下载之后，解压后，可以看到其中包含两个包：
            // local_policy.jar
            // US_export_policy.jar
            Logs.w("警告：您正在使用 192bit or 256bit的高阶密钥，请务必保证系统JRE环境中引入了JCE包，否则加密将出错！");
        }
        return true;
    }
    
    public static boolean isEmpty(final CharSequence cs)
    {
        return cs == null || cs.length() == 0;
    }
    
    public static boolean isNotEmpty(final CharSequence cs)
    {
        return !isEmpty(cs);
    }
    
    public static void main(String[] args)
        throws Exception
    {
        //        String originalStr = "这个是原有的报文  沙发撒旦法  sdfsaf  撒旦法萨芬是方式发撒地方撒地方 sadsafsadfsa @234234%&%&^%&& 原有的报文结束";
        //        String password = "21gty75^";
        //        byte[] encoded = desEncode(originalStr.getBytes("UTF-8"), password);
        //        System.out.println(new String(encoded, "UTF-8"));
        //        
        //        byte[] decoded = desDecode(encoded, password);
        //        System.out.println(new String(decoded, "UTF-8"));
        //        System.out.println(base64Encode("江苏电力掌上生活".getBytes(CharEncoding.UTF_8)));
        System.out.println(base64Encode("江苏电力掌上生活江苏电力掌上生活江苏电力掌上生活江苏电力掌上生活"));
        System.out.println(base64DecodeToStr("5rGf6IuP55S15Yqb5o6M5LiK55Sf5rS75rGf6IuP55S15Yqb5o6M5LiK55Sf5rS75rGf6IuP55S15Yqb5o6M5LiK55Sf5rS75rGf6IuP55S15Yqb5o6M5LiK55Sf5rS7"));
        //        System.out.println(base64DecodeToBytes("5rGf6IuP55S15Yqb5o6M5LiK55Sf5rS7"));
        //        System.out.println(new String(decoded, "UTF-8"));
    }
    
}
