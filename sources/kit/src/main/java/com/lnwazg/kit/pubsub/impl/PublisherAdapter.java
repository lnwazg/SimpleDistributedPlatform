package com.lnwazg.kit.pubsub.impl;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.pubsub.iface.DispatchCenter;
import com.lnwazg.kit.pubsub.iface.Publisher;

public abstract class PublisherAdapter implements Publisher
{
    DispatchCenter dispatchCenterRef;
    
    @Override
    public boolean fireEvent(String eventName, Object... context)
    {
        if (dispatchCenterRef == null)
        {
            Logs.e("尚未注册到DispatchCenter哦！请注册之后再发布事件！");
            return false;
        }
        dispatchCenterRef.fireEvent(eventName, context);
        return true;
    }
    
    @Override
    public boolean registerToDispatchCenter(DispatchCenter dispatchCenter)
    {
        dispatchCenter.registerPublisher(this);
        dispatchCenterRef = dispatchCenter;
        return true;
    }
}
