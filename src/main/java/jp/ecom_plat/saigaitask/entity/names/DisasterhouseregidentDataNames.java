/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseregidentData;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link DisasterhouseregidentData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/01/21 9:57:34")
public class DisasterhouseregidentDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "House damage ( person ) data";
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
     * houseallのプロパティ名を返します。
     * 
     * @return houseallのプロパティ名
     */
    public static PropertyName<Integer> houseall() {
        return new PropertyName<Integer>("houseall");
    }

    /**
     * househalfのプロパティ名を返します。
     * 
     * @return househalfのプロパティ名
     */
    public static PropertyName<Integer> househalf() {
        return new PropertyName<Integer>("househalf");
    }

    /**
     * housepartのプロパティ名を返します。
     * 
     * @return housepartのプロパティ名
     */
    public static PropertyName<Integer> housepart() {
        return new PropertyName<Integer>("housepart");
    }

    /**
     * houseupperのプロパティ名を返します。
     * 
     * @return houseupperのプロパティ名
     */
    public static PropertyName<Integer> houseupper() {
        return new PropertyName<Integer>("houseupper");
    }

    /**
     * houselowerのプロパティ名を返します。
     * 
     * @return houselowerのプロパティ名
     */
    public static PropertyName<Integer> houselower() {
        return new PropertyName<Integer>("houselower");
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
    public static class _DisasterhouseregidentDataNames extends PropertyName<DisasterhouseregidentData> {

        /**
         * インスタンスを構築します。
         */
        public _DisasterhouseregidentDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _DisasterhouseregidentDataNames(final String name) {
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
        public _DisasterhouseregidentDataNames(final PropertyName<?> parent, final String name) {
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
         * houseallのプロパティ名を返します。
         *
         * @return houseallのプロパティ名
         */
        public PropertyName<Integer> houseall() {
            return new PropertyName<Integer>(this, "houseall");
        }

        /**
         * househalfのプロパティ名を返します。
         *
         * @return househalfのプロパティ名
         */
        public PropertyName<Integer> househalf() {
            return new PropertyName<Integer>(this, "househalf");
        }

        /**
         * housepartのプロパティ名を返します。
         *
         * @return housepartのプロパティ名
         */
        public PropertyName<Integer> housepart() {
            return new PropertyName<Integer>(this, "housepart");
        }

        /**
         * houseupperのプロパティ名を返します。
         *
         * @return houseupperのプロパティ名
         */
        public PropertyName<Integer> houseupper() {
            return new PropertyName<Integer>(this, "houseupper");
        }

        /**
         * houselowerのプロパティ名を返します。
         *
         * @return houselowerのプロパティ名
         */
        public PropertyName<Integer> houselower() {
            return new PropertyName<Integer>(this, "houselower");
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
