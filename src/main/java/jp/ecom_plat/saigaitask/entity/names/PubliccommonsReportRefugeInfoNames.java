/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportRefugeInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsReportRefugeInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/09/25 12:52:53")
public class PubliccommonsReportRefugeInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert shelter advisory info";
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
     * attrareaのプロパティ名を返します。
     *
     * @return attrareaのプロパティ名
     */
    public static PropertyName<String> attrarea() {
        return new PropertyName<String>("attrarea");
    }

    /**
     * attrorderstatusのプロパティ名を返します。
     *
     * @return attrorderstatusのプロパティ名
     */
    public static PropertyName<String> attrorderstatus() {
        return new PropertyName<String>("attrorderstatus");
    }

    /**
     * attrhouseholdsのプロパティ名を返します。
     *
     * @return attrhouseholdsのプロパティ名
     */
    public static PropertyName<String> attrhouseholds() {
        return new PropertyName<String>("attrhouseholds");
    }

    /**
     * attrpeopleのプロパティ名を返します。
     *
     * @return attrpeopleのプロパティ名
     */
    public static PropertyName<String> attrpeople() {
        return new PropertyName<String>("attrpeople");
    }

    /**
     * attrordertimeのプロパティ名を返します。
     *
     * @return attrordertimeのプロパティ名
     */
    public static PropertyName<String> attrordertime() {
        return new PropertyName<String>("attrordertime");
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
    public static class _PubliccommonsReportRefugeInfoNames extends PropertyName<PubliccommonsReportRefugeInfo> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsReportRefugeInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _PubliccommonsReportRefugeInfoNames(final String name) {
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
        public _PubliccommonsReportRefugeInfoNames(final PropertyName<?> parent, final String name) {
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
         * attrareaのプロパティ名を返します。
         *
         * @return attrareaのプロパティ名
         */
        public PropertyName<String> attrarea() {
            return new PropertyName<String>(this, "attrarea");
        }

        /**
         * attrorderstatusのプロパティ名を返します。
         *
         * @return attrorderstatusのプロパティ名
         */
        public PropertyName<String> attrorderstatus() {
            return new PropertyName<String>(this, "attrorderstatus");
        }

        /**
         * attrhouseholdsのプロパティ名を返します。
         *
         * @return attrhouseholdsのプロパティ名
         */
        public PropertyName<String> attrhouseholds() {
            return new PropertyName<String>(this, "attrhouseholds");
        }

        /**
         * attrpeopleのプロパティ名を返します。
         *
         * @return attrpeopleのプロパティ名
         */
        public PropertyName<String> attrpeople() {
            return new PropertyName<String>(this, "attrpeople");
        }

        /**
         * attrordertimeのプロパティ名を返します。
         *
         * @return attrordertimeのプロパティ名
         */
        public PropertyName<String> attrordertime() {
            return new PropertyName<String>(this, "attrordertime");
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
