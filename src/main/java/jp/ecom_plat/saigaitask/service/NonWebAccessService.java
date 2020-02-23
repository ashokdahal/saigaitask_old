package jp.ecom_plat.saigaitask.service;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;

/**
 * Web からのリクエスト以外で、loginDataDto を参照する Service を利用するためのクラス
 */
@org.springframework.stereotype.Service
public class NonWebAccessService {

    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

	/**
	 * Service クラスのインスタンスを生成する
	 * 
	 * @param clazz 生成対象のサービスクラス
	 * @param classSet （生成対象のサービスクラスより間接的に参照される） loginDataDto を参照しているクラスの Set
	 * @param loginDataDto Service クラスが参照する LoginDataDto オブジェクト
	 * @return
	 * @throws Exception
	 */
	public Object createService(Class<?> clazz, Set<Class<?>> classSet, LoginDataDto loginDataDto) throws Exception {
		try {
			Object service = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				// "loginDataDto" フィールドを設定する
				if ("loginDataDto".equals(field.getName())) {
					field.setAccessible(true);
					field.set(service, loginDataDto);
				}
				// @Resource アノテーションが付いているフィールドに Service クラスのインスタンスを設定する
				else if (field.getAnnotation(Resource.class) != null) {
					Class<?> fieldClass = field.getType();
					Object obj;
					// サービスクラスが loginDataDto を参照している場合は、当処理を再帰的に呼び出す
					if (classSet.contains(fieldClass)) {
						obj = createService(fieldClass, classSet, loginDataDto);
					}
					//  loginDataDto を参照していない場合
					else {
						obj = autowireCapableBeanFactory.createBean(fieldClass);
					}
					field.setAccessible(true);
					field.set(service, obj);
				}
			}
			return service;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * loginDataDto を参照しているクラスの Set に漏れがあった場合に、スタックトレースより関連のクラスを抽出し、classSet に追加する
	 * 
	 * @param ex Web からのリクエスト以外で loginDataDto を参照したことで発生した Exception
	 * @param classSet （生成対象のサービスクラスより間接的に参照される） loginDataDto を参照しているクラスの Set
	 * @throws Exception
	 */
	public void updateClassSet(Exception ex, Class<?> clazz, Set<Class<?>> classSet) throws Exception {
		boolean found = false;
		for (StackTraceElement elm : ex.getStackTrace()) {
			try {
				String className = elm.getClassName();
				Class<?> cls = Class.forName(className);
				if (cls == clazz)
					break;
				if (className.startsWith("jp.ecom_plat.saigaitask.service.") && className.indexOf("$") < 0 &&
						!classSet.contains(cls)) {
					found = true;
					classSet.add(cls);
				}
			} catch (ClassNotFoundException e) {
				throw e;
			}
		}
		if (!found)
			throw ex;
	}

	/**
	 * loginDataDto を参照しているクラスの Set を更新した後に、対象 Service クラスのインスタンスを生成する
	 * 
	 * @param clazz 生成対象のサービスクラス
	 * @param classSet （生成対象のサービスクラスより間接的に参照される） loginDataDto を参照しているクラスの Set
	 * @param loginDataDto 対象の Service クラスが参照する LoginDataDto オブジェクト
	 * @param ex Web からのリクエスト以外で loginDataDto を参照したことで発生した Exception
	 * @return
	 * @throws Exception
	 */
	public Object createService(Class<?> clazz, Set<Class<?>> classSet, LoginDataDto loginDataDto, Exception ex) throws Exception {
		updateClassSet(ex, clazz, classSet);
		return createService(clazz, classSet, loginDataDto);
	}
}
