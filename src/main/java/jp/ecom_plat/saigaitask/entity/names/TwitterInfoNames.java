/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TwitterInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TwitterInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/06/20 17:37:29")
public class TwitterInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Twitter settings";
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
     * accesstokenのプロパティ名を返します。
     * 
     * @return accesstokenのプロパティ名
     */
    public static PropertyName<String> accesstoken() {
        return new PropertyName<String>("accesstoken");
    }

    /**
     * accesstokensecretのプロパティ名を返します。
     * 
     * @return accesstokensecretのプロパティ名
     */
    public static PropertyName<String> accesstokensecret() {
        return new PropertyName<String>("accesstokensecret");
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
    public static class _TwitterInfoNames extends PropertyName<TwitterInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TwitterInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TwitterInfoNames(final String name) {
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
        public _TwitterInfoNames(final PropertyName<?> parent, final String name) {
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
         * accesstokenのプロパティ名を返します。
         *
         * @return accesstokenのプロパティ名
         */
        public PropertyName<String> accesstoken() {
            return new PropertyName<String>(this, "accesstoken");
        }

        /**
         * accesstokensecretのプロパティ名を返します。
         *
         * @return accesstokensecretのプロパティ名
         */
        public PropertyName<String> accesstokensecret() {
            return new PropertyName<String>(this, "accesstokensecret");
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
