package com.swdeve.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)  //��ʾע��ʹ��������ĳ�Ա������
@Retention(RetentionPolicy.RUNTIME) //��ʾע����Ӧ������ʱ����
@Documented //������JAVA doc��
public @interface SwdeveAutowired {
	String value () default "";
}
