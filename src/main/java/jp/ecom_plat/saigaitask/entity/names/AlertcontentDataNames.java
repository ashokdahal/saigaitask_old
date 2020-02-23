/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AlertcontentData;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AlertcontentData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/03/10 19:33:55")
public class AlertcontentDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Alert data";
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
     * localgovinfoidのプロパティ名を返します。
     * 
     * @return localgovinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovinfoid() {
        return new PropertyName<Long>("localgovinfoid");
    }

    /**
     * teloptypeidのプロパティ名を返します。
     * 
     * @return teloptypeidのプロパティ名
     */
    public static PropertyName<Integer> teloptypeid() {
        return new PropertyName<Integer>("teloptypeid");
    }

    /**
     * receivetimeのプロパティ名を返します。
     * 
     * @return receivetimeのプロパティ名
     */
    public static PropertyName<Timestamp> receivetime() {
        return new PropertyName<Timestamp>("receivetime");
    }

    /**
     * titleのプロパティ名を返します。
     * 
     * @return titleのプロパティ名
     */
    public static PropertyName<String> title() {
        return new PropertyName<String>("title");
    }

    /**
     * contentのプロパティ名を返します。
     * 
     * @return contentのプロパティ名
     */
    public static PropertyName<String> content() {
        return new PropertyName<String>("content");
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
     * @author S2JDBC-Gen
     */
    public static class _AlertcontentDataNames extends PropertyName<AlertcontentData> {

        /**
         * インスタンスを構築します。
         */
        public _AlertcontentDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _AlertcontentDataNames(final String name) {
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
        public _AlertcontentDataNames(final PropertyName<?> parent, final String name) {
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
         * localgovinfoidのプロパティ名を返します。
         *
         * @return localgovinfoidのプロパティ名
         */
        public PropertyName<Long> localgovinfoid() {
            return new PropertyName<Long>(this, "localgovinfoid");
        }

        /**
         * teloptypeidのプロパティ名を返します。
         *
         * @return teloptypeidのプロパティ名
         */
        public PropertyName<Integer> teloptypeid() {
            return new PropertyName<Integer>(this, "teloptypeid");
        }

        /**
         * receivetimeのプロパティ名を返します。
         *
         * @return receivetimeのプロパティ名
         */
        public PropertyName<Timestamp> receivetime() {
            return new PropertyName<Timestamp>(this, "receivetime");
        }

        /**
         * titleのプロパティ名を返します。
         *
         * @return titleのプロパティ名
         */
        public PropertyName<String> title() {
            return new PropertyName<String>(this, "title");
        }

        /**
         * contentのプロパティ名を返します。
         *
         * @return contentのプロパティ名
         */
        public PropertyName<String> content() {
            return new PropertyName<String>(this, "content");
        }

        /**
         * filepathのプロパティ名を返します。
         *
         * @return filepathのプロパティ名
         */
        public PropertyName<String> filepath() {
            return new PropertyName<String>(this, "filepath");
        }
    }
}
