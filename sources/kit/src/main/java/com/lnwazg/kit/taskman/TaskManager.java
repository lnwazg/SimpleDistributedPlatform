package com.lnwazg.kit.taskman;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务管理器<br>
 * 是一个ArrayList，里面的每一个元素都是一个TaskItem<br>
 * 一个重要的用途，就是cleanAndAdd(task)这个方法，添加一个新任务的时候将所有的老任务取消掉！<br>
 * clean的核心就是调用purge()方法，而purge()的核心思路，就是依次遍历当前的ArrayList的每一个元素，并获取该元素的future对象，然后调用future.cancel(true);
 * @author  nan.li
 * @version 2013-12-28
 */
public class TaskManager<R, C extends Callable<R>> extends ArrayList<TaskItem<R, C>>
{
    private static final long serialVersionUID = 6554845574655468808L;
    
    /**
     * 单线程线程池
     * 此处可完全自定义成其他类型的线程池
     */
    private ExecutorService exec = Executors.newSingleThreadExecutor();
    
    /**
     * 提交新任务到线程队列中，排队等待执行
     * @param task
     */
    public void add(C task)
    {
        add(new TaskItem<R, C>(exec.submit(task), task));
    }
    
    /**
     * 先停止掉所有排队中以及正在运行中的任务，然后将新任务提交到任务管理器
     * @author nan.li
     * @param task
     */
    public void cleanAndAdd(C task)
    {
        purge();
        add(task);
    }
    
    /**
     * 获取已经完成的任务的返回结果对象列表，并将这些已经完成的任务从任务管理器列表中删除
     * @return
     */
    public synchronized List<R> getCompletedResults()
    {
        Iterator<TaskItem<R, C>> iterator = iterator();
        List<R> results = new ArrayList<R>();//任务返回的结果列表
        while (iterator.hasNext())
        {
            TaskItem<R, C> item = iterator.next();
            //若该任务已经完成
            if (item.future.isDone())
            {
                try
                {
                    results.add(item.future.get());
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                iterator.remove();//将该任务从任务管理器列表中删除
            }
        }
        return results;
    }
    
    /**
     * 停止所有未完成的线程，返回一个这些线程名称的列表<br>
     * 有对共享资源的操作，所以必须加锁以保证并发情况下的运行正常！
     * @return
     */
    public synchronized List<String> purge()
    {
        Iterator<TaskItem<R, C>> iterator = iterator();
        List<String> results = new ArrayList<String>();
        while (iterator.hasNext())
        {
            TaskItem<R, C> item = iterator.next();
            //若该任务已经完成
            if (!item.future.isDone())
            {
                results.add(String.format("Cancelling %s", item.task.toString()));//直接调用该任务的toString()方法
                item.future.cancel(true);//May interrupt //对于正在运行的线程，可能会将其直接中断
                iterator.remove();//将该任务从任务管理器列表中删除
            }
        }
        return results;
    }
}
