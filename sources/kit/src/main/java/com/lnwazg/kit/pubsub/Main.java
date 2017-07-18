package com.lnwazg.kit.pubsub;

import org.apache.commons.lang.ObjectUtils;

import com.lnwazg.kit.log.Logs;

/**
 * 发布订阅模式的测试类
 * @author Administrator
 * @version 2016年9月17日
 */
public class Main
{
    public static void main(String[] args)
    {
        //比如有个界面(订阅者)是实时显示天气，它就订阅天气事件（注册到调度中心，包括处理程序），
        //当天气变化时（定时获取数据），就作为发布者发布天气信息到调度中心，调度中心就调度订阅者的天气处理程序。
        //调度中心
        WeatherDispatcher weatherDispatcher = WeatherDispatcher.getInstance();
        
        //显示器  订阅者
        WeatherScreen weatherScreen = new WeatherScreen();
        //订阅天气变化事件，收到事件后实时显示当前最新的天气状况
        weatherScreen.registerToDispatchCenter(weatherDispatcher);
        weatherScreen.subscribeEvent("weatherChange", argv -> {
            String weather = ObjectUtils.toString(argv[0]);
            Logs.i(String.format("【显示器89757】当前的天气情况是：%s", weather));
        });
        
        WeatherScreen weatherScreen2 = new WeatherScreen();
        weatherScreen2.registerToDispatchCenter(weatherDispatcher);
        weatherScreen2.subscribeEvent("weatherChange", argv -> {
            String weather = ObjectUtils.toString(argv[0]);
            Logs.i(String.format("【显示器89758】当前的天气情况是：%s", weather));
        });
        weatherScreen2.subscribeEvent("powerOff", argv -> {
            Logs.i(String.format("【显示器89758】准备关机..."));
        });
        
        //气象站 发布者
        WeatherStation weatherStation = new WeatherStation();
        weatherStation.registerToDispatchCenter(weatherDispatcher);
        for (int i = 0; i < 10; i++)
        {
            weatherStation.fireWeatherChangeEvent("26摄氏度");
            //            try
            //            {
            //                TimeUnit.MICROSECONDS.sleep(1);
            //            }
            //            catch (InterruptedException e)
            //            {
            //                e.printStackTrace();
            //            }
        }
        weatherStation.firePowerOffEvent();
        
        //发送一个自定义的事件
        weatherStation.fireEvent("weatherChange", "30度");
        weatherStation.fireEvent("weatherChange", "28度");
        weatherStation.fireEvent("weatherChange", "27度");
    }
}
