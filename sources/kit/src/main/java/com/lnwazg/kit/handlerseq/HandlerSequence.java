package com.lnwazg.kit.handlerseq;

import java.util.LinkedList;
import java.util.List;

/**
 * 一个通用的处理序列
 * @author Administrator
 * @version 2016年4月15日
 */
public class HandlerSequence
{
    private static HandlerSequence INSTANCE = new HandlerSequence();
    
    public static HandlerSequence getInstance()
    {
        return INSTANCE;
    }
    
    /**
     * 处理序列表
     */
    private List<IHandler> handlers = new LinkedList<>();
    
    /**
     * 往启动序列中追加启动器，并执行
     * @author Administrator
     * @param handler
     * @return
     */
    public HandlerSequence exec(IHandler handler)
    {
        handler.handle();
        return this;
    }
    
    public HandlerSequence addHandler(IHandler handler)
    {
        handlers.add(handler);
        return this;
    }
    
    public HandlerSequence execAll(IHandler handler)
    {
        for (IHandler iHandler : handlers)
        {
            iHandler.handle();
        }
        //处理完成后，将任务表清空
        handlers.clear();
        return this;
    }
    
}
