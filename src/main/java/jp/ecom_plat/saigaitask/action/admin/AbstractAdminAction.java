/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.util.Constants.DB_JDBCMANAGER_CLASSNAME;
import static jp.ecom_plat.saigaitask.util.Constants.DB_SERVICE_PKG;
import static jp.ecom_plat.saigaitask.util.Constants.DB_SERVICE_SUFFIX;
import static jp.ecom_plat.saigaitask.util.Constants.ENTITY_PKG;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_MESSAGE;

import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.ReflectionUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.db.MultilangInfoService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 管理画面Actionクラスのスーパークラス
 */
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
@SuppressWarnings({"serial", "unused"})
public abstract class AbstractAdminAction<ENTITY> {

    /** ログイン情報 */
    @Resource protected LoginDataDto loginDataDto;

    /** HTTP Request */
    @Resource protected HttpServletRequest request;
    /** HTTP Response */
    @Resource protected HttpServletResponse response;
    /** HTTP Session */
    @Resource protected HttpSession session;
    /** Servlet Context */
    @Resource protected ServletContext application;
	/** サービスクラス */
	@Resource
	protected ResponseService responseService;
	
	@Resource
	protected JdbcManager jdbcManager;

    /** Logger */
    protected Logger logger = Logger.getLogger(AbstractAction.class);

	@javax.annotation.Resource protected SaigaiTaskDBLang lang;
	@Resource protected LocalgovInfoService localgovInfoService;
	@Resource protected MultilangInfoService multilangInfoService;

	public void setupModel(Map<String,Object>model) {
		model.put("loginDataDto", loginDataDto);
	}

    /*
     * 画面階層上、直下のサブグリッド（テーブル）名とサブグリッドでの親参照カラム名を保持するマップ
     * 画面階層が変更になると修正が必要。
     */
//    private static final Map<String, Map<String, String>> SUBGRID_MAP;
//    static {
//    	Map<String, Map<String, String>> _map = new HashMap<String, Map<String, String>>(){{
//	    	//ユーザ－系
//	    	put("group_info", new HashMap<String, String>(){{
//	    		put("unit_info", Names.unitInfo().groupid().toString());
//	    	}});
//	    	put("unit_info", new HashMap<String, String>(){{
//	    		put("user_info", Names.userInfo().unitid().toString());
//	    	}});
//	    	//メニュー系
//	    	put("menulogin_info", new HashMap<String, String>(){{
//	    		put("menuprocess_info", Names.menuprocessInfo().menulogininfoid().toString());
//	    	}});
//	    	put("menuprocess_info", new HashMap<String, String>(){{
//	    		put("menutask_info", Names.menutaskInfo().menuprocessinfoid().toString());
//	    		put("noticedefault_info", Names.noticedefaultInfo().menuprocessid().toString());
//	    	}});
//	    	put("menutask_info", new HashMap<String, String>(){{
//	    		put("menu_info", Names.menuInfo().menutaskinfoid().toString());
//	    	}});
//	    	put("menu_info", new HashMap<String, String>(){{
//	    		put("menutable_info", Names.menutableInfo().menuinfoid().toString());
//	    		put("pagemenubutton_info", Names.pagemenubuttonInfo().menuinfoid().toString());
//	    		put("maplayer_info", Names.maplayerInfo().menuinfoid().toString());
//	    		put("mapreferencelayer_info", Names.mapreferencelayerInfo().menuinfoid().toString());
//	    		put("mapbaselayer_info", Names.mapbaselayerInfo().menuinfoid().toString());
//	    		put("externalmapdata_info", Names.externalmapdataInfo().menuinfoid().toString());
//	    		put("externaltabledata_info", Names.externaltabledataInfo().menuinfoid().toString());
//	    		put("menumap_info", Names.menumapInfo().menuinfoid().toString());
//	    	}});
//	    	put("menutable_info", new HashMap<String, String>(){{
//	    		put("tablelistcolumn_info", Names.tablelistcolumnInfo().menutableinfoid().toString());
//	    	}});
//	    	put("maplayer_info", new HashMap<String, String>(){{
//	    		put("maplayerattr_info", Names.maplayerattrInfo().maplayerinfoid().toString());
//	    	}});
//    	}};
//    	SUBGRID_MAP = Collections.unmodifiableMap(_map);
//    }

    /**
     * 画面階層上、直下のサブグリッド（テーブル）名とサブグリッドでの親参照カラム名を保持するマップを返却する。
     *  @param tableName
     *  @return サブグリッドマップ
     */
//    protected Map<String, String> getSubgridList(String tableName){
//    	return SUBGRID_MAP.get(tableName);
//    }

    /*
     * 各グリッドの各種設定を保持するクラスへの参照を保持するマップ
     */
    private static final Map<String, Object> GRID_CONF_MAP;
    static{
    	Map<String, Object> _map = new HashMap<String, Object>(){{
	    	//ユーザー系
    		put("group_info", new GroupInfoConf());
	    	put("unit_info", new UnitInfoConf());
	    	put("user_info", new UserInfoConf());
	    	//メニュー系
	    	put("menulogin_info", new MenuloginInfoConf());
	    	put("menuprocess_info", new MenuprocessInfoConf());
	    	put("menutask_info", new MenutaskInfoConf());
	    	put("menutaskmenu_info", new MenutaskmenuInfoConf());
	    	//タスク種別以下
	    	put("menutasktype_info", new MenutasktypeInfoConf());
	    	put("menu_info", new MenuInfoConf());
	    	put("noticedefault_info", new NoticedefaultInfoConf());
	    	put("menutable_info", new MenutableInfoConf());
	    	put("pagemenubutton_info", new PagemenubuttonInfoConf());
	    	put("maplayer_info", new MaplayerInfoConf());
	    	put("mapreferencelayer_info", new MapreferencelayerInfoConf());
	    	put("mapbaselayer_info", new MapbaselayerInfoConf());
	    	put("externalmapdata_info", new ExternalmapdataInfoConf());
	    	put("externaltabledata_info", new ExternaltabledataInfoConf());
	    	put("menumap_info", new MenumapInfoConf());
	    	put("tablelistcolumn_info", new TablelistcolumnInfoConf());
	    	put("maplayerattr_info", new MaplayerattrInfoConf());
    	}};
    	GRID_CONF_MAP = Collections.unmodifiableMap(_map);
    }

    protected static abstract class GridConf{
    	protected List<String> subgridList;
    	protected String parentRefKey;
		public List<String> getSubgridList() {
			return subgridList;
		}
		public String getParentRefKey() {
			return parentRefKey;
		}
    }

    /* 班情報設定クラス */
    private static class GroupInfoConf extends GridConf{
    	GroupInfoConf(){
	    	subgridList = java.util.Arrays.asList("unit_info");
			parentRefKey = "";
    	}
    }
    /* ユニット情報設定クラス */
    private static class UnitInfoConf extends GridConf{
    	UnitInfoConf(){
	    	subgridList = java.util.Arrays.asList("user_info");
	    	//parentRefKey = Names.unitInfo().groupid().toString();
	    	parentRefKey = "";
    	}
    }
    /* ユーザ情報設定クラス */
    private static class UserInfoConf extends GridConf{
    	UserInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.userInfo().unitid().toString();
    	}
    }
    /* メニュー設定情報設定クラス */
    private static class MenuloginInfoConf extends GridConf{
    	MenuloginInfoConf(){
	    	subgridList = java.util.Arrays.asList("menuprocess_info");
	    	parentRefKey = "";
    	}
    }
    /* メニュープロセス情報設定クラス */
    private static class MenuprocessInfoConf extends GridConf{
    	MenuprocessInfoConf(){
//	    	subgridList = java.util.Arrays.asList("menutask_info", "noticedefault_info");
	    	subgridList = java.util.Arrays.asList("menutask_info");
	    	parentRefKey = Names.menuprocessInfo().menulogininfoid().toString();
    	}
    }
    /* メニュータスク情報設定クラス */
    private static class MenutaskInfoConf extends GridConf{
    	MenutaskInfoConf(){
	    	subgridList = java.util.Arrays.asList("menutaskmenu_info");
	    	parentRefKey = Names.menutaskInfo().menuprocessinfoid().toString();
    	}
    }
    /* タスクメニュー情報設定クラス */
    private static class MenutaskmenuInfoConf extends GridConf{
    	MenutaskmenuInfoConf(){
	    	subgridList =  null;
	    	parentRefKey = Names.menutaskmenuInfo().menutaskinfoid().toString();
    	}
    }
    /* タスク種別情報設定クラス */
    private static class MenutasktypeInfoConf extends GridConf{
    	MenutasktypeInfoConf(){
	    	subgridList =  java.util.Arrays.asList("menu_info");
	    	parentRefKey = "";
    	}
    }
    /* 通知デフォルト情報設定クラス */
    private static class NoticedefaultInfoConf extends GridConf{
    	NoticedefaultInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.noticedefaultInfo().menuinfoid().toString();
    	}
    }
    /* メニュー情報設定クラス */
    private static class MenuInfoConf extends GridConf{
    	MenuInfoConf(){
	    	subgridList = java.util.Arrays.asList("menutable_info", "pagemenubutton_info", "maplayer_info", "mapreferencelayer_info", "mapbaselayer_info", "externalmapdata_info", "externaltabledata_info", "menumap_info", "noticedefault_info");
//	    	subgridList =  null;
    		parentRefKey = Names.menuInfo().menutasktypeinfoid().toString();
    	}
    }
    /* メニューテーブル情報設定クラス */
    private static class MenutableInfoConf extends GridConf{
    	MenutableInfoConf(){
	    	subgridList = java.util.Arrays.asList("tablelistcolumn_info");
	    	parentRefKey = Names.menutableInfo().menuinfoid().toString();
    	}
    }
    /* ページボタン表示情報設定クラス */
    private static class PagemenubuttonInfoConf extends GridConf{
    	PagemenubuttonInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.pagemenubuttonInfo().menuinfoid().toString();
    	}
    }
    /* 地図レイヤ情報設定クラス */
    private static class MaplayerInfoConf extends GridConf{
    	MaplayerInfoConf(){
	    	subgridList = java.util.Arrays.asList("maplayerattr_info");
	    	parentRefKey = Names.maplayerInfo().menuinfoid().toString();
    	}
    }
    /* 地図参照レイヤ情報設定クラス */
    private static class MapreferencelayerInfoConf extends GridConf{
    	MapreferencelayerInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.mapreferencelayerInfo().menuinfoid().toString();
    	}
    }
    /* 地図ベースレイヤ情報設定クラス */
    private static class MapbaselayerInfoConf extends GridConf{
    	MapbaselayerInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.mapbaselayerInfo().menuinfoid().toString();
    	}
    }
    /* 外部地図データ情報設定クラス */
    private static class ExternalmapdataInfoConf extends GridConf{
    	ExternalmapdataInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.externalmapdataInfo().menuinfoid().toString();
    	}
    }
    /* 外部リストデータ情報設定クラス */
    private static class ExternaltabledataInfoConf extends GridConf{
    	ExternaltabledataInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.externaltabledataInfo().menuinfoid().toString();
    	}
    }
    /* メニュー地図情報設定クラス */
    private static class MenumapInfoConf extends GridConf{
    	MenumapInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.menumapInfo().menuinfoid().toString();
    	}
    }
    /* テーブルリスト項目情報設定クラス */
    private static class TablelistcolumnInfoConf extends GridConf{
    	TablelistcolumnInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.tablelistcolumnInfo().menutableinfoid().toString();
    	}
    }
    /* 地図レイヤ属性情報設定クラス */
    private static class MaplayerattrInfoConf extends GridConf{
    	MaplayerattrInfoConf(){
	    	subgridList = null;
	    	parentRefKey = Names.maplayerattrInfo().maplayerinfoid().toString();
    	}
    }

    /**
     * 各グリッドの各種設定を保持するクラスへの参照を保持するマップを返却する。
     * @param tableName テーブル名
     * @return テーブルのグリッドの設定マップ
     */
    public GridConf getGridConf(String tableName){
    	return (GridConf)GRID_CONF_MAP.get(tableName);
    }


//    /*
//     * 特定のテーブルから自治体IDを持つテーブルまでの結合を加味した自治体ID検索条件を保持するマップ
//     * リレーションが変更になると修正が必要。
//     */
//    private static final Map<String, String> LOCALGOV_BINDING_MAP;
//    static {
//    	Map<String, String> _map = new HashMap<String, String>();
//    	_map.put("localgov_info", Names.localgovInfo().id().toString());
//    	_map.put("menu_info", Names.menuInfo().menutaskInfo().menuprocessInfo().menuloginInfo().groupInfo().localgovInfo().id().toString());
//    	_map.put("menutable_info", Names.menutableInfo().menuInfo().menutaskInfo().menuprocessInfo().menuloginInfo().groupInfo().localgovinfoid().toString());
//    	_map.put("tablemaster_info", Names.tablemasterInfo().mapmasterInfo().localgovinfoid().toString());
//    	LOCALGOV_BINDING_MAP = Collections.unmodifiableMap(_map);
//    }
//
//    /**
//     * 指定のテーブルから自治体IDを持つテーブルまでの結合を加味した自治体ID検索条件を返却する。
//     *  @param tableName
//     *  @return 検索条件文字列
//     */
//    protected String getLocalgovCondition(String tableName){
//    	return LOCALGOV_BINDING_MAP.get(tableName);
//    }

	/**
	 * クラスの各フィールド名
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getFieldNames(Class cls) {
		Field[] fields = cls.getDeclaredFields();
		List<String> flist = new ArrayList<String>();
		for (Field field : fields)
			// finai修飾子のフィールドと@JoinColumnのフィールドは除く
			if ( ! Modifier.isFinal(field.getModifiers()) && field.getAnnotation(JoinColumn.class)==null && field.getAnnotation(OneToMany.class)==null) {
				flist.add(field.getName());
			}
		return flist;
	}

    /**
     * DBサービスクラス名とDBサービスクラスのメソッド名からメソッドを実行し、結果を返却する。
     * @param dbServiceName DBサービスクラス完全修飾名
     * @param dbServiceMethodName DBサービスクラスのメソッド名
     * @param methodArgType DBサービスクラスのメソッドの引数の型の配列
     * @param methodArg DBサービスクラスのメソッドの引数の配列
     * @return DBサービスクラスのメソッドの実行結果のObject
     * @throws Exception
     */
    protected Object executeDbServiceMethod(String dbServiceName, String dbServiceMethodName, Class<?>[] methodArgType, Object[] methodArg) throws Exception{
        if(StringUtil.isEmpty(dbServiceName)
                || StringUtil.isEmpty(dbServiceMethodName)){
//                || methodArgType == null
//                || methodArg == null){
            return null;
        }

        Class<?> targetCls = ReflectionUtil.forName(dbServiceName);
        //DBサービスクラスのみ実行可能
        if(!DB_SERVICE_PKG.equals(targetCls.getPackage().getName())){
            return null;
        }

        Object dbServiceObj = ReflectionUtil.newInstance(targetCls);

        //リフレクションでインスタンス生成した場合、jdbcManagerがDIされないためここで生成する。
        Class<?> clazz = ReflectionUtil.forName(dbServiceName);
        loop : while(clazz != null){
            Field[] f = clazz.getDeclaredFields();
            for(int i=0; i<f.length; i++){
                if(DB_JDBCMANAGER_CLASSNAME.equals(f[i].getName())){
                    f[i].setAccessible(true);
                    if(ReflectionUtil.getValue(f[i], dbServiceObj) == null){
                        //SingletonS2ContainerFactory.init();
                        //S2Container container = SingletonS2ContainerFactory.getContainer();
                        ReflectionUtil.setValue(f[i], dbServiceObj,  jdbcManager/*(JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME)*/);
                        break loop;
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        //メソッド実行
        Method method = ReflectionUtil.getMethod(targetCls, dbServiceMethodName, methodArgType);
        return ReflectionUtil.invoke(method, dbServiceObj, methodArg);
    }

	/**
	 * テーブルの表示階層に従い、指定されたテーブル以下の紐付くデータをコピーする。
	 * @param tableName コピーの起点となるテーブル名
	 * @param oldParentId コピーする親ID
	 * @param newParentId コピー時に採番した親ID
	 * @throws Exception
	 */
	protected void doAllCopy(String tableName, Object oldParentId, Object newParentId) throws Exception{
		GridConf gridConf = getGridConf(tableName);
		//サブグリッドリスト取得
		List<String> subgridList = gridConf.getSubgridList();
		if(subgridList != null){
			//サブグリッドがあればidに紐付くサブグリッドデータを取得
			for(String subgridName : subgridList){
				//サブグリッド設定取得
				GridConf subGridConf = getGridConf(subgridName);
				//親参照キー取得
				String refKeyName = subGridConf.getParentRefKey();
				//selectの設定
				String dbServiceName = DB_SERVICE_PKG+"."+jp.ecom_plat.saigaitask.util.StringUtil.snakeToCamelCapitalize(subgridName)+DB_SERVICE_SUFFIX;
				String dbServiceSelectMethodName = "getCount";
				Class<?>[] countMethodArgType = {Map.class};
				Map<String, Object> countConditions = new HashMap<String, Object>();
				countConditions.put(refKeyName, oldParentId);
				Object[] countMethodArg = {countConditions};
				//count実行
				Object result = executeDbServiceMethod(dbServiceName, dbServiceSelectMethodName, countMethodArgType, countMethodArg);
				//サブグリッドデータが取得できなかったら次のグリッドへ。
				if(new Integer(result.toString()) == 0){
					continue;
				}
				//select実行
				Class<?>[] selectMethodArgType = {Map.class, String.class, String.class, Integer.class, Integer.class};
				Map<String, Object> conditions = new HashMap<String, Object>();
				conditions.put(refKeyName, oldParentId);
				Object[] selectMethodArg = {conditions, "id", OrderingSpec.ASC.toString(), null, null};
				dbServiceSelectMethodName = "findByCondition";
				result = executeDbServiceMethod(dbServiceName, dbServiceSelectMethodName, selectMethodArgType, selectMethodArg);
				List<?> subgridDataList = (ArrayList<?>)result;

				//insertの設定
				String entityName = ENTITY_PKG+"."+jp.ecom_plat.saigaitask.util.StringUtil.snakeToCamelCapitalize(subgridName);
				String dbServiceInsertMethodName = "insert";
				Class<?>[] insertMethodArgType = {Object.class};

				//取得したサブグリッドデータのidをクリア、親idに上位で採番したidをセットして登録。
				for(int i=0; i<subgridDataList.size(); i++){
					Object entity = subgridDataList.get(i);
					Class<? extends Object> clazz = entity.getClass();
					Field idField = clazz.getField("id");
					Object oldId =idField.get(entity);
					idField.set(entity, null);
					Field parentIdField = clazz.getField(refKeyName);
					parentIdField.set(entity, newParentId);
					Object[] insertMethodArg = {entity};
					executeDbServiceMethod(dbServiceName, dbServiceInsertMethodName, insertMethodArgType, insertMethodArg);
					doAllCopy(subgridName, oldId, idField.get(entity));
				}
			}
		}
	}

	/**
	 * ファイルアップロード処理
	 * @param uploadFile
	 * @param localgovinfoid
	 * @throws ServiceException
	 */
	protected boolean fileUpload(MultipartFile uploadFile, String localgovinfoid, String fileName) throws ServiceException {
		// TODO: 単一ファイルのみ対応。複数アップロードの要件があれば修正が必要。
		// TODO: ファイル拡張子チェックは必要？
		// TODO: ファイルのメタ情報をテーブルで管理しない？
		long fileSize = uploadFile.getSize();
		if (fileSize != 0) {
			// TODO: アップロードディレクトリの定数化？それとも画面から渡す？「xsltファイルパス」はどう使われる？
			// TODO: アップロードディレクトリを工夫しないとファイルが上書かれる。
//			String path = application.getRealPath("/upload/" + uploadFile.getName());
//			String path = application.getRealPath("/WEB-INF/jmaxml/xslt/" + uploadFile.getName());
			ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
			String mxsltPath = rb.getString("METEOXSLTPATH");  //    /xslt/
			String dirPath = "/WEB-INF/jmaxml" + mxsltPath + localgovinfoid + "/";

			String path = application.getRealPath(dirPath + /*uploadFile.getName()*/fileName);

			try {
				File dir = new File(application.getRealPath(dirPath));
				if (!dir.exists()) dir.mkdirs();
				OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
				try {
					out.write(uploadFile.getBytes(), 0, (int) fileSize);
					//TODO: クライアントへの返却メッセージ
					JSONObject json = new JSONObject();
					responseService.responseJsonText(json);
					return true;
				} finally {
					out.close();
				}
			} catch (Exception e) {
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
		}else{
			// TODO: サイズゼロ時のハンドリング処理
			try {
				//responseService.response("{\"result\":\"ok\"}");
				JSONObject json = new JSONObject();
				json.put(JQG_MESSAGE, lang.__("The file size is zero."));
				responseService.responseJsonText(json);
				return false;
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
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
		}
	}

	/**
	 * エクセル帳票テンプレートファイルアップロード処理
	 * @param uploadFile
	 * @param localgovinfoid
	 * @throws ServiceException
	 */
	protected File excelListTemplatefileUpload(MultipartFile uploadFile, String localgovinfoid, String fileName) throws ServiceException {
		long fileSize = uploadFile.getSize();
		if (fileSize != 0) {
			String fileExt = "";
			int point = uploadFile.getOriginalFilename().lastIndexOf(".");
			if (point != -1) {
				fileExt = uploadFile.getOriginalFilename().substring(point + 1);
				fileExt = fileExt.toLowerCase();
			}

			// 拡張子チェック。.xlsxのみ有効
			if(!((!StringUtil.isEmpty(fileExt)) && fileExt.equals("xlsx"))){
				try{
					JSONObject json = new JSONObject();
					json.put(JQG_MESSAGE, lang.__("Excel template file: only XLSX format is valid."));
					responseService.responseJsonText(json);
					return null;
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
			}

			ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
			String dirPath = Constants.EXCELLIST_BASEDIR + localgovinfoid + "/";

			String path = application.getRealPath(dirPath + fileName);

			try {
				File dir = new File(application.getRealPath(dirPath));
				if (!dir.exists()) dir.mkdirs();
				OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
				try {
					out.write(uploadFile.getBytes(), 0, (int) fileSize);
					//TODO: クライアントへの返却メッセージ
//					JSONObject json = new JSONObject();
//					responseService.responseJsonText(json);
					return new File(path);
				} finally {
					out.close();
				}
			} catch (Exception e) {
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
		}else{
			try {
				JSONObject json = new JSONObject();
				json.put(JQG_MESSAGE, lang.__("The file size is zero."));
				responseService.responseJsonText(json);
				return null;
			} catch (Exception e) {
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
		}
	}
	
	/**
	 * ロゴ画像ファイルアップロード処理
	 * @param uploadFile
	 * @param localgovinfoid
	 * @throws ServiceException
	 */
	protected File logoImagefileUpload(MultipartFile uploadFile, String localgovinfoid) throws ServiceException {
		long fileSize = uploadFile.getSize();
		String fileExt = "";
		// IEだとgetOriginalFilename()に絶対パスが入ってくるので一旦Fileオブジェクトに格納
		File uploadFileObj = new File(uploadFile.getOriginalFilename());
		String uploadFileName =  uploadFileObj.getName();
		int point = uploadFileName.lastIndexOf(".");
		if (point != -1) {
			fileExt = uploadFileName.substring(point + 1);
			fileExt = fileExt.toLowerCase();
		}

		// 拡張子チェック。gif,jpeg,jpg,png,bmpであれば有効
		if(!((!StringUtil.isEmpty(fileExt)) && (
				fileExt.equals("gif") ||
				fileExt.equals("jpeg") ||
				fileExt.equals("jpg") ||
				fileExt.equals("png") ||
				fileExt.equals("bmp") 
					))){
			try{
				JSONObject json = new JSONObject();
				json.put(JQG_MESSAGE, lang.__("Logo image file: only image format is valid."));
				responseService.responseJsonText(json);
				return null;
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
		}

		ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
		String dirPath = Constants.LOGOIMAGEFILE_BASEDIR + localgovinfoid + "/";

		String path = application.getRealPath(dirPath + uploadFileName);

		try {
			File dir = new File(application.getRealPath(dirPath));
			if (!dir.exists()) dir.mkdirs();
			File imageFile = new File(path);
			if(imageFile.exists()) {
				imageFile.delete();
			}
			OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
			try {
				out.write(uploadFile.getBytes(), 0, (int) fileSize);
				//TODO: クライアントへの返却メッセージ
//					JSONObject json = new JSONObject();
//					responseService.responseJsonText(json);
				return new File(path);
			} finally {
				out.close();
			}
		} catch (Exception e) {
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
	}

	/**
	 * JSON　を文字列に変換する。
	 *   StringUtil.jsonForCyclicalReferences(obj) が長大な JSON 文字列を生成する問題に対応するため、、
	 * 一覧表示用のデータに特徴的な特定の循環参照をあらかじめ除去する。
	 *   注意：このメソッドは、obj の内部を直接変更する。
	 * 
	 * @param obj
	 * @return
	 */
	public String jsonForCyclicalReferences(Object obj) {
		if (obj instanceof List<?>) {
			for (Object entry : (List<?>)obj) {
				PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(entry.getClass());
				for (PropertyDescriptor prop : props) {
					Method getter = prop.getReadMethod();
					if (getter != null) {
						try {
							Object value = getter.invoke(entry);
							if (value != null) {
								PropertyDescriptor[] props2 = BeanUtils.getPropertyDescriptors(value.getClass());
								for (PropertyDescriptor prop2 : props2) {
									Method getter2 = prop2.getReadMethod();
									if (getter2 != null) {
										Object value2 = getter2.invoke(value);
										if (value2 != null && value2 instanceof List<?>) {
											for (Object obj2 : (List<?>)value2) {
												if (obj2 == entry) {
													Method setter = prop2.getWriteMethod();
													if (setter != null) {
														setter.invoke(value, (Object)null);
														break;
													}
												}
											}
										}
									}
								}
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}

		return jp.ecom_plat.saigaitask.util.StringUtil.jsonForCyclicalReferences(obj);
	}
}
