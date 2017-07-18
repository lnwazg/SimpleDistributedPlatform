package com.lnwazg.myzoo.util;

import java.util.List;
import java.util.Map;

/**
 * 要保存到本地的myzookeeper可用服务器列表信息对象
 * @author nan.li
 * @version 2016年11月1日
 */
public class ZooServers
{
    /**
     * 服务器列表信息<br>
     * key是node名称，值是该node对象的数据
     */
    private Map<String, Map<String, String>> onlineServerInfoMap;
    
    /**
     * 组-节点映射表
     */
    private Map<String, List<String>> onlineGroupNodeInfoMap;
    
    public Map<String, Map<String, String>> getOnlineServerInfoMap()
    {
        return onlineServerInfoMap;
    }
    
    public void setOnlineServerInfoMap(Map<String, Map<String, String>> onlineServerInfoMap)
    {
        this.onlineServerInfoMap = onlineServerInfoMap;
    }
    
    public Map<String, List<String>> getOnlineGroupNodeInfoMap()
    {
        return onlineGroupNodeInfoMap;
    }
    
    public void setOnlineGroupNodeInfoMap(Map<String, List<String>> onlineGroupNodeInfoMap)
    {
        this.onlineGroupNodeInfoMap = onlineGroupNodeInfoMap;
    }
}
