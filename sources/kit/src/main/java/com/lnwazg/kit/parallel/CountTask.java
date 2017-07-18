package com.lnwazg.kit.parallel;

import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import com.lnwazg.kit.executor.ExecMgr;

/**
 * 多线程并行计算任务
 * @author nan.li
 * @version 2016年10月27日
 */
public class CountTask extends RecursiveTask<Long>
{
    private static final long serialVersionUID = 1L;
    
    /**
     * 任务分割的阀值
     */
    private static final int THRESHOLD = 200;
    
    /**
     * 开始
     */
    private int start;
    
    /**
     * 结束
     */
    private int end;
    
    public CountTask(int start, int end)
    {
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected Long compute()
    {
        Long sum = 0L;
        boolean canCompute = (end - start) <= THRESHOLD;
        //如果小于可分割的阀值了，那么就可以计算了
        if (canCompute)
        {
            for (int i = start; i <= end; i++)
                sum += i;
        }
        else
        {
            //如果任务大于阀值，就分裂成两个子任务计算
            int mid = (start + end) / 2;
            
            CountTask leftTask = new CountTask(start, mid);
            CountTask rightTask = new CountTask(mid + 1, end);
            
            //执行子任务
            leftTask.fork();
            rightTask.fork();
            
            //等待子任务执行完，并得到结果
            Long leftResult = (Long)leftTask.join();
            Long rightResult = (Long)rightTask.join();
            
            sum = leftResult + rightResult;
        }
        return sum;
    }
    
    public static void main(String[] args)
    {
        //生成一个计算资格，负责计算1+2+3+4  
        CountTask task = new CountTask(1, 100000);
        Future<Long> result = ExecMgr.forkJoinPool.submit(task);
        try
        {
            System.out.println(result.get());
        }
        catch (Exception e)
        {
        }
    }
}
