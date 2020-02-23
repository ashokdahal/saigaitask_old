/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportShelterInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsReportShelterInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/09/25 12:52:53")
public class PubliccommonsReportShelterInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert shelter info";
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
     * attrshelterのプロパティ名を返します。
     *
     * @return attrshelterのプロパティ名
     */
    public static PropertyName<String> attrshelter() {
        return new PropertyName<String>("attrshelter");
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
     * attraddressのプロパティ名を返します。
     *
     * @return attraddressのプロパティ名
     */
    public static PropertyName<String> attraddress() {
        return new PropertyName<String>("attraddress");
    }

    /**
     * attrphoneのプロパティ名を返します。
     *
     * @return attrphoneのプロパティ名
     */
    public static PropertyName<String> attrphone() {
        return new PropertyName<String>("attrphone");
    }

    /**
     * attrfaxのプロパティ名を返します。
     *
     * @return attrfaxのプロパティ名
     */
    public static PropertyName<String> attrfax() {
        return new PropertyName<String>("attrfax");
    }

    /**
     * attrstaffのプロパティ名を返します。
     *
     * @return attrstaffのプロパティ名
     */
    public static PropertyName<String> attrstaff() {
        return new PropertyName<String>("attrstaff");
    }

    /**
     * attrstatusのプロパティ名を返します。
     *
     * @return attrstatusのプロパティ名
     */
    public static PropertyName<String> attrstatus() {
        return new PropertyName<String>("attrstatus");
    }

    /**
     * attrcapacityのプロパティ名を返します。
     *
     * @return attrcapacityのプロパティ名
     */
    public static PropertyName<String> attrcapacity() {
        return new PropertyName<String>("attrcapacity");
    }

    /**
     * attrsetuptimeのプロパティ名を返します。
     *
     * @return attrsetuptimeのプロパティ名
     */
    public static PropertyName<String> attrsetuptime() {
        return new PropertyName<String>("attrsetuptime");
    }

    /**
     * attrclosetimeのプロパティ名を返します。
     *
     * @return attrclosetimeのプロパティ名
     */
    public static PropertyName<String> attrclosetime() {
        return new PropertyName<String>("attrclosetime");
    }

    /**
     * attrtypeのプロパティ名を返します。
     *
     * @return attrtypeのプロパティ名
     */
    public static PropertyName<String> attrtype() {
        return new PropertyName<String>("attrtype");
    }

    /**
     * attrcircleのプロパティ名を返します。
     *
     * @return attrcircleのプロパティ名
     */
    public static PropertyName<String> attrcircle() {
        return new PropertyName<String>("attrcircle");
    }

    /**
     * attrheadcountのプロパティ名を返します。
     *
     * @return attrheadcountのプロパティ名
     */
    public static PropertyName<String> attrheadcount() {
        return new PropertyName<String>("attrheadcount");
    }

    /**
     * attrheadcountvoluntaryのプロパティ名を返します。
     *
     * @return attrheadcountvoluntaryのプロパティ名
     */
    public static PropertyName<String> attrheadcountvoluntary() {
        return new PropertyName<String>("attrheadcountvoluntary");
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
     * attrhouseholdsvoluntaryのプロパティ名を返します。
     *
     * @return attrhouseholdsvoluntaryのプロパティ名
     */
    public static PropertyName<String> attrhouseholdsvoluntary() {
        return new PropertyName<String>("attrhouseholdsvoluntary");
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
    public static class _PubliccommonsReportShelterInfoNames extends PropertyName<PubliccommonsReportShelterInfo> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsReportShelterInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _PubliccommonsReportShelterInfoNames(final String name) {
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
        public _PubliccommonsReportShelterInfoNames(final PropertyName<?> parent, final String name) {
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
         * attrshelterのプロパティ名を返します。
         *
         * @return attrshelterのプロパティ名
         */
        public PropertyName<String> attrshelter() {
            return new PropertyName<String>(this, "attrshelter");
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
         * attraddressのプロパティ名を返します。
         *
         * @return attraddressのプロパティ名
         */
        public PropertyName<String> attraddress() {
            return new PropertyName<String>(this, "attraddress");
        }

        /**
         * attrphoneのプロパティ名を返します。
         *
         * @return attrphoneのプロパティ名
         */
        public PropertyName<String> attrphone() {
            return new PropertyName<String>(this, "attrphone");
        }

        /**
         * attrfaxのプロパティ名を返します。
         *
         * @return attrfaxのプロパティ名
         */
        public PropertyName<String> attrfax() {
            return new PropertyName<String>(this, "attrfax");
        }

        /**
         * attrstaffのプロパティ名を返します。
         *
         * @return attrstaffのプロパティ名
         */
        public PropertyName<String> attrstaff() {
            return new PropertyName<String>(this, "attrstaff");
        }

        /**
         * attrstatusのプロパティ名を返します。
         *
         * @return attrstatusのプロパティ名
         */
        public PropertyName<String> attrstatus() {
            return new PropertyName<String>(this, "attrstatus");
        }

        /**
         * attrcapacityのプロパティ名を返します。
         *
         * @return attrcapacityのプロパティ名
         */
        public PropertyName<String> attrcapacity() {
            return new PropertyName<String>(this, "attrcapacity");
        }

        /**
         * attrsetuptimeのプロパティ名を返します。
         *
         * @return attrsetuptimeのプロパティ名
         */
        public PropertyName<String> attrsetuptime() {
            return new PropertyName<String>(this, "attrsetuptime");
        }

        /**
         * attrclosetimeのプロパティ名を返します。
         *
         * @return attrclosetimeのプロパティ名
         */
        public PropertyName<String> attrclosetime() {
            return new PropertyName<String>(this, "attrclosetime");
        }

        /**
         * attrtypeのプロパティ名を返します。
         *
         * @return attrtypeのプロパティ名
         */
        public PropertyName<String> attrtype() {
            return new PropertyName<String>(this, "attrtype");
        }

        /**
         * attrcircleのプロパティ名を返します。
         *
         * @return attrcircleのプロパティ名
         */
        public PropertyName<String> attrcircle() {
            return new PropertyName<String>(this, "attrcircle");
        }

        /**
         * attrheadcountのプロパティ名を返します。
         *
         * @return attrheadcountのプロパティ名
         */
        public PropertyName<String> attrheadcount() {
            return new PropertyName<String>(this, "attrheadcount");
        }

        /**
         * attrheadcountvoluntaryのプロパティ名を返します。
         *
         * @return attrheadcountvoluntaryのプロパティ名
         */
        public PropertyName<String> attrheadcountvoluntary() {
            return new PropertyName<String>(this, "attrheadcountvoluntary");
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
         * attrhouseholdsvoluntaryのプロパティ名を返します。
         *
         * @return attrhouseholdsvoluntaryのプロパティ名
         */
        public PropertyName<String> attrhouseholdsvoluntary() {
            return new PropertyName<String>(this, "attrhouseholdsvoluntary");
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
