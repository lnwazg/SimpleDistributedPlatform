package com.lnwazg.kit.str;

import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 * String工具包
 * @author  lnwazg
 * @version  [版本号, 2011-12-17]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class StringKit
{
    /**
     * 字母表
     */
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    
    /** 
     * 从srcStr的头开始向后搜索，截取遇到的第一个beginStr和endStr之间的字符串返回
     * 若无法成功截取，则返回一个随机的long型数字字符串
     * 适用于抓取标题内容
     * @param srcStr
     * @param beginStr
     * @param endStr
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getNeighbouringStrFromStartBetweenAndReturnRandonIfNull(String srcStr, String beginStr, String endStr)
    {
        String result = "";
        if (StringUtils.isEmpty(srcStr) || StringUtils.isEmpty(beginStr) || StringUtils.isEmpty(endStr))
        {
            result = String.valueOf(new Random().nextLong());
        }
        else
        {
            int start = StringUtils.indexOf(srcStr, beginStr) + beginStr.length();
            int end = start + StringUtils.substring(srcStr, start).indexOf(endStr);
            if (start == -1 || end == -1)
            {
                System.err.println("getNeighbouringStrFromStartBetweenAndReturnRandonIfNull  start or end is -1!");
                result = String.valueOf(new Random().nextLong());
            }
            else
            {
                result = StringUtils.substring(srcStr, start, end);
            }
        }
        return result;
    }
    
    /**
     * 二元组的第一个字符串： 
     * 从srcStr的头开始向后搜索，截取遇到的第一个beginStr和endStr之间的字符串(包括beginStr和endStr自身)返回
     * 二元组的第二个字符串：
     * srcStr从endStr之末尾向后截取获得的字符串
     * 
     * 适用于抓取资源链接
     * @param srcStr
     * @param beginStr
     * @param endStr
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static TwoTuples getNeighbouringStrFromStartBetweenAndReturnElseStr(String srcStr, String beginStr, String endStr)
    {
        TwoTuples twoTuples = new TwoTuples();
        String firstString = "";
        String secondString = "";
        if (StringUtils.isEmpty(srcStr) || StringUtils.isEmpty(beginStr) || StringUtils.isEmpty(endStr))
        {
            //do nothing
        }
        else
        {
            int start = StringUtils.indexOf(srcStr, beginStr);
            int endWithoutStartOrEnd = StringUtils.substring(srcStr, start).indexOf(endStr);
            if (start == -1 || endWithoutStartOrEnd == -1)
            {
                //do nothing
                //                System.err.println("getNeighbouringStrFromStartBetweenAndReturnElseStr  start or end is -1!");
            }
            else
            {
                int end = endWithoutStartOrEnd + start + endStr.length();
                firstString = StringUtils.substring(srcStr, start, end);
                if (StringUtils.lastIndexOf(firstString, beginStr) != 0)
                {
                    firstString = StringUtils.substring(firstString, StringUtils.lastIndexOf(firstString, beginStr));
                }
                secondString = StringUtils.substring(srcStr, end);
            }
        }
        twoTuples.setFirstString(firstString);
        twoTuples.setSecondString(secondString);
        return twoTuples;
    }
    
    /**
     * 精简显示，限定最大的字节数，并且去除掉多余的空格<br>
     * 一个中文占用两个字节
     * @author nan.li
     * @param str
     * @param maxLength
     * @return
     */
    public static String abbreviate(String str, int maxLength)
    {
        if (StringUtils.isEmpty(str))
        {
            return "";
        }
        char[] chars = str.toCharArray();
        StringBuilder sBuilder = new StringBuilder();
        int totalLength = 0;
        boolean lastCharIsEmpty = false;
        boolean thisCharIsEmpty = false;
        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];
            if (isChinese(c))
            {
                totalLength += 2;
                thisCharIsEmpty = false;
            }
            else if (c == ' ')
            {
                totalLength += 0;
                thisCharIsEmpty = true;
            }
            else
            {
                totalLength += 1;
                thisCharIsEmpty = false;
            }
            if (thisCharIsEmpty && lastCharIsEmpty)
            {
            
            }
            else
            {
                sBuilder.append(c);
            }
            
            if (c == ' ')
            {
                lastCharIsEmpty = true;
            }
            else
            {
                lastCharIsEmpty = false;
            }
            
            if (totalLength >= maxLength)
            {
                //判定有没有达到终点
                if (i != chars.length - 1)
                {
                    sBuilder.append("...");
                }
                return sBuilder.toString();
            }
        }
        return str;
    }
    
    /**
     * 判断一个字符是否是中文
     * @author nan.li
     * @param c
     * @return
     */
    public static boolean isChinese(char c)
    {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }
}
