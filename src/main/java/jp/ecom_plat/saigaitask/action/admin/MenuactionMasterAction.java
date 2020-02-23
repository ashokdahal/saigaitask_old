/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import org.springframework.web.bind.annotation.RequestMapping;


//public class MenuactionMasterAction extends AbstractAdminAction<MenuactionMaster> {
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/menuactionMaster")
public class MenuactionMasterAction{
//	protected MenuactionMasterForm menuactionMasterForm;
//
//	/** サービスクラス */
//	@Resource
//	protected MenuactionMasterExService menuactionMasterExService;
//
//	/** サービスクラス */
//	@Resource
//	protected ResponseExService responseExService;
//
//
//	/** HttpServletRequest  */
//	@Resource
//	protected HttpServletRequest request;
//
//	/** HttpServletResponse  */
//	@Resource
//	protected HttpServletResponse response;
//
//
	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/menuactionMaster/index";
	}
//
//	/**
//	 * 一覧情報を取得してJSON形式で返却（Ajax）
//	 * @return null
//	 * @throws Exception
//	 */
//	public String jqgridindex() throws Exception {
//		//検索条件マップ
//		BeanMap conditions = new BeanMap();
//
//		//親のIDの取得
//		String parentId = (String)request.getParameter("null");
//		if(StringUtils.isNotEmpty(parentId)){
//			//検索条件をセット
//			conditions.put("null", parentId);
//		}
//
//
//		//検索フォーム関連パラメータ取得
//		boolean isSearch = Boolean.valueOf((String) request.getParameter("_search")); //検索実行時か否か
//		String searchField = (String) request.getParameter("searchField"); //検索対象
//		String searchOper = (String) request.getParameter("searchOper");   //比較演算子
//		String searchString =  (String) request.getParameter("searchString");   //検索文字列
//		if(isSearch && StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchOper) && StringUtils.isNotEmpty(searchString)){
//			//検索条件をセット
//			conditions.put(JqGridUtil.getCoditionStr(searchField, searchOper), searchString);
//		}
//
//		//ページャー関連パラメータ取得
//		int page = Integer.parseInt((String)request.getParameter("page"));  //現在ページ番号
//		int rows =  Integer.parseInt((String)request.getParameter("rows")); //1ページの件数
//		String sidx = (String) request.getParameter("sidx");    //ソート項目名
//		String sord = (String) request.getParameter("sord");    //ソート順（昇順 or 降順）
//
//		//postDataパラメータ取得
//		//loadonce
//		boolean loadonce = Boolean.valueOf((String)request.getParameter("loadonce"));
//
//		//取得件数の判定
//		//loadonce=true：グリッドデータを1回ロードし、その後はある程度クライアント側だけで済ませるため、データは全件取得。
//		//loadonce=false：グリッドデータは必要な時に必要（1ページ）な分だけ取得するため、データは1ページ分取得。
//		int limit = rows;
//		if(loadonce){
//			limit = 0;
//			page = 1;
//		}
//
//		// 一覧件数取得
//		int count = menuactionMasterExService.getCount(conditions);
//
//		// 一覧内容取得
//		List<MenuactionMaster> list = new ArrayList<MenuactionMaster>();
//		if(count > 0){
//			list = menuactionMasterExService.findByCondition(conditions, sidx, sord, limit, (page-1)*limit);
//		}
//
//		//ユーザーデータ取得
//		Map<String, Object> userdata = new HashMap<String, Object>();
//
//		//表示順取得
//		int maxDisporder = 0;
//		BeanMap maxDisporderConditions = new BeanMap();
//		//親IDが存在する場合、親IDに紐付くデータの内、最大の表示順を取得する。
//		maxDisporderConditions.put("null", Integer.parseInt(parentId));
//		maxDisporder = menuactionMasterExService.getLargestDisporder(maxDisporderConditions);
//		userdata.put("maxDisporder", maxDisporder);
//
//		//ページャー関連パラメータ再計算
//		int totalPages = 0;
//		if( count >0 ) {
//			totalPages = (int)Math.ceil((double)count/rows);
//		}
//		if (page > totalPages){
//			page = totalPages;
//		}
//
//		//返却値セット
//		JSONObject json = new JSONObject();
//		json.put("page", page); //現在ページ番号
//		json.put("records", count); //総結果件数
//		json.put("total", totalPages);  //総ページ数
//		json.put("userdata", userdata); //ユーザーデータ
//		json.put("rows", jsonForCyclicalReferences(list));   //結果一覧
//
//		//レスポンスへ出力
//		responseExService.responseJson(json);
//
//		return null;
//	}
//
//	/**
//	 * Jqgrid新規登録、編集、削除実行（Ajax）
//	 * @return null
//	 * @throws Exception
//	 */
//	public String jqgridedit() throws Exception {
//		// CSRF対策
//		if (!FormUtils.checkToken(request)) {
//			throw new InvalidAccessException(lang.__("Invalid session."));
//		};
//
//		//操作種別取得
//		String oper = (String)request.getParameter("oper");
//		if(StringUtils.isEmpty(oper)){
//			//TODO:エラー処理
//			return null;
//		}
//
//		//操作種別に応じたアクションを実行
//		MenuactionMaster entity;
//		//登録
//		if("add".equals(oper)) {
//			entity = Beans.createAndCopy(MenuactionMaster.class, menuactionMasterForm).dateConverter("yyyy-MM-dd").execute();
//			//登録実行
//			menuactionMasterExService.insert(entity);
//			//採番したキー値を返却
//			JSONObject json = new JSONObject();
//			json.put("newEntity", StringUtil.jsonForCyclicalReferences(entity));
//			responseExService.responseJson(json);
//		//編集
//		}else if("edit".equals(oper)) {
//			//編集前データのシリアライズ文字列を取得
//			String serializedPreEditData = (String)request.getParameter("serializedPreEditData");
//			if(StringUtils.isEmpty(serializedPreEditData)){
//				//TODO:エラー処理
//				return null;
//			}else{
//				//現時点の編集対象データを取得
//				MenuactionMaster menuactionMaster = menuactionMasterExService.findById(Integer.parseInt(menuactionMasterForm.id));
//
//				//結果返却用JSON
//				JSONObject json = new JSONObject();
//				if(menuactionMaster != null){
//					//編集対象が存在する場合、実行。
//					//現時点の編集対象データが他者により変更されているかを判定
//					if(JqGridUtil.existsNoChangeByOther(serializedPreEditData, MenuactionMaster.class, menuactionMaster)){
//						//変更されていない場合、更新
//						entity = Beans.createAndCopy(MenuactionMaster.class, menuactionMasterForm).dateConverter("yyyy-MM-dd").execute();
//						PropertyName<?>[] updateExcludesAry = null;
//						List<PropertyName<?>> updateExcludesList = new ArrayList<PropertyName<?>>();
//						//更新実行
//						menuactionMasterExService.update(entity, updateExcludesAry);
//						json.put("editEntity", StringUtil.jsonForCyclicalReferences(entity));
//						responseExService.responseJson(json);
//					}else{
//						//変更されている場合、更新せずにリフレッシュをユーザーに促すメッセージを返却。
//						json.put("message", "別のユーザーにより変更されている可能性があります。\nリフレッシュして最新状態にして下さい。");
//						responseExService.responseJson(json);
//					}
//				}else{
//					//編集対象が存在しない場合、他者により削除された可能性があるため更新は実行実行せず、
//					//リフレッシュをユーザーに促すメッセージを返却。
//					json.put("message", "別のユーザーにより削除されている可能性があります。\nリフレッシュして最新状態にして下さい。");
//					responseExService.responseJson(json);
//				}
//			}
//		//削除
//		}else if("del".equals(oper)) {
//			//削除対象データの有無を取得
//			//検索条件マップ
//			BeanMap conditions = new BeanMap();
//			conditions.put("id", Integer.parseInt(menuactionMasterForm.id));
//			int count = menuactionMasterExService.getCount(conditions);
//			if(count==1){
//				//削除対象が存在する場合、削除実行。
//				entity = menuactionMasterExService.findById(Integer.parseInt(menuactionMasterForm.id));
//				//削除実行
//				menuactionMasterExService.delete(entity);
//			}else{
//				//削除対象が存在しない場合、他者により削除された可能性があるため削除は実行実行せず、
//				//リフレッシュをユーザーに促すメッセージを返却。
//				JSONObject json = new JSONObject();
//				json.put("message", "別のユーザーにより削除された可能性があります。\nリフレッシュして最新状態にします。");
//				responseExService.responseJson(json);
//			}
//		}else{
//			//TODO:エラー処理
//		}
//
//		return null;
//	}
//
//    /**
//     * Jqgrid selectボックスタグの構成要素を生成（Ajax）
//     * @return null
//     * @throws Exception
//     */
//    public String createSelectTag() throws Exception {
//        //パラメータ取得
//        String selectTagColumn = (String) request.getParameter("selectTagColumn"); //selectタグとするグリッドカラム名
//        String tableName = (String) request.getParameter("tableName"); //デコード値を保持しているテーブル名
//        String codeColumn = (String) request.getParameter("codeColumn"); //デコード値を保持しているテーブルのコードカラム
//        String decodeColumn = (String) request.getParameter("decodeColumn"); //デコード値を保持しているテーブルのデコードカラム
//
//        //返却値
//        JSONObject json = new JSONObject();
//
//        if(StringUtils.isEmpty(selectTagColumn)
//                || StringUtils.isEmpty(tableName)
//                || StringUtils.isEmpty(codeColumn)
//                || StringUtils.isEmpty(decodeColumn)){
//            json.put("selectTag", "");
//        }else{
//            //デコード値を保持しているテーブルのデータを取得し、コードをデコードしたselectボックスタグの構成要素を生成。
//            String dbServiceName = "jp.go.bosai.saigaitask.service.db."+StringUtil.snakeToCamelCapitalize(tableName)+"ExService";
//            String dbServiceMethodName = "findByCondition";
//            Class<?>[] methodArgType = {Map.class, String.class, String.class, Integer.class, Integer.class};
//            Object[] methodArg = {new BeanMap(), codeColumn, "asc", 0, null};
//            Object result = executeDbServiceMethod(dbServiceName, dbServiceMethodName, methodArgType, methodArg);
//            List<?> parentList = (ArrayList<?>)result;
//
//            //返却値セット
//            json.put("selectTag", StringUtil.json(JqGridUtil.createSelectTagList(parentList, codeColumn, decodeColumn)));   //結果一覧
//        }
//
//        //レスポンスへ出力
//        responseExService.responseJson(json);
//
//        return null;
//    }
}
