package com.lnwazg.mq.constant;

public class Constants
{
    /**
     * 默认的消息收取数量限制
     */
    public static final int DEFAULT_HANDLE_MESSAGE_LIMIT = 800;
    
    /**
     * 默认的同步的间隔时间<br>
     * 这是一个合理的心跳间隔。因为一旦收到了一条消息之后，就会马不停蹄地收取，直到所有的消息都被收取完毕为止<br>
     * 采用pull模式要更优，因为pull模式不仅可以达到穿透的效果，更能有利于防止客户端呛到——客户端可以自适应地安排每一口的饭量，而不会因为push模式下挤压的消息太多而一下子噎死！
     */
    public static int DEFAULT_PULL_INTERVAL_SECONDS = 10;
    
    /**
     * 最大的轮询间隔时长
     */
    public static int MAX_PULL_INTERVAL_SECONDS = 60;
    
}
