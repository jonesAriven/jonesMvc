package com.swdeve.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})  //表示注解使用在方法上
@Retention(RetentionPolicy.RUNTIME) //表示注解在应用启动时加载
@Documented //包含在JAVA doc中
public @interface SwdeveRequestMapping {
	String value () default "";
}
