/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.DisastersummaryhistoryData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleInfo;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.seasar.jdbc.query.AutoInsertKeepIdImpl;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService.EntityType;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.AutoInsert;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.manager.JdbcManagerImpl;
import org.seasar.extension.jdbc.manager.JdbcManagerImplementor;

/**
 * データベースのユーティリティクラス
 */
public class DatabaseUtil {
	protected static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	// エンティティクラスとテーブル名のキャッシュ
	static HashMap<Class<?>, String> cacheEntityClassMap = new HashMap<Class<?>, String>();

	/**
	 * {@link AbstractService#deleteCascade(Object)}を継承クラスでオーバライドする際のテンプレートを出力する。
	 * @param entity エンティティ
	 * @return テンプレート
	 */
	public static String generateDeleteCascadeTemplate(Class<?> entity) {
		StringBuffer template = new StringBuffer();

		template.append("\n");
		template.append("	@Override"+"\n");
		template.append("	public DeleteCascadeResult deleteCascade2("+entity.getSimpleName()+" entity, DeleteCascadeResult result) {"+"\n");


		// 自分のIDカラムを持っているエンティティを検索(@OneToMany, @OneToOne)
		String idFieldName = DatabaseUtil.getIdFieldName(entity);
		List<Class<?>> childEntityList = DatabaseUtil.findEntitysByFieldName(idFieldName);
		if(0<childEntityList.size()) {
			template.append("\n");
			for(Class<?> childEntity : childEntityList) {
				template.append("		result.cascade("+childEntity.getSimpleName()+".class, "
						+"Names."+StringUtils.uncapitalize(childEntity.getSimpleName())+"()."+idFieldName+"(), entity.id);"+"\n");
			}
		}

		template.append("\n");
		template.append("		// " + lang.__("Delete the target record after records of its child table are deleted successfully.")+"\n");
		template.append("		return super.deleteCascade2(entity, result);"+"\n");
		//template.append("\n");
		template.append("	}"+"\n");

		return template.toString();
	}

	/**
	 * 指定したフィールド名を持つエンティティクラスのリストを取得する
	 * @param fieldName フィールド名(カラム名)
	 * @return 指定したフィールド名を持つエンティティクラスのリスト
	 */
	public static List<Class<?>> findEntitysByFieldName(String fieldName) {
		List<Class<?>> entitysList = new ArrayList<Class<?>>();

		for(Map.Entry<EntityType, List<Class<?>>> entry : ExportService.entitys.entrySet()) {
			List<Class<?>> entitys = entry.getValue();
			for(Class<?> entity : entitys) {
				try {
					entity.getField(fieldName);
					// if found
					entitysList.add(entity);
				} catch (Exception e) {
					// if not found
					// do nothing
				}
			}
		}

		return entitysList;
	}

	/**
	 * 他のクラスで参照されるときのIDカラム名を取得する.
	 * 正しいとは限らないため注意すること
	 * e.g. LocalgovInfo -> localgovinfoid
	 * @param entityClass エンティティ
	 * @return IDカラム名
	 */
	public static String getIdFieldName(Class<?> entityClass) {
		String idFieldName = entityClass.getSimpleName().toLowerCase()+"id";
		// xxxinfoid という書式でない idフィールド
		if(entityClass==GroupInfo.class) idFieldName = Names.userInfo().groupid().toString();
		if(entityClass==UnitInfo.class) idFieldName = Names.userInfo().unitid().toString();
		if(entityClass==UserInfo.class) idFieldName = Names.noticegroupuserInfo().userid().toString();
		// xxxdataid という書式でない idフィールド
		if(entityClass==DisastersummaryhistoryData.class) idFieldName = Names.disastersituationhistoryData().disastersummaryhistoryid().toString();
		if(entityClass==JudgeresultstyleInfo.class) idFieldName = Names.judgeresultstyleData().judageresultstyleinfoid().toString();
		if(entityClass==PubliccommonsReportData.class) idFieldName = Names.publiccommonsReportDataLastShelter().pcommonsreportdataid().toString();

		return idFieldName;
	}

	/**
	 * エンティティクラスのTableアノテーションからテーブル名を取得します.
	 * @param entity エンティティクラスか、エンティティオブジェクト
	 * @return テーブル名
	 */
	public static String getTableName(Class<?> entityClass) {
		String tableName = cacheEntityClassMap.get(entityClass);
		if(tableName==null) {
			// テーブル名を取得
			Table table = entityClass.getAnnotation(Table.class);
			tableName = table.name();
			cacheEntityClassMap.put(entityClass, tableName);
		}
		return tableName;
	}

	/**
	 * エンティティクラスからそのエンティティのサービスクラスを取得する.
	 * @param entityClass エンティティクラス
	 * @return サービスクラス
	 */
	@SuppressWarnings("rawtypes")
	public static Class<? extends AbstractService> getServiceClass(Class<?> entityClass) {
		// Service クラス
		String serviceClassName = entityClass.getSimpleName()+"Service";
		//String serviceComponentName = StringUtil.decapitalize(serviceClassName);

		try {
			@SuppressWarnings("unchecked")
			Class<? extends AbstractService> serviceClass = (Class<? extends AbstractService>) Class.forName(LocalgovInfoService.class.getPackage().getName()+"."+serviceClassName);
			return serviceClass;
		} catch(Exception e) {
			throw new ServiceException("Internal Error: ServiceClass cannot get. "+entityClass.getName(), e);
		}
	}

	/**
	 * テーブルのカラム名のコメントマップを取得
	 * @param jdbcManager
	 * @param tablename
	 * @return LinkedHashMap<カラム名, コメント>
	 */
	public static Map<String, String> getAlterFieldNameMap(JdbcManager jdbcManager, String tablename) {
		Map<String, String> remarksMap = new LinkedHashMap<String, String>();
		Connection con = null;
		ResultSet res = null;
		try {
			if(jdbcManager instanceof JdbcManagerImplementor) {
				JdbcManagerImplementor jdbcManagerImplementor = (JdbcManagerImplementor) jdbcManager;
				con = jdbcManagerImplementor.getDataSource().getConnection();
				DatabaseMetaData dmd = con.getMetaData();
				res = dmd.getColumns(null, null, tablename, "%");
				while (res.next()) {
					String remarks = res.getString("REMARKS");
					String columnName = res.getString("COLUMN_NAME");
					if (!remarksMap.containsKey(columnName) || remarks != null) remarksMap.put(columnName, remarks);
				}
			}
		} catch (SQLException e) {
    		Logger.getLogger(DatabaseUtil.class).error(e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e2) {e2.printStackTrace();}
			try {
				if(res != null) {
				res.close();
				}
			} catch (SQLException e) {
	    		Logger.getLogger(DatabaseUtil.class).error(e);
			}
		}

		return remarksMap;
	}

	/**
	 * テーブルのコメントを取得
	 * @param jdbcManager
	 * @param tablename
	 * @return LinkedHashMap<カラム名, コメント>
	 */
	public static String getTableComment(JdbcManager jdbcManager, String tablename) {
		Connection con = null;
		ResultSet res = null;
		String tableComment = "";
		try {
			if(jdbcManager instanceof JdbcManagerImplementor) {
				JdbcManagerImplementor jdbcManagerImplementor = (JdbcManagerImplementor) jdbcManager;
				con = jdbcManagerImplementor.getDataSource().getConnection();
				DatabaseMetaData dmd = con.getMetaData();
				res = dmd.getTables(null, null, tablename, null);
				while (res.next()) {
					String remarks = res.getString("REMARKS");
					if(remarks != null){
						tableComment = remarks;
					}
				}
			}
		} catch (SQLException e) {
    		Logger.getLogger(DatabaseUtil.class).error(e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e2) {e2.printStackTrace();}
			try {
				if(res != null) {
				res.close();
				}
			} catch (SQLException e) {
	    		Logger.getLogger(DatabaseUtil.class).error(e);
			}
		}

		return tableComment;
	}

	/**
	 * シーケンスによるIDの自動採番をせずに、エンティティにセットされたIDでインサートする{@link AutoInsert}の実装クラスを返します.
	 * @param jdbcManager
	 * @param entity
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static AutoInsertKeepIdImpl insertKeepId(final JdbcManager jdbcManager, final Object entity) {
		JdbcManagerImpl jdbcManagerImpl = (JdbcManagerImpl) jdbcManager;
		AutoInsertKeepIdImpl insert = new AutoInsertKeepIdImpl(jdbcManagerImpl, entity);
		insert.queryTimeout(jdbcManagerImpl.getQueryTimeout());
		return insert;
	}

	/**
	 * SQLステートに応じてエラーの原因を返す
	 * @param sqlState <a href="http://www.postgresql.jp/document/9.3/html/errcodes-appendix.html">PostgreSQLエラーコード</a>
	 * @return
	 */
	public static String getErrorMessage(Exception e) {
		// エラーログ用に、一度キャッチする
		Throwable t = e;
		while((t=t.getCause())!=null) {
			if(t instanceof SQLException) {
				SQLException psqlException = (SQLException) t;
				String sqlState = psqlException.getSQLState();

				// 制約違反
				if(sqlState!=null)
				if(sqlState.startsWith("22")) {
					return lang.__("Data exception");
				}
				else if(sqlState.startsWith("23")) {
					// 外部参照制約違反
					if("23503".equals(sqlState)) {
						return lang.__("Constraint violation of external references");
					}
					// 一意制約違反
					if("23505".equals(sqlState)) {
						return lang.__("Unique constraint violation");
					}
					return lang.__("Constraint violation of integrity");
				}
				else if(sqlState.startsWith("25")) {
					return lang.__("Invalid transaction state");
				}
				else if(sqlState.startsWith("42")) {
					if("42703".equals(sqlState)) {
						return lang.__("Column undefined");
					}
					else if("42P01".equals(sqlState)) {
						return lang.__("Table undefined");
					}
					return lang.__("Syntax error, or access role violation");
				}

				// PSQLException メッセージを追記
				return psqlException.getMessage();
			}
		}
		return null;
	}

	public static List<Field> getColumnFields(Object entity) {
		Class<?> entityClass = entity.getClass();
		List<Field> columnFields = new ArrayList<Field>();
		for(Field field : entityClass.getFields()) {
			if(field.getAnnotation(Column.class)!=null) {
				columnFields.add(field);
			}
		}
		return columnFields;
	}

	public static <ENTITY> ENTITY copyColumnFieldOnly(JSONObject jsonObject, ENTITY entity) {
		try {
			List<Field> columnFields = getColumnFields(entity);
			for(Field columnField : columnFields) {
				// カラムフィールドがJSONデータにあれば読み込む
				if(jsonObject.has(columnField.getName())) {
					Object value = jsonObject.get(columnField.getName());
					// JSONデータが null の場合
					if(value instanceof JSONNull) {
						value = null;
					}
					// 適切な型にキャスト
					else if(columnField.getType()==Long.class) {
						if(value instanceof Integer) {
							value = ((Integer) value).longValue();
						}
					}
					else if(columnField.getType()==Integer.class) {
						if(value instanceof Long) {
							value = ((Long) value).intValue();
						}
					}
					columnField.set(entity, value);
				}
			}
		} catch(Exception e) {
			throw new ServiceException("error", e);
		}
		return entity;
	}
}
