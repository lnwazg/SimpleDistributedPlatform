package com.lnwazg.kit.email;

import java.io.File;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

import com.lnwazg.kit.log.Logs;

/**
 * 邮件工具
 * @author nan.li
 * @version 2016年12月29日
 */
public class EmailKit
{
    /**
     * 发送邮件并嵌入图片
     * @author nan.li
     * @param emailConfig
     * @param mailTitle
     * @param mailContent
     * @param attachmentImagePaths
     */
    public static void sendHtmlMailWithContentEmbeddedImgs(EmailConfig emailConfig, String mailTitle, String mailContent, String... attachmentImagePaths)
    {
        File[] files = new File[attachmentImagePaths.length];
        for (int i = 0; i < files.length; i++)
        {
            files[i] = new File(attachmentImagePaths[i]);
        }
        sendHtmlMailWithContentEmbeddedImgs(emailConfig, mailTitle, mailContent, files);
    }
    
    /**
     * 发文本邮件，并且可以在邮件内容中嵌入若干张图片
     * @author nan.li
     * @param emailConfig
     * @param mailContent
     */
    public static void sendHtmlMailWithContentEmbeddedImgs(EmailConfig emailConfig, String mailTitle, String mailContent, File... attachmentImageFiles)
    {
        if (emailConfig == null)
        {
            return;
        }
        try
        {
            //发送html邮件
            HtmlEmail email = new HtmlEmail();
            email.setCharset("UTF-8");
            //邮件服务器
            email.setHostName(emailConfig.getFromHostName());
            //smtp登录鉴权，认证的用户名和密码
            email.setAuthentication(emailConfig.getFromUsername(), emailConfig.getFromPassword());
            //收件人的邮箱，以及收信人的别名
            email.addTo(emailConfig.getToAddress(), emailConfig.getToNickName());
            //发件人的邮箱，以及发件人的别名
            email.setFrom(emailConfig.getFromAddress(), emailConfig.getFromNickName());//发信者
            //发送的邮件的发送主题
            email.setSubject(mailTitle);
            StringBuilder contents = new StringBuilder();
            contents.append(mailContent);
            int num = 0;
            for (File file : attachmentImageFiles)
            {
                num++;
                contents.append(String.format("<br><br>图片%s：<br><img src=\"cid:%s\" border=\"0\"/>", num, email.embed(file)));
            }
            //邮件正文内容
            email.setHtmlMsg(contents.toString());//内容
            Logs.info("begin to send email...");
            email.send();//发送
            Logs.info("OK!");
        }
        catch (EmailException e)
        {
            Logs.error("sendMail failed!", e);
        }
    }
    
    /**
     * 发带附件的邮件
     * @author nan.li
     * @param emailConfig
     * @param mailTitle
     * @param mailContent
     * @param attachment
     */
    public static void sendHtmlMailWithAttachment(EmailConfig emailConfig, String mailTitle, String mailContent, File... attachmentFiles)
    {
        if (emailConfig == null)
        {
            return;
        }
        try
        {
            //发送html邮件
            MultiPartEmail email = new MultiPartEmail();
            email.setCharset("UTF-8");
            //邮件服务器
            email.setHostName(emailConfig.getFromHostName());
            //smtp登录鉴权，认证的用户名和密码
            email.setAuthentication(emailConfig.getFromUsername(), emailConfig.getFromPassword());
            //收件人的邮箱，以及收信人的别名
            email.addTo(emailConfig.getToAddress(), emailConfig.getToNickName());
            //发件人的邮箱，以及发件人的别名
            email.setFrom(emailConfig.getFromAddress(), emailConfig.getFromNickName());//发信者
            //发送的邮件的发送主题
            email.setSubject(mailTitle);
            //编码格式,也可设置为GBK,但是如果没有指定格式的话,那么正文会变成乱码
            //邮件正文内容
            email.setMsg(mailContent);//内容
            for (File file : attachmentFiles)
            {
                EmailAttachment attachment = new EmailAttachment();
                //                attachment.setPath("c:/234.jpg");// 本地文件  
                // attachment.setURL(new URL("http://xxx/a.gif"));//远程文件  
                attachment.setPath(file.getPath());
                attachment.setDisposition(EmailAttachment.ATTACHMENT);
                attachment.setDescription(file.getName());
                attachment.setName(file.getName());
                email.attach(attachment);
            }
            Logs.info("begin to send html mail with attachment ...");
            email.send();//发送
            Logs.info("OK!");
        }
        catch (EmailException e)
        {
            Logs.error("sendMail failed!", e);
        }
    }
}
