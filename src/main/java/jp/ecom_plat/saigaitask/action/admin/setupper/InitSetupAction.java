/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.map.db.CommunityInfo;
import jp.ecom_plat.map.db.GroupInfo;
import jp.ecom_plat.map.db.InitRangeInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.util.DBLang;
import jp.ecom_plat.map.util.LangUtils;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.dto.admin.setupper.InitSetupStatusDto;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovtypeMaster;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.form.admin.InitSetupForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.FileService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.LocalgovtypeMasterService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService.ExportFileSet;
import jp.ecom_plat.saigaitask.service.setupper.ImportInfoService;
import jp.ecom_plat.saigaitask.service.setupper.ImportMasterService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SaigaiTaskLangUtils;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 自治体セットアッパーの自治体作成画面のアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class InitSetupAction extends AbstractSetupperAction {

	/** アクションフォーム */
	protected InitSetupForm initSetupForm;

	// Service
	@Resource protected LocalgovtypeMasterService localgovtypeMasterService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected GroupInfoService groupInfoService;
	@Resource protected FileUploadService fileUploadService;
	@Resource protected FileService fileService;
	@Resource protected ImportInfoService importInfoService;
	@Resource protected ImportMasterService importMasterService;
	@Resource protected TableService tableService;

	// Dto
	@Resource protected InitSetupStatusDto initSetupStatusDto;

	@Resource private UserTransaction userTransaction;

	/** 自治体種別のSELECT OPTION用 */
	public List<LocalgovtypeMaster> localgovtypeItems;
	/** すでに自治体作成済みの場合のマスタマップ情報 */
	public MapmasterInfo mapmasterInfo;
	/** eコミマップURL */
	public String ecommapURL;

	/** 新規作成したeコミのサイト */
	public CommunityInfo newCommunityInfo = null;

	final String SETUPPER_UPLOAD_TEMPLATE_FILE = "SETUPPER_UPLOAD_TEMPLATE_FILE";

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("initSetupStatusDto", initSetupStatusDto);
		model.put("localgovtypeItems", localgovtypeItems);
		model.put("mapmasterInfo", mapmasterInfo);
		model.put("ecommapURL", ecommapURL);
		model.put("newCommunityInfo", newCommunityInfo);
		model.put("SETUPPER_UPLOAD_TEMPLATE_FILE", SETUPPER_UPLOAD_TEMPLATE_FILE);
		//model.put("loginDataDto", loginDataDto);
	}

	protected Logger getLogger() {
		return Logger.getLogger(InitSetupAction.class);
	}

	/**
	 * エラーがあれば、 request にエラーメッセージをセットする
	 */
	protected ActionMessages checkDomainExists() {
		// 入力チェック
		ActionMessages messages = new ActionMessages();
		List<LocalgovInfo> localgovInfos = localgovInfoService.findByDomain(initSetupForm.domain);
		// ドメインが存在するかどうか
		boolean isInsert = loginDataDto.getLocalgovinfoid()==0; // 新規作成の場合
		// 同じドメインの他の地方自治体情報があるかどうか(有効無効関係なし)
		//boolean existsOtherLocalgovInfo = localgovInfos.size()!=(isInsert?0:1);
		// 同じドメインかつ有効になっている他の地方自治体情報があるかどうか
		boolean existsOtherValidLocalgovInfo = false;
		for(LocalgovInfo li : localgovInfos) {
			// ログイン中の自治体情報
			if(li.id.equals(loginDataDto.getLocalgovinfoid())) {
				if(0<li.id) loginDataDto.setLocalgovInfo(li);
			}
			// 他の自治体情報
			else {
				if(li.valid==true) {
					existsOtherValidLocalgovInfo = true;
				}
			}
		}
		// 同じドメインかつ有効になっている他の地方自治体情報があれば警告メッセージを表示する
		if((isInsert&&existsOtherValidLocalgovInfo)
				// 編集の場合は、有効状態であればメッセージを表示する
				|| (!isInsert&&loginDataDto.getLocalgovInfo().valid&&existsOtherValidLocalgovInfo)) {
			//messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Domain name : \"{0}\" already registered.", initSetupForm.domain), false));
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("[Warning] The same domain : \"{0}\" is set in other local government info.", initSetupForm.domain), false));
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　　　　" + lang.__("To avoid duplication, change domain or set local government info to be invalid."), false));
		}
		// メッセージをセッションに保存（リダイレクト対応）
		if(0<messages.size()) {
			ActionMessagesUtil.saveMessages(session, messages);
		}
		return messages;
	}

	/**
	 * 初期セットアップ
	 *
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/initSetup","/admin/setupper/initSetup/index"})
	public String index(Map<String,Object>model,
			@ModelAttribute InitSetupForm initSetupForm, BindingResult bindingResult) {
		this.initSetupForm = initSetupForm;

		// 管理者権限のないユーザの場合はウイザードページに遷移
		if(!loginDataDto.isAdmin()){
			return "forward:/admin/setupper/menuWizard/";
		}

		// 作成した自治体にログインする
		if(StringUtil.isNotEmpty(initSetupForm.localgovinfoid)) {
			loginLocalgovInfo(Long.parseLong(initSetupForm.localgovinfoid));
		}

		// 表示内容の取得
		content(model, initSetupForm, bindingResult);

		setupModel(model);
		return "/admin/setupper/initSetup/index";
	}

	/**
	 * 初期セットアップ
	 *
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/initSetup/content")
	public String content(Map<String,Object>model,
			@ModelAttribute InitSetupForm initSetupForm, BindingResult bindingResult) {
		this.initSetupForm = initSetupForm;

		// セットアッパーの初期化
		initSetupper();

		ecommapURL = Config.getEcommapURL();

		// 二度押し防止トークン
		TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		// 自治体種別の取得
		localgovtypeItems = localgovtypeMasterService.findAllOrderBy();

		// 登録成功の場合
		if(initSetupForm.success!=null && initSetupForm.success) {
			// アップロードした設定テンプレートをクリア
			session.setAttribute(SETUPPER_UPLOAD_TEMPLATE_FILE, null);
		}
		// 登録失敗の場合
		else {
			// アップロード済みの設定テンプレートファイルがあるかチェック
			ExportFileSet exportFileSet = (ExportFileSet) session.getAttribute(SETUPPER_UPLOAD_TEMPLATE_FILE);
			if(exportFileSet!=null) {
				initSetupForm.confirmTemplateFile = true;
			}
		}

		// 新規作成の場合
		if(loginDataDto.getLocalgovinfoid()==0) {
			// リクエストからドメイン名を取得
			if(StringUtil.isEmpty(initSetupForm.domain)) {
				initSetupForm.domain = request.getServerName();
			}

			// システム名称
			initSetupForm.systemname = siteName;

			// システムバージョン
			initSetupForm.systemVersion = Config.getVersionForView();

			// テンプレートファイルのバージョンチェックの場合
			if(initSetupForm.confirmTemplateFile) {
				// 設定テンプレートの登録モードを「このまま登録」に設定
				initSetupForm.templateFileMode = 1;

				// テンプレートファイルのバージョンを取得
				try {
					ExportFileSet exportFileSet = (ExportFileSet) session.getAttribute(SETUPPER_UPLOAD_TEMPLATE_FILE);
					initSetupForm.templateFileVersion = exportFileSet.getVersion();
					initSetupForm.templateFileName = exportFileSet.zipFileName;
				} catch(Exception e) {
					logger.error(loginDataDto.logInfo(), e);
				}
			}

			// エラーメッセージがなければエラーチェック(insertでエラーチェック済みの場合があるため)
//			if(!ActionMessagesUtil.hasErrors(request)) {
//				// 入力チェック
//				checkInput();
//			}
		}
		// 編集の場合
		else {
			// 自治体情報
			LocalgovInfo localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
			Beans.copy(localgovInfo, initSetupForm).execute();
			// マスタマップ
			mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());

			// 自治体ドメインの存在チェック
			checkDomainExists();
		}

		{
			// FIXME: ファイルアップロード上限エラーメッセージのSpring対応
			/*
			Exception e = (Exception) requestScope.get("org.seasar.struts.upload.S2MultipartRequestHandler.EXCEPTION");
			if(e!=null) {
				logger.error("file upload error", e);
				ActionMessages errors = new ActionMessages();
				addExceptionMessages(errors, e);
				ActionMessagesUtil.addErrors(bindingResult, errors);
			}
			*/
		}

		// 削除結果ページの表示の場合、例外が出ていたらエラーメッセージに追加
		boolean fail = initSetupForm.success!=null && !initSetupForm.success;
		String requestid = initSetupForm.requestid;
		if(fail && StringUtil.isNotEmpty(requestid)) {
			UUID uuid = UUID.fromString(requestid);
			Future<FutureResult> future = initSetupStatusDto.getFuture(uuid);
			if(future!=null && future.isDone()) {
				ActionMessages errors = new ActionMessages();
					try {
						for(Exception e : future.get().exceptions) {
							addExceptionMessages(errors, e);
						}
					} catch (InterruptedException | ExecutionException e) {
						addExceptionMessages(errors, e);
					}
				ActionMessagesUtil.addErrors(bindingResult, errors);
			}
		}

		setupModel(model);
		return "/admin/setupper/initSetup/content";
	}


	/**
	 * 登録実行
	 *
	 * @return 遷移ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/initSetup/insert")
	public String insert(Map<String,Object>model,
			@Valid @ModelAttribute InitSetupForm initSetupForm, BindingResult bindingResult) {
		this.initSetupForm = initSetupForm;

		if(bindingResult.hasErrors()) {
			//return "forward:/admin/setupper/initSetup/";
			return index(model, initSetupForm, bindingResult);
		}

		boolean success = false;
		try {
			Logger logger = getLogger();

			// トークンチェック
			if (!TokenProcessor.getInstance().isTokenValid(request, true)) {
				throw new ServiceException(lang.__("The request is invalid. Old request has been sent."));
			}

			final FutureResult futureResult = new FutureResult();

			// インポート条件入力チェック
			ExportFileSet exportFileSet =  null; // インポートファイルセット
			CommunityInfo communityInfo = null; //出力先サイトID
			jp.ecom_plat.map.db.GroupInfo groupInfo = null; //復元先グループ
			List<String> overrideParams = new ArrayList<>();
			if(0<initSetupForm.importmode) {
				ActionMessages errors = new ActionMessages();

				// ファイルのアップロードは重たいので、
				// 入力チェックよりもさきに処理する

				// インポートファイルセットのチェック・取得
				if(initSetupForm.templateFileMode==1) {
					exportFileSet = (ExportFileSet) session.getAttribute(SETUPPER_UPLOAD_TEMPLATE_FILE);
					if(exportFileSet==null) {
						throw new ServiceException(lang.__("Setting template has not been specified."));
					}
				}
				else {
					try {
						// リクエストパラメータのチェック
						MultipartFile formFile = initSetupForm.templateFile;
						if(formFile==null || formFile.getBytes().length==0) {
							throw new ServiceException(lang.__("Setting template has not been specified."));
						}

						// ファイルの展開
						File tmpDir = FileUtil.getTmpDir();
						// 日付ディレクトリを作成
						File tmpDateDir = null;
						{
							Calendar myCal = Calendar.getInstance();
							DateFormat format = new SimpleDateFormat("yyyyMMdd");
							tmpDateDir = new File(tmpDir, format.format(myCal.getTime()));
							if(tmpDateDir.exists()==false) {
								if(tmpDateDir.mkdir()==false) {
									logger.warn("fail mkdir: "+tmpDateDir.getAbsolutePath());
								}
							}
						}
						// 名前の衝突を回避するために、アップロードファイル専用のランダムなディレクトリを作成
						File uploadDir = null;
						{
							uploadDir = File.createTempFile("setupper-import-", "", tmpDateDir);
							boolean mkdir = false;
							if(uploadDir.delete()) {
								mkdir = uploadDir.mkdir();
							}
							if(mkdir==false) {
								logger.warn("fail mkdir: "+uploadDir.getAbsolutePath());
							}
						}

						HashSet<String> allowedExtent = new HashSet<String>(Arrays.asList(new String[]{"zip"}));
						String savedTemplateFileName = fileUploadService.uploadFile(initSetupForm.templateFile, uploadDir.getPath(), allowedExtent);
						if(savedTemplateFileName==null) {
							throw new ServiceException(lang.__("Failed to expand zip file."));
						}
						File templateZipFile = new File(uploadDir, savedTemplateFileName);
						if(!templateZipFile.exists()) throw new ServiceException(lang.__("There is no setting template."));
						exportFileSet =  ExportFileSet.unzip(templateZipFile);
						// セッションにアップロードしたZIPファイルを記録
						session.setAttribute(SETUPPER_UPLOAD_TEMPLATE_FILE, exportFileSet);

						// バージョンチェック
						if(exportFileSet.isValidVersion()==false) {
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("The version of configuration template does not agree with that of system."), false));
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Register again to continue importing configuration."), false));
							initSetupForm.confirmTemplateFile = true;
							overrideParams.add("confirmTemplateFile="+initSetupForm.confirmTemplateFile);
						}

					} catch(Exception e) {
						String msg = lang.__("Unable to load setting template.");
						logger.error(msg, e);
						// エラー内容をメッセージに追加
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(msg, false));
						addExceptionMessages(errors, e);
					}
					// 設定テンプレートファイルでエラーメッセージが出た場合は、すぐにエラー画面に表示する。
					if(0<errors.size()) {

						ActionMessagesUtil.addErrors(bindingResult, errors);

						setupModel(model);
						//return "forward:/admin/setupper/initSetup?success="+success;
						initSetupForm.success = success;
						return index(model, initSetupForm, bindingResult);
					}
				}

				// 入力チェック
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Text entered is wrong."), false));

				// 班の初期パスワード
				if(StringUtil.isEmpty(initSetupForm.password)) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Password is required."), false));
				// eコミマップユーザアカウント
				if(StringUtil.isEmpty(initSetupForm.ecomuser)) errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("e-Community map user account is required."), false));

				// 復元先サイトの取得
				if(initSetupForm.restoremode == 1){
					try {
						try {
							MapDB mapDB = MapDB.getMapDB();
							communityInfo = mapDB.getCommunityInfo(initSetupForm.mapRestoreCommunityid);
						} catch(Exception e) {
							logger.error("cannot get mapDB.getCommunityInfo("+initSetupForm.mapRestoreCommunityid+")", e);
						}
						if(communityInfo==null) throw new ServiceException(lang.__("Site ID = {0} not exist.", initSetupForm.mapRestoreCommunityid));
					} catch(Exception e) {
						String msg = lang.__("Failed to get the restoration target site of master map.");
						logger.error(msg, e);
						// エラー内容をメッセージに追加
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(msg, false));
						addExceptionMessages(errors, e);
					}
					// 復元先グループの取得
					// 復元先サイトが取得できていない場合は、グループの入力チェックを省略する
					if(communityInfo!=null) {
						try {
							if(0<initSetupForm.mapRestoreGroupid) {
								try {
									groupInfo = jp.ecom_plat.map.db.GroupInfo.getGroupInfo(initSetupForm.mapRestoreGroupid);
								} catch(Exception e) {
									logger.error("cannot get GroupInfo.getGroupInfo("+initSetupForm.mapRestoreGroupid+")", e);
								}
								if(groupInfo==null) throw new ServiceException(lang.__("Restoration target group(ID={0}) of master map not found.", initSetupForm.mapRestoreGroupid));
							}
							else {
								try {
									groupInfo = jp.ecom_plat.map.db.GroupInfo.getCommunityGroupInfo(communityInfo.communityId);
								} catch(Exception e) {
									logger.error("cannot get GroupInfo.getCommunityGroupInfo("+communityInfo.communityId+")", e);
								}
								if(groupInfo==null) throw new ServiceException(lang.__("Whole site group(site ID={0}), restoration target of master map not found.", communityInfo.communityId));
							}
							if(groupInfo.communityId!=communityInfo.communityId) throw new ServiceException(lang.__("Restoration target group of master map not in the specified site."));
						} catch(Exception e) {
							String msg = lang.__("Failed to get the restoration target group of master map.");
							logger.error(msg, e);
							// エラー内容をメッセージに追加
							errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(msg, false));
							addExceptionMessages(errors, e);
						}
					}
				}
				//新規サイト作成
				else{
					String siteName = initSetupForm.siteName;
					// サイト名称が未設定の場合は、自治体名を設定する
					if(StringUtil.isEmpty(siteName)) {
						siteName = initSetupForm.pref
								+ (StringUtil.isEmpty(initSetupForm.city) ? "" : initSetupForm.city);
					}
					String siteURL = initSetupForm.domain;
					if(siteURL != null) siteURL = siteURL.trim()+"/map/";

					double[] extent = new double[4];

					extent[0] = 120.20;
					extent[1] = 22.93;
					extent[2] = 151.35;
					extent[3] = 46.78;

					// ログイン中の言語と同じ言語で、新規サイトの言語を設定
			    	String langCode = lang.getLangCode();
					MapDB mapDB = MapDB.getMapDB();
					newCommunityInfo = mapDB.createNewCommunityInfo(siteName, siteURL, extent[0]+","+extent[1]+","+extent[2]+","+extent[3], langCode);
					// ユーザを新規作成
					UserInfo userInfo = mapDB.createNewUserInfo("admin", initSetupForm.ecomuser, 100, "", "");
					mapDB.insertCommunityUser(newCommunityInfo.communityId, userInfo.userId, userInfo.level);

					// 初期表示範囲
					String initRange = request.getParameter("init_range");

					JSONArray obj = null;
					InitRangeInfo[] initRanges = null;
					if (initRange != null){
						obj = new JSONArray(initRange);

						initRanges = new InitRangeInfo[obj.length()];
						for (int i = 0; i < obj.length(); i++){
							JSONObject one = (JSONObject)obj.get(i);

							InitRangeInfo oneInfo = new InitRangeInfo(i, InitRangeInfo.FOR_COMMUNITY, newCommunityInfo.communityId, one.getString("name"),one.getString("left"),one.getString("bottom"),one.getString("right"),one.getString("top"));
							try {
								InitRangeInfo.insert(oneInfo);
							} catch (Exception e){
								logger.warn("Error InitRange Save", e);
								String msg = lang.__("Error InitRange Save");
								logger.error(msg, e);
								// エラー内容をメッセージに追加
								errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(msg, false));
								addExceptionMessages(errors, e);
							}
							initRanges[i] = oneInfo;
						}
					}
					//デフォルト属性設定
					DBLang lang1 = LangUtils.getSiteDBLang(newCommunityInfo.communityId);
					mapDB.setOption("DEFAULT_ATTRS", "[{name:\""+lang1._("Label")+"\",dataType:12,length:20}]", newCommunityInfo.communityId);

					//サイト内地図投影法設定
					mapDB.setOption("EPSG","3857",newCommunityInfo.communityId);

					logger.info("sid:"+session.getId()+",create:{communityId:"+newCommunityInfo.communityId+"}");
					communityInfo = newCommunityInfo;

					// サイト全体のグループを取得
					groupInfo = jp.ecom_plat.map.db.GroupInfo.getCommunityGroupInfo(communityInfo.communityId);
				}
				// エラーメッセージがある場合は、エラー画面を表示する
				// メッセージを保存(エラーない場合でも"入力が間違っています"のメッセージ１つは必ず存在する)
				if(1<errors.size()) {
					ActionMessagesUtil.addErrors(bindingResult, errors);
					rollbackNewCommunityInfo();
					String url = "forward:/admin/setupper/initSetup?success="+success
							// そのほかのオーバライドパラメータがあれば追加
							+(0<overrideParams.size()?"&"+StringUtils.join(overrideParams, '&'):"");
					setupModel(model);
					return url;
				}
			}

			// 引数を futureResult にセットして渡す
			futureResult.exportFileSet = exportFileSet;
			futureResult.communityInfo = communityInfo;
			futureResult.groupInfo = groupInfo;
			futureResult.lang = SaigaiTaskLangUtils.getSessionLang();
			futureResult.loginDataDto =  new LoginDataDto();
		    BeanUtils.copyProperties(loginDataDto, futureResult.loginDataDto);
			// インポート処理があれば時間がかかるため別スレッドでインポート処理を実行する
			if(0<initSetupForm.importmode.intValue()) {
				Future<FutureResult> future = initSetupStatusDto.exec.submit(new Callable<FutureResult>() {
					@Override
					public FutureResult call() throws Exception {
						return executeInsert(futureResult);
					};
				});
				UUID uuid = initSetupStatusDto.addFuture(future);
				futureResult.requestid = uuid.toString();

				setupModel(model);
				return "forward:/admin/setupper/initSetup/index?requestid="+uuid.toString();
			}
			else {
				executeInsert(futureResult);
				ActionMessages errors = new ActionMessages();
				for(Exception e : futureResult.exceptions) {
					addExceptionMessages(errors, e);
				}
				ActionMessagesUtil.addErrors(bindingResult, errors);
				setupModel(model);
				initSetupForm.localgovinfoid = String.valueOf(futureResult.localgovInfo.id);
				initSetupForm.success = futureResult.success;
				return index(model, initSetupForm, bindingResult);
			}
		} catch (Exception e) {
			rollbackNewCommunityInfo();
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Failed to create local gov. by city configuration."), e);
			ActionMessages errors = new ActionMessages();
			addExceptionMessages(errors, e);

			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);
		return "forward:/admin/setupper/initSetup/index";
	}

	/**
	 * eコミのサイトを新規作成した場合のロールバック処理
	 */
	private void rollbackNewCommunityInfo() {
		// 新規作成したサイトがあれば削除する
		if(newCommunityInfo!=null) {
			ActionMessages errors = new ActionMessages();
			MapDB mapDB = MapDB.getMapDB();
			int communityId = newCommunityInfo.communityId;
			try {
				mapDB.deleteCommunityInfo(communityId);
			} catch (Exception e) {
				String msg = lang.__("Failed to delete ecom-map.");
				logger.error(msg, e);
				// エラー内容をメッセージに追加
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(msg, false));
				addExceptionMessages(errors, e);
			}
			// 削除したらメンバ変数をクリア
			newCommunityInfo = null;
		}
	}

	/**
	 * 自治体の削除を実行する
	 *
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/admin/setupper/initSetup/progress", produces="application/json")
	public ResponseEntity<String> progress(Map<String,Object>model,
			@ModelAttribute InitSetupForm initSetupForm, BindingResult bindingResult) {
		this.initSetupForm = initSetupForm;

		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		try {
			JSONObject json = new JSONObject();

			UUID uuid = UUID.fromString(initSetupForm.requestid);
			Future<FutureResult> future = initSetupStatusDto.getFuture(uuid);

			if(future!=null) {
				json.put("done", future.isDone());

				if(future.isDone()) {
					try {
						FutureResult futureResult = future.get();
						json.put("url", futureResult.url);
						json.put("success", futureResult.success);
						json.put("localgovinfoid", futureResult.localgovInfo.id);
					} catch (InterruptedException | ExecutionException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}

			// 出力の準備
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);

		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
			return new ResponseEntity<String>(null, httpHeaders, HttpStatus.SERVICE_UNAVAILABLE);
		}

	}

	//@TransactionAttribute(TransactionAttributeType.NEVER)
	@Transactional(propagation=Propagation.NEVER)
	protected FutureResult executeInsert(FutureResult futureResult) throws NotSupportedException, SystemException {

		// 別スレッドの場合、HTTPセッションが使えないため、明示的にセット
		SaigaiTaskDBLang lang = futureResult.lang;
		SaigaiTaskDBLang.langThreadLocal.set(lang);

		String logInfo = "[InitSetup.executeInsert]requestid="+futureResult.requestid+" ";

		ExportFileSet exportFileSet = futureResult.exportFileSet;
		CommunityInfo communityInfo = futureResult.communityInfo;
		GroupInfo groupInfo = futureResult.groupInfo;
		LoginDataDto loginDataDto = futureResult.loginDataDto;

		// すでにTransactionが張られていれば、Actionからの直接呼出し
		// まだTransactionが張られていなければ、Callableによる別スレッドの実行
		boolean callableTransaction = userTransaction.getStatus()==Status.STATUS_NO_TRANSACTION;
		if(callableTransaction) userTransaction.begin();
		futureResult.success = false;
		boolean resetSequenceOnError = false;
		LocalgovInfo localgov = null;
		try {
			Logger logger = getLogger();

			// システムマスタのリストア
			if(initSetupForm.importmode.intValue()==2) {
				logger.info(logInfo+" restoreMaster");
				importMasterService.restoreMaster(exportFileSet.masterExportXsl);
				// エラーが起きたらシーケンスリセットするフラグを立てる
				resetSequenceOnError = true;
			}

			// 自治体情報
			localgov = Beans
					.createAndCopy(LocalgovInfo.class, initSetupForm)
					.dateConverter("yyyy-MM-dd").execute();
			localgov.coordinatedecimal = false;
			localgovInfoService.insert(localgov);
			logger.info(logInfo+" localgovInfoService.insert id="+localgov.id);
			futureResult.localgovInfo = localgov;

			// 管理班は推奨設定に含まれているため追加しない。
/*
			// 班情報
			GroupInfo group = Beans.createAndCopy(GroupInfo.class, initSetupForm)
					.dateConverter("yyyy-MM-dd").execute();
			try {
				group.localgovinfoid = localgov.id;
				group.note = "";
				//group.id = localgov.id;
				group.admin = true;
				group.headoffice = true;
				group.password = UserAuthorization.getEncryptedPass(group.password);
				//group.ecompass = UserAuthorization.getEncryptedPass(group.ecompass);
				group.disporder = 1;
				group.valid = true;
				group.deleted = false;
			} catch (NoSuchAlgorithmException e) {
				logger.error(loginDataDto.logInfo(), e);
				throw new ServiceException("パスワードを暗号化できませんでした。", e);
			}
			groupInfoService.insert(group);
*/

			// マスタマップと自治体設定のインポート
			if(initSetupForm.importmode.intValue()==1||initSetupForm.importmode.intValue()==2) {
				logger.info(logInfo+" importInfoService.execute id="+localgov.id);
				try {
					importInfoService.execute(exportFileSet, localgov.id, initSetupForm.password, initSetupForm.ecomuser, communityInfo, groupInfo);
				} catch (Exception e) {
					throw new ServiceException(lang.__("An error occurred while importing master map and local gov. settings."), e);
				}
			}

			// ログ出力
			logger.info(lang.__("Create local gov. info:  localgovinfoid=")+localgov.id);

			futureResult.success=true;
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Failed to create local gov. by city configuration."), e);
			futureResult.exceptions.add(e);

			// システムマスタのリストアを実施した場合はシーケンスのロールバックを実行する
			if(resetSequenceOnError) {
				tableService.rollbackResetMaxSequenceAll();
			}

			rollbackNewCommunityInfo();

		} finally {
			if(callableTransaction) {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
						userTransaction.commit();
					} else {
						userTransaction.rollback();
					}
				} catch (Exception e) {
					logger.error(loginDataDto.logInfo(), e);
				}
			}
			SaigaiTaskDBLang.langThreadLocal.set(null);
		}

		// JavaScript で利用するURLなので「forward:index～」のような記述は不要
		futureResult.url = "index?success="+futureResult.success+(StringUtil.isNotEmpty(futureResult.requestid) ? "&requestid="+futureResult.requestid : "");
		if(futureResult.success) futureResult.url += "&localgovinfoid="+localgov.id;
		return futureResult;
	}

	protected void addExceptionMessages(ActionMessages messages, Exception e) {
		Throwable t = e;
		while(t!=null) {
			if(t instanceof ServiceException) {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　・"+t.getMessage(), false));
			}
			/*
			else if(t instanceof org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException) {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　・"+lang.__("The request failed due to the size of uploaded file over limitation."), false));
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　・"+t.getMessage(), false));
			}
			*/
			t=t.getCause();
		}
	}

	/**
	 * 自治体インポート非同期処理結果クラス
	 */
	public static class FutureResult {

		/** 多言語 */
		public SaigaiTaskDBLang lang;
		/** 処理リクエストID */
		public String requestid;
		/** 発生した例外リスト */
		public List<Exception> exceptions = new ArrayList<>();
		/** 成功フラグ */
		public boolean success = false;
		/** 処理後のURL */
		public String url;
		/** 復元先の班 */
		public GroupInfo groupInfo;
		/** 復元先のサイト情報 */
		public CommunityInfo communityInfo;
		/** 復元ファイルセット */
		public ExportFileSet exportFileSet;
		/** ログイン情報 */
		public LoginDataDto loginDataDto;
		/** 作成した自治体情報 */
		public LocalgovInfo localgovInfo;
	}

	@ExceptionHandler(MultipartException.class)
	@ResponseBody
	String handleFileException(HttpServletRequest request, Throwable ex) {
	    //return your json insted this string.
	    return "File upload error";
	}
}
