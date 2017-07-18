package com.lnwazg;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.lnwazg.api.DistributedTask;

/**
 * 在C盘下面生成一个代表当前节点描述的文件 
 * @author nan.li
 * @version 2017年7月14日
 */
public class Task001 extends DistributedTask
{
    public void executeCustom(Map<String, Object> map)
    {
        File file = new File(String.format("c://%s.txt", getCurrentDataNodeName()));
        try
        {
            file.createNewFile();
            FileUtils.writeStringToFile(file, "Hello, this is " + getCurrentDataNodeName(), "UTF-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public String getTaskDescription()
    {
        return "在C盘创建代表我的节点的文件，并输入一行字";
    }
}
