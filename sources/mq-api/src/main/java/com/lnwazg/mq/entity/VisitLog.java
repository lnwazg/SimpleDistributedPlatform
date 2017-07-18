package com.lnwazg.mq.entity;

import java.util.Date;

import com.lnwazg.dbkit.anno.entity.AutoIncrement;
import com.lnwazg.dbkit.anno.entity.Id;

/**
 * MQ服务访问日志
 * @author Administrator
 * @version 2016年7月31日
 */
public class VisitLog
{
    @Id
    @AutoIncrement
    public Integer id;
    
    String serviceCode;
    
    Date visitTime;
    
    public Integer getId()
    {
        return id;
    }
    
    public VisitLog setId(Integer id)
    {
        this.id = id;
        return this;
    }
    
    public String getServiceCode()
    {
        return serviceCode;
    }
    
    public VisitLog setServiceCode(String serviceCode)
    {
        this.serviceCode = serviceCode;
        return this;
    }
    
    public Date getVisitTime()
    {
        return visitTime;
    }
    
    public VisitLog setVisitTime(Date visitTime)
    {
        this.visitTime = visitTime;
        return this;
    }
    
}
