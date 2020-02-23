/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.Meteoseismicarea1Master;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link Meteoseismicarea1Master}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/09 19:42:52")
public class Meteoseismicarea1MasterNames {

    /**
     * idのプロパティ名を返します。
     * 
     * @return idのプロパティ名
     */
    public static PropertyName<Integer> id() {
        return new PropertyName<Integer>("id");
    }

    /**
     * nameのプロパティ名を返します。
     * 
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
    }

    /**
     * codeのプロパティ名を返します。
     * 
     * @return codeのプロパティ名
     */
    public static PropertyName<String> code() {
        return new PropertyName<String>("code");
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
     * disporderのプロパティ名を返します。
     * 
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _Meteoseismicarea1MasterNames extends PropertyName<Meteoseismicarea1Master> {

        /**
         * インスタンスを構築します。
         */
        public _Meteoseismicarea1MasterNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _Meteoseismicarea1MasterNames(final String name) {
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
        public _Meteoseismicarea1MasterNames(final PropertyName<?> parent, final String name) {
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
         * nameのプロパティ名を返します。
         *
         * @return nameのプロパティ名
         */
        public PropertyName<String> name() {
            return new PropertyName<String>(this, "name");
        }

        /**
         * codeのプロパティ名を返します。
         *
         * @return codeのプロパティ名
         */
        public PropertyName<String> code() {
            return new PropertyName<String>(this, "code");
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
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
        }
    }
}
