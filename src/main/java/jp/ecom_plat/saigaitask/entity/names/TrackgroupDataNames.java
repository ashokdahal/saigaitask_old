/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TrackgroupData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/08/05 16:29:09")
public class TrackgroupDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Record group data";
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
     * preftrackdataidのプロパティ名を返します。
     *
     * @return preftrackdataidのプロパティ名
     */
    public static PropertyName<Long> preftrackdataid() {
        return new PropertyName<Long>("preftrackdataid");
    }

    /**
     * citytrackdataidのプロパティ名を返します。
     *
     * @return citytrackdataidのプロパティ名
     */
    public static PropertyName<Long> citytrackdataid() {
        return new PropertyName<Long>("citytrackdataid");
    }

    /**
     * prefTrackDataのプロパティ名を返します。
     *
     * @return prefTrackDataのプロパティ名
     */
    public static _TrackDataNames prefTrackData() {
        return new _TrackDataNames("prefTrackData");
    }

    /**
     * cityTrackDataのプロパティ名を返します。
     *
     * @return cityTrackDataのプロパティ名
     */
    public static _TrackDataNames cityTrackData() {
        return new _TrackDataNames("cityTrackData");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TrackgroupDataNames extends PropertyName<TrackgroupData> {

        /**
         * インスタンスを構築します。
         */
        public _TrackgroupDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TrackgroupDataNames(final String name) {
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
        public _TrackgroupDataNames(final PropertyName<?> parent, final String name) {
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
         * preftrackdataidのプロパティ名を返します。
         *
         * @return preftrackdataidのプロパティ名
         */
        public PropertyName<Long> preftrackdataid() {
            return new PropertyName<Long>(this, "preftrackdataid");
        }

        /**
         * citytrackdataidのプロパティ名を返します。
         *
         * @return citytrackdataidのプロパティ名
         */
        public PropertyName<Long> citytrackdataid() {
            return new PropertyName<Long>(this, "citytrackdataid");
        }

        /**
         * prefTrackDataのプロパティ名を返します。
         *
         * @return prefTrackDataのプロパティ名
         */
        public _TrackDataNames prefTrackData() {
            return new _TrackDataNames(this, "prefTrackData");
        }

        /**
         * cityTrackDataのプロパティ名を返します。
         *
         * @return cityTrackDataのプロパティ名
         */
        public _TrackDataNames cityTrackData() {
            return new _TrackDataNames(this, "cityTrackData");
        }
    }
}
