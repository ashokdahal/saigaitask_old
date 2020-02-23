/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastRefuge;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames._PubliccommonsReportDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsReportDataLastRefuge}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/04/07 14:07:42")
public class PubliccommonsReportDataLastRefugeNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert warning or direction for evacuation last sending history data";
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
     * pcommonsreportdataidのプロパティ名を返します。
     *
     * @return pcommonsreportdataidのプロパティ名
     */
    public static PropertyName<Long> pcommonsreportdataid() {
        return new PropertyName<Long>("pcommonsreportdataid");
    }

    /**
     * chikunameのプロパティ名を返します。
     *
     * @return chikunameのプロパティ名
     */
    public static PropertyName<String> chikuname() {
    	return new PropertyName<String>("chikuname");
    }

    /**
     * hatureikbnのプロパティ名を返します。
     *
     * @return hatureikbnのプロパティ名
     */
    public static PropertyName<String> hatureikbn() {
    	return new PropertyName<String>("hatureikbn");
    }

    /**
     * peopleのプロパティ名を返します。
     *
     * @return peopleのプロパティ名
     */
    public static PropertyName<String> hatureikbnLastCancel() {
    	return new PropertyName<String>("hatureikbn_last_cancel");
    }

    /**
     * targethouseholdsのプロパティ名を返します。
     *
     * @return targethouseholdsのプロパティ名
     */
    public static PropertyName<Integer> people() {
    	return new PropertyName<Integer>("people");
    }

    /**
     * issueorliftのプロパティ名を返します。
     *
     * @return issueorliftのプロパティ名
     */
    public static PropertyName<Integer> issueorlift() {
    	return new PropertyName<Integer>("issueorlift");
    }

    /**
     * hatureidatetimeのプロパティ名を返します。
     *
     * @return hatureidatetimeのプロパティ名
     */
    public static PropertyName<Timestamp> hatureidatetime() {
    	return new PropertyName<Timestamp>("hatureidatetime");
    }

    /**
     * localgovInfoのプロパティ名を返します。
     *
     * @return localgovInfoのプロパティ名
     */
    public static _TrackDataNames localgovInfo() {
        return new _TrackDataNames("localgovInfo");
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
     * validDateTimeのプロパティ名を返します。
     *
     * @return validDateTimeのプロパティ名
     */
    public static PropertyName<String> validDateTime() {
        return new PropertyName<String>("validDateTime");
    }

    /**
     * publiccommonsReportDataのプロパティ名を返します。
     *
     * @return publiccommonsReportDataのプロパティ名
     */
    public static _PubliccommonsReportDataNames publiccommonsReportData() {
        return new _PubliccommonsReportDataNames("publiccommonsReportData");
    }


    /**
     * @author S2JDBC-Gen
     */
    public static class _PubliccommonsReportDataLastRefugeNames extends PropertyName<PubliccommonsReportDataLastRefuge> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsReportDataLastRefugeNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _PubliccommonsReportDataLastRefugeNames(final String name) {
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
        public _PubliccommonsReportDataLastRefugeNames(final PropertyName<?> parent, final String name) {
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
         * pcommonsreportdataidのプロパティ名を返します。
         *
         * @return pcommonsreportdataidのプロパティ名
         */
        public PropertyName<Long> pcommonsreportdataid() {
            return new PropertyName<Long>(this, "pcommonsreportdataid");
        }

        /**
         * chikunameのプロパティ名を返します。
         *
         * @return chikunameのプロパティ名
         */
        public PropertyName<String> chikuname() {
            return new PropertyName<String>(this, "chikuname");
        }

        /**
         * hatureidateのプロパティ名を返します。
         *
         * @return hatureidateのプロパティ名
         */
        public PropertyName<Date> hatureidate() {
            return new PropertyName<Date>(this, "hatureidate");
        }

        /**
         * hatureikbnのプロパティ名を返します。
         *
         * @return hatureikbnのプロパティ名
         */
        public PropertyName<String> hatureikbn() {
            return new PropertyName<String>(this, "hatureikbn");
        }

        /**
         * peopleのプロパティ名を返します。
         *
         * @return peopleのプロパティ名
         */
        public PropertyName<String> people() {
            return new PropertyName<String>(this, "people");
        }

        /**
         * targethouseholdsのプロパティ名を返します。
         *
         * @return targethouseholdsのプロパティ名
         */
        public PropertyName<String> targethouseholds() {
            return new PropertyName<String>(this, "targethouseholds");
        }

        /**
         * issueorliftのプロパティ名を返します。
         *
         * @return issueorliftのプロパティ名
         */
        public PropertyName<String> issueorlift() {
            return new PropertyName<String>(this, "issueorlift");
        }

        /**
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _TrackDataNames localgovInfo() {
            return new _TrackDataNames(this, "localgovInfo");
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
         * validDateTimeのプロパティ名を返します。
         *
         * @return validDateTimeのプロパティ名
         */
        public PropertyName<String> validDateTime() {
            return new PropertyName<String>(this, "validDateTime");
        }

        /**
         * publiccommonsReportDataのプロパティ名を返します。
         *
         * @return publiccommonsReportDataのプロパティ名
         */
        public _PubliccommonsReportDataNames publiccommonsReportData() {
            return new _PubliccommonsReportDataNames(this, "publiccommonsReportData");
        }
    }
}
