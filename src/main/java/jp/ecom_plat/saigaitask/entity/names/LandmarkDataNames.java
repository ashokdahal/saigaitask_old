/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.LandmarkData;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LandmarkInfoNames._LandmarkInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link LandmarkData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/04/10 17:45:02")
public class LandmarkDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Landmark data info";
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
     * landmarkinfoidのプロパティ名を返します。
     *
     * @return landmarkinfoidのプロパティ名
     */
    public static PropertyName<Long> landmarkinfoid() {
        return new PropertyName<Long>("landmarkinfoid");
    }

    /**
     * groupidのプロパティ名を返します。
     *
     * @return groupidのプロパティ名
     */
    public static PropertyName<Long> groupid() {
        return new PropertyName<Long>("groupid");
    }

    /**
     * landmarkのプロパティ名を返します。
     *
     * @return landmarkのプロパティ名
     */
    public static PropertyName<String> landmark() {
        return new PropertyName<String>("landmark");
    }

    /**
     * latitudeのプロパティ名を返します。
     *
     * @return latitudeのプロパティ名
     */
    public static PropertyName<Double> latitude() {
        return new PropertyName<Double>("latitude");
    }

    /**
     * longitudeのプロパティ名を返します。
     *
     * @return longitudeのプロパティ名
     */
    public static PropertyName<Double> longitude() {
        return new PropertyName<Double>("longitude");
    }

    /**
     * landmarkinfoのプロパティ名を返します。
     *
     * @return landmarkinfoのプロパティ名
     */
    public static _LandmarkInfoNames landmarkInfo() {
        return new _LandmarkInfoNames("landmarkInfo");
    }

    /**
     * groupInfoのプロパティ名を返します。
     *
     * @return groupInfoのプロパティ名
     */
    public static _GroupInfoNames groupInfo() {
        return new _GroupInfoNames("groupInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _LandmarkDataNames extends PropertyName<LandmarkData> {

        /**
         * インスタンスを構築します。
         */
        public _LandmarkDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _LandmarkDataNames(final String name) {
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
        public _LandmarkDataNames(final PropertyName<?> parent, final String name) {
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
         * landmarkinfoidのプロパティ名を返します。
         *
         * @return landmarkinfoidのプロパティ名
         */
        public PropertyName<Long> landmarkinfoid() {
            return new PropertyName<Long>(this, "landmarkinfoid");
        }

        /**
         * groupidのプロパティ名を返します。
         *
         * @return groupidのプロパティ名
         */
        public PropertyName<Long> groupid() {
            return new PropertyName<Long>(this, "groupid");
        }

        /**
         * landmarkのプロパティ名を返します。
         *
         * @return landmarkのプロパティ名
         */
        public PropertyName<String> landmark() {
            return new PropertyName<String>(this, "landmark");
        }

        /**
         * latitudeのプロパティ名を返します。
         *
         * @return latitudeのプロパティ名
         */
        public PropertyName<Double> latitude() {
            return new PropertyName<Double>(this, "latitude");
        }

        /**
         * longitudeのプロパティ名を返します。
         *
         * @return longitudeのプロパティ名
         */
        public PropertyName<Double> longitude() {
            return new PropertyName<Double>(this, "longitude");
        }

        /**
         * landmarkinfoのプロパティ名を返します。
         *
         * @return landmarkinfoのプロパティ名
         */
        public _LandmarkInfoNames landmarkInfo() {
            return new _LandmarkInfoNames(this, "landmarkInfo");
        }

        /**
         * groupInfoのプロパティ名を返します。
         *
         * @return groupInfoのプロパティ名
         */
        public _GroupInfoNames groupInfo() {
            return new _GroupInfoNames(this, "groupInfo");
        }
    }
}
