/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.names.MaplayerInfoNames._MaplayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablelistcolumnInfoNames._TablelistcolumnInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MenutableInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/16 15:02:27")
public class MenutableInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Menu table info";
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
     * addableのプロパティ名を返します。
     *
     * @return addableのプロパティ名
     */
    public static PropertyName<Boolean> addable() {
        return new PropertyName<Boolean>("addable");
    }

    /**
     * deletableのプロパティ名を返します。
     *
     * @return deletableのプロパティ名
     */
    public static PropertyName<Boolean> deletable() {
        return new PropertyName<Boolean>("deletable");
    }

    /**
     * totalableのプロパティ名を返します。
     *
     * @return totalableのプロパティ名
     */
    public static PropertyName<Boolean> totalable() {
        return new PropertyName<Boolean>("totalable");
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
     * disporderのプロパティ名を返します。
     *
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
    }

    /**
     * accordionattridのプロパティ名を返します。
     * 
     * @return accordionattridのプロパティ名
     */
    public static PropertyName<String> accordionattrid() {
        return new PropertyName<String>("accordionattrid");
    }

    /**
     * accordionnameのプロパティ名を返します。
     * 
     * @return accordionnameのプロパティ名
     */
    public static PropertyName<String> accordionname() {
        return new PropertyName<String>("accordionname");
    }

    /**
     * accordionopenのプロパティ名を返します。
     * 
     * @return accordionopenのプロパティ名
     */
    public static PropertyName<Boolean> accordionopen() {
        return new PropertyName<Boolean>("accordionopen");
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
     * tablelistcolumnInfoListのプロパティ名を返します。
     *
     * @return tablelistcolumnInfoListのプロパティ名
     */
    public static _TablelistcolumnInfoNames tablelistcolumnInfoList() {
        return new _TablelistcolumnInfoNames("tablelistcolumnInfoList");
    }

    /**
     * maplayerInfoListのプロパティ名を返します。
     *
     * @return maplayerInfoListのプロパティ名
     */
    public static _MaplayerInfoNames maplayerInfoList() {
        return new _MaplayerInfoNames("maplayerInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MenutableInfoNames extends PropertyName<MenutableInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MenutableInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _MenutableInfoNames(final String name) {
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
        public _MenutableInfoNames(final PropertyName<?> parent, final String name) {
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
         * addableのプロパティ名を返します。
         *
         * @return addableのプロパティ名
         */
        public PropertyName<Boolean> addable() {
            return new PropertyName<Boolean>(this, "addable");
        }

        /**
         * deletableのプロパティ名を返します。
         *
         * @return deletableのプロパティ名
         */
        public PropertyName<Boolean> deletable() {
            return new PropertyName<Boolean>(this, "deletable");
        }

        /**
         * totalableのプロパティ名を返します。
         *
         * @return totalableのプロパティ名
         */
        public PropertyName<Boolean> totalable() {
            return new PropertyName<Boolean>(this, "totalable");
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
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
        }

        /**
         * accordionattridのプロパティ名を返します。
         *
         * @return accordionattridのプロパティ名
         */
        public PropertyName<String> accordionattrid() {
            return new PropertyName<String>(this, "accordionattrid");
        }

        /**
         * accordionnameのプロパティ名を返します。
         *
         * @return accordionnameのプロパティ名
         */
        public PropertyName<String> accordionname() {
            return new PropertyName<String>(this, "accordionname");
        }

        /**
         * accordionopenのプロパティ名を返します。
         *
         * @return accordionopenのプロパティ名
         */
        public PropertyName<Boolean> accordionopen() {
            return new PropertyName<Boolean>(this, "accordionopen");
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
         * tablelistcolumnInfoListのプロパティ名を返します。
         *
         * @return tablelistcolumnInfoListのプロパティ名
         */
        public _TablelistcolumnInfoNames tablelistcolumnInfoList() {
            return new _TablelistcolumnInfoNames(this, "tablelistcolumnInfoList");
        }

        /**
         * maplayerInfoListのプロパティ名を返します。
         *
         * @return maplayerInfoListのプロパティ名
         */
        public _MaplayerInfoNames maplayerInfoList() {
            return new _MaplayerInfoNames(this, "maplayerInfoList");
        }
    }
}
