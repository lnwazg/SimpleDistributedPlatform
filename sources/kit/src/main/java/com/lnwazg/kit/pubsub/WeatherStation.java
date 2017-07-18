package com.lnwazg.kit.pubsub;

import com.lnwazg.kit.pubsub.impl.PublisherAdapter;

/**
 * 气象站-发布者
 * @author Administrator
 * @version 2016年9月17日
 */
public class WeatherStation extends PublisherAdapter
{
    public boolean fireWeatherChangeEvent(String temperature)
    {
        return fireEvent("weatherChange", temperature);
    }
    
    public boolean firePowerOffEvent()
    {
        return fireEvent("powerOff");
    }
}
