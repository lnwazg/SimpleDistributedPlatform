package com.lnwazg.kit.pubsub.iface;

/**
 * 发布者
 * @author Administrator
 * @version 2016年9月17日
 */
public interface Publisher
{
    
    /**
     * 当该事件触发时候，发布者发布该事件到调度中心（顺带上下文）,由调度中心统一调度订阅者注册到调度中心的处理代码。
     * @author Administrator
     * @param dispatchCenter
     * @param eventName
     * @param context
     * @return
     */
    boolean fireEvent(String eventName, Object... context);
    
    /**
     * 注册发布者到某个调度中心
     * @author Administrator
     * @param dispatchCenter
     * @return
     */
    boolean registerToDispatchCenter(DispatchCenter dispatchCenter);
}
