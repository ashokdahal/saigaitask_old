/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.JoinColumn;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.extension.jdbc.service.S2AbstractService;
import org.seasar.extension.jdbc.where.SimpleWhere;
import org.seasar.framework.exception.SQLRuntimeException;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.service.db.MultilangInfoService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.SAStrutsUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * DB操作系サービスクラスの基底クラスです.
 * @param <ENTITY> エンティティクラス
 * 
 * アノテーション@Transactional
 * noRollbackFor=NullPointerException.class FacebookActionでFacebookMaster未設定時にNPEが発生するのでロールバックしないようにする
 */
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
public abstract class AbstractService<ENTITY> extends S2AbstractService<ENTITY> {

	protected Logger logger = Logger.getLogger(entityClass);
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	/** ログイン情報 */
	@Resource protected LoginDataDto loginDataDto;

	protected void setSchemaSearchPath() {
		String langCode = "en";
    	SaigaiTaskDBLang sessionLang = SaigaiTaskLangUtils.getSessionLang();
    	if(sessionLang!=null) langCode = sessionLang.getLangCode();
    	
		// スキーマの切り替え
		MultilangInfoService multilangInfoService = SpringContext.getApplicationContext().getBean(MultilangInfoService.class);
		multilangInfoService.setSchema(langCode);
	}

	@Override
	protected AutoSelect<ENTITY> select() {
		// Spring MVC interceptor や、Spring AOP Pointcutだと search_pathをうまく設定できないので、
		// select() をオーバライドしてここで search_pathを確実に設定する
		setSchemaSearchPath();
		return super.select();
	}
	
    /**
     * エンティティを挿入します。
     * 
     * @param entity
     *            エンティティ
     * @return 更新した行数
     */
    public int insert(ENTITY entity) {
        return jdbcManager.insert(entity).execute();
    }

	/**
     * @param orderByItems order by 指定
     * @return エンティティリスト
     */
    public List<ENTITY> findAll(OrderByItem... orderByItems) {
    	return select().orderBy(orderByItems).getResultList();
    }

    /**
     * 合計値を返す。
     * @param list
     * @return 被災状況土木被害データ合計値
     */
    public ENTITY getTotal(List<ENTITY> list) {
    	try {
        	ENTITY data = entityClass.newInstance();
        	Field[] fields = entityClass.getFields();
        	for (Field field : fields) {
        		if (field.getType() == Integer.class) {
        			field.set(data, Integer.valueOf(0));
        		}
        	}

        	for (ENTITY one : list) {
            	for (Field field : fields) {
            		if (field.getType() == Integer.class && field.get(one) != null) {
            			field.set(data, (Integer)field.get(data)+(Integer)field.get(one));
            		}
            	}
        	}
        	return data;
    	} catch (IllegalAccessException e) {
    		logger.error(e.getMessage(), e);
		} catch (InstantiationException e) {
    		logger.error(e.getMessage(), e);
		}
    	return null;
    }

    /**
     * 削除フラグを true にセットする
     * @param entity
     * @return 行数
     * @throws Exception
     */
    public int deleteLogically(ENTITY entity) throws Exception {
    	Class<?> c = entity.getClass();
    	Field f = c.getField("deleted");
    	f.set(entity, Boolean.valueOf(true));
    	return jdbcManager
				.update(entity)
				.includes("deleted")
				.execute();
    }

    /**
     * 「カラム名 = 値」の条件に一致するレコードの削除フラグをセットする
     * @param name
     * @param value
     * @throws Exception
     */
    public void deleteLogicallyBySimpleWhere(CharSequence name, Object value) throws Exception {
		List<ENTITY> entities = select()
				.where(new SimpleWhere().eq(name, value))
				.getResultList();
		for (ENTITY entity : entities) {
			deleteLogically(entity);
		}
     }

    /**
     * 削除フラグが  false である全部のレコードを抽出する
     * @return 結果リスト
     */
    public List<ENTITY> findAllExceptDeleted() {
        return selectNotDeleted()
        		.getResultList();
    }

    /**
     * 削除フラグが  false であるレコードを select する
     * 注意 ： 呼び出し元がwhere句を持つ場合、上書きしてしまう為使用しない事
     * @return AutoSelect オブジェクト
     */
    protected AutoSelect<ENTITY> selectNotDeleted() {
        return select()
        		.where("not deleted");
    }

	/**
	 * 自治体IDで絞込みができるまで、再帰的にjoinする.
	 * @param entity エンティティクラス
	 * @param names 名前クラス
	 * @param select AutoSelect
	 * @param where SimpleWhere
	 * @param localgovinfoid 自治体ID
	 * @param deleted 削除フラグ
	 * @return localgovinfoid による絞込みができたか
	 */
	protected boolean selectByLocalgovinfoid(Class<?> entity, PropertyName<?> parent, AutoSelect<ENTITY> select, SimpleWhere where, Long localgovinfoid, boolean deleted) {

		// check entity and parent names
		if(!("_"+entity.getSimpleName()+"Names").equals(parent.getClass().getSimpleName())) {
			logger.fatal(lang.__("Entity ({0}) and name class ({1}) not match.", entity.getSimpleName(), parent.getClass().getSimpleName()));
			return false;
		}

		// get Names class
		Class<?> namesClass = SAStrutsUtil.getEntityInnerNamesClass(entity);

		// where deleted
		if(!deleted) {
			PropertyName<?> deletedNames = SAStrutsUtil.getPropertyName(entity, namesClass, parent, "deleted");
			if(deletedNames!=null) {
				where.ne(deletedNames, true);
				logger.debug("where not equals true: "+deletedNames.toString());
			}
		}
		// where localgovinfoid if exists
		PropertyName<?> localgovinfoidNames = SAStrutsUtil.getPropertyName(entity, namesClass, parent, "localgovinfoid");
		if(localgovinfoidNames!=null) {
			where.eq(localgovinfoidNames, localgovinfoid);
			logger.debug("where equals: "+localgovinfoidNames.toString()+"="+localgovinfoid);
			// localgovinfoid で絞り込めたら、これ以上 join は必要ない
			return true;
		}
		if(entity==LocalgovInfo.class) {
			if(parent instanceof _LocalgovInfoNames) {
				_LocalgovInfoNames names = (_LocalgovInfoNames) parent;
				where.eq(names.id(), localgovinfoid);
				logger.debug("where equals: "+names.id().toString()+"="+localgovinfoid);
				return true;
			}
			else logger.fatal("Entity is LocalgovInfo but Names is "+parent.getClass().getSimpleName()+". expect _LocalgovInfoNames");
		}

		// localgovinfoid で絞り込めなければ、再帰的に結合する
		{
			// 定義済みの結合フィールドを取得
			Field[] fields = ExportService.entitysLocalgovinfoidJoinRules.get(entity);
			// 結合フィールドが定義されていない場合はリフレクションを使ってアノテーションの有無で検索する
			if(fields==null) {
				List<Field> fieldList = new ArrayList<Field>();
				for(Field field : entity.getFields()) {
					if(field.getAnnotation(JoinColumn.class)!=null
							// @JoinColumn 以外も検索対象とするとループになるので注意
							//|| field.getAnnotation(OneToMany.class)!=null
							//|| field.getAnnotation(OneToOne.class)!=null
							//|| field.getAnnotation(ManyToOne.class)!=null
							//|| field.getAnnotation(ManyToMany.class)!=null
							)
					{
						fieldList.add(field);
					}
				}
				fields = fieldList.toArray(new Field[fieldList.size()]);
			}
			// 結合フィールドがあれば再帰的に結合する
			for(Field field : fields) {
				// 結合フィールドの型から、結合エンティティクラスを取得する
				Class<?> joinClass = null;
				try {
					joinClass = field.getType();
					// List<TrackmapInfo> とかだったら、ジェネリックスの方 TrackmapInfo を取得しなおす
					if(Collection.class.isAssignableFrom(joinClass)) {
						joinClass = ReflectionUtil.getElementTypeOfCollectionFromFieldType(field);
					}
				} catch (Exception e) {
					logger.fatal("cannot get @JoinColumn field type(class) of "+"Entity "+entity.getSimpleName()+"."+field.getName(), e);
				}
				// 結合エンティティクラスがあれば、再帰的に結合する
				if(joinClass!=null) {
					try{
						Method namesMethod = ClassUtil.getMethod(namesClass, field.getName(), null);
						PropertyName<?> joinClassNames = (PropertyName<?>) namesMethod.invoke(parent, (Object[])null);
						// check loop
						String[] split = joinClassNames.toString().split("\\.");
						if(2<=split.length) {
							// 連続して同じものは join しない (ex: prefLocalgovinfo.prefLocalgovinfo)
							if(split[split.length-2].equals(split[split.length-1])) continue;
						}

						// execute inner join
						//select.innerJoin(joinClassNames);
						// leftjoin に変更
						// TrackgroupData.cityTrackData, TrackgroupData.prefTrackData のようにどちらかで結合する場合があるため。
						select.leftOuterJoin(joinClassNames);
						logger.debug("left join: "+joinClassNames.toString());
						if(selectByLocalgovinfoid(joinClass, joinClassNames, select, where, localgovinfoid, deleted)) {
							return true;
						}
					} catch(Exception e) {
						logger.fatal(e);
					}
				}
			}
		}

		return false;
	}

	/**
	 * 自治体IDで、任意のエンティティを検索する。
	 * join のルールは、{@link ExportService#entitysLocalgovinfoidJoinRules} に定義されている.
	 * join のルールがない場合は、@JoinColumnアノテーションがあれば、Joinを試みる.
	 * 連続して同じエンティティをJoinしないよう、ループの対策はしている.
	 * @param localgovinfoid 自治体ID
	 * @param deleted false: 削除フラグがtrue のものを除く, true: 削除フラグがtrue のものも含める
	 * @return エンティティリスト
	 */
	public List<ENTITY> findByLocalgovinfoid(Long localgovinfoid, boolean deleted) {
		//
		try {
			// Names.<Enitity>() で、エンティティのNamesクラスを取得する
			String entityClassName = this.entityClass.getSimpleName();
			Method namesMethod = ClassUtil.getMethod(Names.class, StringUtil.decapitalize(entityClassName), null);
			PropertyName<?> names = (PropertyName<?>) namesMethod.invoke(null, (Object[]) null);

			// AutoSelect を取得
			SimpleWhere where = new SimpleWhere();
			logger.debug("SELECT "+entityClassName);
			AutoSelect<ENTITY> select = select();
			if(selectByLocalgovinfoid(entityClass, names, select, where, localgovinfoid, deleted)) {
				// Where句があれば追加
				if(0<where.getParams().length) select.where(where);

				// order by id
				{
					// get Names class
					Class<?> namesClass = SAStrutsUtil.getEntityInnerNamesClass(entityClass);
					PropertyName<?> idNames = SAStrutsUtil.getPropertyName(entityClass, namesClass, names, "id");
					select.orderBy(Operations.asc(idNames.toString()));
				}

				// 結果リストを取得
				return (List<ENTITY>) select.getResultList();
			}
			else {
				throw new ServiceException("localgovinfoid not found on "+entityClassName);
			}
			//return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * 自治体IDで、任意のエンティティを検索する。
	 * @param localgovinfoid 自治体ID
	 * @return エンティティリスト
	 */
	public List<ENTITY> findByLocalgovinfoid(Long localgovinfoid) {
		return findByLocalgovinfoid(localgovinfoid, false);
	}

	/**
	 * Publicなフィールドのみの JSONObject に変換します.
	 * @param entity
	 * @return
	 */
	public JSONObject toJSONObject(ENTITY entity) {
		try {
			JSONObject json = new JSONObject();
			Field[] fields = entityClass.getFields();
			for (Field field : fields) {
				if(Modifier.isPublic(field.getModifiers())) {
					String key = field.getName();
					Object value = field.get(entity);
					json.put(key, value);
				}
			}
			return json;
		} catch (Exception e) {
			throw new ServiceException(lang.__("It could not be converted to JSONObject."), e);
		}
	}

	protected ENTITY translate(ENTITY entity) {
		Class<ENTITY> cls = (Class<ENTITY>)entity.getClass();
		Field[] fields = cls.getFields();
		for (Field field : fields) {
			if (field.getType().equals(String.class)) {
				try {
					String value = (String)field.get(entity);
					field.set(entity, lang.__(value));
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
				}
			}
		}
		return entity;
	}

	protected List<ENTITY> translateList(List<ENTITY> list) {
		Field[] fields = null;
		for (ENTITY entity : list) {
			if (fields == null) {
				Class<ENTITY> cls = (Class<ENTITY>)entity.getClass();
				fields = cls.getFields();
			}
			for (Field field : fields) {
				if (field.getType().equals(String.class)) {
					try {
						String value = (String)field.get(entity);
						field.set(entity, lang.__(value));
					}
					catch (IllegalArgumentException | IllegalAccessException e) {
					}
				}
			}
		}
		return list;
	}

	/**
	 * 依存するテーブルを削除後、指定したエンティティのレコードを削除する.
	 * @param entity 削除対象エンティティ
	 * @return 削除行数
	 */
	public DeleteCascadeResult deleteCascade(ENTITY entity) {
		return deleteCascade(entity, new DeleteCascadeResult(entityClass));
	}

	/**
	 * 依存するテーブルを削除後、指定したエンティティのレコードを削除する.
	 *
	 * 依存するテーブルがあるエンティティの場合、そのサービスクラスではこのメソッドをオーバライトすること。
	 * オーバライドのテンプレートは、{@link DatabaseUtil#generateDeleteCascadeTemplate(Class)} を使って生成することができる。
	 * また、テストメソッド LocalgovInfoServiceTest#testGenerateDeleteCascadeTemplate()等を使って、
	 * テンプレート生成を実行することができる。
	 *
	 * @param entity 削除対象エンティティ
	 * @param result カスケード削除結果
	 * @return 削除行数
	 */
	public DeleteCascadeResult deleteCascade(ENTITY entity, DeleteCascadeResult result) {
		try{
	    	result.deleteNum += delete(entity);
	    	// ログ出力
	    	String id = null;
	    	try {
		    	Field idField = entity.getClass().getField("id");
		    	if(idField!=null) {
		    		id = String.valueOf(idField.get(entity));
		    	}
	    	} catch(Exception e) {
	    		logger.warn("can not get id of "+entityClass.getSimpleName()+" for information log. "+e.getMessage());
	    	}
	    	logger.info("DeleteCascade "+entityClass.getSimpleName()+".id="+id);
		} catch(SQLRuntimeException e) {
			// エラーログ用に、一度キャッチする
			String errorMessage = DatabaseUtil.getErrorMessage(e);
			if(StringUtil.isNotEmpty(errorMessage)) {
				StringBuffer message = new StringBuffer(lang.__("Failed to delete.") + " "+entityClass.getSimpleName()+": "+errorMessage);
				// エラーメッセージをログ出力
				// PSQLException メッセージを追記
				 String logMessage = message+"\n"+e.getMessage();
				// 削除対象のエンティティを追記
				logMessage += "\n"+ReflectionToStringBuilder.toString(entity, ToStringStyle.SHORT_PREFIX_STYLE);
				logger.error(logMessage);

				// 最初に発生した例外のエラーメッセージを画面に表示
				Throwable t = e;
				while((t=t.getCause())!=null) {
					if(t instanceof SQLException) {
						// 最初に発生した例外の場合
						if(t.getCause()==null) {
							SQLException sqlException = (SQLException) t;
							message.append("<br/>"+sqlException.getMessage());
						}
					}
				}
				throw new ServiceException(message.toString(), e);
			}
			// そのまま投げる
			throw e;
		}
		return result;
	}

	/**
	 * 「カラム名 = 値」の条件に一致するレコードの削除を行う
	 * @param result 削除結果格納オブジェクト
	 * @param name カラム名
	 * @param value 値
	 * @return 削除対象のエンティティリスト
	 */
	public List<ENTITY> deleteCascadeBySimpleWhere(DeleteCascadeResult result, CharSequence name, Object value) {
		List<ENTITY> entities = select()
				.where(new SimpleWhere().eq(name, value))
				.getResultList();
		for (ENTITY entity : entities) {
			deleteCascade(entity, result);
		}
		return entities;
	}
}
