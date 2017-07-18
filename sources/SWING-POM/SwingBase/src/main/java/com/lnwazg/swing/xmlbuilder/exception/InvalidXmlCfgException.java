package com.lnwazg.swing.xmlbuilder.exception;

/**
 * xml的配置非法
 * @author nan.li
 * @version 2015-11-1
 */
public class InvalidXmlCfgException extends RuntimeException
{
    public InvalidXmlCfgException()
    {
        super();
    }
    
    public InvalidXmlCfgException(String message)
    {
        super(message);
    }
    
    public InvalidXmlCfgException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public InvalidXmlCfgException(Throwable cause)
    {
        super(cause);
    }
    
    private static final long serialVersionUID = 4763129433440505545L;
}
