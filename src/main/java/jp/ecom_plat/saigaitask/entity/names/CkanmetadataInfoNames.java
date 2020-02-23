/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.CkanmetadataInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link CkanmetadataInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/08/19 12:45:15")
public class CkanmetadataInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "CKAN prior data info";
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
     * localgovinfoidのプロパティ名を返します。
     * 
     * @return localgovinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovinfoid() {
        return new PropertyName<Long>("localgovinfoid");
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
     * nameのプロパティ名を返します。
     * 
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
    }

    /**
     * infotypeのプロパティ名を返します。
     * 
     * @return infotypeのプロパティ名
     */
    public static PropertyName<String> infotype() {
        return new PropertyName<String>("infotype");
    }

    /**
     * ownerorgのプロパティ名を返します。
     * 
     * @return ownerorgのプロパティ名
     */
    public static PropertyName<String> ownerorg() {
        return new PropertyName<String>("ownerorg");
    }

    /**
     * ownerorgtitleのプロパティ名を返します。
     * 
     * @return ownerorgtitleのプロパティ名
     */
    public static PropertyName<String> ownerorgtitle() {
        return new PropertyName<String>("ownerorgtitle");
    }

    /**
     * titleのプロパティ名を返します。
     * 
     * @return titleのプロパティ名
     */
    public static PropertyName<String> title() {
        return new PropertyName<String>("title");
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
     * tagsのプロパティ名を返します。
     * 
     * @return tagsのプロパティ名
     */
    public static PropertyName<String> tags() {
        return new PropertyName<String>("tags");
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
     * localgovInfoのプロパティ名を返します。
     * 
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
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
    public static class _CkanmetadataInfoNames extends PropertyName<CkanmetadataInfo> {

        /**
         * インスタンスを構築します。
         */
        public _CkanmetadataInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _CkanmetadataInfoNames(final String name) {
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
        public _CkanmetadataInfoNames(final PropertyName<?> parent, final String name) {
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
         * localgovinfoidのプロパティ名を返します。
         *
         * @return localgovinfoidのプロパティ名
         */
        public PropertyName<Long> localgovinfoid() {
            return new PropertyName<Long>(this, "localgovinfoid");
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
         * nameのプロパティ名を返します。
         *
         * @return nameのプロパティ名
         */
        public PropertyName<String> name() {
            return new PropertyName<String>(this, "name");
        }

        /**
         * infotypeのプロパティ名を返します。
         *
         * @return infotypeのプロパティ名
         */
        public PropertyName<String> infotype() {
            return new PropertyName<String>(this, "infotype");
        }

        /**
         * ownerorgのプロパティ名を返します。
         *
         * @return ownerorgのプロパティ名
         */
        public PropertyName<String> ownerorg() {
            return new PropertyName<String>(this, "ownerorg");
        }

        /**
         * ownerorgtitleのプロパティ名を返します。
         *
         * @return ownerorgtitleのプロパティ名
         */
        public PropertyName<String> ownerorgtitle() {
            return new PropertyName<String>(this, "ownerorgtitle");
        }

        /**
         * titleのプロパティ名を返します。
         *
         * @return titleのプロパティ名
         */
        public PropertyName<String> title() {
            return new PropertyName<String>(this, "title");
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
         * tagsのプロパティ名を返します。
         *
         * @return tagsのプロパティ名
         */
        public PropertyName<String> tags() {
            return new PropertyName<String>(this, "tags");
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
         * localgovInfoのプロパティ名を返します。
         * 
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
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
