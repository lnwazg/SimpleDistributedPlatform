package com.lnwazg.ws.sim;

/**
 * webservice业务异常
 * @author nan.li
 * @version 2014-12-5
 */
public class WsBusinessException extends RuntimeException
{
    private static final long serialVersionUID = 3037420075025115687L;
    
    public WsBusinessException(String msg)
    {
        super(msg);
    }
}
