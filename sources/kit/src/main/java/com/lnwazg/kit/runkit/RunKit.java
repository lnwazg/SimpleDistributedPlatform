package com.lnwazg.kit.runkit;

public class RunKit
{
    public static String getClassName()
    {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        return stacks[1].getClassName();
    }
    
    public static String getMethodName()
    {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        return stacks[1].getMethodName();
    }
    
    public static int getLineNum()
    {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        return stacks[1].getLineNumber();
    }
    
    public static void main(String[] args)
    {
        System.out.println(RunKit.getClassName());
        System.out.println(RunKit.getMethodName());
        System.out.println(RunKit.getLineNum());
    }
}
