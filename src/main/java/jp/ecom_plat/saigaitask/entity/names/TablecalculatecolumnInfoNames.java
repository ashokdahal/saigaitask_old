/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TablecalculatecolumnInfo;
import jp.ecom_plat.saigaitask.entity.names.TablecalculateInfoNames._TablecalculateInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TablecalculatecolumnInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/12/05 16:31:04")
public class TablecalculatecolumnInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Table calculation item info";
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
     * tablemasterinfoidのプロパティ名を返します。
     *
     * @return tablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> tablemasterinfoid() {
        return new PropertyName<Long>("tablemasterinfoid");
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
     * calculateListのプロパティ名を返します。
     *
     * @return calculateListのプロパティ名
     */
    public static _TablecalculateInfoNames calculateList() {
        return new _TablecalculateInfoNames("calculateList");
    }

    /**
     * tablemasterInfoのプロパティ名を返します。
     *
     * @return tablemasterInfoのプロパティ名
     */
    public static _TablemasterInfoNames tablemasterInfo() {
        return new _TablemasterInfoNames("tablemasterInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TablecalculatecolumnInfoNames extends PropertyName<TablecalculatecolumnInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TablecalculatecolumnInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TablecalculatecolumnInfoNames(final String name) {
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
        public _TablecalculatecolumnInfoNames(final PropertyName<?> parent, final String name) {
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
         * tablemasterinfoidのプロパティ名を返します。
         *
         * @return tablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> tablemasterinfoid() {
            return new PropertyName<Long>(this, "tablemasterinfoid");
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
         * calculateListのプロパティ名を返します。
         *
         * @return calculateListのプロパティ名
         */
        public _TablecalculateInfoNames calculateList() {
            return new _TablecalculateInfoNames(this, "calculateList");
        }

        /**
         * tablemasterInfoのプロパティ名を返します。
         *
         * @return tablemasterInfoのプロパティ名
         */
        public _TablemasterInfoNames tablemasterInfo() {
            return new _TablemasterInfoNames(this, "tablemasterInfo");
        }
    }
}
