/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.ecom_plat.saigaitask.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerData;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.StationclassInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.form.LoginForm;
import jp.ecom_plat.saigaitask.service.LoginService;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteotriggerDataService;
import jp.ecom_plat.saigaitask.service.db.NoticegroupInfoService;
import jp.ecom_plat.saigaitask.service.db.StationclassInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;

/**
 * ログインページActionクラス
 * @author take
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class IndexAction extends AbstractAction {

	/** アクションフォーム */
    //@Resource
	protected LoginForm loginForm;

    /** 災害対応中の記録データリスト */
    public List<TrackData> trackDatas;

    /** 災害情報リスト */
	public List<DisasterMaster> disasterItems;
	/** 災害情報マスターサービス */
    @Resource
    protected DisasterMasterService disasterMasterService;

    /** 班情報リスト */
    public List<GroupInfo> groupInfoItems;
    /** 班情報サービス */
    @Resource
    protected GroupInfoService groupInfoService;
    /** 班情報 */
    public GroupInfo groupInfo;

    /** 課情報リスト */
    public List<UnitInfo> unitInfoItems;
    /** 課情報サービス */
    @Resource
    protected UnitInfoService unitInfoService;
    /** 課情報 */
    protected UnitInfo unitInfo;
    /** 災害結合済み */
    public Boolean disasterCombined = false;

    /** 記録データサービス */
    @Resource
    protected TrackDataService trackDataService;

	/** メニューログイン情報サービス */
	@Resource MenuloginInfoService menuloginInfoService;

	/** 気象情報トリガーデータサービス */
	@Resource MeteotriggerDataService meteotriggerDataService;

	/** 体制区分サービス */
	@Resource StationclassInfoService stationclassInfoService;

	/** 通知グループ */
	@Resource NoticegroupInfoService noticegroupInfoService;

	/** ログインサービス */
	@Resource LoginService loginService;

    /** 言語情報リスト */
	public List<MultilangInfo> multilangInfoItems;

	/** 職員参集に対するURLでの安否状況更新メッセージ表示用 */
	public String message;

	/** マスター確認 */
	public boolean bmaster = false;

	/**
     * ログインページ表示
     * @return ログインページ
     */
	@org.springframework.web.bind.annotation.RequestMapping(value={"","/index"})
	public String index() {
    	//return "/login?type=page";
		return "forward:/page/index";

    }

    /**
	 * ログインページに出す実行トリガー情報
	 * @return null
	 */
    @org.springframework.web.bind.annotation.RequestMapping("/triggerinfo/{localgovinfoid}")
    @ResponseBody
	public String triggerinfo(Map<String,Object>model,
			@ModelAttribute LoginForm loginForm, BindingResult bindingResult) {
		StringBuffer buff = new StringBuffer();

		Long localgovInfoId = Long.parseLong(loginForm.localgovinfoid);
    	List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(localgovInfoId);
		buff.append("<div id=\"logininfo_box_1\" class=\"logininfo_box\" style='height: 185px; overflow: auto;'>\n");
    	for(TrackData trackData : trackDatas) {
    	//if (gov != null) {
	    	if (trackData != null && !bmaster) {
	    		// 気象情報トリガーの実行結果をお知らせするリスト
	    		List<String> triggeredItems = new ArrayList<String>();
	    		// 気象情報トリガーの実行結果の状態
	    		List<String> automaticItems = new ArrayList<String>();
				//気象情報トリガーデータを取得
				List<MeteotriggerData> mtriglist = meteotriggerDataService.findByTrackdataIdOrderByTriggertime(trackData.id);
				//トップ画面に表示するトリガーのメッセージの作成
				if (mtriglist != null) {
					//災害モード起動
					boolean isStartup = false;
					//メール通知
					List<String> noticegroupnameList = new ArrayList<String>();
					//体制区分
					List<String> stationclassnameList = new ArrayList<String>();
					//職員参集
					boolean isAssemblemail = false;
					//避難勧告
					List<String> issuetextList = new ArrayList<String>();
					// 公共コモンズ送信
					boolean isPubliccommons = false;
					// 緊急速報メール，エリアメール
					boolean isPubliccommonsmail = false;
					// SNS
					boolean isSns = false;

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					for (MeteotriggerData mtrigd : mtriglist) {
						//トップ画面に表示するトリガー実行のメッセージ
						triggeredItems.add(lang.__("Having received by a trigger :{0}, auto process is executed.", sdf.format(mtrigd.triggertime)));

						//トリガーの実行結果のチェック
						if (mtrigd.startup) isStartup = true;
						if (mtrigd.noticegroupinfoid != null && mtrigd.noticegroupinfoid > 0) {
							NoticegroupInfo nginfo = noticegroupInfoService.findById(mtrigd.noticegroupinfoid);
							noticegroupnameList.add(nginfo.name);
						}
						if (mtrigd.stationclassinfoid != null && mtrigd.stationclassinfoid > 0) {
							StationclassInfo scinfo = stationclassInfoService.findById(mtrigd.stationclassinfoid);
							stationclassnameList.add(scinfo.name);
						}
						if (mtrigd.assemblemail) isAssemblemail = true;
						if (mtrigd.issue) {
							issuetextList.add(mtrigd.issuetext);
						}
						if (mtrigd.publiccommons) isPubliccommons = true;
						if (mtrigd.publiccommonsmail) isPubliccommonsmail = true;
						if (mtrigd.sns) isSns = true;
					}

					//トップ画面に表示するトリガー実行結果の各メッセージ
					if (isStartup)
						automaticItems.add(lang.__("System has been transferred to disaster mode."));
					if (!noticegroupnameList.isEmpty())
						automaticItems.add(lang.__("e-mail is auto delivered."));
					if (!stationclassnameList.isEmpty())
						automaticItems.add(lang.__("System has been automatically transferred to \"{0}\".", stationclassnameList.get(stationclassnameList.size()-1)));
					if (isAssemblemail)
						automaticItems.add(lang.__("Staff gathering request is auto delivered."));
					if (!issuetextList.isEmpty())
						automaticItems.add(lang.__("\"{0}\"is auto announced.", issuetextList.get(issuetextList.size()-1)));
					if (isPubliccommons)
						automaticItems.add(lang.__("Auto delivered to L-Alert."));
					if (isPubliccommonsmail)
						automaticItems.add(lang.__("Emergency e-mail, area mail auto delivered."));
					if (isSns)
						automaticItems.add(lang.__("Auto delivered to SNS."));
				}

				if (triggeredItems.size() > 0) {
				buff.append("	<div class=\"logininfo_area\">\n");

				buff.append("		<div id=\"content_1\" class=\"content content_triggered\">\n");
				buff.append("			<ul class=\"triggered\">\n");
				for (String item : triggeredItems) {
				buff.append("				<li>"+item+"</li>\n");
				}
				buff.append("			</ul>\n");
				buff.append("		</div>\n");

				buff.append("		<div id=\"content_2\" class=\"content content_automatic\">\n");
				buff.append("			<ul class=\"automatic\">\n");
				for (String item : automaticItems) {
				buff.append("				<li>☑　"+item+"</li>\n");
				}
				buff.append("			</ul>\n");
				buff.append("		</div>\n");

				buff.append("	</div>\n");
				}
			}
		}
		buff.append("</div>\n");

		//ResponseUtil.write(buff.toString());
		return buff.toString();

		//return null;
	}

	/**
	 * ログインメッセージを表示
	 * @return null
	 */
    @org.springframework.web.bind.annotation.RequestMapping("/blank")
    @ResponseBody
	public String blank() {
    	//ResponseUtil.write(lang.__("Login again."));
    	//return null;
    	return lang.__("Login again.");
    }
}
