/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsSendHistoryData;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames._PubliccommonsReportDataNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsSendToInfoNames._PubliccommonsSendToInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsSendHistoryData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/09 19:47:29")
public class PubliccommonsSendHistoryDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert sending history data";
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
     * publiccommonsReportDataIdのプロパティ名を返します。
     * 
     * @return publiccommonsReportDataIdのプロパティ名
     */
    public static PropertyName<Long> publiccommonsReportDataId() {
        return new PropertyName<Long>("publiccommonsReportDataId");
    }

    /**
     * publiccommonsSendToInfoIdのプロパティ名を返します。
     * 
     * @return publiccommonsSendToInfoIdのプロパティ名
     */
    public static PropertyName<Integer> publiccommonsSendToInfoId() {
        return new PropertyName<Integer>("publiccommonsSendToInfoId");
    }

    /**
     * sendtimeのプロパティ名を返します。
     * 
     * @return sendtimeのプロパティ名
     */
    public static PropertyName<Timestamp> sendtime() {
        return new PropertyName<Timestamp>("sendtime");
    }

    /**
     * successのプロパティ名を返します。
     * 
     * @return successのプロパティ名
     */
    public static PropertyName<Boolean> success() {
        return new PropertyName<Boolean>("success");
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
     * publiccommonsSendToInfoのプロパティ名を返します。
     * 
     * @return publiccommonsSendToInfoのプロパティ名
     */
    public static _PubliccommonsSendToInfoNames publiccommonsSendToInfo() {
        return new _PubliccommonsSendToInfoNames("publiccommonsSendToInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _PubliccommonsSendHistoryDataNames extends PropertyName<PubliccommonsSendHistoryData> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsSendHistoryDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _PubliccommonsSendHistoryDataNames(final String name) {
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
        public _PubliccommonsSendHistoryDataNames(final PropertyName<?> parent, final String name) {
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
         * publiccommonsReportDataIdのプロパティ名を返します。
         *
         * @return publiccommonsReportDataIdのプロパティ名
         */
        public PropertyName<Long> publiccommonsReportDataId() {
            return new PropertyName<Long>(this, "publiccommonsReportDataId");
        }

        /**
         * publiccommonsSendToInfoIdのプロパティ名を返します。
         *
         * @return publiccommonsSendToInfoIdのプロパティ名
         */
        public PropertyName<Integer> publiccommonsSendToInfoId() {
            return new PropertyName<Integer>(this, "publiccommonsSendToInfoId");
        }

        /**
         * sendtimeのプロパティ名を返します。
         *
         * @return sendtimeのプロパティ名
         */
        public PropertyName<Timestamp> sendtime() {
            return new PropertyName<Timestamp>(this, "sendtime");
        }

        /**
         * successのプロパティ名を返します。
         *
         * @return successのプロパティ名
         */
        public PropertyName<Boolean> success() {
            return new PropertyName<Boolean>(this, "success");
        }

        /**
         * publiccommonsReportDataのプロパティ名を返します。
         * 
         * @return publiccommonsReportDataのプロパティ名
         */
        public _PubliccommonsReportDataNames publiccommonsReportData() {
            return new _PubliccommonsReportDataNames(this, "publiccommonsReportData");
        }

        /**
         * publiccommonsSendToInfoのプロパティ名を返します。
         * 
         * @return publiccommonsSendToInfoのプロパティ名
         */
        public _PubliccommonsSendToInfoNames publiccommonsSendToInfo() {
            return new _PubliccommonsSendToInfoNames(this, "publiccommonsSendToInfo");
        }
    }
}
