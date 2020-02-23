/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerData;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotriggerInfoNames._MeteotriggerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.StationclassInfoNames._StationclassInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MeteotriggerData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/09/09 20:42:23")
public class MeteotriggerDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Meteor info trigger data";
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
     * localgovinfoidのプロパティ名を返します。
     * 
     * @return localgovinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovinfoid() {
        return new PropertyName<Long>("localgovinfoid");
    }

    /**
     * trackdataidのプロパティ名を返します。
     * 
     * @return trackdataidのプロパティ名
     */
    public static PropertyName<Long> trackdataid() {
        return new PropertyName<Long>("trackdataid");
    }

    /**
     * meteotriggerinfoidのプロパティ名を返します。
     * 
     * @return meteotriggerinfoidのプロパティ名
     */
    public static PropertyName<Long> meteotriggerinfoid() {
        return new PropertyName<Long>("meteotriggerinfoid");
    }

    /**
     * triggertimeのプロパティ名を返します。
     * 
     * @return triggertimeのプロパティ名
     */
    public static PropertyName<Timestamp> triggertime() {
        return new PropertyName<Timestamp>("triggertime");
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
     * issueのプロパティ名を返します。
     * 
     * @return issueのプロパティ名
     */
    public static PropertyName<Boolean> issue() {
        return new PropertyName<Boolean>("issue");
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
     * localgovInfoのプロパティ名を返します。
     * 
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * trackDataのプロパティ名を返します。
     * 
     * @return trackDataのプロパティ名
     */
    public static _TrackDataNames trackData() {
        return new _TrackDataNames("trackData");
    }

    /**
     * meteotriggerInfoのプロパティ名を返します。
     * 
     * @return meteotriggerInfoのプロパティ名
     */
    public static _MeteotriggerInfoNames meteotriggerInfo() {
        return new _MeteotriggerInfoNames("meteotriggerInfo");
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
    public static class _MeteotriggerDataNames extends PropertyName<MeteotriggerData> {

        /**
         * インスタンスを構築します。
         */
        public _MeteotriggerDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MeteotriggerDataNames(final String name) {
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
        public _MeteotriggerDataNames(final PropertyName<?> parent, final String name) {
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
         * localgovinfoidのプロパティ名を返します。
         *
         * @return localgovinfoidのプロパティ名
         */
        public PropertyName<Long> localgovinfoid() {
            return new PropertyName<Long>(this, "localgovinfoid");
        }

        /**
         * trackdataidのプロパティ名を返します。
         *
         * @return trackdataidのプロパティ名
         */
        public PropertyName<Long> trackdataid() {
            return new PropertyName<Long>(this, "trackdataid");
        }

        /**
         * meteotriggerinfoidのプロパティ名を返します。
         *
         * @return meteotriggerinfoidのプロパティ名
         */
        public PropertyName<Long> meteotriggerinfoid() {
            return new PropertyName<Long>(this, "meteotriggerinfoid");
        }

        /**
         * triggertimeのプロパティ名を返します。
         *
         * @return triggertimeのプロパティ名
         */
        public PropertyName<Timestamp> triggertime() {
            return new PropertyName<Timestamp>(this, "triggertime");
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
         * issueのプロパティ名を返します。
         *
         * @return issueのプロパティ名
         */
        public PropertyName<Boolean> issue() {
            return new PropertyName<Boolean>(this, "issue");
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
         * localgovInfoのプロパティ名を返します。
         * 
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * trackDataのプロパティ名を返します。
         * 
         * @return trackDataのプロパティ名
         */
        public _TrackDataNames trackData() {
            return new _TrackDataNames(this, "trackData");
        }

        /**
         * meteotriggerInfoのプロパティ名を返します。
         * 
         * @return meteotriggerInfoのプロパティ名
         */
        public _MeteotriggerInfoNames meteotriggerInfo() {
            return new _MeteotriggerInfoNames(this, "meteotriggerInfo");
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
         * stationclassInfoのプロパティ名を返します。
         * 
         * @return stationclassInfoのプロパティ名
         */
        public _StationclassInfoNames stationclassInfo() {
            return new _StationclassInfoNames(this, "stationclassInfo");
        }
    }
}
