/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ImporttrackInfo;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ImporttrackInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/02/18 15:15:38")
public class ImporttrackInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Communication disconnected import data";
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
     * localgovinfoidのプロパティ名を返します。
     *
     * @return localgovinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovinfoid() {
        return new PropertyName<Long>("localgovinfoid");
    }

    /**
     * oldlocalgovinfoidのプロパティ名を返します。
     *
     * @return oldlocalgovinfoidのプロパティ名
     */
    public static PropertyName<Long> oldlocalgovinfoid() {
        return new PropertyName<Long>("oldlocalgovinfoid");
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
     * oldtrackdataidのプロパティ名を返します。
     *
     * @return oldtrackdataidのプロパティ名
     */
    public static PropertyName<Long> oldtrackdataid() {
        return new PropertyName<Long>("oldtrackdataid");
    }

    /**
     * mapidのプロパティ名を返します。
     *
     * @return mapidのプロパティ名
     */
    public static PropertyName<Long> mapid() {
        return new PropertyName<Long>("mapid");
    }

    /**
     * oldmapidのプロパティ名を返します。
     *
     * @return oldmapidのプロパティ名
     */
    public static PropertyName<Long> oldmapid() {
        return new PropertyName<Long>("oldmapid");
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
    public static class _ImporttrackInfoNames extends PropertyName<ImporttrackInfo> {

        /**
         * インスタンスを構築します。
         */
        public _ImporttrackInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _ImporttrackInfoNames(final String name) {
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
        public _ImporttrackInfoNames(final PropertyName<?> parent, final String name) {
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
         * localgovinfoidのプロパティ名を返します。
         *
         * @return localgovinfoidのプロパティ名
         */
        public PropertyName<Long> localgovinfoid() {
            return new PropertyName<Long>(this, "localgovinfoid");
        }

        /**
         * oldlocalgovinfoidのプロパティ名を返します。
         *
         * @return oldlocalgovinfoidのプロパティ名
         */
        public PropertyName<Long> oldlocalgovinfoid() {
            return new PropertyName<Long>(this, "oldlocalgovinfoid");
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
         * oldtrackdataidのプロパティ名を返します。
         *
         * @return oldtrackdataidのプロパティ名
         */
        public PropertyName<Long> oldtrackdataid() {
            return new PropertyName<Long>(this, "oldtrackdataid");
        }

        /**
         * mapidのプロパティ名を返します。
         *
         * @return mapidのプロパティ名
         */
        public PropertyName<Long> mapid() {
            return new PropertyName<Long>(this, "mapid");
        }

        /**
         * oldmapidのプロパティ名を返します。
         *
         * @return oldmapidのプロパティ名
         */
        public PropertyName<Long> oldmapid() {
            return new PropertyName<Long>(this, "oldmapid");
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
