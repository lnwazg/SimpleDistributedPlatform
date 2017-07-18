package com.lnwazg.kit.taskman;

import java.util.concurrent.Callable;

/**
 * 
 * 一个任务对象，用法同Runnable<br>
 * 唯一的区别是，这个对象是为了配合任务管理器使用的<br>
 * 这是一个抽象类，其实现了Runnable和Callable接口
 * @see TaskManager
 * @author  nan.li
 * @version 2013-12-28
 */
public abstract class CallableTask implements Callable<Void>, Runnable
{
    @Override
    public Void call()
        throws Exception
    {
        run();
        return null;
    }
}
