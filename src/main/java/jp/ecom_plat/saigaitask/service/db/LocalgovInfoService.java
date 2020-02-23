/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.localgovInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.constant.Localgovtype;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AdminbackupData;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageInfo;
import jp.ecom_plat.saigaitask.entity.db.AlertcontentData;
import jp.ecom_plat.saigaitask.entity.db.AssembleInfo;
import jp.ecom_plat.saigaitask.entity.db.AuthorizationInfo;
import jp.ecom_plat.saigaitask.entity.db.AutocompleteInfo;
import jp.ecom_plat.saigaitask.entity.db.CkanauthInfo;
import jp.ecom_plat.saigaitask.entity.db.CkanmetadataInfo;
import jp.ecom_plat.saigaitask.entity.db.CkanmetadatadefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadataInfo;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadatadefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousesearchInfo;
import jp.ecom_plat.saigaitask.entity.db.ConvertidData;
import jp.ecom_plat.saigaitask.entity.db.DecisionsupportInfo;
import jp.ecom_plat.saigaitask.entity.db.DemoInfo;
import jp.ecom_plat.saigaitask.entity.db.DisasteritemInfo;
import jp.ecom_plat.saigaitask.entity.db.DisastersummaryhistoryData;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.EcomgwpostInfo;
import jp.ecom_plat.saigaitask.entity.db.FacebookInfo;
import jp.ecom_plat.saigaitask.entity.db.FacebookpostInfo;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.ImporttablemasterData;
import jp.ecom_plat.saigaitask.entity.db.ImporttrackInfo;
import jp.ecom_plat.saigaitask.entity.db.IssuelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.JalertreceivefileData;
import jp.ecom_plat.saigaitask.entity.db.JalertrequestInfo;
import jp.ecom_plat.saigaitask.entity.db.JalertserverInfo;
import jp.ecom_plat.saigaitask.entity.db.JalerttriggerData;
import jp.ecom_plat.saigaitask.entity.db.JudgemanInfo;
import jp.ecom_plat.saigaitask.entity.db.LandmarkInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovgroupmemberInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutasktypeInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteorequestInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerData;
import jp.ecom_plat.saigaitask.entity.db.MeteoxsltInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticeTemplate;
import jp.ecom_plat.saigaitask.entity.db.NoticeaddressInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupInfo;
import jp.ecom_plat.saigaitask.entity.db.OauthconsumerData;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatorydamlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverInfo;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryriverlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.PostingphotolayerInfo;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastEvent;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastGeneral;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportRefugeInfo;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportShelterInfo;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsSendToInfo;
import jp.ecom_plat.saigaitask.entity.db.SafetystateInfo;
import jp.ecom_plat.saigaitask.entity.db.StationalarmInfo;
import jp.ecom_plat.saigaitask.entity.db.StationclassInfo;
import jp.ecom_plat.saigaitask.entity.db.SummarylistInfo;
import jp.ecom_plat.saigaitask.entity.db.TelemeterData;
import jp.ecom_plat.saigaitask.entity.db.TelemeterserverInfo;
import jp.ecom_plat.saigaitask.entity.db.TelemetertimeData;
import jp.ecom_plat.saigaitask.entity.db.TelopData;
import jp.ecom_plat.saigaitask.entity.db.ToolboxData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanlinkData;
import jp.ecom_plat.saigaitask.entity.db.TwitterInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.util.Config;

/** 自治体情報サービス */
@org.springframework.stereotype.Repository
public class LocalgovInfoService extends AbstractService<LocalgovInfo> {
	//private static String[] alterFieldName = {"ID", "ドメイン", "型", "都道府県名", "都道府県コード", "市区町村名", "市区町村コード", "予備", "備考", "有効"};

	/**
	 * IDで検索
	 * @param id 自治体情報ID
	 * @return IDに対応唯一のレコード
	 */
	public LocalgovInfo findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * ID昇順で全検索
	 * @return 全データ
	 */
	public List<LocalgovInfo> findValidOrderByID() {
		return select().where(
				eq(localgovInfo().valid(), true)
			).orderBy(asc(localgovInfo().id())).getResultList();
	}

	/**
	 * ID昇順で全検索
	 * @return 全データ
	 */
	public List<LocalgovInfo> findAllOrderByID() {
		return select().orderBy(asc(localgovInfo().id())).getResultList();
	}

	/**
	 * urlによってデータを取得
	 * @param url
	 * @return 自治体情報
	 */
	public LocalgovInfo findByURLAndValid(String url) {
		LocalgovInfo gov = null;

		List<LocalgovInfo> govlist = this.findValidOrderByID();

		for (LocalgovInfo lgov : govlist) {
			if (StringUtil.isNotEmpty(lgov.domain) && url.equals(lgov.domain.trim()))
				gov = lgov;
		}

		// URL が localhost の場合は、１つ目の自治体を表示する
		// 新しいドメインの場合は、自治体インストール画面を出すため、gov は null のままにする
		if(0<govlist.size()) {
			// FORDEV
			if (gov == null && (url.contains("localhost") || url.contains("ec2"))) {
				gov = govlist.get(0);
			}
		}

		return gov;
	}

	/**
	 * ドメインで検索.
	 * (valid 問わず)
	 *
	 * @param domain ドメイン名
	 * @return 自治体情報リスト
	 */
	public List<LocalgovInfo> findByDomain(String domain) {
		return select().where(eq(localgovInfo().domain(), domain)).getResultList();
	}

	/**
	 * 自治体名を返す。
	 * @param govid
	 * @return 自治体名
	 */
	public String getLocalgovName(Long govid) {
		LocalgovInfo gov = findById(govid);
		if (gov.localgovtypeid.equals(Localgovtype.CITY))
			return gov.city;
		else if (gov.localgovtypeid.equals(Localgovtype.PREF))
			return gov.pref;
		else if (gov.localgovtypeid.equals(Localgovtype.OTHER))
			return gov.section;
		return "";
	}

	/**
	 * 自治体名のフルネームを返す。
	 * @param localgovInfo 自治体情報
	 * @return 自治体のフルネーム
	 */
	public String getLocalgovNameFull(LocalgovInfo localgovInfo) {
		// 自治体名を取得
		String localgovName = localgovInfo.pref;
		if(Localgovtype.PREF.equals(localgovInfo.localgovtypeid)) return localgovName;
		if(Localgovtype.CITY.equals(localgovInfo.localgovtypeid)) {
			if(StringUtil.isNotEmpty(localgovInfo.city)) {
				localgovName = localgovInfo.city;
			}
			// 市区町村名が空の場合があるので、その場合は県名を採用
			else {
				localgovName = localgovInfo.pref;
			}
			return localgovName;
		}
		if(Localgovtype.OTHER.equals(localgovInfo.localgovtypeid)) {
			if(StringUtil.isNotEmpty(localgovInfo.city)) localgovName += localgovInfo.city;
			if(StringUtil.isNotEmpty(localgovInfo.section)) localgovName += localgovInfo.section;
			return localgovName;
		}
		return "";
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(localgovInfo().localgovtypeMaster())
			.leftOuterJoin(localgovInfo().groupInfo())
			.leftOuterJoin(localgovInfo().multilangInfo())
			.where(conditions)
			.getCount();
	}

	/**
	 * 検索条件に従い検索し、結果一覧を取得する。ソート、ページング対応版。
	 * @param conditions 検索条件マップ
	 * @param sortName ソート項目名
	 * @param sortOrder ソート順（昇順 or 降順）
	 * @param limit 取得件数
	 * @param offset 取得開始位置
	 * @return 検索結果
	 */
	public List<LocalgovInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = localgovInfo().id().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!localgovInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(localgovInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[orderByItemList.size()]);

		return select()
			.innerJoin(localgovInfo().localgovtypeMaster())
			.leftOuterJoin(localgovInfo().groupInfo())
			.leftOuterJoin(localgovInfo().multilangInfo())
			.where(conditions)
			.orderBy(orderByItems)
			.limit(limit)
			.offset(offset)
			.getResultList();
	}


	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return 更新数
	*/
	public int update(LocalgovInfo entity, PropertyName<?>[] excludes) {
		if(excludes != null){
			return jdbcManager
				.update(entity)
				.excludes(excludes)
				.execute();
		}else{
			return jdbcManager
				.update(entity)
				.execute();
		}
	}

	/**
	 * 自治体IDに紐付く地方自治体情報を取得する。<br>
	 * @param localgovinfoid 自治体ID
	 * @return 検索結果
	 */
    public List<LocalgovInfo> findByLocalgovinfoid(Long localgovinfoid) {
		AutoSelect<LocalgovInfo> select = select();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者以外の場合、自治体IDで絞り込む
			select.where(
					eq(localgovInfo().id(), localgovinfoid)
					);
		}else{
			//システム管理者
			//全件
		}
		select.orderBy(asc(localgovInfo().id()));
		return select.getResultList();
	}

	/**
	 * 都道府県と連携している自治体情報を検索する。
	 * @param preflocalgovinfoid 都道府県の自治体ID
	 * @return 都道府県と連携している自治体情報
	 */
	public List<LocalgovInfo> findCityLocalgovInfo(Long preflocalgovinfoid) {
		return select()
				.where(eq(localgovInfo().preflocalgovinfoid(), preflocalgovinfoid))
				.orderBy(localgovInfo().citycode().toString())
				.getResultList();
	}

	/**
	 * 都道府県と連携している自治体情報を記録データ付きで検索する。
	 * @param preflocalgovinfoid 都道府県の自治体ID
	 * @param preftrackdataid 都道府県の記録ID
	 * @return 都道府県と連携している自治体情報を記録データ付き
	 */
	public List<LocalgovInfo> findCityLocalgovInfoJoinTrackData(Long preflocalgovinfoid, Long preftrackdataid) {
		return select()
				.leftOuterJoin(localgovInfo().trackDatas())
				.leftOuterJoin(localgovInfo().trackDatas().cityTrackgroupDatas())
				.where(
						eq(localgovInfo().preflocalgovinfoid(), preflocalgovinfoid),
						eq(localgovInfo().trackDatas().cityTrackgroupDatas().preftrackdataid(), preftrackdataid)
				)
				.orderBy(localgovInfo().citycode().toString())
				.getResultList();
	}

	/**
	 * 指定自治体と同じ都道府県コードを持つ自治体情報を検索する
	 * @param localgovinfoid 自治体ID
	 * @return 指定自治体と同じ都道府県コードを持つ自治体情報
	 */
	public List<LocalgovInfo> findLocalgovInfoJoinGroupInfo(Long localgovinfoid) {
		// 都道府県コード取得
		LocalgovInfo gov = select().where(eq(localgovInfo().id(), localgovinfoid)).getSingleResult();

		if (gov == null) {
			return null;
		}
		if (StringUtil.isNotEmpty(gov.prefcode)) {
			return select()
					.leftOuterJoin(localgovInfo().groupInfos())
					.leftOuterJoin(localgovInfo().multilangInfo())
					.where(
							eq(localgovInfo().prefcode(), gov.prefcode)
					)
					.orderBy(localgovInfo().city().toString())
					.getResultList();
		} else {
			return null;
		}
	}

    /**
     * 連携先県コードを取得する
     * @param localgovInfo 自治体情報
     * @return 連携先県コード
     */
    public long havePrefLocalgovinfoid(LocalgovInfo localgovInfo){
		// 県の場合は、連携する自治体を検索する
		if(Localgovtype.PREF.equals(localgovInfo.localgovtypeid)) {
			// 17/06/13 南西レスキューでは県同士でメッセンジャーを使えるようにするため、preflocalgovinfoidを優先して利用する
			return localgovInfo.preflocalgovinfoid == null ? localgovInfo.id : localgovInfo.preflocalgovinfoid;
		}
		// 市町村の場合は連携している県を持っていれば、その県に連携する市町村を検索する
		else{
			return localgovInfo.preflocalgovinfoid == null ? 0 : localgovInfo.preflocalgovinfoid;
		}
    }

	@Override
	public DeleteCascadeResult deleteCascade(LocalgovInfo entity, DeleteCascadeResult result) {

		//
		// データ
		//

		//===============================================================================
		// 管理者向け、設定バックアップデータ
		// LocalgovInfo->AdminbackupData
		result.cascade(AdminbackupData.class, Names.adminbackupData().localgovinfoid(), entity.id);

		//===============================================================================
		// 通信途絶データ
		// LocalgovInfo->ConvertidData
		result.cascade(ConvertidData.class, Names.convertidData().localgovinfoid(), entity.id);

		//===============================================================================
		// JAlert受信データ
		// LocalgovInfo->JalertreceivefileData
		result.cascade(JalertreceivefileData.class, Names.jalertreceivefileData().localgovinfoid(), entity.id);

		//===============================================================================
		// テロップデータ
		// LocalgovInfo->TelopData
		result.cascade(TelopData.class, Names.telopData().localgovinfoid(), entity.id);

		//===============================================================================
		// 記録データ
		// 自治体ID直で可能なデータも、自治体IDで直接削除するのではなく、
		// TrackData 指定での削除も考慮し、TrackDataに紐付けて削除する。
		// LocalgovInfo->TrackData
		// LocalgovInfo->TrackData->AlarmmessageData
		// LocalgovInfo->TrackData->AlarmmessageData->AlarmshowData
		// LocalgovInfo->TrackData->AlertcontentData ??
		// LocalgovInfo->TrackData->AssemblestateData
		// LocalgovInfo->TrackData->DisasterbuildData
		// LocalgovInfo->TrackData->DisastercasualtiesData
		// LocalgovInfo->TrackData->DisasterfarmData
		// LocalgovInfo->TrackData->DisasterfireData
		// LocalgovInfo->TrackData->DisasterhospitalData
		// LocalgovInfo->TrackData->DisasterhouseData
		// LocalgovInfo->TrackData->DisasterhouseholdData
		// LocalgovInfo->TrackData->DisasterhouseregidentData
		// LocalgovInfo->TrackData->DisasterlifelineData
		// LocalgovInfo->TrackData->DisasterroadData
		// LocalgovInfo->TrackData->DisasterschoolData
		// LocalgovInfo->TrackData->DisasterwelfareData
		// LocalgovInfo->TrackData->DisastersummaryhistoryData
		// LocalgovInfo->TrackData->DisastersummaryhistoryData->DisastersituationhistoryData
		// LocalgovInfo->TrackData->GeneralizationhistoryData
		// LocalgovInfo->TrackData->HeadofficeData
		// LocalgovInfo->TrackData->JalerttriggerData
		// LocalgovInfo->TrackData->LoginData
		// LocalgovInfo->TrackData->MeteotriggerData
		// LocalgovInfo->TrackData->NoticemailData
		// LocalgovInfo->TrackData->NoticemailData->NoticemailsendData
		// LocalgovInfo->TrackData->PubliccommonsReportData
		// LocalgovInfo->TrackData->PubliccommonsReportData->PubliccommonsReportDataLastAntidisaster
		// LocalgovInfo->TrackData->PubliccommonsReportData->PubliccommonsReportDataLastDamage
		// LocalgovInfo->TrackData->PubliccommonsReportData->PubliccommonsReportDataLastEvent
		// LocalgovInfo->TrackData->PubliccommonsReportData->PubliccommonsReportDataLastGeneral
		// LocalgovInfo->TrackData->PubliccommonsReportData->PubliccommonsReportDataLastRefuge
		// LocalgovInfo->TrackData->PubliccommonsReportData->PubliccommonsReportDataLastShelter
		// LocalgovInfo->TrackData->PubliccommonsReportData->PubliccommonsSendHistoryData
		// LocalgovInfo->TrackData->ReportData
		// LocalgovInfo->TrackData->ReportData->Reportcontent2Data
		// LocalgovInfo->TrackData->ReportData->ReportcontentData
		// LocalgovInfo->TrackData->ThreadData
		// LocalgovInfo->TrackData->ThreadData->ThreadresponseData
		// LocalgovInfo->TrackData->ThreadData->ThreadsendtoData
		// LocalgovInfo->TrackData->TrackgroupData
		// LocalgovInfo->TrackData->TrackmapInfo
		// LocalgovInfo->TrackData->TrainingplanData
		// LocalgovInfo->TrackData->TrainingplanData->TrainingmeteoData
		// LocalgovInfo->TrackData->TrainingplanData->TrainingplanlinkData
		// LocalgovInfo->TrackData->ImporttrackInfo
		// LocalgovInfo->TrackData->ImporttrackInfo->ImporttracktableInfo
		result.cascade(TrackData.class, Names.trackData().localgovinfoid(), entity.id);
		// ↓はlocalgovinfoidで削除可能な、TrackDataに紐づくデータ
		// 平常時の場合、TrackDataに紐づかないデータになるため念のため削除する
		result.cascade(AlarmmessageData.class, Names.alarmmessageData().localgovinfoid(), entity.id);
		result.cascade(AlertcontentData.class, Names.alertcontentData().localgovinfoid(), entity.id);
		result.cascade(DisastersummaryhistoryData.class, Names.disastersummaryhistoryData().localgovinfoid(), entity.id);
		result.cascade(JalerttriggerData.class, Names.jalerttriggerData().localgovinfoid(), entity.id);
		result.cascade(MeteotriggerData.class, Names.meteotriggerData().localgovinfoid(), entity.id);
		result.cascade(PubliccommonsReportData.class, Names.publiccommonsReportData().localgovinfoid(), entity.id);
		result.cascade(PubliccommonsReportDataLastEvent.class, Names.publiccommonsReportDataLastEvent().localgovinfoid(), entity.id);
		result.cascade(PubliccommonsReportDataLastGeneral.class, Names.publiccommonsReportDataLastGeneral().localgovinfoid(), entity.id);
		result.cascade(TrainingplanData.class, Names.trainingplanData().localgovinfoid(), entity.id);
		result.cascade(TrainingplanlinkData.class, Names.trainingplanlinkData().localgovinfoid(), entity.id);
		result.cascade(ImporttrackInfo.class, Names.importtrackInfo().localgovinfoid(), entity.id);
		// V1.4.10
		result.cascade(ImporttablemasterData.class, Names.importtablemasterData().localgovinfoid(), entity.id);
		// V1.4.14
		result.cascade(OauthconsumerData.class, Names.oauthconsumerData().localgovinfoid(), entity.id);

		// 自治体に紐づかないため削除できないデータ
		//- TelemeterData -
		//- TelemetertimeData -



		//
		// 自治体設定
		//

		//===============================================================================
		// [自治体]-[ログイン]

		// [班情報]
		// LocalgovInfo->GroupInfo
		// LocalgovInfo->GroupInfo->UserInfo
		// LocalgovInfo->GroupInfo->UserInfo->NoticegroupuserInfo
		// LocalgovInfo->GroupInfo->AlarmdefaultgroupInfo
		// LocalgovInfo->GroupInfo->LandmarkData
		// LocalgovInfo->GroupInfo->MenuloginInfo
		// LocalgovInfo->GroupInfo->MenuloginInfo->MenuprocessInfo
		// LocalgovInfo->GroupInfo->MenuloginInfo->MenuprocessInfo->MenutaskInfo
		// LocalgovInfo->GroupInfo->MenuloginInfo->MenuprocessInfo->MenutaskInfo->MenutaskmenuInfo
		// LocalgovInfo->GroupInfo->WhiteboardData
		result.cascade(GroupInfo.class, Names.groupInfo().localgovinfoid(), entity.id);

		// [課情報]
		// LocalgovInfo->UnitInfo
		// LocalgovInfo->UnitInfo->UserInfo
		// LocalgovInfo->UnitInfo->UserInfo->NoticegroupuserInfo
		result.cascade(UnitInfo.class, Names.unitInfo().localgovinfoid(), entity.id);

		// [訓練情報]
		// LocalgovInfo->DemoInfo
		result.cascade(DemoInfo.class, Names.demoInfo().localgovinfoid(), entity.id);

		//===============================================================================
		// [自治体]-[システム全般]
		// [メニュー階層]は班情報のツリーに含まれる

		// [メニュータスク種別]
		// LocalgovInfo->MenutasktypeInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->ExternalmapdataInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->ExternaltabledataInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->FilterInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->MapbaselayerInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->MapkmllayerInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->MapreferencelayerInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->MenumapInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->MeteolayerInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->NoticedefaultInfo->NoticedefaultgroupInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->NoticedefaultInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->ObservmenuInfo->ObservlistInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->ObservmenuInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->PagemenubuttonInfo
		// LocalgovInfo->MenutasktypeInfo->MenuInfo->SummarylistcolumnInfo
		result.cascade(MenutasktypeInfo.class, Names.menutasktypeInfo().localgovinfoid(), entity.id);

		// [地図マスター情報]
		// LocalgovInfo->MapmasterInfo
		// LocalgovInfo->MapmasterInfo->EarthquakegrouplayerData
		// LocalgovInfo->MapmasterInfo->EarthquakegrouplayerData->EarthquakelayerData
		// LocalgovInfo->MapmasterInfo->TablemasterInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->HistorytableInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->HistorytableInfo->HistorycolumnlistInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->TracktableInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->MaplayerInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->MaplayerInfo->MaplayerattrInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->MenutableInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->MenutableInfo->TablelistcolumnInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->MenutableInfo->TablelistcolumnInfo->TablerowstyleInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->ObservatorydamInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->ObservatorydamlayerInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->ObservatoryrainInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->ObservatoryrainlayerInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->ObservatoryriverInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->ObservatoryriverlayerInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->PubliccommonsReportRefugeInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->PubliccommonsReportShelterInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->StationlayerInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->TablecalculatecolumnInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->TablecalculatecolumnInfo->TablecalculateInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->TimelinetableInfo
		// LocalgovInfo->MapmasterInfo->TablemasterInfo->DecisionsupportInfo since V1.4.14
		result.cascade(MapmasterInfo.class, Names.mapmasterInfo().localgovinfoid(), entity.id);

		//===============================================================================
		// [自治体]-[外部地図データ]
		// [メタデータデフォルト情報]
		// LocalgovInfo->ClearinghousemetadatadefaultInfo
		result.cascade(ClearinghousemetadatadefaultInfo.class, Names.clearinghousemetadatadefaultInfo().localgovinfoid(), entity.id);
		// LocalgovInfo->CkanmetadatadefaultInfo
		result.cascade(CkanmetadatadefaultInfo.class, Names.ckanmetadatadefaultInfo().localgovinfoid(), entity.id);

		// [クリアリングハウス事前データ情報]
		// LocalgovInfo->ClearinghousemetadataInfo
		result.cascade(ClearinghousemetadataInfo.class, Names.clearinghousemetadataInfo().localgovinfoid(), entity.id);
		// LocalgovInfo->CkanmetadataInfo
		result.cascade(CkanmetadataInfo.class, Names.ckanmetadataInfo().localgovinfoid(), entity.id);

		// [クリアリングハウス検索情報]
		// LocalgovInfo->ClearinghousesearchInfo
		result.cascade(ClearinghousesearchInfo.class, Names.clearinghousesearchInfo().localgovinfoid(), entity.id);

		// [集計リスト情報]
		// SummarylistInfo->SummarylistcolumnInfo
		result.cascade(SummarylistInfo.class, Names.summarylistInfo().localgovinfoid(), entity.id);

		// [認証情報]
		result.cascade(AuthorizationInfo.class, Names.authorizationInfo().localgovinfoid(), entity.id);
		result.cascade(CkanauthInfo.class, Names.ckanauthInfo().localgovinfoid(), entity.id);

		//===============================================================================
		// [自治体]-[通知]

		// [通知グループ情報]
		// LocalgovInfo->NoticegroupInfo
		result.cascade(NoticegroupInfo.class, Names.noticegroupInfo().localgovinfoid(), entity.id);

		// [通知連絡先情報]
		// LocalgovInfo->NoticeaddressInfo
		// LocalgovInfo->NoticeaddressInfo->NoticegroupaddressInfo
		result.cascade(NoticeaddressInfo.class, Names.noticeaddressInfo().localgovinfoid(), entity.id);

		// [通知テンプレート]
		// LocalgovInfo->NoticeTemplate
		result.cascade(NoticeTemplate.class, Names.noticeTemplate().localgovinfoid(), entity.id);
		result.cascade(ObservatorydamInfo.class, Names.observatorydamInfo().localgovinfoid(), entity.id);
		result.cascade(ObservatorydamlayerInfo.class, Names.observatorydamlayerInfo().localgovinfoid(), entity.id);
		result.cascade(ObservatoryrainInfo.class, Names.observatoryrainInfo().localgovinfoid(), entity.id);
		result.cascade(ObservatoryrainlayerInfo.class, Names.observatoryrainlayerInfo().localgovinfoid(), entity.id);
		result.cascade(ObservatoryriverInfo.class, Names.observatoryriverInfo().localgovinfoid(), entity.id);
		result.cascade(ObservatoryriverlayerInfo.class, Names.observatoryriverlayerInfo().localgovinfoid(), entity.id);
		result.cascade(PubliccommonsReportRefugeInfo.class, Names.publiccommonsReportRefugeInfo().localgovinfoid(), entity.id);
		result.cascade(PubliccommonsReportShelterInfo.class, Names.publiccommonsReportShelterInfo().localgovinfoid(), entity.id);

		// [安否応答状況情報]
		// since V1.4.9
		// LocalgovInfo->SafetystateInfo
		result.cascade(SafetystateInfo.class, Names.safetystateInfo().localgovinfoid(), entity.id);

		// [アラーム]-[アラームメッセージ設定]
		// LocalgovInfo->AlarmmessageInfo
		// LocalgovInfo->AlarmmessageInfo->JudgealarmInfo
		// LocalgovInfo->AlarmmessageInfo->JudgealarmInfo->JudgenoticeInfo
		// LocalgovInfo->AlarmmessageInfo->StationalarmInfo
		result.cascade(AlarmmessageInfo.class, Names.alarmmessageInfo().localgovinfoid(), entity.id);

		//===============================================================================
		// [自治体]-[監視観測]

		// [気象情報]-[気象情報等取得情報]
		// LocalgovInfo->MeteorequestInfo
		// LocalgovInfo->MeteorequestInfo->MeteoData
		// LocalgovInfo->MeteorequestInfo->MeteotriggerInfo
		result.cascade(MeteorequestInfo.class, Names.meteorequestInfo().localgovinfoid(), entity.id);

		// [気象情報]-[気象情報XSLT情報]
		// LocalgovInfo->MeteoxsltInfo
		result.cascade(MeteoxsltInfo.class, Names.meteoxsltInfo().localgovinfoid(), entity.id);

		// [気象情報]-[震度レイヤ情報] ※管理画面なし
		result.cascade(EarthquakelayerInfo.class, Names.earthquakelayerInfo().localgovinfoid(), entity.id);

		// [河川情報]-[テレメータ]
		// LocalgovInfo->TelemeterserverInfo
		// LocalgovInfo->TelemeterserverInfo->TelemeterofficeInfo
		// LocalgovInfo->TelemeterserverInfo->TelemeterofficeInfo->TelemeterfileData
		// LocalgovInfo->TelemeterserverInfo->TelemeterofficeInfo->TelemeterpointInfo
		result.cascade(TelemeterserverInfo.class, Names.telemeterserverInfo().localgovinfoid(), entity.id);

		// [河川情報]-[メニュー設定]はメニュータスク種別のツリーに含まれる
		// [河川情報]-[観測地点]-[雨量レイヤ]は地図マスタ情報のツリーに含まれる
		// [河川情報]-[観測地点]-[雨量観測地点]は地図マスタ情報のツリーに含まれる
		// [河川情報]-[観測地点]-[河川水位レイヤ]は地図マスタ情報のツリーに含まれる
		// [河川情報]-[観測地点]-[河川水位観測地点]は地図マスタ情報のツリーに含まれる
		// [河川情報]-[観測地点]-[ダムレイヤ]は地図マスタ情報のツリーに含まれる
		// [河川情報]-[観測地点]-[ダム観測地点]は地図マスタ情報のツリーに含まれる

		// [河川情報]-[判定]
		// LocalgovInfo->JudgemanInfo
		// LocalgovInfo->JudgemanInfo->JudgeresultData
		// LocalgovInfo->JudgemanInfo->JudgeresultstyleInfo->JudgeresultstyleData
		// LocalgovInfo->JudgemanInfo->JudgeInfo
		// LocalgovInfo->JudgemanInfo->JudgeresultstyleInfo
		result.cascade(JudgemanInfo.class, Names.judgemanInfo().localgovinfoid(), entity.id);

		// [JAlert]-[JAlertサーバ情報]
		// LocalgovInfo->JalertserverInfo
		result.cascade(JalertserverInfo.class, Names.jalertserverInfo().localgovinfoid(), entity.id);

		// [JAlert]-[JAlert情報取得情報]
		// LocalgovInfo->JalertrequestInfo
		// LocalgovInfo->JalertrequestInfo->JalerttriggerInfo
		result.cascade(JalertrequestInfo.class, Names.jalertrequestInfo().localgovinfoid(), entity.id);

		//===============================================================================
		// [自治体]-[避難勧告・指示]
		// [避難勧告レイヤ]
		// LocalgovInfo->IssuelayerInfo
		result.cascade(IssuelayerInfo.class, Names.issuelayerInfo().localgovinfoid(), entity.id);

		//===============================================================================
		// [自治体]-[本部設置]
		// [職員参集情報]
		// LocalgovInfo->AssembleInfo
		result.cascade(AssembleInfo.class, Names.assembleInfo().localgovinfoid(), entity.id);

		// [体制区分]
		// LocalgovInfo->StationclassInfo
		result.cascade(StationclassInfo.class, Names.stationclassInfo().localgovinfoid(), entity.id);

		// [体制レイヤ]は地図マスタ情報のツリーに含まれる

		//===============================================================================
		// [自治体]-[広報]

		// [Twitter設定]
		// LocalgovInfo->TwitterInfo
		result.cascade(TwitterInfo.class, Names.twitterInfo().localgovinfoid(), entity.id);

		// [Facebook設定]
		// LocalgovInfo->FacebookInfo
		result.cascade(FacebookInfo.class, Names.facebookInfo().localgovinfoid(), entity.id);

		// [Facebook投稿先情報]
		// LocalgovInfo->FacebookpostInfo->FacebookpostdefaultInfo
		// LocalgovInfo->FacebookpostInfo
		result.cascade(FacebookpostInfo.class, Names.facebookpostInfo().localgovinfoid(), entity.id);

		// [eコミGW投稿先情報]
		// LocalgovInfo->EcomgwpostInfo
		// LocalgovInfo->EcomgwpostInfo->EcomgwpostdefaultInfo
		result.cascade(EcomgwpostInfo.class, Names.ecomgwpostInfo().localgovinfoid(), entity.id);

		//===============================================================================
		// [自治体]-[意思決定支援]
		// since V1.4.14

		// [避難者推定]
		result.cascade(DecisionsupportInfo.class, Names.decisionsupportInfo().localgovinfoid(), entity.id);

		//===============================================================================
		// [自治体]-[公共情報コモンズ]

		// [公共情報コモンズ避難勧告情報]
		// [公共情報コモンズ避難所情報]

		// [公共情報コモンズ発信先データ]
		// LocalgovInfo->PubliccommonsSendToInfo
		result.cascade(PubliccommonsSendToInfo.class, Names.publiccommonsSendToInfo().localgovinfoid(), entity.id);
		result.cascade(StationalarmInfo.class, Names.stationalarmInfo().localgovinfoid(), entity.id);

		//===============================================================================
		// [自治体]-[その他]

		// [被災項目情報]
		// LocalgovInfo->DisasteritemInfo
		result.cascade(DisasteritemInfo.class, Names.disasteritemInfo().localgovinfoid(), entity.id);

		// [ランドマーク]
		// LocalgovInfo->LandmarkInfo
		result.cascade(LandmarkInfo.class, Names.landmarkInfo().localgovinfoid(), entity.id);

		// [投稿写真レイヤ]
		// since V1.4.8
		// LocalgovInfo->PostingphotolayerInfo
		// LocalgovInfo->PostingphotolayerInfo->PostingphotolayerData
		result.cascade(PostingphotolayerInfo.class, Names.postingphotolayerInfo().localgovinfoid(), entity.id);

		// [ツールボックス情報]
		// since V2.0.5
		// LocalgovInfo->ToolboxData
		result.cascade(ToolboxData.class, Names.toolboxData().localgovinfoid(), entity.id);

		//===============================================================================
		// [？？？？]
		// LocalgovInfo->AutocompleteInfo
		// LocalgovInfo->AutocompleteInfo->AutocompleteData
		result.cascade(AutocompleteInfo.class, Names.autocompleteInfo().localgovinfoid(), entity.id);


		//
		// グローバル設定　※自治体の上に位置する設定
		//

		//===============================================================================
		// [V2.2以前の連携自治体設定]

		// 県を削除する場合は、連携自治体の設定をすべて解除する
		List<LocalgovInfo> cityLocalgovInfos = this.findCityLocalgovInfo(entity.id);
		for(LocalgovInfo cityLocalgovInfo : cityLocalgovInfos) {
			// 連携を解除
			cityLocalgovInfo.preflocalgovinfoid = null;
			update(cityLocalgovInfo);
		}

		//===============================================================================
		// [V2.3以降の自治体グループ設定]
		// LocalgovInfo->LocalgovgroupInfo 削除自治体がオーナーになっている自治体グループを削除
		// LocalgovInfo->LocalgovgroupInfo->LocalgovgroupmemberInfo メンバーも削除される
		result.cascade(LocalgovgroupInfo.class, Names.localgovgroupInfo().localgovinfoid(), entity.id);
		// LocalgovInfo->LocalgovgroupInfo->LocalgovgroupmemberInfo　削除自治体がメンバーになっている自治体グループから削除
		result.cascade(LocalgovgroupmemberInfo.class, Names.localgovgroupmemberInfo().localgovinfoid(), entity.id);

		
		// アップロードされたラスタファイルを削除
		String tilePath =  Config.getString("DISCONNTCT_TILE_PATH");
		File dir = new File(tilePath + "/" + entity.id);
		result.cascadeFile(dir, entity, entity.id.toString(), dir.getPath());

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		//return result;
		return super.deleteCascade(entity, result);
	}

	/** 削除しないテーブル名リスト */
	public static List<String> deleteskippedTableNames;
	{
		// 自治体情報の削除の対象外テーブルを生成(自治体情報にリンクしないため)
		deleteskippedTableNames = new ArrayList<String>();
		deleteskippedTableNames.add(TelemeterData.class.getSimpleName());
		deleteskippedTableNames.add(TelemetertimeData.class.getSimpleName());
	}

}
