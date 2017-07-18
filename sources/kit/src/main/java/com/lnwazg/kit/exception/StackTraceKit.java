package com.lnwazg.kit.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceKit
{
    /**
     * 将堆栈转换成String
     * @author nan.li
     * @param e
     * @return
     */
    public static String getStackTraceString(Throwable e)
    {
        if (e != null)
        {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            String stackTrace = stringWriter.toString();
            return stackTrace;
        }
        return null;
    }
}
