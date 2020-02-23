/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ConvertidData;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ConvertidData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/02/18 11:51:45")
public class ConvertidDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Communication disconnected ID transformation data";
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
     * entitynameのプロパティ名を返します。
     * 
     * @return entitynameのプロパティ名
     */
    public static PropertyName<String> entityname() {
        return new PropertyName<String>("entityname");
    }

    /**
     * idnameのプロパティ名を返します。
     * 
     * @return idnameのプロパティ名
     */
    public static PropertyName<String> idname() {
        return new PropertyName<String>("idname");
    }

    /**
     * oldvalのプロパティ名を返します。
     * 
     * @return oldvalのプロパティ名
     */
    public static PropertyName<String> oldval() {
        return new PropertyName<String>("oldval");
    }

    /**
     * newvalのプロパティ名を返します。
     * 
     * @return newvalのプロパティ名
     */
    public static PropertyName<String> newval() {
        return new PropertyName<String>("newval");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _ConvertidDataNames extends PropertyName<ConvertidData> {

        /**
         * インスタンスを構築します。
         */
        public _ConvertidDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _ConvertidDataNames(final String name) {
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
        public _ConvertidDataNames(final PropertyName<?> parent, final String name) {
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
         * entitynameのプロパティ名を返します。
         *
         * @return entitynameのプロパティ名
         */
        public PropertyName<String> entityname() {
            return new PropertyName<String>(this, "entityname");
        }

        /**
         * idnameのプロパティ名を返します。
         *
         * @return idnameのプロパティ名
         */
        public PropertyName<String> idname() {
            return new PropertyName<String>(this, "idname");
        }

        /**
         * oldvalのプロパティ名を返します。
         *
         * @return oldvalのプロパティ名
         */
        public PropertyName<String> oldval() {
            return new PropertyName<String>(this, "oldval");
        }

        /**
         * newvalのプロパティ名を返します。
         *
         * @return newvalのプロパティ名
         */
        public PropertyName<String> newval() {
            return new PropertyName<String>(this, "newval");
        }
    }
}
