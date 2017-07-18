package com.lnwazg.kit.security;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import com.lnwazg.kit.io.StreamUtils;
import com.lnwazg.kit.log.Logs;

/**
 * 密码箱
 * @author nan.li
 * @version 2016年10月17日
 */
public class PasswordKit
{
    /**
     * 证书名称
     */
    private static final String CERTIFICATE_CRT_PATH = "certificate.crt";
    
    /**
     * 默认的传输所使用的加解密密码
     */
    public static final String DEFAULT_TRANSFER_PASSWORD = "&^*669$%^#$^776%";//长度必须是16位
    
    /**
     * 安全性大幅增强<br>
     * 此为传输秘钥
     */
    public static String PASSWORD = DEFAULT_TRANSFER_PASSWORD;
    
    static
    {
        //启动的时候即加载
        //如果秘钥文件存在，则读取它的内容。将其和默认密码混合之后，md5，结果总计32位，取出前16位作为秘钥
        //否则，采用默认的加密信息
        String content = readSecurityKey();
        if (StringUtils.isNotEmpty(content))
        {
            Logs.i(String.format("读取到证书信息，更新通讯秘钥..."));
            //长度必须是16位
            PASSWORD = SecurityUtils.md5Encode(DEFAULT_TRANSFER_PASSWORD + content).substring(0, 23);//混淆
            PASSWORD = SecurityUtils.md5Encode(PASSWORD).substring(0, 16);
            //AES algorithm allows 128, 192 or 256 bit key length. which is 16, 24 or 32 byte. 
            //your keys length should be 16 , 24 or 32 bytes.
            //默认 Java 中仅支持 128 位密钥，当使用 256 位密钥的时候，会报告密钥长度错误
            // 你需要下载一个支持更长密钥的包。这个包叫做 Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6
            // 下载之后，解压后，可以看到其中包含两个包：
            // local_policy.jar
            // US_export_policy.jar
        }
        Logs.i(String.format("当前的通讯密码为：%s", PASSWORD));
    }
    
    /**
     * 读取配置文件中的证书信息
     * @author nan.li
     * @return
     */
    private static String readSecurityKey()
    {
        //去配置文件目录去读取这个秘钥文件
        InputStream inputStream = PasswordKit.class.getClassLoader().getResourceAsStream(CERTIFICATE_CRT_PATH);
        if (inputStream != null)
        {
            try
            {
                return IOUtils.toString(inputStream, CharEncoding.UTF_8);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                StreamUtils.close(inputStream);
            }
        }
        return null;
    }
    
    public static void main(String[] args)
    {
        System.out.println(SecurityUtils.md5Encode("1"));
        System.out.println(SecurityUtils.md5Encode(DEFAULT_TRANSFER_PASSWORD));
        System.out.println(SecurityUtils.md5Encode(DEFAULT_TRANSFER_PASSWORD + DEFAULT_TRANSFER_PASSWORD));
        System.out.println(SecurityUtils.md5Encode(DEFAULT_TRANSFER_PASSWORD + DEFAULT_TRANSFER_PASSWORD + DEFAULT_TRANSFER_PASSWORD));
    }
}
