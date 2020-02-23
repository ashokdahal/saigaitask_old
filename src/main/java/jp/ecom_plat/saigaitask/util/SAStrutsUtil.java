/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.util.ClassUtil;

/**
 * SAStrutsのユーティリティクラスです.
 */
public class SAStrutsUtil {

	protected static final Logger logger = Logger.getLogger(SAStrutsUtil.class);
	protected static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	/* *
	 * BeanWrapperでラップされているBeanを取得します.
	 * @param beanWrapper BeanWrapper
	 * @return ラップされているBean
	 * /
	public static Object getBean(BeanWrapper beanWrapper) {
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(BeanWrapper.class);
		Field field = beanDesc.getField("bean");
		field.setAccessible(true);
		Object bean = FieldUtil.get(field, beanWrapper);
		return bean;
	}*/

	/**
	 * エンティティクラスから名前クラスを取得します.
	 * @param entity エンティティクラス
	 * @return 名前クラス
	 */
	public static Class<?> getEntityNamesClass(Class<?> entity) {
		try {
			String classShortName = entity.getSimpleName()+"Names";
			String className = ClassUtil.concatName("jp.ecom_plat.saigaitask.entity.names", classShortName);
			return ClassUtil.forName(className);
		} catch(Exception e) {
			logger.fatal(e);
		}
		return null;
	}

	/**
	 * エンティティクラスからインナークラスの方の名前クラスを取得します.
	 * @param entity エンティティクラス
	 * @return インナークラスの名前クラス
	 */
	public static Class<?> getEntityInnerNamesClass(Class<?> entity) {
		try {
			//Class<?> joinClass = ClassUtil.forName(rootpkg+".db."+entity.getSimpleName());
			String innerClassShortName = entity.getSimpleName()+"Names$_"+entity.getSimpleName()+"Names";
			String className = ClassUtil.concatName("jp.ecom_plat.saigaitask.entity.names", innerClassShortName);
			return ClassUtil.forName(className);
			//return namesClass;
		} catch(Exception e) {
			logger.fatal(e);
		}
		return null;
	}
	/**
	 * 指定したエンティティのフィールド名に対応する名前クラスを呼び出します.
	 * @param entity エンティティ
	 * @param namesClass エンティティの名前クラス
	 * @param names インナークラスの名前クラス
	 * @param fieldName エンティティのフィールド名
	 * @return インナークラスの名前クラス
	 */
	public static PropertyName<?> getPropertyName(Class<?> entity, Class<?> namesClass, PropertyName<?> names, String fieldName) {
		// get field
		Field field = null;
		try {
			field = entity.getField(fieldName);
		} catch(NoSuchFieldException e) {
			logger.trace("Entity "+entity.getSimpleName()+" not have "+fieldName);
			return null;
		}

		// check names class
		if(!namesClass.getSimpleName().equals(names.getClass().getSimpleName())) {
			logger.fatal(MessageFormat.format("Name class expected {0} ,but in fact {1}.", namesClass.getSimpleName(), names.getClass().getSimpleName()));
			return null;
		}

		// get fieldName names class
		try {
			//Class<?> namesClass = getEntityInnerNamesClass(entity);
			Method namesMethod = ClassUtil.getMethod(namesClass, field.getName(), null);
			return (PropertyName<?>) namesMethod.invoke(names, (Object [])null);
		} catch (Exception e) {
			logger.fatal(e);
			return null;
		}
	}

	/**
	 * @param val 値
	 * @param defaultValue デフォルト値
	 * @return Null の場合はデフォルト値を返します。
	 */
	public static Boolean defalutValue(Boolean val, Boolean defaultValue) {
		return val==null ? defaultValue : val;
	}
}
