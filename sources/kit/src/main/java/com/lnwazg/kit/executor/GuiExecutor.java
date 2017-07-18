package com.lnwazg.kit.executor;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

/**
 * 适合用于GUI的executor
 * @author nan.li
 * @version 2016年4月13日
 */
public class GuiExecutor extends AbstractExecutorService
{
    private static final ExecutorService INSTANCE = new GuiExecutor();
    
    private GuiExecutor()
    {
    }
    
    public static ExecutorService getInstance()
    {
        return INSTANCE;
    }
    
    @Override
    public void execute(Runnable command)
    {
        if (SwingUtilities.isEventDispatchThread())
        {
            command.run();
        }
        else
        {
            SwingUtilities.invokeLater(command);
        }
    }
    
    @Override
    public void shutdown()
    {
    }
    
    @Override
    public List<Runnable> shutdownNow()
    {
        return null;
    }
    
    @Override
    public boolean isShutdown()
    {
        return false;
    }
    
    @Override
    public boolean isTerminated()
    {
        return false;
    }
    
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException
    {
        return false;
    }
}
