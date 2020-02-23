/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.TwitterMaster;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TwitterMaster}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/06/20 17:37:29")
public class TwitterMasterNames {

    /**
     * idのプロパティ名を返します。
     * 
     * @return idのプロパティ名
     */
    public static PropertyName<Integer> id() {
        return new PropertyName<Integer>("id");
    }

    /**
     * consumerkeyのプロパティ名を返します。
     * 
     * @return consumerkeyのプロパティ名
     */
    public static PropertyName<String> consumerkey() {
        return new PropertyName<String>("consumerkey");
    }

    /**
     * consumersecretのプロパティ名を返します。
     * 
     * @return consumersecretのプロパティ名
     */
    public static PropertyName<String> consumersecret() {
        return new PropertyName<String>("consumersecret");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TwitterMasterNames extends PropertyName<TwitterMaster> {

        /**
         * インスタンスを構築します。
         */
        public _TwitterMasterNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TwitterMasterNames(final String name) {
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
        public _TwitterMasterNames(final PropertyName<?> parent, final String name) {
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
         * consumerkeyのプロパティ名を返します。
         *
         * @return consumerkeyのプロパティ名
         */
        public PropertyName<String> consumerkey() {
            return new PropertyName<String>(this, "consumerkey");
        }

        /**
         * consumersecretのプロパティ名を返します。
         *
         * @return consumersecretのプロパティ名
         */
        public PropertyName<String> consumersecret() {
            return new PropertyName<String>(this, "consumersecret");
        }
    }
}
