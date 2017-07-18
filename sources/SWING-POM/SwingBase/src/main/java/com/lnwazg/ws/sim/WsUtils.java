package com.lnwazg.ws.sim;

import com.google.gson.JsonObject;

/**
 * webservice工具类
 * 
 * @author  Administrator
 * @version  [版本号, 2012-12-1]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class WsUtils
{
    /** 
     * 构造错误信息
     * @param errorCode
     * @param errorMsg
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String errorMsg(int errorCode, String errorMsg)
    {
        JsonObject responseJsonObject = new JsonObject();
        errorMsg(errorCode, errorMsg, responseJsonObject);
        return responseJsonObject.toString();
    }
    
    /** 
     * 构造错误信息
     * @param errorCode
     * @param errorMsg
     * @param responseJsonObject
     * @see [类、类#方法、类#成员]
     */
    public static void errorMsg(int errorCode, String errorMsg, JsonObject responseJsonObject)
    {
        responseJsonObject.addProperty(WSConstants.RESULT_CODE, errorCode);
        responseJsonObject.addProperty(WSConstants.ERROR_MSG, errorMsg);
    }
    
}
