/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.publiccommons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.ServletException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Binding;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPBinding;

import jp.ecom_plat.saigaitask.constant.PubliccommonsSendType;
import jp.ecom_plat.saigaitask.dto.DamageInformationDto;
import jp.ecom_plat.saigaitask.dto.EventInformationDto;
import jp.ecom_plat.saigaitask.dto.GeneralInformationDto;
import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.dto.RefugeInformationDto;
import jp.ecom_plat.saigaitask.dto.ShelterInformationDto;
import jp.ecom_plat.saigaitask.dto.AntidisasterInformationDto;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastAntidisaster;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastDamage;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastEvent;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastGeneral;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastRefuge;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastShelter;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportRefugeInfo;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportShelterInfo;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsSendHistoryData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsSendToInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticemailDataService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportDataLastDamageService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportDataLastEventService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportDataLastGeneralService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportDataLastRefugeService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportDataLastShelterService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportDataLastAntidisasterService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportRefugeInfoService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsReportShelterInfoService;
import jp.ecom_plat.saigaitask.service.db.PubliccommonsSendHistoryDataService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.Constants.PublicCommonsSendStatus;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.antidisasterheadquarter.EnumTypeStatus;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.evacuation.EnumTypeIssueOrLift;
import net.sf.saxon.TransformerFactoryImpl;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSProcessorFactory;
import com.sun.xml.wss.XWSSecurityException;


/**
 * 公共情報コモンズサービス
 * 　mori create 20130908
 */
@org.springframework.stereotype.Service
public class PublicCommonsService extends AbstractPublicCommonsService {

	/** 公共情報コモンズ発信履歴データサービス */
	@Resource
	protected PubliccommonsSendHistoryDataService publiccommonsSendHistoryDataService;

	/** 公共情報コモンズの緊急メール速報サービスクラス */
	@Resource
	protected PcUrgentMailService pcUrgentMailService;

	/** 公共情報コモンズの避難勧告・指示サービスクラス */
	@Resource
	public PcEvacuationService pcEvacuationService;

	/** 公共情報コモンズ避難所サービス */
	@Resource
	public PcShelterService pcShelterService;

	/** 公共情報コモンズイベントサービス */
	@Resource
	public PcEventService pcEventService;

	/** 公共情報コモンズお知らせサービス */
	@Resource
	public PcGeneralService pcGeneralService;

	/** 公共情報コモンズ被害情報サービス */
	@Resource
	public PcDamageService pcDamageService;

	/** 公共情報コモンズの災害対策本部設置状況サービスクラス */
	@Resource
	public PcAntidisasterService pcAntidisasterService;

	@Resource
	protected PubliccommonsReportShelterInfoService publiccommonsReportShelterInfoService;
	@Resource
	protected PubliccommonsReportRefugeInfoService publiccommonsReportRefugeInfoService;
	@Resource
	protected PubliccommonsReportDataLastRefugeService publiccommonsReportDataLastRefugeService;
	@Resource
	protected PubliccommonsReportDataLastShelterService publiccommonsReportDataLastShelterService;
	@Resource
	protected PubliccommonsReportDataLastEventService publiccommonsReportDataLastEventService;
	@Resource
	protected PubliccommonsReportDataLastGeneralService publiccommonsReportDataLastGeneralService;
	@Resource
	protected PubliccommonsReportDataLastDamageService publiccommonsReportDataLastDamageService;
	@Resource
	protected PubliccommonsReportDataLastAntidisasterService publiccommonsReportDataLastAntidisasterService;
	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	@Resource
	protected TrackDataService trackDataService;
	@Resource
	protected TracktableInfoService tracktableInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected TablelistcolumnInfoService tablelistcolumnInfoService;
	@Resource
	protected TableService tableService;
	@Resource
	protected MapService mapService;
	@Resource
	protected NoticemailDataService noticemailDataService;
	@Resource
	protected GroupInfoService groupInfoService;

	/** ロガー */
	Logger logger = Logger.getLogger(AbstractService.class);

	private final int RESULT_CODE_SUCCESS = 0;		// コモンズ発信：正常終了
	private final int RESULT_CODE_USER_ERROR = 401;	// コモンズ発信：ユーザエラー：メッセージ：メッセージのURI形式が不正です。
	private final int RESULT_CODE_SCHEMA_ERROR = 402;	// コモンズ発信：ユーザエラー：メッセージ：入力メッセージはスキーマに従っていません。
	private final int RESULT_CODE_SYSTEM_ERROR = 901;	// コモンズ発信：システムエラー：その他：その他のエラーが発生しました。

	public LocalgovInfo localgovInfoItems;
	public List<NoticemailData> noticemailDataItems;

	/** 公共情報コモンズ発信先データ */
	private Long publiccommonsSendToInfoId;

	/**
	 * 避難所情報リストを取得
	 * @param localgovinfoid 自治体ID
	 * @param trackdataid 記録データID
	  * @return 記録データ有:refugeInformationList 記録データ無:null
	 * @throws ParseException
	 */
//	public List<ShelterInformationDto> getShelterInformationList(Long localgovinfoid, Long trackdataid,  Long tblmasterid) throws ParseException {
//			List<BeanMap> result;
//
//			TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tblmasterid, trackdataid);
//
//			// eコミマップのレイヤ：trackdataidは無い
//			if (ttbl != null) {
//				// 避難所リストをを取得
//				result = tableService.selectAll(ttbl.tablename);
//
//				List<ShelterInformationDto> shelterInformationList = new ArrayList<ShelterInformationDto>();
//				// 避難所リストから避難所名～状態を取り出す
//				for (BeanMap map : result) {
//					// 属性とコモンズ発信項目の対応表を取得
//					PubliccommonsReportShelterInfo publiccommonsReportShelterInfo = publiccommonsReportShelterInfoService.findByLocalgovInfoId(localgovinfoid);
//					if (publiccommonsReportShelterInfo == null) {
//						continue;
//					}
//
//					// 対応表に基づいて避難所名～状態を取り出す
//					ShelterInformationDto shelterInformation = new ShelterInformationDto();
//					//for (TablelistcolumnInfo cur : colinfoItems) {
//						setShelterInformation(publiccommonsReportShelterInfo, map, shelterInformation);
//					//}
//					shelterInformationList.add(shelterInformation);
//				}
//				return shelterInformationList;
//			}
//			return null;
//		}
	public List<ShelterInformationDto> getShelterInformationList(Long localgovinfoid, Long trackdataid) throws ParseException {
		List<ShelterInformationDto> shelterInformationDtoList = new ArrayList<ShelterInformationDto>();

		// メニューテーブル情報に避難所と福祉避難所など、複数テーブルID(レイヤ)を設定すると、リスト表示と発信内容で不整合がおきる
		// 公共情報コモンズ避難所情報からテーブルIDを取得する
		List<PubliccommonsReportShelterInfo> publiccommonsReportShelterInfoList = new ArrayList<PubliccommonsReportShelterInfo>();
		publiccommonsReportShelterInfoList = publiccommonsReportShelterInfoService.findByLocalgovInfoIdList(localgovinfoid);

		for (PubliccommonsReportShelterInfo publiccommonsReportShelterInfo : publiccommonsReportShelterInfoList) {
			TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(publiccommonsReportShelterInfo.tablemasterinfoid, trackdataid);
			// eコミマップのレイヤ：trackdataidは無い
			if (ttbl != null) {

				// 避難所区分(テーブル名)を取得
				TablemasterInfo tablemasterinfo = tablemasterInfoService.findById(ttbl.tablemasterinfoid);

				// 管理画面の避難所情報からマップを取得
				List<BeanMap> result;
				result = tableService.selectAll(ttbl.tablename);
				for (BeanMap map : result) {
					if (publiccommonsReportShelterInfo == null) continue;

					// マップに基づいて避難所情報の各項目を取り出す
					ShelterInformationDto shelterInformationDto = new ShelterInformationDto();
					setShelterInformation(publiccommonsReportShelterInfo, map, shelterInformationDto, ttbl.tablename, tablemasterinfo.name);
					shelterInformationDtoList.add(shelterInformationDto);
				}
			}
		}

		// ここで返したリストが発信画面に表示され、送信した時に発信される内容にもなる
		return shelterInformationDtoList;
	}

	/**
	 * 避難勧告情報リストを取得
	 * @param localgovinfoid 自治体ID
	 * @param trackdataid 記録データID
	 * @param tblmasterid テーブルマスタID
	 * @return 記録データ有:refugeInformationList 記録データ無:null
	 * @throws ParseException
	 */
	public List<RefugeInformationDto> getRefugeInformationList(Long localgovinfoid, Long trackdataid, Long tblmasterid) throws ParseException {
		//List<TablelistcolumnInfo> colinfoItems = tablelistcolumnInfoService.findByMenutableInfoId(mtbl.id);
		List<BeanMap> result;

		TracktableInfo ttbl = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tblmasterid, trackdataid);  // 29, 2

		// eコミマップのレイヤ：trackdataidは無い
		if (ttbl != null) {
			result = tableService.selectAll(ttbl.tablename);

			List<RefugeInformationDto> refugeInformationList = new ArrayList<RefugeInformationDto>();
			for (BeanMap map : result) {
				PubliccommonsReportRefugeInfo publiccommonsReportRefugeInfo = publiccommonsReportRefugeInfoService.findByLocalgovInfoId(localgovinfoid);
				if (publiccommonsReportRefugeInfo == null) {
					continue;
				}

				RefugeInformationDto refugeInformation = new RefugeInformationDto();
				setRefugeInformation(publiccommonsReportRefugeInfo, map, refugeInformation, ttbl.tablename);
				refugeInformationList.add(refugeInformation);
			}
			// ここで返したリストが発信画面に表示され、送信した時に発信される内容にもなる
			return refugeInformationList;
		}
		return null;
	}

	/**
	 * イベント情報リストを取得
	 * @param localgovinfoid 自治体ID
	 * @param trackdataid 記録データID
	 * @param tblmasterid テーブルマスタID
	 * @return 記録データ有:refugeInformationList 記録データ無:null
	 * @throws ParseException
	 */
	public List<EventInformationDto> getEventInformationList(Long localgovinfoid) throws ParseException {

		List<EventInformationDto> eventInformationList = new ArrayList<EventInformationDto>();
		List<PubliccommonsReportDataLastEvent> publiccommonsReportDataLastEventList = publiccommonsReportDataLastEventService.findByLocalgovInfoId(localgovinfoid);

		for (PubliccommonsReportDataLastEvent publiccommonsReportDataLastEvent : publiccommonsReportDataLastEventList) {
			EventInformationDto eventInformation = new EventInformationDto();
			eventInformation.eventArea = publiccommonsReportDataLastEvent.area;
			eventInformation.title = publiccommonsReportDataLastEvent.title;
			eventInformation.text = publiccommonsReportDataLastEvent.text;
			eventInformation.eventNotificationUri = publiccommonsReportDataLastEvent.notificationuri;
			eventInformation.eventFileUri = publiccommonsReportDataLastEvent.fileuri;
			eventInformation.mediaType = publiccommonsReportDataLastEvent.mediatype;
			eventInformation.mimeType = publiccommonsReportDataLastEvent.mimetype;
			eventInformation.disasterInformationType = publiccommonsReportDataLastEvent.disasterinformationtype;
			eventInformation.documentid = publiccommonsReportDataLastEvent.documentid;
			eventInformation.documentRevision = publiccommonsReportDataLastEvent.documentrevision;
			eventInformation.validDateTime = publiccommonsReportDataLastEvent.validdatetime;
			eventInformation.eventFrom = publiccommonsReportDataLastEvent.eventfrom;
			eventInformation.eventTo = publiccommonsReportDataLastEvent.eventto;
			eventInformation.eventFee = publiccommonsReportDataLastEvent.eventfee;
			eventInformation.eventFileCaption = publiccommonsReportDataLastEvent.filecaption;
			eventInformation.emailtitle = publiccommonsReportDataLastEvent.emailtitle;
			eventInformation.content = publiccommonsReportDataLastEvent.content;
			eventInformation.distributiontype = publiccommonsReportDataLastEvent.distributiontype;
			eventInformationList.add(eventInformation);
		}
		return eventInformationList;
	}

	/**
	 * お知らせ情報リストを取得
	 * @param localgovinfoid 自治体ID
	 * @param trackdataid 記録データID
	 * @param tblmasterid テーブルマスタID
	 * @return 記録データ有:refugeInformationList 記録データ無:null
	 * @throws ParseException
	 */
	public List<GeneralInformationDto> getGeneralInformationList(Long localgovinfoid) throws ParseException {
		List<GeneralInformationDto> generalInformationList = new ArrayList<GeneralInformationDto>();
		List<PubliccommonsReportDataLastGeneral> publiccommonsReportDataLastGeneralList = publiccommonsReportDataLastGeneralService.findByLocalgovInfoId(localgovinfoid);

		for (PubliccommonsReportDataLastGeneral publiccommonsReportDataLastGeneral : publiccommonsReportDataLastGeneralList) {
			GeneralInformationDto generalInformation = new GeneralInformationDto();
			generalInformation.area = publiccommonsReportDataLastGeneral.area;
			generalInformation.title = publiccommonsReportDataLastGeneral.title;
			generalInformation.text = publiccommonsReportDataLastGeneral.text;
			generalInformation.notificationUri = publiccommonsReportDataLastGeneral.notificationuri;
			generalInformation.fileUri = publiccommonsReportDataLastGeneral.fileuri;
			generalInformation.mediaType = publiccommonsReportDataLastGeneral.mediatype;
			generalInformation.division = publiccommonsReportDataLastGeneral.division;
			generalInformation.disasterInformationType = publiccommonsReportDataLastGeneral.disasterinformationtype;
			generalInformation.documentid = publiccommonsReportDataLastGeneral.documentid;
			generalInformation.documentRevision = publiccommonsReportDataLastGeneral.documentrevision;
			generalInformation.validDateTime = publiccommonsReportDataLastGeneral.validdatetime;
			generalInformation.distributiontype = publiccommonsReportDataLastGeneral.distributiontype;
			generalInformation.fileCaption = publiccommonsReportDataLastGeneral.filecaption;
			generalInformation.emailtitle = publiccommonsReportDataLastGeneral.emailtitle;
			generalInformation.content = publiccommonsReportDataLastGeneral.content;
			generalInformationList.add(generalInformation);
		}
		return generalInformationList;
	}

	/**
	 * 避難所情報リストをセット
	 * @param publiccommonsReportShelterInfo 公共情報コモンズ避難所レイヤ情報
	 * @param map マップ
	 * @param shelterInformation 避難所情報
	 * @param tablename テーブル名
	 * @param shelterLayerName 避難所レイヤ名
	 * @throws ParseException
	 */
	private void setShelterInformation(PubliccommonsReportShelterInfo publiccommonsReportShelterInfo, BeanMap map, ShelterInformationDto shelterInformation, String tablename, String shelterLayerName) throws ParseException {
		// 避難所レイヤ名
		shelterInformation.shelterLayerName = shelterLayerName;
		// テーブル名
		shelterInformation.tablename = tablename;
		// 避難所名
		shelterInformation.shelterName = getVal(map, publiccommonsReportShelterInfo.attrshelter);
		// 地区
		shelterInformation.chikuName = getVal(map, publiccommonsReportShelterInfo.attrarea);
		// 住所
		shelterInformation.shelterAddress = getVal(map, publiccommonsReportShelterInfo.attraddress);
		// 電話番号
		shelterInformation.shelterPhone = getVal(map, publiccommonsReportShelterInfo.attrphone);
		// FAX
		shelterInformation.shelterFax = getVal(map, publiccommonsReportShelterInfo.attrfax);
		// 代表者氏名
		shelterInformation.shelterStaff = getVal(map, publiccommonsReportShelterInfo.attrstaff);
		// 開設状況
		shelterInformation.shelterStatus = getVal(map, publiccommonsReportShelterInfo.attrstatus);
		//shelterInformation.shelterStatus = "閉鎖";

		convertShelterStatus(shelterInformation);		// 変換(1回以上変換してはならないためgetterは使わない)
		// 収容定員数
		shelterInformation.shelterCapacity = getVal(map, publiccommonsReportShelterInfo.attrcapacity);
		// 開設日時
		if (Constants.SHELTER_OPEN().equals(shelterInformation.shelterStatus)) {		// 開設時のみセット
			String val = getVal(map, publiccommonsReportShelterInfo.attrsetuptime);
			try{shelterInformation.setupTime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(val).getTime());
			} catch (Exception e) {shelterInformation.setupTime = null;}
		}
		// 閉鎖日時
		if (Constants.SHELTER_CLOSE().equals(shelterInformation.shelterStatus) ) {		// 閉鎖時のみセット
			String val = getVal(map, publiccommonsReportShelterInfo.attrclosetime);
			try{shelterInformation.closeTime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(val).getTime());
			} catch (Exception e) {shelterInformation.closeTime = null;}
		}
		// 種別
		shelterInformation.type = getVal(map, publiccommonsReportShelterInfo.attrtype);
		convertShelterType(shelterInformation);		// 変換(1回以上変換してはならないためgetterは使わない)
		// 座標
		shelterInformation.circle = getVal(map, publiccommonsReportShelterInfo.attrcircle);
		convertShelterCircle(shelterInformation);	// 変換

		// 未開設と閉鎖は設定しない（総数はここでセットした数の合計になるので対応不要）
		if (Constants.SHELTER_OPEN().equals(shelterInformation.shelterStatus) || Constants.SHELTER_FULLTIME().equals(shelterInformation.shelterStatus)) {		// 開設と常設時のみセット
			// 避難人数
			shelterInformation.headCount = getVal(map, publiccommonsReportShelterInfo.attrheadcount);
			// 避難人数(うち自主避難)
			shelterInformation.headCountVoluntary = getVal(map, publiccommonsReportShelterInfo.attrheadcountvoluntary);
			// 避難世帯数
			shelterInformation.houseHolds = getVal(map, publiccommonsReportShelterInfo.attrhouseholds);
			// 避難世帯数(うち自主避難)
			shelterInformation.houseHoldsVoluntary = getVal(map, publiccommonsReportShelterInfo.attrhouseholdsvoluntary);
		} else {
			shelterInformation.headCount = "";
			shelterInformation.headCountVoluntary = "";
			shelterInformation.houseHolds = "";
			shelterInformation.houseHoldsVoluntary = "";
		}
	}

	/**
	 * 本システムの種別を公共情報コモンズに対応した名称に変換します
	 * @param shelterInformation 避難所情報
	 */
	public void convertShelterType(ShelterInformationDto shelterInformation) {
		String type = shelterInformation.type;
		String typeDetail = "";

		// レイヤー名が避難所
		if (Constants.SHELTER_TYPE_SHELTER().equals(shelterInformation.shelterLayerName)) {
			if (StringUtil.isEmpty(type)) {type = lang.__("Shelter");}																				// 種別「空」or「NULL」の場合
			else if (Constants.SHELTER_TYPE_EXTRA().equals(type)) {type = lang.__("Temporary shelter<!--2-->");}									// 種別「臨時避難所」の場合
			else if (Constants.SHELTER_TYPE_WIDE().equals(type)) {type = lang.__("Evacuation district: No opening measures");}				// 種別「広域避難場所」の場合
			else if (Constants.SHELTER_TYPE_TEMPORARY().equals(type)) {type = lang.__("Temporary shelter: No open request");}	// 種別「一時避難場所」の場合
			else { typeDetail=type; type = lang.__("Shelter");}																						// それ以外の場合、種別詳細に本システムにおける種別を、種別に避難所をセット

		// レイヤー名が緊急避難場所
		} else if (Constants.SHELTER_TYPE_URGENT().equals(shelterInformation.shelterLayerName)) {
			if (StringUtil.isEmpty(type)) {type = lang.__("Temporary shelter: No open request");}												// 種別「空」or「NULL」の場合
			else if (Constants.SHELTER_TYPE_WIDE().equals(type)) {type = lang.__("Evacuation district: No opening measures");}				// 種別「広域避難場所」の場合
			else if (Constants.SHELTER_TYPE_TEMPORARY().equals(type)) {type = lang.__("Temporary shelter: No open request");}	// 種別「一時避難場所」の場合
			else { typeDetail=type; type = lang.__("Temporary shelter: No open request");}															// それ以外の場合、種別詳細に本システムにおける種別を、種別に一時避難場所:開設措置なしをセット

		// レイヤー名が福祉避難所
		} else if (Constants.SHELTER_TYPE_WELFARE().equals(shelterInformation.shelterLayerName)) {
			typeDetail=lang.__("Welfare shelter");
			type = lang.__("Shelter");

		// レイヤー名がみなし避難所
		} else if (Constants.SHELTER_TYPE_REGARD().equals(shelterInformation.shelterLayerName)) {
			typeDetail=lang.__("Informal shelter");
			type = lang.__("Shelter");
		}
		shelterInformation.type = type;
		shelterInformation.typeDetail = typeDetail;
	}

	/**
	 * 本システムの座標を公共情報コモンズに対応した様式に変換します
	 * @param shelterInformation 避難所情報
	 */
	public void convertShelterCircle(ShelterInformationDto shelterInformation) {
		String circle = shelterInformation.circle;
		if (StringUtils.isNotEmpty(circle)) {
			// ()を除去
			circle = circle.replaceAll("\\(", "");
			circle = circle.replaceAll("\\)", "");

			// 半径0固定を付与
			circle = circle.concat(",0");

			shelterInformation.circle = circle;
		}
	}

	/**
	 * 本システムの開設状況を公共情報コモンズに対応した名称に変換します
	 * この変換を2回以上実行すると開設が不明に変換されるなど不備が生じます
	 * @param shelterInformation 避難所情報
	 */
	public void convertShelterStatus(ShelterInformationDto shelterInformation) {
		String shelterStatus = lang.__("Unknown");
		if (Constants.SHELTER_ESTABLISHMENT_INDICATE_NAME().equals(shelterInformation.shelterStatus)) {
			shelterStatus = Constants.SHELTER_NOESTABLISH();
		} else if (Constants.SHELTER_ESTABLISHMENT_COMPLETION_NAME().equals(shelterInformation.shelterStatus)) {
			shelterStatus = Constants.SHELTER_OPEN();
		} else if (Constants.SHELTER_ESTABLISHMENT_IMPOSSIBLE_NAME().equals(shelterInformation.shelterStatus)) {
			shelterStatus = Constants.SHELTER_NOESTABLISH();
		} else if (Constants.SHELTER_ESTABLISHMENT_NOT_NAME().equals(shelterInformation.shelterStatus)) {
			shelterStatus = Constants.SHELTER_NOESTABLISH();
		} else if (Constants.SHELTER_CLOSE_NAME().equals(shelterInformation.shelterStatus)) {
			shelterStatus = Constants.SHELTER_CLOSE();
		} else if (Constants.SHELTER_ESTABLISHMENT_FULLTIME_NAME().equals(shelterInformation.shelterStatus)) {
			shelterStatus = Constants.SHELTER_FULLTIME();
		// 緊急避難場所は常設で固定
		} else if (Constants.SHELTER_TYPE_URGENT().equals(shelterInformation.shelterLayerName)) {
			shelterStatus = Constants.SHELTER_FULLTIME();
		}
		shelterInformation.shelterStatus = shelterStatus;
	}

	/**
	 * 避難勧告情報をセット
	 * @param publiccommonsReportRefugeInfo 公共情報コモンズ避難勧告レイヤ情報
	 * @param map マップ
	 * @param refugeInformation 避難勧告情報
	 * @throws ParseException
	 */
	private void setRefugeInformation(PubliccommonsReportRefugeInfo publiccommonsReportRefugeInfo, BeanMap map, RefugeInformationDto refugeInformation, String tablename) throws ParseException {
		// 地区名
		refugeInformation.chikuName = getVal(map, publiccommonsReportRefugeInfo.attrarea);
		// 発令区分
		refugeInformation.hatureiKbn = getVal(map, publiccommonsReportRefugeInfo.attrorderstatus);
		// 発令日時
		String val = getVal(map, publiccommonsReportRefugeInfo.attrordertime);
		refugeInformation.hatureiDateTime = StringUtil.isBlank(val) ? null : new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(val).getTime());
		// 対象世帯数
		val = getVal(map, publiccommonsReportRefugeInfo.attrhouseholds);

		// 解除以外の場合のみ
		if (!Constants.ISSUE_CANCEL_NAME().equals(refugeInformation.hatureiKbn)) {
				try {
					refugeInformation.targetHouseholds = StringUtil.isBlank(val) ? null : new Integer(val);
				} catch (NumberFormatException e) {e.printStackTrace();}
				// 人数
				val = getVal(map, publiccommonsReportRefugeInfo.attrpeople);
				try {
					refugeInformation.people = StringUtil.isBlank(val) ? null : new Integer(val);
				} catch (NumberFormatException e) {e.printStackTrace();}
		}
		// テーブル名
		refugeInformation.tablename = tablename;
	}

	/**
	 * 内容の取得
	 * @param map マップ
	 * @param attrid テーブル項目名
	 * @return 文字列
	 */
	private static String getVal(BeanMap map, String attrid) {
		return map.get(attrid) != null ? map.get(attrid).toString() : "";
	}

	/**
	 * 緊急速報メール送信
	 * @param title タイトル
	 * @param content 本文
	 * @param pCommonsSendDto コモンズ送信情報
	 * @throws JAXBException
	 * @return true: 送信OK, false: 送信失敗
	 */
	public boolean sendUrgentMail(String title, String content, PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException {
		PubliccommonsReportData publiccommonsReportData = newPubliccommonsReportData(PubliccommonsSendType.URGENT_MAIL, pCommonsSendDto);

		// EDXL-DEを作成
		EDXLDistributionType edxlDistributionType = pcUrgentMailService.createEDXLDistribution(publiccommonsReportData, title, content, pCommonsSendDto);

		boolean docomo = true;
		boolean au = true;
		boolean softbank = true;
		pcUrgentMailService.setCarrier(edxlDistributionType, docomo, au, softbank);

		// DBに保存
		publiccommonsReportDataService.insert(publiccommonsReportData);

		// ファイル出力
		File file = pcEvacuationService.createFile(publiccommonsReportData);
		publiccommonsReportData.filename = file.getName();
		JAXBElement<?> jaxbElement = PublicCommonsUtils.edxlFactory.createEDXLDistribution(edxlDistributionType);
		try {
			PublicCommonsUtils.marshal(jaxbElement, file);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (JAXBException e) {
			throw e;
		}

		// 送信
		boolean result = send(publiccommonsReportData);
		if (result) {
			publiccommonsReportData.filename = file.getName();
			publiccommonsReportData.status = PublicCommonsSendStatus.SEND;
			publiccommonsReportData.sendtime = new Timestamp(System.currentTimeMillis());

		} else {
			publiccommonsReportData.status = PublicCommonsSendStatus.FAILED;
		}

		// DBに保存
		publiccommonsReportDataService.update(publiccommonsReportData);

		return result;
	}

	/**
	 * メディア送信（避難勧告/避難指示）
	 * @param noticemailDataItems 通知履歴リスト
	 * @param refugeInformationList 避難情報リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @return true: 送信OK, false: 送信失敗
	 */
	public boolean sendMediaEvacuationOrder(PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException {
		PubliccommonsReportData publiccommonsReportData = newPubliccommonsReportData(PubliccommonsSendType.EVACUATION_ORDER, pCommonsSendDto);
		EDXLDistributionType edxlDistributionType = pcEvacuationService.createEDXLDistributionType(publiccommonsReportData, pCommonsSendDto);

		Long pcommonsreportdataid = sendMedia(publiccommonsReportData, edxlDistributionType, pCommonsSendDto);

		// 送信成功時
		if (pcommonsreportdataid != null) {
			// 公共情報コモンズ避難勧告／避難指示最終送信履歴管理テーブルに履歴登録
			for (RefugeInformationDto refugeInformation : pCommonsSendDto.refugeInformationList) {
				// 発令区分が空のものは対象外
				if (StringUtil.isNotEmpty(refugeInformation.hatureiKbn)) {
					PubliccommonsReportDataLastRefuge publiccommonsReportDataLastRefuge = new PubliccommonsReportDataLastRefuge();

					if(Constants.ISSUE_CANCEL_NAME().equals(refugeInformation.hatureiKbn)){
						publiccommonsReportDataLastRefuge.hatureikbn = refugeInformation.lasthatureiKbn;
						publiccommonsReportDataLastRefuge.issueorlift = EnumTypeIssueOrLift.解除.toString();
					}else{
						publiccommonsReportDataLastRefuge.hatureikbn = refugeInformation.hatureiKbn;
						publiccommonsReportDataLastRefuge.issueorlift = EnumTypeIssueOrLift.発令.toString();
					}

					publiccommonsReportDataLastRefuge.pcommonsreportdataid = pcommonsreportdataid;				// 公共情報コモンズデータID
					publiccommonsReportDataLastRefuge.chikuname = refugeInformation.chikuName;						// 地区
					publiccommonsReportDataLastRefuge.people = refugeInformation.people;									// 人数
					publiccommonsReportDataLastRefuge.targethouseholds = refugeInformation.targetHouseholds;	// 対象世帯数
					publiccommonsReportDataLastRefuge.hatureidatetime = refugeInformation.hatureiDateTime;		// 発令日時
					publiccommonsReportDataLastRefugeService.insert(publiccommonsReportDataLastRefuge);
				}
			}

			return true;
		// 送信失敗時
		} else {
			return false;
		}
	}

	/**
	 * メディア送信（避難所）
	 * @param shelterInformationList 避難所情報リスト
	 * @param PCommonsSendDto コモンズ送信情報
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @return true: 送信OK, false: 送信失敗
	 */
	public boolean sendMediaShelter(List<ShelterInformationDto> shelterInformationList, PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException {
		pCommonsSendDto.shelterInformationList = shelterInformationList;
		PubliccommonsReportData publiccommonsReportData = newPubliccommonsReportData(PubliccommonsSendType.SHELTER, pCommonsSendDto);
		EDXLDistributionType edxlDistributionType = pcShelterService.createEDXLDistributionType(publiccommonsReportData, shelterInformationList, pCommonsSendDto);

		Long pcommonsreportdataid = sendMedia(publiccommonsReportData, edxlDistributionType, pCommonsSendDto);

		// 送信成功時
		if (pcommonsreportdataid != null) {
			// 公共情報コモンズ避難所最終送信履歴管理テーブルに履歴登録
			for (ShelterInformationDto shelterInformation : shelterInformationList) {
				PubliccommonsReportDataLastShelter publiccommonsReportDataLastShelter = new PubliccommonsReportDataLastShelter();
				publiccommonsReportDataLastShelter.pcommonsreportdataid = pcommonsreportdataid;		// 公共情報コモンズレポートデータID
				publiccommonsReportDataLastShelter.chikuname = shelterInformation.chikuName;				// 地区
				publiccommonsReportDataLastShelter.closetime = shelterInformation.closeTime;					// 閉鎖日時
				publiccommonsReportDataLastShelter.setuptime = shelterInformation.setupTime;				// 開設日時
				publiccommonsReportDataLastShelter.shelteraddress = shelterInformation.shelterAddress;	// 住所
				publiccommonsReportDataLastShelter.sheltercapacity = shelterInformation.shelterCapacity;	// 収容定員数
				publiccommonsReportDataLastShelter.shelterfax = shelterInformation.shelterFax;				// FAX
				publiccommonsReportDataLastShelter.sheltername = shelterInformation.shelterName;		// 避難所名
				publiccommonsReportDataLastShelter.shelterphone = shelterInformation.shelterPhone;		// 電話番号
				publiccommonsReportDataLastShelter.shelterstaff = shelterInformation.shelterStaff;			// 代表者氏名
				publiccommonsReportDataLastShelter.shelterstatus = shelterInformation.shelterStatus;		// 開設状況
				publiccommonsReportDataLastShelter.circle = shelterInformation.circle;								// 座標
				publiccommonsReportDataLastShelter.type = shelterInformation.type;								// 種別
				publiccommonsReportDataLastShelter.typedetail = shelterInformation.typeDetail;				// 種別詳細
				publiccommonsReportDataLastShelter.headcount = shelterInformation.headCount;				// 避難人数
				publiccommonsReportDataLastShelter.headcountvoluntary = shelterInformation.headCountVoluntary;// 避難人数(うち自主避難)
				publiccommonsReportDataLastShelter.households = shelterInformation.houseHolds;				// 避難世帯数
				publiccommonsReportDataLastShelter.householdsvoluntary = shelterInformation.houseHoldsVoluntary;// 避難世帯数(うち自主避難)
				publiccommonsReportDataLastShelterService.insert(publiccommonsReportDataLastShelter);
			}
			return true;
		// 送信失敗時
		} else {
			return false;
		}
	}

	/**
	 * メディア送信（被害情報）
	 * @param noticemailDataItems 通知履歴リスト
	 * @param damageInformationList 被害情報リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @return true: 送信OK, false: 送信失敗
	 */
	public boolean sendMediaDamageInformation(List<NoticemailData> noticemailDataItems, DamageInformationDto damageInformationDto, PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException {
		pCommonsSendDto.damageInformationDto = damageInformationDto;
		PubliccommonsReportData publiccommonsReportData = newPubliccommonsReportData(PubliccommonsSendType.DAMAGE_INFORMATION, pCommonsSendDto);
		EDXLDistributionType edxlDistributionType = pcDamageService.createEDXLDistributionType(publiccommonsReportData, damageInformationDto, pCommonsSendDto);

		Long pcommonsreportdataid = sendMedia(publiccommonsReportData, edxlDistributionType, pCommonsSendDto);

		// 送信成功時
		if (pcommonsreportdataid != null) {
			// 公共情報コモンズ被害情報最終送信履歴管理テーブルに履歴登録
			PubliccommonsReportDataLastDamage publiccommonsReportDataLastDamage = new PubliccommonsReportDataLastDamage();

			publiccommonsReportDataLastDamage.pcommonsreportdataid = pcommonsreportdataid;
			publiccommonsReportDataLastDamage.remarks = damageInformationDto.remarks;
			publiccommonsReportDataLastDamage.deadpeople = damageInformationDto.deadPeople;
			publiccommonsReportDataLastDamage.missingpeople = damageInformationDto.missingPeople;
			publiccommonsReportDataLastDamage.seriouslyinjuredpeople = damageInformationDto.seriouslyInjuredPeople;
			publiccommonsReportDataLastDamage.slightlyinjuredpeople = damageInformationDto.slightlyInjuredPeople;
			publiccommonsReportDataLastDamage.totalcollapsebuilding = damageInformationDto.totalCollapseBuilding;
			publiccommonsReportDataLastDamage.totalcollapsehousehold = damageInformationDto.totalCollapseHousehold;
			publiccommonsReportDataLastDamage.totalcollapsehuman = damageInformationDto.totalCollapseHuman;
			publiccommonsReportDataLastDamage.halfcollapsebuilding = damageInformationDto.halfCollapseBuilding;
			publiccommonsReportDataLastDamage.halfcollapsehousehold = damageInformationDto.halfCollapseHousehold;
			publiccommonsReportDataLastDamage.halfcollapsehuman = damageInformationDto.halfCollapseHuman;
			publiccommonsReportDataLastDamage.somecollapsebuilding = damageInformationDto.someCollapseBuilding;
			publiccommonsReportDataLastDamage.somecollapsehousehold = damageInformationDto.someCollapseHousehold;
			publiccommonsReportDataLastDamage.somecollapsehuman = damageInformationDto.someCollapseHuman;
			publiccommonsReportDataLastDamage.overinundationbuilding = damageInformationDto.overInundationBuilding;
			publiccommonsReportDataLastDamage.overinundationhousehold = damageInformationDto.overInundationHousehold;
			publiccommonsReportDataLastDamage.overinundationhuman = damageInformationDto.overInundationHuman;
			publiccommonsReportDataLastDamage.underinundationbuilding = damageInformationDto.underInundationBuilding;
			publiccommonsReportDataLastDamage.underinundationhousehold = damageInformationDto.underInundationHousehold;
			publiccommonsReportDataLastDamage.underinundationhuman = damageInformationDto.underInundationHuman;
			publiccommonsReportDataLastDamage.publicbuilding = damageInformationDto.publicBuilding;
			publiccommonsReportDataLastDamage.otherbuilding = damageInformationDto.otherBuilding;
			publiccommonsReportDataLastDamage.ricefieldoutflowburied = damageInformationDto.ricefieldOutflowBuried;
			publiccommonsReportDataLastDamage.ricefieldflood = damageInformationDto.ricefieldFlood;
			publiccommonsReportDataLastDamage.fieldoutflowburied = damageInformationDto.fieldOutflowBuried;
			publiccommonsReportDataLastDamage.fieldflood = damageInformationDto.fieldFlood;
			publiccommonsReportDataLastDamage.educationalfacilities = damageInformationDto.educationalFacilities;
			publiccommonsReportDataLastDamage.hospital = damageInformationDto.hospital;
			publiccommonsReportDataLastDamage.road = damageInformationDto.road;
			publiccommonsReportDataLastDamage.bridge = damageInformationDto.bridge;
			publiccommonsReportDataLastDamage.river = damageInformationDto.river;
			publiccommonsReportDataLastDamage.port = damageInformationDto.port;
			publiccommonsReportDataLastDamage.sedimentcontrol = damageInformationDto.sedimentControl;
			publiccommonsReportDataLastDamage.cleaningfacility = damageInformationDto.cleaningFacility;
			publiccommonsReportDataLastDamage.cliffcollapse = damageInformationDto.cliffCollapse;
			publiccommonsReportDataLastDamage.railwayinterruption = damageInformationDto.railwayInterruption;
			publiccommonsReportDataLastDamage.ship = damageInformationDto.ship;
			publiccommonsReportDataLastDamage.water = damageInformationDto.water;
			publiccommonsReportDataLastDamage.phone = damageInformationDto.phone;
			publiccommonsReportDataLastDamage.electric = damageInformationDto.electric;
			publiccommonsReportDataLastDamage.gas = damageInformationDto.gas;
			publiccommonsReportDataLastDamage.blockwalls_etc = damageInformationDto.blockWalls_Etc;
			publiccommonsReportDataLastDamage.suffererhousehold = damageInformationDto.suffererHousehold;
			publiccommonsReportDataLastDamage.suffererhuman = damageInformationDto.suffererHuman;
			publiccommonsReportDataLastDamage.firebuilding = damageInformationDto.fireBuilding;
			publiccommonsReportDataLastDamage.firedangerousgoods = damageInformationDto.fireDangerousGoods;
			publiccommonsReportDataLastDamage.otherfire = damageInformationDto.otherFire;
			publiccommonsReportDataLastDamage.publicscoolfacillities = damageInformationDto.publicScoolFacillities;
			publiccommonsReportDataLastDamage.agriculturefacilities = damageInformationDto.agricultureFacilities;
			publiccommonsReportDataLastDamage.publicengineeringfacilities = damageInformationDto.publicEngineeringFacilities;
			publiccommonsReportDataLastDamage.subtotaldamagefacilities = damageInformationDto.subtotalDamageFacilities;
			publiccommonsReportDataLastDamage.farmingdamage = damageInformationDto.farmingDamage;
			publiccommonsReportDataLastDamage.forestrydamage = damageInformationDto.forestryDamage;
			publiccommonsReportDataLastDamage.animaldamage = damageInformationDto.animalDamage;
			publiccommonsReportDataLastDamage.fisheriesdamage = damageInformationDto.fisheriesDamage;
			publiccommonsReportDataLastDamage.commerceandindustrydamage = damageInformationDto.commerceAndIndustryDamage;
			publiccommonsReportDataLastDamage.otherdamageother = damageInformationDto.otherDamageOther;
			publiccommonsReportDataLastDamage.totaldamage = damageInformationDto.totalDamage;
			publiccommonsReportDataLastDamage.schoolmount = damageInformationDto.schoolmount;
			publiccommonsReportDataLastDamage.farmmount = damageInformationDto.farmmount;
			publiccommonsReportDataLastDamage.subtotalotherdamage = damageInformationDto.subtotalOtherDamage;
			publiccommonsReportDataLastDamage.fireman1 = damageInformationDto.fireman1;
			publiccommonsReportDataLastDamage.fireman2 = damageInformationDto.fireman2;
			publiccommonsReportDataLastDamageService.insert(publiccommonsReportDataLastDamage);

			return true;
		// 送信失敗時
		} else {
			return false;
		}
	}

	/**
	 * メディア送信（イベント）
	 * @param noticemailDataItems 通知履歴リスト
	 * @param eventInformationDto イベント情報
	 * @param pCommonsSendDto コモンズ送信情報
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @return true: 送信OK, false: 送信失敗
	 * @throws ParseException
	 */
	public boolean sendMediaEventInformation(List<NoticemailData> noticemailDataItems, EventInformationDto eventInformationDto, PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException, ParseException {
		pCommonsSendDto.eventInformationDto = eventInformationDto;
		PubliccommonsReportData publiccommonsReportData = newPubliccommonsReportData(PubliccommonsSendType.EVENT, pCommonsSendDto);
		EDXLDistributionType edxlDistributionType = pcEventService.createEDXLDistributionType(publiccommonsReportData, eventInformationDto, pCommonsSendDto);

		Long pcommonsreportdataid = sendMedia(publiccommonsReportData, edxlDistributionType, pCommonsSendDto);

		// 送信成功時
		if (pcommonsreportdataid != null) {
			// 公共情報コモンズ避難勧告／避難指示最終送信履歴管理テーブルに履歴登録
			PubliccommonsReportDataLastEvent publiccommonsReportDataLastEvent = new PubliccommonsReportDataLastEvent();
			publiccommonsReportDataLastEvent.pcommonsreportdataid = pcommonsreportdataid;
			publiccommonsReportDataLastEvent.area = eventInformationDto.eventArea;		// 場所
			publiccommonsReportDataLastEvent.title = eventInformationDto.title;		// タイトル
			publiccommonsReportDataLastEvent.text = eventInformationDto.text;		// 本文
			publiccommonsReportDataLastEvent.notificationuri = eventInformationDto.eventNotificationUri;		// 告知URI
			publiccommonsReportDataLastEvent.fileuri = eventInformationDto.eventFileUri;		// ファイルURI
			publiccommonsReportDataLastEvent.filecaption = eventInformationDto.eventFileCaption;		// ファイルタイトル
			publiccommonsReportDataLastEvent.mediatype = eventInformationDto.mediaType;		// メディアタイプ
			publiccommonsReportDataLastEvent.mimetype = eventInformationDto.mimeType;	// MIMEタイプ
			publiccommonsReportDataLastEvent.documentid = publiccommonsReportData.documentId;		// documentID
			publiccommonsReportDataLastEvent.localgovinfoid = publiccommonsReportData.localgovinfoid;		// 自治体ID
			publiccommonsReportDataLastEvent.documentrevision = publiccommonsReportData.documentRevision.longValue();		// 版数
			publiccommonsReportDataLastEvent.disasterinformationtype = eventInformationDto.disasterInformationType;		// 情報識別区分
			publiccommonsReportDataLastEvent.validdatetime = eventInformationDto.validDateTime;		//希望公開終了日時
			publiccommonsReportDataLastEvent.distributiontype = eventInformationDto.distributiontype;		//更新種別
			publiccommonsReportDataLastEvent.eventfrom = eventInformationDto.eventFrom;		//開催開始日時
			publiccommonsReportDataLastEvent.eventto = eventInformationDto.eventTo;		//開催終了日時
			publiccommonsReportDataLastEvent.eventfee = eventInformationDto.eventFee;		//参加料金
			publiccommonsReportDataLastEvent.emailtitle = eventInformationDto.emailtitle;		//メールタイトル
			publiccommonsReportDataLastEvent.content = eventInformationDto.content;		//送信本文
			publiccommonsReportDataLastEventService.insert(publiccommonsReportDataLastEvent);

			return true;
		// 送信失敗時
		} else {
			return false;
		}
	}

	/**
	 * メディア送信（お知らせ）
	 * @param noticemailDataItems 通知履歴リスト
	 * @param generalInformationDto お知らせ情報
	 * @param pCommonsSendDto コモンズ送信情報
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @return true: 送信OK, false: 送信失敗
	 */
	public boolean sendMediaGeneralInformation(List<NoticemailData> noticemailDataItems, GeneralInformationDto generalInformationDto, PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException {
		pCommonsSendDto.generalInformationDto = generalInformationDto;
		PubliccommonsReportData publiccommonsReportData = newPubliccommonsReportData(PubliccommonsSendType.GENERAL_INFORMATION, pCommonsSendDto);
		EDXLDistributionType edxlDistributionType = pcGeneralService.createEDXLDistributionType(publiccommonsReportData, generalInformationDto, pCommonsSendDto);

		Long pcommonsreportdataid = sendMedia(publiccommonsReportData, edxlDistributionType, pCommonsSendDto);

		// 送信成功時
		if (pcommonsreportdataid != null) {
			// 公共情報コモンズ避難勧告／避難指示最終送信履歴管理テーブルに履歴登録
				PubliccommonsReportDataLastGeneral publiccommonsReportDataLastGeneral = new PubliccommonsReportDataLastGeneral();
				publiccommonsReportDataLastGeneral.pcommonsreportdataid = pcommonsreportdataid;
				publiccommonsReportDataLastGeneral.division = generalInformationDto.division;		// 分類
				publiccommonsReportDataLastGeneral.area = generalInformationDto.area;		// 場所
				publiccommonsReportDataLastGeneral.title = generalInformationDto.title;		// タイトル
				publiccommonsReportDataLastGeneral.text = generalInformationDto.text;		// 本文
				publiccommonsReportDataLastGeneral.notificationuri = generalInformationDto.notificationUri;		// 告知URI
				publiccommonsReportDataLastGeneral.fileuri = generalInformationDto.fileUri;		// ファイルURI
				publiccommonsReportDataLastGeneral.filecaption = generalInformationDto.fileCaption;		// ファイルタイトル
				publiccommonsReportDataLastGeneral.mediatype = generalInformationDto.mediaType;		// メディアタイプ
				publiccommonsReportDataLastGeneral.mimetype = generalInformationDto.mimeType;		// MIMEタイプ
				publiccommonsReportDataLastGeneral.documentid = publiccommonsReportData.documentId;		// documentID
				publiccommonsReportDataLastGeneral.localgovinfoid = publiccommonsReportData.localgovinfoid;		// 自治体ID
				publiccommonsReportDataLastGeneral.documentrevision = publiccommonsReportData.documentRevision.longValue();		// 版数
				publiccommonsReportDataLastGeneral.disasterinformationtype = generalInformationDto.disasterInformationType;		// 情報識別区分
				publiccommonsReportDataLastGeneral.validdatetime = generalInformationDto.validDateTime;		//希望公開終了日時
				publiccommonsReportDataLastGeneral.distributiontype = generalInformationDto.distributiontype;		//更新種別
				publiccommonsReportDataLastGeneral.emailtitle = generalInformationDto.emailtitle;		//メールタイトル
				publiccommonsReportDataLastGeneral.content = generalInformationDto.content;		//送信本文
				publiccommonsReportDataLastGeneralService.insert(publiccommonsReportDataLastGeneral);

			return true;
		// 送信失敗時
		} else {
			return false;
		}
	}

	/**
	 * メディア送信（災害対策本部設置状況）
	 * @param noticemailDataItems 通知履歴リスト
	 * @param antidisasterInformationList 避難情報リスト
	 * @param pCommonsSendDto コモンズ送信情報
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @return true: 送信OK, false: 送信失敗
	 * @throws ParseException
	 */
	public boolean sendMediaAntidisasterInformation(List<NoticemailData> noticemailDataItems, AntidisasterInformationDto antidisasterInformationDto, PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException, ParseException {

		pCommonsSendDto.antidisasterInformationDto = antidisasterInformationDto;
		PubliccommonsReportData publiccommonsReportData = newPubliccommonsReportData(PubliccommonsSendType.ANTIDISASTER_HEADQUARTER, pCommonsSendDto);
		EDXLDistributionType edxlDistributionType = pcAntidisasterService.createEDXLDistributionType(publiccommonsReportData, antidisasterInformationDto, pCommonsSendDto);

		Long pcommonsreportdataid = sendMedia(publiccommonsReportData, edxlDistributionType, pCommonsSendDto);

		// 送信成功時
		if (pcommonsreportdataid != null) {
			// 公共情報コモンズ災害対策本部設置状況最終送信履歴管理テーブルに履歴登録
			PubliccommonsReportDataLastAntidisaster publiccommonsReportDataLastAntidisaster = new PubliccommonsReportDataLastAntidisaster();

			if(Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(pCommonsSendDto.antidisasterInformationDto.antidisasterKbn)){
				publiccommonsReportDataLastAntidisaster.antidisasterkbn = pCommonsSendDto.antidisasterInformationDto.lastAntidisasterKbn;
				publiccommonsReportDataLastAntidisaster.issueorlift = EnumTypeStatus.解散.toString();
			}else{
				publiccommonsReportDataLastAntidisaster.antidisasterkbn = pCommonsSendDto.antidisasterInformationDto.antidisasterKbn;
				publiccommonsReportDataLastAntidisaster.issueorlift = EnumTypeStatus.設置.toString();
			}

			publiccommonsReportDataLastAntidisaster.pcommonsreportdataid = pcommonsreportdataid;
			publiccommonsReportDataLastAntidisaster.hatureidatetime =  pCommonsSendDto.antidisasterInformationDto.hatureiDateTime;
			publiccommonsReportDataLastAntidisaster.name = pCommonsSendDto.antidisasterInformationDto.name;
			publiccommonsReportDataLastAntidisasterService.insert(publiccommonsReportDataLastAntidisaster);

			return true;
		// 送信失敗時
		} else {
			return false;
		}
	}

	/**
	 * メディア送信
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @param edxlDistributionType edxl更新種別
	 * @param pCommonsSendDto コモンズ送信情報
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @return true: 送信OK, false: 送信失敗
	 */
	private Long sendMedia(PubliccommonsReportData publiccommonsReportData, EDXLDistributionType edxlDistributionType, PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException {
		// DBに保存
		publiccommonsReportDataService.insert(publiccommonsReportData);

		// ファイル出力
		File file = pcEvacuationService.createFile(publiccommonsReportData);
		JAXBElement<?> jaxbElement = PublicCommonsUtils.edxlFactory.createEDXLDistribution(edxlDistributionType);
		try {
			PublicCommonsUtils.marshal(jaxbElement, file);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (JAXBException e) {
			throw e;
		}

		// 送信
		Long result = null;
		if (send(publiccommonsReportData)) {
			publiccommonsReportData.filename = file.getName();																										// XMLファイル名
			publiccommonsReportData.status = PublicCommonsSendStatus.SEND;																			// 送信結果
			publiccommonsReportData.sendtime = new Timestamp(System.currentTimeMillis());														// 送信日時
			result = publiccommonsReportData.id;
		} else {
			publiccommonsReportData.status = PublicCommonsSendStatus.FAILED;
		}

		// DBに保存
		publiccommonsReportDataService.update(publiccommonsReportData);
		return result;
	}

	/**
	 * 公共情報コモンズ送信データを作成
	 * @param category 情報種別
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return 公共情報コモンズ送信データ
	 */
	private PubliccommonsReportData newPubliccommonsReportData(String category, PCommonsSendDto pCommonsSendDto) {
		PubliccommonsReportData publiccommonsReportData = new PubliccommonsReportData();
		// 発信履歴を取得
		List<PubliccommonsReportData> pReportDataList = new ArrayList<PubliccommonsReportData>();
		// 検索条件が異なるため注意
		if (PubliccommonsSendType.SHELTER.equals(category)) {		//避難所
			pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.SHELTER, pCommonsSendDto.trackdataid, null, "");
		}else if (PubliccommonsSendType.EVACUATION_ORDER.equals(category)) {	//避難勧告・避難指示
			pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.EVACUATION_ORDER, pCommonsSendDto.trackdataid, null, "");
		}else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(category)) {		//被害情報
			pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.DAMAGE_INFORMATION, null, pCommonsSendDto.localgovinfoid, "");
		}else if (PubliccommonsSendType.EVENT.equals(category)) {	//イベント情報
			pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.EVENT, null, pCommonsSendDto.localgovinfoid, "");
		}else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(category)) {	//お知らせ情報
			pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.GENERAL_INFORMATION, null, pCommonsSendDto.localgovinfoid, "");
		}else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(category)) {	//災害対策歩武設置状況
			pReportDataList = publiccommonsReportDataService.findByReportDataWithLast(PubliccommonsSendType.ANTIDISASTER_HEADQUARTER, pCommonsSendDto.trackdataid, null, "");
		}

		// 見出し(edxlde:contentDescriptionとpcx_ib:Text)
		pCommonsSendDto.contentDescription = getContentDescriptionText(category, pReportDataList, pCommonsSendDto);

		// 送信システムIDとディストリビューションID
		pCommonsSendDto.senderId = getSenderId(pCommonsSendDto.localgovinfoid);
		pCommonsSendDto.distributionId = getDistributionId(pCommonsSendDto.localgovinfoid);

		// documentIDシリアルと版番号
	    final String TYPE_NEW = "new"; 											// documentID新規付番(状態遷移型/お知らせ型共通)※現在の記録データID(災害)をキーにして取得したシリアルに加算
	    final String TYPE_CONTINUE_TRANS = "continue_trans"; 		// documentID継続(状態遷移型用)※現在の記録データID(災害)をキーにして取得したシリアルに加算
	    final String TYPE_CONTINUE_INFO = "continue_info";				// documentID継続(お知らせ型用)※画面で指定されたdocumentIDをキーにして取得したシリアルに加算、災害が立っているとは限らないため
	    String documentIdType = "";
		int maxDocumentIdSerial = publiccommonsReportDataService.getMaxDocumentIdSerial(pCommonsSendDto.trackdataid, category);
		int documentIdSerial = maxDocumentIdSerial + 1;
		int documentRevision = 1;
		String documentId = "";

		// ライフサイクル終了後でも、同一災害の場合、documentIDは継続する
		//checkExpiryDate(noticemailDataItems, pCommonsSendDto.localgovinfoid);

		// 前回が取消発信
		if ("cancelSend".equals(pCommonsSendDto.distributiontypelast)) {
			documentIdType = TYPE_NEW;
		// イベント、お知らせ(お知らせ型)
		} else if(PubliccommonsSendType.EVENT.equals(category) || PubliccommonsSendType.GENERAL_INFORMATION.equals(category)){
			// 通常発信(更新)、訂正発信、取消発信
			if (!"0".equals(pCommonsSendDto.documentId) || StringUtil.isEmpty(pCommonsSendDto.documentId)) {
				documentIdType = TYPE_CONTINUE_INFO;
			// 通常発信(新規)
			} else {
				documentIdType = TYPE_NEW;
			}
		// 緊急速報メール
		} else if(PubliccommonsSendType.URGENT_MAIL.equals(category)){
			documentIdType = TYPE_NEW;
		// 避難勧告・避難指示、避難所、被害情報(状態遷移型)
		} else {
			// 通常発信(更新)、訂正発信、取消発信
			if(0 < maxDocumentIdSerial) {
				documentIdType = TYPE_CONTINUE_TRANS;
			// 通常発信(新規)
			} else {
				documentIdType = TYPE_NEW;
			}
		}
		// documentID新規付番(状態遷移型/お知らせ型共通)
		if (TYPE_NEW.equals(documentIdType)) {
			documentRevision = 1;
			documentIdSerial = maxDocumentIdSerial + 1;
			documentId = getDocumentId(pCommonsSendDto.localgovinfoid, pCommonsSendDto.trackdataid, category, documentIdSerial);
		// documentID継続(状態遷移型用)
		} else if (TYPE_CONTINUE_TRANS.equals(documentIdType)) {
			List<PubliccommonsReportData> revisionDatas = publiccommonsReportDataService.findRevisionDatas(pCommonsSendDto.trackdataid, category, String.valueOf(maxDocumentIdSerial));
			int maxDocumentRevision = publiccommonsReportDataService.getMaxDocumentRevision(revisionDatas);
			documentRevision = maxDocumentRevision + 1;
			documentIdSerial = maxDocumentIdSerial;
			documentId = getDocumentId(pCommonsSendDto.localgovinfoid, pCommonsSendDto.trackdataid, category, documentIdSerial);
		// documentID継続(お知らせ型用)
		} else if (TYPE_CONTINUE_INFO.equals(documentIdType)) {
			if (StringUtil.isEmpty(pCommonsSendDto.documentRevision)) pCommonsSendDto.documentRevision = "0";
			documentRevision = Integer.valueOf(pCommonsSendDto.documentRevision) + 1;
			documentIdSerial = publiccommonsReportDataService.getMaxGeneralDocumentIdSerial(pCommonsSendDto.trackdataid, pCommonsSendDto.documentId);
			documentId = pCommonsSendDto.documentId;
		}

		pCommonsSendDto.documentRevision = Integer.toString(documentRevision);																	// 版数
		pCommonsSendDto.documentId = documentId;																												// ドキュメントID
		publiccommonsReportData.trackdataid = pCommonsSendDto.trackdataid;																		// 記録データID
	    publiccommonsReportData.localgovinfoid = pCommonsSendDto.localgovinfoid;																	// 自治体ID
		publiccommonsReportData.category = category;																												// カテゴリ
		publiccommonsReportData.distributionType = pCommonsSendDto.distributiontype;															// 更新種別
		publiccommonsReportData.distributionId = pCommonsSendDto.distributionId;																	// ディストリビューションID
		publiccommonsReportData.filename = "";																														// XMLファイル名
		publiccommonsReportData.status = Constants.PublicCommonsSendStatus.UNSENT;															// 送信状況
		publiccommonsReportData.documentRevision = documentRevision;																					// 版番号
		publiccommonsReportData.documentIdSerial = documentIdSerial;																					// documentID連番(ex. 90)
		publiccommonsReportData.documentId = documentId;																									// documentID(ex. sanjo.jp-2-EVENT-90)
		publiccommonsReportData.complementaryinfo = pCommonsSendDto.complementaryInfo;												// 補足情報
		publiccommonsReportData.contentdescription = pCommonsSendDto.contentDescription;													// 見出し

		// 初版作成日時(pcx_ib:FirstCreateDateTime) ※documentIDが確定してからでないと探せないため注意
		Timestamp startsendtime;
		if (pCommonsSendDto.firstcreatedatetime != null) {
			// ここを通る時は取消発信
			startsendtime = pCommonsSendDto.firstcreatedatetime;
		} else {
			startsendtime = getStartSendTime(pReportDataList, publiccommonsReportData.documentId);
			// 初版作成日時がない場合、この発信が初版なので現在日時をセット
			if (startsendtime == null) startsendtime = pCommonsSendDto.createdatetime;
		}

		publiccommonsReportData.createtime = pCommonsSendDto.createdatetime;																	// 作成日時
		publiccommonsReportData.startsendtime = startsendtime;																								// 初版作成日時
		publiccommonsReportData.reporttime = pCommonsSendDto.reporttime;																			// 公式発表日時
	    publiccommonsReportData.registtime =pCommonsSendDto.createdatetime;																	// 登録日時
	    publiccommonsReportData.targetdatetime =pCommonsSendDto.targetdatetime;																// 希望公開開始日時
		publiccommonsReportData.personresponsible = pCommonsSendDto.personResponsible;													// 発表組織 担当者名
		publiccommonsReportData.organizationname = pCommonsSendDto.organizationName;													// 発表組織 組織名
		publiccommonsReportData.organizationcode = pCommonsSendDto.organizationCode;														// 発表組織 地方公共団体コード
		publiccommonsReportData.organizationdomainname = pCommonsSendDto.organizationDomainName;								// 発表組織 組織ドメイン
		publiccommonsReportData.officename = pCommonsSendDto.officeName;																		// 発表組織 部署名
		publiccommonsReportData.officenamekana = pCommonsSendDto.officeNameKana;															// 発表組織 部署名(カナ)
		publiccommonsReportData.officelocationarea = pCommonsSendDto.officeLocationArea;													// 発表組織 部署住所
		publiccommonsReportData.phone = pCommonsSendDto.phone;																						// 発表組織 部署電話番号
		publiccommonsReportData.fax = pCommonsSendDto.fax;																								// 発表組織 部署FAX番号
		publiccommonsReportData.email = pCommonsSendDto.email;																						// 発表組織 部署メールアドレス
		publiccommonsReportData.officedomainname = pCommonsSendDto.officeDomainName;													// 発表組織 部署ドメイン
		publiccommonsReportData.organizationnameeditorial = pCommonsSendDto.organizationNameEditorial;							// 作成組織 組織名
		publiccommonsReportData.organizationcodeeditorial = pCommonsSendDto.organizationCodeEditorial;								// 作成組織 地方公共団体コード
		publiccommonsReportData.organizationdomainnameeditorial = pCommonsSendDto.organizationDomainNameEditorial;		// 作成組織 組織ドメイン
		publiccommonsReportData.officenameeditorial = pCommonsSendDto.officeNameEditorial;												// 作成組織 部署名
		publiccommonsReportData.officenamekanaeditorial = pCommonsSendDto.officeNameKanaEditorial;									// 作成組織 部署名(カナ)
		publiccommonsReportData.officelocationareaeditorial = pCommonsSendDto.officeLocationAreaEditorial;								// 作成組織 部署住所
		publiccommonsReportData.phoneeditorial = pCommonsSendDto.phoneEditorial;																// 作成組織 部署電話番号
		publiccommonsReportData.faxeditorial = pCommonsSendDto.faxEditorial;																		// 作成組織 部署FAX番号
		publiccommonsReportData.emaileditorial = pCommonsSendDto.emailEditorial;																	// 作成組織 部署メールアドレス
		publiccommonsReportData.officedomainnameeditorial = pCommonsSendDto.officeDomainNameEditorial;							// 作成組織 部署ドメイン

		//希望公開終了日時
		if ( pCommonsSendDto.validdatetime != null && pCommonsSendDto.validdatetime.length() > 0 ){
			publiccommonsReportData.validDateTime = pCommonsSendDto.validdatetime;
		}

		return publiccommonsReportData;
	}

	/**
	 * 公共情報コモンズ発信履歴から初版作成日時を取得
	 * @param pReportDataList publiccommons_report_dataの発信履歴
	 * @param documentId documentID
	 * @return 初版作成日時
	 */
	private Timestamp getStartSendTime(List<PubliccommonsReportData> pReportDataList, String documentId){
		try {
			// 「発信履歴がある」場合、版番号が1の時の作成日時
			if (pReportDataList.size() > 0) {
				for (PubliccommonsReportData pReportData : pReportDataList) {
					if (pReportData.documentRevision == 1 && documentId.equals(pReportData.documentId)) return pReportData.createtime;
				}
			}
		} catch (Exception e) {
			// 発信できなくなる事態を防ぐため、例外は記録だけつける
			e.printStackTrace();
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 公共情報コモンズ発信履歴から見出しを取得
	 * @param pReportDataList publiccommons_report_dataの発信履歴
	 * @return 見出し
	 */
	private String getContentDescriptionText(String category, List<PubliccommonsReportData> pReportDataList, PCommonsSendDto pCommonsSendDto){
		StringBuffer leftFixed = new StringBuffer();						// 見出し(固定文・見出しの左)
		StringBuffer rightFixed = new StringBuffer();					// 見出し(固定文・見出しの右)
		StringBuffer centerVariable = new StringBuffer();			// 見出し(可変文・見出しの中央)
		final int TELOPMAXLEN = 255;										// コモンズビューアのテロップ最大文字数
		final int TITLE_TYPE_SABUN = 1;									// 前回発信内容との差分で見出し生成
		final int TITLE_TYPE_ALL = 2;										// 今回発信内容で見出し生成
		int titletype = TITLE_TYPE_ALL;									// 見出し生成方法

		try{
			// 取消発信の場合且つ、前回発信履歴がある場合
			if ("cancelSend".equals(pCommonsSendDto.distributiontype) && pReportDataList.size() > 0) {
				return lang.__("Cancel of {0}.", pReportDataList.get(0).contentdescription);	// 前回発信の見出しをそのまま適用
			}

			// 見出しの生成方法を取得
			if (PubliccommonsSendType.SHELTER.equals(category) || PubliccommonsSendType.EVACUATION_ORDER.equals(category)) {
				// 発信履歴がある場合
				if (pReportDataList.size() > 0) {
					// 前回が取消発信以外は差分見出し、前回が取消発信は今回分全件
					titletype = !"cancelSend".equals(pReportDataList.get(0).distributionType) ? TITLE_TYPE_SABUN : TITLE_TYPE_ALL;
				// 発信履歴がない場合
				} else {
					titletype = TITLE_TYPE_ALL;
				}
			} else {
				titletype = TITLE_TYPE_ALL;
			}

			// 現在日付を取得
			String nowString = new SimpleDateFormat(lang.__("MMM.d,yyyy<!--2-->"), lang.getLocale()).format(new Date());

			if (PubliccommonsSendType.SHELTER.equals(category)) {		//避難所
				// 見出し作成(差分)
				if (titletype == TITLE_TYPE_SABUN) {
					for (PubliccommonsReportData pReportData : pReportDataList) {
						// 「前回発信分」の場合、今回との差分を抽出する
						for (ShelterInformationDto thisData : pCommonsSendDto.shelterInformationList) {
							boolean checkflg = false;
							for (PubliccommonsReportDataLastShelter lastData  : pReportData.publiccommonsReportDataLastShelterList) {
								// 「今回発信分」と「前回発信分」の避難所名と状態が一致した場合、対象外とする
								if (thisData.shelterName.equals(lastData.sheltername) && thisData.shelterStatus.equals(lastData.shelterstatus)) {checkflg = true;break;}
							}
							// 差異があった分の避難所名と状態を追加
							if (checkflg == false) {
								centerVariable.append(",");centerVariable.append(thisData.shelterName);
								centerVariable.append(" ");centerVariable.append(PublicCommonsUtils.getSortMap().get(thisData.shelterStatus));
							}
						}
						// 「前回発信分」との差分抽出が終わったので検索終了
						break;
					}
				// 見出し作成(今回全件)
				} else if (titletype == TITLE_TYPE_ALL) {
					for (ShelterInformationDto thisData : pCommonsSendDto.shelterInformationList) {
						centerVariable.append(",");centerVariable.append(thisData.shelterName);
						centerVariable.append(" ");centerVariable.append(PublicCommonsUtils.getSortMap().get(thisData.shelterStatus));
					}
				}
				if (centerVariable.length() > 0) {centerVariable.delete(0, 1); centerVariable.insert(0, " (");centerVariable.append(")");}

				// 見出し作成
				leftFixed.append(lang.__("Shelter<!--2-->"));
				leftFixed.append(nowString);
				if (!"".equals(pCommonsSendDto.trackdataname)) {
					rightFixed.append(" ");rightFixed.append(lang.__("Shelter info induced by {0}", pCommonsSendDto.trackdataname));
				} else {
					rightFixed.append(" " + lang.__("Shelter info"));
				}
			} else if (PubliccommonsSendType.EVACUATION_ORDER.equals(category)) {	//避難勧告・避難指示
				// 見出し作成(差分)
				if (titletype == TITLE_TYPE_SABUN) {
					// 差分対象の前回発信を検索
					for (PubliccommonsReportData pReportData : pReportDataList) {
						// 「前回発信分」の場合、今回との差分を抽出する
						for (RefugeInformationDto thisData : pCommonsSendDto.refugeInformationList) {
							boolean checkflg = false;
							for (PubliccommonsReportDataLastRefuge lastData  : pReportData.publiccommonsReportDataLastRefugeList) {
								// 「今回発信分」に発令の文字はないため、解除以外は入れ替える
								String thisDataIssueorlift = Constants.ISSUE_HATUREI_NAME();
								if (Constants.ISSUE_CANCEL_NAME().equals(thisData.hatureiKbn)) thisDataIssueorlift = Constants.ISSUE_CANCEL_NAME();
								String thisDataHatureikbn = thisData.hatureiKbn;
								if (Constants.ISSUE_CANCEL_NAME().equals(thisData.hatureiKbn)) thisDataHatureikbn = thisData.lasthatureiKbn;

								// 「今回発信分」と「前回発信分」の地区名と発令区分が一致した場合、対象外とする
								if (thisData.chikuName.equals(lastData.chikuname) && thisDataIssueorlift.equals(lastData.issueorlift)  && thisDataHatureikbn.equals(lastData.hatureikbn)) {
									checkflg = true;break;
								}
							}
							// 差異があった分の地区名、発令区分、発令/解除を追加
							if (checkflg == false) {
								// 全域で発令区分がブランク以外の場合、または全域以外の場合(全域で発令区分がブランクは発信しないため)
								if ( (Constants.ALL_AREA().equals(thisData.chikuName) && !"".equals(thisData.hatureiKbn.trim())) ||
										!Constants.ALL_AREA().equals(thisData.chikuName)) {
									centerVariable.append(",");
									// 全域の場合
									if (Constants.ALL_AREA().equals(thisData.chikuName)) {
										centerVariable.append(thisData.chikuNameAllArea);
									} else {
										centerVariable.append(thisData.chikuName);
									}
									String kbn = Constants.ISSUE_CANCEL_NAME().equals(thisData.hatureiKbn) ? thisData.lasthatureiKbn + " " + Constants.ISSUE_CANCEL_NAME() : thisData.hatureiKbn + " " +Constants.ISSUE_HATUREI_NAME();
									centerVariable.append(" ");centerVariable.append(kbn);
								}
							}
						}
						// 「前回発信分」との差分抽出が終わったので検索終了
						break;
					}
					// 見出し作成(今回全件)
				} else if (titletype == TITLE_TYPE_ALL) {
					for (RefugeInformationDto thisData : pCommonsSendDto.refugeInformationList) {
						// 「今回発信分」に発令の文字はないため、解除以外は入れ替える
						String thisDataIssueorlift = Constants.ISSUE_HATUREI_NAME();
						if (Constants.ISSUE_CANCEL_NAME().equals(thisData.hatureiKbn)) thisDataIssueorlift = Constants.ISSUE_CANCEL_NAME();
						String thisDataHatureikbn = thisData.hatureiKbn;
						if (Constants.ISSUE_CANCEL_NAME().equals(thisData.hatureiKbn)) thisDataHatureikbn = thisData.lasthatureiKbn;

						if ( (Constants.ALL_AREA().equals(thisData.chikuName) && !"".equals(thisData.hatureiKbn.trim())) ||
								!Constants.ALL_AREA().equals(thisData.chikuName)) {
							centerVariable.append(",");
							// 全域の場合
							if (Constants.ALL_AREA().equals(thisData.chikuName)) {
								centerVariable.append(thisData.chikuNameAllArea);
							} else {
								centerVariable.append(thisData.chikuName);
							}
							centerVariable.append(" ");centerVariable.append(thisDataHatureikbn + " " + thisDataIssueorlift);
						}
					}
				}
				if (centerVariable.length() > 0) {centerVariable.delete(0, 1);centerVariable.insert(0, " (");centerVariable.append(")");}

				// 見出し作成
				leftFixed.append(lang.__("Evacuation advisory, instructions"));
				leftFixed.append(nowString);
				if (!"".equals(pCommonsSendDto.trackdataname)) {
					rightFixed.append(" ");rightFixed.append(lang.__("Evacuation advisory/order info induced by {0}", pCommonsSendDto.trackdataname));
				} else {
					rightFixed.append(" " + lang.__("Evacuation advisory, order info"));
				}
			}else if (PubliccommonsSendType.DAMAGE_INFORMATION.equals(category)) {		//被害情報
				leftFixed.append(lang.__("Damage info<!--2-->"));
				leftFixed.append(nowString);
				if (!"".equals(pCommonsSendDto.trackdataname)) {
					rightFixed.append(" ");rightFixed.append(lang.__("Damage info induced by {0}", pCommonsSendDto.trackdataname));
				} else {
					rightFixed.append(" " + lang.__("Damage info"));
				}
				if (!"".equals(pCommonsSendDto.damageInformationDto.reportno)) {rightFixed.append(MessageFormat.format(" " + lang.__("No. {0} report"), pCommonsSendDto.damageInformationDto.reportno));}
			}else if (PubliccommonsSendType.EVENT.equals(category)) {	//イベント情報
				leftFixed.append(pCommonsSendDto.eventInformationDto.title);
			}else if (PubliccommonsSendType.GENERAL_INFORMATION.equals(category)) {	//お知らせ情報
				leftFixed.append(pCommonsSendDto.generalInformationDto.title);
			}else if (PubliccommonsSendType.URGENT_MAIL.equals(category)) {	//緊急速報メール
				leftFixed.append(pCommonsSendDto.title);
			}else if (PubliccommonsSendType.ANTIDISASTER_HEADQUARTER.equals(category)) {	//災害対策本部設置状況
				leftFixed.append(lang.__("Disaster response HQ"));
				if(Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(pCommonsSendDto.antidisasterInformationDto.antidisasterKbn)){
					leftFixed.append(EnumTypeStatus.解散.toString());
				}else{
					leftFixed.append(EnumTypeStatus.設置.toString());
				}
				leftFixed.append(" " + nowString + " ");
				leftFixed.append(lang.__("According to {0}", pCommonsSendDto.trackdataname));
			}

			// コモンズビューアのテロップ最大文字数を越えた場合
			if (leftFixed.length() + centerVariable.length() + rightFixed.length() > TELOPMAXLEN) {
				// 見出し(可変文)がある場合
				if (centerVariable.length() > 0) {
					// 見出し(可変文)の最大文字数を算出
					int centerMaxLen = TELOPMAXLEN - leftFixed.length() -  rightFixed.length() - (" " + lang.__("Other<!--2-->") + ",()").length();
					if (centerMaxLen < 1) centerMaxLen = 0;

					// ()を除去
					centerVariable.delete(0, 2);
					centerVariable.delete(centerVariable.length()-1, centerVariable.length());

					// 最大文字数を越えはじめる区切要素まで入れ直す
					String centerAry[] = centerVariable.toString().split(",");
					centerVariable = new StringBuffer();
					for (String center : centerAry) {
						// 見出し(可変文)の最大文字数を越える場合は入れ直し終了
						if (centerMaxLen < centerVariable.length() + center.length()) break;
						centerVariable.append(",");
						centerVariable.append(center);
					}
					if (centerVariable.length() > 0) {
						// 先頭の,を除去
						centerVariable.delete(0, 1);
						centerVariable.insert(0, "(");
						centerVariable.append(" " + lang.__("Other<!--2-->") + ")");
					}
				}
			}

		} catch (Exception e) {
			// 発信できなくなる事態を防ぐため、例外は記録だけつける
			e.printStackTrace();
			logger.error("", e);
			logger.error(lang.__("Exception error occurred while creating the headline. Without interruption, transmission processing will continue."));
			logger.error("localgovermentid : " + pCommonsSendDto.localgovinfoid);
		}
		return leftFixed.toString() + centerVariable.toString() + rightFixed.toString();
	}

	/**
	 * 送信
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @return 送信結果
	 */
	private boolean send(PubliccommonsReportData publiccommonsReportData) {
		boolean successFlg = false;

		// 送信先情報を取得
		PubliccommonsSendToInfo sendToInto = getPubliccommonsSendToInfo(publiccommonsReportData);

		int retryMax = getRetryCount();
		int retryCount = 0;
		int resultCode = -1;
		while (resultCode == -1 && (retryCount<retryMax || retryCount==0) ) {
			// リトライ発信時は間隔を空ける
			if (retryCount > 0) waitRetry(retryCount);
			resultCode = sendAllNode(sendToInto, publiccommonsReportData);
			// 失敗した場合はリトライ送信
			retryCount++;
		}

		// 処理結果の判定
		switch (resultCode) {
			case RESULT_CODE_SUCCESS:
				successFlg = true;
				logger.info(lang.__("L-Alert notice") + " Normal End (Normal node) [DocumentId]" + publiccommonsReportData.documentId);
				break;
			case RESULT_CODE_USER_ERROR:
				logger.error(lang.__("L-Alert notice") + " Response Error (Normal node) [Code]" + Integer.toString(resultCode) + " [Message]" + lang.__("URI format of the message is invalid.") + " [DocumentId]" + publiccommonsReportData.documentId);
				break;
			case RESULT_CODE_SCHEMA_ERROR:
				logger.error(lang.__("L-Alert notice") + " Response Error (Normal node) [Code]" + Integer.toString(resultCode) + " [Message]" + lang.__("Input message not complied with schema.") + " [DocumentId]" + publiccommonsReportData.documentId);
				break;
			case -1:	//Excption時は出力しない
				break;
			case RESULT_CODE_SYSTEM_ERROR:
			default:
				logger.error(lang.__("L-Alert notice") + " Response Error (Normal node) [Code]" + Integer.toString(resultCode) + " [Message]" + lang.__("Another error occurred in L-Alert.") + " [DocumentId]" + publiccommonsReportData.documentId);
				break;
		}

		// 送信履歴を残す
		PubliccommonsSendHistoryData sendHistoryData = new PubliccommonsSendHistoryData();
		sendHistoryData.id = publiccommonsReportData.id;
		sendHistoryData.publiccommonsReportDataId = publiccommonsSendToInfoId;
		sendHistoryData.sendtime = new Timestamp(System.currentTimeMillis());
		sendHistoryData.success = successFlg;
		publiccommonsSendHistoryDataService.insert(sendHistoryData);

		return successFlg;
	}

	/**
	 * 全ノードに送信
	 * @param sendToInto 公共情報コモンズ発信先データ
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @return 送信結果
	 */
	private int sendAllNode(PubliccommonsSendToInfo sendToInto, PubliccommonsReportData publiccommonsReportData) {
		int resultCode = -1;
		int resultCodeBackup = -1;

		// 送信(通常ノード)
		try {
			resultCode = sendCommons(sendToInto.endpointUrl, sendToInto.username, sendToInto.password, publiccommonsReportData);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(lang.__("L-Alert notice") + " Exception Error (Normal node) [EndpointURL]" + sendToInto.endpointUrl + " [Username]" + sendToInto.username + " [DocumentId]" + publiccommonsReportData.documentId);
			logger.error("", e);
		}

		// 送信(バックアップノード)
		try {
			resultCodeBackup = sendCommons(sendToInto.endpointUrlBackup, sendToInto.usernameBackup, sendToInto.passwordBackup, publiccommonsReportData);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(lang.__("L-Alert notice") + " Exception Error (Backup node) [EndpointURLBackup]" + sendToInto.endpointUrlBackup + " [UsernameBackup]" + sendToInto.usernameBackup + " [DocumentId]" + publiccommonsReportData.documentId);
			logger.error("", e);
		}

		if (resultCode != -1) {	// 通常ノード成功
			return resultCode;
		} else if (resultCodeBackup != -1) {	// バックアップノードのみ成功
			return resultCodeBackup;
		} else {	// 両方のノードで送信失敗
			return -1;
		}
	}

	/**
	 * 公共情報コモンズにリトライ発信する時に間隔を空ける
	 * @param retryCount 現在のリトライ試行回数
	 * @return
	 */
	private void waitRetry(int retryCount) {
		if (retryCount <= 0) retryCount = 1;
	    try{
	    	// 1～10sの乱数を生成
	    	Random rnd = new Random();
	        int rand = (rnd.nextInt(10) + 1)  * 1000;	// +1sして0s * 1000を回避

			// リトライ間隔 = 現在のリトライ試行回数 × 60000ms + ランダムms
			int waittime = retryCount * 60000 + rand;

			// リトライ間隔が5分を超過した場合は、5分固定
			if (waittime > 300000) waittime = 300000;

			// リトライ待機
	    	Thread.sleep(waittime);
	    }catch(Exception e) {
	    	// 待機するだけの処理なのでExceptionは記録だけして無視
	    	e.printStackTrace();
			logger.error("", e);
		}
    	return;
	}

	/**
	 * 公共情報コモンズのリトライ回数を取得
	 * @return リトライ回数
	 */
	private int getRetryCount() {
		final String DEFAULT_VALUE = "3";

		InputStream is = getClass().getResourceAsStream("/SaigaiTask.properties");
		Properties prop = new Properties();
		try {
			prop.load(is);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("", e);
				}
			}
		}

		// デフォルト 3回
		try {
			return Integer.parseInt(prop.getProperty("PCOMMONS_RETRY_COUNT", DEFAULT_VALUE));

		} catch(NumberFormatException e) {
			e.printStackTrace();
			logger.error(lang.__("SaigaiTask.properties : PCOMMONS_RETRY_COUNT value is invalid."));
			return Integer.parseInt(DEFAULT_VALUE);
		}
	}

	/**
	 * 希望公開終了日時を経過しているか判定する
	 * @return 経過している true 経過していない false
	 * @throws ParseException
	 */
	public boolean checkValidDateTime(String validDataTime) throws ParseException{
		Date now = new Date();
		long nowtime = now.getTime();
		Date nowdate = new Date(nowtime);
		Locale.setDefault(Locale.JAPAN);
	    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    Date validDataTimeDate = dateFormat.parse(validDataTime);
		return nowdate.before(validDataTimeDate);
	}

	/**
	 * 渡された日付から90日後を日時を返す
	 * @return 90日後の日時
	 * @throws ParseException
	 */
	public static String getMaxValidDateTime(String date) throws ParseException {
		Date validDateTimeDate = DateFormat.getDateTimeInstance().parse(date);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		now.setTime(validDateTimeDate);
		now.add(Calendar.DATE, 90);
		validDateTimeDate = now.getTime();
		return dateFormat.format(validDateTimeDate);
	}

	/**
	 * 現在日時から48時間後を日時を返す
	 * @return 48時間後の日時
	 */
	public static String getValidDateTime() {
		// 48時間後固定を希望公開終了日時にセット
		Date now = new Date();
		long nowtime = now.getTime();
		long targettime = nowtime + (long)(48*60*60*1000);	// 48時間語固定
		Date targetdate = new Date(targettime);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(targetdate);
	}

	/**
	 * 48時間経過しているか確認
	 * @param noticemailDataItems 送信履歴
	 * @param localgovinfoid 自治体ID
	 * @return 48時間経過:true 48時間未経過:false
	 */
//	public boolean checkExpiryDate(List<NoticemailData> noticemailDataItems, long localgovinfoid){
//		PubliccommonsSendToInfo pcommonsSendToInfo;
//		boolean  elapsedTime = false;
//		if (noticemailDataItems.size() > 0) {
//			//前回の投稿日時
//			Timestamp sendtime = noticemailDataItems.get(0).sendtime;
//			Date lastSendData = new Date(sendtime.getTime());
//
//			//前回の投稿日時に設定した時間(documentValidHour)を追加
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(lastSendData);
//			pcommonsSendToInfo = publiccommonsSendToInfoService.search(localgovinfoid);
//			String validDateStr = Integer.toString(pcommonsSendToInfo.documentValidHour);
//			int validDateInt = Integer.parseInt(validDateStr);
////			cal.add(Calendar.SECOND, validDateInt);
//			cal.add(Calendar.HOUR, validDateInt);
//			lastSendData = cal.getTime();
//			//現在の投稿日時
//			Date newSendData = new Date();
//			elapsedTime = newSendData.after(lastSendData);
//		}
//		return elapsedTime;
//	}

	/**
	 * 避難所が全未開設か確認する
	 * @param shelterInformationList 避難所情報リスト
	 * @return 全未開設:true 全未開設でない:false
	 */
	public static boolean checkAllEstablishShelter(List<ShelterInformationDto> shelterInformationList){
		//全ての避難所が未開設の場合、発信できない
		for (ShelterInformationDto shelterInformationDto: shelterInformationList) {
			if (!Constants.SHELTER_NOESTABLISH().equals(shelterInformationDto.shelterStatus)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 避難所が全閉鎖か確認する
	 * @param shelterInformationList 避難所情報リスト
	 * @return 全閉鎖:true 全閉鎖でない:false
	 */
	public static boolean checkAllCloseShelter(List<ShelterInformationDto> shelterInformationList){
		//1箇所でも閉鎖以外のステータスが存在した場合、希望公開終了日時の入力欄は表示しない
		for (ShelterInformationDto shelterInformationDto: shelterInformationList) {
			if (!Constants.SHELTER_CLOSE().equals(shelterInformationDto.shelterStatus) &&
				!Constants.SHELTER_FULLTIME().equals(shelterInformationDto.shelterStatus) &&
				!Constants.SHELTER_NOESTABLISH().equals(shelterInformationDto.shelterStatus)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 避難勧告／避難指示が全解除か確認する
	 * @param shelterInformationList 避難所情報リスト
	 * @return 全閉鎖:true 全閉鎖でない:false
	 */
	public static boolean checkAllCloseRefuge(List<RefugeInformationDto> refugeInformationList){
		//1箇所でも閉鎖以外のステータスが存在した場合、希望公開終了日時の入力欄は表示しない
		for (RefugeInformationDto refugeInformationDto: refugeInformationList) {
			if (!Constants.ISSUE_CANCEL_NAME().equals(refugeInformationDto.hatureiKbn)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 本部設置状況が解散か確認する
	 * @param antidisasterInformationList 本部設置状況リスト
	 * @return 解散:true 解散でない:false
	 */
	public static boolean checkAllCloseAntidisaster(AntidisasterInformationDto antidisasterInformationDto){
		// 解散以外の場合、希望公開終了日時の入力欄は表示しない
		if (!Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(antidisasterInformationDto.antidisasterKbn)) {
			return false;
		}
		return true;
	}

	/**
	 * メディア送信（避難勧告/避難指示）自動発報用(主に気象庁XMLを受信した時のアラームから呼ばれます)
	 * @param refugeInformationList 避難情報リスト
	 * @param pCommonsSendDto コモンズ送信情報(記録データIDのみ必須指定)
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 * @return true: 送信OK, false: 送信失敗
	 */
	public boolean sendMediaEvacuationOrderAuto(List<RefugeInformationDto> refugeInformationList, PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException {
		// アラーム側で設定できない項目を補完
		PCommonsSendDto newDto = createPCommonsSendDtoAuto(pCommonsSendDto.trackdataid);
		newDto.categoryType = PubliccommonsSendType.EVACUATION_ORDER;

		// アラーム側で「訓練」指定されている場合、管理画面の設定より優先する(主にデモの時に訓練固定にする)
		if (StringUtil.isNotEmpty(pCommonsSendDto.statusValue)) newDto.statusValue = pCommonsSendDto.statusValue;

		// 全解除でない場合、希望公開終了日時は空
		if (!PublicCommonsService.checkAllCloseRefuge(refugeInformationList)) newDto.validdatetime = "";
		newDto.refugeInformationList = refugeInformationList;
		PublicCommonsUtils.convertAllArea(newDto.refugeInformationList);
		return sendMediaEvacuationOrder(newDto);
	}

	/**
	 * メディア送信（避難所）自動発報用(主に気象庁XMLを受信した時のアラームから呼ばれます)
	 * @param shelterInformationList 避難所情報リスト
	 * @param pCommonsSendDto コモンズ送信情報(記録データIDのみ必須指定)
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @return true: 送信OK, false: 送信失敗
	 */
	public boolean sendMediaShelterAuto(List<ShelterInformationDto> shelterInformationList, PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException {
		// アラーム側で設定できない項目を補完
		PCommonsSendDto newDto = createPCommonsSendDtoAuto(pCommonsSendDto.trackdataid);
		newDto.categoryType = PubliccommonsSendType.SHELTER;

		// アラーム側で「訓練」指定されている場合、管理画面の設定より優先する(主にデモの時に訓練固定にする)
		if (StringUtil.isNotEmpty(pCommonsSendDto.statusValue)) newDto.statusValue = pCommonsSendDto.statusValue;

		// 全閉鎖(常設除く)でない場合、希望公開終了日時は空
		if (!PublicCommonsService.checkAllCloseShelter(shelterInformationList)) newDto.validdatetime = "";

		return sendMediaShelter(shelterInformationList, newDto);
	}

	/**
	 * 緊急速報メール送信 自動発報用(主に気象庁XMLを受信した時のアラームから呼ばれます)
	 * @param pCommonsSendDto コモンズ送信情報(記録データID、タイトル、本文のみ必須指定)
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 * @return true: 送信OK, false: 送信失敗
	 */
	public boolean sendUrgentMaiAutol(PCommonsSendDto pCommonsSendDto) throws FileNotFoundException, JAXBException {
		// アラーム側で設定できない項目を補完
		PCommonsSendDto newDto = createPCommonsSendDtoAuto(pCommonsSendDto.trackdataid);
		newDto.categoryType = PubliccommonsSendType.URGENT_MAIL;

		// アラーム側で「訓練」指定されている場合、管理画面の設定より優先する(主にデモの時に訓練固定にする)
		if (StringUtil.isNotEmpty(pCommonsSendDto.statusValue)) newDto.statusValue = pCommonsSendDto.statusValue;

		return sendUrgentMail(pCommonsSendDto.title, pCommonsSendDto.description, newDto);
	}

	/**
	 * 避難勧告／避難指示全解除送信 自動発報用(主に災害対応を終了した時に呼ばれます)
	 * @param trackdataid 記録データID
	 * @return 成功:true 失敗:false
	 */
	public boolean sendAllCloseEvacuationOrder(Long trackdataid){
		// 指定された記録データIDに紐付く、自治体と災害に関して避難勧告／避難指示の全解除を発信する
		try{
			List<RefugeInformationDto> refugeInformationList = new ArrayList<RefugeInformationDto>();
			PCommonsSendDto pCommonsSendDto = createPCommonsSendDtoAuto(trackdataid);
			pCommonsSendDto.categoryType = PubliccommonsSendType.EVACUATION_ORDER;

			// 記録データIDが一致、取消発信以外、送信成功分のみ
			List<PubliccommonsReportData> publiccommonsReportDataList = publiccommonsReportDataService.findByReportDataByIdEvacuationOrder(trackdataid);

			if (publiccommonsReportDataList == null) {
				logger.info(lang.__("Evacuation advisory/order at the end of disaster, auto cancel transmission processing, it did not release because of no target "));
				return true;
			}

			boolean sendFlg = false;		// 送信フラグ
			Timestamp now = new Timestamp(System.currentTimeMillis());
			for (PubliccommonsReportData report : publiccommonsReportDataList){
				for (PubliccommonsReportDataLastRefuge last : report.publiccommonsReportDataLastRefugeList){
					// 前回、解除以外の発信が1つでもあれば送信する
					if (!Constants.ISSUE_CANCEL_NAME().equals(last.issueorlift)) sendFlg = true;

					// 送信内容をセット
					RefugeInformationDto refugeInformation = new RefugeInformationDto();
					refugeInformation.chikuName = last.chikuname;
					refugeInformation.hatureiDateTime = now;										// 現在日時
					refugeInformation.hatureiKbn = Constants.ISSUE_CANCEL_NAME();	// 解除固定
					refugeInformation.lasthatureiKbn = last.hatureikbn;						// 解除対象の発令区分
					refugeInformation.people = null;														// 解除はXML要素ごと削除が必要なためnull固定
					refugeInformation.targetHouseholds = null;										// 解除はXML要素ごと削除が必要なためnull固定
					refugeInformationList.add(refugeInformation);
				}
			}
			if (refugeInformationList != null && sendFlg) {
				pCommonsSendDto.refugeInformationList = refugeInformationList;
				sendMediaEvacuationOrder(pCommonsSendDto);
			} else if (sendFlg == false){
				logger.info(lang.__("Evacuation advisory/order at the end of disaster, auto cancel transmission processing, it did not release because of no target "));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("trackdataid : " + trackdataid);
			logger.error(lang.__("Evacuation advisory/order at the end of disaster, auto cancel transmission processing, failed"));
			return false;
		}
		return true;
	}

	/**
	 * 避難所全閉鎖送信 自動発報用(主に災害対応を終了した時に呼ばれます/常設と未開設の避難所は閉鎖しません)
	 * @param trackdataid 記録データID
	 * @return 成功:true 失敗:false
	 */
	public boolean sendAllCloseShelter(Long trackdataid){
		// 指定された記録データIDに紐付く、自治体と災害に関して避難所の全閉鎖を発信する
		try{
			List<ShelterInformationDto> shelterInformationList = new ArrayList<ShelterInformationDto>();
			PCommonsSendDto pCommonsSendDto = createPCommonsSendDtoAuto(trackdataid);
			pCommonsSendDto.categoryType = PubliccommonsSendType.SHELTER;

			// 記録データIDが一致、取消発信以外、送信成功分のみ
			List<PubliccommonsReportData> publiccommonsReportDataList = publiccommonsReportDataService.findByReportDataByIdShelter(trackdataid);

			if (publiccommonsReportDataList == null) {
				logger.info(lang.__("Shelter at disaster end  automatic all closing transmission  it was not closed for no target. "));
				return true;
			}

			boolean sendFlg = false;		// 送信フラグ
			Timestamp now = new Timestamp(System.currentTimeMillis());
			for (PubliccommonsReportData report : publiccommonsReportDataList){
				for (PubliccommonsReportDataLastShelter last : report.publiccommonsReportDataLastShelterList){
					// 前回、開設の発信が1つでもあれば送信する
					if (Constants.SHELTER_OPEN().equals(last.shelterstatus)) sendFlg = true;
					// 送信内容をセット
					ShelterInformationDto shelterInformation = new ShelterInformationDto();
					shelterInformation.chikuName = last.chikuname;
					shelterInformation.shelterAddress = last.shelteraddress;
					shelterInformation.shelterCapacity = last.sheltercapacity;
					shelterInformation.shelterFax = last.shelterfax;
					shelterInformation.shelterName = last.sheltername;
					shelterInformation.shelterPhone = last.shelterphone;
					shelterInformation.shelterStaff = last.shelterstaff;
					shelterInformation.circle = last.circle;
					shelterInformation.type = last.type;
					shelterInformation.typeDetail = last.typedetail;
					shelterInformation.setupTime = null;

					// 常設
					if (Constants.SHELTER_FULLTIME().equals(last.shelterstatus)) {
						shelterInformation.headCount = last.headcount;
						shelterInformation.headCountVoluntary = last.headcountvoluntary;
						shelterInformation.houseHolds = last.households;
						shelterInformation.houseHoldsVoluntary = last.householdsvoluntary;
						shelterInformation.closeTime = null;
						shelterInformation.shelterStatus = Constants.SHELTER_FULLTIME();
					// 未開設
					} else if (Constants.SHELTER_NOESTABLISH().equals(last.shelterstatus)) {
						shelterInformation.headCount = null;
						shelterInformation.headCountVoluntary = null;
						shelterInformation.houseHolds = null;
						shelterInformation.houseHoldsVoluntary = null;
						shelterInformation.closeTime = null;
						shelterInformation.shelterStatus = Constants.SHELTER_NOESTABLISH();
					// 閉鎖
					} else {
						shelterInformation.headCount = null;
						shelterInformation.headCountVoluntary = null;
						shelterInformation.houseHolds = null;
						shelterInformation.houseHoldsVoluntary = null;
						shelterInformation.closeTime = now;							// 現在日時
						shelterInformation.shelterStatus = Constants.SHELTER_CLOSE();
					}
					shelterInformationList.add(shelterInformation);
				}
			}
			if (shelterInformationList != null && sendFlg) {
				sendMediaShelter(shelterInformationList, pCommonsSendDto);
			} else if (sendFlg == false){
				logger.info(lang.__("Shelter at disaster end  automatic all closing transmission  it was not closed for no target. "));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("trackdataid : " + trackdataid);
			logger.error(lang.__("Shelter at disaster end  automatic all closing transmission processing  failure"));
			return false;
		}
		return true;
	}

	/**
	 * コモンズ発信情報生成 自動発報用
	 * @param trackdataid 記録データID
	 * @return pCommonsSendDto コモンズ送信情報(null:生成失敗)
	 */
	private PCommonsSendDto createPCommonsSendDtoAuto(Long trackdataid){
		Timestamp now = new Timestamp(System.currentTimeMillis());
		TrackData track = trackDataService.findById(trackdataid);
		LocalgovInfo gov = localgovInfoService.findById(track.localgovinfoid);
		GroupInfo group = groupInfoService.findById(gov.autostartgroupinfoid);	// 自動発報時の班IDで取得
		String code = PublicCommonsUtils.getLocalGovermentCode(gov);
		PubliccommonsSendToInfo publiccommonsSendToInfo = publiccommonsSendToInfoService.search(track.localgovinfoid);

		PCommonsSendDto pCommonsSendDto = new PCommonsSendDto();
		pCommonsSendDto.localgovinfoid = track.localgovinfoid;																		// 自治体ID
		pCommonsSendDto.trackdataname = getTrackDataNameSingle(track.localgovinfoid, trackdataid);		// 災害名称
		pCommonsSendDto.trackdataid = trackdataid;																					// 記録データID
		pCommonsSendDto.statusValue = publiccommonsSendToInfo.statusValues;											// 運用種別
		pCommonsSendDto.distributiontype = "normalSend";																			// 発信種別(normalSend:新規or更新 correctionSend:訂正 cancelSend:取消)
		pCommonsSendDto.description = "";																									// 訂正・取消理由
		pCommonsSendDto.distributiontypelast = "normalSend";																		// 前回発信種別
		pCommonsSendDto.documentId = "";																									// documentID
		pCommonsSendDto.documentRevision = "";																						// 版数
		pCommonsSendDto.reporttime = now;																								// 公式発表日時
		pCommonsSendDto.createdatetime = now;																						// 作成日時
		pCommonsSendDto.targetdatetime = now;																						// 希望公開開始日時
		pCommonsSendDto.validdatetime = getValidDateTime();																	// 希望公開終了日時(48時間後固定)
		pCommonsSendDto.complementaryInfo = "";																						// 補足情報
		pCommonsSendDto.organizationName  = StringUtil.isEmpty(gov.city) ? gov.pref : gov.city;					// 発表組織名
		pCommonsSendDto.organizationCode = code;																						// 発表組織 地方公共団体コード
		pCommonsSendDto.organizationDomainName = gov.domain;																// 発表組織 組織ドメイン
		pCommonsSendDto.officeName = group.name;																					// 発表組織 部署名
		pCommonsSendDto.officeNameKana = group.namekana;																	// 発表組織 部署名(カナ)
		pCommonsSendDto.officeLocationArea = group.address;																		// 発表組織 部署住所
		pCommonsSendDto.phone = group.phone;																							// 発表組織 部署電話番号
		pCommonsSendDto.fax = group.fax;																									// 発表組織 部署FAX番号
		pCommonsSendDto.email = group.email;																							// 発表組織 部署メールアドレス
		pCommonsSendDto.officeDomainName = group.domain;																		// 発表組織 部署ドメイン
		pCommonsSendDto.organizationNameEditorial = StringUtil.isEmpty(gov.city) ? gov.pref : gov.city;		// 作成組織 組織名
		pCommonsSendDto.organizationCodeEditorial = code;																			// 作成組織 地方公共団体コード
		pCommonsSendDto.organizationDomainNameEditorial = gov.domain;													// 作成組織 組織ドメイン
		pCommonsSendDto.officeNameEditorial = group.name;																		// 作成組織 部署名
		pCommonsSendDto.officeNameKanaEditorial = group.namekana;															// 作成組織 部署名(カナ)
		pCommonsSendDto.officeLocationAreaEditorial = group.address;															// 作成組織 部署住所
		pCommonsSendDto.phoneEditorial = group.phone;																				// 作成組織 部署電話番号
		pCommonsSendDto.faxEditorial = group.fax;																						// 作成組織 部署FAX番号
		pCommonsSendDto.emailEditorial = group.email;																				// 作成組織 部署メールアドレス
		pCommonsSendDto.officeDomainNameEditorial = group.domain;															// 作成組織 部署ドメイン

		return pCommonsSendDto;
	}

	/**
	 * 避難勧告／避難指示全解除チェック (主に災害対応を完了する画面から呼ばれます)
	 * @param trackdataid 記録データID
	 * @return 全解除対象あり:true 全解除対象なしまたは失敗:false
	 */
	public boolean checkAllCloseEvacuationOrder(Long trackdataid){
		// 指定された記録データIDに紐付く、自治体と災害に関して避難勧告／避難指示の全解除対象があるかチェックする
		try{
			// 記録データIDが一致、取消発信以外、送信成功分のみ
			List<PubliccommonsReportData> publiccommonsReportDataList = publiccommonsReportDataService.findByReportDataByIdEvacuationOrder(trackdataid);

			if (publiccommonsReportDataList == null) return false;

			for (PubliccommonsReportData report : publiccommonsReportDataList){
				for (PubliccommonsReportDataLastRefuge last : report.publiccommonsReportDataLastRefugeList){
					// 前回、解除以外の発信が1つでもあれば対象あり
					if (!Constants.ISSUE_CANCEL_NAME().equals(last.issueorlift)) return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("trackdataid : " + trackdataid);
			logger.error(lang.__("Evacuation advisory/order at disaster end  all cancel processing  failure"));
			return false;
		}
		return false;
	}

	/**
	 * 避難所全閉鎖チェック (主に災害対応を完了する画面から呼ばれます/常設と未開設の避難所は対象外です)
	 * @param trackdataid 記録データID
	 * @return 全閉鎖対象あり:true 全閉鎖対象なしまたは失敗:false
	 */
	public boolean checkAllCloseShelter(Long trackdataid){
		// 指定された記録データIDに紐付く、自治体と災害に関して避難所の全閉鎖対象があるかチェックする
		try{
			// 記録データIDが一致、取消発信以外、送信成功分のみ
			List<PubliccommonsReportData> publiccommonsReportDataList = publiccommonsReportDataService.findByReportDataByIdShelter(trackdataid);

			if (publiccommonsReportDataList == null) return false;

			for (PubliccommonsReportData report : publiccommonsReportDataList){
				for (PubliccommonsReportDataLastShelter last : report.publiccommonsReportDataLastShelterList){
					// 前回、開設の発信が1つでもあれば対象あり
					if (Constants.SHELTER_OPEN().equals(last.shelterstatus)) return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * 災害対策本部設置状況全解散チェック (主に災害対応を完了する画面から呼ばれます)
	 * @param trackdataid 記録データID
	 * @return 全解散対象あり:true 全解散対象なしまたは失敗:false
	 */
	public boolean checkAllCloseAntidisaster(Long trackdataid){
		// 指定された記録データIDに紐付く、自治体と災害に関して災害対策本部設置状況の全解散対象があるかチェックする
		try{
			// 記録データIDが一致、取消発信以外、送信成功分のみ
			List<PubliccommonsReportData> publiccommonsReportDataList = publiccommonsReportDataService.findByReportDataByIdAntidisaster(trackdataid);

			if (publiccommonsReportDataList == null) return false;

			for (PubliccommonsReportData report : publiccommonsReportDataList){
				// 前回、解散以外の発信が1つでもあれば対象あり
				if (!Constants.ANTIDISASTER_HEADQUARTER_DISSOLUTION_NAME().equals(report.publiccommonsReportDataLastAntidisaster.issueorlift)) return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * 認証情報を作成
	 * @param user ユーザ
	 * @param password パスワード
	 * @return 認証用のハンドラリスト
	 * @throws XWSSecurityException
	 * @throws IOException
	 * @throws TransformerException
	 */
	@SuppressWarnings("rawtypes")
	private List<Handler> createHandlers(String name, String password) throws XWSSecurityException, IOException, TransformerException {

		List<Handler> handlers = new ArrayList<Handler>();

		// 認証xmlをテンプレートから作成
		InputStream in = null;
		ByteArrayOutputStream clientConfigResult = null;
		try {
			in = getResource("client-config.xslt").openStream();

			Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(in));
	    	transformer.setParameter("name", name);
	    	transformer.setParameter("pass", password);
	    	clientConfigResult = new ByteArrayOutputStream();
	    	StreamSource source = new StreamSource(new ByteArrayInputStream("<root />".getBytes()));
	    	StreamResult result = new StreamResult(clientConfigResult);
	    	transformer.transform(source, result);

		} finally {
			IOUtils.closeQuietly(in);
		}

        final XWSSProcessor xwssProcessor = XWSSProcessorFactory.newInstance().createProcessorForSecurityConfiguration(
        		new ByteArrayInputStream(clientConfigResult.toByteArray())
        		, new CallbackHandler() {
					@Override
					public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
						//
					}
                });

        // 認証情報を作成するハンドラインスタンスを作成。
	    handlers.add(new SOAPHandler<SOAPMessageContext>() {

		    @Override
			public boolean handleMessage(SOAPMessageContext context) {
				 if (!((Boolean) context.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY))) return true;
				 ProcessingContext processingContext = null;
				 try {
					 processingContext = xwssProcessor.createProcessingContext(context.getMessage());
					 processingContext.setSOAPMessage(context.getMessage());
					 SOAPMessage soapMessage = xwssProcessor.secureOutboundMessage(processingContext);
					 context.setMessage(soapMessage);
				     return true;
				 } catch (XWSSecurityException e) {
					 throw new IllegalStateException(e.getMessage(), e);
				 }
			}

			@Override
			public boolean handleFault(SOAPMessageContext context) {
				return true;
			}

			@Override
			public void close(MessageContext context) {
					//
			}

			@Override
			public Set<QName> getHeaders() {
		         QName wsse = new QName("http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd", "Security", "wsse");
		         HashSet<QName> headersSet = new HashSet<QName>();
		         headersSet.add(wsse);
		         return headersSet;
			}
		});

        return handlers;
	}

    /**
     * 送信するsoapドキュメントを作成する
     * @param publiccommonsReportData 対象ファイルを内包するdto
     * @return 送信するsoapドキュメント
     * @throws IOException
     * @throws TransformerException
     */
	public Node createSendNode(PubliccommonsReportData publiccommonsReportData) throws IOException, TransformerException {

		// メッセージxmlファイルを取得
		File file =  getFile(publiccommonsReportData);

		// メッセージxmlをテンプレートxmlへ埋め込む。
		InputStream in = null;
		try {
			in = getResource("pcommons_template.xsl").openStream();
			TransformerFactory transformerFactory = new TransformerFactoryImpl();
			StreamSource transSource = new StreamSource(in);
			Transformer transformer = transformerFactory.newTransformer(transSource);
			StreamSource source = new StreamSource(file);
			DOMResult result = new DOMResult();
			transformer.transform(source, result);
			Node node = result.getNode();
			return node;
		} finally {
			IOUtils.closeQuietly(in);
		}

	}

	/**
	 * クラスパス上のリソースを取得
	 * @param name リソース名
	 * @return リソースのURL
	 */
	private URL getResource(String name) {
		return PublicCommonsService.class.getResource("/" + name);
	}

	/**
	 * コモンズ送信
	 * @param url 対象URL
	 * @param user ユーザ
	 * @param password パスワード
	 * @param publiccommonsReportData 送信データ
	 * @return コモンズからの結果コード
	 * @throws XWSSecurityException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws XMLStreamException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws SOAPException
	 */
	private int sendCommons(
			String url
			, String user
			, String password
			, PubliccommonsReportData publiccommonsReportData
			) throws XWSSecurityException, IOException, TransformerException, XMLStreamException, ParserConfigurationException, SAXException, SOAPException {

		// 名前空間の定義
		String namespace = "http://soap.publiccommons.ne.jp/";
		String serviceName = "MQService";

		QName serviceQName = new QName(namespace, serviceName);
		QName endPointQName = new QName(namespace, "endpoint");

		// wsdlを取得し、送信準備
		URL resource = getResource("MQService.template.wsdl");
		Service service = Service.create(resource, serviceQName);
		service.addPort(endPointQName, SOAPBinding.SOAP11HTTP_BINDING, url);

		// テンプレートへ、送信情報を流し込む
 		Node node = createSendNode(publiccommonsReportData);
		DOMSource domSource = new DOMSource(node);

		Dispatch<Source> dispatch = service.createDispatch(endPointQName , Source.class, Service.Mode.PAYLOAD);

        // 認証情報を作成
		@SuppressWarnings("rawtypes")
		List<Handler> handlers = createHandlers(user, password);

        Binding binding = dispatch.getBinding();
        binding.setHandlerChain(handlers);

        // 送信
        Source result = dispatch.invoke(domSource);

		 // --------------------------------------------------------- コモンズからのリターンコードを解析する。
         // 結果からDocumentを用意する
		 TransformerFactory transFactory = TransformerFactory.newInstance();
	     Transformer transformer = transFactory.newTransformer();
	     StringWriter out = new StringWriter();
	     StreamResult result2 = new StreamResult(out);
	     transformer.transform(result, result2);

		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes());

        // 結果にはShift-JISが含まれることがあり、parse()でMalformedByteSequenceExceptionが発生するのでUTF-8に変換しておくこと
        Reader reader = new InputStreamReader(in, "UTF-8");
        InputSource source = new InputSource(reader);
        Document doc = builder.parse(source);

        // 結果が格納されているタグを取得
    	NodeList nodeList = doc.getElementsByTagNameNS(namespace, "code");
		if (nodeList.getLength() > 0) {
// 			タグから結果コードを取得
//			0 正常終了
//			401 ユーザエラー：メッセージ：メッセージのURI形式が不正です。
//			402 ユーザエラー：メッセージ：入力メッセージはスキーマに従っていません。
//			901 システムエラー：その他：その他のエラーが発生しました。
			Node codeNode = nodeList.item(0);
			String strCode = codeNode.getTextContent();
			try {
				int code = Integer.parseInt(strCode);
				return code;
			} catch (NumberFormatException ne) {
				// 解析失敗。
				logger.error(ne.getMessage(), ne);
			}
		}

		// 解析不能・失敗：リトライを要求
		return -1;
	}

	/**
	 * 送信先情報を取得
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @return Sender
	 * @throws ServletException
	 */
	private PubliccommonsSendToInfo getPubliccommonsSendToInfo(PubliccommonsReportData publiccommonsReportData) {
		// 公共情報コモンズ発信先情報を取得
		PubliccommonsSendToInfo publiccommonsSendToInfo = publiccommonsSendToInfoService.search(publiccommonsReportData.localgovinfoid);

		if (publiccommonsSendToInfo == null) {
			throw new IllegalArgumentException(lang.__("No post-to info"));
		}

		return publiccommonsSendToInfo;
	}

	/**
	 * 発生中の災害名称を取得(都道府県/市町村、複数災害対応版)
	 * @param 自治体ID
	 * @return 災害名称
	 */
	public String getTrackDataName(Long localgovinfoid) {
		// ログイン中の自治体で削除されていない災害を検索
		List<TrackmapInfo> trackmapInfos = trackmapInfoService.findByLocalgovInfoIdAndTrackDataNotDelete(localgovinfoid);
		String tracknames = "";

		// 有効な災害名を連結
		for(TrackmapInfo trackmapInfo : trackmapInfos) {
			for(TrackData trackData : trackmapInfo.trackDatas) {
				// 終了していない災害の場合
				if (trackData.endtime == null) {
					tracknames = tracknames + ", " + trackData.name;
				}
			}
		}
		if (tracknames.length() > 2) tracknames = tracknames.substring(2);
		return tracknames;
	}

	/**
	 * 発生中の災害のうち指定記録データIDの災害名称を取得(都道府県/市町村、複数災害対応版)
	 * @param 自治体ID
	 * @return 災害名称
	 */
	public String getTrackDataNameSingle(Long localgovinfoid, Long trackdataid) {
		// ログイン中の自治体で削除されていない災害を検索
		List<TrackmapInfo> trackmapInfos = trackmapInfoService.findByLocalgovInfoIdAndTrackDataNotDelete(localgovinfoid);

		// 有効な災害名を連結
		for(TrackmapInfo trackmapInfo : trackmapInfos) {
			for(TrackData trackData : trackmapInfo.trackDatas) {
				// 終了していない災害且つ記録データIDが一致する場合
				if (trackData.endtime == null && trackData.id == trackdataid.longValue()) {
					return trackData.name;
				}
			}
		}
		return "";
	}
}
