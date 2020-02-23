/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.DisasterfireData;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link DisasterfireData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/22 14:41:59")
public class DisasterfireDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Fire data";
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
     * fire1のプロパティ名を返します。
     * 
     * @return fire1のプロパティ名
     */
    public static PropertyName<Integer> fire1() {
        return new PropertyName<Integer>("fire1");
    }

    /**
     * fire2のプロパティ名を返します。
     * 
     * @return fire2のプロパティ名
     */
    public static PropertyName<Integer> fire2() {
        return new PropertyName<Integer>("fire2");
    }

    /**
     * fire3のプロパティ名を返します。
     * 
     * @return fire3のプロパティ名
     */
    public static PropertyName<Integer> fire3() {
        return new PropertyName<Integer>("fire3");
    }

    /**
     * buildmountのプロパティ名を返します。
     * 
     * @return buildmountのプロパティ名
     */
    public static PropertyName<Integer> buildmount() {
        return new PropertyName<Integer>("buildmount");
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
    public static class _DisasterfireDataNames extends PropertyName<DisasterfireData> {

        /**
         * インスタンスを構築します。
         */
        public _DisasterfireDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _DisasterfireDataNames(final String name) {
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
        public _DisasterfireDataNames(final PropertyName<?> parent, final String name) {
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
         * fire1のプロパティ名を返します。
         *
         * @return fire1のプロパティ名
         */
        public PropertyName<Integer> fire1() {
            return new PropertyName<Integer>(this, "fire1");
        }

        /**
         * fire2のプロパティ名を返します。
         *
         * @return fire2のプロパティ名
         */
        public PropertyName<Integer> fire2() {
            return new PropertyName<Integer>(this, "fire2");
        }

        /**
         * fire3のプロパティ名を返します。
         *
         * @return fire3のプロパティ名
         */
        public PropertyName<Integer> fire3() {
            return new PropertyName<Integer>(this, "fire3");
        }

        /**
         * buildmountのプロパティ名を返します。
         *
         * @return buildmountのプロパティ名
         */
        public PropertyName<Integer> buildmount() {
            return new PropertyName<Integer>(this, "buildmount");
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
