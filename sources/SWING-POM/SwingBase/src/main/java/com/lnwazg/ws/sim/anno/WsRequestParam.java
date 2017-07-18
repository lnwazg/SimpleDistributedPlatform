package com.lnwazg.ws.sim.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解需要传入的元素的字符串数组
 * 一个参数处理器
 * 
 * @author  Administrator
 * @version  [版本号, 2012-11-29]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WsRequestParam
{
    /**
     * 该字段的参数名
     * 默认情况下无须设置，那么这个值就采用默认值（这个参数字段的名称）
     * 但是这个字段还是有保留的必要的（适用于保留字冲突导致的json序列化失败的场景，这种情况下就必须用到别名了！），虽说实际使用率可能会很低！
     * @return
     */
    String value() default "";
    
    /**
     * 该字段的文档描述
     * @return
     */
    String desc() default "";
    
    /**
     * 该参数是否强制需要
     * @author nan.li
     * @return
     */
    boolean required() default true;
}
