/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskInfoNames._MenutaskInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MenutaskmenuInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/04 16:53:49")
public class MenutaskmenuInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Task menu info";
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
     * menutaskinfoidのプロパティ名を返します。
     * 
     * @return menutaskinfoidのプロパティ名
     */
    public static PropertyName<Long> menutaskinfoid() {
        return new PropertyName<Long>("menutaskinfoid");
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
     * disporderのプロパティ名を返します。
     * 
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
    }

    /**
     * menutaskInfoのプロパティ名を返します。
     * 
     * @return menutaskInfoのプロパティ名
     */
    public static _MenutaskInfoNames menutaskInfo() {
        return new _MenutaskInfoNames("menutaskInfo");
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
    public static class _MenutaskmenuInfoNames extends PropertyName<MenutaskmenuInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MenutaskmenuInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MenutaskmenuInfoNames(final String name) {
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
        public _MenutaskmenuInfoNames(final PropertyName<?> parent, final String name) {
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
         * menutaskinfoidのプロパティ名を返します。
         *
         * @return menutaskinfoidのプロパティ名
         */
        public PropertyName<Long> menutaskinfoid() {
            return new PropertyName<Long>(this, "menutaskinfoid");
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
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
        }

        /**
         * menutaskInfoのプロパティ名を返します。
         * 
         * @return menutaskInfoのプロパティ名
         */
        public _MenutaskInfoNames menutaskInfo() {
            return new _MenutaskInfoNames(this, "menutaskInfo");
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
