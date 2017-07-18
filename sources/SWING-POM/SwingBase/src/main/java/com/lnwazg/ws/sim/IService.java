package com.lnwazg.ws.sim;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 所有的webservice必定要实现的接口
 * @author  Administrator
 * @version  [版本号, 2012-11-27]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface IService
{
    Gson gson = new Gson();
    
    /**
     * 新版本的接口，抛弃了requestJsonObject参数 by lnwazg @2013-12-30
     * 之所以抛弃requestJsonObject参数，是因为其所能干的事情（解析输入参数），已经全部由注解处理器完成
     * 因此，去掉该请求参数，让该通用请求接口更加简洁实用
     * @param responseJsonObject
     * @throws Exception
     */
    void execute(JsonObject responseJsonObject)
        throws Exception;
}
