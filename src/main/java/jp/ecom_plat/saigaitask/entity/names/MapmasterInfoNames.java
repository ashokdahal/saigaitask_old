/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MapmasterInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/11 18:48:37")
public class MapmasterInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Map master info";
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
     * restrictedextentのプロパティ名を返します。
     *
     * @return restrictedextentのプロパティ名
     */
    public static PropertyName<String> restrictedextent() {
        return new PropertyName<String>("restrictedextent");
    }

    /**
     * copyのプロパティ名を返します。
     *
     * @return copyのプロパティ名
     */
    public static PropertyName<Boolean> copy() {
        return new PropertyName<Boolean>("copy");
    }

    /**
     * deletedのプロパティ名を返します。
     *
     * @return deletedのプロパティ名
     */
    public static PropertyName<Boolean> deleted() {
        return new PropertyName<Boolean>("deleted");
    }

    /**
     * localgovInfoのプロパティ名を返します。
     *
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MapmasterInfoNames extends PropertyName<MapmasterInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MapmasterInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _MapmasterInfoNames(final String name) {
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
        public _MapmasterInfoNames(final PropertyName<?> parent, final String name) {
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
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * copyのプロパティ名を返します。
         *
         * @return copyのプロパティ名
         */
        public PropertyName<Boolean> copy() {
            return new PropertyName<Boolean>(this, "copy");
        }

        /**
         * deletedのプロパティ名を返します。
         *
         * @return deletedのプロパティ名
         */
        public PropertyName<Boolean> deleted() {
            return new PropertyName<Boolean>(this, "deleted");
        }
    }
}
