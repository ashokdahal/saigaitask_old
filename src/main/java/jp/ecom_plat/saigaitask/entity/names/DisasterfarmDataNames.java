/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.DisasterfarmData;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link DisasterfarmData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/22 14:41:59")
public class DisasterfarmDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Agricultural damage data";
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
     * field1のプロパティ名を返します。
     * 
     * @return field1のプロパティ名
     */
    public static PropertyName<Float> field1() {
        return new PropertyName<Float>("field1");
    }

    /**
     * field2のプロパティ名を返します。
     * 
     * @return field2のプロパティ名
     */
    public static PropertyName<Float> field2() {
        return new PropertyName<Float>("field2");
    }

    /**
     * farm1のプロパティ名を返します。
     * 
     * @return farm1のプロパティ名
     */
    public static PropertyName<Float> farm1() {
        return new PropertyName<Float>("farm1");
    }

    /**
     * farm2のプロパティ名を返します。
     * 
     * @return farm2のプロパティ名
     */
    public static PropertyName<Float> farm2() {
        return new PropertyName<Float>("farm2");
    }

    /**
     * farmmountのプロパティ名を返します。
     * 
     * @return farmmountのプロパティ名
     */
    public static PropertyName<Integer> farmmount() {
        return new PropertyName<Integer>("farmmount");
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
    public static class _DisasterfarmDataNames extends PropertyName<DisasterfarmData> {

        /**
         * インスタンスを構築します。
         */
        public _DisasterfarmDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _DisasterfarmDataNames(final String name) {
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
        public _DisasterfarmDataNames(final PropertyName<?> parent, final String name) {
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
         * field1のプロパティ名を返します。
         *
         * @return field1のプロパティ名
         */
        public PropertyName<Float> field1() {
            return new PropertyName<Float>(this, "field1");
        }

        /**
         * field2のプロパティ名を返します。
         *
         * @return field2のプロパティ名
         */
        public PropertyName<Float> field2() {
            return new PropertyName<Float>(this, "field2");
        }

        /**
         * farm1のプロパティ名を返します。
         *
         * @return farm1のプロパティ名
         */
        public PropertyName<Float> farm1() {
            return new PropertyName<Float>(this, "farm1");
        }

        /**
         * farm2のプロパティ名を返します。
         *
         * @return farm2のプロパティ名
         */
        public PropertyName<Float> farm2() {
            return new PropertyName<Float>(this, "farm2");
        }

        /**
         * farmmountのプロパティ名を返します。
         *
         * @return farmmountのプロパティ名
         */
        public PropertyName<Integer> farmmount() {
            return new PropertyName<Integer>(this, "farmmount");
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