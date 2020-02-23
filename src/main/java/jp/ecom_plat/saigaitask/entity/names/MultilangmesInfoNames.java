/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.MultilangmesInfo;
import jp.ecom_plat.saigaitask.entity.names.MultilangInfoNames._MultilangInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MultilangmesInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/08/13 9:10:29")
public class MultilangmesInfoNames {

    /**
     * idのプロパティ名を返します。
     *
     * @return idのプロパティ名
     */
    public static PropertyName<Long> id() {
        return new PropertyName<Long>("id");
    }

    /**
     * multilanginfoidのプロパティ名を返します。
     *
     * @return multilanginfoidのプロパティ名
     */
    public static PropertyName<Long> multilanginfoid() {
        return new PropertyName<Long>("multilanginfoid");
    }

    /**
     * messageidのプロパティ名を返します。
     *
     * @return messageidのプロパティ名
     */
    public static PropertyName<String> messageid() {
        return new PropertyName<String>("messageid");
    }

    /**
     * messageのプロパティ名を返します。
     *
     * @return messageのプロパティ名
     */
    public static PropertyName<String> message() {
        return new PropertyName<String>("message");
    }

    /**
     * multilangInfoのプロパティ名を返します。
     *
     * @return multilangInfoのプロパティ名
     */
    public static _MultilangInfoNames multilangInfo() {
        return new _MultilangInfoNames("multilangInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MultilangmesInfoNames extends PropertyName<MultilangmesInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MultilangmesInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _MultilangmesInfoNames(final String name) {
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
        public _MultilangmesInfoNames(final PropertyName<?> parent, final String name) {
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
         * multilanginfoidのプロパティ名を返します。
         *
         * @return multilanginfoidのプロパティ名
         */
        public PropertyName<Long> multilanginfoid() {
            return new PropertyName<Long>(this, "multilanginfoid");
        }

        /**
         * messageidのプロパティ名を返します。
         *
         * @return messageidのプロパティ名
         */
        public PropertyName<String> messageid() {
            return new PropertyName<String>(this, "messageid");
        }

        /**
         * messageのプロパティ名を返します。
         *
         * @return messageのプロパティ名
         */
        public PropertyName<String> message() {
            return new PropertyName<String>(this, "message");
        }

        /**
         * multilangInfoのプロパティ名を返します。
         *
         * @return multilangInfoのプロパティ名
         */
        public _MultilangInfoNames multilangInfo() {
            return new _MultilangInfoNames("multilangInfo");
        }
   }
}
