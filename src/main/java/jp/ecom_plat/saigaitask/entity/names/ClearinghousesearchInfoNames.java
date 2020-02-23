/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousesearchInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ClearinghousesearchInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/08/19 12:45:15")
public class ClearinghousesearchInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Clearing house search info";
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
     * intervalのプロパティ名を返します。
     * 
     * @return intervalのプロパティ名
     */
    public static PropertyName<Integer> interval() {
        return new PropertyName<Integer>("interval");
    }

    /**
     * queryのプロパティ名を返します。
     * 
     * @return queryのプロパティ名
     */
    public static PropertyName<String> query() {
        return new PropertyName<String>("query");
    }

    /**
     * areaのプロパティ名を返します。
     * 
     * @return areaのプロパティ名
     */
    public static PropertyName<String> area() {
        return new PropertyName<String>("area");
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
    public static class _ClearinghousesearchInfoNames extends PropertyName<ClearinghousesearchInfo> {

        /**
         * インスタンスを構築します。
         */
        public _ClearinghousesearchInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _ClearinghousesearchInfoNames(final String name) {
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
        public _ClearinghousesearchInfoNames(final PropertyName<?> parent, final String name) {
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
         * intervalのプロパティ名を返します。
         *
         * @return intervalのプロパティ名
         */
        public PropertyName<Integer> interval() {
            return new PropertyName<Integer>(this, "interval");
        }

        /**
         * queryのプロパティ名を返します。
         *
         * @return queryのプロパティ名
         */
        public PropertyName<String> query() {
            return new PropertyName<String>(this, "query");
        }

        /**
         * areaのプロパティ名を返します。
         *
         * @return areaのプロパティ名
         */
        public PropertyName<String> area() {
            return new PropertyName<String>(this, "area");
        }

        /**
         * localgovInfoのプロパティ名を返します。
         * 
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }
    }
}