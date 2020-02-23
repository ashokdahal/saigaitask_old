/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.StationlayerInfo;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link StationlayerInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/09/09 18:18:57")
public class StationlayerInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "System layer info";
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
     * stationclassattridのプロパティ名を返します。
     * 
     * @return stationclassattridのプロパティ名
     */
    public static PropertyName<String> stationclassattrid() {
        return new PropertyName<String>("stationclassattrid");
    }

    /**
     * shifttimeattridのプロパティ名を返します。
     * 
     * @return shifttimeattridのプロパティ名
     */
    public static PropertyName<String> shifttimeattrid() {
        return new PropertyName<String>("shifttimeattrid");
    }

    /**
     * registtimeattridのプロパティ名を返します。
     * 
     * @return registtimeattridのプロパティ名
     */
    public static PropertyName<String> registtimeattrid() {
        return new PropertyName<String>("registtimeattrid");
    }

    /**
     * closetimeattridのプロパティ名を返します。
     * 
     * @return closetimeattridのプロパティ名
     */
    public static PropertyName<String> closetimeattrid() {
        return new PropertyName<String>("closetimeattrid");
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
    public static class _StationlayerInfoNames extends PropertyName<StationlayerInfo> {

        /**
         * インスタンスを構築します。
         */
        public _StationlayerInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _StationlayerInfoNames(final String name) {
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
        public _StationlayerInfoNames(final PropertyName<?> parent, final String name) {
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
         * stationclassattridのプロパティ名を返します。
         *
         * @return stationclassattridのプロパティ名
         */
        public PropertyName<String> stationclassattrid() {
            return new PropertyName<String>(this, "stationclassattrid");
        }

        /**
         * shifttimeattridのプロパティ名を返します。
         *
         * @return shifttimeattridのプロパティ名
         */
        public PropertyName<String> shifttimeattrid() {
            return new PropertyName<String>(this, "shifttimeattrid");
        }

        /**
         * registtimeattridのプロパティ名を返します。
         *
         * @return registtimeattridのプロパティ名
         */
        public PropertyName<String> registtimeattrid() {
            return new PropertyName<String>(this, "registtimeattrid");
        }

        /**
         * closetimeattridのプロパティ名を返します。
         *
         * @return closetimeattridのプロパティ名
         */
        public PropertyName<String> closetimeattrid() {
            return new PropertyName<String>(this, "closetimeattrid");
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
