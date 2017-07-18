/*
 * 文件名：URIEncoderDecoder.java
 * 版权：Copyright 2008-2009 Huawei Tech.Co.Ltd.All Rights Reserved.
 * 描述：URI编解码执行器。
 * 修改人：t00101719
 * 修改时间：2009-5-10
 * 修改内容：新增
 */
package com.lnwazg.kit.http.url;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.CharEncoding;

import com.lnwazg.kit.number.Hexadecimal;

/**
 * URI编解码执行器
 * 
 * @author t00101719
 * @version C02 2009-5-10
 * @since OpenEye WIDGET_SRV V100R001C02
 */
public final class URIEncoderDecoder
{
    private static final int MAX_INT = 0xFF;
    
    private static final int NUM_CHAR = 256;
    
    // escape
    private static List<Integer> dontNeedEncodingEscape = new ArrayList<Integer>();
    
    // encodeURI
    private static List<Integer> dontNeedEncodingURI = new ArrayList<Integer>();
    
    // encodeURIComponent
    private static List<Integer> dontNeedEncodingURIComponent = new ArrayList<Integer>();
    
    private URIEncoderDecoder()
    {
    }
    
    static
    {
        int i;
        
        for (i = 'a'; i <= 'z'; i++)
        {
            dontNeedEncodingEscape.add(Integer.valueOf(i));
            dontNeedEncodingURI.add(Integer.valueOf(i));
            dontNeedEncodingURIComponent.add(Integer.valueOf(i));
        }
        for (i = 'A'; i <= 'Z'; i++)
        {
            dontNeedEncodingEscape.add(Integer.valueOf(i));
            dontNeedEncodingURI.add(Integer.valueOf(i));
            dontNeedEncodingURIComponent.add(Integer.valueOf(i));
        }
        for (i = '0'; i <= '9'; i++)
        {
            dontNeedEncodingEscape.add(Integer.valueOf(i));
            dontNeedEncodingURI.add(Integer.valueOf(i));
            dontNeedEncodingURIComponent.add(Integer.valueOf(i));
        }
        
        dontNeedEncodingEscape.add(Integer.valueOf('*'));
        dontNeedEncodingEscape.add(Integer.valueOf('+'));
        dontNeedEncodingEscape.add(Integer.valueOf('-'));
        dontNeedEncodingEscape.add(Integer.valueOf('.'));
        dontNeedEncodingEscape.add(Integer.valueOf('/'));
        dontNeedEncodingEscape.add(Integer.valueOf('@'));
        dontNeedEncodingEscape.add(Integer.valueOf('_'));
        
        dontNeedEncodingURI.add(Integer.valueOf('!'));
        dontNeedEncodingURI.add(Integer.valueOf('#'));
        dontNeedEncodingURI.add(Integer.valueOf('$'));
        dontNeedEncodingURI.add(Integer.valueOf('&'));
        dontNeedEncodingURI.add(Integer.valueOf('\''));
        dontNeedEncodingURI.add(Integer.valueOf('('));
        dontNeedEncodingURI.add(Integer.valueOf(')'));
        dontNeedEncodingURI.add(Integer.valueOf('*'));
        dontNeedEncodingURI.add(Integer.valueOf('+'));
        dontNeedEncodingURI.add(Integer.valueOf(','));
        dontNeedEncodingURI.add(Integer.valueOf('-'));
        dontNeedEncodingURI.add(Integer.valueOf('.'));
        dontNeedEncodingURI.add(Integer.valueOf('/'));
        dontNeedEncodingURI.add(Integer.valueOf(':'));
        dontNeedEncodingURI.add(Integer.valueOf(';'));
        dontNeedEncodingURI.add(Integer.valueOf('='));
        dontNeedEncodingURI.add(Integer.valueOf('?'));
        dontNeedEncodingURI.add(Integer.valueOf('@'));
        dontNeedEncodingURI.add(Integer.valueOf('_'));
        dontNeedEncodingURI.add(Integer.valueOf('~'));
        
        dontNeedEncodingURIComponent.add(Integer.valueOf('!'));
        dontNeedEncodingURIComponent.add(Integer.valueOf('\''));
        dontNeedEncodingURIComponent.add(Integer.valueOf('('));
        dontNeedEncodingURIComponent.add(Integer.valueOf(')'));
        dontNeedEncodingURIComponent.add(Integer.valueOf('*'));
        dontNeedEncodingURIComponent.add(Integer.valueOf('-'));
        dontNeedEncodingURIComponent.add(Integer.valueOf('.'));
        dontNeedEncodingURIComponent.add(Integer.valueOf('_'));
        dontNeedEncodingURIComponent.add(Integer.valueOf('~'));
    }
    
    /**
     * 对URL进行编码
     * 
     * @param type
     *            编码方式
     * @param str
     *            待编码字符串
     * @return 编码结果
     */
    public static String encode(String type, String str)
    {
        if (str == null)
        {
            return null;
        }
        List<Integer> dontNeedEncoding = null;
        if (type.equals("escape"))
        {
            dontNeedEncoding = dontNeedEncodingEscape;
        }
        else if (type.equals("encodeURI"))
        {
            dontNeedEncoding = dontNeedEncodingURI;
        }
        else if (type.equals("encodeURIComponent"))
        {
            dontNeedEncoding = dontNeedEncodingURIComponent;
        }
        else
        {
            return null;
        }
        byte[] sequence = str.getBytes();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < sequence.length; i++)
        {
            byte b = sequence[i];
            int c = (int)MAX_INT & b;
            
            if (0 <= c && c < NUM_CHAR && dontNeedEncoding.contains(Integer.valueOf(c)))
            {
                buf.append((char)c);
            }
            else
            {
                buf.append('%');
                buf.append(Hexadecimal.valueOf(b).toUpperCase(Locale.getDefault()));
            }
        }
        return buf.toString();
    }
    
    /**
     * 对编码后的URL字符串进行解码
     * 
     * @param str
     *            编码后的URL字符串
     * @return 解码后的字符串
     */
    public static String decode(String str)
    {
        if (str == null)
        {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length = str.length();
        int index = 0;
        while (index < length)
        {
            char c = str.charAt(index);
            index++;
            if (c == '%' && index < length)
            {
                char c1 = str.charAt(index);
                index++;
                if (index >= length)
                {
                    baos.write((byte)c);
                    baos.write((byte)c1);
                }
                else if (c1 == '%')
                {
                    baos.write((byte)c1);
                }
                else
                {
                    char c2 = str.charAt(index);
                    index++;
                    try
                    {
                        baos.write((byte)Hexadecimal.octetValue(c1, c2));
                    }
                    catch (NumberFormatException excpt)
                    {
                        throw new IllegalArgumentException(excpt.toString(), excpt);
                    }
                }
            }
            else
            {
                baos.write((byte)c);
            }
        }
        try
        {
            return new String(baos.toByteArray(), CharEncoding.UTF_8);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
