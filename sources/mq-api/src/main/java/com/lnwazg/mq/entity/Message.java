package com.lnwazg.mq.entity;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.lnwazg.dbkit.anno.entity.AutoIncrement;
import com.lnwazg.dbkit.anno.entity.Id;
import com.lnwazg.dbkit.anno.entity.Index;

/**
 * MQ服务访问日志
 * @author Administrator
 * @version 2016年7月31日
 */
public class Message
{
    @Id
    @AutoIncrement
    public Integer id;
    
    @Index
    private String node;
    
    private String content;
    
    private Date createTime;
    
    //是否已经发送
    //一个消息就不应该被发送两次，因此消费了默认就应该删除掉!
    //    private Boolean sent;
    
    //要删除就直接删除，不留
    //    private Boolean deleted;
    
    public Integer getId()
    {
        return id;
    }
    
    public Message setId(Integer id)
    {
        this.id = id;
        return this;
    }
    
    public String getNode()
    {
        return node;
    }
    
    public Message setNode(String node)
    {
        this.node = node;
        return this;
    }
    
    public String getContent()
    {
        return content;
    }
    
    public Message setContent(String content)
    {
        this.content = content;
        return this;
    }
    
    public Date getCreateTime()
    {
        return createTime;
    }
    
    public Message setCreateTime(Date createTime)
    {
        this.createTime = createTime;
        return this;
    }
    
    //    public Boolean getSent()
    //    {
    //        return sent;
    //    }
    //    
    //    public Message setSent(Boolean sent)
    //    {
    //        this.sent = sent;
    //        return this;
    //    }
    
    //    public Boolean getDeleted()
    //    {
    //        return deleted;
    //    }
    //    
    //    public Message setDeleted(Boolean deleted)
    //    {
    //        this.deleted = deleted;
    //        return this;
    //    }
    
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
}
