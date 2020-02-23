/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.DisastercasualtiesData;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link DisastercasualtiesData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/22 14:41:59")
public class DisastercasualtiesDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Loss of lives data";
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
     * casualties1のプロパティ名を返します。
     * 
     * @return casualties1のプロパティ名
     */
    public static PropertyName<Integer> casualties1() {
        return new PropertyName<Integer>("casualties1");
    }

    /**
     * casualties2のプロパティ名を返します。
     * 
     * @return casualties2のプロパティ名
     */
    public static PropertyName<Integer> casualties2() {
        return new PropertyName<Integer>("casualties2");
    }

    /**
     * casualties3のプロパティ名を返します。
     * 
     * @return casualties3のプロパティ名
     */
    public static PropertyName<Integer> casualties3() {
        return new PropertyName<Integer>("casualties3");
    }

    /**
     * casualties4のプロパティ名を返します。
     * 
     * @return casualties4のプロパティ名
     */
    public static PropertyName<Integer> casualties4() {
        return new PropertyName<Integer>("casualties4");
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
    public static class _DisastercasualtiesDataNames extends PropertyName<DisastercasualtiesData> {

        /**
         * インスタンスを構築します。
         */
        public _DisastercasualtiesDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _DisastercasualtiesDataNames(final String name) {
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
        public _DisastercasualtiesDataNames(final PropertyName<?> parent, final String name) {
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
         * casualties1のプロパティ名を返します。
         *
         * @return casualties1のプロパティ名
         */
        public PropertyName<Integer> casualties1() {
            return new PropertyName<Integer>(this, "casualties1");
        }

        /**
         * casualties2のプロパティ名を返します。
         *
         * @return casualties2のプロパティ名
         */
        public PropertyName<Integer> casualties2() {
            return new PropertyName<Integer>(this, "casualties2");
        }

        /**
         * casualties3のプロパティ名を返します。
         *
         * @return casualties3のプロパティ名
         */
        public PropertyName<Integer> casualties3() {
            return new PropertyName<Integer>(this, "casualties3");
        }

        /**
         * casualties4のプロパティ名を返します。
         *
         * @return casualties4のプロパティ名
         */
        public PropertyName<Integer> casualties4() {
            return new PropertyName<Integer>(this, "casualties4");
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
