/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MeteolayerInfo;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotypeMasterNames._MeteotypeMasterNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MeteolayerInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/09/09 18:18:57")
public class MeteolayerInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Weather layer info";
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
     * meteotypeidのプロパティ名を返します。
     * 
     * @return meteotypeidのプロパティ名
     */
    public static PropertyName<Long> meteotypeid() {
        return new PropertyName<Long>("meteotypeid");
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
     * searchableのプロパティ名を返します。
     * 
     * @return searchableのプロパティ名
     */
    public static PropertyName<Boolean> searchable() {
        return new PropertyName<Boolean>("searchable");
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
     * menuInfoのプロパティ名を返します。
     * 
     * @return menuInfoのプロパティ名
     */
    public static _MenuInfoNames menuInfo() {
        return new _MenuInfoNames("menuInfo");
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
    public static class _MeteolayerInfoNames extends PropertyName<MeteolayerInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MeteolayerInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MeteolayerInfoNames(final String name) {
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
        public _MeteolayerInfoNames(final PropertyName<?> parent, final String name) {
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
         * meteotypeidのプロパティ名を返します。
         *
         * @return meteotypeidのプロパティ名
         */
        public PropertyName<Long> meteotypeid() {
            return new PropertyName<Long>(this, "meteotypeid");
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
         * searchableのプロパティ名を返します。
         *
         * @return searchableのプロパティ名
         */
        public PropertyName<Boolean> searchable() {
            return new PropertyName<Boolean>(this, "searchable");
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
         * menuInfoのプロパティ名を返します。
         * 
         * @return menuInfoのプロパティ名
         */
        public _MenuInfoNames menuInfo() {
            return new _MenuInfoNames(this, "menuInfo");
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
