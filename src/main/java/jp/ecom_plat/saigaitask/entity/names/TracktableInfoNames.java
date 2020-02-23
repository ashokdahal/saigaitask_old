/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrackmapInfoNames._TrackmapInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TracktableInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/23 16:58:46")
public class TracktableInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Record table data";
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
     * layeridのプロパティ名を返します。
     * 
     * @return layeridのプロパティ名
     */
    public static PropertyName<String> layerid() {
        return new PropertyName<String>("layerid");
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
    public static class _TracktableInfoNames extends PropertyName<TracktableInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TracktableInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TracktableInfoNames(final String name) {
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
        public _TracktableInfoNames(final PropertyName<?> parent, final String name) {
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
         * layeridのプロパティ名を返します。
         *
         * @return layeridのプロパティ名
         */
        public PropertyName<String> layerid() {
            return new PropertyName<String>(this, "layerid");
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
