package com.lnwazg.kit.pubsub.impl;

import java.util.HashMap;
import java.util.Map;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.pubsub.iface.DispatchCenter;
import com.lnwazg.kit.pubsub.iface.Handler;
import com.lnwazg.kit.pubsub.iface.Subscriber;

public class SubscriberAdapter implements Subscriber
{
    DispatchCenter dispatchCenterRef;
    
    /**
     * 订阅者可以处理的事件列表
     */
    Map<String, Handler> eventMap = new HashMap<>();
    
    @Override
    public boolean subscribeEvent(DispatchCenter dispatchCenter, String eventName, Handler handler)
    {
        registerToDispatchCenter(dispatchCenter);
        return subscribeEvent(eventName, handler);
    }
    
    @Override
    public boolean subscribeEvent(String eventName, Handler handler)
    {
        if (!eventMap.containsKey(eventName))
        {
            eventMap.put(eventName, handler);
            return true;
        }
        Logs.w(String.format("已经注册过%s事件，无法重新注册！", eventName));
        return false;
    }
    
    @Override
    public boolean registerToDispatchCenter(DispatchCenter dispatchCenter)
    {
        boolean result = dispatchCenter.registerSubscriber(this);
        this.dispatchCenterRef = dispatchCenter;
        return result;
    }
    
}
