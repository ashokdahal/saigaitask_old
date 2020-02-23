/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerInfo;
import jp.ecom_plat.saigaitask.entity.names.MeteorequestInfoNames._MeteorequestInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.StationclassInfoNames._StationclassInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MeteotriggerInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/09 19:42:52")
public class MeteotriggerInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Weather trigger info";
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
     * meteorequestinfoidのプロパティ名を返します。
     *
     * @return meteorequestinfoidのプロパティ名
     */
    public static PropertyName<Long> meteorequestinfoid() {
        return new PropertyName<Long>("meteorequestinfoid");
    }

    /**
     * triggerのプロパティ名を返します。
     *
     * @return triggerのプロパティ名
     */
    public static PropertyName<String> trigger() {
        return new PropertyName<String>("trigger");
    }

    /**
     * startupのプロパティ名を返します。
     *
     * @return startupのプロパティ名
     */
    public static PropertyName<Boolean> startup() {
        return new PropertyName<Boolean>("startup");
    }

    /**
     * noticegroupinfoidのプロパティ名を返します。
     *
     * @return noticegroupinfoidのプロパティ名
     */
    public static PropertyName<Long> noticegroupinfoid() {
        return new PropertyName<Long>("noticegroupinfoid");
    }

    /**
     * stationclassinfoidのプロパティ名を返します。
     *
     * @return stationclassinfoidのプロパティ名
     */
    public static PropertyName<Long> stationclassinfoid() {
        return new PropertyName<Long>("stationclassinfoid");
    }

    /**
     * assemblemailのプロパティ名を返します。
     *
     * @return assemblemailのプロパティ名
     */
    public static PropertyName<Boolean> assemblemail() {
        return new PropertyName<Boolean>("assemblemail");
    }

    /**
     * issuetablemasterinfoidのプロパティ名を返します。
     *
     * @return issuetablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> issuetablemasterinfoid() {
        return new PropertyName<Long>("issuetablemasterinfoid");
    }

    /**
     * issueattridのプロパティ名を返します。
     *
     * @return issueattridのプロパティ名
     */
    public static PropertyName<String> issueattrid() {
        return new PropertyName<String>("issueattrid");
    }

    /**
     * issuetextのプロパティ名を返します。
     *
     * @return issuetextのプロパティ名
     */
    public static PropertyName<String> issuetext() {
        return new PropertyName<String>("issuetext");
    }

    /**
     * publiccommonsのプロパティ名を返します。
     *
     * @return publiccommonsのプロパティ名
     */
    public static PropertyName<Boolean> publiccommons() {
        return new PropertyName<Boolean>("publiccommons");
    }

    /**
     * publiccommonsmailのプロパティ名を返します。
     *
     * @return publiccommonsmailのプロパティ名
     */
    public static PropertyName<Boolean> publiccommonsmail() {
        return new PropertyName<Boolean>("publiccommonsmail");
    }

    /**
     * snsのプロパティ名を返します。
     *
     * @return snsのプロパティ名
     */
    public static PropertyName<Boolean> sns() {
        return new PropertyName<Boolean>("sns");
    }

    /**
     * addlayerのプロパティ名を返します。
     *
     * @return addlayerのプロパティ名
     */
    public static PropertyName<Boolean> addlayer() {
        return new PropertyName<Boolean>("addlayer");
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
     * noticegroupInfoのプロパティ名を返します。
     *
     * @return noticegroupInfoのプロパティ名
     */
    public static _NoticegroupInfoNames noticegroupInfo() {
        return new _NoticegroupInfoNames("noticegroupInfo");
    }

    /**
     * meteorequestInfoのプロパティ名を返します。
     *
     * @return meteorequestInfoのプロパティ名
     */
    public static _MeteorequestInfoNames meteorequestInfo() {
        return new _MeteorequestInfoNames("meteorequestInfo");
    }

    /**
     * stationclassInfoのプロパティ名を返します。
     *
     * @return stationclassInfoのプロパティ名
     */
    public static _StationclassInfoNames stationclassInfo() {
        return new _StationclassInfoNames("stationclassInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MeteotriggerInfoNames extends PropertyName<MeteotriggerInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MeteotriggerInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _MeteotriggerInfoNames(final String name) {
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
        public _MeteotriggerInfoNames(final PropertyName<?> parent, final String name) {
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
         * meteorequestinfoidのプロパティ名を返します。
         *
         * @return meteorequestinfoidのプロパティ名
         */
        public PropertyName<Long> meteorequestinfoid() {
            return new PropertyName<Long>(this, "meteorequestinfoid");
        }

        /**
         * triggerのプロパティ名を返します。
         *
         * @return triggerのプロパティ名
         */
        public PropertyName<String> trigger() {
            return new PropertyName<String>(this, "trigger");
        }

        /**
         * startupのプロパティ名を返します。
         *
         * @return startupのプロパティ名
         */
        public PropertyName<Boolean> startup() {
            return new PropertyName<Boolean>(this, "startup");
        }

        /**
         * noticegroupinfoidのプロパティ名を返します。
         *
         * @return noticegroupinfoidのプロパティ名
         */
        public PropertyName<Long> noticegroupinfoid() {
            return new PropertyName<Long>(this, "noticegroupinfoid");
        }

        /**
         * stationclassinfoidのプロパティ名を返します。
         *
         * @return stationclassinfoidのプロパティ名
         */
        public PropertyName<Long> stationclassinfoid() {
            return new PropertyName<Long>(this, "stationclassinfoid");
        }

        /**
         * assemblemailのプロパティ名を返します。
         *
         * @return assemblemailのプロパティ名
         */
        public PropertyName<Boolean> assemblemail() {
            return new PropertyName<Boolean>(this, "assemblemail");
        }

        /**
         * issuetablemasterinfoidのプロパティ名を返します。
         *
         * @return issuetablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> issuetablemasterinfoid() {
            return new PropertyName<Long>(this, "issuetablemasterinfoid");
        }

        /**
         * issueattridのプロパティ名を返します。
         *
         * @return issueattridのプロパティ名
         */
        public PropertyName<String> issueattrid() {
            return new PropertyName<String>(this, "issueattrid");
        }

        /**
         * issuetextのプロパティ名を返します。
         *
         * @return issuetextのプロパティ名
         */
        public PropertyName<String> issuetext() {
            return new PropertyName<String>(this, "issuetext");
        }

        /**
         * publiccommonsのプロパティ名を返します。
         *
         * @return publiccommonsのプロパティ名
         */
        public PropertyName<Boolean> publiccommons() {
            return new PropertyName<Boolean>(this, "publiccommons");
        }

        /**
         * publiccommonsmailのプロパティ名を返します。
         *
         * @return publiccommonsmailのプロパティ名
         */
        public PropertyName<Boolean> publiccommonsmail() {
            return new PropertyName<Boolean>(this, "publiccommonsmail");
        }

        /**
         * snsのプロパティ名を返します。
         *
         * @return snsのプロパティ名
         */
        public PropertyName<Boolean> sns() {
            return new PropertyName<Boolean>(this, "sns");
        }

        /**
         * addlayerのプロパティ名を返します。
         *
         * @return addlayerのプロパティ名
         */
        public PropertyName<Boolean> addlayer() {
            return new PropertyName<Boolean>(this, "addlayer");
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
         * noticegroupInfoのプロパティ名を返します。
         *
         * @return noticegroupInfoのプロパティ名
         */
        public _NoticegroupInfoNames noticegroupInfo() {
            return new _NoticegroupInfoNames(this, "noticegroupInfo");
        }

        /**
         * meteorequestInfoのプロパティ名を返します。
         *
         * @return meteorequestInfoのプロパティ名
         */
        public _MeteorequestInfoNames meteorequestInfo() {
            return new _MeteorequestInfoNames(this, "meteorequestInfo");
        }

        /**
         * stationclassInfoのプロパティ名を返します。
         *
         * @return stationclassInfoのプロパティ名
         */
        public _StationclassInfoNames stationclassInfo() {
            return new _StationclassInfoNames(this, "stationclassInfo");
        }
    }
}
