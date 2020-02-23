/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.MeteowarningcodeMaster;
import jp.ecom_plat.saigaitask.entity.names.MeteotypeMasterNames._MeteotypeMasterNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MeteowarningcodeMaster}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/09/11 16:53:42")
public class MeteowarningcodeMasterNames {

    /**
     * idのプロパティ名を返します。
     *
     * @return idのプロパティ名
     */
    public static PropertyName<Integer> id() {
        return new PropertyName<Integer>("id");
    }

    /**
     * meteotypemasteridのプロパティ名を返します。
     *
     * @return meteotypemasteridのプロパティ名
     */
    public static PropertyName<Integer> meteotypemasterid() {
        return new PropertyName<Integer>("meteotypemasterid");
    }

    /**
     * codeのプロパティ名を返します。
     *
     * @return codeのプロパティ名
     */
    public static PropertyName<Integer> code() {
        return new PropertyName<Integer>("code");
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
     * noteのプロパティ名を返します。
     *
     * @return noteのプロパティ名
     */
    public static PropertyName<String> note() {
        return new PropertyName<String>("note");
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
     * validのプロパティ名を返します。
     *
     * @return validのプロパティ名
     */
    public static PropertyName<Boolean> valid() {
        return new PropertyName<Boolean>("valid");
    }

    /**
     * meteotypeMasterのプロパティ名を返します。
     *
     * @return meteotypeMasterのプロパティ名
     */
    public static _MeteotypeMasterNames meteotypeMaster() {
        return new _MeteotypeMasterNames("meteotypeMaster");
    }


    /**
     * @author S2JDBC-Gen
     */
    public static class _MeteowarningcodeMasterNames extends PropertyName<MeteowarningcodeMaster> {

        /**
         * インスタンスを構築します。
         */
        public _MeteowarningcodeMasterNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _MeteowarningcodeMasterNames(final String name) {
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
        public _MeteowarningcodeMasterNames(final PropertyName<?> parent, final String name) {
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
         * meteotypemasteridのプロパティ名を返します。
         *
         * @return meteotypemasteridのプロパティ名
         */
        public PropertyName<Integer> meteotypemasterid() {
            return new PropertyName<Integer>(this, "meteotypemasterid");
        }

        /**
         * codeのプロパティ名を返します。
         *
         * @return codeのプロパティ名
         */
        public PropertyName<Integer> code() {
            return new PropertyName<Integer>(this, "code");
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
         * noteのプロパティ名を返します。
         *
         * @return noteのプロパティ名
         */
        public PropertyName<String> note() {
            return new PropertyName<String>(this, "note");
        }

        /**
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
        }

        /**
         * validのプロパティ名を返します。
         *
         * @return validのプロパティ名
         */
        public PropertyName<Boolean> valid() {
            return new PropertyName<Boolean>(this, "valid");
        }

        /**
         * meteotypeMasterのプロパティ名を返します。
         *
         * @return meteotypeMasterのプロパティ名
         */
        public _MeteotypeMasterNames meteotypeMaster() {
            return new _MeteotypeMasterNames(this, "meteotypeMaster");
        }
    }
}
