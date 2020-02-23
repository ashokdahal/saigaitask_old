/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.*;
import jp.ecom_plat.saigaitask.entity.names.AdminbackupDataNames._AdminbackupDataNames;
import jp.ecom_plat.saigaitask.entity.names.AdminmenuInfoNames._AdminmenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AlarmdefaultgroupInfoNames._AlarmdefaultgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AlarmmessageDataNames._AlarmmessageDataNames;
import jp.ecom_plat.saigaitask.entity.names.AlarmmessageInfoNames._AlarmmessageInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AlarmshowDataNames._AlarmshowDataNames;
import jp.ecom_plat.saigaitask.entity.names.AlarmtypeMasterNames._AlarmtypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.AlertcontentDataNames._AlertcontentDataNames;
import jp.ecom_plat.saigaitask.entity.names.AssembleInfoNames._AssembleInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AssemblestateDataNames._AssemblestateDataNames;
import jp.ecom_plat.saigaitask.entity.names.AuthorizationInfoNames._AuthorizationInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AutocompleteDataNames._AutocompleteDataNames;
import jp.ecom_plat.saigaitask.entity.names.AutocompleteInfoNames._AutocompleteInfoNames;
import jp.ecom_plat.saigaitask.entity.names.CkanauthInfoNames._CkanauthInfoNames;
import jp.ecom_plat.saigaitask.entity.names.CkanmetadataInfoNames._CkanmetadataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.CkanmetadatadefaultInfoNames._CkanmetadatadefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ClearinghousemetadataInfoNames._ClearinghousemetadataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ClearinghousemetadatadefaultInfoNames._ClearinghousemetadatadefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ClearinghousesearchInfoNames._ClearinghousesearchInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ConvertidDataNames._ConvertidDataNames;
import jp.ecom_plat.saigaitask.entity.names.DatatransferInfoNames._DatatransferInfoNames;
import jp.ecom_plat.saigaitask.entity.names.DecisionsupportInfoNames._DecisionsupportInfoNames;
import jp.ecom_plat.saigaitask.entity.names.DecisionsupporttypeMasterNames._DecisionsupporttypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.DemoInfoNames._DemoInfoNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterMasterNames._DisasterMasterNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterbuildDataNames._DisasterbuildDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisastercasualtiesDataNames._DisastercasualtiesDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterfarmDataNames._DisasterfarmDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterfireDataNames._DisasterfireDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterhospitalDataNames._DisasterhospitalDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterhouseDataNames._DisasterhouseDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterhouseholdDataNames._DisasterhouseholdDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterhouseregidentDataNames._DisasterhouseregidentDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasteritemInfoNames._DisasteritemInfoNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterlifelineDataNames._DisasterlifelineDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterroadDataNames._DisasterroadDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterschoolDataNames._DisasterschoolDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisastersituationhistoryDataNames._DisastersituationhistoryDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisastersummaryhistoryDataNames._DisastersummaryhistoryDataNames;
import jp.ecom_plat.saigaitask.entity.names.DisasterwelfareDataNames._DisasterwelfareDataNames;
import jp.ecom_plat.saigaitask.entity.names.EarthquakegrouplayerDataNames._EarthquakegrouplayerDataNames;
import jp.ecom_plat.saigaitask.entity.names.EarthquakelayerDataNames._EarthquakelayerDataNames;
import jp.ecom_plat.saigaitask.entity.names.EarthquakelayerInfoNames._EarthquakelayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.EcomgwpostInfoNames._EcomgwpostInfoNames;
import jp.ecom_plat.saigaitask.entity.names.EcomgwpostdefaultInfoNames._EcomgwpostdefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ExternalmapdataInfoNames._ExternalmapdataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ExternaltabledataInfoNames._ExternaltabledataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.FacebookInfoNames._FacebookInfoNames;
import jp.ecom_plat.saigaitask.entity.names.FacebookMasterNames._FacebookMasterNames;
import jp.ecom_plat.saigaitask.entity.names.FacebookpostInfoNames._FacebookpostInfoNames;
import jp.ecom_plat.saigaitask.entity.names.FacebookpostdefaultInfoNames._FacebookpostdefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.FilterInfoNames._FilterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.GeneralizationhistoryDataNames._GeneralizationhistoryDataNames;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.HeadofficeDataNames._HeadofficeDataNames;
import jp.ecom_plat.saigaitask.entity.names.HistorycolumnlistInfoNames._HistorycolumnlistInfoNames;
import jp.ecom_plat.saigaitask.entity.names.HistorytableInfoNames._HistorytableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ImporttablemasterDataNames._ImporttablemasterDataNames;
import jp.ecom_plat.saigaitask.entity.names.ImporttrackInfoNames._ImporttrackInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ImporttracktableInfoNames._ImporttracktableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.IssuelayerInfoNames._IssuelayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JalertreceivefileDataNames._JalertreceivefileDataNames;
import jp.ecom_plat.saigaitask.entity.names.JalertrequestInfoNames._JalertrequestInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JalertserverInfoNames._JalertserverInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JalerttriggerDataNames._JalerttriggerDataNames;
import jp.ecom_plat.saigaitask.entity.names.JalerttriggerInfoNames._JalerttriggerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JalerttypeMasterNames._JalerttypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.JsonimportapiInfoNames._JsonimportapiInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JsonimportlayerInfoNames._JsonimportlayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgeInfoNames._JudgeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgealarmInfoNames._JudgealarmInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgeformulaMasterNames._JudgeformulaMasterNames;
import jp.ecom_plat.saigaitask.entity.names.JudgemanInfoNames._JudgemanInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgenoticeInfoNames._JudgenoticeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgeresultDataNames._JudgeresultDataNames;
import jp.ecom_plat.saigaitask.entity.names.JudgeresultstyleDataNames._JudgeresultstyleDataNames;
import jp.ecom_plat.saigaitask.entity.names.JudgeresultstyleInfoNames._JudgeresultstyleInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LandmarkDataNames._LandmarkDataNames;
import jp.ecom_plat.saigaitask.entity.names.LandmarkInfoNames._LandmarkInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovgroupInfoNames._LocalgovgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovgroupmemberInfoNames._LocalgovgroupmemberInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovtypeMasterNames._LocalgovtypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.LoginDataNames._LoginDataNames;
import jp.ecom_plat.saigaitask.entity.names.MapbaselayerInfoNames._MapbaselayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MapkmllayerInfoNames._MapkmllayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MaplayerInfoNames._MaplayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MaplayerattrInfoNames._MaplayerattrInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MapmasterInfoNames._MapmasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MapreferencelayerInfoNames._MapreferencelayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuloginInfoNames._MenuloginInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenumapInfoNames._MenumapInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuprocessInfoNames._MenuprocessInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutableInfoNames._MenutableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskInfoNames._MenutaskInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskmenuInfoNames._MenutaskmenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutasktypeInfoNames._MenutasktypeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutypeMasterNames._MenutypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.MeteoDataNames._MeteoDataNames;
import jp.ecom_plat.saigaitask.entity.names.MeteoareainformationcityMasterNames._MeteoareainformationcityMasterNames;
import jp.ecom_plat.saigaitask.entity.names.MeteolayerInfoNames._MeteolayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteorainareaMasterNames._MeteorainareaMasterNames;
import jp.ecom_plat.saigaitask.entity.names.MeteorequestInfoNames._MeteorequestInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteoriverMasterNames._MeteoriverMasterNames;
import jp.ecom_plat.saigaitask.entity.names.MeteoriverareaMasterNames._MeteoriverareaMasterNames;
import jp.ecom_plat.saigaitask.entity.names.Meteoseismicarea1MasterNames._Meteoseismicarea1MasterNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotriggerDataNames._MeteotriggerDataNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotriggerInfoNames._MeteotriggerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotsunamiareaMasterNames._MeteotsunamiareaMasterNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotypeMasterNames._MeteotypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.MeteovolcanoMasterNames._MeteovolcanoMasterNames;
import jp.ecom_plat.saigaitask.entity.names.MeteowarningcodeMasterNames._MeteowarningcodeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.MeteoxsltInfoNames._MeteoxsltInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MobileqrcodeInfoNames._MobileqrcodeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MultilangInfoNames._MultilangInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MultilangmesInfoNames._MultilangmesInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticeTemplateNames._NoticeTemplateNames;
import jp.ecom_plat.saigaitask.entity.names.NoticeaddressInfoNames._NoticeaddressInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultInfoNames._NoticedefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultgroupInfoNames._NoticedefaultgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupaddressInfoNames._NoticegroupaddressInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupuserInfoNames._NoticegroupuserInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticemailDataNames._NoticemailDataNames;
import jp.ecom_plat.saigaitask.entity.names.NoticemailsendDataNames._NoticemailsendDataNames;
import jp.ecom_plat.saigaitask.entity.names.NoticetemplatetypeMasterNames._NoticetemplatetypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.NoticetypeMasterNames._NoticetypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.OauthconsumerDataNames._OauthconsumerDataNames;
import jp.ecom_plat.saigaitask.entity.names.OauthtokenDataNames._OauthtokenDataNames;
import jp.ecom_plat.saigaitask.entity.names.ObservMasterNames._ObservMasterNames;
import jp.ecom_plat.saigaitask.entity.names.ObservatorydamInfoNames._ObservatorydamInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ObservatorydamlayerInfoNames._ObservatorydamlayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ObservatoryrainInfoNames._ObservatoryrainInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ObservatoryrainlayerInfoNames._ObservatoryrainlayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ObservatoryriverInfoNames._ObservatoryriverInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ObservatoryriverlayerInfoNames._ObservatoryriverlayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ObservlistInfoNames._ObservlistInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ObservmenuInfoNames._ObservmenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PagebuttonMasterNames._PagebuttonMasterNames;
import jp.ecom_plat.saigaitask.entity.names.PagemenubuttonInfoNames._PagemenubuttonInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PostingphotolayerDataNames._PostingphotolayerDataNames;
import jp.ecom_plat.saigaitask.entity.names.PostingphotolayerInfoNames._PostingphotolayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastAntidisasterNames._PubliccommonsReportDataLastAntidisasterNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastDamageNames._PubliccommonsReportDataLastDamageNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastEventNames._PubliccommonsReportDataLastEventNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastGeneralNames._PubliccommonsReportDataLastGeneralNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastRefugeNames._PubliccommonsReportDataLastRefugeNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastShelterNames._PubliccommonsReportDataLastShelterNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames._PubliccommonsReportDataNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportRefugeInfoNames._PubliccommonsReportRefugeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportShelterInfoNames._PubliccommonsReportShelterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsSendHistoryDataNames._PubliccommonsSendHistoryDataNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsSendToInfoNames._PubliccommonsSendToInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ReportDataNames._ReportDataNames;
import jp.ecom_plat.saigaitask.entity.names.Reportcontent2DataNames._Reportcontent2DataNames;
import jp.ecom_plat.saigaitask.entity.names.ReportcontentDataNames._ReportcontentDataNames;
import jp.ecom_plat.saigaitask.entity.names.SafetystateInfoNames._SafetystateInfoNames;
import jp.ecom_plat.saigaitask.entity.names.SafetystateMasterNames._SafetystateMasterNames;
import jp.ecom_plat.saigaitask.entity.names.StationMasterNames._StationMasterNames;
import jp.ecom_plat.saigaitask.entity.names.StationalarmInfoNames._StationalarmInfoNames;
import jp.ecom_plat.saigaitask.entity.names.StationclassInfoNames._StationclassInfoNames;
import jp.ecom_plat.saigaitask.entity.names.StationlayerInfoNames._StationlayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.SummarylistInfoNames._SummarylistInfoNames;
import jp.ecom_plat.saigaitask.entity.names.SummarylistcolumnInfoNames._SummarylistcolumnInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablecalculateInfoNames._TablecalculateInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablecalculatecolumnInfoNames._TablecalculatecolumnInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablecolumnMasterNames._TablecolumnMasterNames;
import jp.ecom_plat.saigaitask.entity.names.TablelistcolumnInfoNames._TablelistcolumnInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablelistkarteInfoNames._TablelistkarteInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TableresetcolumnDataNames._TableresetcolumnDataNames;
import jp.ecom_plat.saigaitask.entity.names.TablerowstyleInfoNames._TablerowstyleInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterDataNames._TelemeterDataNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterfileDataNames._TelemeterfileDataNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterofficeInfoNames._TelemeterofficeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterpointInfoNames._TelemeterpointInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterserverInfoNames._TelemeterserverInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TelemetertimeDataNames._TelemetertimeDataNames;
import jp.ecom_plat.saigaitask.entity.names.TelopDataNames._TelopDataNames;
import jp.ecom_plat.saigaitask.entity.names.TeloptypeMasterNames._TeloptypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.ThreadDataNames._ThreadDataNames;
import jp.ecom_plat.saigaitask.entity.names.ThreadresponseDataNames._ThreadresponseDataNames;
import jp.ecom_plat.saigaitask.entity.names.ThreadsendtoDataNames._ThreadsendtoDataNames;
import jp.ecom_plat.saigaitask.entity.names.TimelinemenueMasterNames._TimelinemenueMasterNames;
import jp.ecom_plat.saigaitask.entity.names.TimelinetableInfoNames._TimelinetableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ToolboxDataNames._ToolboxDataNames;
import jp.ecom_plat.saigaitask.entity.names.ToolboxtypeMasterNames._ToolboxtypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrackgroupDataNames._TrackgroupDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrackmapInfoNames._TrackmapInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TracktableInfoNames._TracktableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrainingmeteoDataNames._TrainingmeteoDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrainingplanDataNames._TrainingplanDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrainingplanlinkDataNames._TrainingplanlinkDataNames;
import jp.ecom_plat.saigaitask.entity.names.TwitterInfoNames._TwitterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TwitterMasterNames._TwitterMasterNames;
import jp.ecom_plat.saigaitask.entity.names.UnitInfoNames._UnitInfoNames;
import jp.ecom_plat.saigaitask.entity.names.UserInfoNames._UserInfoNames;
import jp.ecom_plat.saigaitask.entity.names.WhiteboardDataNames._WhiteboardDataNames;

/**
 * 名前クラスの集約です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesAggregateModelFactoryImpl"}, date = "2017/06/13 11:44:14")
public class Names {

    /**
     * {@link AdminbackupData}の名前クラスを返します。
     *
     * @return AdminbackupDataの名前クラス
     */
    public static _AdminbackupDataNames adminbackupData() {
        return new _AdminbackupDataNames();
    }

    /**
     * {@link AdminmenuInfo}の名前クラスを返します。
     *
     * @return AdminmenuInfoの名前クラス
     */
    public static _AdminmenuInfoNames adminmenuInfo() {
        return new _AdminmenuInfoNames();
    }

    /**
     * {@link AlarmdefaultgroupInfo}の名前クラスを返します。
     *
     * @return AlarmdefaultgroupInfoの名前クラス
     */
    public static _AlarmdefaultgroupInfoNames alarmdefaultgroupInfo() {
        return new _AlarmdefaultgroupInfoNames();
    }

    /**
     * {@link AlarmmessageData}の名前クラスを返します。
     *
     * @return AlarmmessageDataの名前クラス
     */
    public static _AlarmmessageDataNames alarmmessageData() {
        return new _AlarmmessageDataNames();
    }

    /**
     * {@link AlarmmessageInfo}の名前クラスを返します。
     *
     * @return AlarmmessageInfoの名前クラス
     */
    public static _AlarmmessageInfoNames alarmmessageInfo() {
        return new _AlarmmessageInfoNames();
    }

    /**
     * {@link AlarmshowData}の名前クラスを返します。
     *
     * @return AlarmshowDataの名前クラス
     */
    public static _AlarmshowDataNames alarmshowData() {
        return new _AlarmshowDataNames();
    }

    /**
     * {@link AlarmtypeMaster}の名前クラスを返します。
     *
     * @return AlarmtypeMasterの名前クラス
     */
    public static _AlarmtypeMasterNames alarmtypeMaster() {
        return new _AlarmtypeMasterNames();
    }

    /**
     * {@link AlertcontentData}の名前クラスを返します。
     *
     * @return AlertcontentDataの名前クラス
     */
    public static _AlertcontentDataNames alertcontentData() {
        return new _AlertcontentDataNames();
    }

    /**
     * {@link AssembleInfo}の名前クラスを返します。
     *
     * @return AssembleInfoの名前クラス
     */
    public static _AssembleInfoNames assembleInfo() {
        return new _AssembleInfoNames();
    }

    /**
     * {@link AssemblestateData}の名前クラスを返します。
     *
     * @return AssemblestateDataの名前クラス
     */
    public static _AssemblestateDataNames assemblestateData() {
        return new _AssemblestateDataNames();
    }

    /**
     * {@link AuthorizationInfo}の名前クラスを返します。
     *
     * @return AuthorizationInfoの名前クラス
     */
    public static _AuthorizationInfoNames authorizationInfo() {
        return new _AuthorizationInfoNames();
    }

    /**
     * {@link AutocompleteData}の名前クラスを返します。
     *
     * @return AutocompleteDataの名前クラス
     */
    public static _AutocompleteDataNames autocompleteData() {
        return new _AutocompleteDataNames();
    }

    /**
     * {@link AutocompleteInfo}の名前クラスを返します。
     *
     * @return AutocompleteInfoの名前クラス
     */
    public static _AutocompleteInfoNames autocompleteInfo() {
        return new _AutocompleteInfoNames();
    }

    /**
     * {@link CkanauthInfo}の名前クラスを返します。
     *
     * @return CkanauthInfoの名前クラス
     */
    public static _CkanauthInfoNames ckanauthInfo() {
        return new _CkanauthInfoNames();
    }

    /**
     * {@link CkanmetadatadefaultInfo}の名前クラスを返します。
     *
     * @return CkanmetadatadefaultInfoの名前クラス
     */
    public static _CkanmetadatadefaultInfoNames ckanmetadatadefaultInfo() {
        return new _CkanmetadatadefaultInfoNames();
    }

    /**
     * {@link CkanmetadataInfo}の名前クラスを返します。
     *
     * @return CkanmetadataInfoの名前クラス
     */
    public static _CkanmetadataInfoNames ckanmetadataInfo() {
        return new _CkanmetadataInfoNames();
    }

    /**
     * {@link ClearinghousemetadatadefaultInfo}の名前クラスを返します。
     *
     * @return ClearinghousemetadatadefaultInfoの名前クラス
     */
    public static _ClearinghousemetadatadefaultInfoNames clearinghousemetadatadefaultInfo() {
        return new _ClearinghousemetadatadefaultInfoNames();
    }

    /**
     * {@link ClearinghousemetadataInfo}の名前クラスを返します。
     *
     * @return ClearinghousemetadataInfoの名前クラス
     */
    public static _ClearinghousemetadataInfoNames clearinghousemetadataInfo() {
        return new _ClearinghousemetadataInfoNames();
    }

    /**
     * {@link ClearinghousesearchInfo}の名前クラスを返します。
     *
     * @return ClearinghousesearchInfoの名前クラス
     */
    public static _ClearinghousesearchInfoNames clearinghousesearchInfo() {
        return new _ClearinghousesearchInfoNames();
    }

    /**
     * {@link ConvertidData}の名前クラスを返します。
     *
     * @return ConvertidDataの名前クラス
     */
    public static _ConvertidDataNames convertidData() {
        return new _ConvertidDataNames();
    }

    /**
     * {@link DatatransferInfo}の名前クラスを返します。
     *
     * @return DatatransferInfoの名前クラス
     */
    public static _DatatransferInfoNames datatransferInfo() {
        return new _DatatransferInfoNames();
    }

    /**
     * {@link DecisionsupportInfo}の名前クラスを返します。
     *
     * @return DecisionsupportInfoの名前クラス
     */
    public static _DecisionsupportInfoNames decisionsupportInfo() {
        return new _DecisionsupportInfoNames();
    }

    /**
     * {@link DecisionsupporttypeMaster}の名前クラスを返します。
     *
     * @return DecisionsupporttypeMasterの名前クラス
     */
    public static _DecisionsupporttypeMasterNames decisionsupporttypeMaster() {
        return new _DecisionsupporttypeMasterNames();
    }

    /**
     * {@link DemoInfo}の名前クラスを返します。
     *
     * @return DemoInfoの名前クラス
     */
    public static _DemoInfoNames demoInfo() {
        return new _DemoInfoNames();
    }

    /**
     * {@link DisasterbuildData}の名前クラスを返します。
     *
     * @return DisasterbuildDataの名前クラス
     */
    public static _DisasterbuildDataNames disasterbuildData() {
        return new _DisasterbuildDataNames();
    }

    /**
     * {@link DisastercasualtiesData}の名前クラスを返します。
     *
     * @return DisastercasualtiesDataの名前クラス
     */
    public static _DisastercasualtiesDataNames disastercasualtiesData() {
        return new _DisastercasualtiesDataNames();
    }

    /**
     * {@link DisasterfarmData}の名前クラスを返します。
     *
     * @return DisasterfarmDataの名前クラス
     */
    public static _DisasterfarmDataNames disasterfarmData() {
        return new _DisasterfarmDataNames();
    }

    /**
     * {@link DisasterfireData}の名前クラスを返します。
     *
     * @return DisasterfireDataの名前クラス
     */
    public static _DisasterfireDataNames disasterfireData() {
        return new _DisasterfireDataNames();
    }

    /**
     * {@link DisasterhospitalData}の名前クラスを返します。
     *
     * @return DisasterhospitalDataの名前クラス
     */
    public static _DisasterhospitalDataNames disasterhospitalData() {
        return new _DisasterhospitalDataNames();
    }

    /**
     * {@link DisasterhouseData}の名前クラスを返します。
     *
     * @return DisasterhouseDataの名前クラス
     */
    public static _DisasterhouseDataNames disasterhouseData() {
        return new _DisasterhouseDataNames();
    }

    /**
     * {@link DisasterhouseholdData}の名前クラスを返します。
     *
     * @return DisasterhouseholdDataの名前クラス
     */
    public static _DisasterhouseholdDataNames disasterhouseholdData() {
        return new _DisasterhouseholdDataNames();
    }

    /**
     * {@link DisasterhouseregidentData}の名前クラスを返します。
     *
     * @return DisasterhouseregidentDataの名前クラス
     */
    public static _DisasterhouseregidentDataNames disasterhouseregidentData() {
        return new _DisasterhouseregidentDataNames();
    }

    /**
     * {@link DisasteritemInfo}の名前クラスを返します。
     *
     * @return DisasteritemInfoの名前クラス
     */
    public static _DisasteritemInfoNames disasteritemInfo() {
        return new _DisasteritemInfoNames();
    }

    /**
     * {@link DisasterlifelineData}の名前クラスを返します。
     *
     * @return DisasterlifelineDataの名前クラス
     */
    public static _DisasterlifelineDataNames disasterlifelineData() {
        return new _DisasterlifelineDataNames();
    }

    /**
     * {@link DisasterMaster}の名前クラスを返します。
     *
     * @return DisasterMasterの名前クラス
     */
    public static _DisasterMasterNames disasterMaster() {
        return new _DisasterMasterNames();
    }

    /**
     * {@link DisasterroadData}の名前クラスを返します。
     *
     * @return DisasterroadDataの名前クラス
     */
    public static _DisasterroadDataNames disasterroadData() {
        return new _DisasterroadDataNames();
    }

    /**
     * {@link DisasterschoolData}の名前クラスを返します。
     *
     * @return DisasterschoolDataの名前クラス
     */
    public static _DisasterschoolDataNames disasterschoolData() {
        return new _DisasterschoolDataNames();
    }

    /**
     * {@link DisastersituationhistoryData}の名前クラスを返します。
     *
     * @return DisastersituationhistoryDataの名前クラス
     */
    public static _DisastersituationhistoryDataNames disastersituationhistoryData() {
        return new _DisastersituationhistoryDataNames();
    }

    /**
     * {@link DisastersummaryhistoryData}の名前クラスを返します。
     *
     * @return DisastersummaryhistoryDataの名前クラス
     */
    public static _DisastersummaryhistoryDataNames disastersummaryhistoryData() {
        return new _DisastersummaryhistoryDataNames();
    }

    /**
     * {@link DisasterwelfareData}の名前クラスを返します。
     *
     * @return DisasterwelfareDataの名前クラス
     */
    public static _DisasterwelfareDataNames disasterwelfareData() {
        return new _DisasterwelfareDataNames();
    }

    /**
     * {@link EarthquakegrouplayerData}の名前クラスを返します。
     *
     * @return EarthquakegrouplayerDataの名前クラス
     */
    public static _EarthquakegrouplayerDataNames earthquakegrouplayerData() {
        return new _EarthquakegrouplayerDataNames();
    }

    /**
     * {@link EarthquakelayerData}の名前クラスを返します。
     *
     * @return EarthquakelayerDataの名前クラス
     */
    public static _EarthquakelayerDataNames earthquakelayerData() {
        return new _EarthquakelayerDataNames();
    }

    /**
     * {@link EarthquakelayerInfo}の名前クラスを返します。
     *
     * @return EarthquakelayerInfoの名前クラス
     */
    public static _EarthquakelayerInfoNames earthquakelayerInfo() {
        return new _EarthquakelayerInfoNames();
    }

    /**
     * {@link EcomgwpostdefaultInfo}の名前クラスを返します。
     *
     * @return EcomgwpostdefaultInfoの名前クラス
     */
    public static _EcomgwpostdefaultInfoNames ecomgwpostdefaultInfo() {
        return new _EcomgwpostdefaultInfoNames();
    }

    /**
     * {@link EcomgwpostInfo}の名前クラスを返します。
     *
     * @return EcomgwpostInfoの名前クラス
     */
    public static _EcomgwpostInfoNames ecomgwpostInfo() {
        return new _EcomgwpostInfoNames();
    }

    /**
     * {@link ExternalmapdataInfo}の名前クラスを返します。
     *
     * @return ExternalmapdataInfoの名前クラス
     */
    public static _ExternalmapdataInfoNames externalmapdataInfo() {
        return new _ExternalmapdataInfoNames();
    }

    /**
     * {@link ExternaltabledataInfo}の名前クラスを返します。
     *
     * @return ExternaltabledataInfoの名前クラス
     */
    public static _ExternaltabledataInfoNames externaltabledataInfo() {
        return new _ExternaltabledataInfoNames();
    }

    /**
     * {@link FacebookInfo}の名前クラスを返します。
     *
     * @return FacebookInfoの名前クラス
     */
    public static _FacebookInfoNames facebookInfo() {
        return new _FacebookInfoNames();
    }

    /**
     * {@link FacebookMaster}の名前クラスを返します。
     *
     * @return FacebookMasterの名前クラス
     */
    public static _FacebookMasterNames facebookMaster() {
        return new _FacebookMasterNames();
    }

    /**
     * {@link FacebookpostdefaultInfo}の名前クラスを返します。
     *
     * @return FacebookpostdefaultInfoの名前クラス
     */
    public static _FacebookpostdefaultInfoNames facebookpostdefaultInfo() {
        return new _FacebookpostdefaultInfoNames();
    }

    /**
     * {@link FacebookpostInfo}の名前クラスを返します。
     *
     * @return FacebookpostInfoの名前クラス
     */
    public static _FacebookpostInfoNames facebookpostInfo() {
        return new _FacebookpostInfoNames();
    }

    /**
     * {@link FilterInfo}の名前クラスを返します。
     *
     * @return FilterInfoの名前クラス
     */
    public static _FilterInfoNames filterInfo() {
        return new _FilterInfoNames();
    }

    /**
     * {@link GeneralizationhistoryData}の名前クラスを返します。
     *
     * @return GeneralizationhistoryDataの名前クラス
     */
    public static _GeneralizationhistoryDataNames generalizationhistoryData() {
        return new _GeneralizationhistoryDataNames();
    }

    /**
     * {@link GroupInfo}の名前クラスを返します。
     *
     * @return GroupInfoの名前クラス
     */
    public static _GroupInfoNames groupInfo() {
        return new _GroupInfoNames();
    }

    /**
     * {@link HeadofficeData}の名前クラスを返します。
     *
     * @return HeadofficeDataの名前クラス
     */
    public static _HeadofficeDataNames headofficeData() {
        return new _HeadofficeDataNames();
    }

    /**
     * {@link HistorycolumnlistInfo}の名前クラスを返します。
     *
     * @return HistorycolumnlistInfoの名前クラス
     */
    public static _HistorycolumnlistInfoNames historycolumnlistInfo() {
        return new _HistorycolumnlistInfoNames();
    }

    /**
     * {@link HistorytableInfo}の名前クラスを返します。
     *
     * @return HistorytableInfoの名前クラス
     */
    public static _HistorytableInfoNames historytableInfo() {
        return new _HistorytableInfoNames();
    }

    /**
     * {@link ImporttablemasterData}の名前クラスを返します。
     *
     * @return ImporttablemasterDataの名前クラス
     */
    public static _ImporttablemasterDataNames importtablemasterData() {
        return new _ImporttablemasterDataNames();
    }

    /**
     * {@link ImporttrackInfo}の名前クラスを返します。
     *
     * @return ImporttrackInfoの名前クラス
     */
    public static _ImporttrackInfoNames importtrackInfo() {
        return new _ImporttrackInfoNames();
    }

    /**
     * {@link ImporttracktableInfo}の名前クラスを返します。
     *
     * @return ImporttracktableInfoの名前クラス
     */
    public static _ImporttracktableInfoNames importtracktableInfo() {
        return new _ImporttracktableInfoNames();
    }

    /**
     * {@link IssuelayerInfo}の名前クラスを返します。
     *
     * @return IssuelayerInfoの名前クラス
     */
    public static _IssuelayerInfoNames issuelayerInfo() {
        return new _IssuelayerInfoNames();
    }

    /**
     * {@link JalertreceivefileData}の名前クラスを返します。
     *
     * @return JalertreceivefileDataの名前クラス
     */
    public static _JalertreceivefileDataNames jalertreceivefileData() {
        return new _JalertreceivefileDataNames();
    }

    /**
     * {@link JalertrequestInfo}の名前クラスを返します。
     *
     * @return JalertrequestInfoの名前クラス
     */
    public static _JalertrequestInfoNames jalertrequestInfo() {
        return new _JalertrequestInfoNames();
    }

    /**
     * {@link JalertserverInfo}の名前クラスを返します。
     *
     * @return JalertserverInfoの名前クラス
     */
    public static _JalertserverInfoNames jalertserverInfo() {
        return new _JalertserverInfoNames();
    }

    /**
     * {@link JalerttriggerData}の名前クラスを返します。
     *
     * @return JalerttriggerDataの名前クラス
     */
    public static _JalerttriggerDataNames jalerttriggerData() {
        return new _JalerttriggerDataNames();
    }

    /**
     * {@link JsonimportapiInfo}の名前クラスを返します。
     *
     * @return JsonimportapiInfoの名前クラス
     */
    public static _JsonimportapiInfoNames jsonimportapiInfo() {
        return new _JsonimportapiInfoNames();
    }

    /**
     * {@link JsonimportlayerInfo}の名前クラスを返します。
     *
     * @return JsonimportlayerInfoの名前クラス
     */
    public static _JsonimportlayerInfoNames jsonimportlayerInfo() {
        return new _JsonimportlayerInfoNames();
    }

    /**
     * {@link JalerttriggerInfo}の名前クラスを返します。
     *
     * @return JalerttriggerInfoの名前クラス
     */
    public static _JalerttriggerInfoNames jalerttriggerInfo() {
        return new _JalerttriggerInfoNames();
    }

    /**
     * {@link JalerttypeMaster}の名前クラスを返します。
     *
     * @return JalerttypeMasterの名前クラス
     */
    public static _JalerttypeMasterNames jalerttypeMaster() {
        return new _JalerttypeMasterNames();
    }

    /**
     * {@link JudgealarmInfo}の名前クラスを返します。
     *
     * @return JudgealarmInfoの名前クラス
     */
    public static _JudgealarmInfoNames judgealarmInfo() {
        return new _JudgealarmInfoNames();
    }

    /**
     * {@link JudgeformulaMaster}の名前クラスを返します。
     *
     * @return JudgeformulaMasterの名前クラス
     */
    public static _JudgeformulaMasterNames judgeformulaMaster() {
        return new _JudgeformulaMasterNames();
    }

    /**
     * {@link JudgeInfo}の名前クラスを返します。
     *
     * @return JudgeInfoの名前クラス
     */
    public static _JudgeInfoNames judgeInfo() {
        return new _JudgeInfoNames();
    }

    /**
     * {@link JudgemanInfo}の名前クラスを返します。
     *
     * @return JudgemanInfoの名前クラス
     */
    public static _JudgemanInfoNames judgemanInfo() {
        return new _JudgemanInfoNames();
    }

    /**
     * {@link JudgenoticeInfo}の名前クラスを返します。
     *
     * @return JudgenoticeInfoの名前クラス
     */
    public static _JudgenoticeInfoNames judgenoticeInfo() {
        return new _JudgenoticeInfoNames();
    }

    /**
     * {@link JudgeresultData}の名前クラスを返します。
     *
     * @return JudgeresultDataの名前クラス
     */
    public static _JudgeresultDataNames judgeresultData() {
        return new _JudgeresultDataNames();
    }

    /**
     * {@link JudgeresultstyleData}の名前クラスを返します。
     *
     * @return JudgeresultstyleDataの名前クラス
     */
    public static _JudgeresultstyleDataNames judgeresultstyleData() {
        return new _JudgeresultstyleDataNames();
    }

    /**
     * {@link JudgeresultstyleInfo}の名前クラスを返します。
     *
     * @return JudgeresultstyleInfoの名前クラス
     */
    public static _JudgeresultstyleInfoNames judgeresultstyleInfo() {
        return new _JudgeresultstyleInfoNames();
    }

    /**
     * {@link LandmarkData}の名前クラスを返します。
     *
     * @return LandmarkDataの名前クラス
     */
    public static _LandmarkDataNames landmarkData() {
        return new _LandmarkDataNames();
    }

    /**
     * {@link LandmarkInfo}の名前クラスを返します。
     *
     * @return LandmarkInfoの名前クラス
     */
    public static _LandmarkInfoNames landmarkInfo() {
        return new _LandmarkInfoNames();
    }

    /**
     * {@link LocalgovInfo}の名前クラスを返します。
     *
     * @return LocalgovInfoの名前クラス
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames();
    }

    /**
     * {@link LocalgovgroupInfo}の名前クラスを返します。
     * 
     * @return LocalgovgroupInfoの名前クラス
     */
    public static _LocalgovgroupInfoNames localgovgroupInfo() {
        return new _LocalgovgroupInfoNames();
    }

    /**
     * {@link LocalgovgroupmemberInfo}の名前クラスを返します。
     * 
     * @return LocalgovgroupmemberInfoの名前クラス
     */
    public static _LocalgovgroupmemberInfoNames localgovgroupmemberInfo() {
        return new _LocalgovgroupmemberInfoNames();
    }
    
    /**
     * {@link LocalgovtypeMaster}の名前クラスを返します。
     *
     * @return LocalgovtypeMasterの名前クラス
     */
    public static _LocalgovtypeMasterNames localgovtypeMaster() {
        return new _LocalgovtypeMasterNames();
    }

    /**
     * {@link LoginData}の名前クラスを返します。
     *
     * @return LoginDataの名前クラス
     */
    public static _LoginDataNames loginData() {
        return new _LoginDataNames();
    }

    /**
     * {@link MapbaselayerInfo}の名前クラスを返します。
     *
     * @return MapbaselayerInfoの名前クラス
     */
    public static _MapbaselayerInfoNames mapbaselayerInfo() {
        return new _MapbaselayerInfoNames();
    }

    /**
     * {@link MapkmllayerInfo}の名前クラスを返します。
     *
     * @return MapkmllayerInfoの名前クラス
     */
    public static _MapkmllayerInfoNames mapkmllayerInfo() {
        return new _MapkmllayerInfoNames();
    }

    /**
     * {@link MaplayerattrInfo}の名前クラスを返します。
     *
     * @return MaplayerattrInfoの名前クラス
     */
    public static _MaplayerattrInfoNames maplayerattrInfo() {
        return new _MaplayerattrInfoNames();
    }

    /**
     * {@link MaplayerInfo}の名前クラスを返します。
     *
     * @return MaplayerInfoの名前クラス
     */
    public static _MaplayerInfoNames maplayerInfo() {
        return new _MaplayerInfoNames();
    }

    /**
     * {@link MapmasterInfo}の名前クラスを返します。
     *
     * @return MapmasterInfoの名前クラス
     */
    public static _MapmasterInfoNames mapmasterInfo() {
        return new _MapmasterInfoNames();
    }

    /**
     * {@link MapreferencelayerInfo}の名前クラスを返します。
     *
     * @return MapreferencelayerInfoの名前クラス
     */
    public static _MapreferencelayerInfoNames mapreferencelayerInfo() {
        return new _MapreferencelayerInfoNames();
    }

    /**
     * {@link MenuInfo}の名前クラスを返します。
     *
     * @return MenuInfoの名前クラス
     */
    public static _MenuInfoNames menuInfo() {
        return new _MenuInfoNames();
    }

    /**
     * {@link MenuloginInfo}の名前クラスを返します。
     *
     * @return MenuloginInfoの名前クラス
     */
    public static _MenuloginInfoNames menuloginInfo() {
        return new _MenuloginInfoNames();
    }

    /**
     * {@link MenumapInfo}の名前クラスを返します。
     *
     * @return MenumapInfoの名前クラス
     */
    public static _MenumapInfoNames menumapInfo() {
        return new _MenumapInfoNames();
    }

    /**
     * {@link MenuprocessInfo}の名前クラスを返します。
     *
     * @return MenuprocessInfoの名前クラス
     */
    public static _MenuprocessInfoNames menuprocessInfo() {
        return new _MenuprocessInfoNames();
    }

    /**
     * {@link MenutableInfo}の名前クラスを返します。
     *
     * @return MenutableInfoの名前クラス
     */
    public static _MenutableInfoNames menutableInfo() {
        return new _MenutableInfoNames();
    }

    /**
     * {@link MenutaskInfo}の名前クラスを返します。
     *
     * @return MenutaskInfoの名前クラス
     */
    public static _MenutaskInfoNames menutaskInfo() {
        return new _MenutaskInfoNames();
    }

    /**
     * {@link MenutaskmenuInfo}の名前クラスを返します。
     *
     * @return MenutaskmenuInfoの名前クラス
     */
    public static _MenutaskmenuInfoNames menutaskmenuInfo() {
        return new _MenutaskmenuInfoNames();
    }

    /**
     * {@link MenutasktypeInfo}の名前クラスを返します。
     *
     * @return MenutasktypeInfoの名前クラス
     */
    public static _MenutasktypeInfoNames menutasktypeInfo() {
        return new _MenutasktypeInfoNames();
    }

    /**
     * {@link MenutypeMaster}の名前クラスを返します。
     *
     * @return MenutypeMasterの名前クラス
     */
    public static _MenutypeMasterNames menutypeMaster() {
        return new _MenutypeMasterNames();
    }

    /**
     * {@link MeteoareainformationcityMaster}の名前クラスを返します。
     *
     * @return MeteoareainformationcityMasterの名前クラス
     */
    public static _MeteoareainformationcityMasterNames meteoareainformationcityMaster() {
        return new _MeteoareainformationcityMasterNames();
    }

    /**
     * {@link MeteoData}の名前クラスを返します。
     *
     * @return MeteoDataの名前クラス
     */
    public static _MeteoDataNames meteoData() {
        return new _MeteoDataNames();
    }

    /**
     * {@link MeteolayerInfo}の名前クラスを返します。
     *
     * @return MeteolayerInfoの名前クラス
     */
    public static _MeteolayerInfoNames meteolayerInfo() {
        return new _MeteolayerInfoNames();
    }

    /**
     * {@link MeteorainareaMaster}の名前クラスを返します。
     *
     * @return MeteorainareaMasterの名前クラス
     */
    public static _MeteorainareaMasterNames meteorainareaMaster() {
        return new _MeteorainareaMasterNames();
    }

    /**
     * {@link MeteorequestInfo}の名前クラスを返します。
     *
     * @return MeteorequestInfoの名前クラス
     */
    public static _MeteorequestInfoNames meteorequestInfo() {
        return new _MeteorequestInfoNames();
    }

    /**
     * {@link MeteoriverareaMaster}の名前クラスを返します。
     *
     * @return MeteoriverareaMasterの名前クラス
     */
    public static _MeteoriverareaMasterNames meteoriverareaMaster() {
        return new _MeteoriverareaMasterNames();
    }

    /**
     * {@link MeteoriverMaster}の名前クラスを返します。
     *
     * @return MeteoriverMasterの名前クラス
     */
    public static _MeteoriverMasterNames meteoriverMaster() {
        return new _MeteoriverMasterNames();
    }

    /**
     * {@link Meteoseismicarea1Master}の名前クラスを返します。
     *
     * @return Meteoseismicarea1Masterの名前クラス
     */
    public static _Meteoseismicarea1MasterNames meteoseismicarea1Master() {
        return new _Meteoseismicarea1MasterNames();
    }

    /**
     * {@link MeteotriggerData}の名前クラスを返します。
     *
     * @return MeteotriggerDataの名前クラス
     */
    public static _MeteotriggerDataNames meteotriggerData() {
        return new _MeteotriggerDataNames();
    }

    /**
     * {@link MeteotriggerInfo}の名前クラスを返します。
     *
     * @return MeteotriggerInfoの名前クラス
     */
    public static _MeteotriggerInfoNames meteotriggerInfo() {
        return new _MeteotriggerInfoNames();
    }

    /**
     * {@link MeteotsunamiareaMaster}の名前クラスを返します。
     *
     * @return MeteotsunamiareaMasterの名前クラス
     */
    public static _MeteotsunamiareaMasterNames meteotsunamiareaMaster() {
        return new _MeteotsunamiareaMasterNames();
    }

    /**
     * {@link MeteotypeMaster}の名前クラスを返します。
     *
     * @return MeteotypeMasterの名前クラス
     */
    public static _MeteotypeMasterNames meteotypeMaster() {
        return new _MeteotypeMasterNames();
    }

    /**
     * {@link MeteovolcanoMaster}の名前クラスを返します。
     *
     * @return MeteovolcanoMasterの名前クラス
     */
    public static _MeteovolcanoMasterNames meteovolcanoMaster() {
        return new _MeteovolcanoMasterNames();
    }

    /**
     * {@link MeteowarningcodeMaster}の名前クラスを返します。
     *
     * @return MeteowarningcodeMasterの名前クラス
     */
    public static _MeteowarningcodeMasterNames meteowarningcodeMaster() {
        return new _MeteowarningcodeMasterNames();
    }

    /**
     * {@link MeteoxsltInfo}の名前クラスを返します。
     *
     * @return MeteoxsltInfoの名前クラス
     */
    public static _MeteoxsltInfoNames meteoxsltInfo() {
        return new _MeteoxsltInfoNames();
    }

    /**
     * {@link MobileqrcodeInfo}の名前クラスを返します。
     * 
     * @return MobileqrcodeInfoの名前クラス
     */
    public static _MobileqrcodeInfoNames mobileqrcodeInfo() {
        return new _MobileqrcodeInfoNames();
    }

    /**
     * {@link MultilangInfo}の名前クラスを返します。
     *
     * @return MultilangInfoの名前クラス
     */
    public static _MultilangInfoNames multilangInfo() {
        return new _MultilangInfoNames();
    }

    /**
     * {@link MultilangmesInfo}の名前クラスを返します。
     *
     * @return MultilangmesInfoの名前クラス
     */
    public static _MultilangmesInfoNames multilangmesInfo() {
        return new _MultilangmesInfoNames();
    }

    /**
     * {@link NoticeaddressInfo}の名前クラスを返します。
     *
     * @return NoticeaddressInfoの名前クラス
     */
    public static _NoticeaddressInfoNames noticeaddressInfo() {
        return new _NoticeaddressInfoNames();
    }

    /**
     * {@link NoticedefaultgroupInfo}の名前クラスを返します。
     *
     * @return NoticedefaultgroupInfoの名前クラス
     */
    public static _NoticedefaultgroupInfoNames noticedefaultgroupInfo() {
        return new _NoticedefaultgroupInfoNames();
    }

    /**
     * {@link NoticedefaultInfo}の名前クラスを返します。
     *
     * @return NoticedefaultInfoの名前クラス
     */
    public static _NoticedefaultInfoNames noticedefaultInfo() {
        return new _NoticedefaultInfoNames();
    }

    /**
     * {@link NoticegroupaddressInfo}の名前クラスを返します。
     *
     * @return NoticegroupaddressInfoの名前クラス
     */
    public static _NoticegroupaddressInfoNames noticegroupaddressInfo() {
        return new _NoticegroupaddressInfoNames();
    }

    /**
     * {@link NoticegroupInfo}の名前クラスを返します。
     *
     * @return NoticegroupInfoの名前クラス
     */
    public static _NoticegroupInfoNames noticegroupInfo() {
        return new _NoticegroupInfoNames();
    }

    /**
     * {@link NoticegroupuserInfo}の名前クラスを返します。
     *
     * @return NoticegroupuserInfoの名前クラス
     */
    public static _NoticegroupuserInfoNames noticegroupuserInfo() {
        return new _NoticegroupuserInfoNames();
    }

    /**
     * {@link NoticemailData}の名前クラスを返します。
     *
     * @return NoticemailDataの名前クラス
     */
    public static _NoticemailDataNames noticemailData() {
        return new _NoticemailDataNames();
    }

    /**
     * {@link NoticemailsendData}の名前クラスを返します。
     *
     * @return NoticemailsendDataの名前クラス
     */
    public static _NoticemailsendDataNames noticemailsendData() {
        return new _NoticemailsendDataNames();
    }

    /**
     * {@link NoticeTemplate}の名前クラスを返します。
     *
     * @return NoticeTemplateの名前クラス
     */
    public static _NoticeTemplateNames noticeTemplate() {
        return new _NoticeTemplateNames();
    }

    /**
     * {@link NoticetemplatetypeMaster}の名前クラスを返します。
     *
     * @return NoticetemplatetypeMasterの名前クラス
     */
    public static _NoticetemplatetypeMasterNames noticetemplatetypeMaster() {
        return new _NoticetemplatetypeMasterNames();
    }

    /**
     * {@link NoticetypeMaster}の名前クラスを返します。
     *
     * @return NoticetypeMasterの名前クラス
     */
    public static _NoticetypeMasterNames noticetypeMaster() {
        return new _NoticetypeMasterNames();
    }

    /**
     * {@link OauthconsumerData}の名前クラスを返します。
     *
     * @return OauthconsumerDataの名前クラス
     */
    public static _OauthconsumerDataNames oauthconsumerData() {
        return new _OauthconsumerDataNames();
    }

    /**
     * {@link OauthtokenData}の名前クラスを返します。
     *
     * @return OauthtokenDataの名前クラス
     */
    public static _OauthtokenDataNames oauthtokenData() {
        return new _OauthtokenDataNames();
    }

    /**
     * {@link ObservatorydamInfo}の名前クラスを返します。
     *
     * @return ObservatorydamInfoの名前クラス
     */
    public static _ObservatorydamInfoNames observatorydamInfo() {
        return new _ObservatorydamInfoNames();
    }

    /**
     * {@link ObservatorydamlayerInfo}の名前クラスを返します。
     *
     * @return ObservatorydamlayerInfoの名前クラス
     */
    public static _ObservatorydamlayerInfoNames observatorydamlayerInfo() {
        return new _ObservatorydamlayerInfoNames();
    }

    /**
     * {@link ObservatoryrainInfo}の名前クラスを返します。
     *
     * @return ObservatoryrainInfoの名前クラス
     */
    public static _ObservatoryrainInfoNames observatoryrainInfo() {
        return new _ObservatoryrainInfoNames();
    }

    /**
     * {@link ObservatoryrainlayerInfo}の名前クラスを返します。
     *
     * @return ObservatoryrainlayerInfoの名前クラス
     */
    public static _ObservatoryrainlayerInfoNames observatoryrainlayerInfo() {
        return new _ObservatoryrainlayerInfoNames();
    }

    /**
     * {@link ObservatoryriverInfo}の名前クラスを返します。
     *
     * @return ObservatoryriverInfoの名前クラス
     */
    public static _ObservatoryriverInfoNames observatoryriverInfo() {
        return new _ObservatoryriverInfoNames();
    }

    /**
     * {@link ObservatoryriverlayerInfo}の名前クラスを返します。
     *
     * @return ObservatoryriverlayerInfoの名前クラス
     */
    public static _ObservatoryriverlayerInfoNames observatoryriverlayerInfo() {
        return new _ObservatoryriverlayerInfoNames();
    }

    /**
     * {@link ObservlistInfo}の名前クラスを返します。
     *
     * @return ObservlistInfoの名前クラス
     */
    public static _ObservlistInfoNames observlistInfo() {
        return new _ObservlistInfoNames();
    }

    /**
     * {@link ObservMaster}の名前クラスを返します。
     *
     * @return ObservMasterの名前クラス
     */
    public static _ObservMasterNames observMaster() {
        return new _ObservMasterNames();
    }

    /**
     * {@link ObservmenuInfo}の名前クラスを返します。
     *
     * @return ObservmenuInfoの名前クラス
     */
    public static _ObservmenuInfoNames observmenuInfo() {
        return new _ObservmenuInfoNames();
    }

    /**
     * {@link PagebuttonMaster}の名前クラスを返します。
     *
     * @return PagebuttonMasterの名前クラス
     */
    public static _PagebuttonMasterNames pagebuttonMaster() {
        return new _PagebuttonMasterNames();
    }

    /**
     * {@link PagemenubuttonInfo}の名前クラスを返します。
     *
     * @return PagemenubuttonInfoの名前クラス
     */
    public static _PagemenubuttonInfoNames pagemenubuttonInfo() {
        return new _PagemenubuttonInfoNames();
    }

    /**
     * {@link PostingphotolayerData}の名前クラスを返します。
     *
     * @return PostingphotolayerDataの名前クラス
     */
    public static _PostingphotolayerDataNames postingphotolayerData() {
        return new _PostingphotolayerDataNames();
    }

    /**
     * {@link PostingphotolayerInfo}の名前クラスを返します。
     *
     * @return PostingphotolayerInfoの名前クラス
     */
    public static _PostingphotolayerInfoNames postingphotolayerInfo() {
        return new _PostingphotolayerInfoNames();
    }

    /**
     * {@link PubliccommonsReportData}の名前クラスを返します。
     *
     * @return PubliccommonsReportDataの名前クラス
     */
    public static _PubliccommonsReportDataNames publiccommonsReportData() {
        return new _PubliccommonsReportDataNames();
    }

    /**
     * {@link PubliccommonsReportDataLastAntidisaster}の名前クラスを返します。
     *
     * @return PubliccommonsReportDataLastAntidisasterの名前クラス
     */
    public static _PubliccommonsReportDataLastAntidisasterNames publiccommonsReportDataLastAntidisaster() {
        return new _PubliccommonsReportDataLastAntidisasterNames();
    }

    /**
     * {@link PubliccommonsReportDataLastDamage}の名前クラスを返します。
     *
     * @return PubliccommonsReportDataLastDamageの名前クラス
     */
    public static _PubliccommonsReportDataLastDamageNames publiccommonsReportDataLastDamage() {
        return new _PubliccommonsReportDataLastDamageNames();
    }

    /**
     * {@link PubliccommonsReportDataLastEvent}の名前クラスを返します。
     *
     * @return PubliccommonsReportDataLastEventの名前クラス
     */
    public static _PubliccommonsReportDataLastEventNames publiccommonsReportDataLastEvent() {
        return new _PubliccommonsReportDataLastEventNames();
    }

    /**
     * {@link PubliccommonsReportDataLastGeneral}の名前クラスを返します。
     *
     * @return PubliccommonsReportDataLastGeneralの名前クラス
     */
    public static _PubliccommonsReportDataLastGeneralNames publiccommonsReportDataLastGeneral() {
        return new _PubliccommonsReportDataLastGeneralNames();
    }

    /**
     * {@link PubliccommonsReportDataLastRefuge}の名前クラスを返します。
     *
     * @return PubliccommonsReportDataLastRefugeの名前クラス
     */
    public static _PubliccommonsReportDataLastRefugeNames publiccommonsReportDataLastRefuge() {
        return new _PubliccommonsReportDataLastRefugeNames();
    }

    /**
     * {@link PubliccommonsReportDataLastShelter}の名前クラスを返します。
     *
     * @return PubliccommonsReportDataLastShelterの名前クラス
     */
    public static _PubliccommonsReportDataLastShelterNames publiccommonsReportDataLastShelter() {
        return new _PubliccommonsReportDataLastShelterNames();
    }

    /**
     * {@link PubliccommonsReportRefugeInfo}の名前クラスを返します。
     *
     * @return PubliccommonsReportRefugeInfoの名前クラス
     */
    public static _PubliccommonsReportRefugeInfoNames publiccommonsReportRefugeInfo() {
        return new _PubliccommonsReportRefugeInfoNames();
    }

    /**
     * {@link PubliccommonsReportShelterInfo}の名前クラスを返します。
     *
     * @return PubliccommonsReportShelterInfoの名前クラス
     */
    public static _PubliccommonsReportShelterInfoNames publiccommonsReportShelterInfo() {
        return new _PubliccommonsReportShelterInfoNames();
    }

    /**
     * {@link PubliccommonsSendHistoryData}の名前クラスを返します。
     *
     * @return PubliccommonsSendHistoryDataの名前クラス
     */
    public static _PubliccommonsSendHistoryDataNames publiccommonsSendHistoryData() {
        return new _PubliccommonsSendHistoryDataNames();
    }

    /**
     * {@link PubliccommonsSendToInfo}の名前クラスを返します。
     *
     * @return PubliccommonsSendToInfoの名前クラス
     */
    public static _PubliccommonsSendToInfoNames publiccommonsSendToInfo() {
        return new _PubliccommonsSendToInfoNames();
    }

    /**
     * {@link Reportcontent2Data}の名前クラスを返します。
     *
     * @return Reportcontent2Dataの名前クラス
     */
    public static _Reportcontent2DataNames reportcontent2Data() {
        return new _Reportcontent2DataNames();
    }

    /**
     * {@link ReportcontentData}の名前クラスを返します。
     *
     * @return ReportcontentDataの名前クラス
     */
    public static _ReportcontentDataNames reportcontentData() {
        return new _ReportcontentDataNames();
    }

    /**
     * {@link ReportData}の名前クラスを返します。
     *
     * @return ReportDataの名前クラス
     */
    public static _ReportDataNames reportData() {
        return new _ReportDataNames();
    }

    /**
     * {@link SafetystateInfo}の名前クラスを返します。
     *
     * @return SafetystateInfoの名前クラス
     */
    public static _SafetystateInfoNames safetystateInfo() {
        return new _SafetystateInfoNames();
    }

    /**
     * {@link SafetystateMaster}の名前クラスを返します。
     *
     * @return SafetystateMasterの名前クラス
     */
    public static _SafetystateMasterNames safetystateMaster() {
        return new _SafetystateMasterNames();
    }

    /**
     * {@link StationalarmInfo}の名前クラスを返します。
     *
     * @return StationalarmInfoの名前クラス
     */
    public static _StationalarmInfoNames stationalarmInfo() {
        return new _StationalarmInfoNames();
    }

    /**
     * {@link StationclassInfo}の名前クラスを返します。
     *
     * @return StationclassInfoの名前クラス
     */
    public static _StationclassInfoNames stationclassInfo() {
        return new _StationclassInfoNames();
    }

    /**
     * {@link StationlayerInfo}の名前クラスを返します。
     *
     * @return StationlayerInfoの名前クラス
     */
    public static _StationlayerInfoNames stationlayerInfo() {
        return new _StationlayerInfoNames();
    }

    /**
     * {@link StationMaster}の名前クラスを返します。
     *
     * @return StationMasterの名前クラス
     */
    public static _StationMasterNames stationMaster() {
        return new _StationMasterNames();
    }

    /**
     * {@link SummarylistcolumnInfo}の名前クラスを返します。
     *
     * @return SummarylistcolumnInfoの名前クラス
     */
    public static _SummarylistcolumnInfoNames summarylistcolumnInfo() {
        return new _SummarylistcolumnInfoNames();
    }

    /**
     * {@link SummarylistInfo}の名前クラスを返します。
     *
     * @return SummarylistInfoの名前クラス
     */
    public static _SummarylistInfoNames summarylistInfo() {
        return new _SummarylistInfoNames();
    }

    /**
     * {@link TablecalculatecolumnInfo}の名前クラスを返します。
     *
     * @return TablecalculatecolumnInfoの名前クラス
     */
    public static _TablecalculatecolumnInfoNames tablecalculatecolumnInfo() {
        return new _TablecalculatecolumnInfoNames();
    }

    /**
     * {@link TablecalculateInfo}の名前クラスを返します。
     *
     * @return TablecalculateInfoの名前クラス
     */
    public static _TablecalculateInfoNames tablecalculateInfo() {
        return new _TablecalculateInfoNames();
    }

    /**
     * {@link TablecolumnMaster}の名前クラスを返します。
     *
     * @return TablecolumnMasterの名前クラス
     */
    public static _TablecolumnMasterNames tablecolumnMaster() {
        return new _TablecolumnMasterNames();
    }

    /**
     * {@link TablelistcolumnInfo}の名前クラスを返します。
     *
     * @return TablelistcolumnInfoの名前クラス
     */
    public static _TablelistcolumnInfoNames tablelistcolumnInfo() {
        return new _TablelistcolumnInfoNames();
    }

    /**
     * {@link TablelistkarteInfo}の名前クラスを返します。
     *
     * @return TablelistkarteInfoの名前クラス
     */
    public static _TablelistkarteInfoNames tablelistkarteInfo() {
        return new _TablelistkarteInfoNames();
    }

    /**
     * {@link TablemasterInfo}の名前クラスを返します。
     *
     * @return TablemasterInfoの名前クラス
     */
    public static _TablemasterInfoNames tablemasterInfo() {
        return new _TablemasterInfoNames();
    }

    /**
     * {@link TableresetcolumnData}の名前クラスを返します。
     *
     * @return TableresetcolumnDataの名前クラス
     */
    public static _TableresetcolumnDataNames tableresetcolumnData() {
        return new _TableresetcolumnDataNames();
    }

    /**
     * {@link TablerowstyleInfo}の名前クラスを返します。
     *
     * @return TablerowstyleInfoの名前クラス
     */
    public static _TablerowstyleInfoNames tablerowstyleInfo() {
        return new _TablerowstyleInfoNames();
    }

    /**
     * {@link TelemeterData}の名前クラスを返します。
     *
     * @return TelemeterDataの名前クラス
     */
    public static _TelemeterDataNames telemeterData() {
        return new _TelemeterDataNames();
    }

    /**
     * {@link TelemeterfileData}の名前クラスを返します。
     *
     * @return TelemeterfileDataの名前クラス
     */
    public static _TelemeterfileDataNames telemeterfileData() {
        return new _TelemeterfileDataNames();
    }

    /**
     * {@link TelemeterofficeInfo}の名前クラスを返します。
     *
     * @return TelemeterofficeInfoの名前クラス
     */
    public static _TelemeterofficeInfoNames telemeterofficeInfo() {
        return new _TelemeterofficeInfoNames();
    }

    /**
     * {@link TelemeterpointInfo}の名前クラスを返します。
     *
     * @return TelemeterpointInfoの名前クラス
     */
    public static _TelemeterpointInfoNames telemeterpointInfo() {
        return new _TelemeterpointInfoNames();
    }

    /**
     * {@link TelemeterserverInfo}の名前クラスを返します。
     *
     * @return TelemeterserverInfoの名前クラス
     */
    public static _TelemeterserverInfoNames telemeterserverInfo() {
        return new _TelemeterserverInfoNames();
    }

    /**
     * {@link TelemetertimeData}の名前クラスを返します。
     *
     * @return TelemetertimeDataの名前クラス
     */
    public static _TelemetertimeDataNames telemetertimeData() {
        return new _TelemetertimeDataNames();
    }

    /**
     * {@link TelopData}の名前クラスを返します。
     *
     * @return TelopDataの名前クラス
     */
    public static _TelopDataNames telopData() {
        return new _TelopDataNames();
    }

    /**
     * {@link TeloptypeMaster}の名前クラスを返します。
     *
     * @return TeloptypeMasterの名前クラス
     */
    public static _TeloptypeMasterNames teloptypeMaster() {
        return new _TeloptypeMasterNames();
    }

    /**
     * {@link ThreadData}の名前クラスを返します。
     *
     * @return ThreadDataの名前クラス
     */
    public static _ThreadDataNames threadData() {
        return new _ThreadDataNames();
    }

    /**
     * {@link ThreadresponseData}の名前クラスを返します。
     *
     * @return ThreadresponseDataの名前クラス
     */
    public static _ThreadresponseDataNames threadresponseData() {
        return new _ThreadresponseDataNames();
    }

    /**
     * {@link ThreadsendtoData}の名前クラスを返します。
     *
     * @return ThreadsendtoDataの名前クラス
     */
    public static _ThreadsendtoDataNames threadsendtoData() {
        return new _ThreadsendtoDataNames();
    }

    /**
     * {@link TimelinemenueMaster}の名前クラスを返します。
     *
     * @return TimelinemenueMasterの名前クラス
     */
    public static _TimelinemenueMasterNames timelinemenueMaster() {
        return new _TimelinemenueMasterNames();
    }

    /**
     * {@link TimelinetableInfo}の名前クラスを返します。
     *
     * @return TimelinetableInfoの名前クラス
     */
    public static _TimelinetableInfoNames timelinetableInfo() {
        return new _TimelinetableInfoNames();
    }

    /**
     * {@link ToolboxData}の名前クラスを返します。
     *
     * @return ToolboxDataの名前クラス
     */
    public static _ToolboxDataNames toolboxData() {
        return new _ToolboxDataNames();
    }

    /**
     * {@link ToolboxtypeMaster}の名前クラスを返します。
     *
     * @return ToolboxtypeMasterの名前クラス
     */
    public static _ToolboxtypeMasterNames toolboxtypeMaster() {
        return new _ToolboxtypeMasterNames();
    }

    /**
     * {@link TrackData}の名前クラスを返します。
     *
     * @return TrackDataの名前クラス
     */
    public static _TrackDataNames trackData() {
        return new _TrackDataNames();
    }

    /**
     * {@link TrackgroupData}の名前クラスを返します。
     *
     * @return TrackgroupDataの名前クラス
     */
    public static _TrackgroupDataNames trackgroupData() {
        return new _TrackgroupDataNames();
    }

    /**
     * {@link TrackmapInfo}の名前クラスを返します。
     *
     * @return TrackmapInfoの名前クラス
     */
    public static _TrackmapInfoNames trackmapInfo() {
        return new _TrackmapInfoNames();
    }

    /**
     * {@link TracktableInfo}の名前クラスを返します。
     *
     * @return TracktableInfoの名前クラス
     */
    public static _TracktableInfoNames tracktableInfo() {
        return new _TracktableInfoNames();
    }

    /**
     * {@link TrainingmeteoData}の名前クラスを返します。
     *
     * @return TrainingmeteoDataの名前クラス
     */
    public static _TrainingmeteoDataNames trainingmeteoData() {
        return new _TrainingmeteoDataNames();
    }

    /**
     * {@link TrainingplanData}の名前クラスを返します。
     *
     * @return TrainingplanDataの名前クラス
     */
    public static _TrainingplanDataNames trainingplanData() {
        return new _TrainingplanDataNames();
    }

    /**
     * {@link TrainingplanlinkData}の名前クラスを返します。
     *
     * @return TrainingplanlinkDataの名前クラス
     */
    public static _TrainingplanlinkDataNames trainingplanlinkData() {
        return new _TrainingplanlinkDataNames();
    }

    /**
     * {@link TwitterInfo}の名前クラスを返します。
     *
     * @return TwitterInfoの名前クラス
     */
    public static _TwitterInfoNames twitterInfo() {
        return new _TwitterInfoNames();
    }

    /**
     * {@link TwitterMaster}の名前クラスを返します。
     *
     * @return TwitterMasterの名前クラス
     */
    public static _TwitterMasterNames twitterMaster() {
        return new _TwitterMasterNames();
    }

    /**
     * {@link UnitInfo}の名前クラスを返します。
     *
     * @return UnitInfoの名前クラス
     */
    public static _UnitInfoNames unitInfo() {
        return new _UnitInfoNames();
    }

    /**
     * {@link UserInfo}の名前クラスを返します。
     *
     * @return UserInfoの名前クラス
     */
    public static _UserInfoNames userInfo() {
        return new _UserInfoNames();
    }

    /**
     * {@link WhiteboardData}の名前クラスを返します。
     *
     * @return WhiteboardDataの名前クラス
     */
    public static _WhiteboardDataNames whiteboardData() {
        return new _WhiteboardDataNames();
    }
}
