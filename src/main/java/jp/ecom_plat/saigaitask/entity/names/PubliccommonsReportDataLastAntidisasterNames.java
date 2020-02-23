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
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastAntidisaster;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames._PubliccommonsReportDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsReportDataLastAntidisaster}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/04/07 14:07:42")
public class PubliccommonsReportDataLastAntidisasterNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert last disaster prevention head quarter sending history data";
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
     * hatureidatetimeのプロパティ名を返します。
     *
     * @return hatureidatetimeのプロパティ名
     */
    public static PropertyName<Timestamp> hatureidatetime() {
    	return new PropertyName<Timestamp>("hatureidatetime");
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
     * antidisasterkbnのプロパティ名を返します。
     *
     * @return antidisasterKbnのプロパティ名
     */
    public static PropertyName<String> antidisasterkbn() {
    	return new PropertyName<String>("antidisasterkbn");
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
    public static class _PubliccommonsReportDataLastAntidisasterNames extends PropertyName<PubliccommonsReportDataLastAntidisaster> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsReportDataLastAntidisasterNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _PubliccommonsReportDataLastAntidisasterNames(final String name) {
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
        public _PubliccommonsReportDataLastAntidisasterNames(final PropertyName<?> parent, final String name) {
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
         * hatureidateのプロパティ名を返します。
         *
         * @return hatureidateのプロパティ名
         */
        public PropertyName<Date> hatureidate() {
            return new PropertyName<Date>(this, "hatureidate");
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
         * antidisasterkbnのプロパティ名を返します。
         *
         * @return antidisasterkbnのプロパティ名
         */
        public PropertyName<String> antidisasterkbn() {
            return new PropertyName<String>(this, "antidisasterkbn");
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
         * publiccommonsReportDataのプロパティ名を返します。
         *
         * @return publiccommonsReportDataのプロパティ名
         */
        public _PubliccommonsReportDataNames publiccommonsReportData() {
            return new _PubliccommonsReportDataNames(this, "publiccommonsReportData");
        }
    }
}
