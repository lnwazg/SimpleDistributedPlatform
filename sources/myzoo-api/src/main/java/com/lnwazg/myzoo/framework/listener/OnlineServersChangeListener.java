package com.lnwazg.myzoo.framework.listener;

/**
 * 在线服务器列表更新的监听器
 * @author nan.li
 * @version 2017年5月31日
 */
@FunctionalInterface
public interface OnlineServersChangeListener
{
    /**
     * 当收到了服务器列表更新了之后的回调方法
     * @author nan.li
     */
    void callback();
}
