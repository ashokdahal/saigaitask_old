/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TableresetcolumnData;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TableresetcolumnData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2016/08/05 14:58:06")
public class TableresetcolumnDataNames implements EntityNameInterface {
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	@Override
	public String entityName() {
		return ("Table reset target attribution info");
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
     * attridのプロパティ名を返します。
     *
     * @return attridのプロパティ名
     */
    public static PropertyName<String> attrid() {
        return new PropertyName<String>("attrid");
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
    public static class _TableresetcolumnDataNames extends PropertyName<TableresetcolumnData> {

        /**
         * インスタンスを構築します。
         */
        public _TableresetcolumnDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TableresetcolumnDataNames(final String name) {
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
        public _TableresetcolumnDataNames(final PropertyName<?> parent, final String name) {
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
         * attridのプロパティ名を返します。
         *
         * @return attridのプロパティ名
         */
        public PropertyName<String> attrid() {
            return new PropertyName<String>(this, "attrid");
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
         * tablemasterInfoのプロパティ名を返します。
         *
         * @return tablemasterInfoのプロパティ名
         */
        public _TablemasterInfoNames tablemasterInfo() {
            return new _TablemasterInfoNames(this, "tablemasterInfo");
        }
    }
}
