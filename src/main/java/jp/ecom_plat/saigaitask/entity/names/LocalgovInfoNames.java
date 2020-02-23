/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.names.AdminbackupDataNames._AdminbackupDataNames;
import jp.ecom_plat.saigaitask.entity.names.AlarmmessageInfoNames._AlarmmessageInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AssembleInfoNames._AssembleInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AutocompleteInfoNames._AutocompleteInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ClearinghousemetadataInfoNames._ClearinghousemetadataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ClearinghousemetadatadefaultInfoNames._ClearinghousemetadatadefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ClearinghousesearchInfoNames._ClearinghousesearchInfoNames;
import jp.ecom_plat.saigaitask.entity.names.DemoInfoNames._DemoInfoNames;
import jp.ecom_plat.saigaitask.entity.names.FacebookInfoNames._FacebookInfoNames;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovtypeMasterNames._LocalgovtypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.MapmasterInfoNames._MapmasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteorequestInfoNames._MeteorequestInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteoxsltInfoNames._MeteoxsltInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MultilangInfoNames._MultilangInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticeTemplateNames._NoticeTemplateNames;
import jp.ecom_plat.saigaitask.entity.names.NoticeaddressInfoNames._NoticeaddressInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportRefugeInfoNames._PubliccommonsReportRefugeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportShelterInfoNames._PubliccommonsReportShelterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsSendToInfoNames._PubliccommonsSendToInfoNames;
import jp.ecom_plat.saigaitask.entity.names.SafetystateInfoNames._SafetystateInfoNames;
import jp.ecom_plat.saigaitask.entity.names.StationalarmInfoNames._StationalarmInfoNames;
import jp.ecom_plat.saigaitask.entity.names.StationclassInfoNames._StationclassInfoNames;
import jp.ecom_plat.saigaitask.entity.names.SummarylistInfoNames._SummarylistInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;
import jp.ecom_plat.saigaitask.entity.names.TwitterInfoNames._TwitterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link LocalgovInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/04 11:53:15")
public class LocalgovInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Local gov. info<!--2-->";
	}

    /**
     * idのプロパティ名を返します。
     *
     * @return idのプロパティ名
     */
    public static PropertyName<Long> id() {
        return new PropertyName<Long>("id");
    }

    /**
     * domainのプロパティ名を返します。
     *
     * @return domainのプロパティ名
     */
    public static PropertyName<String> domain() {
        return new PropertyName<String>("domain");
    }

    /**
     * systemnameのプロパティ名を返します。
     *
     * @return systemnameのプロパティ名
     */
    public static PropertyName<String> systemname() {
        return new PropertyName<String>("systemname");
    }

    /**
     * localgovtypeidのプロパティ名を返します。
     *
     * @return localgovtypeidのプロパティ名
     */
    public static PropertyName<Integer> localgovtypeid() {
        return new PropertyName<Integer>("localgovtypeid");
    }

    /**
     * preflocalgovinfoidのプロパティ名を返します。
     *
     * @return preflocalgovinfoidのプロパティ名
     */
    public static PropertyName<Long> preflocalgovinfoid() {
        return new PropertyName<Long>("preflocalgovinfoid");
    }

    /**
     * prefのプロパティ名を返します。
     *
     * @return prefのプロパティ名
     */
    public static PropertyName<String> pref() {
        return new PropertyName<String>("pref");
    }

    /**
     * prefcodeのプロパティ名を返します。
     *
     * @return prefcodeのプロパティ名
     */
    public static PropertyName<String> prefcode() {
        return new PropertyName<String>("prefcode");
    }

    /**
     * cityのプロパティ名を返します。
     *
     * @return cityのプロパティ名
     */
    public static PropertyName<String> city() {
        return new PropertyName<String>("city");
    }

    /**
     * citycodeのプロパティ名を返します。
     *
     * @return citycodeのプロパティ名
     */
    public static PropertyName<String> citycode() {
        return new PropertyName<String>("citycode");
    }

    /**
     * sectionのプロパティ名を返します。
     *
     * @return sectionのプロパティ名
     */
    public static PropertyName<String> section() {
        return new PropertyName<String>("section");
    }

    /**
     * autostartgroupinfoidのプロパティ名を返します。
     *
     * @return autostartgroupinfoidのプロパティ名
     */
    public static PropertyName<Long> autostartgroupinfoid() {
        return new PropertyName<Long>("autostartgroupinfoid");
    }

    /**
     * autostartのプロパティ名を返します。
     *
     * @return autostartのプロパティ名
     */
    public static PropertyName<Boolean> autostart() {
        return new PropertyName<Boolean>("autostart");
    }

    /**
     * alarmintervalのプロパティ名を返します。
     *
     * @return alarmintervalのプロパティ名
     */
    public static PropertyName<Integer> alarminterval() {
        return new PropertyName<Integer>("alarminterval");
    }

    /**
     * smtpのプロパティ名を返します。
     *
     * @return smtpのプロパティ名
     */
    public static PropertyName<String> smtp() {
        return new PropertyName<String>("smtp");
    }

    /**
     * emailのプロパティ名を返します。
     *
     * @return emailのプロパティ名
     */
    public static PropertyName<String> email() {
        return new PropertyName<String>("email");
    }

    /**
     * logoimagefileのプロパティ名を返します。
     *
     * @return logoimagefileのプロパティ名
     */
    public static PropertyName<String> logoimagefile() {
        return new PropertyName<String>("logoimagefile");
    }

    /**
     * noteのプロパティ名を返します。
     *
     * @return noteのプロパティ名
     */
    public static PropertyName<String> note() {
        return new PropertyName<String>("note");
    }

    /**
     * validのプロパティ名を返します。
     *
     * @return validのプロパティ名
     */
    public static PropertyName<Boolean> valid() {
        return new PropertyName<Boolean>("valid");
    }

    /**
     * multilanginfoidのプロパティ名を返します。
     *
     * @return multilanginfoidのプロパティ名
     */
    public static PropertyName<Long> multilanginfoid() {
        return new PropertyName<Long>("multilanginfoid");
    }

    /**
     * groupinfonameのプロパティ名を返します。
     *
     * @return groupinfonameのプロパティ名
     */
    public static PropertyName<String> groupinfoname() {
        return new PropertyName<String>("groupinfoname");
    }

    /**
     * localgovtypeMasterのプロパティ名を返します。
     *
     * @return localgovtypeMasterのプロパティ名
     */
    public static _LocalgovtypeMasterNames localgovtypeMaster() {
        return new _LocalgovtypeMasterNames("localgovtypeMaster");
    }

    /**
     * groupInfoのプロパティ名を返します。
     *
     * @return groupInfoのプロパティ名
     */
    public static _GroupInfoNames groupInfo() {
        return new _GroupInfoNames("groupInfo");
    }

    /**
     * groupInfosのプロパティ名を返します。
     *
     * @return groupInfosのプロパティ名
     */
    public static _GroupInfoNames groupInfos() {
        return new _GroupInfoNames("groupInfos");
    }

    /**
     * mapmasterInfoListのプロパティ名を返します。
     *
     * @return mapmasterInfoListのプロパティ名
     */
    public static _MapmasterInfoNames mapmasterInfoList() {
        return new _MapmasterInfoNames("mapmasterInfoList");
    }

    /**
     * noticegroupInfoListのプロパティ名を返します。
     *
     * @return noticegroupInfoListのプロパティ名
     */
    public static _NoticegroupInfoNames noticegroupInfoList() {
        return new _NoticegroupInfoNames("noticegroupInfoList");
    }

    /**
     * noticeaddressInfoListのプロパティ名を返します。
     *
     * @return noticeaddressInfoListのプロパティ名
     */
    public static _NoticeaddressInfoNames noticeaddressInfoList() {
        return new _NoticeaddressInfoNames("noticeaddressInfoList");
    }

    /**
     * noticeTemplateListのプロパティ名を返します。
     *
     * @return noticeTemplateListのプロパティ名
     */
    public static _NoticeTemplateNames noticeTemplateList() {
        return new _NoticeTemplateNames("noticeTemplateList");
    }

    /**
     * alarmmessageInfoListのプロパティ名を返します。
     *
     * @return alarmmessageInfoListのプロパティ名
     */
    public static _AlarmmessageInfoNames alarmmessageInfoList() {
        return new _AlarmmessageInfoNames("alarmmessageInfoList");
    }

    /**
     * meteorequestInfoListのプロパティ名を返します。
     *
     * @return meteorequestInfoListのプロパティ名
     */
    public static _MeteorequestInfoNames meteorequestInfoList() {
        return new _MeteorequestInfoNames("meteorequestInfoList");
    }

    /**
     * meteoxsltInfoListのプロパティ名を返します。
     *
     * @return meteoxsltInfoListのプロパティ名
     */
    public static _MeteoxsltInfoNames meteoxsltInfoList() {
        return new _MeteoxsltInfoNames("meteoxsltInfoList");
    }

    /**
     * assembleInfoListのプロパティ名を返します。
     *
     * @return assembleInfoListのプロパティ名
     */
    public static _AssembleInfoNames assembleInfoList() {
        return new _AssembleInfoNames("assembleInfoList");
    }

    /**
     * twitterInfoListのプロパティ名を返します。
     *
     * @return twitterInfoListのプロパティ名
     */
    public static _TwitterInfoNames twitterInfoList() {
        return new _TwitterInfoNames("twitterInfoList");
    }

    /**
     * autocompleteInfoListのプロパティ名を返します。
     *
     * @return autocompleteInfoListのプロパティ名
     */
    public static _AutocompleteInfoNames autocompleteInfoList() {
        return new _AutocompleteInfoNames("autocompleteInfoList");
    }

    /**
     * stationclassInfoListのプロパティ名を返します。
     *
     * @return stationclassInfoListのプロパティ名
     */
    public static _StationclassInfoNames stationclassInfoList() {
        return new _StationclassInfoNames("stationclassInfoList");
    }

    /**
     * clearinghousemetadatadefaultInfoListのプロパティ名を返します。
     *
     * @return clearinghousemetadatadefaultInfoListのプロパティ名
     */
    public static _ClearinghousemetadatadefaultInfoNames clearinghousemetadatadefaultInfoList() {
        return new _ClearinghousemetadatadefaultInfoNames("clearinghousemetadatadefaultInfoList");
    }

    /**
     * clearinghousemetadataInfoListのプロパティ名を返します。
     *
     * @return clearinghousemetadataInfoListのプロパティ名
     */
    public static _ClearinghousemetadataInfoNames clearinghousemetadataInfoList() {
        return new _ClearinghousemetadataInfoNames("clearinghousemetadataInfoList");
    }

    /**
     * clearinghousesearchInfoListのプロパティ名を返します。
     *
     * @return clearinghousesearchInfoListのプロパティ名
     */
    public static _ClearinghousesearchInfoNames clearinghousesearchInfoList() {
        return new _ClearinghousesearchInfoNames("clearinghousesearchInfoList");
    }

    /**
     * adminbackupDataListのプロパティ名を返します。
     *
     * @return adminbackupDataListのプロパティ名
     */
    public static _AdminbackupDataNames adminbackupDataList() {
        return new _AdminbackupDataNames("adminbackupDataList");
    }

    /**
     * facebookInfoListのプロパティ名を返します。
     *
     * @return facebookInfoListのプロパティ名
     */
    public static _FacebookInfoNames facebookInfoList() {
        return new _FacebookInfoNames("facebookInfoList");
    }

    /**
     * publiccommonsReportRefugeInfoListのプロパティ名を返します。
     *
     * @return publiccommonsReportRefugeInfoListのプロパティ名
     */
    public static _PubliccommonsReportRefugeInfoNames publiccommonsReportRefugeInfoList() {
        return new _PubliccommonsReportRefugeInfoNames("publiccommonsReportRefugeInfoList");
    }

    /**
     * publiccommonsReportShelterInfoListのプロパティ名を返します。
     *
     * @return publiccommonsReportShelterInfoListのプロパティ名
     */
    public static _PubliccommonsReportShelterInfoNames publiccommonsReportShelterInfoList() {
        return new _PubliccommonsReportShelterInfoNames("publiccommonsReportShelterInfoList");
    }

    /**
     * demoInfoListのプロパティ名を返します。
     *
     * @return demoInfoListのプロパティ名
     */
    public static _DemoInfoNames demoInfoList() {
        return new _DemoInfoNames("demoInfoList");
    }

    /**
     * publiccommonsSendToInfoListのプロパティ名を返します。
     *
     * @return publiccommonsSendToInfoListのプロパティ名
     */
    public static _PubliccommonsSendToInfoNames publiccommonsSendToInfoList() {
        return new _PubliccommonsSendToInfoNames("publiccommonsSendToInfoList");
    }

    /**
     * stationalarmInfoListのプロパティ名を返します。
     *
     * @return stationalarmInfoListのプロパティ名
     */
    public static _StationalarmInfoNames stationalarmInfoList() {
        return new _StationalarmInfoNames("stationalarmInfoList");
    }

    /**
     * prefLocalgovInfoのプロパティ名を返します。
     *
     * @return prefLocalgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames prefLocalgovInfo() {
        return new _LocalgovInfoNames("prefLocalgovInfo");
    }

    /**
     * trackDatasのプロパティ名を返します。
     *
     * @return trackDatasのプロパティ名
     */
    public static _TrackDataNames trackDatas() {
        return new _TrackDataNames("trackDatas");
    }

    /**
     * multilangInfoのプロパティ名を返します。
     *
     * @return multilangInfoのプロパティ名
     */
    public static _MultilangInfoNames multilangInfo() {
        return new _MultilangInfoNames("multilangInfo");
    }

    /**
     * safetystateInfoListのプロパティ名を返します。
     *
     * @return noticeTemplateListのプロパティ名
     */
    public static _SafetystateInfoNames safetystateInfoList() {
        return new _SafetystateInfoNames("safetystateInfoList");
    }

    /**
     * summarylistInfoListのプロパティ名を返します。
     *
     * @return summarylistInfoListのプロパティ名
     */
    public static _SummarylistInfoNames summarylistInfoList() {
        return new _SummarylistInfoNames("summarylistInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _LocalgovInfoNames extends PropertyName<LocalgovInfo> {

        /**
         * インスタンスを構築します。
         */
        public _LocalgovInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _LocalgovInfoNames(final String name) {
            super(name);
        }

        /**
         * インスタンスを構築します。
         *
         * @param parent
         *            親
         * @param name
         *            名前
         */
        public _LocalgovInfoNames(final PropertyName<?> parent, final String name) {
            super(parent, name);
        }

        /**
         * idのプロパティ名を返します。
         *
         * @return idのプロパティ名
         */
        public PropertyName<Long> id() {
            return new PropertyName<Long>(this, "id");
        }

        /**
         * domainのプロパティ名を返します。
         *
         * @return domainのプロパティ名
         */
        public PropertyName<String> domain() {
            return new PropertyName<String>(this, "domain");
        }

        /**
         * systemnameのプロパティ名を返します。
         *
         * @return systemnameのプロパティ名
         */
        public PropertyName<String> systemname() {
            return new PropertyName<String>(this, "systemname");
        }

        /**
         * localgovtypeidのプロパティ名を返します。
         *
         * @return localgovtypeidのプロパティ名
         */
        public PropertyName<Integer> localgovtypeid() {
            return new PropertyName<Integer>(this, "localgovtypeid");
        }

        /**
         * preflocalgovinfoidのプロパティ名を返します。
         *
         * @return preflocalgovinfoidのプロパティ名
         */
        public PropertyName<Long> preflocalgovinfoid() {
            return new PropertyName<Long>(this, "preflocalgovinfoid");
        }

        /**
         * prefのプロパティ名を返します。
         *
         * @return prefのプロパティ名
         */
        public PropertyName<String> pref() {
            return new PropertyName<String>(this, "pref");
        }

        /**
         * prefcodeのプロパティ名を返します。
         *
         * @return prefcodeのプロパティ名
         */
        public PropertyName<String> prefcode() {
            return new PropertyName<String>(this, "prefcode");
        }

        /**
         * cityのプロパティ名を返します。
         *
         * @return cityのプロパティ名
         */
        public PropertyName<String> city() {
            return new PropertyName<String>(this, "city");
        }

        /**
         * citycodeのプロパティ名を返します。
         *
         * @return citycodeのプロパティ名
         */
        public PropertyName<String> citycode() {
            return new PropertyName<String>(this, "citycode");
        }

        /**
         * sectionのプロパティ名を返します。
         *
         * @return sectionのプロパティ名
         */
        public PropertyName<String> section() {
            return new PropertyName<String>(this, "section");
        }

        /**
         * autostartのプロパティ名を返します。
         *
         * @return autostartのプロパティ名
         */
        public PropertyName<Boolean> autostart() {
            return new PropertyName<Boolean>(this, "autostart");
        }

        /**
         * autostartgroupinfoidのプロパティ名を返します。
         *
         * @return autostartのプロパティ名
         */
        public PropertyName<Long> autostartgroupinfoid() {
            return new PropertyName<Long>(this, "autostartgroupinfoid");
        }

        /**
         * alarmintervalのプロパティ名を返します。
         *
         * @return alarmintervalのプロパティ名
         */
        public PropertyName<Integer> alarminterval() {
            return new PropertyName<Integer>(this, "alarminterval");
        }

        /**
         * smtpのプロパティ名を返します。
         *
         * @return smtpのプロパティ名
         */
        public PropertyName<String> smtp() {
            return new PropertyName<String>(this, "smtp");
        }

        /**
         * emailのプロパティ名を返します。
         *
         * @return emailのプロパティ名
         */
        public PropertyName<String> email() {
            return new PropertyName<String>(this, "email");
        }

        /**
         * logoimagefileのプロパティ名を返します。
         *
         * @return logoimagefileのプロパティ名
         */
        public PropertyName<String> logoimagefile() {
            return new PropertyName<String>("logoimagefile");
        }

        /**
         * noteのプロパティ名を返します。
         *
         * @return noteのプロパティ名
         */
        public PropertyName<String> note() {
            return new PropertyName<String>(this, "note");
        }

        /**
         * validのプロパティ名を返します。
         *
         * @return validのプロパティ名
         */
        public PropertyName<Boolean> valid() {
            return new PropertyName<Boolean>(this, "valid");
        }

        /**
         * multilanginfoidのプロパティ名を返します。
         *
         * @return multilanginfoidのプロパティ名
         */
        public PropertyName<Long> multilanginfoid() {
            return new PropertyName<Long>("multilanginfoid");
        }

        /**
         * groupinfonameのプロパティ名を返します。
         *
         * @return groupinfonameのプロパティ名
         */
        public PropertyName<String> groupinfoname() {
            return new PropertyName<String>(this, "groupinfoname");
        }

        /**
         * localgovtypeMasterのプロパティ名を返します。
         *
         * @return localgovtypeMasterのプロパティ名
         */
        public _LocalgovtypeMasterNames localgovtypeMaster() {
            return new _LocalgovtypeMasterNames(this, "localgovtypeMaster");
        }

        /**
         * groupInfoのプロパティ名を返します。
         *
         * @return groupInfoのプロパティ名
         */
        public _GroupInfoNames groupInfo() {
            return new _GroupInfoNames(this, "groupInfo");
        }

        /**
         * groupInfosのプロパティ名を返します。
         *
         * @return groupInfosのプロパティ名
         */
        public _GroupInfoNames groupInfos() {
            return new _GroupInfoNames(this, "groupInfos");
        }

        /**
         * mapmasterInfoListのプロパティ名を返します。
         *
         * @return mapmasterInfoListのプロパティ名
         */
        public _MapmasterInfoNames mapmasterInfoList() {
            return new _MapmasterInfoNames(this, "mapmasterInfoList");
        }

        /**
         * noticegroupInfoListのプロパティ名を返します。
         *
         * @return noticegroupInfoListのプロパティ名
         */
        public _NoticegroupInfoNames noticegroupInfoList() {
            return new _NoticegroupInfoNames(this, "noticegroupInfoList");
        }

        /**
         * noticeaddressInfoListのプロパティ名を返します。
         *
         * @return noticeaddressInfoListのプロパティ名
         */
        public _NoticeaddressInfoNames noticeaddressInfoList() {
            return new _NoticeaddressInfoNames(this, "noticeaddressInfoList");
        }

        /**
         * noticeTemplateListのプロパティ名を返します。
         *
         * @return noticeTemplateListのプロパティ名
         */
        public _NoticeTemplateNames noticeTemplateList() {
            return new _NoticeTemplateNames(this, "noticeTemplateList");
        }

        /**
         * alarmmessageInfoListのプロパティ名を返します。
         *
         * @return alarmmessageInfoListのプロパティ名
         */
        public _AlarmmessageInfoNames alarmmessageInfoList() {
            return new _AlarmmessageInfoNames(this, "alarmmessageInfoList");
        }

        /**
         * meteorequestInfoListのプロパティ名を返します。
         *
         * @return meteorequestInfoListのプロパティ名
         */
        public _MeteorequestInfoNames meteorequestInfoList() {
            return new _MeteorequestInfoNames(this, "meteorequestInfoList");
        }

        /**
         * meteoxsltInfoListのプロパティ名を返します。
         *
         * @return meteoxsltInfoListのプロパティ名
         */
        public _MeteoxsltInfoNames meteoxsltInfoList() {
            return new _MeteoxsltInfoNames(this, "meteoxsltInfoList");
        }

        /**
         * assembleInfoListのプロパティ名を返します。
         *
         * @return assembleInfoListのプロパティ名
         */
        public _AssembleInfoNames assembleInfoList() {
            return new _AssembleInfoNames(this, "assembleInfoList");
        }

        /**
         * twitterInfoListのプロパティ名を返します。
         *
         * @return twitterInfoListのプロパティ名
         */
        public _TwitterInfoNames twitterInfoList() {
            return new _TwitterInfoNames(this, "twitterInfoList");
        }

        /**
         * autocompleteInfoListのプロパティ名を返します。
         *
         * @return autocompleteInfoListのプロパティ名
         */
        public _AutocompleteInfoNames autocompleteInfoList() {
            return new _AutocompleteInfoNames(this, "autocompleteInfoList");
        }

        /**
         * stationclassInfoListのプロパティ名を返します。
         *
         * @return stationclassInfoListのプロパティ名
         */
        public _StationclassInfoNames stationclassInfoList() {
            return new _StationclassInfoNames(this, "stationclassInfoList");
        }

        /**
         * clearinghousemetadatadefaultInfoListのプロパティ名を返します。
         *
         * @return clearinghousemetadatadefaultInfoListのプロパティ名
         */
        public _ClearinghousemetadatadefaultInfoNames clearinghousemetadatadefaultInfoList() {
            return new _ClearinghousemetadatadefaultInfoNames(this, "clearinghousemetadatadefaultInfoList");
        }

        /**
         * clearinghousemetadataInfoListのプロパティ名を返します。
         *
         * @return clearinghousemetadataInfoListのプロパティ名
         */
        public _ClearinghousemetadataInfoNames clearinghousemetadataInfoList() {
            return new _ClearinghousemetadataInfoNames(this, "clearinghousemetadataInfoList");
        }

        /**
         * clearinghousesearchInfoListのプロパティ名を返します。
         *
         * @return clearinghousesearchInfoListのプロパティ名
         */
        public _ClearinghousesearchInfoNames clearinghousesearchInfoList() {
            return new _ClearinghousesearchInfoNames(this, "clearinghousesearchInfoList");
        }

        /**
         * adminbackupDataListのプロパティ名を返します。
         *
         * @return adminbackupDataListのプロパティ名
         */
        public _AdminbackupDataNames adminbackupDataList() {
            return new _AdminbackupDataNames(this, "adminbackupDataList");
        }

        /**
         * facebookInfoListのプロパティ名を返します。
         *
         * @return facebookInfoListのプロパティ名
         */
        public _FacebookInfoNames facebookInfoList() {
            return new _FacebookInfoNames(this, "facebookInfoList");
        }

        /**
         * publiccommonsReportRefugeInfoListのプロパティ名を返します。
         *
         * @return publiccommonsReportRefugeInfoListのプロパティ名
         */
        public _PubliccommonsReportRefugeInfoNames publiccommonsReportRefugeInfoList() {
            return new _PubliccommonsReportRefugeInfoNames(this, "publiccommonsReportRefugeInfoList");
        }

        /**
         * publiccommonsReportShelterInfoListのプロパティ名を返します。
         *
         * @return publiccommonsReportShelterInfoListのプロパティ名
         */
        public _PubliccommonsReportShelterInfoNames publiccommonsReportShelterInfoList() {
            return new _PubliccommonsReportShelterInfoNames(this, "publiccommonsReportShelterInfoList");
        }

        /**
         * demoInfoListのプロパティ名を返します。
         *
         * @return demoInfoListのプロパティ名
         */
        public _DemoInfoNames demoInfoList() {
            return new _DemoInfoNames(this, "demoInfoList");
        }

        /**
         * publiccommonsSendToInfoListのプロパティ名を返します。
         *
         * @return publiccommonsSendToInfoListのプロパティ名
         */
        public _PubliccommonsSendToInfoNames publiccommonsSendToInfoList() {
            return new _PubliccommonsSendToInfoNames(this, "publiccommonsSendToInfoList");
        }

        /**
         * stationalarmInfoListのプロパティ名を返します。
         *
         * @return stationalarmInfoListのプロパティ名
         */
        public _StationalarmInfoNames stationalarmInfoList() {
            return new _StationalarmInfoNames(this, "stationalarmInfoList");
        }

        /**
         * prefLocalgovInfoのプロパティ名を返します。
         *
         * @return prefLocalgovInfoのプロパティ名
         */
        public _LocalgovInfoNames prefLocalgovInfo() {
            return new _LocalgovInfoNames(this, "prefLocalgovInfo");
        }

        /**
         * trackDatasのプロパティ名を返します。
         *
         * @return trackDatasのプロパティ名
         */
        public _TrackDataNames trackDatas() {
            return new _TrackDataNames(this, "trackDatas");
        }

        /**
         * multilangInfoのプロパティ名を返します。
         *
         * @return multilangInfoのプロパティ名
         */
        public _MultilangInfoNames multilangInfo() {
            return new _MultilangInfoNames("multilangInfo");
        }

        /**
         * safetystateInfoListのプロパティ名を返します。
         *
         * @return noticeTemplateListのプロパティ名
         */
        public  _SafetystateInfoNames safetystateInfoList() {
            return new _SafetystateInfoNames("safetystateInfoList");
        }

        /**
         * summarylistInfoListのプロパティ名を返します。
         *
         * @return summarylistInfoListのプロパティ名
         */
        public _SummarylistInfoNames summarylistInfoList() {
            return new _SummarylistInfoNames(this, "summarylistInfoList");
        }
    }
}
