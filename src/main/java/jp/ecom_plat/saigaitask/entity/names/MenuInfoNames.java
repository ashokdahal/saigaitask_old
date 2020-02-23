/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.names.ExternalmapdataInfoNames._ExternalmapdataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ExternaltabledataInfoNames._ExternaltabledataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.FilterInfoNames._FilterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MapbaselayerInfoNames._MapbaselayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MapreferencelayerInfoNames._MapreferencelayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenumapInfoNames._MenumapInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutableInfoNames._MenutableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskmenuInfoNames._MenutaskmenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutasktypeInfoNames._MenutasktypeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutypeMasterNames._MenutypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultInfoNames._NoticedefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PagemenubuttonInfoNames._PagemenubuttonInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MenuInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/04 11:53:15")
public class MenuInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Menu info";
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
     * menutasktypeinfoidのプロパティ名を返します。
     *
     * @return menutasktypeinfoidのプロパティ名
     */
    public static PropertyName<Long> menutasktypeinfoid() {
        return new PropertyName<Long>("menutasktypeinfoid");
    }

    /**
     * menutypeidのプロパティ名を返します。
     *
     * @return menutypeidのプロパティ名
     */
    public static PropertyName<Integer> menutypeid() {
        return new PropertyName<Integer>("menutypeid");
    }

    /**
     * nameのプロパティ名を返します。
     *
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
    }

    /**
     * helphrefのプロパティ名を返します。
     *
     * @return helphrefのプロパティ名
     */
    public static PropertyName<String> helphref() {
        return new PropertyName<String>("helphref");
    }

    /**
     * filteridのプロパティ名を返します。
     *
     * @return filteridのプロパティ名
     */
    public static PropertyName<Long> filterid() {
        return new PropertyName<Long>("filterid");
    }

    /**
     * visibleのプロパティ名を返します。
     *
     * @return visibleのプロパティ名
     */
    public static PropertyName<Boolean> visible() {
        return new PropertyName<Boolean>("visible");
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
     * excellistoutputtablemasterinfoidのプロパティ名を返します。
     *
     * @return excellistoutputtablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> excellistoutputtablemasterinfoid() {
        return new PropertyName<Long>("excellistoutputtablemasterinfoid");
    }

    /**
     * excellistoutputtableregisttimeattridのプロパティ名を返します。
     *
     * @return excellistoutputtableregisttimeattridのプロパティ名
     */
    public static PropertyName<String> excellistoutputtableregisttimeattrid() {
        return new PropertyName<String>("excellistoutputtableregisttimeattrid");
    }

    /**
     * excellistoutputtabledownloadlinkattridのプロパティ名を返します。
     *
     * @return excellistoutputtabledownloadlinkattridのプロパティ名
     */
    public static PropertyName<String> excellistoutputtabledownloadlinkattrid() {
        return new PropertyName<String>("excellistoutputtabledownloadlinkattrid");
    }

    /**
     * deletedのプロパティ名を返します。
     * 
     * @return deletedのプロパティ名
     */
    public static PropertyName<Boolean> deleted() {
        return new PropertyName<Boolean>("deleted");
    }

    /**
     * menutaskmenuInfoListのプロパティ名を返します。
     *
     * @return menutaskmenuInfoListのプロパティ名
     */
    public static _MenutaskmenuInfoNames menutaskmenuInfoList() {
        return new _MenutaskmenuInfoNames("menutaskmenuInfoList");
    }

    /**
     * menutasktypeInfoのプロパティ名を返します。
     *
     * @return menutasktypeInfoのプロパティ名
     */
    public static _MenutasktypeInfoNames menutasktypeInfo() {
        return new _MenutasktypeInfoNames("menutasktypeInfo");
    }

    /**
     * menutypeMasterのプロパティ名を返します。
     *
     * @return menutypeMasterのプロパティ名
     */
    public static _MenutypeMasterNames menutypeMaster() {
        return new _MenutypeMasterNames("menutypeMaster");
    }

    /**
     * menutableInfoListのプロパティ名を返します。
     *
     * @return menutableInfoListのプロパティ名
     */
    public static _MenutableInfoNames menutableInfoList() {
        return new _MenutableInfoNames("menutableInfoList");
    }

    /**
     * externalmapdataInfoListのプロパティ名を返します。
     *
     * @return externalmapdataInfoListのプロパティ名
     */
    public static _ExternalmapdataInfoNames externalmapdataInfoList() {
        return new _ExternalmapdataInfoNames("externalmapdataInfoList");
    }

    /**
     * externaltabledataInfoListのプロパティ名を返します。
     *
     * @return externaltabledataInfoListのプロパティ名
     */
    public static _ExternaltabledataInfoNames externaltabledataInfoList() {
        return new _ExternaltabledataInfoNames("externaltabledataInfoList");
    }

    /**
     * pagemenubuttonInfoListのプロパティ名を返します。
     *
     * @return pagemenubuttonInfoListのプロパティ名
     */
    public static _PagemenubuttonInfoNames pagemenubuttonInfoList() {
        return new _PagemenubuttonInfoNames("pagemenubuttonInfoList");
    }

    /**
     * mapbaselayerInfoListのプロパティ名を返します。
     *
     * @return mapbaselayerInfoListのプロパティ名
     */
    public static _MapbaselayerInfoNames mapbaselayerInfoList() {
        return new _MapbaselayerInfoNames("mapbaselayerInfoList");
    }

    /**
     * mapreferencelayerInfoListのプロパティ名を返します。
     *
     * @return mapreferencelayerInfoListのプロパティ名
     */
    public static _MapreferencelayerInfoNames mapreferencelayerInfoList() {
        return new _MapreferencelayerInfoNames("mapreferencelayerInfoList");
    }

    /**
     * menumapInfoListのプロパティ名を返します。
     *
     * @return menumapInfoListのプロパティ名
     */
    public static _MenumapInfoNames menumapInfoList() {
        return new _MenumapInfoNames("menumapInfoList");
    }

    /**
     * noticedefaultInfoListのプロパティ名を返します。
     *
     * @return noticedefaultInfoListのプロパティ名
     */
    public static _NoticedefaultInfoNames noticedefaultInfoList() {
        return new _NoticedefaultInfoNames("noticedefaultInfoList");
    }

    /**
     * filterInfoListのプロパティ名を返します。
     * 
     * @return filterInfoListのプロパティ名
     */
    public static _FilterInfoNames filterInfoList() {
        return new _FilterInfoNames("filterInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MenuInfoNames extends PropertyName<MenuInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MenuInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _MenuInfoNames(final String name) {
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
        public _MenuInfoNames(final PropertyName<?> parent, final String name) {
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
         * menutasktypeinfoidのプロパティ名を返します。
         *
         * @return menutasktypeinfoidのプロパティ名
         */
        public PropertyName<Long> menutasktypeinfoid() {
            return new PropertyName<Long>(this, "menutasktypeinfoid");
        }

        /**
         * menutypeidのプロパティ名を返します。
         *
         * @return menutypeidのプロパティ名
         */
        public PropertyName<Integer> menutypeid() {
            return new PropertyName<Integer>(this, "menutypeid");
        }

        /**
         * nameのプロパティ名を返します。
         *
         * @return nameのプロパティ名
         */
        public PropertyName<String> name() {
            return new PropertyName<String>(this, "name");
        }

        /**
         * helphrefのプロパティ名を返します。
         *
         * @return helphrefのプロパティ名
         */
        public PropertyName<String> helphref() {
            return new PropertyName<String>(this, "helphref");
        }

        /**
         * filteridのプロパティ名を返します。
         *
         * @return filteridのプロパティ名
         */
        public PropertyName<Long> filterid() {
            return new PropertyName<Long>(this, "filterid");
        }

        /**
         * visibleのプロパティ名を返します。
         *
         * @return visibleのプロパティ名
         */
        public PropertyName<Boolean> visible() {
            return new PropertyName<Boolean>(this, "visible");
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
         * excellistoutputtablemasterinfoidのプロパティ名を返します。
         *
         * @return excellistoutputtablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> excellistoutputtablemasterinfoid() {
            return new PropertyName<Long>(this, "excellistoutputtablemasterinfoid");
        }

        /**
         * excellistoutputtableregisttimeattridのプロパティ名を返します。
         *
         * @return excellistoutputtableregisttimeattridのプロパティ名
         */
        public PropertyName<String> excellistoutputtableregisttimeattrid() {
            return new PropertyName<String>(this, "excellistoutputtableregisttimeattrid");
        }

        /**
         * excellistoutputtabledownloadlinkattridのプロパティ名を返します。
         *
         * @return excellistoutputtabledownloadlinkattridのプロパティ名
         */
        public PropertyName<String> excellistoutputtabledownloadlinkattrid() {
            return new PropertyName<String>(this, "excellistoutputtabledownloadlinkattrid");
        }

        /**
         * deletedのプロパティ名を返します。
         *
         * @return deletedのプロパティ名
         */
        public PropertyName<Boolean> deleted() {
            return new PropertyName<Boolean>(this, "deleted");
        }

        /**
         * menutaskmenuInfoListのプロパティ名を返します。
         *
         * @return menutaskmenuInfoListのプロパティ名
         */
        public _MenutaskmenuInfoNames menutaskmenuInfoList() {
            return new _MenutaskmenuInfoNames(this, "menutaskmenuInfoList");
        }

        /**
         * menutasktypeInfoのプロパティ名を返します。
         *
         * @return menutasktypeInfoのプロパティ名
         */
        public _MenutasktypeInfoNames menutasktypeInfo() {
            return new _MenutasktypeInfoNames(this, "menutasktypeInfo");
        }

        /**
         * menutypeMasterのプロパティ名を返します。
         *
         * @return menutypeMasterのプロパティ名
         */
        public _MenutypeMasterNames menutypeMaster() {
            return new _MenutypeMasterNames(this, "menutypeMaster");
        }

        /**
         * menutableInfoListのプロパティ名を返します。
         *
         * @return menutableInfoListのプロパティ名
         */
        public _MenutableInfoNames menutableInfoList() {
            return new _MenutableInfoNames(this, "menutableInfoList");
        }

        /**
         * externalmapdataInfoListのプロパティ名を返します。
         *
         * @return externalmapdataInfoListのプロパティ名
         */
        public _ExternalmapdataInfoNames externalmapdataInfoList() {
            return new _ExternalmapdataInfoNames(this, "externalmapdataInfoList");
        }

        /**
         * externaltabledataInfoListのプロパティ名を返します。
         *
         * @return externaltabledataInfoListのプロパティ名
         */
        public _ExternaltabledataInfoNames externaltabledataInfoList() {
            return new _ExternaltabledataInfoNames(this, "externaltabledataInfoList");
        }

        /**
         * pagemenubuttonInfoListのプロパティ名を返します。
         *
         * @return pagemenubuttonInfoListのプロパティ名
         */
        public _PagemenubuttonInfoNames pagemenubuttonInfoList() {
            return new _PagemenubuttonInfoNames(this, "pagemenubuttonInfoList");
        }

        /**
         * mapbaselayerInfoListのプロパティ名を返します。
         *
         * @return mapbaselayerInfoListのプロパティ名
         */
        public _MapbaselayerInfoNames mapbaselayerInfoList() {
            return new _MapbaselayerInfoNames(this, "mapbaselayerInfoList");
        }

        /**
         * mapreferencelayerInfoListのプロパティ名を返します。
         *
         * @return mapreferencelayerInfoListのプロパティ名
         */
        public _MapreferencelayerInfoNames mapreferencelayerInfoList() {
            return new _MapreferencelayerInfoNames(this, "mapreferencelayerInfoList");
        }

        /**
         * menumapInfoListのプロパティ名を返します。
         *
         * @return menumapInfoListのプロパティ名
         */
        public _MenumapInfoNames menumapInfoList() {
            return new _MenumapInfoNames(this, "menumapInfoList");
        }

        /**
         * noticedefaultInfoListのプロパティ名を返します。
         *
         * @return noticedefaultInfoListのプロパティ名
         */
        public _NoticedefaultInfoNames noticedefaultInfoList() {
            return new _NoticedefaultInfoNames(this, "noticedefaultInfoList");
        }

        /**
         * filterInfoListのプロパティ名を返します。
         * 
         * @return filterInfoListのプロパティ名
         */
        public _FilterInfoNames filterInfoList() {
            return new _FilterInfoNames(this, "filterInfoList");
        }
    }
}