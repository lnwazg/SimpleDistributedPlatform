package com.lnwazg.kit.task;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.task.result.TaskBaseResult;

/**
 * 抽象任务调度器<br>
 * 当调用doService()方法的时候，该调度器会一直阻塞执行等待，直到所有任务都执行完毕，方能正常退出<br>
 * 支持批量执行任务，原理是将任务逐个地提交到线程池去执行。<br>
 * 当所有任务都执行完毕之后，该方法才会返回。<br>
 * 返回的是和任务列表一一对应的执行结果对象列表<br>
 * 等待机制，是通过Callable、Future<T>、future.isDone()这三个核心设施来实现的！<br>
 * 这是一个抽象类，业务代码交由具体的实现类去实现<br>
 * P  - Parameter的缩写<br>
 * R  - Result的缩写<br>
 * @修改人 chenyankai
 * @版本号 0.0.1 
 * @修改日期： 2016年11月3日 上午10:54:29
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public abstract class AbstractTaskScheduler<P, R extends TaskBaseResult>
{
    /**
     * 执行线程池，限定了最大的并发线程数
     */
    private ExecutorService executor = Executors.newFixedThreadPool(8);
    
    /**
     * 调度任务<br>
     * 支持批量执行任务，原理是将任务逐个地提交到线程池去执行。<br>
     * 当所有任务都执行完毕之后，该方法才会返回。<br>
     * 返回的是和任务列表一一对应的执行结果对象列表<br>
     * 等待机制，是通过Callable、Future<T>、future.isDone()这三个核心设施来实现的！
     * @param taskParameters 执行参数
     * @return list
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public List<R> doService(List<P> taskParameters)
    {
        return submitTaskToThreadPool(taskParameters);
    }
    
    /**
     * 提交任务至任务调度线程池
     * 
     * @param taskParameters 任务参数
     * @return list
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public List<R> submitTaskToThreadPool(List<P> taskParameters)
    {
        
        List<Future<R>> results = runJobInThreadPool(taskParameters);
        
        /** VERY IMPORTANT !! **/
        waitAllJobDone(results);
        /** VERY IMPORTANT !! **/
        
        List<R> threadResults = new ArrayList<R>();
        
        for (Future<R> result : results)
        {
            try
            {
                threadResults.add(result.get());
            }
            catch (InterruptedException e)
            {
                Logs.error("Failed to execute Future.get() ! ", e);
                e.printStackTrace();
            }
            catch (ExecutionException e)
            {
                Logs.error("Failed to execute Future.get() ! ", e);
                e.printStackTrace();
            }
        }
        return threadResults;
    }
    
    /**
     * 启动定时任务调度
     * 
     * @param taskParameters 任务参数
     * @return list
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private List<Future<R>> runJobInThreadPool(List<P> taskParameters)
    {
        
        List<Future<R>> futureList = new ArrayList<Future<R>>();
        for (P taskParameter : taskParameters)
        {
            Callable<R> task = buildTask(taskParameter);
            Future<R> future = executor.submit(task);
            futureList.add(future);
        }
        return futureList;
    }
    
    /**
     * 创建定时任务调度
     * 
     * @param taskParameter任务参数
     * @return [返回类型说明]
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private Callable<R> buildTask(final P taskParameter)
    {
        // call() 抛出的异常将被线程池消费掉， 主线程无法捕获, 故应该在方法中消费自己业务逻辑抛出的所有异常！！
        return new Callable<R>()
        {
            @Override
            public R call()
                throws Exception
            {
                R taskResult = newTaskResultObjectAndSetParams();
                try
                {
                    //如果不报错，那么就算该任务执行成功了
                    doBusiness(taskParameter, taskResult);
                    //框架层可以自定义地填充一些任务的执行结果关键信息
                    if (null == taskResult.getTime())
                    {
                        taskResult.setTime(new Date());
                    }
                    taskResult.setSuccess(true);
                }
                catch (Exception ex)
                {
                    //如果报错了，那么说明该任务执行失败了
                    //报错的时候，会将报错信息写入到任务执行结果对象里
                    taskResult.setTime(new Date());
                    taskResult.setSuccess(false);
                    StringWriter errors = new StringWriter();
                    ex.printStackTrace(new PrintWriter(errors));
                    String errStackTraceInfo = errors.toString();
                    taskResult.setErrMsg(ex.getMessage());
                    Logs.error("Failed to execute thread with parameter [" + taskParameter + "]" + ", the stack trace : " + errStackTraceInfo, ex);
                    return taskResult;
                }
                return taskResult;
            }
        };
    }
    
    /**
     * 创建一个任务结果对象<br>
     * 因为抽象类型T无法简单地实例化，因此交由具体的实现类去实现<br>
     * 并且还可以自定义地对该对象进行预处理
     * @author nan.li
     * @return
     */
    public abstract R newTaskResultObjectAndSetParams();
    
    /**
     * 核心的业务处理，都在这里了
     * @author nan.li
     * @param taskParameter
     * @param taskResult
     * @throws Exception
     */
    public abstract void doBusiness(final P taskParameter, R taskResult)
        throws Exception;
        
    /**
     * @param futureList </br>
     * 阻塞方法 ！</br>
     * 仅供等待所有的线程执行完毕返回， 此方法会一直阻塞直到参数中的所有 Future 的 isDone() 都为 true<br>
     * 这个方法是整个任务调度器最绝妙的地方！
     */
    protected void waitAllJobDone(List<Future<R>> futureList)
    {
        int nFuture = futureList.size();
        int nDone = 0;
        while (true)
        {
            for (Future<R> future : futureList)
            {
                if (future.isDone())
                {
                    nDone = nDone + 1;
                }
                else
                {
                    break;
                }
            }
            if (nDone == nFuture)
            {
                break;
            }
            else
            {
                nDone = 0;
            }
        }
    }
}
