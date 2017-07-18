package com.lnwazg.ui;

import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTextPane;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.random.RandomStrUtils;
import com.lnwazg.mq.framework.MQConfig;
import com.lnwazg.mq.util.MQHelper;
import com.lnwazg.myzoo.framework.MyZooClient;
import com.lnwazg.swing.util.WinMgr;
import com.lnwazg.swing.xmlbuilder.XmlJFrame;
import com.lnwazg.swing.xmlbuilder.anno.XmlBuild;

/**
 * DataNode的主窗体<br>
 * 工人节点，用于接收任务、运行可执行jar包、上报最后的计算结果等等
 * @author nan.li
 * @version 2017年7月7日
 */
@XmlBuild("DataNode.xml")
public class MainFrame extends XmlJFrame
{
    private static final long serialVersionUID = 416736654918898426L;
    
    private JTextPane logScreen;
    
    private JLabel statusLabel;
    
    @Override
    public void afterUIBind()
    {
        /**
         * 当DataNode收到来自NameNode的消息时，首先确认是一个xxxTask消息，然后解包消息体，去对应的url从网络加载jar包，直接从网络加载执行即可
         * 
         * 可执行jar包被拉起之后，立即执行exec()方法。
         * exec方法可以迅速执行结束，也可以一直驻留执行。需要上报的时候，将其submit()上报到MQ上即可（可以将上报mq的模块做成公用的jar包，就可以大大将其任务包的大小）
         * 工人调用end()方法，发送一条消息，表示自己已经上报完毕。
         * 
         * 当可执行jar包在NameNode被拉起的时候，也是执行exec()方法。但是其更重要的是有一个receive()方法，可以用于接收工人的上报结果。
         * 最终，将所有的结果汇总，生成一份报告即可！
         * 当发现所有的客户端都调用了end()方法之后，那么说明所有的已经上报完毕了，就可以停止服务端的任务了！
         */
        
        //        配置swing日志窗口
        logScreen.setContentType("text/html");
        Logs.addLogDest(logScreen);
        
        WinMgr.reg(this);
        
        //初始化MQ客户端
        initMqClient();
    }
    
    /**
     * 显示客户端的执行状态
     * @author nan.li
     * @param text
     */
    public void showStatus(String text)
    {
        ExecMgr.guiExec.execute(() -> {
            statusLabel.setText(text);
        });
    }
    
    /**
     * 我的MQ邮箱
     */
    public String myselfAddress = String.format("DataNode-%s", RandomStrUtils.generateRandomString(8));
    
    /**
     * 目标MQ邮箱
     */
    String targetAddress = "NameNode";
    
    /**
     * 初始化MQ客户端
     * @author nan.li
     */
    private void initMqClient()
    {
        boolean success = MyZooClient.initDefaultConfig();
        if (success)
        {
            Map<String, String> m = MyZooClient.queryServiceConfigByNodeGroupNameThenChooseOne("mq");
            if (m == null)
            {
                Logs.e("MyZooKeeper查询MQ失败！");
                return;
            }
            //监听自己的邮箱的邮件
            boolean result = MQConfig.initMq(m.get("server"), Integer.valueOf(m.get("port")), myselfAddress, myselfAddress);
            if (result)
            {
                //需要将自己上报上去，告诉NameNode： 本DataNode已经上线了！
                MQHelper.sendAsyncMsg(targetAddress, "/inbox/clientOnline", "clientName", myselfAddress);
                Logs.i("MQ初始化成功！");
            }
        }
        else
        {
            Logs.e("MyZooKeeper集群初始化失败！");
        }
    }
    
    /**
     * 任务执行完毕了
     * @author nan.li
     */
    public void endTask()
    {
        MQHelper.sendAsyncMsg(targetAddress, "/inbox/end", "clientName", myselfAddress);
    }

    public void report(Object... keyvalues)
    {
        MQHelper.sendAsyncMsg(targetAddress, "/inbox/reportKeyValues", keyvalues);
    }
    
}
