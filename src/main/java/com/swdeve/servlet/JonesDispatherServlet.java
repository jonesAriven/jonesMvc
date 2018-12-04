package com.swdeve.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.swdeve.annotation.SwdeveAutowired;
import com.swdeve.annotation.SwdeveController;
import com.swdeve.annotation.SwdeveRequestParam;
import com.swdeve.annotation.SwdeveRequestMapping;
import com.swdeve.annotation.SwdeveService;
import com.swdeve.controller.JonesController;

public class JonesDispatherServlet extends HttpServlet {
	List<String> classNames = new ArrayList<String>();
	// ioc����
	Map<String, Object> beans = new HashMap<String, Object>();
	// handleMap
	Map<String, Object> handleMaps = new HashMap<String, Object>();

	public void init(ServletConfig config) {
		// 1.ʵ����������ע����࣬basePackage��scan
		doScan("com.swdeve");
		// 2.��ɨ�������.class����ʵ����
		doInstance();
		// 3.
		doAutowried();
		// 4.
		UrlHanding();
	}

	private void UrlHanding() {
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			Object instance = entry.getValue();
			Class<?> clazz = instance.getClass();
			if (clazz.isAnnotationPresent(SwdeveController.class)) {
				SwdeveRequestMapping annotation = clazz.getAnnotation(SwdeveRequestMapping.class);
				String classPath = annotation.value();
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(SwdeveRequestMapping.class)) {
						SwdeveRequestMapping annotationTemp = method.getAnnotation(SwdeveRequestMapping.class);
						String methodPath = annotationTemp.value();
						handleMaps.put(classPath + methodPath, method);
					} else {
						continue;
					}

				}
			}
		}
	}

	private void doAutowried() {
		for (Map.Entry<String, Object> entry : beans.entrySet()) {// ����beans
			Object instance = entry.getValue();
			Class<?> clazz = instance.getClass();// ͨ��ʵ���õ�Class����
			if (clazz.isAnnotationPresent(SwdeveController.class)) {// �����������Ŀ��Ƶ�Class����
				Field[] fields = clazz.getDeclaredFields();// ͨ��calss����õ���Ա����
				for (Field field : fields) {
					if (field.isAnnotationPresent(SwdeveAutowired.class)) {
						SwdeveAutowired auto = field.getAnnotation(SwdeveAutowired.class);
						String key = auto.value();
						Object obj = beans.get(key);
						field.setAccessible(true);
						try {
							field.set(instance, obj);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						continue;
					}
				}
			} else {
				continue;
			}
		}
	}

	private void doInstance() {
		for (String className : classNames) {
			String cn = className.replace(".class", "");
			try {
				// System.out.print("-----------------------------------"+cn);
				Class<?> clazz = Class.forName(cn);// ͨ�� ����+���� �õ�Class����
				if (clazz.isAnnotationPresent(SwdeveController.class)) {// �����������Ŀ��Ƶ�Class����
					Object instance = clazz.newInstance();// ͨ��Class�����newInstance����ʵ������
					// SwdeveController annotation = clazz.getAnnotation(SwdeveController.class);
					SwdeveRequestMapping annotation = clazz.getAnnotation(SwdeveRequestMapping.class);
					String key = annotation.value();
					// System.out.println("key1=" + key);
					beans.put(key, instance);// ��ioc�����ִ��bean
				} else if (clazz.isAnnotationPresent(SwdeveRequestMapping.class)) {// �����������Ŀ��Ƶ�Class����
					Object instance = clazz.newInstance();// ͨ��Class�����newInstance����ʵ������
					SwdeveRequestMapping annotation = clazz.getAnnotation(SwdeveRequestMapping.class);
					String key = annotation.value();
					// System.out.println("key2=" + key);
					beans.put(key, instance);// ��ioc�����ִ��bean
				} else if (clazz.isAnnotationPresent(SwdeveService.class)) {// �����������Ŀ��Ƶ�Class����
					Object instance = clazz.newInstance();// ͨ��Class�����newInstance����ʵ������
					SwdeveService annotation = clazz.getAnnotation(SwdeveService.class);
					String key = annotation.value();
					// System.out.println("key3=" + key);
					beans.put(key, instance);// ��ioc�����ִ��bean
				} else {
					continue;
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void doScan(String basePackage) {
		// E:/workspace/swdeve/src/com
		// System.out.println("basePackage=" + basePackage);
		String resource = "/" + basePackage.replace(".", "/");
		/*
		 * replace�Ĳ�����char��CharSequence��������֧���ַ����滻��Ҳ֧���ַ������滻(CharSequence���ַ������е���˼,
		 * ˵����Ҳ�����ַ���)��
		 */
		// �� basePackage ������ ��\\.���滻�� ��/��
		// String resource = "/" + basePackage.replace("\\.", "/");
		/*
		 * replaceAll�Ĳ�����regex��������������ʽ���滻�����磬����ͨ��replaceAll("\\d",
		 * "*")��һ���ַ������е������ַ��������Ǻ�;
		 */
		// �� basePackage������ ��.���滻�� ��/��
		// String resource = "/" + basePackage.replaceAll("\\.", "/");
		// System.out.println("resource=" + resource);
		URL url = this.getClass().getClassLoader().getResource(resource);
		// �õ��ļ�·��
		String fileStr = url.getFile();
		// �����ļ�·���õ��ļ�
		File file = new File(fileStr);
		// �õ��ļ��µ������ļ��е�·�������ļ�·��
		String[] fileStrs = file.list();
		// �����ļ���·�����ļ�·��
		for (String path : fileStrs) {
			// System.out.println("fileStr=" + fileStr + "~path=" + path);
			File fileTemp = new File(fileStr + path);
			if (fileTemp.isDirectory()) {
				// TODO jones
				doScan(basePackage + "." + path);
				// doScan(basePackage + path);
			} else {
				classNames.add(basePackage + "." + fileTemp.getName());
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String uri = req.getRequestURI();
		String context = req.getContextPath();
		String path = uri.replace(context, "");
		// System.out.println("uri=" + uri + "~context=" + context + "~path=" + path);
		Method method = (Method) handleMaps.get(path);
		// System.out.println("handleMaps=" + handleMaps + "~method=" + method);
		// System.out.println("beans=" + beans);
		JonesController instance = (JonesController) beans.get("/" + path.split("/")[1]);
		Object[] args = hand(req, resp, method);
		try {
			method.invoke(instance, args);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ����ģʽ
	 * 
	 * @param req
	 * @param resp
	 * @param method
	 * @return
	 */
	private static Object[] hand(HttpServletRequest req, HttpServletResponse resp, Method method) {
		Class<?>[] paramClazzs = method.getParameterTypes();// �õ���ǰ��ִ�з�������Щ����
		Object[] args = new Object[paramClazzs.length];// ���ݲ�������newһ���������飬������������в�����ֵ��args��
		int args_i = 0;
		int index = 0;
		for (Class<?> paramClazz : paramClazzs) {
			if (ServletRequest.class.isAssignableFrom(paramClazz)) {
				args[args_i++] = req;
			}
			if (ServletResponse.class.isAssignableFrom(paramClazz)) {
				args[args_i++] = resp;
			}
			// ��0-1�ж���û��RequestParamע�⣬������paramClazzΪ0��1ʱ������
			// ��Ϊ2��3ʱΪ@RequestParam����Ҫ����
			Annotation[] params = method.getParameterAnnotations()[index];
			// System.out.println("params.length=" + params.length);
			if (params.length > 0) {
				for (Annotation param : params) {
					if (SwdeveRequestParam.class.isAssignableFrom(param.getClass())) {
						SwdeveRequestParam rp = (SwdeveRequestParam) param;
						// �ҵ�ע�����name��age
						// System.out.println("rp.value()=" + rp.value());
						args[args_i++] = req.getParameter(rp.value());
						// System.out.println(args[0].toString() + "~" + args[1].toString());
					}
				}
			}
			index++;
		}
		return args;
	}

}
