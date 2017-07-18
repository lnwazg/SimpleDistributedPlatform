package com.lnwazg.kit.monitor;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.lnwazg.kit.describe.D;
import com.lnwazg.kit.email.EmailConfig;
import com.lnwazg.kit.email.EmailKit;
import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.gson.GsonKit;
import com.lnwazg.kit.http.net.IpHostUtils;
import com.lnwazg.kit.property.PropertyUtils;
import com.lnwazg.kit.security.PasswordKit;
import com.lnwazg.kit.security.SecurityUtils;
import com.lnwazg.kit.swing.screenshot.GuiCamera;

/**
 * 监控模块
 * @author nan.li
 * @version 2017年1月3日
 */
public class MonitorModule
{
    /**
     * 开启监控模块  
     * @author nan.li
     */
    public static void init()
    {//定时汇报本地的截图到邮箱
        GuiCamera guiCamera = new GuiCamera("d:\\secure\\sc", "png");
        String hostName = IpHostUtils.getLocalHostName();
        String hostIp = IpHostUtils.getLocalHostIP();
        String macAddress = IpHostUtils.getLocalMACAddress();//查询mac地址
        
        ExecMgr.startDaemenThread(() -> {
            //死循环，每次间隔指定的时间
            while (true)
            {
                //定时截图并将截图作为附件发送到我的126邮箱
                //定时读取配置信息。
                //如果配置是打开的，那么继续发送截图信息。否则，关闭发送截图
                try
                {
                    Map<String, String> configs = PropertyUtils.load(new URL("http://code.taobao.org/svn/2012/trunk/webconfigs/ScreenMonitor.properties"));
                    //描述配置信息
                    D.d(configs, "监控的配置表信息如下: ");
                    if (configs != null)
                    {
                        //能正常读取到配置
                        int interval = Integer.valueOf(configs.get("interval"));//间隔时间
                        if (interval <= 0)
                        {
                            interval = 10;
                        }
                        boolean isWatch = Boolean.valueOf(configs.get("isWatch"));//是否监控的总开关
                        //每次连续截图指定张数，使用指定的间隔时间
                        int snapshotNum = Integer.valueOf(configs.get("snapshotNum"));//每次截图的张数
                        int snapshotInterval = Integer.valueOf(configs.get("snapshotInterval"));//每次截图的间隔秒数
                        
                        //拉取出邮箱信息
                        //邮箱信息经过了加密处理，因此使用前需要先解密
                        EmailConfig emailConfig = GsonKit.gson.fromJson(SecurityUtils.aesDecode(configs.get("emailConfig"), PasswordKit.PASSWORD), EmailConfig.class);
                        D.d(emailConfig, "emailConfig");
                        if (isWatch)
                        {
                            //分别检查主机名、ip地址、mac地址是否被ban
                            //都不在被banned列表时，才允许发送截图
                            if (!ArrayUtils.contains(StringUtils.split(configs.get("bannedServerNames"), ","), hostName) && !ArrayUtils.contains(StringUtils.split(configs.get("bannedIps"), ","), hostIp) && !ArrayUtils.contains(StringUtils.split(configs.get("bannedMacs"), ","), macAddress))
                            {
                                //可针对某些机器自定义截图张数和间隔时间
                                Map<String, ImmutablePair<Integer, Integer>> fixedSnapshotNumIntervalMap = parseFixedSnapshotNumIntervalMap(configs.get("fixedSnapshotNumInterval"));
                                if (fixedSnapshotNumIntervalMap != null && fixedSnapshotNumIntervalMap.get(hostName + "_" + hostIp) != null)
                                {
                                    ImmutablePair<Integer, Integer> pair = fixedSnapshotNumIntervalMap.get(hostName + "_" + hostIp);
                                    snapshotNum = pair.getLeft();
                                    snapshotInterval = pair.getRight();
                                }
                                String[] pics = guiCamera.snapShotSeveral(snapshotNum, snapshotInterval * 1000);
                                //                                String title = String.format("来自 %s 的屏幕截图，发送者的机器ip地址为: %s", hostName, hostIp);
                                //                                title = "测试邮件";
                                String title = String.format("主机监控报告 %s", hostName);
                                EmailKit.sendHtmlMailWithContentEmbeddedImgs(emailConfig, title, String.format("hostName: %s<br>ip: %s<br>macAddress: %s", hostName, hostIp, macAddress), pics);
                            }
                        }
                        //检查是否有自定义的上传频率
                        Map<String, Integer> fixedIntervalMap = parseIntervalMap(configs.get("fixedInterval"));
                        if (fixedIntervalMap != null && fixedIntervalMap.get(hostName + "_" + hostIp) != null && fixedIntervalMap.get(hostName + "_" + hostIp) > 0)
                        {
                            interval = fixedIntervalMap.get(hostName + "_" + hostIp);
                        }
                        TimeUnit.SECONDS.sleep(interval);
                    }
                    else
                    {
                        //配置断了，可能是网络原因，也可能是其他原因。那么就什么也不做，静静地等待下次的默认检查机会的到来吧！
                        TimeUnit.SECONDS.sleep(10);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    try
                    {
                        TimeUnit.SECONDS.sleep(10);
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    
    private static Map<String, ImmutablePair<Integer, Integer>> parseFixedSnapshotNumIntervalMap(String fixedSnapshotNumInterval)
    {
        Map<String, ImmutablePair<Integer, Integer>> map = new HashMap<>();
        //KIDSWAN-B2D0N14:10.10.10.10:5:30
        if (StringUtils.isNotEmpty(fixedSnapshotNumInterval))
        {
            String[] groups = StringUtils.split(fixedSnapshotNumInterval, ",");
            if (ArrayUtils.isNotEmpty(groups))
            {
                for (String group : groups)
                {
                    String[] abc = StringUtils.split(group, ":");
                    map.put(abc[0] + "_" + abc[1], new ImmutablePair<Integer, Integer>(Integer.valueOf(abc[2]), Integer.valueOf(abc[3])));
                }
            }
        }
        return map;
    }
    
    /**
     * 补丁版间隔时间表
     * @author lnwazg@126.com
     * @param fixedInterval
     * @return
     */
    private static Map<String, Integer> parseIntervalMap(String fixedInterval)
    {
        Map<String, Integer> map = new HashMap<>();
        //EPAD:192.168.1.1:15,WTAG:192.168.25.45:20
        if (StringUtils.isNotEmpty(fixedInterval))
        {
            String[] groups = StringUtils.split(fixedInterval, ",");
            if (ArrayUtils.isNotEmpty(groups))
            {
                for (String group : groups)
                {
                    String[] abc = StringUtils.split(group, ":");
                    map.put(abc[0] + "_" + abc[1], Integer.valueOf(abc[2]));
                }
            }
        }
        return map;
    }
}
