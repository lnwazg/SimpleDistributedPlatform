package com.lnwazg.kit.pubsub.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lnwazg.kit.pubsub.iface.DispatchCenter;
import com.lnwazg.kit.pubsub.iface.Handler;
import com.lnwazg.kit.pubsub.iface.Publisher;

public class DispatchCenterAdapter implements DispatchCenter
{
    List<Publisher> publishers = new ArrayList<>();
    
    List<SubscriberAdapter> subscribers = new ArrayList<>();
    
    @Override
    public boolean registerPublisher(Publisher publisher)
    {
        if (!publishers.contains(publisher))
        {
            publishers.add(publisher);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean registerSubscriber(SubscriberAdapter subscriberAdapter)
    {
        if (!subscribers.contains(subscriberAdapter))
        {
            subscribers.add(subscriberAdapter);
            return true;
        }
        return false;
    }
    
    @Override
    public void fireEvent(String eventName, Object... context)
    {
        for (SubscriberAdapter subscriberAdapter : subscribers)
        {
            Map<String, Handler> eventMap = subscriberAdapter.eventMap;
            if (eventMap.containsKey(eventName))
            {
                Handler handler = eventMap.get(eventName);
                if (null != handler)
                {
                    handler.handle(context);
                }
            }
        }
    }
    
}
