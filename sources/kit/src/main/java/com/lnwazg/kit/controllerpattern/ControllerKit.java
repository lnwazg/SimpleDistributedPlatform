package com.lnwazg.kit.controllerpattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;

/**
 * Controller通用工具包
 * @author lnwazg@126.com
 * @version 2017年3月31日
 */
public class ControllerKit
{
    /**
     * path验证及修复<br>
     * 如果没有以“/”开头，那么就加上这个开头
     * @author nan.li
     * @param path
     * @return
     */
    public static String fixPath(String path)
    {
        if (StringUtils.isEmpty(path))
        {
            return "";
        }
        else
        {
            if (!path.startsWith("/"))
            {
                path = String.format("/%s", path);
            }
            return path;
        }
    }
    
    /**
     * 是否符合一个controller的请求结构
     * @author nan.li
     * @param key
     * @return
     */
    public static boolean matchPath(String key)
    {
        Pattern pattern = Pattern.compile("^\\/\\w+\\/\\w+$");
        Matcher matcher = pattern.matcher(key);
        return matcher.matches();
    }
    
    /**
     * 解析出这个Controller对应的Class和Method
     * @author nan.li
     * @param key
     * @return
     */
    public static MutablePair<String, String> resolvePath(String key)
    {
        MutablePair<String, String> retPair = new MutablePair<>();
        List<String> ret = new ArrayList<>();
        Pattern pat = Pattern.compile("^\\/(\\w+)\\/(\\w+)$");
        Matcher mat = pat.matcher(key);
        while (mat.find())
        {
            for (int i = 1; i <= mat.groupCount(); i++)
            {
                String find = mat.group(i);
                ret.add(find);
            }
        }
        if (ret.size() == 2)
        {
            retPair.setLeft(ret.get(0));
            retPair.setRight(ret.get(1));
            return retPair;
        }
        return null;
    }
    
}
