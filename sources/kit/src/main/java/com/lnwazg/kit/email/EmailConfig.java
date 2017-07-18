package com.lnwazg.kit.email;

/**
 * 邮箱的配置信息
 * @author nan.li
 * @version 2016年12月29日
 */
public class EmailConfig
{
    private String fromUsername;
    
    private String fromPassword;
    
    private String fromHostName;
    
    private String fromAddress;
    
    private String fromNickName;
    
    private String toAddress;
    
    private String toNickName;
    
    public String getFromUsername()
    {
        return fromUsername;
    }
    
    public void setFromUsername(String fromUsername)
    {
        this.fromUsername = fromUsername;
    }
    
    public String getFromPassword()
    {
        return fromPassword;
    }
    
    public void setFromPassword(String fromPassword)
    {
        this.fromPassword = fromPassword;
    }
    
    public String getFromHostName()
    {
        return fromHostName;
    }
    
    public void setFromHostName(String fromHostName)
    {
        this.fromHostName = fromHostName;
    }
    
    public String getFromAddress()
    {
        return fromAddress;
    }
    
    public void setFromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress;
    }
    
    public String getToAddress()
    {
        return toAddress;
    }
    
    public void setToAddress(String toAddress)
    {
        this.toAddress = toAddress;
    }
    
    public String getToNickName()
    {
        return toNickName;
    }
    
    public void setToNickName(String toNickName)
    {
        this.toNickName = toNickName;
    }
    
    public String getFromNickName()
    {
        return fromNickName;
    }
    
    public void setFromNickName(String fromNickName)
    {
        this.fromNickName = fromNickName;
    }
    
    public EmailConfig(String fromUsername, String fromPassword, String fromHostName, String fromAddress, String fromNickName, String toAddress,
        String toNickName)
    {
        super();
        this.fromUsername = fromUsername;
        this.fromPassword = fromPassword;
        this.fromHostName = fromHostName;
        this.fromAddress = fromAddress;
        this.fromNickName = fromNickName;
        this.toAddress = toAddress;
        this.toNickName = toNickName;
    }
    
    @Override
    public String toString()
    {
        return "EmailConfig [fromUsername=" + fromUsername + ", fromPassword=" + fromPassword + ", fromHostName=" + fromHostName + ", fromAddress="
            + fromAddress + ", fromNickName=" + fromNickName + ", toAddress=" + toAddress + ", toNickName=" + toNickName + "]";
    }
    
}
