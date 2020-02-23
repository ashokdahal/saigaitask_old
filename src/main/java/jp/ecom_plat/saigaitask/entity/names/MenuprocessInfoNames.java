/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.names.MenuloginInfoNames._MenuloginInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskInfoNames._MenutaskInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MenuprocessInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/04 11:53:15")
public class MenuprocessInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Menu process info";
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
     * menulogininfoidのプロパティ名を返します。
     * 
     * @return menulogininfoidのプロパティ名
     */
    public static PropertyName<Long> menulogininfoid() {
        return new PropertyName<Long>("menulogininfoid");
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
     * visibleのプロパティ名を返します。
     * 
     * @return visibleのプロパティ名
     */
    public static PropertyName<Boolean> visible() {
        return new PropertyName<Boolean>("visible");
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
     * noteのプロパティ名を返します。
     * 
     * @return noteのプロパティ名
     */
    public static PropertyName<String> note() {
        return new PropertyName<String>("note");
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
     * deletedのプロパティ名を返します。
     * 
     * @return deletedのプロパティ名
     */
    public static PropertyName<Boolean> deleted() {
        return new PropertyName<Boolean>("deleted");
    }

    /**
     * menutaskInfosのプロパティ名を返します。
     * 
     * @return menutaskInfosのプロパティ名
     */
    public static _MenutaskInfoNames menutaskInfos() {
        return new _MenutaskInfoNames("menutaskInfos");
    }

    /**
     * menuloginInfoのプロパティ名を返します。
     * 
     * @return menuloginInfoのプロパティ名
     */
    public static _MenuloginInfoNames menuloginInfo() {
        return new _MenuloginInfoNames("menuloginInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MenuprocessInfoNames extends PropertyName<MenuprocessInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MenuprocessInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MenuprocessInfoNames(final String name) {
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
        public _MenuprocessInfoNames(final PropertyName<?> parent, final String name) {
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
         * menulogininfoidのプロパティ名を返します。
         *
         * @return menulogininfoidのプロパティ名
         */
        public PropertyName<Long> menulogininfoid() {
            return new PropertyName<Long>(this, "menulogininfoid");
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
         * visibleのプロパティ名を返します。
         *
         * @return visibleのプロパティ名
         */
        public PropertyName<Boolean> visible() {
            return new PropertyName<Boolean>(this, "visible");
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
         * noteのプロパティ名を返します。
         *
         * @return noteのプロパティ名
         */
        public PropertyName<String> note() {
            return new PropertyName<String>(this, "note");
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
         * deletedのプロパティ名を返します。
         *
         * @return deletedのプロパティ名
         */
        public PropertyName<Boolean> deleted() {
            return new PropertyName<Boolean>(this, "deleted");
        }

        /**
         * menutaskInfosのプロパティ名を返します。
         * 
         * @return menutaskInfosのプロパティ名
         */
        public _MenutaskInfoNames menutaskInfos() {
            return new _MenutaskInfoNames(this, "menutaskInfos");
        }

        /**
         * menuloginInfoのプロパティ名を返します。
         * 
         * @return menuloginInfoのプロパティ名
         */
        public _MenuloginInfoNames menuloginInfo() {
            return new _MenuloginInfoNames(this, "menuloginInfo");
        }
    }
}