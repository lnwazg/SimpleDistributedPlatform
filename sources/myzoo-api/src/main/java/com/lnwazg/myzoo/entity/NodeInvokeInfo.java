package com.lnwazg.myzoo.entity;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.lnwazg.dbkit.anno.entity.AutoIncrement;
import com.lnwazg.dbkit.anno.entity.Comment;
import com.lnwazg.dbkit.anno.entity.Id;
import com.lnwazg.dbkit.anno.entity.Varchar;

@Comment("节点调用次数信息表")
public class NodeInvokeInfo
{
    @Id
    @AutoIncrement
    @Comment("主键")
    Integer id;
    
    @Comment("节点名称")
    String nodeName;
    
    @Varchar(25)
    @Comment("调用次数")
    Integer invokeTimes;
    
    @Comment("该记录的创建时间")
    Date createTime;
    
    public Integer getId()
    {
        return id;
    }
    
    public NodeInvokeInfo setId(Integer id)
    {
        this.id = id;
        return this;
    }
    
    public String getNodeName()
    {
        return nodeName;
    }
    
    public NodeInvokeInfo setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
        return this;
    }
    
    public Integer getInvokeTimes()
    {
        return invokeTimes;
    }
    
    public NodeInvokeInfo setInvokeTimes(Integer invokeTimes)
    {
        this.invokeTimes = invokeTimes;
        return this;
    }
    
    public Date getCreateTime()
    {
        return createTime;
    }
    
    public NodeInvokeInfo setCreateTime(Date createTime)
    {
        this.createTime = createTime;
        return this;
    }
    
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
