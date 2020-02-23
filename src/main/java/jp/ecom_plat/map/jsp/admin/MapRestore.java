/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.map.jsp.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import jp.ecom_plat.map.db.CommunityInfo;
import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.GroupInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.UserInfo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.postgresql.PGStatement;
import org.seasar.framework.beans.util.BeanMap;

/**
 * eコミマップの地図バックアップデータ復元クラスの拡張。
 */
public class MapRestore extends jp.ecom_plat.map.admin.MapRestore {

	static Logger logger = Logger.getLogger(MapRestore.class);

	static String CONTENTS_PATH;
	static {
		// 設定ファイルからパスを取得
		ResourceBundle pathBundle = ResourceBundle.getBundle("PathInfo", Locale.getDefault());
		CONTENTS_PATH = pathBundle.getString("CONTENTS_PATH");
	}

	/**
	 *
	 * @param zipFile
	 * @param regist
	 * @throws IOException
	 * @throws JSONException
	 */
	public MapRestore(File zipFile, boolean regist) throws IOException, JSONException
	{
		super(zipFile, regist);
		restoreResult = new MapRestore.Result();
	}

	////////////////////////////////////////////////////////////////
	// 復元先

	/**
	 * 復元先の設定
	 * TODO ID指定の上書きがある場合はIDを追加して、古いデータは削除する
	 * @param communityInfo 復元先のサイト
	 * @param groupInfo 復元先のグループ(0: サイト全体)
	 * @param userInfoTable ユーザID変換テーブル
	 * @return 地図復元結果
	 */
	public MapRestore dest(CommunityInfo communityInfo, GroupInfo groupInfo, HashMap<Integer, UserInfo> userInfoTable) {
		restoreResult.communityInfo = communityInfo;
		restoreResult.groupInfo = groupInfo;
		restoreResult.userInfoTable = userInfoTable;
		return this;
	}

	////////////////////////////////////////////////////////////////
	// LayerInfo

	/** レイヤのリストアのインデックス */
	private int restoreLayerInfoArrayIndex = 0;
	/**
	 * 次のリストア対象のレイヤがあるかチェックする.
	 * @return 次のリストア対象がある場合は true
	 */
	public boolean hasNextRestoreLayer() {
		return restoreLayerInfoArrayIndex<layerInfoArray.length();
	}

	private Connection conn = null;

	/**
	 * DBのコネクションや復元対象のインデックスを初期化
	 * @throws Exception
	 */
	public void initRestoreContentsLayerData() throws Exception {
		////////////////////////////////////////////////////////////////
		// メモリのGC
		// gc();

		conn = ExMapDB.getDataSourceConnection();

//		//DBに直接接続
//		ResourceBundle bundle = ResourceBundle.getBundle("ResourceInfo", Locale.getDefault());
//		String namespace = bundle.getString("MAPDB");
//		//Resource Injection Datasource
//		InitialContext ic = new InitialContext();
//		DataSource dataSource = (DataSource)ic.lookup("java:comp/env/"+namespace);
//
//		conn = dataSource.getConnection();
	}

	/**
	 * DBのコネクションの終了処理
	 * @throws Exception
	 */
	public void destroyRestoreContentsLayerData() throws Exception {
		if (conn != null) conn.close();
	}


	private static int getDstUserId(HashMap<Integer, UserInfo> userInfoTable, String srcUserId)
	{
		return getDstUserId(userInfoTable, Integer.valueOf(srcUserId));
	}
	private static int getDstUserId(HashMap<Integer, UserInfo> userInfoTable, Integer srcUserId)
	{
		UserInfo convUserInfo = userInfoTable.get(srcUserId);
		return convUserInfo==null?0:convUserInfo.userId;
	}

	/**
	 * コンテンツデータの復元結果
	 */
	public static class ContentsLayerDataRestoreResult {
		/** 復元前レイヤID */
		public String srcLayerId;
		/** 復元先レイヤ情報 */
		public LayerInfo dstLayerInfo;
		/** 地物の数 */
		public int featureCount;
		/** INSERTクエリ */
		public String query;
		/** 値の入った実際のINSERT */
		public String insert;
		/** 強制実行時に無視した例外 */
		public Vector<Exception> exceptions = new Vector<Exception>();
		/** フィーチャファイルの復元結果リスト */
		public Vector<ContentsLayerFeatureFileRestoreResult> contentsLayerFeatureFileRestoreResults
		= new Vector<MapRestore.ContentsLayerFeatureFileRestoreResult>();
	}

	/**
	 * フィーチャファイルの復元結果
	 */
	public static class ContentsLayerFeatureFileRestoreResult {
		/** 復元前コンテンツファイルURL */
		public Vector<String> srcContentsFileUrls = new Vector<String>();
		/** 復元後コンテンツファイルURL */
		public Vector<String> dstContentsFileUrls = new Vector<String>();
		/** 復元後コンテンツファイル */
		public Vector<File> dstContentsFiles = new Vector<File>();
		/** 強制実行時に無視した例外 */
		public Vector<Exception> exceptions = new Vector<Exception>();
	}








	// ---- 以下通信途絶対応
	/**
	 *
	 * @return LayerInfo
	 * @throws Exception
	 */
	public LayerInfo readLayerInfo() throws Exception {
		JSONObject layerInfoJSON = layerInfoArray.getJSONObject(restoreLayerInfoArrayIndex);

		String srcLayerId = layerInfoJSON.getString("layerId");
		// レイヤ種別からレイヤID接頭辞を取得
		short layerType = (short)layerInfoJSON.getInt("type");

		LayerInfo layerInfo = new LayerInfo();
		layerInfo.layerId = srcLayerId;
		layerInfo.type = layerType;

		// 読み込み対象を次へ
		restoreLayerInfoArrayIndex+=1;

		return layerInfo;
	}

	/**
	 * 地図バックアップファイルからfeatureの属性の列情報を取得
	 * @param featureColumnNames
	 * @return featureの列名リスト
	 * @throws Exception
	 */
	public List<String>  getFileFeatureAttrColumnNames(List<String> featureColumnNames) throws Exception {
		List<String> retVale = new ArrayList<String>();
		List<String> notAtttrColumnNames = null;
		if ("2.0.2".compareTo(version) <= 0) {
			notAtttrColumnNames = new ArrayList<String>(Arrays.asList("mid","status","userid","created","moduserid","modified","time_from","time_to","gid","the_geom","_orgid"));
		} else {
			notAtttrColumnNames = new ArrayList<String>(Arrays.asList("mid","userid","status","moduserid","modified","time_from","time_to","gid","the_geom"));
		}

		for(String featureColumnName :  featureColumnNames){
			boolean isNotAttrColumn = false;
			for(String notAttrColumnName : notAtttrColumnNames){
				if(featureColumnName.equals(notAttrColumnName)){
					isNotAttrColumn = true;
					break;
				}
			}
			if(isNotAttrColumn == false){
				retVale.add(featureColumnName);
			}
		}
		return retVale;
	}

	/**
	 * 地図バックアップファイルからfeatureの列情報を取得
	 * @param srcLayerId
	 * @return featureの列名リスト
	 * @throws Exception
	 */
	public List<String> getFileFeatureColumnNames(String srcLayerId) throws Exception {
		File contentsFile = new File(tmpPath+"features/"+srcLayerId);
		List<String> columnNamesList = null;

		if (contentsFile.isFile()) {
			//一行目読み込み
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(contentsFile));
				String line = br.readLine();
				if(line==null) throw new Exception("Unsupported Format ContentsFile: "+contentsFile.getName());
				String [] array = line.split("\t");
				columnNamesList = new ArrayList<String>(Arrays.asList(array));
			}finally{
				if(br!=null) br.close();
			}
		}

		return columnNamesList;
	}


	/**
	 * 地図バックアップファイルからfeatureを取得
	 * @param srcLayerId
	 * @return gidをキーとした、feature各行のハッシュマップ
	 * @throws Exception
	 */
	public List<BeanMap> getFileFeatureRecords(String srcLayerId) throws Exception {
		File contentsFile = new File(tmpPath+"features/"+srcLayerId);

		List<BeanMap> rows = new ArrayList<BeanMap>();
		if (contentsFile.isFile()) {

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(contentsFile));

				// 一行目を読み込み、カラム名を所得
				String line = br.readLine();
				if(line==null) throw new Exception("Unsupported Format ContentsFile: "+contentsFile.getName());
				List<String> columnNamesList = new ArrayList<String>(Arrays.asList(line.split("\t")));

				// 残りの行を読み込み、BeanMap作成
				while ((line=br.readLine()) != null) {
					List<String> dataRow = new ArrayList<String>(Arrays.asList(line.split("\t")));

					if (dataRow == null || dataRow.size() <= 0){
						continue;
					}

					// 末尾の";"を除外
					String lastColumn = dataRow.get(dataRow.size() -1);
					if(lastColumn.equals(";")){
						dataRow.remove(dataRow.size() -1);
					}

					if (dataRow.size() != columnNamesList.size()) {
						br.close(); throw new Exception("Column size not same");
					}

					BeanMap row = new BeanMap();
					int index = 0;
					for(String columnName : columnNamesList){
						row.put(columnName, dataRow.get(index));
						index++;
					}
					rows.add(row);
				}
			} finally {
				if(br!=null) br.close();
			}
		}

		return rows;
	}

	/**
	 *
	 * @param dstLayerInfo
	 * @param destMapId
	 * @param featureColumnNameRow
	 * @param featureDataRow
	 * @param generateGid TODO
	 */
	public void restoreLayerFeature(LayerInfo dstLayerInfo, Long destMapId, List<String>featureColumnNameRow, BeanMap featureDataRow, boolean isUpdate){
		int attrOffset = 0;

		// gid を取得
		Long gid = new Long((String)featureDataRow.get("gid"));

		// リストを複製し、 "gid" を取り除く
		BeanMap featureDataRowSave = featureDataRow;
		featureColumnNameRow = new ArrayList<String>(featureColumnNameRow);
		featureColumnNameRow.remove("gid");
		featureDataRow = (BeanMap)featureDataRow.clone();
		featureDataRow.remove("gid");

		String[] attrIds =  (String[])featureColumnNameRow.toArray(new String[0]);

		StringBuilder query = new StringBuilder("");
		if (isUpdate)
			query.append("UPDATE ").append(dstLayerInfo.layerId).append(" SET");
		else
			query.append("INSERT INTO ").append(dstLayerInfo.layerId);

		if ("2.0.2".compareTo(version) <= 0) {
			query.append(" (mid,status,userid,created,moduserid,modified,time_from,time_to,the_geom,_orgid");
			attrOffset = 10;
		} else {
			query.append(" (mid,userid,status,moduserid,modified,time_from,time_to,the_geom");
			attrOffset = 9;
		}

		//属性追加 全て文字列型
		for (int j=attrOffset; j<attrIds.length; j++) {
			//テーブル作成時にLayerInfo内のAttrInfoが自動で追加されるので不要
			//if (regist) mapDB.insertAttrColumn(dstLayerInfo.layerId, attrIds[j], "text");
			query.append(",").append(attrIds[j]);
		}

		query.append(isUpdate ? ") =" : ") VALUES");
		if ("2.0.2".compareTo(version) <= 0) {
			query.append(" (?,?,?,?,?,?,?,?,Geometry(?),?");
		} else {
			query.append(" (?,?,?,?,?,?,?,Geometry(?)");
		}

		for (int j=attrOffset; j<attrIds.length; j++) {
			query.append(",?");
		}

		query.append(isUpdate ? ") WHERE gid = ?" : ") RETURNING gid");

		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(query.toString());

			//データをINSERT/UPDATE
			int idx = 0;
			String[] values = new String [featureDataRow.size()];
			int columnIndex = 0;
			for(String columnName : featureColumnNameRow){
				values[columnIndex] = featureDataRow.get(columnName).toString();
				columnIndex++;
			}

			if (values.length != attrIds.length) { throw new Exception("Column size not same"); }

			// if (values[idx].length()==0) statement.setNull(idx+1, Types.BIGINT); else statement.setLong(idx+1, Long.parseLong(values[idx]));
			 statement.setLong(idx+1, destMapId);
			idx++;
			if ("2.0.2".compareTo(version) <= 0) {
				if (values[idx].length()==0) statement.setNull(idx+1, Types.TINYINT); else statement.setShort(idx+1, Short.valueOf(values[idx]));
				idx++;
				if (values[idx].length()==0 || Integer.parseInt(values[idx]) <= 0) statement.setNull(idx+1, Types.INTEGER); else statement.setInt(idx+1, getDstUserId(restoreResult.userInfoTable, values[idx]));
				idx++;
				if (values[idx].length()==0) statement.setNull(idx+1, Types.TIMESTAMP); else statement.setTimestamp(idx+1, Timestamp.valueOf(values[idx]));
				idx++;
			} else {
				if (values[idx].length()==0 || Integer.parseInt(values[idx]) <= 0) statement.setNull(idx+1, Types.INTEGER); else statement.setInt(idx+1, getDstUserId(restoreResult.userInfoTable, values[idx]));
				idx++;
				if (values[idx].length()==0) statement.setNull(idx+1, Types.TINYINT); else statement.setShort(idx+1, Short.valueOf(values[idx]));
				idx++;
			}
			if (values[idx].length()==0 || Integer.parseInt(values[idx]) <= 0) statement.setNull(idx+1, Types.INTEGER); else statement.setInt(idx+1, getDstUserId(restoreResult.userInfoTable, values[idx]));
			idx++;
			if (values[idx].length()==0) statement.setNull(idx+1, Types.TIMESTAMP); else statement.setTimestamp(idx+1, Timestamp.valueOf(values[idx]));
			idx++;
			if (values[idx].length()==0) statement.setNull(idx+1, Types.TIMESTAMP); else statement.setTimestamp(idx+1, StringToTimestamp(values[idx]));
			idx++;
			if (values[idx].length()==0) statement.setNull(idx+1, Types.TIMESTAMP); else statement.setTimestamp(idx+1, StringToTimestamp(values[idx]));
			idx++;
			if (values[idx].length()==0) statement.setNull(idx+1, Types.VARCHAR); else statement.setString(idx+1, values[idx]);
			idx++;
			statement.setLong(idx+1, Long.parseLong(values[idx]));
			idx++;
			for (int j=attrOffset; j<attrIds.length; j++) {
				//改行は \f に変換してある
				if (values[idx].length()==0) statement.setNull(idx+1, Types.VARCHAR); else statement.setString(idx+1, values[idx].replaceAll("\f","\n"));
				idx++;
			}

			// WHERE 句
			if (isUpdate) {
				statement.setLong(idx+1, gid);
				idx++;
			}

			// 旧 gid を保存する
			featureDataRowSave.put("old_gid", "" + gid);

			//DBに登録
			if (isUpdate) {
				statement.executeUpdate();
			}
			else {
				ResultSet result = statement.executeQuery();
				// 新規に採番された gid を返却する
				if (result.next())
					featureDataRowSave.put("gid", result.getString(1));
			}

		} catch (Exception e) {
			if(statement != null){
				logger.debug(statement, e);
			}else{
				logger.debug("restoreLayerFeatures failed",e);
			}
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * DBから、登録情報の地物に添付されたファイル情報を取得する。FeatureDB.getFeatureFileList()の代替。
	 * @param layerId
	 * @param fid
	 * @return Vector<FeatureFileInfo>
	 * @throws SQLException
	 */
	public Vector<FeatureFileInfo> getDbFeatureFiles(String layerId, long fid) throws SQLException{
		Statement statement = conn.createStatement();
		StringBuilder queryBuf = new StringBuilder();
		queryBuf.append("SELECT url, title, gid, type, time_upload FROM _feature_files WHERE gid=");
		queryBuf.append(fid).append(" AND layerid='").append(StringEscapeUtils.escapeSql(layerId)).append("'");

		logger.debug(queryBuf.toString());
		ResultSet result = statement.executeQuery(queryBuf.toString());

		Vector<FeatureFileInfo> vecFileResult = new Vector<FeatureFileInfo>();
		while (result.next()) {
			FeatureFileInfo fileResult = new FeatureFileInfo();
			fileResult.url = result.getString(1);
			fileResult.title = result.getString(2);
			fileResult.gid = result.getLong(3);
			fileResult.type = result.getInt(3);
			if(result.getTimestamp(5) != null){
				fileResult.time_upload = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(result.getTimestamp(5));
			}else{
				fileResult.time_upload = "";
			}

			vecFileResult.add(fileResult);
		}

		return vecFileResult;
	}

	/**
	 * 地図バックアップファイルから、登録情報の地物に添付されたファイル情報を取得する
	 * @param srcLayerId
	 * @param fid
	 * @return List<FeatureFileInfo>
	 * @throws Exception
	 */
	public List<FeatureFileInfo> getFileFeatureFiles(String srcLayerId, Long fid, Long fildFid) throws Exception {
		List<FeatureFileInfo> featureFileInfos = new ArrayList<FeatureFileInfo>();

		File featureFilesFile = new File(tmpPath+"feature_files/_feature_files_"+srcLayerId);

		if (featureFilesFile.isFile()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(featureFilesFile));
				String line;
				while ((line=br.readLine()) != null) {
					String[] values = line.split("\t");
					if (values.length == 0){
						continue;
					}

					Long targetFid = Long.parseLong(values[0]);
					if(! targetFid.equals(fildFid)){
						continue;
					}

					FeatureFileInfo featureFileInfo = new FeatureFileInfo();
					featureFileInfo.gid = fid;
					featureFileInfo.mid = Long.parseLong(values[1]);
					featureFileInfo.layerid = values[2];
					featureFileInfo.userid = Integer.parseInt(values[3]);
					featureFileInfo.status = Integer.parseInt(values[4]);
					featureFileInfo.type = Integer.parseInt(values[5]);
					featureFileInfo.time_upload = values[6];
					featureFileInfo.title = values[7];
					featureFileInfo.url = values[8];
					featureFileInfo.file_order = Integer.parseInt(values[9]);

					featureFileInfos.add(featureFileInfo);
				}
			} finally {
				if(br!=null) br.close();
			}
		}

		return featureFileInfos;
	}


	/**
	 * 地図バックアップファイルから、登録情報の地物に添付されたファイルを復元する
	 * @param srcLayerId
	 * @param destMapId
	 * @param dstLayerInfo
	 * @param gId
	 * @throws Exception
	 */
	public void restoreFeatureFiles(String srcLayerId, Long destMapId, LayerInfo dstLayerInfo, Long gId, Long fileGId) throws Exception {
		restoreContentsLayerFeatureFiles(conn, srcLayerId, destMapId, dstLayerInfo, gId, fileGId);
	}

	/**
	 * 地図バックアップファイルから、登録情報の地物に添付されたファイルを追加または更新する
	 * @param srcLayerId
	 * @param dstMapId
	 * @param dstLayerInfo
	 * @param featureFileInfo
	 * @param isUpdate
	 * @throws Exception
	 */
	public void updateFeatureFile(String srcLayerId, Long dstMapId, LayerInfo dstLayerInfo, FeatureFileInfo featureFileInfo, Boolean isUpdate) throws Exception {

		//パス取得
		final String contentsDir = MAP_DIR+CONTENTS_PATH;

		//DB内のfeatureのユーザIDとURLを変更
		File featureFilesFile = new File(tmpPath+"feature_files/_feature_files_"+srcLayerId);

		String featureFilesSql = "";
		if(isUpdate){
			featureFilesSql = "UPDATE _feature_files set time_upload = ? WHERE gid = ? AND mid = ? AND layerid = ? AND title = ? AND url = ?";
		}else{
			featureFilesSql = "INSERT INTO _feature_files (gid,mid,layerid,userid,status,type,time_upload,title,url,file_order) VALUES (?,?,?,?,?, ?,?,?,?,?)";
		}
		PreparedStatement statement = conn.prepareStatement(featureFilesSql);

		//ファイル情報のパスを修正してDBに追加
		if (featureFilesFile.isFile()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(featureFilesFile));
				String line;
				while ((line=br.readLine()) != null) {
					String[] values = line.split("\t");
					if (values.length == 0) continue;
					//URL変換
					String srcContentsFileUrl = values[8];
					String dstContentsFileUrl = null;
					File srcContentsFile = null;
					File dstContentsFile = null;

					// 引数のfeatureFileInfoと完全一致したレコードのみ処理
					if( Long.parseLong(values[0]) != featureFileInfo.gid
							|| Long.parseLong(values[1]) != featureFileInfo.mid
							|| !(values[2].equals(featureFileInfo.layerid))
							|| Integer.parseInt(values[3]) != featureFileInfo.userid
							|| Integer.parseInt(values[4]) != featureFileInfo.status
							|| Integer.parseInt(values[5]) != featureFileInfo.type
							|| !(values[6].equals(featureFileInfo.time_upload))
							|| !(values[7].equals(featureFileInfo.title))
							|| !(values[8].equals(featureFileInfo.url))
							|| Integer.parseInt(values[9]) != featureFileInfo.file_order
					){
						continue;
					}

					if (srcContentsFileUrl.startsWith("/")) {
						//無効なファイルなら登録しない
						if (srcContentsFileUrl.indexOf("/",1)>0) {
							srcContentsFile = new File(URLDecoder.decode(tmpPath+srcContentsFileUrl.substring(srcContentsFileUrl.indexOf("/",1)), "UTF-8"));

							//System.out.writeln(srcContentsFile);
							//tmpファイルが存在しなかったら登録しない
							if (srcContentsFile.exists()) {

								//String dstContentsPath = filePathTable.get(srcContentsFile.getPath());
								//if (dstContentsPath != null) dstContentsFileUrl = dstContentsPath.substring(contentsDir.length());

								String srcContentsFilePath = srcContentsFile.getPath().substring(contentsDir.length());

								// FORDEV
								srcContentsFilePath = srcContentsFilePath.replaceAll("\\\\", "/");

								String srcFileUid = srcContentsFilePath.substring(0, srcContentsFilePath.indexOf("/"));
								UserInfo dstUserInfo = null;
								try{ dstUserInfo = restoreResult.userInfoTable.get(Integer.valueOf(srcFileUid)); } catch(Exception e) {logger.trace(e);}
								if (dstUserInfo == null) dstUserInfo = restoreResult.userInfoTable.get(0);

								//contents/uid/layerid に配置変更
								dstContentsFileUrl = CONTENTS_PATH+dstUserInfo.userId+"/"+dstLayerInfo.layerId+"/"+srcContentsFileUrl.replaceAll("^/[^/]+/[^/]+/[^/]+/[^/]+/[^/]+/", "");
								dstContentsFile = new File(MAP_DIR+URLDecoder.decode(dstContentsFileUrl, "UTF-8"));
								dstContentsFileUrl = CONTEXT_PATH+dstContentsFileUrl;
							}
						}
					} else {
						dstContentsFileUrl = srcContentsFileUrl;
					}

					if (dstContentsFileUrl != null) {
						////////////////////////////////
						//ファイルをコピー
						if (dstContentsFile != null) {
							//dstFile = getUniqFile(dstFile);
							//ファイル移動
							if (regist) {
								if(!dstContentsFile.getParentFile().mkdirs()) logger.warn("can not mkdirs: "+dstContentsFile.getName());
								FileUtils.copyFile(srcContentsFile, dstContentsFile);
							}
						}

						////////////////////////////////
						//DBに追加
						UserInfo dstUserInfo = restoreResult.userInfoTable.get(Integer.valueOf(values[3]));
						if(dstUserInfo==null) dstUserInfo = restoreResult.userInfoTable.get(0);

						//ステートメント生成
						int idx = 0;
						if(isUpdate){
							if (values[6].length() > 0) statement.setTimestamp(++idx, Timestamp.valueOf(values[6]));
							else statement.setNull(++idx, Types.TIMESTAMP);

							statement.setLong(++idx, Long.parseLong(values[0]));
							statement.setLong(++idx, dstMapId==null?0:dstMapId);
							statement.setString(++idx, dstLayerInfo.layerId);//テーブルと同じレイヤIDが入る
							statement.setString(++idx, values[7]);
							statement.setString(++idx, dstContentsFileUrl);//url

						}else{
							statement.setLong(++idx, Long.parseLong(values[0]));
							statement.setLong(++idx, dstMapId==null?0:dstMapId);
							statement.setString(++idx, dstLayerInfo.layerId);//テーブルと同じレイヤIDが入る
							statement.setInt(++idx, dstUserInfo.userId);
							statement.setInt(++idx, Integer.parseInt(values[4]));
							statement.setInt(++idx, Integer.parseInt(values[5]));
							if (values[6].length() > 0) statement.setTimestamp(++idx, Timestamp.valueOf(values[6]));
							else statement.setNull(++idx, Types.TIMESTAMP);
							statement.setString(++idx, values[7]);
							statement.setString(++idx, dstContentsFileUrl);//url
							statement.setInt(++idx, Integer.parseInt(values[9]));
						}

						//DBに登録
						try {
							if (regist) statement.execute();
							System.out.println(statement);
						} catch (Exception e) {
							logger.debug(statement);
						}
					}
				}
			} finally {
				if(br!=null) br.close();
			}
		}
	}


	/**
	 * 登録情報の地物に添付されたファイルを復元する。FeatureIDを指定
	 * @param conn
	 * @param srcLayerId
	 * @param dstMapId
	 * @param dstLayerInfo
	 * @param fid
	 * @throws Exception
	 */
	private ContentsLayerFeatureFileRestoreResult restoreContentsLayerFeatureFiles(Connection conn, String srcLayerId, Long dstMapId, LayerInfo dstLayerInfo, Long fid, Long fileGId) throws Exception {

		// 復元結果
		ContentsLayerFeatureFileRestoreResult contentsLayerFeatureFileRestoreResult = new ContentsLayerFeatureFileRestoreResult();

		//パス取得
		final String contentsDir = MAP_DIR+CONTENTS_PATH;

		//DB内のfeatureのユーザIDとURLを変更
		File featureFilesFile = new File(tmpPath+"feature_files/_feature_files_"+srcLayerId);
		String featureFilesInsert = "INSERT INTO _feature_files (gid,mid,layerid,userid,status,type,time_upload,title,url,file_order) VALUES (?,?,?,?,?, ?,?,?,?,?)";
		PreparedStatement statement = conn.prepareStatement(featureFilesInsert);

		//ファイル情報のパスを修正してDBに追加
		if (featureFilesFile.isFile()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(featureFilesFile));
				String line;
				while ((line=br.readLine()) != null) {
					String[] values = line.split("\t");
					if (values.length == 0) continue;
					//URL変換
					String srcContentsFileUrl = values[8];
					String dstContentsFileUrl = null;
					File srcContentsFile = null;
					File dstContentsFile = null;

					Long targetFid = Long.parseLong(values[0]);
					if(!targetFid.equals(fileGId)){
						continue;
					}

					if (srcContentsFileUrl.startsWith("/")) {
						//無効なファイルなら登録しない
						if (srcContentsFileUrl.indexOf("/",1)>0) {
							srcContentsFile = new File(URLDecoder.decode(tmpPath+srcContentsFileUrl.substring(srcContentsFileUrl.indexOf("/",1)), "UTF-8"));

							//System.out.writeln(srcContentsFile);
							//tmpファイルが存在しなかったら登録しない
							if (srcContentsFile.exists()) {

								//String dstContentsPath = filePathTable.get(srcContentsFile.getPath());
								//if (dstContentsPath != null) dstContentsFileUrl = dstContentsPath.substring(contentsDir.length());

								String srcContentsFilePath = srcContentsFile.getPath().substring(contentsDir.length());

								// FORDEV
								srcContentsFilePath = srcContentsFilePath.replaceAll("\\\\", "/");

								String srcFileUid = srcContentsFilePath.substring(0, srcContentsFilePath.indexOf("/"));
								UserInfo dstUserInfo = null;
								try{ dstUserInfo = restoreResult.userInfoTable.get(Integer.valueOf(srcFileUid)); } catch(Exception e) {logger.trace(e);}
								if (dstUserInfo == null) dstUserInfo = restoreResult.userInfoTable.get(0);

								//contents/uid/layerid に配置変更
								dstContentsFileUrl = CONTENTS_PATH+dstUserInfo.userId+"/"+dstLayerInfo.layerId+"/"+srcContentsFileUrl.replaceAll("^/[^/]+/[^/]+/[^/]+/[^/]+/[^/]+/", "");
								dstContentsFile = new File(MAP_DIR+URLDecoder.decode(dstContentsFileUrl, "UTF-8"));
								dstContentsFileUrl = CONTEXT_PATH+dstContentsFileUrl;
							}
						}
					} else {
						dstContentsFileUrl = srcContentsFileUrl;
					}

					if (dstContentsFileUrl != null) {
						////////////////////////////////
						//ファイルをコピー
						if (dstContentsFile != null) {
							//dstFile = getUniqFile(dstFile);
							//ファイル移動
							if (regist) {
								if(!dstContentsFile.getParentFile().mkdirs()) logger.warn("can not mkdirs: "+dstContentsFile.getName());
								FileUtils.copyFile(srcContentsFile, dstContentsFile);
							}
						}

						////////////////////////////////
						//DBに追加
						UserInfo dstUserInfo = restoreResult.userInfoTable.get(Integer.valueOf(values[3]));
						if(dstUserInfo==null) dstUserInfo = restoreResult.userInfoTable.get(0);

						//ステートメント生成
						int idx = 0;
						statement.setLong(++idx, fid);
						statement.setLong(++idx, dstMapId==null?0:dstMapId);
						statement.setString(++idx, dstLayerInfo.layerId);//テーブルと同じレイヤIDが入る
						statement.setInt(++idx, dstUserInfo.userId);
						statement.setInt(++idx, Integer.parseInt(values[4]));
						statement.setInt(++idx, Integer.parseInt(values[5]));
						if (values[6].length() > 0) statement.setTimestamp(++idx, Timestamp.valueOf(values[6]));
						else statement.setNull(++idx, Types.TIMESTAMP);
						statement.setString(++idx, values[7]);
						statement.setString(++idx, dstContentsFileUrl);//url
						statement.setInt(++idx, Integer.parseInt(values[9]));
						//DBに登録
						try {
							if (regist) statement.execute();
							System.out.println(statement);
						} catch (Exception e) {
							logger.debug(statement);
							contentsLayerFeatureFileRestoreResult.exceptions.add(e);
						}
					}

					// 復元結果の記録
					contentsLayerFeatureFileRestoreResult.srcContentsFileUrls.add(srcContentsFileUrl);
					contentsLayerFeatureFileRestoreResult.dstContentsFileUrls.add(dstContentsFileUrl);
					contentsLayerFeatureFileRestoreResult.dstContentsFiles.add(dstContentsFile);
				}
			} finally {
				if(br!=null) br.close();
			}
		}

		return contentsLayerFeatureFileRestoreResult;
	}

	/**
	 * フィーチャファイルの情報
	 */
	public static class FeatureFileInfo {
		/**<div lang="ja"> ファイルのURL </div>*/
		/**<div lang="en"> URL of the file </div>*/
		public String url;
		/**<div lang="ja"> ファイルのタイトル </div>*/
		/**<div lang="en"> title of the file </div>*/
		public String title;
		/**<div lang="ja">アップロード日時</div>*/
		/**<div lang="en">upload date</div>*/
		public String time_upload;

		public String create_time;
		/** ファイルが登録されているFeatureId */
		public long gid;

		public long mid;

		public String layerid;

		public int userid;

		public int status;

		public int type;

		public int file_order;

	}

	/**
	 * 文字列を Timestamp オブジェクトに変換する（infinity 対応）
	 *
	 * @param str
	 * @return
	 */
	private Timestamp StringToTimestamp(String str) {
		if (str == null)
			return null;
		else if ("infinity".equals(str))
			return new Timestamp(PGStatement.DATE_POSITIVE_INFINITY);
		else if ("-infinity".equals(str))
			return new Timestamp(PGStatement.DATE_NEGATIVE_INFINITY);
		else
			return Timestamp.valueOf(str);
	}

	/**
	 * シーケンスを更新しないで次の値を取得する
	 *
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public long getNextSeqVal(String table) throws SQLException {
		long val = 0;
		String sql = "SELECT last_value, is_called FROM " + table + "_gid_seq";
		Statement stmt = null;
		stmt = conn.createStatement();
		ResultSet result = stmt.executeQuery(sql);
		if (result.next()) {
			val = result.getLong(1);
			if (result.getBoolean(2))
				val++;
		}
		return val;
	}

	/**
	 * シーケンスの値が指定された値より大きくなるように修正する
	 *
	 * @param table
	 * @param value
	 * @throws SQLException
	 */
	public void adjustSequence(String table, long value) throws SQLException {
		long seqVal = getNextSeqVal(table);
		if (seqVal < value) {
			String sql = "SELECT SETVAL('" + table + "_gid_seq', ?, false)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, value);
			pstmt.executeQuery();
			pstmt.close();
		}
	}

	/**
	 * _orgid の値を変更する
	 *
	 * @param table
	 * @param oldOrgid
	 * @param newOrgid
	 * @throws SQLException
	 */
	public void updateOrgid(String table, long oldOrgid, long newOrgid) throws SQLException {
		String sql = "UPDATE " + table + " SET _orgid = ? WHERE _orgid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setLong(1,  newOrgid);
		pstmt.setLong(2, oldOrgid);
		pstmt.executeUpdate();
	}

	/**
	 * time_from の値が指定された時刻より大きいレコードを削除する
	 *
	 * @param table
	 * @param timeTo
	 * @throws SQLException
	 */
	public void deleteByTimeFrom(String table, long _orgid, Timestamp timeTo) throws SQLException {
		String sql = "DELETE FROM " + table + " WHERE _orgid = ? AND time_from > ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setLong(1, _orgid);
		pstmt.setTimestamp(2, timeTo);
		pstmt.executeUpdate();
		pstmt.close();
	}

	/**
	 * time_to の値を、次の time_from の値 - 1 msec に設定する
	 *
	 * @param table
	 * @param _orgid
	 * @throws SQLException
	 */
	public void setTimeTo(String table, long _orgid, Timestamp startTime) throws SQLException {
		String sql = "UPDATE " + table + " t1 SET time_to = t2.new_time_to FROM"
				+ " (SELECT gid, LEAD(time_from) over (order by time_from) - INTERVAL '0.001' SECOND new_time_to"
				+ "  FROM " + table + " WHERE _orgid = ? AND time_from >= ?) t2"
				+ " WHERE t1.gid = t2.gid AND t1._orgid = ? AND time_from >= ? AND new_time_to IS NOT NULL";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setLong(1, _orgid);
		pstmt.setTimestamp(2, startTime);
		pstmt.setLong(3, _orgid);
		pstmt.setTimestamp(4, startTime);
		pstmt.executeUpdate();
		pstmt.close();
	}

	// ---- 以上通信途絶対応
}
