/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.names.MaplayerattrInfoNames._MaplayerattrInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenutableInfoNames._MenutableInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MaplayerInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/23 16:58:46")
public class MaplayerInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Map layer info";
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
     * tablemasterinfoidのプロパティ名を返します。
     * 
     * @return tablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> tablemasterinfoid() {
        return new PropertyName<Long>("tablemasterinfoid");
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
     * editableのプロパティ名を返します。
     * 
     * @return editableのプロパティ名
     */
    public static PropertyName<Boolean> editable() {
        return new PropertyName<Boolean>("editable");
    }

    /**
     * addableのプロパティ名を返します。
     * 
     * @return addableのプロパティ名
     */
    public static PropertyName<Boolean> addable() {
        return new PropertyName<Boolean>("addable");
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
     * snapableのプロパティ名を返します。
     * 
     * @return snapableのプロパティ名
     */
    public static PropertyName<Boolean> snapable() {
        return new PropertyName<Boolean>("snapable");
    }

    /**
     * intersectionlayeridのプロパティ名を返します。
     * 
     * @return intersectionlayeridのプロパティ名
     */
    public static PropertyName<String> intersectionlayerid() {
        return new PropertyName<String>("intersectionlayerid");
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
     * deletedのプロパティ名を返します。
     * 
     * @return deletedのプロパティ名
     */
    public static PropertyName<Boolean> deleted() {
        return new PropertyName<Boolean>("deleted");
    }

    /**
     * maplayerattrInfosのプロパティ名を返します。
     * 
     * @return maplayerattrInfosのプロパティ名
     */
    public static _MaplayerattrInfoNames maplayerattrInfos() {
        return new _MaplayerattrInfoNames("maplayerattrInfos");
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
     * menuInfoのプロパティ名を返します。
     * 
     * @return menuInfoのプロパティ名
     */
    public static _MenuInfoNames menuInfo() {
        return new _MenuInfoNames("menuInfo");
    }

    /**
     * menutableInfoのプロパティ名を返します。
     * 
     * @return menutableInfoのプロパティ名
     */
    public static _MenutableInfoNames menutableInfo() {
        return new _MenutableInfoNames("menutableInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MaplayerInfoNames extends PropertyName<MaplayerInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MaplayerInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MaplayerInfoNames(final String name) {
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
        public _MaplayerInfoNames(final PropertyName<?> parent, final String name) {
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
         * tablemasterinfoidのプロパティ名を返します。
         *
         * @return tablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> tablemasterinfoid() {
            return new PropertyName<Long>(this, "tablemasterinfoid");
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
         * editableのプロパティ名を返します。
         *
         * @return editableのプロパティ名
         */
        public PropertyName<Boolean> editable() {
            return new PropertyName<Boolean>(this, "editable");
        }

        /**
         * addableのプロパティ名を返します。
         *
         * @return addableのプロパティ名
         */
        public PropertyName<Boolean> addable() {
            return new PropertyName<Boolean>(this, "addable");
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
         * snapableのプロパティ名を返します。
         *
         * @return snapableのプロパティ名
         */
        public PropertyName<Boolean> snapable() {
            return new PropertyName<Boolean>(this, "snapable");
        }

        /**
         * intersectionlayeridのプロパティ名を返します。
         *
         * @return intersectionlayeridのプロパティ名
         */
        public PropertyName<String> intersectionlayerid() {
            return new PropertyName<String>(this, "intersectionlayerid");
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
         * deletedのプロパティ名を返します。
         *
         * @return deletedのプロパティ名
         */
        public PropertyName<Boolean> deleted() {
            return new PropertyName<Boolean>(this, "deleted");
        }

        /**
         * maplayerattrInfosのプロパティ名を返します。
         * 
         * @return maplayerattrInfosのプロパティ名
         */
        public _MaplayerattrInfoNames maplayerattrInfos() {
            return new _MaplayerattrInfoNames(this, "maplayerattrInfos");
        }

        /**
         * tablemasterInfoのプロパティ名を返します。
         * 
         * @return tablemasterInfoのプロパティ名
         */
        public _TablemasterInfoNames tablemasterInfo() {
            return new _TablemasterInfoNames(this, "tablemasterInfo");
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
         * menutableInfoのプロパティ名を返します。
         * 
         * @return menutableInfoのプロパティ名
         */
        public _MenutableInfoNames menutableInfo() {
            return new _MenutableInfoNames(this, "menutableInfo");
        }
}
}
