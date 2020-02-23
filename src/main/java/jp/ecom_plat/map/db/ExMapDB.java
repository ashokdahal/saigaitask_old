/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.map.db;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.geoserver.util.ISO8601Formatter;

import jp.ecom_plat.map.db.FeatureResult.AttrResult;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.map.file.SLDFile;
import jp.ecom_plat.saigaitask.action.ServiceException;

/**
 * {@link jp.ecom_plat.map.db.MapDB} を拡張するクラスです.
 */
public class ExMapDB {
	
	/** Logger log4j */
	private static Logger logger = Logger.getLogger(ExMapDB.class.getName());

	/** PostgerSQL -∞ Timestamp */
	public static Timestamp negativeInfinity;
	/** PostgerSQL +∞ Timestamp */
	public static Timestamp positiveInfinity;
	/** ISO8601 Formatter 紀元前対応 */
	public static final ISO8601Formatter iso8601df = new ISO8601Formatter();

	static {
		// PostgreSQL の -infinity, infinity を取得して、チェックに用いる
		Connection con = null;
		Statement s = null;
		ResultSet rs = null;
		try {
			con = getDataSourceConnection();
			String sql = "SELECT '-infinity'::timestamp, 'infinity'::timestamp;";
			s = con.createStatement();
			rs = s.executeQuery(sql);
			if(rs.next()) {
				Timestamp negainf = rs.getTimestamp(1);
				logger.debug("negative infinity: "+negainf.getTime()+" "+iso8601df.format(negainf));
				Timestamp posiinf = rs.getTimestamp(2);
				logger.debug("positive infinity: "+posiinf.getTime()+" "+iso8601df.format(posiinf));
				negativeInfinity = negainf;
				positiveInfinity = posiinf;
			}
		} catch(Exception e) {
			
		} finally {
			if(rs!=null) try { rs.close(); } catch(Exception e) {}
			if(s!=null) try { s.close(); } catch(Exception e) {}
			if(con!=null) try { con.close(); } catch(Exception e) {}
		}
	}

	public static Connection getDataSourceConnection() throws SQLException {
		try {
			return MapDB.getMapDB().getDataSourceConnection();
		} catch(IllegalAccessError e) {
			// MapDB.getDataSourceConnection() が本来は同じパッケージなので呼び出し可能だが、
			// クラスローダが異なる場合に IllegalAccessError が発生することがある
			// 開発では Spring Devtools を有効化するために、
			// eコミJAR は sun.misc.Launcher$AppClassLoader
			// ExMapDB  は org.springframework.boot.devtools.restart.classloader.RestartClassLoader
			// という別々のクラスローダで読み込まれるためこのエラーが発生する。
			try {
				Method method = MapDB.class.getDeclaredMethod("getDataSourceConnection");
				method.setAccessible( true );
				return (Connection) method.invoke( MapDB.getMapDB() );
			} catch(Exception e2) {
				String msg = "FATAL ERROR: could not get ecommap connection";
				logger.error(msg, e2);
				throw new ServiceException(msg, e2);
			}
		}
	}

	/**
	 * <div lang="ja">
	 * PostGISにテーブル作成. コンテンツ格納テーブルも作成
	 * @param layerInfo テーブルを作成するLayerInfo
	 * @param geometryType PostGISのGeometry型文字列(POINY,POLYLINE,POLYGON)
	 * @param coltype 
	 * @return 生成できたらtrue エラーならfalse
	 * </div>
	 * 
	 * <div lang="en">
	 * Create tables for PostGIS. the table in which the contents is stored is also created
	 * @param layerInfo the LayerInfo the tables are created for
	 * @param geometryType the Geometry type string of PostGIS (POINY, POLYLINE, POLYGON)
	 * @param coltype 
	 * @throws SQLException 
	 * return true if created, false if error
	 * </div>
	 */
	public static boolean createGeometryTable(LayerInfo layerInfo, String geometryType, String[] coltype) throws SQLException
	{
		Connection conn = getDataSourceConnection();
		try {
			boolean result = true;
			conn.setAutoCommit(false);
			Statement statement = conn.createStatement();
			String layerId = StringEscapeUtils.escapeSql(layerInfo.layerId);
			geometryType = StringEscapeUtils.escapeSql(geometryType);
			
			//<div lang="ja">属性を設定したPostGISのフィーチャテーブルを作成</div>
			//<div lang="en">create a PostGIS feature table that contains the attributes</div>
			StringBuilder buf = new StringBuilder(1024);
			buf.append("CREATE TABLE \"").append(layerId).append("\" (");
			//<div lang="ja">gid追加</div>select
			//<div lang="en">add gid</div>
			buf.append("gid bigserial PRIMARY KEY");
			//<div lang="ja">残りの属性</div>
			//<div lang="en">other contents</div>
			int i = 0;
			for (AttrInfo attrInfo : layerInfo.getAttrIterable()) {
				//<div lang="ja">型はテキスト限定</div>
				//<div lang="en">data type is restricted to text</div>
				buf.append(",\"").append(StringEscapeUtils.escapeSql(attrInfo.attrId)).append("\" "+coltype[i]);
				i++;
			}
			buf.append(") INHERITS (geometry_base)");
			try {
				logger.debug(buf.toString());
				statement.execute(buf.toString());
				conn.commit();
			} catch (SQLException e) {
				logger.error("createGeometryTable create : "+e); conn.rollback(); result = false;
			}
			try {
				//<div lang="ja">AddGeometryColumn呼び出し（the_geom追加+geometry_columnsに情報設定）</div>
				//<div lang="en">call AddGeometryColumn (set attirbute to the_geom+geometry_columns)</div>
				i = 1;
				PreparedStatement pstatement = conn.prepareStatement(MapDB.SQL_POSTGIS_ADDGEOMETRYCOLUMN);
				pstatement.setString(i++, layerId);
				pstatement.setString(i++, geometryType);
				pstatement.execute();
				conn.commit();
			} catch (SQLException e) {
				logger.error("createGeometryTable AddGeometry : "+e); conn.rollback(); result = false;
			}
			try {
				//GIST index
				statement.execute("CREATE INDEX \""+layerId+"_geom\" ON \""+layerId+"\" USING GIST (the_geom gist_geometry_ops)");
				conn.commit();
				//GIST index EPSG:32653
				statement.execute("CREATE INDEX \""+layerId+"_geom32653\" ON \""+layerId+"\" USING GIST (ST_Transform(the_geom,32653))");
				conn.commit();
				//GIST Centroid
				statement.execute("CREATE INDEX \""+layerId+"_geomCentroid\" ON \""+layerId+"\" USING GIST (ST_Centroid(the_geom))");
				conn.commit();
				statement.execute("CREATE INDEX \""+layerId+"_geomCentroidX\" ON \""+layerId+"\" (ST_X(ST_Centroid(the_geom)))");
				conn.commit();
				statement.execute("CREATE INDEX \""+layerId+"_geomCentroidY\" ON \""+layerId+"\" (ST_Y(ST_Centroid(the_geom)))");
				conn.commit();
				
			} catch (SQLException e) {
				logger.error("createGeometryTable CreateIndex : "+e); conn.rollback(); result = false;
			}
			
			conn.commit();
			return result;
		} finally { conn.close(); }
	}

	/*
	public static Connection getConnection() throws SQLException {
		return getDataSourceConnection();
	}
	*/

	/**
	 * 地図情報の物理削除
	 * @param mapInfo
	 * @return 削除結果
	 * @throws SQLException
	 */
	public static boolean deleteMapInfo(MapInfo mapInfo) throws SQLException
	{
		boolean deleted = false;
		if(mapInfo==null) {
			logger.info("ExMapDB.deleteLayerMapInfo mapInfo is null");
			return deleted;
		}
		
		// INFOログ用メッセージ
		String logInfo = "ExMapDB.deleteMapInfo("+mapInfo.mapId+") ";

		// check if MapDir exists
		File mapDir = new File(SLDFile.getMapDir());
		if(mapDir.exists()==false) {
			String msg = "Can not delete contents feature file dirctory. ecommap directory not found.";
			logger.error(logInfo+msg+" :"+mapDir.getPath());
			throw new RuntimeException(msg);
		}

		logger.info("BEGIN "+logInfo);

		Connection conn = getDataSourceConnection();
		PreparedStatement pstatement = null;
		try {

			// begin;
			conn.setAutoCommit(false);

			// 地図
			{
				List<String> sqls = new ArrayList<String>();
				sqls.add("DELETE FROM _widget_option WHERE map_id=? -- "+logInfo);
				sqls.add("DELETE FROM _option WHERE map_id=? -- "+logInfo);
				sqls.add("DELETE FROM _pdf_range WHERE map_id=? -- "+logInfo);
				sqls.add("DELETE FROM _groupmap WHERE map_id=? -- "+logInfo);
				sqls.add("DELETE FROM _auth WHERE auth_mapid=? -- "+logInfo);
				sqls.add("DELETE FROM _list_condition WHERE map_id=? -- "+logInfo);
				sqls.add("DELETE FROM _layout WHERE map_id=? -- "+logInfo);
				sqls.add("DELETE FROM _aggregatelog_result WHERE map_id=? -- "+logInfo);
				sqls.add("DELETE FROM _initrange_map WHERE map_id=? -- "+logInfo);
				sqls.add("DELETE FROM _widget_layout WHERE map_id=? -- "+logInfo);
				//sqls.add("DELETE FROM _layer WHERE owner_mapid=? -- "+logInfo);
				sqls.add("DELETE FROM _tag WHERE tag_mapid=? -- "+logInfo);
				sqls.add("DELETE FROM _maplayer WHERE map_id=? -- "+logInfo);
				sqls.add("DELETE FROM _map WHERE map_id=? -- "+logInfo);

				// execute sql
				for(String sql : sqls) {
					try {
						pstatement = conn.prepareStatement(sql);
						pstatement.setLong(1, mapInfo.mapId);
						int executeNum = pstatement.executeUpdate();
						pstatement.close();
						logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());
					} finally {
						if(pstatement!=null && !pstatement.isClosed()) {
							pstatement.close();
						}
					}
				}
			}

			// deleteFiles
			// 削除ファイルリスト
			List<File> deleteFiles = new ArrayList<File>();
			{
				// delete legend
				File legendDir = new File(mapDir, "files/legend");
				if(legendDir.exists() && legendDir.isDirectory()) {
					File mapLegendDir = new File(legendDir, String.valueOf(mapInfo.mapId));
					if(mapLegendDir.exists()) {
						deleteFiles.add(mapLegendDir);
					}
				}

				// delete memo
				File memoDir = new File(mapDir, "files/memo");
				if(memoDir.exists() && memoDir.isDirectory()) {
					File mapMemoDir = new File(memoDir, String.valueOf(mapInfo.mapId));
					if(mapMemoDir.exists()) {
						deleteFiles.add(mapMemoDir);
					}
				}

				// delete preview(MapPreviewFile)
				File previewDir = new File(mapDir, "files/preview");
				if(previewDir.exists() && previewDir.isDirectory()) {
					for(File subPreviewDir : previewDir.listFiles()) {
						// subPreviewDir: 0から始まる
						if(subPreviewDir.exists() && subPreviewDir.isDirectory()) {
							File mapPreviewDir = new File(subPreviewDir, String.valueOf(mapInfo.mapId));
							if(mapPreviewDir.exists()) {
								deleteFiles.add(mapPreviewDir);
							}
						}
					}
				}

				// delete styles
				File stylesDir = new File(mapDir, "files/styles");
				if(stylesDir.exists() && stylesDir.isDirectory()) {
					File mapStylesDir = new File(stylesDir, String.valueOf(mapInfo.mapId));
					if(mapStylesDir.exists()) {
						deleteFiles.add(mapStylesDir);
					}
				}
			}

			// Exec delete files
			logger.info(logInfo+"BEGIN delete files");
			int executeNum = deleteFiles(deleteFiles);
			logger.info(logInfo+"END delete files: "+executeNum);

			// commit
			conn.commit();
			deleted = true;
		} catch(Exception e) {
			logger.error("Error "+logInfo, e);
			// rollback, if occur exception
			if(conn!=null) conn.rollback();
			throw new RuntimeException("Error "+logInfo, e);
		} finally {
			try {
				if(pstatement!=null && !pstatement.isClosed()) {
					pstatement.close();
				}
			} finally {
				if(conn!=null && !conn.isClosed()) {
					conn.close();
				}
			}
		}

		logger.info("END "+logInfo+"deleted:"+deleted);
		return deleted;
	}

	/**
	 * レイヤを完全に削除する.
	 * @param layerInfo 削除対象レイヤ情報
	 * @return 削除結果
	 * @throws SQLException
	 */
	public static boolean deleteLayerInfo(LayerInfo layerInfo) throws SQLException
	{
		boolean deleted = false;
		if(layerInfo==null) {
			logger.info("ExMapDB.deleteLayerInfo layerInfo is null");
			return deleted;
		}
		
		// INFOログ用メッセージ
		String logInfo = "ExMapDB.deleteLayerInfo("+layerInfo.layerId+") ";

		logger.info("BEGIN "+logInfo);

		// check if MapDir exists
		File mapDir = new File(SLDFile.getMapDir());
		if(mapDir.exists()==false) {
			String msg = "Can not delete contents feature file dirctory. ecommap directory not found.";
			logger.error(logInfo+msg+" :"+mapDir.getPath());
			throw new RuntimeException(msg);
		}

		Connection conn = getDataSourceConnection();
		PreparedStatement pstatement = null;
		try {
			int executeNum = 0;
			int parameterIndex = 1;

			// begin;
			conn.setAutoCommit(false);

			// すべての地図からレイヤを取り除く
			{
				// delete _maplayer
				pstatement = conn.prepareStatement("DELETE FROM _maplayer WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());
			}

			// このレイヤが検索対象となっている検索条件の削除
			{
				// delete _list_condition
				pstatement = conn.prepareStatement("DELETE FROM _list_condition WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());
			}

			// レイヤのフィーチャ関連のデータを削除する
			{
				// delete _feature_comment
				pstatement = conn.prepareStatement("DELETE FROM _feature_comment WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _feature_files
				pstatement = conn.prepareStatement("DELETE FROM _feature_files WHERE layerid=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _feature_links
				pstatement = conn.prepareStatement("DELETE FROM _feature_files WHERE layerid=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _feature_log
				pstatement = conn.prepareStatement("DELETE FROM _feature_log WHERE layerid=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());
			}

			// レイヤの管理データを削除する
			{
				// delete _layer_comment
				pstatement = conn.prepareStatement("DELETE FROM _layer_comment WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _grouplayer 公開範囲
				pstatement = conn.prepareStatement("DELETE FROM _grouplayer WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _report_style 帳票スタイル
				pstatement = conn.prepareStatement("DELETE FROM _report_style WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delelete _cswlayer
				// TODO: クリアリングハウスからメタデータを削除する
				pstatement = conn.prepareStatement("DELETE FROM _cswlayer WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delelete _attr
				pstatement = conn.prepareStatement("DELETE FROM _attr WHERE attr_layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _layer
				pstatement = conn.prepareStatement("DELETE FROM _layer WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _option
				pstatement = conn.prepareStatement("DELETE FROM _option WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _widget_option
				pstatement = conn.prepareStatement("DELETE FROM _widget_option WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());
				
				// delete _aggregatelog_result
				pstatement = conn.prepareStatement("DELETE FROM _aggregatelog_result WHERE layer_id=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _notice
				pstatement = conn.prepareStatement("DELETE FROM _notice WHERE layerid=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _notice_data
				pstatement = conn.prepareStatement("DELETE FROM _notice_data WHERE layerid=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _auth
				pstatement = conn.prepareStatement("DELETE FROM _auth WHERE auth_layerid=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

				// delete _tag
				pstatement = conn.prepareStatement("DELETE FROM _tag WHERE tag_layerid=? -- "+logInfo);
				parameterIndex = 1;
				pstatement.setString(parameterIndex++, layerInfo.layerId);
				executeNum = pstatement.executeUpdate();
				pstatement.close();
				logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());
			}
			// todo: _replacedlayer

			// ※先にレイヤを削除してそのあとにGeoServerの削除リクエストを投げると、
			// 　eコミマップ側のトランザクションで AccessExclusiveLock がテーブルにロックされ、
			// 　GeoServer の削除処理では一度 select を行うため、 テーブルに AccessShareLock ロックをかけるが、
			// 　ここでデッドロックとなり、GeoServer側の処理がeコミマップのトランザクション待ちとなってしまう。
			// 　そのため、先にGeoServerに削除リクエストを投げる
			// delete from geoserver
			if(layerInfo.type==LayerInfo.TYPE_LOCAL) {
				deleteGeoServerFeature(layerInfo.layerId);
			}

			// drop layer
			pstatement = conn.prepareStatement("DROP TABLE IF EXISTS "+StringEscapeUtils.escapeSql(layerInfo.layerId)+" -- "+logInfo);
			executeNum = pstatement.execute() ? 1 : 0;
			pstatement.close();
			logger.info(logInfo+"SQL executeNum="+executeNum+" "+pstatement.toString());

			// 削除ファイルリスト
			boolean doReloadGeoServerCatalog = false;
			List<File> deleteFiles = new ArrayList<File>();
			{
				// delete contents
				File contentsDir = new File(mapDir, "files/contents");
				if(contentsDir.exists() && contentsDir.isDirectory()) {
					for(File citeContentsDir : contentsDir.listFiles()) {
						if(citeContentsDir.exists() && citeContentsDir.isDirectory()) {
							for(File layerContentsDir : citeContentsDir.listFiles()) {
								if(layerContentsDir.exists() && layerContentsDir.isDirectory()) {
									if(layerInfo.layerId.equals(layerContentsDir.getName())) {
										deleteFiles.add(layerContentsDir);
									}
								}
							}
						}
					}
				}

				// do nothing: css, form, icons, imported, kml

				// delete legend
				File legendDir = new File(mapDir, "files/legend");
				if(legendDir.exists() && legendDir.isDirectory()) {
					for(File subLegendDir : legendDir.listFiles()) {
						// subLegendDir: 数値だけなら地図ID, 先頭にcが付くならサイトID
						if(subLegendDir.exists() && subLegendDir.isDirectory()) {
							for(File legendImageFile : subLegendDir.listFiles()) {
								// 拡張子を問わず、ファイル名がレイヤIDとなっているファイルを削除対象とする
								if(legendImageFile.exists() && legendImageFile.isFile()) {
									if(legendImageFile.getName().startsWith(layerInfo.layerId+".")) {
										deleteFiles.add(legendImageFile);
									}
								}
							}
						}
					}
				}

				// do nothing: memo, memo_buttons

				// delete metadata
				File metadataDir = new File(mapDir, "files/metadata");
				if(metadataDir.exists() && metadataDir.isDirectory()) {
					for(File subMetadataDir : metadataDir.listFiles()) {
						// subMetadataDir: 先頭にcが付くならサイトID
						if(subMetadataDir.exists() && subMetadataDir.isDirectory()) {
							for(File metadataFile : subMetadataDir.listFiles()) {
								// 拡張子を問わず、ファイル名がレイヤIDとなっているファイルを削除対象とする
								if(metadataFile.exists() && metadataFile.isFile()) {
									if(metadataFile.getName().startsWith(layerInfo.layerId+".")) {
										deleteFiles.add(metadataFile);
									}
								}
							}
						}
					}
				}

				// do nothing: preview(MapPreviewFile)

				// delete reportstyles
				File reportstylesDir = new File(mapDir, "files/reportstyles");
				if(reportstylesDir.exists() && reportstylesDir.isDirectory()) {
					for(File layerReportstyleDir : reportstylesDir.listFiles()) {
						// ディレクトリ名がレイヤIDとなっているディレクトリを削除対象とする
						if(layerReportstyleDir.exists() && layerReportstyleDir.isFile()) {
							if(layerReportstyleDir.getName().startsWith(layerInfo.layerId)) {
								deleteFiles.add(layerReportstyleDir);
							}
						}
					}
				}

				// delete styles
				File stylesDir = new File(mapDir, "files/styles");
				if(stylesDir.exists() && stylesDir.isDirectory()) {
					for(File subStylesDir : stylesDir.listFiles()) {
						// subStylesDir: 数値だけなら地図ID, 先頭にcが付くならサイトID
						if(subStylesDir.exists() && subStylesDir.isDirectory()) {
							for(File stylesFile : subStylesDir.listFiles()) {
								// 拡張子を問わず、ファイル名がレイヤIDとなっているファイルを削除対象とする
								if(stylesFile.exists() && stylesFile.isFile()) {
									if(stylesFile.getName().startsWith(layerInfo.layerId+".")) {
										deleteFiles.add(stylesFile);
									}
								}
							}
						}
					}
				}

				// do nothing template, terms, thumb
			}

			// delete layer.xml from geoserver datastore
			{
				File datastoreDir = new File(mapDir, "../../geoserver/data_dir/workspaces/map/map/");
				if(datastoreDir.exists()) {
					File layerDir = new File(datastoreDir, layerInfo.layerId);
					if(layerDir.exists()) {
						deleteFiles.add(layerDir);
						doReloadGeoServerCatalog = true;
					}
				}
				else {
					throw new RuntimeException(logInfo+"GeoServer DataStore directory("+datastoreDir.getAbsolutePath()+") not found");
				}
			}
			

			// Exec delete files
			logger.info(logInfo+"BEGIN delete files");
			executeNum = deleteFiles(deleteFiles);
			logger.info(logInfo+"END delete files: "+executeNum);

			// reload GeoServer Catalog
			if(doReloadGeoServerCatalog) {
				reloadGeoServerCatalog();
			}

			// commit
			conn.commit();
			deleted = true;
		} catch(Exception e) {
			logger.error("Error "+logInfo, e);
			// rollback, if occur exception
			if(conn!=null) conn.rollback();
			throw new RuntimeException("Error "+logInfo, e);
		} finally {
			try {
				if(pstatement!=null && !pstatement.isClosed()) {
					pstatement.close();
				}
			} finally {
				if(conn!=null && !conn.isClosed()) {
					conn.close();
				}
			}
		}

		logger.info("END "+logInfo+"deleted:"+deleted);
		return deleted;
	}

	// select _feature_files
	/*
	pstatement = conn.prepareStatement("SELECT * FROM _feature_files WHERE layer_id=?");
	parameterIndex = 1;
	pstatement.setString(parameterIndex++, layerInfo.layerId);
	ResultSet rs = pstatement.executeQuery();
	while(rs.next()) {
		// read record
		Map<String, Object> record = new HashMap<String, Object>();
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int columnIdx=1; columnIdx<=rsmd.getColumnCount(); columnIdx++) {
				String columnLabel = rsmd.getColumnLabel(columnIdx);
				record.put(columnLabel, rs.getObject(columnIdx));
			}
		}

		// delete file
		String url = (String) record.get("url");
		url.indexOf("/");
	}
	*/

	/**
	 * Reloads the GeoServer catalog and configuration from disk.
	 * GeoServer REST configuration API:
	 * POST "http://localhost:18080/geoserver/rest/reload"
	 * @return reload flag
	 * @throws MalformedURLException 
	 * @throws IOException 
	 */
	public static boolean reloadGeoServerCatalog() throws MalformedURLException, IOException
	{
		String username = ResourceInfo.getGeoserverUsername();
		String password = ResourceInfo.getGeoserverPassword();

		Logger logger = Logger.getLogger(ExMapDB.class);

		String localRootURL = PathInfo.getLocalRootURL();
		String url = localRootURL+"/geoserver/rest/reload";
		logger.info("reloadGeoServerCatalog: "+url);
		
		HttpURLConnection loadConn = (HttpURLConnection)(new URL(url).openConnection());
		loadConn.setRequestMethod("POST");
		loadConn.setRequestProperty("Authorization", "Basic "+Base64.encodeBase64String(String.valueOf(username+":"+password).getBytes()));
		int code = loadConn.getResponseCode();
		logger.info("reloadGeoServerCatalog: code="+code);
		loadConn.disconnect();
		logger.info("########## reloadGeoServerCatalog end1");
		if (code >= 400) {
			//<div lang="ja">再接続</div>
			//<div lang="ja">recconect</div>
			loadConn = (HttpURLConnection)(new URL(url).openConnection());
			code = loadConn.getResponseCode();
			logger.info("reloadGeoServerCatalog: code="+code);
			loadConn.disconnect();
			if (code >= 400) {
				return false;
			}
		}
		return true;
	}

	/**
	 * DELETE http://myserver:myport/geoserver/rest/workspaces/workspace/datastores/datastore/featuretypes/typename?recurse=true
	 *        http://localhost:18080/geoserver/rest/workspaces/map/datastores/map/featuretypes/c10?recurse=true
	 * @param featureType
	 * @return deleted
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static boolean deleteGeoServerFeature(String featureType) throws MalformedURLException, IOException
	{
		String username = ResourceInfo.getGeoserverUsername();
		String password = ResourceInfo.getGeoserverPassword();

		Logger logger = Logger.getLogger(ExMapDB.class);

		String url = PathInfo.getLocalRootURL()+"/geoserver/rest"
		+"/workspaces/"+ResourceInfo.getGeoserverWorkspace()
		+"/datastores/"+ResourceInfo.getGeoserverDataStore()
		+"/featuretypes/"+featureType+"?recurse=true";
		logger.info("deleteGeoServerFeature: "+url);
		
		HttpURLConnection loadConn = (HttpURLConnection)(new URL(url).openConnection());
		loadConn.setRequestMethod("DELETE");
		loadConn.setRequestProperty("Authorization", "Basic "+Base64.encodeBase64String(String.valueOf(username+":"+password).getBytes()));
		int code = loadConn.getResponseCode();
		logger.info("deleteGeoServerFeature: code="+code);
		loadConn.disconnect();
		logger.info("########## deleteGeoServerFeature end1");
		if (code >= 400) {
			//<div lang="ja">再接続</div>
			//<div lang="ja">recconect</div>
			loadConn = (HttpURLConnection)(new URL(url).openConnection());
			code = loadConn.getResponseCode();
			logger.info("deleteGeoServerFeature: code="+code);
			loadConn.disconnect();
			if (code >= 400) {
				return false;
			}
		}
		return true;
	}

	public static int deleteFiles(List<File> deleteFiles) throws IOException {
		int executeNum = 0;
		for(int deleteFilesIndex=0; deleteFilesIndex<deleteFiles.size(); deleteFilesIndex++) {
			File deleteFile = deleteFiles.get(deleteFilesIndex);
			if(deleteFile.exists()==false) {
				logger.warn("delete file not exists: "+deleteFile.getPath());
				continue;
			}
			if(deleteFile.isDirectory()) {
				FileUtils.deleteDirectory(deleteFile);
				if(!deleteFile.exists()) {
					logger.info("Delete directory: "+deleteFile.getPath());
					executeNum++;
				}
				else {
					logger.warn("Failed to delete file: "+deleteFile.getPath());
				}
			}
			else {
				if(deleteFile.delete()) {
					logger.info("Delete file: "+deleteFile.getPath());
					executeNum++;
				}
				else {
					logger.warn("Failed to delete file: "+deleteFile.getPath());
				}
			}
		}
		return executeNum;
	}

	/**
	 * レイヤの time_from の min/max を取得する。
	 * @param layerId レイヤID
	 * @return Timestamp[]{begin, end}
	 */
	public static Timestamp[] getLayerTimefromMinMax(String layerId) {
		MapDB mapDB = MapDB.getMapDB();
		LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
		Timestamp[] timefromto = new Timestamp[]{null, null};
		if(layerInfo!=null && TimeSeriesType.HISTORY.equals(layerInfo.timeSeriesType)) {
			Connection con = null;
			Statement s = null;
			ResultSet rs = null;
			try {
				con = getDataSourceConnection();
				String sql = "SELECT min(time_from), max(time_from) FROM \""+StringEscapeUtils.escapeSql(layerId)+"\"";
				s = con.createStatement();
				rs = s.executeQuery(sql);
				if(rs.next()) {
					timefromto[0] = (Timestamp) rs.getObject(1);
					timefromto[1] = (Timestamp) rs.getObject(2);
				}
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				if(rs!=null) try { rs.close(); } catch(Exception e) {}
				if(s!=null) try { s.close(); } catch(Exception e) {}
				if(con!=null) try { con.close(); } catch(Exception e) {}
			}
		}
		return timefromto;
	}

	/**
	 * @param timestamp 判定したいTimestamp
	 * @return PostgreSQL の-infinity の判定
	 */
	public static boolean isNegativeInfinity(Timestamp timestamp) { return timestamp!=null && timestamp.getTime()==negativeInfinity.getTime(); }
	/**
	 * @param iso8601 判定したいTimestampのISO8601
	 * @return PostgreSQL の-infinity の判定
	 */
	public static boolean isNegativeInfinity(String iso8601) { return ExMapDB.iso8601df.format(negativeInfinity).equals(iso8601); }
	/**
	 * @param timestamp 判定したいTimestamp
	 * @return PostgreSQL の infinity の判定
	 */
	public static boolean isPositiveInfinity(Timestamp timestamp) { return timestamp!=null && timestamp.getTime()==positiveInfinity.getTime(); }
	/**
	 * @param iso8601 判定したいTimestampのISO8601
	 * @return PostgreSQL の infinity の判定
	 */
	public static boolean isPositiveInfinity(String iso8601) { return ExMapDB.iso8601df.format(positiveInfinity).equals(iso8601); }

	/**
	 * eコミマップのプログラム{@link FeatureDB#getTimeQuery(LayerInfo, Date[])}に委譲.
	 * TimeSeriesType.HISTORY で 引数１つの場合、下記のようなクエリが作成される.
	 * (
	 *  ((time_to IS NULL) AND (time_from = '2016-06-15 10:18:21.623'::timestamp without time zone))
	 *  OR
	 *  ((time_to IS NOT NULL) AND (time_from <= '2016-06-15 10:18:21.623'::timestamp without time zone) AND (time_to >= '2016-06-15 10:18:21.623'::timestamp without time zone))
	 * )
	 * 
	 * @param layerInfo 時系列種別を判断するために利用
	 * @param timeParam 時間条件nullは不可（配列要素は１つか２つまで）
	 * @return 時間に関するWHERE句
	 */
	public static String getTimeQuery(LayerInfo layerInfo, Date[] timeParam) {
		try {
			 return FeatureDB.getTimeQuery(layerInfo, timeParam);
		} catch(IllegalAccessError e) {
			// 本来は同じパッケージなので呼び出し可能だが、
			// クラスローダが異なる場合に IllegalAccessError が発生することがある
			// 開発では Spring Devtools を有効化するために、
			// eコミJAR は sun.misc.Launcher$AppClassLoader
			// ExMapDB  は org.springframework.boot.devtools.restart.classloader.RestartClassLoader
			// という別々のクラスローダで読み込まれるためこのエラーが発生する。
			try {
				Method method = FeatureDB.class.getDeclaredMethod("getTimeQuery", LayerInfo.class, Date[].class);
				method.setAccessible( true );
				return (String) method.invoke( null, layerInfo, timeParam );
			} catch(Exception e2) {
				String msg = "FATAL ERROR: could not get timeQuery";
				logger.error(msg, e2);
				throw new ServiceException(msg, e2);
			}
		}
	}

	/**
	 * 属性を HashMap で取得する関数（eコミにないので追加）
	 * @param featureResult eコミの地物取得結果
	 * @return 属性Map
	 */
	public static HashMap<String, String> getAttributes(FeatureResult featureResult) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		for(int i=0; i<featureResult.countAttrResult(); i++) {
			AttrResult attrResult = featureResult.getAttrResult(i);
			attributes.put(attrResult.getAttrId(), attrResult.getAttrValue());
		}
		return attributes;
	}

	/**
	 * _orgid と 時間から、gid を取得する
	 * @param layerId レイヤID
	 * @param _orgid orgid
	 * @param timeParam 時間パラメータ
	 * @return 該当する時間の gid
	 */
	public static long getHistoryFeatureId(String layerId, long _orgid, Date[] timeParam) {
		Connection con = null;
		try {
			con = getDataSourceConnection();
			try {
				return FeatureDB.getHistoryFeatureId(con, layerId, _orgid, timeParam);
			} catch(IllegalAccessError e) {
				// 本来は同じパッケージなので呼び出し可能だが、
				// クラスローダが異なる場合に IllegalAccessError が発生することがある
				// 開発では Spring Devtools を有効化するために、
				// eコミJAR は sun.misc.Launcher$AppClassLoader
				// ExMapDB  は org.springframework.boot.devtools.restart.classloader.RestartClassLoader
				// という別々のクラスローダで読み込まれるためこのエラーが発生する。
				try {
					Method method = FeatureDB.class.getDeclaredMethod("getHistoryFeatureId", Connection.class, String.class, long.class, Date[].class);
					method.setAccessible( true );
					return (long) method.invoke( null, con, layerId, _orgid, timeParam);
				} catch(Exception e2) {
					String msg = "FATAL ERROR: could not getHistoryFeatureId";
					logger.error(msg, e2);
					throw new ServiceException(msg, e2);
				}
			}
		} catch(Exception e) {
			throw new ServiceException(e);
		} finally {
			if(con!=null) try { con.close(); } catch(Exception e) {}
		}
	}

	/**
	 * ResourceInfo.properties のクラス
	 */
	static class ResourceInfo {

		/** ResourceInfo.properties ResourceBundle */
		public static ResourceBundle bundle = ResourceBundle.getBundle("ResourceInfo", Locale.getDefault());

		/** @return GeoServer Workspace */
		public static String getGeoserverWorkspace() {
			return "map";
		}

		/** @return GeoServer DataStore */
		public static String getGeoserverDataStore() {
			return bundle.getString("GEOSERVER_DATA_STORE");
		}

		/** @return GeoServer Username(default "admin") */
		public static String getGeoserverUsername() {
			String username = "admin";
			String key = "GEOSERVER_USERNAME";
			if(bundle.containsKey(key)) {
				username = bundle.getString(key);
			}
			else Logger.getLogger(ResourceInfo.class).warn(key+" not found on ResourceInfo.properties");
			return username;
		}

		/** @return GeoServer Password(default "geoserver") */
		public static String getGeoserverPassword() {
			String password = "geoserver";
			String key = "GEOSERVER_PASSWORD";
			if(bundle.containsKey(key)) {
				password = bundle.getString(key);
			}
			else Logger.getLogger(ResourceInfo.class).warn(key+" not found on ResourceInfo.properties");
			return password;
		}
	}

	/**
	 * PathInfo.properties のクラス
	 */
	static class PathInfo {

		/** PathInfo.properties ResourceBundle */
		public static ResourceBundle bundle = ResourceBundle.getBundle("PathInfo", Locale.getDefault());

		/** @return GeoServer Workspace */
		public static String getLocalRootURL() {
			return bundle.getString("LOCAL_ROOT_URL");
		}
	}
}
