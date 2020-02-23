/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.names.MenuprocessInfoNames._MenuprocessInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutaskmenuInfoNames._MenutaskmenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutasktypeInfoNames._MenutasktypeInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MenutaskInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/04 11:53:15")
public class MenutaskInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Menu task info";
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
     * menuprocessinfoidのプロパティ名を返します。
     *
     * @return menuprocessinfoidのプロパティ名
     */
    public static PropertyName<Long> menuprocessinfoid() {
        return new PropertyName<Long>("menuprocessinfoid");
    }

    /**
     * menutasktypeinfoidのプロパティ名を返します。
     *
     * @return menutasktypeinfoidのプロパティ名
     */
    public static PropertyName<Long> menutasktypeinfoid() {
        return new PropertyName<Long>("menutasktypeinfoid");
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
     * menuprocessInfoのプロパティ名を返します。
     *
     * @return menuprocessInfoのプロパティ名
     */
    public static _MenuprocessInfoNames menuprocessInfo() {
        return new _MenuprocessInfoNames("menuprocessInfo");
    }

    /**
     * menutasktypeInfoのプロパティ名を返します。
     *
     * @return menutasktypeInfoのプロパティ名
     */
    public static _MenutasktypeInfoNames menutasktypeInfo() {
        return new _MenutasktypeInfoNames("menutasktypeInfo");
    }

    /**
     * menutaskmenuInfosのプロパティ名を返します。
     *
     * @return menutaskmenuInfosのプロパティ名
     */
    public static _MenutaskmenuInfoNames menutaskmenuInfos() {
        return new _MenutaskmenuInfoNames("menutaskmenuInfos");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MenutaskInfoNames extends PropertyName<MenutaskInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MenutaskInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _MenutaskInfoNames(final String name) {
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
        public _MenutaskInfoNames(final PropertyName<?> parent, final String name) {
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
         * menuprocessinfoidのプロパティ名を返します。
         *
         * @return menuprocessinfoidのプロパティ名
         */
        public PropertyName<Long> menuprocessinfoid() {
            return new PropertyName<Long>(this, "menuprocessinfoid");
        }

        /**
         * menutasktypeinfoidのプロパティ名を返します。
         *
         * @return menutasktypeinfoidのプロパティ名
         */
        public PropertyName<Long> menutasktypeinfoid() {
            return new PropertyName<Long>(this, "menutasktypeinfoid");
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
         * menuprocessInfoのプロパティ名を返します。
         *
         * @return menuprocessInfoのプロパティ名
         */
        public _MenuprocessInfoNames menuprocessInfo() {
            return new _MenuprocessInfoNames(this, "menuprocessInfo");
        }

        /**
         * menutasktypeInfoのプロパティ名を返します。
         *
         * @return menutasktypeInfoのプロパティ名
         */
        public _MenutasktypeInfoNames mentasktypeInfo() {
            return new _MenutasktypeInfoNames(this, "menutasktypeInfo");
        }

        /**
         * menutaskmenuInfosのプロパティ名を返します。
         *
         * @return menutaskmenuInfosのプロパティ名
         */
        public _MenutaskmenuInfoNames menutaskmenuInfos() {
            return new _MenutaskmenuInfoNames(this, "menutaskmenuInfos");
        }
    }
}