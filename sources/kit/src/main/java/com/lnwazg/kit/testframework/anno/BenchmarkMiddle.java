package com.lnwazg.kit.testframework.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 中间级别的benchmark测试，100K次循环<br>
 * 控制台输出很耗时严重影响性能对比，因此应该通过纯计算过程去进行对比才能有所发现
 * @author nan.li
 * @version 2017年2月27日
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BenchmarkMiddle
{
}
