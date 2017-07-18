package com.lnwazg.mq.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.GsonBuilder;
import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.map.Maps;
import com.lnwazg.mq.api.MqRequest;
import com.lnwazg.mq.framework.MQFramework;

/**
 * MQ工具类
 * @author nan.li
 * @version 2017年3月29日
 */
public class MQHelper
{
    /**
     * 消息发送<br>
     * 如果消息发不出去：<br>
     * 1.MQ服务器没正常启动，可以查看MQ服务器的日志文件<br>
     * 2.连接到了代理MQ服务器，但是代理设置的地址和端口号不对<br>
     * 3.客户端配置文件夹中没有放置跟服务端相同的通信证书:  certificate.crt
     * 
     * @author nan.li
     * @param targetAddress  收件人地址
     * @param targetInvokeMethodFullPath     收件人的被调用方法的完整地址
     * @param targetInvokeMethodParams   收件人的被调用方法的调用参数表
     */
    public static void sendAsyncMsg(String targetAddress, String targetInvokeMethodFullPath, Object... targetInvokeMethodParams)
    {
        ExecMgr.cachedExec.execute(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("path", targetInvokeMethodFullPath);
            Map<String, Object> paramMap = Maps.asMap(targetInvokeMethodParams);
            if (StringUtils.isNotEmpty(MQFramework.myselfAddress))
            {
                paramMap.put("from", MQFramework.myselfAddress);
            }
            map.put("params", paramMap);
            String message = new GsonBuilder().setPrettyPrinting().create().toJson(map);
            new MqRequest("SendMessage").addParam("message", message).addParam("node", targetAddress).sendAsync();
        });
    }
    
}
