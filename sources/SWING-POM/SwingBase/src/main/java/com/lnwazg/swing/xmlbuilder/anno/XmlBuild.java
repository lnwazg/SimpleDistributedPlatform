package com.lnwazg.swing.xmlbuilder.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 采用XML的方式自动构建Swing界面
 * @author nan.li
 * @version 2015-10-30
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlBuild
{
    String value();
}
