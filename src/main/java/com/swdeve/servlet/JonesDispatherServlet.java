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
	// ioc容器
	Map<String, Object> beans = new HashMap<String, Object>();
	// handleMap
	Map<String, Object> handleMaps = new HashMap<String, Object>();

	public void init(ServletConfig config) {
		// 1.实例化所有有注解的类，basePackage，scan
		doScan("com.swdeve");
		// 2.把扫描出来的.class进行实例化
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
		for (Map.Entry<String, Object> entry : beans.entrySet()) {// 遍历beans
			Object instance = entry.getValue();
			Class<?> clazz = instance.getClass();// 通过实例得到Class对象
			if (clazz.isAnnotationPresent(SwdeveController.class)) {// 被遍历出来的控制的Class对象
				Field[] fields = clazz.getDeclaredFields();// 通过calss对象得到成员变量
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
				Class<?> clazz = Class.forName(cn);// 通过 包名+类名 得到Class对象
				if (clazz.isAnnotationPresent(SwdeveController.class)) {// 被遍历出来的控制的Class对象
					Object instance = clazz.newInstance();// 通过Class对象的newInstance方法实例化类
					// SwdeveController annotation = clazz.getAnnotation(SwdeveController.class);
					SwdeveRequestMapping annotation = clazz.getAnnotation(SwdeveRequestMapping.class);
					String key = annotation.value();
					// System.out.println("key1=" + key);
					beans.put(key, instance);// 往ioc容器种存放bean
				} else if (clazz.isAnnotationPresent(SwdeveRequestMapping.class)) {// 被遍历出来的控制的Class对象
					Object instance = clazz.newInstance();// 通过Class对象的newInstance方法实例化类
					SwdeveRequestMapping annotation = clazz.getAnnotation(SwdeveRequestMapping.class);
					String key = annotation.value();
					// System.out.println("key2=" + key);
					beans.put(key, instance);// 往ioc容器种存放bean
				} else if (clazz.isAnnotationPresent(SwdeveService.class)) {// 被遍历出来的控制的Class对象
					Object instance = clazz.newInstance();// 通过Class对象的newInstance方法实例化类
					SwdeveService annotation = clazz.getAnnotation(SwdeveService.class);
					String key = annotation.value();
					// System.out.println("key3=" + key);
					beans.put(key, instance);// 往ioc容器种存放bean
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
		 * replace的参数是char和CharSequence，即可以支持字符的替换，也支持字符串的替换(CharSequence即字符串序列的意思,
		 * 说白了也就是字符串)；
		 */
		// 将 basePackage 中所有 “\\.”替换成 “/”
		// String resource = "/" + basePackage.replace("\\.", "/");
		/*
		 * replaceAll的参数是regex，即基于正则表达式的替换，比如，可以通过replaceAll("\\d",
		 * "*")把一个字符串所有的数字字符都换成星号;
		 */
		// 将 basePackage中所有 “.”替换成 “/”
		// String resource = "/" + basePackage.replaceAll("\\.", "/");
		// System.out.println("resource=" + resource);
		URL url = this.getClass().getClassLoader().getResource(resource);
		// 得到文件路径
		String fileStr = url.getFile();
		// 根据文件路径得到文件
		File file = new File(fileStr);
		// 得到文件下的所有文件夹的路径和子文件路径
		String[] fileStrs = file.list();
		// 遍历文件夹路径和文件路径
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
	 * 策略模式
	 * 
	 * @param req
	 * @param resp
	 * @param method
	 * @return
	 */
	private static Object[] hand(HttpServletRequest req, HttpServletResponse resp, Method method) {
		Class<?>[] paramClazzs = method.getParameterTypes();// 拿到当前待执行方法有哪些参数
		Object[] args = new Object[paramClazzs.length];// 根据参数个数new一个参数数组，将方法里的所有参数赋值到args来
		int args_i = 0;
		int index = 0;
		for (Class<?> paramClazz : paramClazzs) {
			if (ServletRequest.class.isAssignableFrom(paramClazz)) {
				args[args_i++] = req;
			}
			if (ServletResponse.class.isAssignableFrom(paramClazz)) {
				args[args_i++] = resp;
			}
			// 从0-1判断有没有RequestParam注解，很明显paramClazz为0和1时，不是
			// 当为2和3时为@RequestParam，需要解析
			Annotation[] params = method.getParameterAnnotations()[index];
			// System.out.println("params.length=" + params.length);
			if (params.length > 0) {
				for (Annotation param : params) {
					if (SwdeveRequestParam.class.isAssignableFrom(param.getClass())) {
						SwdeveRequestParam rp = (SwdeveRequestParam) param;
						// 找到注解里的name和age
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
