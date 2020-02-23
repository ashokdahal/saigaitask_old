/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MapreferencelayerInfo;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MapreferencelayerInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/17 19:42:38")
public class MapreferencelayerInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Map reference info";
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
     * menuinfoidのプロパティ名を返します。
     * 
     * @return menuinfoidのプロパティ名
     */
    public static PropertyName<Long> menuinfoid() {
        return new PropertyName<Long>("menuinfoid");
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
     * visibleのプロパティ名を返します。
     * 
     * @return visibleのプロパティ名
     */
    public static PropertyName<Boolean> visible() {
        return new PropertyName<Boolean>("visible");
    }

    /**
     * closedのプロパティ名を返します。
     * 
     * @return closedのプロパティ名
     */
    public static PropertyName<Boolean> closed() {
        return new PropertyName<Boolean>("closed");
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
     * disporderのプロパティ名を返します。
     * 
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
    }

    /**
     * layeropacityのプロパティ名を返します。
     *
     * @return layeropacityのプロパティ名
     */
    public static PropertyName<Double> layeropacity() {
        return new PropertyName<Double>("layeropacity");
    }

    /**
     * menuInfoのプロパティ名を返します。
     * 
     * @return menuInfoのプロパティ名
     */
    public static _MenuInfoNames menuInfo() {
        return new _MenuInfoNames("menuInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MapreferencelayerInfoNames extends PropertyName<MapreferencelayerInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MapreferencelayerInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MapreferencelayerInfoNames(final String name) {
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
        public _MapreferencelayerInfoNames(final PropertyName<?> parent, final String name) {
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
         * menuinfoidのプロパティ名を返します。
         *
         * @return menuinfoidのプロパティ名
         */
        public PropertyName<Long> menuinfoid() {
            return new PropertyName<Long>(this, "menuinfoid");
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
         * visibleのプロパティ名を返します。
         *
         * @return visibleのプロパティ名
         */
        public PropertyName<Boolean> visible() {
            return new PropertyName<Boolean>(this, "visible");
        }

        /**
         * closedのプロパティ名を返します。
         *
         * @return closedのプロパティ名
         */
        public PropertyName<Boolean> closed() {
            return new PropertyName<Boolean>(this, "closed");
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
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
        }

        /**
         * layeropacityのプロパティ名を返します。
         *
         * @return layeropacityのプロパティ名
         */
        public PropertyName<Double> layeropacity() {
            return new PropertyName<Double>(this, "layeropacity");
        }

        /**
         * menuInfoのプロパティ名を返します。
         *
         * @return menuInfoのプロパティ名
         */
        public _MenuInfoNames menuInfo() {
            return new _MenuInfoNames(this, "menuInfo");
        }
    }
}
