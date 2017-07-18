package com.lnwazg.kit.servicecenter.api;

import java.util.Collection;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * 服务中心<br>
 * @author nan.li
 * @version 2016年8月30日
 */
public interface ServiceCenter
{
    /**
     * 注册服务
     * @author nan.li
     * @param id  服务编号，只要不重复即可
     * @param serviceObject   服务对象
     * @param remark   服务的说明
     * @return
     */
    boolean registerService(int id, Object serviceObject, String remark);
    
    boolean registerService(String id, Object serviceObject, String remark);
    
    /**
     * 根据端口号获取某一个service对象以及对象的说明
     * @author nan.li
     * @param id 服务编号，只要不重复即可
     * @return
     */
    ImmutablePair<Object, String> getService(int id);
    
    ImmutablePair<Object, String> getService(String id);
    
    /**
     * 获取所有可用的服务列表
     * @author nan.li
     * @return
     */
    Collection<ImmutablePair<Object, String>> getAllServices();
    
}
