package com.lnwazg.mq.framework;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.mq.util.MQHelper;

public class BaseController
{
    /**
     * 请求的map
     */
    protected Map<String, String> request;
    
    /**
     * 请求的map,等价于request
     */
    protected Map<String, String> paramMap;
    
    /**
     * 消息的发送者的邮箱
     */
    protected String from;
    
    protected void reply(String path, String... params)
    {
        if (StringUtils.isNotEmpty(from))
        {
            MQHelper.sendAsyncMsg(from, path, params);
        }
        else
        {
            Logs.e("该消息是匿名的，即【未指定发件人】，因此无法正常回复！您需要手动指定收信人地址！");
        }
    }
    
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
