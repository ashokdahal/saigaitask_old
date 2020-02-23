/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.FacebookMaster;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link FacebookMaster}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/08/29 18:39:12")
public class FacebookMasterNames {

    /**
     * idのプロパティ名を返します。
     *
     * @return idのプロパティ名
     */
    public static PropertyName<Integer> id() {
        return new PropertyName<Integer>("id");
    }

    /**
     * appidのプロパティ名を返します。
     *
     * @return appidのプロパティ名
     */
    public static PropertyName<String> appid() {
        return new PropertyName<String>("appid");
    }

    /**
     * appsecretのプロパティ名を返します。
     *
     * @return appsecretのプロパティ名
     */
    public static PropertyName<String> appsecret() {
        return new PropertyName<String>("appsecret");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _FacebookMasterNames extends PropertyName<FacebookMaster> {

        /**
         * インスタンスを構築します。
         */
        public _FacebookMasterNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _FacebookMasterNames(final String name) {
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
        public _FacebookMasterNames(final PropertyName<?> parent, final String name) {
            super(parent, name);
        }

        /**
         * idのプロパティ名を返します。
         *
         * @return idのプロパティ名
         */
        public PropertyName<Integer> id() {
            return new PropertyName<Integer>(this, "id");
        }

        /**
         * appidのプロパティ名を返します。
         *
         * @return appidのプロパティ名
         */
        public PropertyName<String> appid() {
            return new PropertyName<String>(this, "appid");
        }

        /**
         * appsecretのプロパティ名を返します。
         *
         * @return appsecretのプロパティ名
         */
        public PropertyName<String> appsecret() {
            return new PropertyName<String>(this, "appsecret");
        }
    }
}
