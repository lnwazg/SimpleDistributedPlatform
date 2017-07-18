/*
 * 文件名：Hexadecimal.java
 * 版权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描述：
 * 修改人：t00101719
 * 修改时间：2009-5-10
 * 修改内容：新增
 */
package com.lnwazg.kit.number;

/**
 * 数值的十六进制转换器
 * 
 * @author t00101719
 * @version C02 2009-5-10
 * @since OpenEye WIDGET_SRV V100R001C02
 */
public class Hexadecimal
{
    private String hex = null;
    
    private int num = 0;
    
    /**
     * Constructs a hexadecimal number with a byte.
     * 
     * @param num
     *            a byte
     */
    public Hexadecimal(byte num)
    {
        this.hex = valueOf(num);
        this.num = (int)num;
    }
    
    /**
     * Constructs a hexadecimal number with a integer.
     * 
     * @param num
     *            a integer
     */
    public Hexadecimal(int num)
    {
        this.hex = valueOf(num);
        this.num = (int)num;
    }
    
    /**
     * Constructs a hexadecimal number with a short integer.
     * 
     * @param num
     *            a short integer
     */
    public Hexadecimal(short num)
    {
        this.hex = valueOf(num);
        this.num = (int)num;
    }
    
    /**
     * Gets a byte value.
     * 
     * @return a byte of the hexadecimal number
     */
    public byte byteValue()
    {
        if (num > 255 || num < 0)
        {
            throw new NumberFormatException("Out of range for byte.");
        }
        return (byte)num;
    }
    
    /**
     * Gets a string in hexadecimal notation.
     * 
     * @return string in hexadecimal notation of the number
     */
    public String hexadecimalValue()
    {
        return hex;
    }
    
    /**
     * Converts a pair of characters as an octet in hexadecimal notation into
     * integer.
     * 
     * @param c0
     *            higher character of given octet in hexadecimal notation
     * @param c1
     *            lower character of given octet in hexadecimal notation
     * @return a integer value of the octet
     */
    public static int octetValue(char c0, char c1)
    {
        int n0 = Character.digit(c0, 16);
        
        if (n0 < 0)
        {
            throw new NumberFormatException(c0 + " is not a hexadecimal character.");
        }
        int n1 = Character.digit(c1, 16);
        
        if (n1 < 0)
        {
            throw new NumberFormatException(c1 + " is not a hexadecimal character.");
        }
        return (n0 << 4) + n1;
    }
    
    /**
     * Converts a string in hexadecimal notation into byte.
     * 
     * @param hex
     *            string in hexadecimal notation
     * @return a byte (1bytes)
     */
    public static byte parseByte(String hex)
    {
        if (hex == null)
        {
            throw new IllegalArgumentException("Null string in hexadecimal notation.");
        }
        if (hex.equals(""))
        {
            return 0;
        }
        Integer num = Integer.valueOf("0x" + hex, 16);
        int n = num.intValue();
        
        if (n > 255 || n < 0)
        {
            throw new NumberFormatException("Out of range for byte.");
        }
        return num.byteValue();
    }
    
    /**
     * Converts a string in hexadecimal notation into byte sequence.
     * 
     * @param str
     *            a string in hexadecimal notation
     * @return byte sequence
     */
    public static byte[] parseSeq(String str)
    {
        if (str == null || str.equals(""))
        {
            return null;
        }
        int len = str.length();
        
        if (len % 2 != 0)
        {
            throw new NumberFormatException("Illegal length of string in hexadecimal notation.");
        }
        int numOfOctets = len / 2;
        byte[] seq = new byte[numOfOctets];
        
        for (int i = 0; i < numOfOctets; i++)
        {
            String hex = str.substring(i * 2, i * 2 + 2);
            
            seq[i] = parseByte(hex);
        }
        return seq;
    }
    
    /**
     * Converts a string in hexadecimal notation into short integer.
     * 
     * @param hex
     *            string in hexadecimal notation
     * @return a short integer (2bytes)
     */
    public static short parseShort(String hex)
    {
        if (hex == null)
        {
            throw new IllegalArgumentException("Null string in hexadecimal notation.");
        }
        if (hex.equals(""))
        {
            return 0;
        }
        Integer num = Integer.valueOf("0x" + hex, 16);
        int n = num.intValue();
        
        if (n > 65535 || n < 0)
        {
            throw new NumberFormatException("Out of range for short integer.");
        }
        return num.shortValue();
    }
    
    /**
     * Gets a short integer value.
     * 
     * @return a short integer of the hexadecimal number
     */
    public short shortValue()
    {
        if (num > 65535 || num < 0)
        {
            throw new NumberFormatException("Out of range for short integer.");
        }
        return (short)num;
    }
    
    /**
     * Converts a byte sequence into its hexadecimal notation.
     * 
     * @param seq
     *            a byte sequence
     * @return hexadecimal notation of the byte sequence
     */
    public static String valueOf(byte[] seq)
    {
        if (seq == null)
        {
            return null;
        }
        StringBuffer buff = new StringBuffer();
        
        for (int i = 0; i < seq.length; i++)
        {
            buff.append(valueOf(seq[i], true));
        }
        return buff.toString();
    }
    
    /**
     * Converts a byte sequence into its hexadecimal notation.
     * 
     * @param seq
     *            a byte sequence
     * @param separator
     *            separator between bytes
     * @return hexadecimal notation of the byte sequence
     */
    public static String valueOf(byte[] seq, char separator)
    {
        if (seq == null)
        {
            return null;
        }
        StringBuffer buff = new StringBuffer();
        
        for (int i = 0; i < seq.length; i++)
        {
            if (i > 0)
            {
                buff.append(separator);
            }
            buff.append(valueOf(seq[i], true));
        }
        return buff.toString();
    }
    
    /**
     * Converts a byte into its hexadecimal notation.
     * 
     * @param num
     *            a byte (1bytes)
     * @return hexadecimal notation of the byte
     */
    public static String valueOf(byte num)
    {
        return valueOf(num, true);
    }
    
    /**
     * Converts a byte into its hexadecimal notation.
     * 
     * @param num
     *            a byte (1bytes)
     * @param padding
     *            fit the length to 2 by filling with '0' when padding is true
     * @return hexadecimal notation of the byte
     */
    public static String valueOf(byte num, boolean padding)
    {
        String hex = Integer.toHexString((int)num);
        
        if (padding)
        {
            hex = "00" + hex;
            int len = hex.length();
            
            hex = hex.substring(len - 2, len);
        }
        return hex;
    }
    
    /**
     * Converts a integer into its hexadecimal notation.
     * 
     * @param num
     *            a integer (4bytes)
     * @return hexadecimal notation of the integer
     */
    public static String valueOf(int num)
    {
        return valueOf(num, true);
    }
    
    /**
     * Converts a integer into its hexadecimal notation.
     * 
     * @param num
     *            a integer (4bytes)
     * @param padding
     *            fit the length to 8 by filling with '0' when padding is true
     * @return hexadecimal notation of the integer
     */
    public static String valueOf(int num, boolean padding)
    {
        String hex = Integer.toHexString(num);
        
        if (padding)
        {
            hex = "00000000" + hex;
            int len = hex.length();
            
            hex = hex.substring(len - 8, len);
        }
        return hex;
    }
    
    /**
     * Converts a long integer into its hexadecimal notation.
     * 
     * @param num
     *            a long integer (8bytes)
     * @return hexadecimal notation of the long integer
     */
    public static String valueOf(long num)
    {
        return valueOf(num, true);
    }
    
    /**
     * Converts a long integer into its hexadecimal notation.
     * 
     * @param num
     *            a long integer (8bytes)
     * @param padding
     *            fit the length to 16 by filling with '0' when padding is true
     * @return hexadecimal notation of the long integer
     */
    public static String valueOf(long num, boolean padding)
    {
        String hex = Long.toString(num, 16);
        
        if (padding)
        {
            hex = "0000000000000000" + hex;
            int len = hex.length();
            
            hex = hex.substring(len - 16, len);
        }
        return hex;
    }
    
    /**
     * Converts a short integer into its hexadecimal notation.
     * 
     * @param num
     *            a short integer (2bytes)
     * @return hexadecimal notation of the short integer
     */
    public static String valueOf(short num)
    {
        return valueOf(num, true);
    }
    
    /**
     * Converts a short integer into its hexadecimal notation.
     * 
     * @param num
     *            a short integer (2bytes)
     * @param padding
     *            fit the length to 8 by filling with '0' when padding is true
     * @return hexadecimal notation of the short integer
     */
    public static String valueOf(short num, boolean padding)
    {
        String hex = Integer.toHexString((int)num);
        
        if (padding)
        {
            hex = "0000" + hex;
            int len = hex.length();
            
            hex = hex.substring(len - 4, len);
        }
        return hex;
    }
}
