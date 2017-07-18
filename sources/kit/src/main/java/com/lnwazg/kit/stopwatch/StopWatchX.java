package com.lnwazg.kit.stopwatch;

import org.apache.commons.lang.time.StopWatch;

/**
 * 增加了计算分割的间隔时间的功能
 * @author nan.li
 * @version 2017年5月26日
 */
public class StopWatchX extends StopWatch
{
    /**
     * 上一次的Split time
     */
    long lastSplitTime = 0;
    
    /**
     * 当前的Split time
     */
    long thisSplitTime = 0;
    
    @Override
    public void split()
    {
        super.split();
        //先将上次的间隔时间保存起来
        lastSplitTime = thisSplitTime;
        //然后记录本次的间隔时间
        thisSplitTime = getSplitTime();
    }
    
    /**
     * 获取最近两次间隔的分割时间
     * @author nan.li
     * @return
     */
    public long getSplitIntervalTime()
    {
        return thisSplitTime - lastSplitTime;
    }
}
