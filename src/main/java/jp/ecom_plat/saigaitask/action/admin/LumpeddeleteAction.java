/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;


import static jp.ecom_plat.saigaitask.entity.Names.maplayerInfo;
import static jp.ecom_plat.saigaitask.entity.Names.maplayerattrInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.dto.admin.LumpeddeleteDto;
import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerattrInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablerowstyleInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames;
import jp.ecom_plat.saigaitask.form.admin.LumpeddeleteForm;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MaplayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MaplayerattrInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablerowstyleInfoService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.SpringContext;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/lumpeddelete")
public class LumpeddeleteAction extends AbstractAction {

	public LumpeddeleteForm lumpeddeleteForm;

	/** 画面表示内容オブジェクト*/
	public LumpeddeleteDto lumpeddeleteDto;

	/** 自治体切り替えSELECTオプション */
	public Map<Long, String> localgovSelectOptions;

	/** 災害種別マスタサービス */
	@Resource
	protected DisasterMasterService disasterMasterService;

	/** 班情報サービス */
	@Resource GroupInfoService groupInfoService;

	/** メニュー情報サービス */
	@Resource
	protected MenuInfoService menuInfoService;

	/**  地図レイヤ情報サービス */
	@Resource
	protected MaplayerInfoService maplayerInfoService;

	/**  地図レイヤ属性情報情報サービス */
	@Resource
	protected MaplayerattrInfoService maplayerattrInfoService;

	/**  メニューテーブル情報サービス */
	@Resource
	protected MenutableInfoService menutableInfoService;

	/**  テーブルリスト項目情報サービス */
	@Resource
	protected TablelistcolumnInfoService tablelistcolumnInfoService;

	/** 属性行スタイル情報サービス **/
	@Resource
	protected TablerowstyleInfoService tablerowstyleInfoService;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("lumpeddeleteDto", lumpeddeleteDto);
		model.put("localgovSelectOptions", localgovSelectOptions);
	}

	/**
	 * トップ画面
	 * @return
	 */
	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model,
			@ModelAttribute LumpeddeleteForm lumpeddeleteForm, BindingResult bindingResult) {

		// 初期化
		initAction();

		// 表示内容の取得
		content(model, lumpeddeleteForm, bindingResult);

		setupModel(model);
		return "/admin/lumpeddelete/index";
	}

	/**
	 * ページ表示内容作成
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value="/content")
	public String content(Map<String,Object>model,
			@ModelAttribute LumpeddeleteForm lumpeddeleteForm, BindingResult bindingResult) {
		this.lumpeddeleteForm = lumpeddeleteForm;
		lumpeddeleteDto = new LumpeddeleteDto();
		boolean isAdmin = false;

		if(loginDataDto.getLocalgovinfoid()==Constants.ADMIN_LOCALGOVINFOID) {
			// 自治体セレクトボックスの作成
			createLocalgovSelectOptions();
		}

		// 災害種別マスタの取得
		List<DisasterMaster>  disasterMasters = disasterMasterService.findAll();
		Map<Integer,String> disasterMasterMap = new HashMap<Integer,String>();
		for(DisasterMaster disasterMaster : disasterMasters){
			disasterMasterMap.put(disasterMaster.id, disasterMaster.name);
		}

		// 自治体情報の取得
		Long localgovinfoid = loginDataDto.getLocalgovinfoid();
		List<LocalgovInfo> currentLocalgovInfos = new ArrayList<LocalgovInfo>();
		if(localgovinfoid != Constants.ADMIN_LOCALGOVINFOID){
			currentLocalgovInfos.add(loginDataDto.getLocalgovInfo());
			lumpeddeleteDto.viewLocalgovInfoLabel = Boolean.FALSE;
			lumpeddeleteDto.currentLocalgovinfoid = localgovinfoid.toString();

			if(loginDataDto.isAdmin()){
				isAdmin = true;
			}else{
				isAdmin = false;
			}
		}else{
			long localgovinfoId;
			// システム管理者の場合
			if(lumpeddeleteForm.selectLocalgov != null && lumpeddeleteForm.selectLocalgov.length() > 0){
				localgovinfoId = Long.parseLong(lumpeddeleteForm.selectLocalgov);
				lumpeddeleteDto.currentLocalgovinfoid = lumpeddeleteForm.selectLocalgov;
			}else{
				// セレクトボックス先頭の自治体を取得
				Long topLocalgovinfoid = 0L;
				for(Map.Entry<Long, String> e : localgovSelectOptions.entrySet()){
					topLocalgovinfoid = e.getKey();
					break;
				}
				localgovinfoId = topLocalgovinfoid;
				lumpeddeleteDto.currentLocalgovinfoid = topLocalgovinfoid.toString();
			}
			currentLocalgovInfos.add(localgovInfoService.findById(localgovinfoId));
			lumpeddeleteDto.viewLocalgovInfoLabel = Boolean.TRUE;
			isAdmin = true;
		}

		for(LocalgovInfo localgovInfo : currentLocalgovInfos){
			String pref = localgovInfo.pref;
			String city = localgovInfo.city;
			String section = localgovInfo.section;
			if(pref == null){
				pref = "";
			}
			if(city == null){
				city = "";
			}
			if(section == null){
				section = "";
			}
			lumpeddeleteDto.localgovInfos.put(localgovInfo.id, localgovInfo.id+ Constants.COMMON_CONCATIDNAME  + pref + city + section);

			// 自治体のメニュー情報を取得し、上位のテーブルに遡る
			List<MenuInfo> menuInfoList = menuInfoService.findByLocalgovinfoid(localgovInfo.id);
			Map<Long,Integer> localgovInfoMenuloginInfosMenuCountMap = new HashMap<Long,Integer>();
			Map<Long,Integer> localgovInfoMenuprocessInfosMenuCountMap = new HashMap<Long,Integer>();
			Map<Long,Integer> localgovInfoMenutaskInfosMenuCountMap = new HashMap<Long,Integer>();

			Map<Long,Map<Long,MenuloginInfo>> menuloginInfos = new LinkedHashMap<Long,Map<Long,MenuloginInfo>>();
			Map<Long,Map<Long,MenuprocessInfo>> menuprocessInfos = new LinkedHashMap<Long,Map<Long,MenuprocessInfo>>();
			Map<Long,Map<Long,MenutaskInfo>> menutaskInfos = new LinkedHashMap<Long,Map<Long,MenutaskInfo>>();
			Map<Long,Map<Long,MenutaskmenuInfo>> menutaskmenuInfos = new LinkedHashMap<Long,Map<Long,MenutaskmenuInfo>>();

			// 先にテーブルリスト項目情報と地図レイヤ属性情報を取得する
			List<TablelistcolumnInfo> tablelistcolumnInfoList = tablelistcolumnInfoService.findByLocalgovinfoid(localgovInfo.id);
			List<MaplayerattrInfo> maplayerattrInfoList = maplayerattrInfoService.findByLocalgovinfoid(localgovInfo.id);


			if(menuInfoList != null){
				for(MenuInfo menuInfo : menuInfoList){
					if(menuInfo.menutaskmenuInfoList == null){
						continue;
					}
					for(MenutaskmenuInfo menutaskmenuInfo: menuInfo.menutaskmenuInfoList){
						MenutaskInfo menutaskInfo = menutaskmenuInfo.menutaskInfo;
						MenuprocessInfo menuprocessInfo = menutaskInfo.menuprocessInfo;
						MenuloginInfo menuloginInfo = menuprocessInfo.menuloginInfo;

						// 管理者権限ではないユーザの場合、自グループの情報のみ削除対象とする
						if(! isAdmin){
							if(menuloginInfo.groupid != loginDataDto.getGroupid()){
								continue;
							}
						}

						// メニュー情報以下にテーブルリストカラム情報と地図レイヤ属性情報リストのどちらも
						// 存在しないレコードはスキップする
						boolean skipData = true;
						for(TablelistcolumnInfo tablelistcolumnInfo : tablelistcolumnInfoList){
							if(tablelistcolumnInfo.menutableInfo.menuinfoid != null && tablelistcolumnInfo.menutableInfo.menuinfoid.equals(menuInfo.id)){
								skipData = false;
								break;
							}
						}
						if(skipData){
							for(MaplayerattrInfo maplayerattrInfo : maplayerattrInfoList){
								if(maplayerattrInfo.maplayerInfo.menuinfoid != null && maplayerattrInfo.maplayerInfo.menuinfoid.equals(menuInfo.id)){
									skipData = false;
									break;
								}
							}
						}
						if(skipData){
							continue;
						}

						// 取得したエンティティを、各階層毎に保存
						// メニュー設定情報の作成
						Map<Long,MenuloginInfo> menuloginInfoValue = menuloginInfos.get(localgovInfo.id);
						if(menuloginInfoValue == null){
							menuloginInfoValue = new HashMap<Long,MenuloginInfo>();
							menuloginInfoValue.put(menuloginInfo.id, menuloginInfo);
							localgovInfoMenuloginInfosMenuCountMap.put(menuloginInfo.id, 1);
						}else{
							menuloginInfoValue.put(menuloginInfo.id, menuloginInfo);
							Integer menuInfoCountByMenuloginInfo = localgovInfoMenuloginInfosMenuCountMap.get(menuloginInfo.id);
							if(menuInfoCountByMenuloginInfo == null){
								menuInfoCountByMenuloginInfo = 1;
							}else{
								menuInfoCountByMenuloginInfo++;
							}
							localgovInfoMenuloginInfosMenuCountMap.put(menuloginInfo.id,menuInfoCountByMenuloginInfo);
						}
						menuloginInfos.put(localgovInfo.id,menuloginInfoValue);


						// メニュープロセス情報の作成
						Map<Long,MenuprocessInfo> menuprocessInfoValue = menuprocessInfos.get(menuloginInfo.id);
						if(menuprocessInfoValue == null){
							menuprocessInfoValue = new HashMap<Long,MenuprocessInfo>();
							menuprocessInfoValue.put(menuprocessInfo.id, menuprocessInfo);
							localgovInfoMenuprocessInfosMenuCountMap.put(menuprocessInfo.id,1);
						}else{
							menuprocessInfoValue.put(menuprocessInfo.id, menuprocessInfo);
							Integer menuInfoCountByMenuprocessInfo = localgovInfoMenuprocessInfosMenuCountMap.get(menuprocessInfo.id);
							if(menuInfoCountByMenuprocessInfo == null){
								menuInfoCountByMenuprocessInfo = 1;
							}else{
								menuInfoCountByMenuprocessInfo++;
							}
							localgovInfoMenuprocessInfosMenuCountMap.put(menuprocessInfo.id,menuInfoCountByMenuprocessInfo);
						}
						menuprocessInfos.put(menuloginInfo.id,menuprocessInfoValue);
						/*
						System.out.println(menuprocessInfo.id+":"+menuprocessInfo.name+"["+localgovInfoMenuprocessInfosMenuCountMap.get(menuprocessInfo.id)+"]"
						+" "+menutaskInfo.id+":"+menutaskInfo.name
						+" "+menuInfo.id+":"+menuInfo.name);
						*/

						// メニュータスク情報の作成
						Map<Long,MenutaskInfo> menutaskInfoValue = menutaskInfos.get(menuprocessInfo.id);
						if(menutaskInfoValue == null){
							menutaskInfoValue = new HashMap<Long,MenutaskInfo>();
							menutaskInfoValue.put(menutaskInfo.id, menutaskInfo);
							localgovInfoMenutaskInfosMenuCountMap.put(menutaskInfo.id,1);
						}else{
							menutaskInfoValue.put(menutaskInfo.id, menutaskInfo);
							Integer menuInfoCountByMenutaskInfo = localgovInfoMenutaskInfosMenuCountMap.get(menutaskInfo.id);
							if(menuInfoCountByMenutaskInfo == null){
								menuInfoCountByMenutaskInfo = 1;
							}else{
								menuInfoCountByMenutaskInfo++;
							}
							localgovInfoMenutaskInfosMenuCountMap.put(menutaskInfo.id,menuInfoCountByMenutaskInfo);
						}
						menutaskInfos.put(menuprocessInfo.id, menutaskInfoValue);

						// タスクメニュー情報の作成
						Map<Long,MenutaskmenuInfo> menutaskmenuInfoValue = menutaskmenuInfos.get(menutaskInfo.id);
						if(menutaskmenuInfoValue == null){
							menutaskmenuInfoValue = new HashMap<Long,MenutaskmenuInfo>();
							menutaskmenuInfoValue.put(menutaskmenuInfo.id, menutaskmenuInfo);
						}else{
							menutaskmenuInfoValue.put(menutaskmenuInfo.id, menutaskmenuInfo);
						}
						menutaskmenuInfos.put(menutaskInfo.id, menutaskmenuInfoValue);
						lumpeddeleteDto.viewDataCount++;
					}
				}
				// メニュー設定情報から見たタスクメニュー情報の合計数を保存
				lumpeddeleteDto.localgovInfoMenuloginInfosMenuCount.put(localgovInfo.id, localgovInfoMenuloginInfosMenuCountMap);
				// メニュープロセス情報から見たタスクメニュー情報の合計数を保存
				lumpeddeleteDto.localgovInfoMenuprocessInfosMenuCount.put(localgovInfo.id, localgovInfoMenuprocessInfosMenuCountMap);
				// メニュータスク情報から見たタスクメニュー情報の合計数を保存
				lumpeddeleteDto.localgovInfoMenutaskInfosMenuCount.put(localgovInfo.id, localgovInfoMenutaskInfosMenuCountMap);


				// ソートし、画面表示に必要な情報のみDTOに格納
				// メニュー設定情報
				ArrayList<Map.Entry<Long, Map<Long,MenuloginInfo>>> menuloginInfosList = new ArrayList<Map.Entry<Long, Map<Long,MenuloginInfo>>>(menuloginInfos.entrySet());
				// 全体を自治体IDでソート
				Collections.sort(menuloginInfosList, new Comparator<Map.Entry<Long, Map<Long,MenuloginInfo>>>() {
		            @Override
		            public int compare(Map.Entry<Long, Map<Long,MenuloginInfo>> entry1, Map.Entry<Long, Map<Long,MenuloginInfo>> entry2) {
		                return ((Long)entry1.getKey()).compareTo((Long)entry2.getKey());
		            }
		        });
				// 各メニュー設定情報を災害種別IDでソートし、画面表示に必要な情報のみDTOに格納
		        for(Map.Entry<Long, Map<Long,MenuloginInfo>> menuloginInfosListEntry : menuloginInfosList){
		        	ArrayList<Map.Entry<Long, MenuloginInfo>> menuloginInfosListEntryList = new ArrayList<Map.Entry<Long, MenuloginInfo>>(menuloginInfosListEntry.getValue().entrySet());
			        Collections.sort(menuloginInfosListEntryList, new Comparator<Map.Entry<Long, MenuloginInfo>>() {
			            @Override
			            public int compare(Map.Entry<Long, MenuloginInfo> entry1, Map.Entry<Long, MenuloginInfo> entry2) {
			                return ((Integer)entry1.getValue().disasterid).compareTo((Integer)entry2.getValue().disasterid);
			            }
			        });
			        Map<Long, String> menuloginInfoPrintValue = new LinkedHashMap<Long, String>();
			        for(Map.Entry<Long, MenuloginInfo> menuloginInfosListEntryListValue : menuloginInfosListEntryList){
						// 災害種別
						String disasterName = disasterMasterMap.get(menuloginInfosListEntryListValue.getValue().disasterid);
						if(disasterName == null){
							disasterName = "";
						}
						disasterName = menuloginInfosListEntryListValue.getValue().disasterid + Constants.COMMON_CONCATIDNAME + disasterName;
						// 班情報
						String  groupInfoName = menuloginInfosListEntryListValue.getValue().groupInfo.name;
						if(groupInfoName == null){
							groupInfoName = "";
						}
						groupInfoName = menuloginInfosListEntryListValue.getValue().groupid + Constants.COMMON_CONCATIDNAME + groupInfoName;
						String printStr = disasterName + "<BR>" + groupInfoName;
						menuloginInfoPrintValue.put(menuloginInfosListEntryListValue.getKey(), printStr);
			        }
			        lumpeddeleteDto.menuloginInfos.put(menuloginInfosListEntry.getKey(), menuloginInfoPrintValue);
		        }

				// メニュープロセス情報
				ArrayList<Map.Entry<Long, Map<Long,MenuprocessInfo>>> menuprocessInfosList = new ArrayList<Map.Entry<Long, Map<Long,MenuprocessInfo>>>(menuprocessInfos.entrySet());
				// 全体をメニュー設定情報IDでソート
				Collections.sort(menuprocessInfosList, new Comparator<Map.Entry<Long, Map<Long,MenuprocessInfo>>>() {
		            @Override
		            public int compare(Map.Entry<Long, Map<Long,MenuprocessInfo>> entry1, Map.Entry<Long, Map<Long,MenuprocessInfo>> entry2) {
		                return ((Long)entry1.getKey()).compareTo((Long)entry2.getKey());
		            }
		        });
				//各メニュープロセス情報を表示順でソートし、画面表示に必要な情報のみDTOに格納
		        for(Map.Entry<Long, Map<Long,MenuprocessInfo>> menuprocessInfosListEntry : menuprocessInfosList){
		        	ArrayList<Map.Entry<Long, MenuprocessInfo>> menuprocessInfosListEntryList = new ArrayList<Map.Entry<Long, MenuprocessInfo>>(menuprocessInfosListEntry.getValue().entrySet());
			        Collections.sort(menuprocessInfosListEntryList, new Comparator<Map.Entry<Long, MenuprocessInfo>>() {
			            @Override
			            public int compare(Map.Entry<Long, MenuprocessInfo> entry1, Map.Entry<Long, MenuprocessInfo> entry2) {
			                return ((Integer)entry1.getValue().disporder).compareTo((Integer)entry2.getValue().disporder);
			            }
			        });
			        Map<Long, String> menuprocessInfoPrintValue = new LinkedHashMap<Long, String>();
			        for(Map.Entry<Long, MenuprocessInfo> menuprocessInfosListEntryListValue : menuprocessInfosListEntryList){
						String menuprocessInfoName = menuprocessInfosListEntryListValue.getValue().name;
						if(menuprocessInfoName == null){
							menuprocessInfoName = "";
						}
						menuprocessInfoName = menuprocessInfosListEntryListValue.getKey() + Constants.COMMON_CONCATIDNAME + menuprocessInfoName;
						menuprocessInfoPrintValue.put(menuprocessInfosListEntryListValue.getKey(), menuprocessInfoName);
			        }
			        lumpeddeleteDto.menuprocessInfos.put(menuprocessInfosListEntry.getKey(), menuprocessInfoPrintValue);
		        }

				// メニュータスク情報
				ArrayList<Map.Entry<Long, Map<Long,MenutaskInfo>>> menutaskInfosList = new ArrayList<Map.Entry<Long, Map<Long,MenutaskInfo>>>(menutaskInfos.entrySet());
				// 全体をメニュープロセス情報IDでソート
		        Collections.sort(menutaskInfosList, new Comparator<Map.Entry<Long, Map<Long,MenutaskInfo>>>() {
		            @Override
		            public int compare(Map.Entry<Long, Map<Long,MenutaskInfo>> entry1, Map.Entry<Long, Map<Long,MenutaskInfo>> entry2) {
		                return ((Long)entry1.getKey()).compareTo((Long)entry2.getKey());
		            }
		        });
				//各メニュータスク情報を表示順でソートし、画面表示に必要な情報のみDTOに格納
		        for(Map.Entry<Long, Map<Long,MenutaskInfo>> menutaskInfosListEntry : menutaskInfosList){
		        	ArrayList<Map.Entry<Long, MenutaskInfo>> menutaskInfosListEntryList = new ArrayList<Map.Entry<Long, MenutaskInfo>>(menutaskInfosListEntry.getValue().entrySet());
			        Collections.sort(menutaskInfosListEntryList, new Comparator<Map.Entry<Long, MenutaskInfo>>() {
			            @Override
			            public int compare(Map.Entry<Long, MenutaskInfo> entry1, Map.Entry<Long, MenutaskInfo> entry2) {
			                return ((Integer)entry1.getValue().disporder).compareTo((Integer)entry2.getValue().disporder);
			            }
			        });
			        Map<Long, String> menutaskInfoPrintValue = new LinkedHashMap<Long, String>();
			        for(Map.Entry<Long, MenutaskInfo> menutaskInfosListEntryListValue : menutaskInfosListEntryList){
						String menutaskInfoName = menutaskInfosListEntryListValue.getValue().name;
						if(menutaskInfoName == null){
							menutaskInfoName = "";
						}
						menutaskInfoName = menutaskInfosListEntryListValue.getKey() + Constants.COMMON_CONCATIDNAME + menutaskInfoName;
						menutaskInfoPrintValue.put(menutaskInfosListEntryListValue.getKey(), menutaskInfoName);
			        }
			        lumpeddeleteDto.menutaskInfos.put(menutaskInfosListEntry.getKey(), menutaskInfoPrintValue);
		        }

				// タスクメニュー情報
				ArrayList<Map.Entry<Long, Map<Long,MenutaskmenuInfo>>> menutaskmenuInfosList = new ArrayList<Map.Entry<Long, Map<Long,MenutaskmenuInfo>>>(menutaskmenuInfos.entrySet());
				// 全体をメニュータスク情報IDでソート
		        Collections.sort(menutaskmenuInfosList, new Comparator<Map.Entry<Long, Map<Long,MenutaskmenuInfo>>>() {
		            @Override
		            public int compare(Map.Entry<Long, Map<Long,MenutaskmenuInfo>> entry1, Map.Entry<Long, Map<Long,MenutaskmenuInfo>> entry2) {
		                return ((Long)entry1.getKey()).compareTo((Long)entry2.getKey());
		            }
		        });
				//各タスクメニュー情報を表示順でソートし、画面表示に必要な情報のみDTOに格納
		        for(Map.Entry<Long, Map<Long,MenutaskmenuInfo>> menutaskmenuInfosListEntry : menutaskmenuInfosList){
		        	ArrayList<Map.Entry<Long, MenutaskmenuInfo>> menutaskmenuInfosListEntryList = new ArrayList<Map.Entry<Long, MenutaskmenuInfo>>(menutaskmenuInfosListEntry.getValue().entrySet());
			        Collections.sort(menutaskmenuInfosListEntryList, new Comparator<Map.Entry<Long, MenutaskmenuInfo>>() {
			            @Override
			            public int compare(Map.Entry<Long, MenutaskmenuInfo> entry1, Map.Entry<Long, MenutaskmenuInfo> entry2) {
			                return ((Integer)entry1.getValue().disporder).compareTo((Integer)entry2.getValue().disporder);
			            }
			        });
			        Map<Long, String> menutaskmenuInfoPrintValue = new LinkedHashMap<Long, String>();
			        for(Map.Entry<Long, MenutaskmenuInfo> menutaskmenuInfosListEntryListValue : menutaskmenuInfosListEntryList){
						String menutaskmenuInfoName = menutaskmenuInfosListEntryListValue.getValue().menuInfo.name;
						if(menutaskmenuInfoName == null){
							menutaskmenuInfoName = "";
						}
						menutaskmenuInfoName = menutaskmenuInfosListEntryListValue.getValue().menuInfo.id + Constants.COMMON_CONCATIDNAME  + menutaskmenuInfoName;
						menutaskmenuInfoPrintValue.put(menutaskmenuInfosListEntryListValue.getValue().menuInfo.id, menutaskmenuInfoName);
			        }
			        lumpeddeleteDto.menutaskmenuInfos.put(menutaskmenuInfosListEntry.getKey(), menutaskmenuInfoPrintValue);
		        }
			}
		}
		setupModel(model);
		return "/admin/lumpeddelete/content";
	}

	/**
	 * 一括削除処理実行
	 * @return
	 */
	@RequestMapping(value="/doLumpeddelete", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String doLumpeddelete(Map<String,Object>model,
			@ModelAttribute LumpeddeleteForm lumpeddeleteForm, BindingResult bindingResult){

		// 自治体情報の取得
		Long localgovinfoid = loginDataDto.getLocalgovinfoid();
		if(localgovinfoid == Constants.ADMIN_LOCALGOVINFOID){
			lumpeddeleteForm.selectLocalgov = lumpeddeleteForm.currentLocalgovinfoid;
			lumpeddeleteDto.currentLocalgovinfoid = lumpeddeleteForm.currentLocalgovinfoid;
		}

		String [] deleteTableListIds = lumpeddeleteForm.deleteTableListIds;
		String [] deleteMapLayerAttrIds = lumpeddeleteForm.deleteMapLayerAttrIds;

		if((deleteTableListIds == null || deleteTableListIds.length <= 0 ) &&
				(deleteMapLayerAttrIds == null || deleteMapLayerAttrIds.length <= 0)){
			// 入力チェック
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Select more than one table list item info or map layer attribute info."), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}else{
			// 削除結果件数表示メッセージ
			ActionMessages messages = new ActionMessages();

			// テーブルリスト項目情報と属性行スタイル情報の削除
			if(deleteTableListIds != null && deleteTableListIds.length > 0 ){
				int tablelistcolumnInfoDeleteCount = 0;
				for(String deleteTableListMenuInfoId : deleteTableListIds){
					List<MenutableInfo> menutableInfoList = menutableInfoService.findByMenuInfoId(Long.parseLong(deleteTableListMenuInfoId));
					if(menutableInfoList != null && menutableInfoList.size() > 0){
						for(MenutableInfo menutableInfo : menutableInfoList){
							List<TablelistcolumnInfo> tablelistcolumnInfoList = tablelistcolumnInfoService.findByMenutableInfoId(menutableInfo.id);
							if(tablelistcolumnInfoList != null && tablelistcolumnInfoList.size() > 0){
								for(TablelistcolumnInfo tablelistcolumnInfo : tablelistcolumnInfoList){
									List<TablerowstyleInfo> tablerowstyleInfoList = tablerowstyleInfoService.findByTablelistcolumnInfoId(tablelistcolumnInfo.id);
									if(tablerowstyleInfoList != null && tablerowstyleInfoList.size() > 0){
										for(TablerowstyleInfo tablerowstyleInfo : tablerowstyleInfoList ){
											tablerowstyleInfoService.delete(tablerowstyleInfo);
										}
									}
									int dc = tablelistcolumnInfoService.delete(tablelistcolumnInfo);
									tablelistcolumnInfoDeleteCount += dc;
								}
							}
						}
					}
				}
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} items of table list item info deleted.", tablelistcolumnInfoDeleteCount), false));
			}
			// 地図レイヤ属性情報の削除
			if(deleteMapLayerAttrIds != null && deleteMapLayerAttrIds.length > 0 ){
				int maplayerattrInfoDeleteCount = 0;
				for(String deleteMapLayerAttrMenuInfoId : deleteMapLayerAttrIds){
					BeanMap conditions = new BeanMap();
					conditions.put(maplayerInfo().menuinfoid().toString(), deleteMapLayerAttrMenuInfoId);
					List<MaplayerInfo> maplayerInfoList = maplayerInfoService.findByCondition(conditions);
					if(maplayerInfoList != null && maplayerInfoList.size() > 0){
						for(MaplayerInfo maplayerInfo : maplayerInfoList){
							conditions = new BeanMap();
							conditions.put(maplayerattrInfo().maplayerinfoid().toString(), maplayerInfo.id.toString());
							List<MaplayerattrInfo> maplayerattrInfoList = maplayerattrInfoService.findByCondition(conditions);
							if(maplayerattrInfoList != null && maplayerattrInfoList.size() > 0){
								for(MaplayerattrInfo maplayerattrInfo : maplayerattrInfoList){
									int dc = maplayerattrInfoService.delete(maplayerattrInfo);
									maplayerattrInfoDeleteCount += dc;
								}
							}
						}
					}
				}
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("{0} items of map layer attribute info deleted.", maplayerattrInfoDeleteCount), false));
			}

			ActionMessagesUtil.addMessages(SpringContext.getRequest(), messages);
		}
		content(model, lumpeddeleteForm, bindingResult);
		return "/admin/lumpeddelete/index";
	}

	/**
	 * 自治体セレクトボックス選択による画面再描画
	 * @return トップページ
	 */
	@RequestMapping(value="/changeLocalgov", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	public String changeLocalgov(Map<String,Object>model,
			@ModelAttribute LumpeddeleteForm lumpeddeleteForm, BindingResult bindingResult){

		content(model, lumpeddeleteForm, bindingResult);
		return "/admin/lumpeddelete/index";
	}

	/**
	 * 自治体セレクトボックスの作成
	 */
	private void createLocalgovSelectOptions(){
		String pref = "";
		String city = "";
		String section = "";

		// 自治体切り替えSELECTオプション
		localgovSelectOptions = new LinkedHashMap<Long, String>();
		// システム管理者でログイン中
		if(loginDataDto.getLocalgovinfoid()==Constants.ADMIN_LOCALGOVINFOID) {
			List<LocalgovInfo> localgovInfos = localgovInfoService.findAll(
//					Operations.asc(LocalgovInfoNames.prefcode()),
//					Operations.asc(LocalgovInfoNames.citycode()),
					Operations.asc(LocalgovInfoNames.id())
			);
			for(LocalgovInfo localgovInfo : localgovInfos) {
				if(localgovInfo.id.equals(0L)){
					continue;
				}
				pref = localgovInfo.pref;
				city = localgovInfo.city;
				section = localgovInfo.section;
				if(pref == null){
					pref = "";
				}
				if(city == null){
					city = "";
				}
				if(section == null){
					section = "";
				}
				localgovSelectOptions.put(localgovInfo.id, pref+city+section);
			}
		}
		else {
			LocalgovInfo localgovInfo = loginDataDto.getLocalgovInfo();
			pref = localgovInfo.pref;
			city = localgovInfo.city;
			section = localgovInfo.section;
			if(pref == null){
				pref = "";
			}
			if(city == null){
				city = "";
			}
			if(section == null){
				section = "";
			}
			localgovSelectOptions.put(localgovInfo.id, pref+city+section);
		}

	}
}
