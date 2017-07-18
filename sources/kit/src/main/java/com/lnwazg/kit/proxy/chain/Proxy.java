package com.lnwazg.kit.proxy.chain;

/**
 * 代理类<br>
 * 真正执行的时候是交予代理链去依次执行（执行过程类似于一个"大于号"）
 * @author Administrator
 * @version 2016年5月2日
 */
public interface Proxy
{
    void doProxy(ProxyChain proxyChain);
}
