package com.lnwazg.kit.pubsub;

import com.lnwazg.kit.pubsub.impl.DispatchCenterAdapter;

/**
 * 天气调度器程序
 * @author Administrator
 * @version 2016年9月17日
 */
public class WeatherDispatcher extends DispatchCenterAdapter
{
    private static final WeatherDispatcher INSTANCE = new WeatherDispatcher();
    
    public static WeatherDispatcher getInstance()
    {
        return INSTANCE;
    }
    
    private WeatherDispatcher()
    {
    }
    
}
