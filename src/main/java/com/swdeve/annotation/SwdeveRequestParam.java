package com.swdeve.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)  //��ʾע��ʹ����������
@Retention(RetentionPolicy.RUNTIME) //��ʾע����Ӧ������ʱ����
@Documented //������JAVA doc��
public @interface SwdeveRequestParam {
	String value () default "";
}