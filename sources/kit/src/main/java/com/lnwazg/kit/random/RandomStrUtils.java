package com.lnwazg.kit.random;

import java.util.Random;

/**
 * 随机字符串生成器
 * 
 * @author g00106664
 * @version C03 2009-7-23
 * @since OpenEye WIDGET_PLT V100R002C03
 */
public final class RandomStrUtils
{
    /**
     * 数字表
     */
    private static final String DIGITS = "0123456789";
    
    /**
     * 字母表
     */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    
    /**
     * 数字与字母表的组合
     */
    private static final String DIGITS_AND_ALPHABET = DIGITS + ALPHABET;
    
    /**
     * 随机生成字符串(生成的字符串长度为codeCount，从baseString中取得字符作为备选字符)
     * 
     * @param codeCount
     *            字符串个数
     * @param baseString
     *            备选字符组成的字符串
     * @return 随机生成的字符串
     */
    public static String generateCodeFromString(int codeCount, String baseString)
    {
        if (null == baseString)
        {
            return null;
        }
        
        //备选字符串的长度
        int baseLength = baseString.length();
        
        StringBuilder result = new StringBuilder();
        //备选字符数组
        char[] chars = baseString.toCharArray();
        
        Random random = new Random();
        
        int number;
        for (int i = 0; i < codeCount; i++)
        {
            //随机取得备选字符串的第n位
            number = random.nextInt(baseLength);
            //将该位的字符追加到结果字符中
            result.append(chars[number]);
        }
        
        return result.toString();
    }
    
    /**
     * 随机生成n位字符串，其中基本字符为数字和大小写字母的组合
     * 当codeCount为4时,通常应用于自动生成验证码
     * 当codeCount为512时,通常用于自动生成超长密钥
     * 
     * @return 随机生成的字符串
     */
    public static String generateRandomString(int codeCount)
    {
        return RandomStrUtils.generateCodeFromString(codeCount, DIGITS_AND_ALPHABET);
    }
    
    /**
     * 生成随机的数字验证码
     * @author nan.li
     * @param codeCount
     * @return
     */
    public static String generateRandomDigits(int codeCount)
    {
        return RandomStrUtils.generateCodeFromString(codeCount, DIGITS);
    }
    
    /**
     * 生成一个30位的随机字符串
     * @author nan.li
     * @return
     */
    public static String generateRandomString()
    {
        return generateRandomString(30);
    }
    
    /** 
     * 简单的测试
     * @param args
     * @see [类、类#方法、类#成员]
     */
    public static void main(String[] args)
    {
        System.out.println(RandomStrUtils.generateRandomString(4));//生成大小写字母数字混合的验证码
        System.out.println(RandomStrUtils.generateRandomString(512));//生成超长密钥
        System.out.println(RandomStrUtils.generateRandomString(128));//生成超长密钥
    }
    
}
