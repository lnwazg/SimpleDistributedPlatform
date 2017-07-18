package com.lnwazg.kit.proxy.constant;

/**
 * 代理的标记<br>
 * 标记了使用哪一个代理对象
 * @author Administrator
 * @version 2016年5月2日
 */
public enum ProxyFlag
{
    /**
     * 代理<br>
     * 序号为0的那个对象
     */
    DO_PROXY(0),
    /**
     * 不代理<br>
     * 序号为1的代理对象是： NoOp.INSTANCE，也就是说：不代理
     */
    DO_NOT_PROXY(1);
    
    int flag;
    
    ProxyFlag(int flag)
    {
        this.flag = flag;
    }
    
    public int get()
    {
        return flag;
    }
}
