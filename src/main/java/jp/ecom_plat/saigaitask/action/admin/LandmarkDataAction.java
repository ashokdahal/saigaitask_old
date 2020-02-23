/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.util.Constants.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mozilla.universalchardet.UniversalDetector;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.exception.SQLRuntimeException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.LandmarkData;
import jp.ecom_plat.saigaitask.entity.db.LandmarkInfo;
import jp.ecom_plat.saigaitask.entity.names.LandmarkDataNames;
import jp.ecom_plat.saigaitask.form.db.LandmarkDataForm;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.LandmarkDataService;
import jp.ecom_plat.saigaitask.service.db.LandmarkInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.JqGridUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONObject;

/**
 * 目標物データのアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/landmarkData")
public class LandmarkDataAction extends AbstractAdminAction<LandmarkData> {
	protected LandmarkDataForm landmarkDataForm;

	/** サービスクラス */
	@Resource
	protected LandmarkDataService landmarkDataService;
	@Resource
	protected LandmarkInfoService landmarkInfoService;
	@Resource
	protected FileUploadService fileUploadService;


	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/landmarkData/index";
	}

	/**
	 * 一覧情報を取得してJSON形式で返却（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/jqgridindex/{parentgrid_refkey}/{parentId}")
	@ResponseBody
	public String jqgridindex(
				@PathVariable("parentgrid_refkey") String parentgridRefkey,
				@PathVariable String parentId) throws ServiceException {
		try{
			//検索条件マップ
			BeanMap conditions = new BeanMap();

			//親のIDの取得
//			String parentgridRefkey = (String)request.getParameter("parentgrid_refkey");
//			String parentId = (String)request.getParameter("parentId");
			if(StringUtils.isNotEmpty(parentId)){
				//検索条件をセット
				conditions.put(parentgridRefkey, parentId);
			}


			//検索フォーム関連パラメータ取得
			boolean isSearch = Boolean.valueOf((String) request.getParameter(JQG_DO_SEARCH)); //検索実行時か否か
			String searchField = (String) request.getParameter(JQG_SEARCH_FIELD); //検索対象
			String searchOper = (String) request.getParameter(JQG_SEARCH_OPER);   //比較演算子
			String searchString =  (String) request.getParameter(JQG_SEARCH_STRING);   //検索文字列
			if(isSearch && StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchOper) && StringUtils.isNotEmpty(searchString)){
				//検索条件をセット
				conditions.put(JqGridUtil.getCoditionStr(searchField, searchOper), searchString);
			}

			//ページャー関連パラメータ取得
			int page = Integer.parseInt((String)request.getParameter(JQG_PAGER_PAGE));  //現在ページ番号
			int rows =  Integer.parseInt((String)request.getParameter(JQG_PAGER_ROWS)); //1ページの件数
			String sidx = (String) request.getParameter(JQG_PAGER_SIDX);    //ソート項目名
			String sord = (String) request.getParameter(JQG_PAGER_SORD);    //ソート順（昇順 or 降順）

			//postDataパラメータ取得
			//loadonce
			boolean loadonce = Boolean.valueOf((String)request.getParameter(JQG_LOADONCE));
			boolean editing = Boolean.valueOf((String)request.getParameter("editing"));

			//取得件数の判定
			//loadonce=true：グリッドデータを1回ロードし、その後はある程度クライアント側だけで済ませるため、データは全件取得。
			//loadonce=false：グリッドデータは必要な時に必要（1ページ）な分だけ取得するため、データは1ページ分取得。
			int limit = rows;
			if(loadonce){
				limit = NON_LIMIT;
				if (!editing) {
					page = JQG_FIRST_PAGE;
				}
			}

			// 一覧件数取得
			int count = landmarkDataService.getCount(conditions);

			// 一覧内容取得
			List<LandmarkData> list = new ArrayList<LandmarkData>();
			if(count > 0){
				list = landmarkDataService.findByCondition(conditions, sidx, sord, limit, (page-1)*limit);
			}

			//ユーザーデータ取得
			Map<String, Object> userdata = new HashMap<String, Object>();


			//ページャー関連パラメータ再計算
			int totalPages = 0;
			if( count >0 ) {
				totalPages = (int)Math.ceil((double)count/rows);
			}
			if (page > totalPages){
				page = totalPages;
			}

			//返却値セット
			JSONObject json = new JSONObject();
			json.put(JQG_PAGER_PAGE, page); //現在ページ番号
			json.put(JQG_PAGER_RECORDS, count); //総結果件数
			json.put(JQG_PAGER_TOTAL, totalPages);  //総ページ数
			json.put(JQG_USERDATA, userdata); //ユーザーデータ
			json.put(JQG_PAGER_ROWS, jsonForCyclicalReferences(list));   //結果一覧

			//レスポンスへ出力
			responseService.responseJson(json);

		}catch(Exception e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}

		return null;
	}

	/**
	 * Jqgrid新規登録、編集、削除実行（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/jqgridedit", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String jqgridedit(@ModelAttribute LandmarkDataForm landmarkDataForm) throws ServiceException {

		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtils.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			LandmarkData entity;
			//登録
			if(JQG_OPER_ADD.equals(oper)) {
				// 通常の登録
				if(StringUtils.isEmpty(landmarkDataForm.csvfilename)){
					entity = Beans.createAndCopy(LandmarkData.class, landmarkDataForm).dateConverter(DATE_FORMAT_PATTERN).execute();
					//登録実行
					landmarkDataService.insert(entity);
					//採番したキー値を返却
					JSONObject json = new JSONObject();
					json.put(JQG_NEW_ENTITY, StringUtil.jsonForCyclicalReferences(entity));
					responseService.responseJson(json);
				// CSVファイルによる一括登録の場合は何もせずレスポンスを返す
				}else{
					JSONObject json = new JSONObject();
					json.put(JQG_MESSAGE, "accept");
					responseService.responseJson(json);
				}
			//編集
			}else if(JQG_OPER_EDIT.equals(oper)) {
				//編集前データのシリアライズ文字列を取得
				String serializedPreEditData = (String)request.getParameter(JQG_SERIALIZED_PRE_EDIT_DATA);
				if(StringUtils.isEmpty(serializedPreEditData)){
					throw new IllegalStateException(lang.__("There is no serialization string of pre-edit data."));
				}else{
					//現時点の編集対象データを取得
					LandmarkData landmarkData = landmarkDataService.findById(Long.decode(landmarkDataForm.id));

					//結果返却用JSON
					JSONObject json = new JSONObject();
					if(landmarkData != null){
						//編集対象が存在する場合、実行。
						//現時点の編集対象データが他者により変更されているかを判定
						if(JqGridUtil.existsNoChangeByOther(serializedPreEditData, LandmarkData.class, landmarkData)){
							//変更されていない場合、更新
							entity = Beans.createAndCopy(LandmarkData.class, landmarkDataForm).dateConverter(DATE_FORMAT_PATTERN).execute();
							PropertyName<?>[] updateExcludesAry = null;
							List<PropertyName<?>> updateExcludesList = new ArrayList<PropertyName<?>>();
							//更新実行
							landmarkDataService.update(entity, updateExcludesAry);
							json.put(JQG_EDIT_ENTITY, StringUtil.jsonForCyclicalReferences(entity));
							responseService.responseJson(json);
						}else{
							//変更されている場合、更新せずにリフレッシュをユーザーに促すメッセージを返却。
							json.put(JQG_MESSAGE, lang.__("It may have been changed by another user.\n You need modify the data on edit form."));
							responseService.responseJson(json);
						}
					}else{
						//編集対象が存在しない場合、他者により削除された可能性があるため更新は実行実行せず、
						//リフレッシュをユーザーに促すメッセージを返却。
						json.put(JQG_MESSAGE, lang.__("It may have been deleted by another user."));
						responseService.responseJson(json);
					}
				}
			//削除
			}else if(JQG_OPER_DEL.equals(oper)) {
				//削除対象データの有無を取得
				//検索条件マップ
				BeanMap conditions = new BeanMap();
				conditions.put("id", Integer.parseInt(landmarkDataForm.id));
				int count = landmarkDataService.getCount(conditions);
				if(count==1){
					//削除対象が存在する場合、削除実行。
					entity = landmarkDataService.findById(Long.decode(landmarkDataForm.id));
					//削除実行
					try{
						landmarkDataService.delete(entity);
					}catch(SQLRuntimeException se){
						//子レコードが存在する場合、外部キーエラーを検知し、メッセージを返却。
						if(TableService.isForeignKeyViolation(se)){
							JSONObject json = new JSONObject();
							json.put(JQG_MESSAGE, lang.__("You can't delete the data because of its child data."));
							responseService.responseJson(json);
						}else{
							throw se;
						}
					}
				}else{
					//削除対象が存在しない場合、他者により削除された可能性があるため削除は実行実行せず、
					//リフレッシュをユーザーに促すメッセージを返却。
					JSONObject json = new JSONObject();
					json.put(JQG_MESSAGE, lang.__("It may have been deleted by another user."));
					responseService.responseJson(json);
				}
			}else{
				throw new IllegalStateException(lang.__("Not proper operation type."));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed to update. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}

		return null;
	}

	/**
	 * Jqgrid selectボックスタグの構成要素を生成（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/createSelectTag/{selectTagColumn}/{tableName}/{codeColumn}/{decodeColumn}")
	@ResponseBody
	public String createSelectTag(
				@PathVariable String selectTagColumn,
				@PathVariable String tableName,
				@PathVariable String codeColumn,
				@PathVariable String decodeColumn) throws ServiceException {
		try{
			//パラメータ取得
//			String selectTagColumn = (String) request.getParameter(JQG_SELECT_TAG_COLUMN); //selectタグとするグリッドカラム名
//			String tableName = (String) request.getParameter(JQG_TABLE_NAME); //デコード値を保持しているテーブル名
//			String codeColumn = (String) request.getParameter(JQG_CODE_COLUMN); //デコード値を保持しているテーブルのコードカラム
//			String decodeColumn = (String) request.getParameter(JQG_DECODE_COLUMN); //デコード値を保持しているテーブルのデコードカラム

			//返却値
			JSONObject json = new JSONObject();

			if(StringUtils.isEmpty(selectTagColumn)
					|| StringUtils.isEmpty(tableName)
					|| StringUtils.isEmpty(codeColumn)
					|| StringUtils.isEmpty(decodeColumn)){
				throw new IllegalStateException(lang.__("Pull-down creation parameters is invalid."));
			}else{
				//デコード値を保持しているテーブルのデータを取得し、コードをデコードしたselectボックスタグの構成要素を生成。
				String dbServiceName = DB_SERVICE_PKG+"."+StringUtil.snakeToCamelCapitalize(tableName)+DB_SERVICE_SUFFIX;
				String dbServiceMethodName = "findByCondition";
				Class<?>[] methodArgType = {Map.class, String.class, String.class, Integer.class, Integer.class};
				//検索条件追加。
				BeanMap conditions = new BeanMap();
				long localgovinfoid = loginDataDto.getLocalgovinfoid();
				if("localgov_info".equals(tableName)){
					//作成するテーブルが地方自治体情報の場合、管理者以外であればログインした自治体で絞り込む。
					if(localgovinfoid != ADMIN_LOCALGOVINFOID){
						//管理者(localgovinfoid == 0)は全件参照可能
						conditions.put(Names.localgovInfo().id().toString(), localgovinfoid);
					}
				}else{
					//作成する対象テーブルに自治体idカラムが存在する場合、管理者以外はログイン時の自治体idで絞り込む。
					if(localgovinfoid != ADMIN_LOCALGOVINFOID){
						String entityName = ENTITY_PKG+"."+StringUtil.snakeToCamelCapitalize(tableName);
						if(JqGridUtil.hasEntityProperty(entityName, "localgovinfoid")){
							conditions.put("localgovinfoid", localgovinfoid);
						}
					}
				}
				Object[] methodArg = {conditions, codeColumn, ASC, NON_LIMIT, NON_SELECT_OFFSET};
				Object result = executeDbServiceMethod(dbServiceName, dbServiceMethodName, methodArgType, methodArg);
				List<?> parentList = (ArrayList<?>)result;

				//返却値セット
				if("localgov_info".equals(tableName)){
					json.put(JQG_SELECT_TAG, StringUtil.json(JqGridUtil.createSelectTagList(parentList, codeColumn, Names.localgovInfo().pref().toString(), Names.localgovInfo().city().toString())));   //結果一覧
				}else{
					json.put(JQG_SELECT_TAG, StringUtil.json(JqGridUtil.createSelectTagList(parentList, codeColumn, decodeColumn)));   //結果一覧
				}
			}

			//レスポンスへ出力
			responseService.responseJson(json);

		}catch(Exception e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed to create pull down. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}

		return null;
	}

	/**
	 * Jqgrid 一括削除処理（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/jqgriddeleteall")
	@ResponseBody
	public String jqgriddeleteall() throws ServiceException {
		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtils.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			List<LandmarkData> entitys;
			// このメソッドで実行するのは一括削除のみ
			if(JQG_OPER_DELALL.equals(oper)) {
				String parentId = (String)request.getParameter(JQG_PARENTID);

				//削除対象データの有無を取得
				//検索条件マップ
				BeanMap conditions = new BeanMap();
				conditions.put(LandmarkDataNames.landmarkinfoid().toString(), Long.parseLong(parentId));
				int count = landmarkDataService.getCount(conditions);
				if(count > 0){
					//削除対象が存在する場合、削除実行。
					entitys = landmarkDataService.findByCondition(conditions);
					//削除実行
					try{
						for(LandmarkData entity : entitys){
							landmarkDataService.delete(entity);
						}
						JSONObject json = new JSONObject();
						json.put(JQG_MESSAGE, count);
						responseService.responseJson(json);
					}catch(SQLRuntimeException se){
						//子レコードが存在する場合、外部キーエラーを検知し、メッセージを返却。
						if(TableService.isForeignKeyViolation(se)){
							JSONObject json = new JSONObject();
							json.put(JQG_MESSAGE, lang.__("You can't delete the data because of its child data."));
							responseService.responseJson(json);
						}else{
							throw se;
						}
					}
				}else{
					//削除対象が存在しない場合、他者により削除された可能性があるため削除は実行実行せず、
					//リフレッシュをユーザーに促すメッセージを返却。
					JSONObject json = new JSONObject();
					json.put(JQG_MESSAGE, lang.__("It may have been deleted by another user."));
					responseService.responseJson(json);
				}
			}else{
				throw new IllegalStateException(lang.__("Not proper operation type."));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed to update. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}

		return null;

	}

	/**
	 * CSVファイル一括登録処理（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/csvimport")
	@ResponseBody
	public String csvimport(@ModelAttribute LandmarkDataForm landmarkDataForm) throws ServiceException {
		long fileSize = landmarkDataForm.csvfile.getSize();
		HashSet<String> allowedExtent = new HashSet<String>(Arrays.asList(new String[]{Constants.LANDMARKDATA_CSVFILE_EXT,Constants.LANDMARKDATA_CSVFILE_EXT.toUpperCase()}));
		JSONObject json = new JSONObject();

		Map<Integer,String> errorMap = new LinkedHashMap<Integer, String>();
		try{
			if(fileSize > 0){
				// 作業ディレクトリにファイルを保存
				File tmpDir = FileUtil.getTmpDir();
				String csvSaveDirName = "landmarkdatacsvfile_" + loginDataDto.getLocalgovinfoid() + "-" + loginDataDto.getGroupid() + "-" + UUID.randomUUID().toString() + "_" + landmarkDataForm.csvfile.getOriginalFilename();
				File csvSaveDir = new File(tmpDir, csvSaveDirName);
				if(! csvSaveDir.exists()){
					csvSaveDir.mkdir();
				}

				String savedCsvFileName = fileUploadService.uploadFile(landmarkDataForm.csvfile, csvSaveDir.getPath(), allowedExtent);
				if(StringUtils.isEmpty(savedCsvFileName)){
					json.put(JQG_MESSAGE, lang.__("Uploaded file is not CSV file."));
					responseService.responseJsonText(json);
				}
				File csvFile = new File(csvSaveDir, savedCsvFileName);
				if(!csvFile.exists()){
					throw new ServiceException(lang.__("Failed to temporarily save CSV file in server."));
				}


				// 文字コードの判定。SJISのみ許可
				String csvFileEncode = getEncoding(csvFile);
				if(!(StringUtils.isEmpty(csvFileEncode) || Constants.LANDMARKDATA_CSVFILE_ENCODE.equals(csvFileEncode))){
					json.put(JQG_MESSAGE, lang.__("CSV file character code is not SJIS."));
					responseService.responseJsonText(json);
			        // 一時作業領域の削除
					FileUtil.dirDelete(csvSaveDir);
					return null;
				}

		        try {
					Map<String,String> landmarkdataNameMap = new LinkedHashMap<String, String>();
		            //ファイルを読み込む
		            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "Shift_JIS"));

		            //読み込んだファイルを１行ずつ処理する
		            String line;
		            StringTokenizer token;
		            int lineCount = 1;
		            while ((line = br.readLine()) != null) {
		                //区切り文字","で分割する
		                token = new StringTokenizer(line, Constants.LANDMARKDATA_CSVFILE_DELIMITER);

		                // 3つのカラムがあるか（4つ以上あっても4つ目以降は無視）
		                if(token.countTokens() < Constants.LANDMARKDATA_CSVFILE_COLUMNS){
		                	errorMap.put(lineCount, lang.__("Number of columns is 2 or less."));
		                }

		                // 先頭行がカラム名になっていたら飛ばす
		                // カラム名であるかの判別は、データがすべて文字列になっているか否か、とする
		                String [] columns = new String[Constants.LANDMARKDATA_CSVFILE_COLUMNS];
		                int columnIndex = 0;
		                boolean isHeader = true;
		                if(lineCount == 1){
			                while (columnIndex < Constants.LANDMARKDATA_CSVFILE_COLUMNS) {
			                	columns[columnIndex] = token.nextToken();
			                    if(NumberUtils.isNumber(columns[columnIndex])){
			                    	isHeader = false;
			                    }
			                    columnIndex++;
			                }
		                }else{
	                    	isHeader = false;
			                while (columnIndex < Constants.LANDMARKDATA_CSVFILE_COLUMNS) {
			                	columns[columnIndex] = token.nextToken();
			                    columnIndex++;
			                }
		                }

		                if(!isHeader){
		                	// それぞれが順番に「文字列,数値,数値」となっているか
		                	if( (!NumberUtils.isNumber(columns[Constants.LANDMARKDATA_CSVFILE_DATASTRIDX]))
		                			&& NumberUtils.isNumber(columns[Constants.LANDMARKDATA_CSVFILE_LATIDX])
		                			&& NumberUtils.isNumber(columns[Constants.LANDMARKDATA_CSVFILE_LONIDX])){

		                		// 「入れ替え」の場合はCSVファイルのデータ内でランドマーク名称の重複チェック
	                			if(landmarkdataNameMap.get(columns[Constants.LANDMARKDATA_CSVFILE_DATASTRIDX]) == null){
	                				landmarkdataNameMap.put(columns[Constants.LANDMARKDATA_CSVFILE_DATASTRIDX], columns[Constants.LANDMARKDATA_CSVFILE_DATASTRIDX]);
	                			}else{
	        	                	errorMap.put(lineCount, lang.__("Same landmark data string exists in CSV file."));
	                			}
		                		//「追加」の場合は、CSVファイルのデータ内と登録済DB内の両方でランドマーク名称の重複チェック
		                		if(Constants.LANDMARKDATA_CSVFILE_APPEND.equals(landmarkDataForm.csvloadradio)){
		            				BeanMap conditions = new BeanMap();
		            				LandmarkInfo landmarkinfo = landmarkInfoService.findById( Long.parseLong(landmarkDataForm.landmarkinfoid));
		            				long localgovinfoid = landmarkinfo.localgovinfoid;
//		            				conditions.put(LandmarkDataNames.landmarkinfoid().toString(), Long.parseLong(landmarkDataForm.landmarkinfoid));
		            				conditions.put(Names.landmarkData().landmarkInfo().localgovinfoid().toString(), localgovinfoid);
//		            				conditions.put(LandmarkDataNames.groupid().toString(), Long.parseLong(landmarkDataForm.groupid));
		            				conditions.put(LandmarkDataNames.landmark().toString(), columns[Constants.LANDMARKDATA_CSVFILE_DATASTRIDX]);
		            				if(landmarkDataService.getCount(conditions) > 0) {
		        	                	errorMap.put(lineCount, lang.__("Same landmark data string exists in database."));
		            				}
		                		}
		                	}else{
	    	                	errorMap.put(lineCount, lang.__("Data format is not \"string\", \"numeric\"."));
		                	}
		                }
		                lineCount++;
		            }

		            //終了処理
		            br.close();

		        } catch (IOException e) {
					e.printStackTrace();
					logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
					logger.error("", e);
					if(EnvUtil.isProductEnv()){
						throw new ServiceException(lang.__("Failed to update. Contact to system administrator."), e);
					}else{
						//本番環境でなければエラー詳細内容も合わせて返却。
						throw new ServiceException(e);
					}
		        }

		        if(errorMap.size() <= 0){
			        try {
			            //ファイルを読み込む
			            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "Shift_JIS"));

			            // テーブル削除
	                	if(Constants.LANDMARKDATA_CSVFILE_SWAP.equals(landmarkDataForm.csvloadradio)){
            				BeanMap conditions = new BeanMap();
            				conditions.put(LandmarkDataNames.landmarkinfoid().toString(), Long.parseLong(landmarkDataForm.landmarkinfoid));
//            				conditions.put(LandmarkDataNames.groupid().toString(), Long.parseLong(landmarkDataForm.groupid));
            				List<LandmarkData> entities = landmarkDataService.findByCondition(conditions);
            				if(entities.size() > 0){
            					for(LandmarkData entity : entities){
	            					landmarkDataService.delete(entity);
            					}
            				}
	                	}

			            //読み込んだファイルを１行ずつ処理する
			            String line;
			            StringTokenizer token;
			            int lineCount = 1;
			            int dataCount = 0;
			            while ((line = br.readLine()) != null) {
			                //区切り文字","で分割する
			                token = new StringTokenizer(line, Constants.LANDMARKDATA_CSVFILE_DELIMITER);

			                // 先頭行がカラム名になっていたら飛ばす
			                // カラム名であるかの判別は、データがすべて文字列になっているか否か、とする
			                String [] columns = new String[Constants.LANDMARKDATA_CSVFILE_COLUMNS];
			                int columnIndex = 0;
			                boolean isHeader = true;
			                if(lineCount == 1){
				                while (columnIndex < Constants.LANDMARKDATA_CSVFILE_COLUMNS) {
				                	columns[columnIndex] = token.nextToken();
				                    if(NumberUtils.isNumber(columns[columnIndex])){
				                    	isHeader = false;
				                    }
				                    columnIndex++;
				                }
			                }else{
		                    	isHeader = false;
				                while (columnIndex < Constants.LANDMARKDATA_CSVFILE_COLUMNS) {
				                	columns[columnIndex] = token.nextToken();
				                    columnIndex++;
				                }
			                }

			                if(!isHeader){
	            				LandmarkData entity = new LandmarkData();
			                	entity.landmarkinfoid = Long.parseLong(landmarkDataForm.landmarkinfoid);
			                	entity.groupid = Long.parseLong(landmarkDataForm.groupid);
			                	entity.landmark = columns[Constants.LANDMARKDATA_CSVFILE_DATASTRIDX];
			                	entity.latitude = Double.parseDouble(columns[Constants.LANDMARKDATA_CSVFILE_LATIDX]);
			                	entity.longitude = Double.parseDouble(columns[Constants.LANDMARKDATA_CSVFILE_LONIDX]);
			                	int insCount = landmarkDataService.insert(entity);
			                	if(insCount != 1){
			            			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid()
			            					+" landmark_data insert failed ["
			            					+ "landmarkinfoid:"  + entity.landmarkinfoid
			            					+ ", groupid:"  + entity.groupid
			            					+ ", landmark:"  + entity.landmark
			            					+ ", latitude:"  + entity.latitude
			            					+ ", longitude:"  + entity.longitude
			            					+ "]");
			        				throw new IllegalStateException(lang.__("Failed to update. Contact to system administrator."));
			                	}
			                	dataCount++;
			                }
			                lineCount++;
			            }

			            //終了処理
			            br.close();

			            json.put(JQG_MESSAGE, lang.__("{0} records registered.", dataCount));
						responseService.responseJsonText(json);

			        } catch (IOException ex) {
			            //例外発生時処理
			            ex.printStackTrace();
			        }

		        }else{
		        	// エラーメッセージ作成
		        	StringBuffer errorMessageBuf = new StringBuffer();
	        		errorMessageBuf.append(lang.__("Due to following error, bulk registry is canceled."));
		        	for(Map.Entry<Integer, String> e : errorMap.entrySet()){
		        		errorMessageBuf.append(e.getKey());
		        		errorMessageBuf.append(lang.__("Line:"));
		        		errorMessageBuf.append(e.getValue());
		        		errorMessageBuf.append("\n");
		        	}
					json.put(JQG_MESSAGE, errorMessageBuf.toString());
					responseService.responseJsonText(json);
		        }

		        // 一時作業領域の削除
				FileUtil.dirDelete(csvSaveDir);
			}else{
				json.put(JQG_MESSAGE, lang.__("CSV file size is 0."));
				responseService.responseJsonText(json);
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed to update. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}
		return null;
	}

	/**
	 * ファイルの文字コード判定メソッド
	 * @param file
	 * @return
	 */
	private String getEncoding(File file){
		FileInputStream fis = null;
		try{
			 fis = new FileInputStream(file);
			 byte[] buf = new byte[4096];
			 UniversalDetector detector = new UniversalDetector(null);

			 int nread;
			 while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				 detector.handleData(buf, 0, nread);
			 }

			 detector.dataEnd();
			 return  detector.getDetectedCharset();
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 if(fis != null){
				 try{
					 fis.close();
				 }catch(IOException e){
				 }
			 }
		 }

		 return null;
	}
}
