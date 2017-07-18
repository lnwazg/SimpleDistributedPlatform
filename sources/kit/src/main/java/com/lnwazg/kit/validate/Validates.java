package com.lnwazg.kit.validate;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.lnwazg.kit.exception.ValidateException;

/**
 * 验证工具类<br>
 * 判断某个字符串参数是否是指定的类型，或者满足指定的格式要求等
 * @author nan.li
 * @version 2017年4月8日
 */
public class Validates
{
    
    /** 英文字母 、数字和下划线 */
    public final static Pattern GENERAL = Pattern.compile("^\\w+$");
    
    /** 数字 */
    public final static Pattern NUMBERS = Pattern.compile("\\d+");
    
    /** 分组 */
    public final static Pattern GROUP_VAR = Pattern.compile("\\$(\\d+)");
    
    /** IP v4 */
    public final static Pattern IPV4 = Pattern.compile(
        "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        
    /** 货币 */
    public final static Pattern MONEY = Pattern.compile("^(\\d+(?:\\.\\d+)?)$");
    
    /** 邮件 */
    public final static Pattern EMAIL = Pattern.compile("(\\w|.)+@\\w+(\\.\\w+){1,2}");
    
    /** 移动电话 */
    public final static Pattern MOBILE = Pattern.compile("1\\d{10}");
    
    /** 身份证号码 */
    public final static Pattern CITIZEN_ID = Pattern.compile("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)");
    
    /** 邮编 */
    public final static Pattern ZIP_CODE = Pattern.compile("\\d{6}");
    
    /** 生日 */
    public final static Pattern BIRTHDAY = Pattern.compile("^(\\d{2,4})([/\\-\\.年]?)(\\d{1,2})([/\\-\\.月]?)(\\d{1,2})日?$");
    
    /** URL */
    public final static Pattern URL = Pattern.compile("(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?");
    
    /** 中文字、英文字母、数字和下划线 */
    public final static Pattern GENERAL_WITH_CHINESE = Pattern.compile("^[\\u0391-\\uFFE5\\w]+$");
    
    /** UUID */
    public final static Pattern UUID = Pattern.compile("^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$");
    
    /** 不带横线的UUID */
    public final static Pattern UUID_SIMPLE = Pattern.compile("^[0-9a-z]{32}$");
    
    /** 正则表达式匹配中文 */
    public final static String RE_CHINESE = "[\u4E00-\u9FFF]";
    
    /**
     * 验证手机号是否合法
     * @author nan.li
     * @param chars
     * @param message
     * @param values
     * @return
     */
    public static String validateTel(final String str, final String message, final Object... values)
    {
        if (str == null)
        {
            throw new NullPointerException(String.format(message, values));
        }
        if (str.length() == 0)
        {
            throw new IllegalArgumentException(String.format(message, values));
        }
        if (!isMobile(str))
        {
            throw new IllegalStateException(String.format(message, values));
        }
        return str;
    }
    
    /**
     * 验证是否是一个合法的布尔值
     * @author nan.li
     * @param str
     * @param message
     * @param values
     */
    public static String validateBoolean(final String str, final String message, final Object... values)
    {
        if (StringUtils.isEmpty(str))
        {
            throw new IllegalArgumentException(String.format(message, values));
        }
        //        if (!Boolean.TRUE.equals(str) && !Boolean.FALSE.equals(str))
        if (!"true".equalsIgnoreCase(str) && !"false".equalsIgnoreCase(str))
        {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return str;
    }
    
    /**
     * 是否中文人名
     * @author nan.li
     * @param str
     * @return
     */
    public static boolean isChineseName(String str)
    {
        //是否为中文人名
        String regEx = "[\u4e00-\u9fa5]{2,6}"; //2个以上汉字，不可包含空格
        return str.matches(regEx);
    }
    
    /**
     * 是否是合法手机号
     * @author nan.li
     * @param str
     * @return
     */
    public static boolean isMobile(String str)
    {
        Pattern p = null;
        Matcher m = null;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        return m.matches();
    }
    
    /**
     * 验证输入的邮箱格式是否符合
     * @param email
     * @return 是否合法
     */
    public static boolean emailFormat(String email)
    {
        boolean tag = true;
        final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{3,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        if (!mat.find())
        {
            tag = false;
        }
        return tag;
    }
    
    /**
     * 是否整数
     * @author nan.li
     * @param str
     * @return
     */
    public static boolean isInteger(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    /**
     * 验证参数是否是整数
     * @author nan.li
     * @param str
     * @param message
     */
    public static void validateInteger(String str, String message)
    {
        if (!isInteger(str))
        {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * 是否正整数
     * @author nan.li
     * @param str
     * @return
     */
    public static boolean isPositiveInteger(String str)
    {
        return isInteger(str) && Integer.parseInt(str) > 0;
    }
    
    /**
     * 验证参数是否是正整数
     * @author nan.li
     * @param str
     * @param message
     */
    public static void validatePositiveInteger(String str, String message)
    {
        if (!isPositiveInteger(str))
        {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * 是否负整数
     * @author nan.li
     * @param str
     * @return
     */
    public static boolean isNegativeInteger(String str)
    {
        return isInteger(str) && Integer.parseInt(str) < 0;
    }
    
    /**
     * 验证参数是否是负整数
     * @author nan.li
     * @param str
     * @param message
     */
    public static void validateNegativeInteger(String str, String message)
    {
        if (!isNegativeInteger(str))
        {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * 是否长整数
     * @author nan.li
     * @param str
     * @return
     */
    public static boolean isLong(String str)
    {
        try
        {
            Long.parseLong(str);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    /**
     * 是否双精度
     * @author nan.li
     * @param str
     * @return
     */
    public static boolean isDouble(String str)
    {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    /**
     * 验证是否为空<br>
     * 对于String类型判定是否为empty(null 或 "")<br>
     * 
     * @param value 值
     * @return 是否为空
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T value)
    {
        return (null == value || (value instanceof String && StringUtils.isEmpty((String)value)));
    }
    
    /**
     * 验证是否为空<br>
     * 对于String类型判定是否为empty(null 或 "")<br>
     * 
     * @param value 值
     * @return 是否为空
     * @return 是否为空
     */
    public static <T> boolean isNotEmpty(T value)
    {
        return false == isEmpty(value);
    }
    
    /**
     * 验证是否为空，为空时抛出异常<br>
     * 对于String类型判定是否为empty(null 或 "")<br>
     * 
     * @param value 值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static <T> void validateNotEmpty(T value, String errorMsg)
        throws ValidateException
    {
        if (isEmpty(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 通过正则表达式验证
     * 
     * @param pattern 正则模式
     * @param value 值
     * @return 是否匹配正则
     */
    public static boolean isMatchRegex(Pattern pattern, String content)
    {
        if (content == null || pattern == null)
        {
            //提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }
    
    public static boolean isMatchRegex(String regex, String value)
    {
        return isMatch(regex, value);
    }
    
    public static boolean isMatch(String regex, String content)
    {
        if (content == null)
        {
            //提供null的字符串为不匹配
            return false;
        }
        if (StringUtils.isEmpty(regex))
        {
            //正则不存在则为全匹配
            return true;
        }
        return Pattern.matches(regex, content);
    }
    
    /**
     * 验证是否为货币
     * 
     * @param value 值
     * @return 是否为货币
     */
    public static boolean isMoney(String value)
    {
        return isMatchRegex(MONEY, value);
    }
    
    /**
     * 验证是否为货币
     * 
     * @param value 值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateMoney(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isMoney(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 验证是否为邮政编码（中国）
     * 
     * @param value 值
     * @return 是否为邮政编码（中国）
     */
    public static boolean isZipCode(String value)
    {
        return isMatchRegex(ZIP_CODE, value);
    }
    
    /**
     * 验证是否为邮政编码（中国）
     * 
     * @param value 表单值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateZipCode(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isZipCode(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 验证是否为可用邮箱地址
     * 
     * @param value 值
     * @return 否为可用邮箱地址
     */
    public static boolean isEmail(String value)
    {
        return isMatchRegex(EMAIL, value);
    }
    
    /**
     * 验证是否为可用邮箱地址
     * 
     * @param value 值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateEmail(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isEmail(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 验证是否为手机号码（中国）
     * 
     * @param value 值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateMobile(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isMobile(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 验证是否为身份证号码（18位中国）<br>
     * 出生日期只支持到到2999年
     * 
     * @param value 值
     * @return 是否为身份证号码（18位中国）
     */
    public static boolean isCitizenId(String value)
    {
        return isMatchRegex(CITIZEN_ID, value);
    }
    
    /**
     * 验证是否为身份证号码（18位中国）<br>
     * 出生日期只支持到到2999年
     * 
     * @param value 值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateCitizenIdNumber(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isCitizenId(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 验证是否为IPV4地址
     * 
     * @param value 值
     * @return 是否为IPV4地址
     */
    public static boolean isIpv4(String value)
    {
        return isMatchRegex(IPV4, value);
    }
    
    /**
     * 验证是否为IPV4地址
     * 
     * @param value 值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateIpv4(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isIpv4(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 验证是否为URL
     * 
     * @param value 值
     * @return 是否为URL
     */
    public static boolean isUrl(String value)
    {
        try
        {
            new java.net.URL(value);
        }
        catch (MalformedURLException e)
        {
            return false;
        }
        return true;
    }
    
    /**
     * 验证是否为URL
     * 
     * @param value 值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateUrl(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isUrl(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 验证是否为汉字
     * 
     * @param value 值
     * @return 是否为汉字
     */
    public static boolean isChinese(String value)
    {
        return isMatchRegex("^" + RE_CHINESE + "+$", value);
    }
    
    /**
     * 验证是否为汉字
     * 
     * @param value 表单值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateChinese(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isChinese(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 验证是否为中文字、英文字母、数字和下划线
     * 
     * @param value 值
     * @return 是否为中文字、英文字母、数字和下划线
     */
    public static boolean isGeneralWithChinese(String value)
    {
        return isMatchRegex(GENERAL_WITH_CHINESE, value);
    }
    
    /**
     * 验证是否为中文字、英文字母、数字和下划线
     * 
     * @param value 值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateGeneralWithChinese(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isGeneralWithChinese(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
    /**
     * 验证是否为UUID<br>
     * 包括带横线标准格式和不带横线的简单模式
     * 
     * @param value 值
     * @return 是否为UUID
     */
    public static boolean isUUID(String value)
    {
        return isMatchRegex(UUID, value) || isMatchRegex(UUID_SIMPLE, value);
    }
    
    /**
     * 验证是否为UUID<br>
     * 包括带横线标准格式和不带横线的简单模式
     * 
     * @param value 值
     * @param errorMsg 验证错误的信息
     * @throws ValidateException
     */
    public static void validateUUID(String value, String errorMsg)
        throws ValidateException
    {
        if (false == isUUID(value))
        {
            throw new ValidateException(errorMsg);
        }
    }
    
}
