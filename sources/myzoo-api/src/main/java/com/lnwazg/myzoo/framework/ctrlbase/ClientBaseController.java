package com.lnwazg.myzoo.framework.ctrlbase;

import com.esotericsoftware.kryonet.Client;

public class ClientBaseController extends BaseController
{
    /**
     * 客户端对象
     */
    protected Client client;
    
    /**
     * 客户端监听的服务器节点对象
     */
    protected String listenNode;
}
