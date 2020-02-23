/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PagemenubuttonInfo;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PagebuttonMasterNames._PagebuttonMasterNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PagemenubuttonInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/16 16:18:38")
public class PagemenubuttonInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Page button info";
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
     * pagebuttonidのプロパティ名を返します。
     * 
     * @return pagebuttonidのプロパティ名
     */
    public static PropertyName<Integer> pagebuttonid() {
        return new PropertyName<Integer>("pagebuttonid");
    }

    /**
     * hrefのプロパティ名を返します。
     * 
     * @return hrefのプロパティ名
     */
    public static PropertyName<String> href() {
        return new PropertyName<String>("href");
    }

    /**
     * targetのプロパティ名を返します。
     * 
     * @return targetのプロパティ名
     */
    public static PropertyName<String> target() {
        return new PropertyName<String>("target");
    }

    /**
     * enableのプロパティ名を返します。
     * 
     * @return enableのプロパティ名
     */
    public static PropertyName<Boolean> enable() {
        return new PropertyName<Boolean>("enable");
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
     * pagebuttonMasterのプロパティ名を返します。
     * 
     * @return pagebuttonMasterのプロパティ名
     */
    public static _PagebuttonMasterNames pagebuttonMaster() {
        return new _PagebuttonMasterNames("pagebuttonMaster");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _PagemenubuttonInfoNames extends PropertyName<PagemenubuttonInfo> {

        /**
         * インスタンスを構築します。
         */
        public _PagemenubuttonInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _PagemenubuttonInfoNames(final String name) {
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
        public _PagemenubuttonInfoNames(final PropertyName<?> parent, final String name) {
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
         * pagebuttonidのプロパティ名を返します。
         *
         * @return pagebuttonidのプロパティ名
         */
        public PropertyName<Integer> pagebuttonid() {
            return new PropertyName<Integer>(this, "pagebuttonid");
        }

        /**
         * hrefのプロパティ名を返します。
         *
         * @return hrefのプロパティ名
         */
        public PropertyName<String> href() {
            return new PropertyName<String>(this, "href");
        }

        /**
         * targetのプロパティ名を返します。
         *
         * @return targetのプロパティ名
         */
        public PropertyName<String> target() {
            return new PropertyName<String>(this, "target");
        }

        /**
         * enableのプロパティ名を返します。
         *
         * @return enableのプロパティ名
         */
        public PropertyName<Boolean> enable() {
            return new PropertyName<Boolean>(this, "enable");
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
         * pagebuttonMasterのプロパティ名を返します。
         * 
         * @return pagebuttonMasterのプロパティ名
         */
        public _PagebuttonMasterNames pagebuttonMaster() {
            return new _PagebuttonMasterNames(this, "pagebuttonMaster");
        }
    }
}
