package com.lnwazg.myzoo.framework.ctrlbase;

import com.esotericsoftware.kryonet.Server;

public class ServerBaseController extends BaseController
{
    /**
     * 当前提供服务的server对象，单例模式的
     */
    protected Server server;
    
}
