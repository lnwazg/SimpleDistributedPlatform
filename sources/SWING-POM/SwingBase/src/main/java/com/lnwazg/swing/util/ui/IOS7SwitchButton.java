package com.lnwazg.swing.util.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.swing.ui.comp.NonBorderButton;

/**
 * IOS7开关按钮
 * @author Administrator
 * @version 2016年2月14日
 */
public class IOS7SwitchButton extends NonBorderButton
{
    private static final long serialVersionUID = -7199264704343599325L;
    
    /**
     * 打开图标
     */
    private Icon onIcon = new ImageIcon(getClass().getClassLoader().getResource("common/default/icon/ios7_toggle_on.png"));
    
    /**
     * 关闭图标
     */
    private Icon offIcon = new ImageIcon(getClass().getClassLoader().getResource("common/default/icon/ios7_toggle_off.png"));
    
    /**
     * 打开图标被触发时候的回调函数
     */
    private OnCallBack onCallBack;
    
    /**
     * 关闭图标被触发时候的回调函数
     */
    private OffCallBack offCallBack;
    
    /**
     * 按钮的当前状态<br>
     * true表示开启，false表示关闭
     */
    private boolean switchStatus;
    
    /**
     * 构造函数 
     */
    public IOS7SwitchButton()
    {
        super();
        doInitConfiguration();
    }
    
    /**
     * 初始化配置信息
     * @author nan.li
     */
    private void doInitConfiguration()
    {
        //默认状态就是打开的图标
        this.setIcon(onIcon);
        switchStatus = true;
        
        //启动时加载自动切换线路的信息。
        //如果初始化的状态和配置的状态不一致，则需要手动触发一次！
        //为按钮注册监听器
        this.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //当该按钮被点击的时候，进行图标切换
                IOS7SwitchButton source = (IOS7SwitchButton)(e.getSource());
                Icon currentIcon = source.getIcon();
                //如果之前是开着的，那么要将其切换成关闭状态
                if (currentIcon == onIcon)
                {
                    source.setIcon(offIcon);
                    switchStatus = false;
                    if (offCallBack != null)
                    {
                        ExecMgr.cachedExec.execute(new Runnable()
                        {
                            public void run()
                            {
                                offCallBack.call();
                            }
                        });
                    }
                }
                else
                {
                    //反之亦然
                    source.setIcon(onIcon);
                    switchStatus = true;
                    if (onCallBack != null)
                    {
                        ExecMgr.cachedExec.execute(new Runnable()
                        {
                            public void run()
                            {
                                onCallBack.call();
                            }
                        });
                    }
                }
            }
        });
    }
    
    /**
     * 主动设置按钮的状态
     * @author Administrator
     * @param b
     */
    public void setStatus(boolean status)
    {
        //如果需要设置成打开
        if (status)
        {
            //如果之前不是开状态
            if (this.getIcon() != onIcon)
            {
                //那么需要手动点击一次按钮
                ExecMgr.guiExec.execute(new Runnable()
                {
                    public void run()
                    {
                        doClick();
                    }
                });
            }
        }
        else
        {
            //反之亦然
            //如果之前不是关状态，也要手动点击按钮触发一次
            if (this.getIcon() != offIcon)
            {
                ExecMgr.guiExec.execute(new Runnable()
                {
                    public void run()
                    {
                        doClick();
                    }
                });
            }
        }
    }
    
    /**
     * 设置按钮被开启时候的监听器
     * @author nan.li
     * @param onCallBack
     */
    public void setOnCallback(OnCallBack onCallBack)
    {
        this.onCallBack = onCallBack;
    }
    
    /**
     * 设置按钮被关闭时候的监听器
     * @author nan.li
     * @param offCallBack
     */
    public void setOffCallback(OffCallBack offCallBack)
    {
        this.offCallBack = offCallBack;
    }
    
    /**
     * 调节到打开状态的时候的回调函数
     * @author Administrator
     * @version 2016年2月14日
     */
    public static interface OnCallBack
    {
        void call();
    }
    
    /**
     * 调节到关闭状态的时候的回调函数
     * @author Administrator
     * @version 2016年2月14日
     */
    public static interface OffCallBack
    {
        void call();
    }
    
    public boolean isSwitchStatus()
    {
        return switchStatus;
    }
    
    public void setSwitchStatus(boolean switchStatus)
    {
        this.switchStatus = switchStatus;
    }
    
}
