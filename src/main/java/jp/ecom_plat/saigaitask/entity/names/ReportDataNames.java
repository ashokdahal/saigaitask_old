/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ReportData;
import jp.ecom_plat.saigaitask.entity.names.Reportcontent2DataNames._Reportcontent2DataNames;
import jp.ecom_plat.saigaitask.entity.names.ReportcontentDataNames._ReportcontentDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ReportData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/22 14:41:59")
public class ReportDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Type-4 format";
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
     * trackdataidのプロパティ名を返します。
     *
     * @return trackdataidのプロパティ名
     */
    public static PropertyName<Long> trackdataid() {
        return new PropertyName<Long>("trackdataid");
    }

    /**
     * filepathのプロパティ名を返します。
     *
     * @return filepathのプロパティ名
     */
    public static PropertyName<String> filepath() {
        return new PropertyName<String>("filepath");
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
     * registtimeのプロパティ名を返します。
     *
     * @return registtimeのプロパティ名
     */
    public static PropertyName<Timestamp> registtime() {
        return new PropertyName<Timestamp>("registtime");
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
     * reportcontentDatasのプロパティ名を返します。
     *
     * @return reportcontentDatasのプロパティ名
     */
    public static _ReportcontentDataNames reportcontentDatas() {
        return new _ReportcontentDataNames("reportcontentDatas");
    }

    /**
     * reportcontent2Datasのプロパティ名を返します。
     *
     * @return reportcontent2Datasのプロパティ名
     */
    public static _Reportcontent2DataNames reportcontent2Datas() {
        return new _Reportcontent2DataNames("reportcontent2Datas");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _ReportDataNames extends PropertyName<ReportData> {

        /**
         * インスタンスを構築します。
         */
        public _ReportDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _ReportDataNames(final String name) {
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
        public _ReportDataNames(final PropertyName<?> parent, final String name) {
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
         * trackdataidのプロパティ名を返します。
         *
         * @return trackdataidのプロパティ名
         */
        public PropertyName<Long> trackdataid() {
            return new PropertyName<Long>(this, "trackdataid");
        }

        /**
         * filepathのプロパティ名を返します。
         *
         * @return filepathのプロパティ名
         */
        public PropertyName<String> filepath() {
            return new PropertyName<String>(this, "filepath");
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
         * registtimeのプロパティ名を返します。
         *
         * @return registtimeのプロパティ名
         */
        public PropertyName<Timestamp> registtime() {
            return new PropertyName<Timestamp>(this, "registtime");
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
         * reportcontentDatasのプロパティ名を返します。
         *
         * @return reportcontentDatasのプロパティ名
         */
        public _ReportcontentDataNames reportcontentDatas() {
            return new _ReportcontentDataNames(this, "reportcontentDatas");
        }

        /**
         * reportcontent2Datasのプロパティ名を返します。
         *
         * @return reportcontent2Datasのプロパティ名
         */
        public _Reportcontent2DataNames reportcontent2Datas() {
            return new _Reportcontent2DataNames(this, "reportcontent2Datas");
        }
    }
}
