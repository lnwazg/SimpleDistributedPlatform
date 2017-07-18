package com.lnwazg.kit.plugin;

/**
 * 所有的插件必须要实现的通用接口列表
 * @author nan.li
 * @version 2017年2月21日
 */
public interface IPlugin
{
    /**
     * 插件的初始化
     * @author nan.li
     */
    void init();
    
    /**
     * 插件的加载
     * @author nan.li
     */
    void load();
    
    /**
     * 插件的卸载
     * @author nan.li
     */
    void unload();
    
    /**
     * 插件的环境清理工作
     * @author nan.li
     */
    void destroy();
}
