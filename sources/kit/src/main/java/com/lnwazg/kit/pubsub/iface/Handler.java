package com.lnwazg.kit.pubsub.iface;

/**
 * 仿js的方式实现回调处理器<br>
 * 该实现方式是最灵活的方式
 * @author Administrator
 * @version 2016年9月17日
 */
@FunctionalInterface
public interface Handler
{
    void handle(Object... args);
}
