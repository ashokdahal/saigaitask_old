/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;

import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.container.autoregister.AbstractAutoRegister;
import org.seasar.framework.util.ClassLoaderUtil;
import org.seasar.framework.util.ClassTraversal.ClassHandler;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.ResourcesUtil;
import org.seasar.framework.util.ResourcesUtil.Resources;

/**
 * constantパッケージにあるクラスの定数をアプリケーション・スコープに登録します.
 */
public class JSTLConstantRegister extends AbstractAutoRegister
	implements ClassHandler {

	/** constantパッケージ名 */
	private String constantPackageName = "constant";

	/** 定数として登録する対象の型 */
	private static final List<Class<?>> targetTypes = new LinkedList<Class<?>>() {
		private static final long serialVersionUID = 1L;

		{
			add(String.class);
			add(Integer.class);
			add(Long.class);
			add(Enum.class);
			add(Boolean.class);
		}
	};

	/**
	 * constパッケージにあるクラスを検索して登録する.
	 * サブパッケージのクラスも検索されます.
	 */
	@Override
	public void registerAll() {
		String constantPackage = "jp.ecom_plat.saigaitask"+"."+constantPackageName;
		Resources[] resourcesTypes = ResourcesUtil.getResourcesTypes(constantPackage);
		for(Resources resources : resourcesTypes) {
			try{
				resources.forEach(this);
			} finally {
				resources.close();
			}
		}
	}

	/**
	 * 検索したクラスを登録するハンドラ(ClassHandler)
	 */
	@Override
    public void processClass(String packageName, String shortClassName) {
		register(ClassUtil.concatName(packageName, shortClassName));
	}

	/**
	 * 指定されたクラスの定数をアプリケーション・スコープに登録
	 * サブパッケージのクラスはそのまま登録する.
	 * インナークラスは非対応.(SuperClass$InnerClassで登録されます)
	 */
	protected void register(String className) {
		Class<?> clazz = ClassLoaderUtil.loadClass(
				ResourceUtil.getClassLoader(), className);
		BeanMap beanMap = new BeanMap();
		for (Field f : clazz.getFields()) {
			if (f.getDeclaringClass() != clazz) {
				continue;
			}
			if (!targetTypes.contains(f.getType())) {
				continue;
				// 再帰呼び出しは使用しない
				//register(f.getType().getName());
			}
			int mod = f.getModifiers();
			if (Modifier.isPublic(mod) && Modifier.isStatic(mod)) {
				String key = f.getName();
				Object value = FieldUtil.get(f, null);
				beanMap.put(key, value);
			}
		}
		if (beanMap.size() > 0) {
			String shortClassName = ClassUtil.getShortClassName(className);
			ServletContext application = SpringContext.getApplicationContext().getBean(ServletContext.class);
			application.setAttribute(shortClassName, beanMap);
			System.out.println("Regist Constant: "+shortClassName);
		}
	}

}
