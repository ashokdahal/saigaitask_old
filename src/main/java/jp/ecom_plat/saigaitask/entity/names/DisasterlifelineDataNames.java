/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.DisasterlifelineData;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link DisasterlifelineData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/22 14:41:59")
public class DisasterlifelineDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Essential utilities damage data";
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
     * telephoneのプロパティ名を返します。
     * 
     * @return telephoneのプロパティ名
     */
    public static PropertyName<Integer> telephone() {
        return new PropertyName<Integer>("telephone");
    }

    /**
     * electricityのプロパティ名を返します。
     * 
     * @return electricityのプロパティ名
     */
    public static PropertyName<Integer> electricity() {
        return new PropertyName<Integer>("electricity");
    }

    /**
     * gasのプロパティ名を返します。
     * 
     * @return gasのプロパティ名
     */
    public static PropertyName<Integer> gas() {
        return new PropertyName<Integer>("gas");
    }

    /**
     * lifelinemountのプロパティ名を返します。
     * 
     * @return lifelinemountのプロパティ名
     */
    public static PropertyName<Integer> lifelinemount() {
        return new PropertyName<Integer>("lifelinemount");
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
     * @author S2JDBC-Gen
     */
    public static class _DisasterlifelineDataNames extends PropertyName<DisasterlifelineData> {

        /**
         * インスタンスを構築します。
         */
        public _DisasterlifelineDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _DisasterlifelineDataNames(final String name) {
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
        public _DisasterlifelineDataNames(final PropertyName<?> parent, final String name) {
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
         * telephoneのプロパティ名を返します。
         *
         * @return telephoneのプロパティ名
         */
        public PropertyName<Integer> telephone() {
            return new PropertyName<Integer>(this, "telephone");
        }

        /**
         * electricityのプロパティ名を返します。
         *
         * @return electricityのプロパティ名
         */
        public PropertyName<Integer> electricity() {
            return new PropertyName<Integer>(this, "electricity");
        }

        /**
         * gasのプロパティ名を返します。
         *
         * @return gasのプロパティ名
         */
        public PropertyName<Integer> gas() {
            return new PropertyName<Integer>(this, "gas");
        }

        /**
         * lifelinemountのプロパティ名を返します。
         *
         * @return lifelinemountのプロパティ名
         */
        public PropertyName<Integer> lifelinemount() {
            return new PropertyName<Integer>(this, "lifelinemount");
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
    }
}
