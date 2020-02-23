/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrackmapInfoNames._TrackmapInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link HistorytableInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/10/08 0:45:21")
public class HistorytableInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "History table data";
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
     * trackmapinfoidのプロパティ名を返します。
     * 
     * @return trackmapinfoidのプロパティ名
     */
    public static PropertyName<Long> trackmapinfoid() {
        return new PropertyName<Long>("trackmapinfoid");
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
     * historytablenameのプロパティ名を返します。
     * 
     * @return historytablenameのプロパティ名
     */
    public static PropertyName<String> historytablename() {
        return new PropertyName<String>("historytablename");
    }

    /**
     * idColumnのプロパティ名を返します。
     * 
     * @return idColumnのプロパティ名
     */
    public static PropertyName<String> idColumn() {
        return new PropertyName<String>("idColumn");
    }

    /**
     * trackmapInfoのプロパティ名を返します。
     * 
     * @return trackmapInfoのプロパティ名
     */
    public static _TrackmapInfoNames trackmapInfo() {
        return new _TrackmapInfoNames("trackmapInfo");
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
    public static class _HistorytableInfoNames extends PropertyName<HistorytableInfo> {

        /**
         * インスタンスを構築します。
         */
        public _HistorytableInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _HistorytableInfoNames(final String name) {
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
        public _HistorytableInfoNames(final PropertyName<?> parent, final String name) {
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
         * trackmapinfoidのプロパティ名を返します。
         *
         * @return trackmapinfoidのプロパティ名
         */
        public PropertyName<Long> trackmapinfoid() {
            return new PropertyName<Long>(this, "trackmapinfoid");
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
         * historytablenameのプロパティ名を返します。
         *
         * @return historytablenameのプロパティ名
         */
        public PropertyName<String> historytablename() {
            return new PropertyName<String>(this, "historytablename");
        }

        /**
         * idColumnのプロパティ名を返します。
         *
         * @return idColumnのプロパティ名
         */
        public PropertyName<String> idColumn() {
            return new PropertyName<String>(this, "idColumn");
        }

        /**
         * trackmapInfoのプロパティ名を返します。
         * 
         * @return trackmapInfoのプロパティ名
         */
        public _TrackmapInfoNames trackmapInfo() {
            return new _TrackmapInfoNames(this, "trackmapInfo");
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