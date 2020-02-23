/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.transaction.UserTransaction;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import jp.ecom_plat.map.jsp.admin.MapDelete;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.service.setupper.ExportService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService.EntityType;
import jp.ecom_plat.saigaitask.util.CSWUtil;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * カスケード削除結果クラス
 */
public class DeleteCascadeResult{

	Logger logger = Logger.getLogger(DeleteCascadeResult.class);

	/** 削除対象エンティティクラス */
	public Class<?> entityClass;

	/** カスケード元の削除結果 */
	public DeleteCascadeResult parent;

	/** カスケードした削除結果 */
	public List<DeleteCascadeResult> children = new ArrayList<DeleteCascadeResult>();

	/** 削除数 */
	public Long deleteNum = 0L;

	/** 全カスケードで共通のマップ */
	private Map<Class<?>, DeleteCascadeResult> map;

	//@Binding(bindingType=BindingType.NONE)
	private AbstractService<?> service;

	/** 全カスケードで共通のファイルカスケード削除結果リスト */
	private List<FileDeleteCascadeResult> fileDeleteCascadeResults;

	/** 全カスケードで共通のeコミマップカスケード削除結果リスト */
	private List<EcommapDeleteCascadeResult> ecommapDeleteCascadeResults;

	/** 全カスケードで共通のクリアリングハウスのメタデータカスケード削除結果リスト */
	private List<ClearinghouseDeleteCascadeResult> clearinghouseDeleteCascadeResults;

	/**
	 * デフォルトコンストラクタ.
	 * カスケード削除する際に、最初に指定するのがこのコンストラクタである.
	 *
	 * レコードがないとカスケードされないので注意すること.
	 *  @param entityClass 削除対象のエンティティクラス
	 */
	public DeleteCascadeResult(Class<?> entityClass) {
		this(null, entityClass);
	}

	/**
	 * カスケードする際に用いるコンストラクタ
	 * @param parent カスケード元の削除結果
	 * @param entityClass カスケードで削除されるエンティティクラス
	 */
	public DeleteCascadeResult(DeleteCascadeResult parent, Class<?> entityClass) {
		this.parent = parent;
		this.entityClass = entityClass;
		this.map = (parent!=null) ? parent.map : new HashMap<Class<?>, DeleteCascadeResult>();
		this.fileDeleteCascadeResults = (parent!=null) ? parent.fileDeleteCascadeResults : new ArrayList<FileDeleteCascadeResult>();
		this.ecommapDeleteCascadeResults = (parent!=null) ? parent.ecommapDeleteCascadeResults : new ArrayList<EcommapDeleteCascadeResult>();
		this.clearinghouseDeleteCascadeResults = (parent!=null) ? parent.clearinghouseDeleteCascadeResults : new ArrayList<ClearinghouseDeleteCascadeResult>();

		@SuppressWarnings("rawtypes")
		Class<? extends AbstractService> serviceClass = DatabaseUtil.getServiceClass(entityClass);
		this.service = SpringContext.getApplicationContext().getBean(serviceClass);

		// regist entity
		this.map.put(entityClass, this);
	}

	/**
	 * カスケードで削除したいエンティティ(1レコード)を指定して、
	 * カスケード削除を実行する.
	 *
	 * 「カラム名 = 値」の条件に一致するレコードの削除を行う
	 *
	 * {@link AbstractService#deleteCascade(Object, DeleteCascadeResult)}をオーバライドするメソッドで使用する。
	 * @param cascadeEntityClass カスケード削除対象のエンティティクラス
	 * @param name カラム名
	 * @param value 値
	 * @return this
	 */
	public DeleteCascadeResult cascade(Class<?> cascadeEntityClass, CharSequence name, Object value) {
		// cascade 対象として追加
		DeleteCascadeResult child = getChild(cascadeEntityClass);

		// cascade delete を実行
		List<?> entities = child.service.deleteCascadeBySimpleWhere(child, name, value);
		child.deleteNum = new Long(entities.size());

		return this;
	}

	/**
	 * eコミマップの地図をカスケード削除する.
	 *
	 * {@link AbstractService#deleteCascade(Object, DeleteCascadeResult)}をオーバライドするメソッドで使用する。
	 * @param mapid 削除対象のeコミマップ地図ID
	 * @param entity エンティティ
	 * @param entityId エンティティID
	 * @return this
	 */
	public DeleteCascadeResult cascadeEcommapMapInfo(Long mapid, Object entity, String entityId) {
		if(mapid!=null) {
			EcommapDeleteCascadeResult result = new EcommapDeleteCascadeResult(mapid, entity);
			result.setEntity(entity, entityId);
			ecommapDeleteCascadeResults.add(result);
		}
		return this;
	}

	/**
	 * ローカルファイルをカスケード削除する.
	 *
	 * {@link AbstractService#deleteCascade(Object, DeleteCascadeResult)}をオーバライドするメソッドで使用する。
	 * @param file 削除対象ファイル
	 * @param entity エンティティ
	 * @param entityId エンティティID
	 * @param entityFilePath エンティティファイルパス
	 * @return this
	 */
	public DeleteCascadeResult cascadeFile(File file, Object entity, String entityId, String entityFilePath) {
		if(file.exists()) {
			FileDeleteCascadeResult result = new FileDeleteCascadeResult();
			result.setEntity(entity, entityId);
			result.file = file;
			result.entityFilePath = entityFilePath;
			fileDeleteCascadeResults.add(result);
		}
		return this;
	}

	/**
	 * クリアリングハウスに登録したメタデータをカスケード削除する.
	 *
	 * {@link AbstractService#deleteCascade(Object, DeleteCascadeResult)}をオーバライドするメソッドで使用する。
	 * @param metadataid メタデータID
	 * @param entity エンティティ
	 * @param entityId エンティティID
	 * @return this
	 */
	public DeleteCascadeResult cascadeClearinghouse(String metadataid, Object entity, String entityId) {
		ClearinghouseDeleteCascadeResult result = new ClearinghouseDeleteCascadeResult(metadataid);
		result.setEntity(entity, entityId);
		clearinghouseDeleteCascadeResults.add(result);
		return this;
	}

	/**
	 * カスケード削除対象の削除結果オブジェクトを取得する.
	 * もし、未登録の場合は新規作成する.
	 * @param entityClass カスケード削除対象のエンティティクラス
	 * @return カスケード削除対象の削除結果オブジェクト
	 */
	public DeleteCascadeResult getChild(Class<?> entityClass) {
		// 既に登録済みか検索する
		for(DeleteCascadeResult child : children) {
			if(child.entityClass==entityClass) {
				return child;
			}
		}

		// なければ作成する
		DeleteCascadeResult child = new DeleteCascadeResult(this, entityClass);
		children.add(child);
		return child;
	}

	/**
	 * 削除結果を {@link System#out} に出力する.
	 */
	public void printResult() {
		printResult(System.out);
	}

	/**
	 * 削除結果を指定した出力ストリームに出力する.
	 * @param out
	 */
	public void printResult(PrintStream out) {
		out.println(getPath()+": "+deleteNum);
		for(DeleteCascadeResult child : children) {
			child.printResult(out);
		}
	}

	/**
	 * 全てのエンティティを走査し、カスケード削除結果数を {@link System#out} に出力する.
	 */
	public void printCheckResult() {
		printCheckResult(System.out);
	}

	/**
	 * 全てのエンティティを走査し、カスケード削除結果数を出力する.
	 * @param out 出力ストリーム
	 */
	public void printCheckResult(PrintStream out) {
		out.println("○: cascade delete target, × not target");
		for(Map.Entry<EntityType, List<Class<?>>> entry : ExportService.entitys.entrySet()) {
			List<Class<?>> entitys = entry.getValue();
			for(Class<?> entity : entitys) {
				out.println(entity.getName()+(this.map.containsKey(entity)?"○":"×"));
			}
		}
	}

	/**
	 * @return カスケード元のStack
	 */
	public Stack<DeleteCascadeResult> getParentStack() {
		Stack<DeleteCascadeResult> stack = new Stack<DeleteCascadeResult>();
		DeleteCascadeResult parent = this.parent;
		if(parent!=null) {
			do {
				stack.push(parent);
				parent = parent.parent;
			}while(parent!=null);
		}

		return stack;
	}

	/**
	 * カスケード元を辿って、この削除対象エンティティまでのパスを取得する.
	 * @return パス
	 */
	public String getPath() {
		StringBuffer path = new StringBuffer();
		Stack<DeleteCascadeResult> stack = getParentStack();

		boolean first = true;
		DeleteCascadeResult deleteCascade = null;
		while(!stack.isEmpty()) {
			deleteCascade = stack.pop();
			if(first) first=false;
			else path.append("->");
			path.append(deleteCascade.entityClass.getSimpleName());
		}

		if(path.length()!=0)
			path.append("->");
		path.append(this.entityClass==null ? "null" : this.entityClass.getSimpleName());

		return path.toString();
	}

	/**
	 * 削除コミット
	 * @return 結果
	 */
	public boolean commit() {
		// File削除を実行する
		logger.info("BEGIN Delete files: "+fileDeleteCascadeResults.size());
		for(FileDeleteCascadeResult fileDeleteCascadeResult : fileDeleteCascadeResults) {
			fileDeleteCascadeResult.commit();
		}
		logger.info("END Delete files: "+fileDeleteCascadeResults.size());

		// eコミマップ削除を実行する
		logger.info("BEGIN Delete ecommap mapInfo: "+ecommapDeleteCascadeResults.size());
		List<Long> deleteMapIds = new ArrayList<Long>();
		{
			for(EcommapDeleteCascadeResult ecommapDeleteCascadeResult : ecommapDeleteCascadeResults) {
				deleteMapIds.add(ecommapDeleteCascadeResult.mapid);
			}
		}
		for(EcommapDeleteCascadeResult ecommapDeleteCascadeResult : ecommapDeleteCascadeResults) {
			ecommapDeleteCascadeResult.deleteMapIds = deleteMapIds;
			ecommapDeleteCascadeResult.commit();
		}
		logger.info("END Delete ecommap mapInfo: "+ecommapDeleteCascadeResults.size());

		// レイヤ削除後、GeoServer のリソースをリロードさせる
		/*
		if(0<ecommapDeleteCascadeResults.size()) {
			boolean reload = false;
			try {
				reload = ExMapDB.reloadGeoServerCatalog();
			} catch(Exception e) {
				// do nothing
			} finally {
				if(reload) {
					logger.info("MapDelete reloadGeoServer: success");
				}
				else {
					logger.info("MapDelete reloadGeoServer: failed");
				}
			}
		}
		*/

		// クリアリングハウスのメタデータ削除を実行する
		logger.info("BEGIN Delete Clearinghouse metadata: "+clearinghouseDeleteCascadeResults.size());
		for(ClearinghouseDeleteCascadeResult clearinghouseDeleteCascadeResult : clearinghouseDeleteCascadeResults) {
			clearinghouseDeleteCascadeResult.commit();
		}
		logger.info("END Delete Clearinghouse metadata: "+clearinghouseDeleteCascadeResults.size());

		return true;
	}

	/**
	 * 削除ロールバック
	 * @param userTransaction DB Transaction
	 * @return 結果
	 */
	public boolean rollback(UserTransaction userTransaction) {
		// Rollback Database
		if(userTransaction!=null) {
			logger.info("Rollback Database");
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e) {
				logger.error("DeleteCascade faild to setRollbackOnly "+entityClass.getSimpleName(), e);
				return false;
			}
		}

		// File削除をロールバックする
		logger.info("BEGIN Rollback Delete files: "+fileDeleteCascadeResults.size());
		for(FileDeleteCascadeResult fileDeleteCascadeResult : fileDeleteCascadeResults) {
			fileDeleteCascadeResult.rollback();
		}
		logger.info("END Rollback Delete files: "+fileDeleteCascadeResults.size());

		// eコミマップ削除をロールバックする
		logger.info("BEGIN Rollback Delete ecommap mapInfo: "+ecommapDeleteCascadeResults.size());
		for(EcommapDeleteCascadeResult ecommapDeleteCascadeResult : ecommapDeleteCascadeResults) {
			ecommapDeleteCascadeResult.rollback();
		}
		logger.info("END Rollback Delete ecommap mapInfo: "+fileDeleteCascadeResults.size());

		return true;
	}

	/**
	 * カスケード削除結果抽象クラス
	 */
	abstract static class AbstractDeleteCascadeResult {
		Object entity;
		String entityId;

		public AbstractDeleteCascadeResult setEntity(Object entity, String entityId) {
			this.entity = entity;
			this.entityId = entityId;
			return this;
		}

		public String logMessage() {
			Class<?> entityClass = entity.getClass();
			return "DeleteCascade "+entityClass.getSimpleName()+".id="+entityId+": ";
		}

		/**
		 * 削除実行する
		 * @return 実行成功フラグ
		 */
		abstract public boolean commit();

		/**
		 * 削除をロールバックする
		 * @return ロールバック成功フラグ
		 */
		abstract public boolean rollback();
	}

	/**
	 * Fileカスケード削除結果
	 */
	static class FileDeleteCascadeResult extends AbstractDeleteCascadeResult {
		File file;
		String entityFilePath;

		Logger logger = Logger.getLogger(FileDeleteCascadeResult.class);

		@Override
		public boolean commit() {
			FileDeleteCascadeResult fileDeleteCascadeResult = this;

			// check if file exists
			File file = fileDeleteCascadeResult.file;
			if(file.exists()==false) {
				logger.warn(logMessage()+"file not exists "+entityFilePath);
				throw new ServiceException(logMessage()+"file not exists(already delete? or wrong path?) "+entityFilePath);
			}
			// delete file, if exists
			else if (file.isFile()){
				boolean deleted = file.delete();
				logger.info(logMessage()+"Delete file (deleted="+deleted+") "+file.getPath());
			}
			// delete directory
			else if (file.isDirectory()){
				boolean deleted = false;
				try {
					FileUtils.deleteDirectory(file);
					deleted = true;
				} catch (IOException e) {
				}
				logger.info(logMessage()+"Delete directory (deleted="+deleted+") "+file.getPath());
			}

			return true;
		}

		@Override
		public boolean rollback() {
			logger.info("Rollback file not support");
			return true;
		}
	}

	/**
	 * eコミマップカスケード削除結果
	 */
	static class EcommapDeleteCascadeResult extends AbstractDeleteCascadeResult {
		long mapid;
		MapDelete mapDelete;

		Logger logger = Logger.getLogger(EcommapDeleteCascadeResult.class);

		public List<Long> deleteMapIds;

		public EcommapDeleteCascadeResult(long mapid, Object entity) {
			this.mapid = mapid;
			this.entity = entity;
			this.mapDelete = new MapDelete(mapid);
			this.mapDelete.deleteLogically = false; // 物理削除
		}

		@Override
		public boolean commit() {
			return mapDelete.commit(deleteMapIds);
		}

		@Override
		public boolean rollback() {
			logger.info("Rollback ecommap");
			boolean success = mapDelete.rollback();
			logger.info("Rollback ecommap "+(success?"Success":"Failed"));
			return success;
		}
	}

	/**
	 * クリアリングハウスメタデータカスケード削除結果
	 */
	static class ClearinghouseDeleteCascadeResult extends AbstractDeleteCascadeResult {

		String metadataid;

		List<String> cswurls = new ArrayList<String>();
		List<String> cswusers = new ArrayList<String>();
		List<String> cswpasswds = new ArrayList<String>();

		//String rollbackMetadataXML;
		Map<String, String> rollbackMetadataXML = new HashMap<String, String>();

		Logger logger = Logger.getLogger(ClearinghouseDeleteCascadeResult.class);

		public ClearinghouseDeleteCascadeResult(String metadataid) {
			this.metadataid = metadataid;

			// 災害モードと訓練モードでクリアリングハウスの登録先を変更する
			cswurls.add(Config.getString("CSWURL"));
			cswurls.add(Config.getString("CSWURL_TRAINING"));

			cswusers.add(Config.getString("CSWUSER"));
			cswusers.add(Config.getString("CSWUSER_TRAINING"));

			cswpasswds.add(Config.getString("CSWPASSWD"));
			cswpasswds.add(Config.getString("CSWPASSWD_TRAINING"));
		}

		@Override
		public boolean commit() {

			for(String cswurl : cswurls) {
				int idx = cswurls.indexOf(cswurl);
				String user = cswusers.get(idx);
				String passwd = cswpasswds.get(idx);
				// メタデータを取得して存在チェック
				String xml = CSWUtil.getRecordById(metadataid);
				if(xml==null) {
					logger.info(logMessage()+"metadata not found: "+metadataid+" on cswurl="+cswurl);
					continue;
				}

				// 存在していれば削除実行する
				boolean deleted = CSWUtil.deleteMetadata(cswurl,user,passwd, metadataid);
				if(deleted) {
					logger.info(logMessage()+"metadata deleted: "+metadataid+" on cswurl="+cswurl);
					rollbackMetadataXML.put(cswurl, xml);
				}
				else {
					logger.warn(logMessage()+"metadata delete failed: "+metadataid+" on cswurl="+cswurl);
				}
			}

			return true;
		}

		@Override
		public boolean rollback() {
			for(Map.Entry<String, String> entry : rollbackMetadataXML.entrySet()) {
				String cswurl = entry.getKey();
				int idx = cswurls.indexOf(cswurl);
				String user = cswusers.get(idx);
				String passwd = cswpasswds.get(idx);
				String rollbackXML = entry.getValue();
				boolean uploaded = CSWUtil.uploadMetadata(cswurl,user,passwd, rollbackXML,null,null);
				if(uploaded) {
					logger.info(logMessage()+"metadata rollback success: "+metadataid+" on cswurl="+cswurl);
				}
				else {
					logger.info(logMessage()+"metadata rollback fail: "+metadataid+" on cswurl="+cswurl);
				}
			}

			return true;
		}
	}

}
