package com.lnwazg.kit.servicecenter.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.servicecenter.api.ServiceCenter;

public class ServiceCenterImpl implements ServiceCenter
{
    private static final ServiceCenterImpl INSTANCE = new ServiceCenterImpl();
    
    static Map<Integer, ImmutablePair<Object, String>> serviceIntMap = new HashMap<>();
    
    static Map<String, ImmutablePair<Object, String>> serviceStringMap = new HashMap<>();
    
    private ServiceCenterImpl()
    {
    }
    
    public static ServiceCenterImpl getInstance()
    {
        return INSTANCE;
    }
    
    @Override
    public boolean registerService(int id, Object serviceObject, String remark)
    {
        if (serviceIntMap.containsKey(id))
        {
            Logs.w(String.format("服务号【%d】已经被注册使用！", id));
            return false;
        }
        serviceIntMap.put(id, new ImmutablePair<>(serviceObject, remark));
        return true;
    }
    
    @Override
    public boolean registerService(String id, Object serviceObject, String remark)
    {
        if (serviceStringMap.containsKey(id))
        {
            Logs.w(String.format("服务号【%s】已经被注册使用！", id));
            return false;
        }
        serviceStringMap.put(id, new ImmutablePair<>(serviceObject, remark));
        return true;
    }
    
    @Override
    public ImmutablePair<Object, String> getService(int id)
    {
        return serviceIntMap.get(id);
    }
    
    @Override
    public ImmutablePair<Object, String> getService(String id)
    {
        return serviceStringMap.get(id);
    }
    
    @Override
    public Collection<ImmutablePair<Object, String>> getAllServices()
    {
        Collection<ImmutablePair<Object, String>> ret = serviceIntMap.values();
        ret.addAll(serviceStringMap.values());
        return ret;
    }
    
}
