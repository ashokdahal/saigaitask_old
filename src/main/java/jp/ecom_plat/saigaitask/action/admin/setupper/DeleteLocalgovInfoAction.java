/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.transaction.UserTransaction;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.ListDto;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.dto.PageDto;
import jp.ecom_plat.saigaitask.dto.admin.setupper.DeleteLocalgovInfoStatusDto;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.form.admin.setupper.DeleteLocalgovInfoForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService.EntityType;
import jp.ecom_plat.saigaitask.service.setupper.ExportService.LocalgovFilterTable;
import jp.ecom_plat.saigaitask.tags.S2Functions;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;
import jp.ecom_plat.saigaitask.util.SpringContext;
import jp.ecom_plat.saigaitask.util.TableSheetWriter.TableSheetRow;

/**
 * 自治体セットアッパーの自治体削除画面のアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class DeleteLocalgovInfoAction extends AbstractSetupperAction {

	/** ActionForm */
	DeleteLocalgovInfoForm deleteLocalgovInfoForm;
	BindingResult bindingResult;

	// for JSP
	/** PageDto */
	public PageDto pageDto;

	/** 表示するテーブル */
	public List<ListDto> listDtos;

	private static Boolean DEVEL_ALWAYS_ROOLBACK = false;

	@Resource private UserTransaction userTransaction;
	@Resource protected DeleteLocalgovInfoStatusDto deleteLocalgovInfoStatusDto;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("pageDto", pageDto);
		model.put("listDtos", listDtos);

	}

	protected Logger getLogger() {
		return Logger.getLogger(DeleteLocalgovInfoAction.class);
	}

	/**
	 * 自治体削除画面
	 *
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/deleteLocalgovInfo","/admin/setupper/deleteLocalgovInfo/index"})
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute DeleteLocalgovInfoForm deleteLocalgovInfoForm, BindingResult bindingResult) {
		this.deleteLocalgovInfoForm = deleteLocalgovInfoForm;

		// 削除後、ログイン中の自治体情報がなくなるため、
		// 自治体ID=0 でログインしなおす
		// 作成した自治体にログインする
		if(deleteLocalgovInfoForm.success) {
			loginLocalgovInfo(0L);
		}

		// セットアッパーの初期化
		initSetupper();

		// 表示内容の取得
		content(model, deleteLocalgovInfoForm, bindingResult);

		setupModel(model);
		return "/admin/setupper/deleteLocalgovInfo/index";
	}

	/**
	 * 自治体削除画面.
	 * 削除対象となる自治体設定、災害データのレコード数を一覧表示するS画面
	 *
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/deleteLocalgovInfo/content")
	public String content(Map<String,Object>model,
			@Valid @ModelAttribute DeleteLocalgovInfoForm deleteLocalgovInfoForm, BindingResult bindingResult) {
		this.deleteLocalgovInfoForm = deleteLocalgovInfoForm;
		this.bindingResult = bindingResult;
		if(loginDataDto.getGroupid()!=0) {
			throw new ServiceException(lang.__("System admin privilege is required."));
		}

		// 二度押し防止トークン
		TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		// リスト初期化
		listDtos = new ArrayList<ListDto>();

		// 削除対象自治体情報
		LocalgovInfo deleteLocalgovInfo = loginDataDto.getLocalgovInfo();
		// 削除成功の場合は、ログイン中の自治体IDではなく、パラメータから取得する
		if(deleteLocalgovInfoForm.success) {
			deleteLocalgovInfo = new LocalgovInfo();
			deleteLocalgovInfo.id = deleteLocalgovInfoForm.localgovinfoid;
		}

		// 削除結果ページの表示の場合、例外が出ていたらエラーメッセージに追加
		if(deleteLocalgovInfoForm.fail && StringUtil.isNotEmpty(deleteLocalgovInfoForm.requestid)) {
			UUID uuid = UUID.fromString(deleteLocalgovInfoForm.requestid);
			Future<FutureResult> future = deleteLocalgovInfoStatusDto.getFuture(uuid);
			if(future.isDone()) {
				ActionMessages errors = new ActionMessages();
					try {
						for(Exception e : future.get().exceptions) {
							addExceptionMessages(errors, e);
						}
					} catch (InterruptedException | ExecutionException e) {
						addExceptionMessages(errors, e);
					}
				ActionMessagesUtil.addErrors(session, errors);
			}
			else {
				logger.info("Future.isDone: "+future.isDone());
			}
		}

		/*
		// 自治体情報の削除の対象外テーブルを生成(自治体情報にリンクしないため)
		{
			ListDto deleteskippedListDto = new ListDto();
			listDtos.add(deleteskippedListDto);
			deleteskippedListDto.title = "削除対象外";
			deleteskippedListDto.columnNames.add(lang.__("Table name"));
			for(String tableName : LocalgovInfoService.deleteskippedTableNames) {
				// リストへ一行追加
				List<String> values = new ArrayList<String>();
				values.add(tableName); // テーブル名
				deleteskippedListDto.columnValues.add(values);
			}
		}
		*/

		// 自治体情報に関する全ての設定・データを一覧できる表を作成する
		{
			// リストの作成順を指定
			List<EntityType> entityTypes = new ArrayList<EntityType>();
			entityTypes.add(EntityType.globalinfo); // グローバル設定
			entityTypes.add(EntityType.info); // 自治体設定
			entityTypes.add(EntityType.data); // 災害データ
			// 万が一、EntityType が追加された場合の処理
			for(Map.Entry<EntityType, List<Class<?>>> entry : ExportService.entitys.entrySet()) {
				// システムマスタは除く
				if(entry.getKey().equals(EntityType.master)) continue;
				// グローバル設定は不要
				if(entry.getKey().equals(EntityType.globalinfo)) continue;

				// 未追加ならEntityType追加
				if(entityTypes.contains(entry.getKey())==false) {
					entityTypes.add(entry.getKey());
				}
			}

			//
			// 行の追加
			//
			for(EntityType entityType: entityTypes) {

				// リストの作成
				ListDto listDto = new ListDto();
				listDto.title = entityType.getName(lang);
				listDto.styleId = entityType+"list";
				listDtos.add(listDto);

				//
				// 列情報の設定
				//
				listDto.columnNames.add(lang.__("Table name"));
				listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
				listDto.columnNames.add(lang.__("the number of data (undeleted)"));
				listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "number");
				listDto.columnNames.add(lang.__("the number of data (deleted)"));
				listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "number");

				List<Class<?>> entitys = ExportService.entitys.get(entityType);
				for(Class<?> entity : entitys) {
					// 削除対象外テーブルならスキップ
					if(LocalgovInfoService.deleteskippedTableNames.contains(entity.getSimpleName())) continue;

					// リストへ一行追加
					List<String> values = new ArrayList<String>();
					//values.add(entity.getSimpleName()); // テーブル名
					values.add(lang.getEntityName(entity)); // テーブル名

					// レコード数のカウント
					String notdeleted = "-";
					String deleted = "-";
					{
						try {
							ExportService.LocalgovFilterTable table = new ExportService.LocalgovFilterTable(deleteLocalgovInfo.id, entity);
							int totalNum = table.getCount();
							Integer deletedNum = table.getCountDeleted();
							int notdeletedNum = (deletedNum!=null ? totalNum-deletedNum : totalNum);
							notdeleted = ""+notdeletedNum;
							if(deletedNum!=null) deleted = ""+deletedNum;
						} catch(Exception e) {
							notdeleted = "check failed: "+e.getMessage();
						}
					}
					values.add(notdeleted); // データ件数(未削除)
					values.add(deleted); // データ件数(削除済み)

					listDto.columnValues.add(values);
				}
			}
		}

		// eコミマップの削除リスト
		if(deleteLocalgovInfo!=null) {
			listDtos.addAll(getEcommapDeleteListDtos(deleteLocalgovInfo.id));
		}

		setupModel(model);
		return "/admin/setupper/deleteLocalgovInfo/content";
	}

	/**
	 * 自治体の削除を実行する
	 *
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/deleteLocalgovInfo/delete")
	public String delete(Map<String,Object>model,
			@Valid @ModelAttribute DeleteLocalgovInfoForm deleteLocalgovInfoForm, BindingResult bindingResult) {
		this.deleteLocalgovInfoForm = deleteLocalgovInfoForm;
		if(loginDataDto.getGroupid()!=0) {
			throw new ServiceException(lang.__("System admin privilege is required."));
		}

		try {
			// セットアッパーの初期化
			initSetupper();

			// トークンチェック
			if (!TokenProcessor.getInstance().isTokenValid(request, true)) {
				throw new ServiceException(lang.__("The request is invalid. Old request has been sent."));
			}

			// 削除対象の自治体チェック
			LocalgovInfo localgovInfo = loginDataDto.getLocalgovInfo();
			if(localgovInfo.getId().equals(0L)) {
				throw new ServiceException(lang.__("Municipality ID=0 can't be deleted."));
			}
			// ログイン中の自治体情報と送信パラメータの自治体IDが変わっていないかチェック
			if(localgovInfo.getId().equals(deleteLocalgovInfoForm.localgovinfoid)==false) {
				throw new ServiceException(lang.__("Though municipality ID={0} deletion is requested, failed to continue the process because you logged in municipality ID={1}.", deleteLocalgovInfoForm.localgovinfoid, localgovInfo.getId()));
			}

			final FutureResult futureResult = new FutureResult();

			// 引数を futureResult にセットして渡す
			futureResult.lang = SaigaiTaskLangUtils.getSessionLang();
			futureResult.loginDataDto = new LoginDataDto();
			BeanUtils.copyProperties(loginDataDto, futureResult.loginDataDto);
			//this.loginDataDto.setLocalgovInfo(new LocalgovInfo());
			//BeanUtils.copyProperties(loginDataDto.getLocalgovInfo(), this.loginDataDto);

			Future<FutureResult> future = deleteLocalgovInfoStatusDto.exec.submit(new Callable<FutureResult>() {
				@Override
				public FutureResult call() throws Exception {
					return executeDelete(futureResult);
				};
			});
			UUID uuid = deleteLocalgovInfoStatusDto.addFuture(future);

			setupModel(model);
			return "forward:index?requestid="+uuid.toString();

		} catch (ServiceException e) {
			// エラーをキャッチしたら、ログ出力、ユーザへのエラーメッセージを追加
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Municipality info can't be deleted."), e);
			ActionMessages errors = new ActionMessages();
			addExceptionMessages(errors, e);
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		return "forward:index";
	}

	/**
	 * 自治体の削除を実行する
	 *
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/deleteLocalgovInfo/progress","/admin/setupper/deleteLocalgovInfo/progress/"}, produces="application/json")
	public ResponseEntity progress(Map<String,Object>model,
			@Valid @ModelAttribute DeleteLocalgovInfoForm deleteLocalgovInfoForm, BindingResult bindingResult) {
		this.deleteLocalgovInfoForm = deleteLocalgovInfoForm;
		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		try {
			JSONObject json = new JSONObject();
			// requestidに","が含まれてしまうので削除
			// TODO:, の削除
			deleteLocalgovInfoForm.requestid = deleteLocalgovInfoForm.requestid.replaceAll(",","");

			UUID uuid = UUID.fromString(deleteLocalgovInfoForm.requestid);
			Future<FutureResult> future = deleteLocalgovInfoStatusDto.getFuture(uuid);

			if(future!=null) {
				json.put("done", future.isDone());

				if(future.isDone()) {
					try {
						FutureResult futureResult = future.get();
						json.put("url", futureResult.url);
					} catch (InterruptedException | ExecutionException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}

			//// 出力の準備
			//response.setContentType("application/json");
			//response.setCharacterEncoding("UTF-8");
			/*PrintWriter out = response.getWriter();
			out.print(json.toString());*/
			//return json.toString();
			// 出力の準備
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		}

		return new ResponseEntity<String>(null, httpHeaders, HttpStatus.SERVICE_UNAVAILABLE);
	}

	protected void addExceptionMessages(ActionMessages messages, Exception e) {
		Throwable t = e;
		while(t!=null) {
			if(t instanceof ServiceException) {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　・"+t.getMessage(), false));
			}
			t=t.getCause();
		}
	}

	protected List<ListDto> getEcommapDeleteListDtos(long localgovinfoid) {

		List<Long> deleteMapIds = new ArrayList<Long>();
		List<Class<?>> deleteMapClasses = new ArrayList<Class<?>>();

		// get deleteMapIds, deleteMapClasses
		Class<?>[] classArray = new Class<?>[]{MapmasterInfo.class, TrackmapInfo.class};
		for(Class<?> clazz : classArray) {
			LocalgovFilterTable table = new LocalgovFilterTable(localgovinfoid, clazz);
			while(table.hasNext()) {
				TableSheetRow row = table.next();
				Long mapId = (Long) row.getColumnValueOnCurrentRow(Names.mapmasterInfo().mapid().toString());
				if(mapId!=null) {
					// 論理削除チェック
					if(clazz==MapmasterInfo.class) {
						Boolean deleted = (Boolean) row.getColumnValueOnCurrentRow(Names.mapmasterInfo().deleted().toString());
						if(deleted==true) continue;
					}
					
					deleteMapIds.add(mapId);
					deleteMapClasses.add(clazz);
				}
			}
		}

		// get ListDto
		List<ListDto> listDtos = getEcommapDeleteListDtos(deleteMapIds);

		// set title
		for(int idx=0; idx<deleteMapIds.size(); idx++) {
			Long mapId = deleteMapIds.get(idx);
			ListDto listDto = listDtos.get(idx);
			Class<?> clazz = deleteMapClasses.get(idx);
			listDto.title = (clazz==MapmasterInfo.class?lang.__("Master map"):lang.__("Disaster map"))+lang.__("(e-Community mapID={0})", mapId);
		}

		return listDtos;
	}

	protected List<ListDto> getEcommapDeleteListDtos(List<Long> deleteMapIds) {
		List<ListDto> listDtos = new ArrayList<ListDto>();

		MapDB mapDB = MapDB.getMapDB();
		// ヘッダの作成
		for(Long mapId : deleteMapIds) {
			// リストの作成
			ListDto listDto = new ListDto();
			listDto.styleId = "ecommap_mapid"+mapId+"_list";
			listDtos.add(listDto);

			//
			// 列情報の設定
			//
			listDto.columnNames.add(lang.__("Type<!--2-->"));
			listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
			listDto.columnNames.add(lang.__("Layer ID"));
			listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
			listDto.columnNames.add(lang.__("Layer name<!--2-->"));
			listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
			listDto.columnNames.add(lang.__("owner map ID"));
			listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "number");
			listDto.columnNames.add(lang.__("Delete target"));
			listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
		}

		for(int idx=0; idx<deleteMapIds.size(); idx++) {
			Long mapId = deleteMapIds.get(idx);
			ListDto listDto = listDtos.get(idx);

			// MapLayerInfo
			MapInfo mapInfo = mapDB.getMapInfo(mapId);
			if(mapInfo==null) {
				addRequestErrorMessage(bindingResult, lang.__("mapID={0} not found. Skip deletion process.", mapId));
			}
			else {
				for(MapLayerInfo mapLayerInfo : mapInfo.getMapLayerIterable()) {
					// リストへ一行追加
					List<String> values = new ArrayList<String>();
					values.add(lang.__("Layer info"));
					values.add(mapLayerInfo.layerId);
					values.add(mapLayerInfo.layerName);

					LayerInfo layerInfo = mapDB.getLayerInfo(mapLayerInfo.layerId);
					// すでにレイヤ情報が削除されていて地図レイヤ情報に残っている場合は飛ばす。
					if(layerInfo==null) continue;
					values.add(String.valueOf(layerInfo.ownerMapId));

					// 削除対象
					boolean isDeleted = deleteMapIds.contains(layerInfo.ownerMapId);
					String checkbox = "<input type=checkbox name=layerid value="+S2Functions.h(layerInfo.layerId)+" "+
							(isDeleted ? "checked" : "")
							//+" readonly='readonly' "
							+" disabled='disabled' "
							+"/>";
					//values.add((isDeleted ? "○" : "×")+checkbox);
					values.add(checkbox);

					listDto.columnValues.add(values);
				}
			}
		}

		return listDtos;
	}

	/**
	 * 削除実行
	 * @param futureResult FutureResult
	 * @return 結果ページ
	 */
	public FutureResult executeDelete(FutureResult futureResult) {
		DeleteCascadeResult result = null;
		try {
			// 別スレッドの場合、HTTPセッションが使えないため、明示的にセット
			SaigaiTaskDBLang lang = futureResult.lang;
			SaigaiTaskDBLang.langThreadLocal.set(lang);
			LoginDataDto loginDataDto = futureResult.loginDataDto;
			futureResult.success = false;

			// 削除対象の自治体チェック
			LocalgovInfo localgovInfo = loginDataDto.getLocalgovInfo();
			if(localgovInfo.getId().equals(0L)) {
				throw new ServiceException(lang.__("Municipality ID=0 can't be deleted."));
			}
			// ログイン中の自治体情報と送信パラメータの自治体IDが変わっていないかチェック
			if(localgovInfo.getId().equals(deleteLocalgovInfoForm.localgovinfoid)==false) {
				throw new ServiceException(lang.__("Though municipality ID={0} deletion is requested, failed to continue the process because you logged in municipality ID={1}.", deleteLocalgovInfoForm.localgovinfoid, localgovInfo.getId()));
			}

			logger.info("BEGIN Delete LocalgovInfo.id="+localgovInfo.getId());

			// 自治体の削除
			result = localgovInfoService.deleteCascade(localgovInfo);

			// 自治体削除できたかチェック
			logger.info("BEGIN Check Delete LocalgovInfo.id="+localgovInfo.getId());
			List<Class<?>> cannotDeleteEntityClasses = new ArrayList<Class<?>>();
			for(Map.Entry<EntityType, List<Class<?>>> entry : ExportService.entitys.entrySet()) {
				// マスタは除く
				if(entry.getKey().equals(EntityType.master)) continue;
				List<Class<?>> entitys = entry.getValue();
				for(Class<?> entity : entitys) {
					// 削除対象外テーブルならスキップ
					if(LocalgovInfoService.deleteskippedTableNames.contains(entity.getSimpleName())) continue;
					try {
						ExportService.LocalgovFilterTable table = new ExportService.LocalgovFilterTable(localgovInfo.getId(), entity);
						int notdeletedNum = table.getCountNotDeleted();
						if(notdeletedNum!=0) {
							cannotDeleteEntityClasses.add(entity);
						}
					} catch(Exception e) {
						logger.error(lang.__("Deletion error of municipality setting and disaster data."), e);
						cannotDeleteEntityClasses.add(entity);
					}
				}
			}
			if(0==cannotDeleteEntityClasses.size()) {
				logger.info("LocalgovInfo.id="+localgovInfo.getId()+" entities has been completely deleted.");
			}
			else {
				StringBuffer sb = new StringBuffer();
				for(Class<?> entity : cannotDeleteEntityClasses) {
					if(0<sb.length()) sb.append(", ");
					sb.append(entity.getSimpleName());
				}
				String errorMsg = lang.__("Among municipality setting and disaster data, some data not deleted.") + " "+sb.toString();
				logger.error(errorMsg);
				throw new ServiceException(errorMsg);
			}
			
			// Excel帳票テンプレート格納ディレクトリの削除
			File exceltemplateDir = new File(application.getRealPath(Constants.EXCELLIST_BASEDIR + localgovInfo.getId()));
			FileUtil.dirDelete(exceltemplateDir);
			
			// 自治体ロゴ画像ファイル格納ディレクトリの削除
			File logoimagefileDir = new File(application.getRealPath(Constants.LOGOIMAGEFILE_BASEDIR + localgovInfo.getId()));
			FileUtil.dirDelete(logoimagefileDir);
			
			logger.info("END Check Delete LocalgovInfo.id="+localgovInfo.getId());

			logger.info("END Delete LocalgovInfo.id="+localgovInfo.getId());

			// 削除実行
			// for debug: rollback
			if(!DEVEL_ALWAYS_ROOLBACK) {
				result.commit();
			}

			// TODO:, の削除
			deleteLocalgovInfoForm.requestid = deleteLocalgovInfoForm.requestid = deleteLocalgovInfoForm.requestid.replaceAll(",","");
			futureResult.url = "index?success=true&localgovinfoid="+deleteLocalgovInfoForm.localgovinfoid+"&requestid="+deleteLocalgovInfoForm.requestid;
			futureResult.end = new Timestamp(System.currentTimeMillis());
			return futureResult;

		} catch (ServiceException e) {
			// エラーをキャッチしたら、ログ出力、ユーザへのエラーメッセージを追加
			logger.error(futureResult.loginDataDto.logInfo()+ "\n" + lang.__("Municipality info can't be deleted."), e);
			futureResult.exceptions.add(e);

			// ロールバックする
			if(result!=null) {
				result.rollback(userTransaction);
				result.printResult();
			}
		} finally {
			// for debug: rollback
			if(DEVEL_ALWAYS_ROOLBACK) {
				result.rollback(userTransaction);
			}
		}
		// TODO:, の削除
		deleteLocalgovInfoForm.requestid = deleteLocalgovInfoForm.requestid = deleteLocalgovInfoForm.requestid.replaceAll(",","");
		futureResult.url = "index?fail=true&requestid="+deleteLocalgovInfoForm.requestid+"&localgovinfoid="+futureResult.loginDataDto.getLocalgovinfoid();
		futureResult.end = new Timestamp(System.currentTimeMillis());
		return futureResult;
	}

	/**
	 * 非同期処理結果
	 * 非同期処理実行後の戻り値として利用する.
	 */
	public static class FutureResult {
		/** 多言語 */
		public SaigaiTaskDBLang lang;
		/** リダイレクト先URL */
		public String url;
		/** 実行開始時刻 */
		public Timestamp start = new Timestamp(System.currentTimeMillis());
		/** 実行終了時刻 */
		public Timestamp end;
		/** 成功フラグ */
		public boolean success = false;
		/** 発生した例外リスト */
		public List<Exception> exceptions = new ArrayList<Exception>();
		/** ログインデータDto */
		public LoginDataDto loginDataDto ;
	}
}
