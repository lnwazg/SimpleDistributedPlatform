package com.lnwazg.kit.taskman;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 任务管理器的任务项<br>
 * R  返回的对象<br>
 * C  一个Callable对象，作为task参数传入进来<br>
 * Future<R> future 也是一个参数，它是一个跟泛型对象R相呼应的Future参数
 * @author  nan.li
 * @version 2013-12-28
 */
public class TaskItem<R, C extends Callable<R>>
{
    /**
     * 任务未来对象
     */
    public final Future<R> future;
    
    /**
     * 任务
     */
    public final C task;
    
    /**
     * 构造函数 
     * @param future 任务的执行结果
     * @param task   任务对象
     */
    public TaskItem(Future<R> future, C task)
    {
        this.future = future;
        this.task = task;
    }
}