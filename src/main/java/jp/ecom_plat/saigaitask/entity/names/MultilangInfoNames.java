/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.entity.names.MultilangmesInfoNames._MultilangmesInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MultilangInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/08/13 9:08:49")
public class MultilangInfoNames {

    /**
     * idのプロパティ名を返します。
     *
     * @return idのプロパティ名
     */
    public static PropertyName<Long> id() {
        return new PropertyName<Long>("id");
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
     * nameのプロパティ名を返します。
     *
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
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
     * updatetimeのプロパティ名を返します。
     *
     * @return updatetimeのプロパティ名
     */
    public static PropertyName<Timestamp> updatetime() {
        return new PropertyName<Timestamp>("updatetime");
    }

    /**
     * multilangmesInfoListのプロパティ名を返します。
     *
     * @return multilangmesInfoListのプロパティ名
     */
    public static _MultilangmesInfoNames multilangmesInfoList() {
        return new _MultilangmesInfoNames("multilangmesInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MultilangInfoNames extends PropertyName<MultilangInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MultilangInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _MultilangInfoNames(final String name) {
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
        public _MultilangInfoNames(final PropertyName<?> parent, final String name) {
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
         * codeのプロパティ名を返します。
         *
         * @return codeのプロパティ名
         */
        public PropertyName<String> code() {
            return new PropertyName<String>(this, "code");
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
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>("disporder");
        }

        /**
         * updatetimeのプロパティ名を返します。
         *
         * @return updatetimeのプロパティ名
         */
        public PropertyName<Timestamp> updatetime() {
            return new PropertyName<Timestamp>(this, "updatetime");
        }

        /**
         * multilangmesInfoListのプロパティ名を返します。
         *
         * @return multilangmesInfoListのプロパティ名
         */
        public _MultilangmesInfoNames multilangmesInfoList() {
            return new _MultilangmesInfoNames("multilangmesInfoList");
        }
   }
}
