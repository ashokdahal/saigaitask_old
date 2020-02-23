/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MeteoData;
import jp.ecom_plat.saigaitask.entity.names.MeteorequestInfoNames._MeteorequestInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MeteoData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/09 19:42:52")
public class MeteoDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Meteor info getting data";
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
     * meteoidのプロパティ名を返します。
     * 
     * @return meteoidのプロパティ名
     */
    public static PropertyName<Long> meteoid() {
        return new PropertyName<Long>("meteoid");
    }

    /**
     * reporttimeのプロパティ名を返します。
     * 
     * @return reporttimeのプロパティ名
     */
    public static PropertyName<Timestamp> reporttime() {
        return new PropertyName<Timestamp>("reporttime");
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
     * meteorequestInfoのプロパティ名を返します。
     * 
     * @return meteorequestInfoのプロパティ名
     */
    public static _MeteorequestInfoNames meteorequestInfo() {
        return new _MeteorequestInfoNames("meteorequestInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MeteoDataNames extends PropertyName<MeteoData> {

        /**
         * インスタンスを構築します。
         */
        public _MeteoDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MeteoDataNames(final String name) {
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
        public _MeteoDataNames(final PropertyName<?> parent, final String name) {
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
         * meteoidのプロパティ名を返します。
         *
         * @return meteoidのプロパティ名
         */
        public PropertyName<Long> meteoid() {
            return new PropertyName<Long>(this, "meteoid");
        }

        /**
         * reporttimeのプロパティ名を返します。
         *
         * @return reporttimeのプロパティ名
         */
        public PropertyName<Timestamp> reporttime() {
            return new PropertyName<Timestamp>(this, "reporttime");
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
         * meteorequestInfoのプロパティ名を返します。
         * 
         * @return meteorequestInfoのプロパティ名
         */
        public _MeteorequestInfoNames meteorequestInfo() {
            return new _MeteorequestInfoNames(this, "meteorequestInfo");
        }
    }
}
