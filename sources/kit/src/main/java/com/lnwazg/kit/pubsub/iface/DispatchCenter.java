package com.lnwazg.kit.pubsub.iface;

import com.lnwazg.kit.pubsub.impl.SubscriberAdapter;

/**
 * 调度中心
 * @author Administrator
 * @version 2016年9月17日
 */
public interface DispatchCenter
{
    /**
     * 注册发布者
     * @author Administrator
     * @param publisher
     */
    boolean registerPublisher(Publisher publisher);
    
    /**
     * 注册订阅者
     * @author Administrator
     * @param subscriber
     * @return
     */
    boolean registerSubscriber(SubscriberAdapter subscriberAdapter);
    
    /**
     * 触发某个事件。依次通知每个订阅者触发
     * @author Administrator
     * @param eventName
     * @param context
     */
    void fireEvent(String eventName, Object... context);
}
