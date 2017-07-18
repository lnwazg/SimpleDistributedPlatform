package com.lnwazg.swing.xmlbuilder.exception;

/**
 * xmlRef节点定义非法
 * @author nan.li
 * @version 2015-11-1
 */
public class InvalidXmlRefNodeException extends RuntimeException
{
    public InvalidXmlRefNodeException()
    {
        super();
    }
    
    public InvalidXmlRefNodeException(String message)
    {
        super(message);
    }
    
    public InvalidXmlRefNodeException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public InvalidXmlRefNodeException(Throwable cause)
    {
        super(cause);
    }
    
    private static final long serialVersionUID = 4763129433440505545L;
}
