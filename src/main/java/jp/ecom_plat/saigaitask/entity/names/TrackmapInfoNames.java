/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;
import jp.ecom_plat.saigaitask.entity.names.TracktableInfoNames._TracktableInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TrackmapInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/23 16:58:46")
public class TrackmapInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Recorded map data";
	}

    /**
     * idのプロパティ名を返します。
     *
     * @return idのプロパティ名
     */
    public static PropertyName<Long> id() {
        return new PropertyName<Long>("id");
    }

//    /**
//     * trackdataidのプロパティ名を返します。
//     *
//     * @return trackdataidのプロパティ名
//     */
//    @Deprecated
//    public static PropertyName<Long> trackdataid() {
//        return new PropertyName<Long>("trackdataid");
//    }

    /**
     * communityidのプロパティ名を返します。
     *
     * @return communityidのプロパティ名
     */
    public static PropertyName<Integer> communityid() {
        return new PropertyName<Integer>("communityid");
    }

    /**
     * mapgroupidのプロパティ名を返します。
     *
     * @return mapgroupidのプロパティ名
     */
    public static PropertyName<Integer> mapgroupid() {
        return new PropertyName<Integer>("mapgroupid");
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
     * trackDataのプロパティ名を返します。
     *
     * @return trackDataのプロパティ名
     */
    @Deprecated
    public static _TrackDataNames trackData() {
        return new _TrackDataNames("trackData");
    }

    /**
     * trackDatasのプロパティ名を返します。
     *
     * @return trackDatasのプロパティ名
     */
    public static _TrackDataNames trackDatas() {
        return new _TrackDataNames("trackDatas");
    }

    /**
     * tracktableInfosのプロパティ名を返します。
     *
     * @return tracktableInfosのプロパティ名
     */
    public static _TracktableInfoNames tracktableInfos() {
        return new _TracktableInfoNames("tracktableInfos");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TrackmapInfoNames extends PropertyName<TrackmapInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TrackmapInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TrackmapInfoNames(final String name) {
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
        public _TrackmapInfoNames(final PropertyName<?> parent, final String name) {
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

//        /**
//         * trackdataidのプロパティ名を返します。
//         *
//         * @return trackdataidのプロパティ名
//         */
//        @Deprecated
//        public PropertyName<Long> trackdataid() {
//            return new PropertyName<Long>(this, "trackdataid");
//        }

        /**
         * communityidのプロパティ名を返します。
         *
         * @return communityidのプロパティ名
         */
        public PropertyName<Integer> communityid() {
            return new PropertyName<Integer>(this, "communityid");
        }

        /**
         * mapgroupidのプロパティ名を返します。
         *
         * @return mapgroupidのプロパティ名
         */
        public PropertyName<Integer> mapgroupid() {
            return new PropertyName<Integer>(this, "mapgroupid");
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
         * trackDataのプロパティ名を返します。
         *
         * @return trackDataのプロパティ名
         */
        @Deprecated
        public _TrackDataNames trackData() {
            return new _TrackDataNames(this, "trackData");
        }

        /**
         * trackDatasのプロパティ名を返します。
         *
         * @return trackDatasのプロパティ名
         */
        public _TrackDataNames trackDatas() {
            return new _TrackDataNames(this, "trackDatas");
        }

        /**
         * tracktableInfosのプロパティ名を返します。
         *
         * @return tracktableInfosのプロパティ名
         */
        public _TracktableInfoNames tracktableInfos() {
            return new _TracktableInfoNames(this, "tracktableInfos");
        }
    }
}
