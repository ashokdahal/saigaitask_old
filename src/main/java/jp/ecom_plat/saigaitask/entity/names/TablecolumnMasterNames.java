/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.db.TablecolumnMaster;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TablecolumnMaster}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/08/09 12:57:52")
public class TablecolumnMasterNames {

    /**
     * idのプロパティ名を返します。
     * 
     * @return idのプロパティ名
     */
    public static PropertyName<Integer> id() {
        return new PropertyName<Integer>("id");
    }

    /**
     * tablenameのプロパティ名を返します。
     * 
     * @return tablenameのプロパティ名
     */
    public static PropertyName<String> tablename() {
        return new PropertyName<String>("tablename");
    }

    /**
     * columnnameのプロパティ名を返します。
     * 
     * @return columnnameのプロパティ名
     */
    public static PropertyName<String> columnname() {
        return new PropertyName<String>("columnname");
    }

    /**
     * nullableのプロパティ名を返します。
     * 
     * @return nullableのプロパティ名
     */
    public static PropertyName<Boolean> nullable() {
        return new PropertyName<Boolean>("nullable");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TablecolumnMasterNames extends PropertyName<TablecolumnMaster> {

        /**
         * インスタンスを構築します。
         */
        public _TablecolumnMasterNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TablecolumnMasterNames(final String name) {
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
        public _TablecolumnMasterNames(final PropertyName<?> parent, final String name) {
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
         * tablenameのプロパティ名を返します。
         *
         * @return tablenameのプロパティ名
         */
        public PropertyName<String> tablename() {
            return new PropertyName<String>(this, "tablename");
        }

        /**
         * columnnameのプロパティ名を返します。
         *
         * @return columnnameのプロパティ名
         */
        public PropertyName<String> columnname() {
            return new PropertyName<String>(this, "columnname");
        }

        /**
         * nullableのプロパティ名を返します。
         *
         * @return nullableのプロパティ名
         */
        public PropertyName<Boolean> nullable() {
            return new PropertyName<Boolean>(this, "nullable");
        }
    }
}
