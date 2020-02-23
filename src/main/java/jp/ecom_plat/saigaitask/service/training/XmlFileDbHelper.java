/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.training;

import static jp.ecom_plat.saigaitask.entity.Names.meteorequestInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.extension.jdbc.JdbcManager;

import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteoareainformationcityMaster;
import jp.ecom_plat.saigaitask.entity.db.MeteorainareaMaster;
import jp.ecom_plat.saigaitask.entity.db.MeteorequestInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteoriverMaster;
import jp.ecom_plat.saigaitask.entity.db.MeteoriverareaMaster;
import jp.ecom_plat.saigaitask.entity.db.Meteoseismicarea1Master;
import jp.ecom_plat.saigaitask.entity.db.MeteotsunamiareaMaster;
import jp.ecom_plat.saigaitask.entity.db.MeteotypeMaster;
import jp.ecom_plat.saigaitask.entity.db.MeteowarningcodeMaster;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteoareainformationcityMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteorainareaMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteorequestInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteoriverMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteoriverareaMasterService;
import jp.ecom_plat.saigaitask.service.db.Meteoseismicarea1MasterService;
import jp.ecom_plat.saigaitask.service.db.MeteotsunamiareaMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteotypeMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteowarningcodeMasterService;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * DB関連
 */
public class XmlFileDbHelper {


	/**
	 * ファイルの種別の取得
	 */
	public class MeteotypeMasterServiceExt extends MeteotypeMasterService {
		/**
		 * ファイルの種別の取得
		 */
		public MeteotypeMasterServiceExt()
		{
			init();
		}
		/**
		 * 初期化
		 */
		public void init() {
			/*//this.jdbcManager;
            SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            //ReflectionUtil.setValue(f[i], dbServiceObj,  (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME));
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);
		}

		/**
		 * ファイルの種別の取得
		 * @return
		 */
		public String getTypeByID(String iD, List<Map<String, String>> oType) {
			String strType = "";
			for(int i = 0; i < oType.size(); i++) {
				Map<String, String> mT =  oType.get(i);
				if(mT.get("key").equals(iD)) {
					strType = mT.get("type");
					return strType;
				}
			}
			return "";
		}

		/**
		 * ファイルの種別の取得
		 * @return
		 */
		public ArrayList<Map<String,String>> getComboboxDataCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();


			List<MeteotypeMaster> result = select().orderBy("disporder ASC").getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataCodeName2 size=" + result.size());
			/*
			<option value="1">地震速報</option>
			<option value="2">地震・震度に関する情報</option>
			<option value="3">津波警報・注意報</option>
			<option value="4">噴火警報・予報</option>
			<option value="5">気象警報・注意報</option>
			<option value="6">指定河川洪水予報</option>
			<option value="7">土砂災害警戒情報</option>
			<option value="8">記録的短時間大雨情報</option>
			<option value="9">竜巻注意報</option>

			2	震度速報	shindoSokuhou		2
			4	震源・震度に関する情報	shingenShindoJouhou		3
			5	指定河川洪水予報	shiteiKasenKouzuiYohou		5
			6	土砂災害警戒情報	dosyasaigaiKeikaiJouhou		6
			7	記録的短時間大雨情報	kirokutekiTanjikanOoameJouhou		7
			8	竜巻注意情報	tastumakiChuuiJouhou		8
			9	噴火警報・予報	funkakeihouYohou		9
			3	津波警報・注意報・予報a	tsunamikeihouChuihou		4
			1	気象特別警報・警報・注意報	kishoukeihouChuihou		1
			10	test	funkakeihouYohou		10

			*/
        	for(int i = 0; i < result.size(); i++) {
        		MeteotypeMaster info = result.get(i);
        		Map<String, String> map = new HashMap<String, String>();
        		/*
        		int iNewID = 0;
        		if(info.id == 2)iNewID = 1;
        		if(info.id == 4)iNewID = 2;
        		if(info.id == 5)iNewID = 6;
        		if(info.id == 6)iNewID = 7;
        		if(info.id == 7)iNewID = 8;
        		if(info.id == 8)iNewID = 9;
        		if(info.id == 9)iNewID = 4;
        		if(info.id == 3)iNewID = 3;
        		if(info.id == 1)iNewID = 5;
        		if(info.id >= 10)continue;
        		*/
        		map.put("key", ""+info.id);
        		map.put("type", ""+info.type);
        		map.put("value", info.name);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * ファイルの種別の取得
		 */
		public void dumpInfo() {
           try {
            	List<MeteotypeMaster> result = select().orderBy("disporder ASC").getResultList();

            	XmlFileEditHelper.outputLog("MeteorequestInfoServiceExt size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		MeteotypeMaster info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.name);
            		XmlFileEditHelper.outputLog("=" + info.note);
            		XmlFileEditHelper.outputLog("=" + info.id);
            		XmlFileEditHelper.outputLog("=" + info.disporder);
            	}

            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("MeteorequestInfoServiceExt ex=" + e.getMessage());
            }
		}

	}

	//////////////////////////////////////////////////////////////////////////
	//shindoSokuhou_edit.xsl
	//気象情報等取得情報
	/**
	 * 気象情報等取得情報
	 */
	public class MeteorequestInfoServiceExt extends MeteorequestInfoService {
		/**
		 * 気象情報等取得情報初期化
		 */
		MeteorequestInfoServiceExt()
		{
			init();
		}
		/**
		 * 気象情報等取得情報初期化
		 */
		public void init() {
			//this.jdbcManager;
           /* SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            //ReflectionUtil.setValue(f[i], dbServiceObj,  (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME));
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);

		}
		/**
		 * 気象情報等取得情報の表示
		 */
		public void dumpInfo() {
           try {
            	List<MeteorequestInfo> result = select()
    				.innerJoin(meteorequestInfo().meteotypeMaster())
    				.innerJoin(meteorequestInfo().localgovInfo())
    				.getResultList();

            	XmlFileEditHelper.outputLog("MeteorequestInfoServiceExt size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		MeteorequestInfo info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.meteoareaid);
            		XmlFileEditHelper.outputLog("=" + info.meteoareaid2);
            		XmlFileEditHelper.outputLog("=" + info.note);
            		XmlFileEditHelper.outputLog("=" + info.id);
            		XmlFileEditHelper.outputLog("=" + info.localgovinfoid);
            		XmlFileEditHelper.outputLog("=" + info.meteotypeid);
            		XmlFileEditHelper.outputLog("=" + info.alarm);
            		XmlFileEditHelper.outputLog("=" + info.localgovInfo.city);
            		XmlFileEditHelper.outputLog("=" + info.localgovInfo.citycode);
            		XmlFileEditHelper.outputLog("=" + info.localgovInfo.domain);
            		XmlFileEditHelper.outputLog("=" + info.localgovInfo.pref);
            		XmlFileEditHelper.outputLog("=" + info.localgovInfo.prefcode);
            		XmlFileEditHelper.outputLog("=" + info.localgovInfo.section);
            		XmlFileEditHelper.outputLog("=" + info.localgovInfo.systemname);
            	}

            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("MeteorequestInfoServiceExt ex=" + e.getMessage());
            }
		}

	}

	//	震度速報エリア情報（meteoseismicarea1_master）
	/**
	 * 震度速報エリア情報
	 */
	public class Meteoseismicarea1MasterServiceExt extends Meteoseismicarea1MasterService {
		/**
		 * 初期化
		 */
		Meteoseismicarea1MasterServiceExt()
		{
			init();
		}
		/**
		 * 初期化
		 */
		public void init() {
			//this.jdbcManager;
            /*SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            //ReflectionUtil.setValue(f[i], dbServiceObj,  (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME));
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);

		}
		/**
		 * 震度速報エリア情報
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataCodeName2() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			List<Meteoseismicarea1Master> result = jdbcManager.selectBySql(Meteoseismicarea1Master.class,
					"select code,name "
					+ "from meteoseismicarea1_master "
					+ "where code notnull and code != '' and "
					+ "name notnull and name != '' "
					+ "group by code,name order by code ASC").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataCodeName2 size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}
        	for(int i = 0; i < result.size(); i++) {
        		Meteoseismicarea1Master info = result.get(i);
        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.name);
        		map.put("value", info.code + ":" + info.name);
        		list.add(map);
        	}

			return list;
		}


		/**
		 * 震度速報エリア情報
		 */
		public void dumpInfo() {
           try {
            	List<Meteoseismicarea1Master> result = select()
    				.getResultList();

            	XmlFileEditHelper.outputLog(lang.__("Seismic intensity area info") + " size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		Meteoseismicarea1Master info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.area);
            		XmlFileEditHelper.outputLog("=" + info.code);
            		XmlFileEditHelper.outputLog("=" + info.name);
            		XmlFileEditHelper.outputLog("=" + info.id);
            	}

            	/*
				=--------------------------------------i=0
				=null
				=100
				=石狩地方北部
				=1
				=--------------------------------------i=1
				=null
				=100
				=石狩地方北部
				=2

            	 */

            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("Meteoseismicarea1MasterServiceExt ex=" + e.getMessage());
            }
		}
	}

	//	地方自治体情報（localgov_info）
	/**
	 * 地方自治体情報（localgov_info）
	 */
	public class LocalgovInfoServiceExt extends LocalgovInfoService {
		/**
		 * 初期化
		 */
		LocalgovInfoServiceExt()
		{
			init();
		}

		/**
		 * 初期化
		 */
		public void init() {
            /*SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);
 		}
		/**
		 * 県コードを取得
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataPrefCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();


			List<LocalgovInfo> result = jdbcManager.selectBySql(LocalgovInfo.class,
					"select prefcode,pref "
					+ "from localgov_info "
					+ "where prefcode notnull and prefcode != '' and "
					+ "pref notnull and pref != '' "
					+ "group by prefcode,pref order by prefcode ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataPrefCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}


        	for(int i = 0; i < result.size(); i++) {
        		LocalgovInfo info = result.get(i);
        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.prefcode);
        		map.put("name", info.pref);
        		map.put("value", info.prefcode + ":" + info.pref);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * Cityコードを取得する
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataCityCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();


			List<LocalgovInfo> result = jdbcManager.selectBySql(LocalgovInfo.class,
					"select citycode,city "
					+ "from localgov_info "
					+ "where citycode notnull and citycode != '' and "
					+ "city notnull and city != '' "
					+ "group by citycode,city order by citycode ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataCityCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}


        	for(int i = 0; i < result.size(); i++) {
        		LocalgovInfo info = result.get(i);
        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.citycode);
        		map.put("name", info.city);
        		map.put("value", info.citycode + ":" + info.city);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * 地方自治体情報を表示する
		 */
		public void dumpInfo() {
           try {
            	List<LocalgovInfo> result = select()
    				.getResultList();

            	XmlFileEditHelper.outputLog(lang.__("Local gov. info<!--2-->") + " size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		LocalgovInfo info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.city);
            		XmlFileEditHelper.outputLog("=" + info.citycode);
            		XmlFileEditHelper.outputLog("=" + info.pref);
            		XmlFileEditHelper.outputLog("=" + info.prefcode);
            	}
            	/*
            	=--------------------------------------i=2
    			=流山市
    			=220
    			=千葉県
    			=１２
    			*/
            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("LocalgovInfoServiceExt ex=" + e.getMessage());
            }
		}
	}
	//	気象・地震・火山情報／市町村等コードマスタ（meteoareainformationcity_master）
	public class MeteoareainformationcityMasterServiceExt extends MeteoareainformationcityMasterService {
		MeteoareainformationcityMasterServiceExt()
		{
			init();
		}
		public void init() {
            /*SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);
 		}


		/**
		 * 気象・地震・火山情報／市町村
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataNameseismicCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			/*
			地域コード（参照：防災情報XMLトリガーとエリア.docx）

			気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)
			参照先テーブル　変更
			絞り込み　nameseismicが空でない
			表示　「code：nameseismic」

			震源・震度に関する情報
			 * */
			List<MeteoareainformationcityMaster> result = jdbcManager.selectBySql(MeteoareainformationcityMaster.class,
					"select code,nameseismic "
					+ "from meteoareainformationcity_master "
					+ "where code notnull and code != '' and "
					+ "nameseismic notnull and nameseismic != '' "
					+ "group by code,nameseismic order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataNameseismicCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteoareainformationcityMaster info = result.get(i);
        		if(info.nameseismic == null)continue;
        		if(info.nameseismic.isEmpty())continue;

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.nameseismic);
        		map.put("value", info.code + ":" + info.nameseismic);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * 気象・地震・火山情報／市町村
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataKishoukeihouCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			/*
			気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)の「id:name」をセレクトボックスで表示する。
			参照先テーブル　ＯＫ
			絞り込み　warnflag=”TRUE”
			表示　「code：namewarn」

			気象特別警報・警報・注意報
			 * */
			List<MeteoareainformationcityMaster> result = jdbcManager.selectBySql(MeteoareainformationcityMaster.class,
					"select code,namewarn "
					+ "from meteoareainformationcity_master "
					+ "where code notnull and code != '' and "
					+ "namewarn notnull and namewarn != '' and "
					+ "warnflag = 't' "
					+ "group by code,namewarn order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataKishoukeihouCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteoareainformationcityMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.namewarn);
        		map.put("value", info.code + ":" + info.namewarn);
        		list.add(map);
        	}

			return list;
		}


		/**
		 * 気象・地震・火山情報/市町村等
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataDosyasaigaiCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			/*
			気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)の「id:name」をセレクトボックスで表示する。
			参照先テーブル　ＯＫ
			絞り込み　landslideflag=”TRUE”
			土砂災害警戒情報
			 * */
			List<MeteoareainformationcityMaster> result = jdbcManager.selectBySql(MeteoareainformationcityMaster.class,
					"select code,namewarn "
					+ "from meteoareainformationcity_master "
					+ "where code notnull and code != '' and "
					+ "namewarn notnull and namewarn != '' and "
					+ "landslideflag = 't' "
					+ "group by code,namewarn order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataDosyasaigaiCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteoareainformationcityMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.namewarn);
        		map.put("value", info.code + ":" + info.namewarn);
        		list.add(map);
        	}

			return list;
		}



		/**
		 * 気象・地震・火山情報/市町村等
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataVolcanoAreaCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			/*
			気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)
			参照先テーブル　変更
			絞り込み　namevolcanoが空で無い。
			表示　「code：namevolcano」
			噴火警報・予報
			* */
			List<MeteoareainformationcityMaster> result = jdbcManager.selectBySql(MeteoareainformationcityMaster.class,
					"select code,namevolcano "
					+ "from meteoareainformationcity_master "
					+ "where code notnull and code != '' and "
					+ "namevolcano notnull and namevolcano != ''  "
					+ "group by code,namevolcano order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataVolcanoAreaCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteoareainformationcityMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.namevolcano);
        		map.put("value", info.code + ":" + info.namevolcano);
        		list.add(map);
        	}

			return list;
		}


		/**
		 * 気象・地震・火山情報/市町村等
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataTatsumakiAreaCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			/*
			気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)の「id:name」をセレクトボックスで表示する。
			絞り込み　tatsumakiflag=”TRUE”
			竜巻注意情報
			* */
			List<MeteoareainformationcityMaster> result = jdbcManager.selectBySql(MeteoareainformationcityMaster.class,
					"select code,namewarn "
					+ "from meteoareainformationcity_master "
					+ "where code notnull and code != '' and "
					+ "namewarn notnull and namewarn != '' and "
					+ "tatsumakiflag = 't' "
					+ "group by code,namewarn order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataTatsumakiAreaCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteoareainformationcityMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.namewarn);
        		map.put("value", info.code + ":" + info.namewarn);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * 気象・地震・火山情報/市町村等
		 * **** 注意：この関数は指定河川洪水予報の旧関数です。***
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataRiverAreaCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			/*
			気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)
			絞り込み　riverflag=”TRUE”
			指定河川洪水予報
			* */
			List<MeteoareainformationcityMaster> result = jdbcManager.selectBySql(MeteoareainformationcityMaster.class,
					"select code,namewarn "
					+ "from meteoriver_master "
					+ "where code notnull and code != '' and "
					+ "namewarn notnull and namewarn != '' "
					+ "group by code,namewarn order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataTatsumakiAreaCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteoareainformationcityMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.namewarn);
        		map.put("value", info.code + ":" + info.namewarn);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * 気象・地震・火山情報／市町村
		 */
		public void dumpInfo() {
           try {
            	List<MeteoareainformationcityMaster> result = select()
    				.getResultList();

            	XmlFileEditHelper.outputLog(lang.__("weather quake volcano info/municipalities code master/landslide disaster") + " size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		MeteoareainformationcityMaster info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.code);
            		XmlFileEditHelper.outputLog("=" + info.line);
            		XmlFileEditHelper.outputLog("=" + info.name);
            		XmlFileEditHelper.outputLog("=" + info.nameseismic);
            		XmlFileEditHelper.outputLog("=" + info.namevolcano);
            		XmlFileEditHelper.outputLog("=" + info.namewarn);
            		XmlFileEditHelper.outputLog("=" + info.point);
            		XmlFileEditHelper.outputLog("=" + info.polygon);
            		XmlFileEditHelper.outputLog("=" + info.disporder);
            		XmlFileEditHelper.outputLog("=" + info.id);
            		XmlFileEditHelper.outputLog("=" + info.landslideflag);
            		XmlFileEditHelper.outputLog("=" + info.riverflag);
            		XmlFileEditHelper.outputLog("=" + info.tatsumakiflag);
            		XmlFileEditHelper.outputLog("=" + info.warnflag);
            	}

            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("LocalgovInfoServiceExt ex=" + e.getMessage());
            }
		}
	}

	//記録的短時間大雨情報
	//防災情報XMLトリガーとエリアmeteorainarea_master

	/**
	 * 記録的短時間大雨情報
	 */
	public class MeteorainareaMasterServiceExt extends MeteorainareaMasterService {
		/**
		 * 記録的短時間大雨情報
		 */
		MeteorainareaMasterServiceExt()
		{
			init();
		}
		/**
		 * 初期化
		 */
		public void init() {
           /* SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);
 		}
		/**
		 * 雨量エリアマスタ
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataRainAreaCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			/*
			雨量エリアマスタ(meteorainarea_master)

			* */
			List<MeteorainareaMaster> result = jdbcManager.selectBySql(MeteorainareaMaster.class,
					"select code,name "
					+ "from meteorainarea_master "
					+ "where code notnull and code != '' and "
					+ "name notnull and name != '' "
					+ "group by code,name order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataRainAreaCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteorainareaMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.name);
        		map.put("value", info.code + ":" + info.name);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * 記録的短時間大雨情報
		 */
		public void dumpInfo() {
           try {
            	List<MeteorainareaMaster> result = select()
    				.getResultList();

            	XmlFileEditHelper.outputLog(lang.__("Heavy rain emergency warning") + "  size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		MeteorainareaMaster info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.area);
            		XmlFileEditHelper.outputLog("=" + info.code);
            		XmlFileEditHelper.outputLog("=" + info.name);
            		XmlFileEditHelper.outputLog("=" + info.id);
            		XmlFileEditHelper.outputLog("=" + info.disporder);
            	}

            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("LocalgovInfoServiceExt ex=" + e.getMessage());
            }
		}
	}

	/**
	 * 津波警報注意報meteotsunamiarea_master
	 * dbtatsunamiareacode
	 */
	public class MeteotsunamiareaMasterServiceExt extends MeteotsunamiareaMasterService {
		/**
		 * 初期化
		 */
		MeteotsunamiareaMasterServiceExt()
		{
			init();
		}
		/**
		 * 初期化
		 */
		public void init() {
            /*SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);
 		}
		/**
		 * 津波警報注意報
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDaTatsunamiAreaCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			List<MeteotsunamiareaMaster> result = jdbcManager.selectBySql(MeteotsunamiareaMaster.class,
					"select code,name "
					+ "from meteotsunamiarea_master "
					+ "where code notnull and code != '' and "
					+ "name notnull and name != '' "
					+ "group by code,name order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDaTatsunamiAreaCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteotsunamiareaMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.name);
        		map.put("value", info.code + ":" + info.name);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * 津波警報注意報データを表示する
		 */
		public void dumpInfo() {
           try {
            	List<MeteotsunamiareaMaster> result = select()
    				.getResultList();

            	XmlFileEditHelper.outputLog(lang.__("Tsunami district info") + "  size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		MeteotsunamiareaMaster info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.code);
            		XmlFileEditHelper.outputLog("=" + info.line);
            		XmlFileEditHelper.outputLog("=" + info.name);
            		XmlFileEditHelper.outputLog("=" + info.id);
            		XmlFileEditHelper.outputLog("=" + info.disporder);
            	}

            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("LocalgovInfoServiceExt ex=" + e.getMessage());
            }
		}
	}
	/**
	 * 警報meteowarningcode_master
	 */
	public class MeteowarningcodeMasterServiceExt extends MeteowarningcodeMasterService {
		/**
		 * 初期化
		 */
		MeteowarningcodeMasterServiceExt()
		{
			init();
		}
		/**
		 * 初期化
		 */
		public void init() {
            /*SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);
 		}
		/**
		 * 警報meteowarningcode_master
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxWarningCodeName(int iFileType) {

			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			XmlFileEditHelper.outputLog("iFileType=" + iFileType);

			List<MeteowarningcodeMaster> result = jdbcManager.selectBySql(MeteowarningcodeMaster.class,
					"select code,name "
					+ "from meteowarningcode_master "
					+ "where code notnull and code != '' and "
					+ "name notnull and name != '' and "
					+ "valid notnull and valid = 'true' and "
					+ "meteotypemasterid = " + iFileType + " "
					+ "group by code,name order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxWarningCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteowarningcodeMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.name);
        		map.put("value", info.code + ":" + info.name);
        		list.add(map);
        	}
        	if(result.size() == 0) {
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "test");
	    		map.put("name", "test");
	    		map.put("value", "test:test");
	    		list.add(map);
        	}

			return list;
		}
		/**
		 * 警報データを表示する
		 */
		public void dumpInfo() {
           try {
        	   /*
				2	2	5+	震度5強		1	t
				3	4	5+	震度5強		1	t
				4	3	51	津波警報		1	t
				5	3	52	大津波警報		2	t
				6	5	21	はん濫注意情報		1	t
				7	5	22	はん濫注意情報(警戒情報解除)		2	t
				8	6	3	警戒		1	t
				9	7	1	記録的短時間大雨情報		1	t
				10	8	1	竜巻注意情報		1	t
				11	9	01	噴火警報		1	t
				12	9	02	火口周辺警報		2	t
				13	2	6-	震度6弱		2	t
				14	2	6+	震度6強		3	t
				15	2	7	震度7		4	t
				1	1	03	大雨警報		1	t

				震度                   				2   5+	震度5強      dbmaxquake
				気象警報注意報        				1   03	大雨警報     dbwarncode
				津波警報注意報コード   				3   51	津波警報     dbcategorykeihoucode
				土砂災害警報情報 警報コード
        	    * */
            	List<MeteowarningcodeMaster> result = select()
    				.getResultList();

            	XmlFileEditHelper.outputLog(lang.__("Tsunami district info") + "  size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		MeteowarningcodeMaster info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.code);
            		XmlFileEditHelper.outputLog("=" + info.name);
            		XmlFileEditHelper.outputLog("=" + info.note);
            		XmlFileEditHelper.outputLog("=" + info.disporder);
            		XmlFileEditHelper.outputLog("=" + info.id);
            		XmlFileEditHelper.outputLog("=" + info.meteotypemasterid);
            		XmlFileEditHelper.outputLog("=" + info.valid);
            	}

            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("LocalgovInfoServiceExt ex=" + e.getMessage());
            }
		}
	}
	/**
	 * 指定河川洪水予報
	 */
	public class MeteoriverMasterServiceExt extends MeteoriverMasterService {
		/**
		 * 指定河川洪水予報
		 */
		MeteoriverMasterServiceExt()
		{
			init();
		}
		/**
		 * 初期化
		 */
		public void init() {
            /*SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);
 		}
		/**
		 * 指定河川洪水予報マスタ
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataRiverAreaCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			/*
			指定河川洪水予報(meteoriver_master)
			meteoareainformationcity_masterからではなく、meteoriver_masterの
			code,nameから取得頂きますようお願い致します。
			なお、絞り込みは不要です。
			* */
			List<MeteoriverMaster> result = jdbcManager.selectBySql(MeteoriverMaster.class,
					"select code,name "
					+ "from meteoriver_master "
					+ "where code notnull and code != '' and "
					+ "name notnull and name != '' "
					+ "group by code,name order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataRainAreaCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteoriverMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.name);
        		map.put("value", info.code + ":" + info.name);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * 指定河川洪水予報
		 */
		public void dumpInfo() {
           try {
            	List<MeteoriverMaster> result = select()
    				.getResultList();

            	XmlFileEditHelper.outputLog(lang.__("Specified river inundation forecast") + "  size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		MeteoriverMaster info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.area);
            		XmlFileEditHelper.outputLog("=" + info.code);
            		XmlFileEditHelper.outputLog("=" + info.name);
            		XmlFileEditHelper.outputLog("=" + info.id);
            		XmlFileEditHelper.outputLog("=" + info.disporder);
            	}

            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("LocalgovInfoServiceExt ex=" + e.getMessage());
            }
		}
	}
	/**
	 * 河川流域マスタ
	 */
	public class MeteoriverareaMasterServiceExt extends MeteoriverareaMasterService {
		/**
		 * 河川流域マスタ
		 */
		MeteoriverareaMasterServiceExt()
		{
			init();
		}
		/**
		 * 初期化
		 */
		public void init() {
            /*SingletonS2ContainerFactory.init();
            S2Container container = SingletonS2ContainerFactory.getContainer();
            jdbcManager = (JdbcManager)container.getComponent(DB_JDBCMANAGER_CLASSNAME);*/
			jdbcManager = SpringContext.getApplicationContext().getBean(JdbcManager.class);
 		}
		/**
		 * 河川流域マスタ
		 * @return
		 */
		ArrayList<Map<String,String>> getComboboxDataRiverAreaCodeName() {
			ArrayList<Map<String,String>>  list = new ArrayList<Map<String,String>> ();

			/*
			雨量エリアマスタ(meteoriverarea_master)

			* */
			List<MeteoriverareaMaster> result = jdbcManager.selectBySql(MeteoriverareaMaster.class,
					"select code,name "
					+ "from meteoriverarea_master "
					+ "where code notnull and code != '' and "
					+ "name notnull and name != '' "
					+ "group by code,name order by code ASC ").
    				getResultList();

			XmlFileEditHelper.outputLog("getComboboxDataRainAreaCodeName size=" + result.size());

			{
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("key", "");
	    		map.put("name", "");
	    		map.put("value", "");
	    		list.add(map);
			}

        	for(int i = 0; i < result.size(); i++) {
        		MeteoriverareaMaster info = result.get(i);

        		Map<String, String> map = new HashMap<String, String>();
        		map.put("key", info.code);
        		map.put("name", info.name);
        		map.put("value", info.code + ":" + info.name);
        		list.add(map);
        	}

			return list;
		}
		/**
		 * 記録的短時間大雨情報
		 */
		public void dumpInfo() {
           try {
            	List<MeteoriverareaMaster> result = select()
    				.getResultList();

            	XmlFileEditHelper.outputLog(lang.__("Heavy rain emergency warning") + "  size=" + result.size());
            	for(int i = 0; i < result.size() && i < 10; i++) {
            		MeteoriverareaMaster info = result.get(i);
            		XmlFileEditHelper.outputLog("=--------------------------------------i="+i);
            		XmlFileEditHelper.outputLog("=" + info.area);
            		XmlFileEditHelper.outputLog("=" + info.code);
            		XmlFileEditHelper.outputLog("=" + info.name);
            		XmlFileEditHelper.outputLog("=" + info.id);
            		XmlFileEditHelper.outputLog("=" + info.disporder);
            	}

            } catch (Exception e) {
            	XmlFileEditHelper.outputLog("LocalgovInfoServiceExt ex=" + e.getMessage());
            }
		}
	}


	public void dumpInfo() {
		{
			//気象情報等取得情報
			MeteorequestInfoServiceExt ext = new MeteorequestInfoServiceExt();
			ext.init();
			ext.dumpInfo();
		}

		{
			//震度速報エリア情報
			Meteoseismicarea1MasterServiceExt ext2 = new Meteoseismicarea1MasterServiceExt();
			ext2.init();
			ext2.dumpInfo();
		}
		{
			//地方自治体情報
			LocalgovInfoServiceExt ext = new LocalgovInfoServiceExt();
			ext.init();
			ext.dumpInfo();
		}
		{
			//気象・地震・火山情報／市町村等コードマスタ
			MeteoareainformationcityMasterServiceExt ext = new MeteoareainformationcityMasterServiceExt();
			ext.init();
			ext.dumpInfo();
		}
		{
			//土砂災害警報情報
			//<!-- 種別Code-->
			//<xsl:value-of select="jmx_ib:Kind/jmx_ib:Code"/>
			//0 なし
			//1 解除
			//3 警戒

		}
		{
			//噴火警報予報   funkakeihouYohou_template.xml
			//funkakeihouYohou_edit.xsl
			//VolcanicWarning
			/*
			01	噴火警報
			02	火口周辺警報
			03	噴火警報（周辺海域）
			04	噴火予報：警報解除
			05	噴火予報
			06	降灰予報（定時）
			07	降灰予報（速報）
			08	降灰予報（詳細）
			11	レベル１（平常）
			12	レベル２（火口周辺規制）
			13	レベル３（入山規制）
			14	レベル４（避難準備）
			15	レベル５（避難）
			21	平常
			22	火口周辺危険
			23	入山危険
			24	山麓厳重警戒
			25	居住地域厳重警戒
			31	海上警報（噴火警報）
			32	海上警報（噴火警報解除）
			33	海上予報（噴火予報）
			35	平常（海底火山）
			36	周辺海域警戒
			41 	噴火警報：避難等
			42	噴火警報：入山規制等
			43	火口周辺警報：入山規制等
			44	噴火警報（周辺海域）：周辺海域警戒
			45	平常
			46	噴火警報：当該居住地域厳重警戒
			47	噴火警報：当該山麓厳重警戒
			48	噴火警報：火口周辺警戒
			49	火口周辺警報：火口周辺警戒
			51	爆発
			52	噴火
			53	噴火開始
			54	連続噴火継続
			55	連続噴火停止
			56	噴火多発
			61	爆発したもよう
			62	噴火したもよう
			63	噴火開始したもよう
			64	連続噴火が継続しているもよう
			65	連続噴火は停止したもよう
			70	降灰
			71	少量の降灰
			72	やや多量の降灰
			73	多量の降灰
			75	小さな噴石の落下
			91	不明
			99	その他の現象
			*/
		}
		{
			//記録的短時間大雨情報   kirokutekiTanjikanOoameJouhou_template.xml
			/*
			0 なし
			1 記録的短時間大雨情報
			*/
			//防災情報XMLトリガーとエリア
		}
		{

			MeteoriverareaMasterServiceExt ext = new MeteoriverareaMasterServiceExt();
			ext.init();
			ext.dumpInfo();
		}
		{
			//指定河川洪水予報   shiteiKasenKouzuiYohou_template.xml
			//気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)
			//絞り込み　riverflag=”TRUE”
			/*
			はん濫注意情報解除 10 洪水注意報解除
			はん濫注意情報     20 洪水注意報（発表）
			はん濫注意情報     21 洪水注意報
			はん濫注意情報（警戒情報解除） 22 洪水注意報（警報解除）
			はん濫警戒情報 30 洪水警報（発表）
			はん濫警戒情報 31 洪水警報
			はん濫危険情報 40 洪水警報（発表）
			はん濫危険情報 41 洪水警報
			はん濫発生情報 50 洪水警報（発表）
			はん濫発生情報 51 洪水警報
			はん濫発生情報（はん濫水の予報） 52 洪水警報（発表）
			はん濫発生情報（はん濫水の予報） 53 洪水警報
			*/
		}
		{
			//竜巻注意情報   tatsumakiChuuiJouhou_template.xml
			/*
			0 なし
			1 竜巻注意情報
			*/
			/*
			気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)の「id:name」をセレクトボックスで表示する。
			絞り込み　tatsumakiflag=”TRUE”

			 */

		}
		{
			//津波警報注意報   tsunamikeihouChuihou_template.xml
			/*
			00	津波なし
			50	警報解除
			51	津波警報
			52	大津波警報
			53	大津波警報：発表
			60	津波注意報解除
			62	津波注意報
			71	津波予報（若干の海面変動）
			72	津波予報（若干の海面変動）
			73	津波予報（若干の海面変動）

			 */
			/*
			津波エリア情報テーブル(meteotsunamiarea_master)
			参照先テーブル　なし
			*/

			MeteotsunamiareaMasterServiceExt ext = new MeteotsunamiareaMasterServiceExt();
			ext.init();
			ext.dumpInfo();
		}

		{
			//気象警報注意報   kishoukeihouChuihou_template.xml
			/*
			気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)の「id:name」をセレクトボックスで表示する。
			絞り込み　warnflag=”TRUE”
			 */

			/*
			00	解除
			02	暴風雪警報
			03	大雨警報
			04	洪水警報
			05	暴風警報
			06	大雪警報
			07	波浪警報
			08	高潮警報
			10	大雨注意報
			12	大雪注意報
			13	風雪注意報
			14	雷注意報
			15	強風注意報
			16	波浪注意報
			17	融雪注意報
			18	洪水注意報
			19	高潮注意報
			20	濃霧注意報
			21	乾燥注意報
			22	なだれ注意報
			23	低温注意報
			24	霜注意報
			25	着氷注意報
			26	着雪注意報
			27	その他の注意報
			32	暴風雪特別警報（Control/Title="気象警報・注意報"の場合には出現しない。）
			33	大雨特別警報（Control/Title="気象警報・注意報"の場合には出現しない。）
			35	暴風特別警報（Control/Title="気象警報・注意報"の場合には出現しない。）
			36	大雪特別警報（Control/Title="気象警報・注意報"の場合には出現しない。）
			37	波浪特別警報（Control/Title="気象警報・注意報"の場合には出現しない。）
			38	高潮特別警報（Control/Title="気象警報・注意報"の場合には出現しない。）

			 */
		}

		{
			//震度速報   shindoSokuhou_template.xml
			//震度速報エリア情報テーブル(meteoseismicarea1_master)
			//MaxInt【最大震度】（値：“1”/“2”/“3”/“4”/“5-”/“5+”/“6-”/“6+”/“7”）地震火山関連XML 電文解説資料より

		}

		{
			//震源・震度に関する情報   shingenShindoJouhou_template.xml
			//MaxInt【最大震度】（値：“1”/“2”/“3”/“4”/“5-”/“5+”/“6-”/“6+”/“7”）
			/*
			気象・地震・火山情報/市町村等コードマスタ(meteoareainformationcity_master)
			参照先テーブル　変更
			絞り込み　nameseismicが空でない
			*/
		}
	}
}
