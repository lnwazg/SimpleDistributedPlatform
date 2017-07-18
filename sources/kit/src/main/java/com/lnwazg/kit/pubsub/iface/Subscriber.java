package com.lnwazg.kit.pubsub.iface;

/**
 * 订阅者
 * @author Administrator
 * @version 2016年9月17日
 */
public interface Subscriber
{
    /**
     * 注册到调度中心，并且订阅某个事件
     * @author Administrator
     * @param dispatchCenter  要注册到的调度中心
     * @param eventName  事件名称
     * @return  订阅是否成功
     */
    boolean subscribeEvent(DispatchCenter dispatchCenter, String eventName, Handler handler);
    
    /**
     * 注册到调度中心
     * @author Administrator
     * @param dispatchCenter
     * @return
     */
    boolean registerToDispatchCenter(DispatchCenter dispatchCenter);
    
    /**
     * 订阅某个事件
     * @author Administrator
     * @param eventName
     * @param handler
     * @return
     */
    boolean subscribeEvent(String eventName, Handler handler);
}
