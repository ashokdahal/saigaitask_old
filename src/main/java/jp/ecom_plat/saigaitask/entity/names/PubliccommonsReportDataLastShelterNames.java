/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastShelter;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames._PubliccommonsReportDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsReportDataLastShelter}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/09 19:47:29")
public class PubliccommonsReportDataLastShelterNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert shelter last sending history data";
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
     * pcommonsreportdataidのプロパティ名を返します。
     *
     * @return pcommonsreportdataidのプロパティ名
     */
    public static PropertyName<Long> pcommonsreportdataid() {
        return new PropertyName<Long>("pcommonsreportdataid");
    }

    /**
     * chikunameのプロパティ名を返します。
     *
     * @return chikunameのプロパティ名
     */
    public static PropertyName<String> chikuname() {
        return new PropertyName<String>("chikuname");
    }

    /**
     * closetimeのプロパティ名を返します。
     *
     * @return closetimeのプロパティ名
     */
    public static PropertyName<Timestamp> closetime() {
        return new PropertyName<Timestamp>("closetime");
    }

    /**
     * setuptimeのプロパティ名を返します。
     *
     * @return setuptimeのプロパティ名
     */
    public static PropertyName<Timestamp> setuptime() {
        return new PropertyName<Timestamp>("setuptime");
    }

    /**
     * shelteraddresseのプロパティ名を返します。
     *
     * @return shelteraddresseのプロパティ名
     */
    public static PropertyName<String> shelteraddress() {
        return new PropertyName<String>("shelteraddress");
    }

    /**
     * sheltercapacityのプロパティ名を返します。
     *
     * @return sheltercapacityのプロパティ名
     */
    public static PropertyName<String> sheltercapacity() {
        return new PropertyName<String>("sheltercapacity");
    }

    /**
     * shelterfaxのプロパティ名を返します。
     *
     * @return shelterfaxのプロパティ名
     */
    public static PropertyName<String> shelterfax() {
        return new PropertyName<String>("shelterfax");
    }

     /**
     * shelternameのプロパティ名を返します。
     *
     * @return shelternameのプロパティ名
     */
    public static PropertyName<String> sheltername() {
        return new PropertyName<String>("sheltername");
    }

    /**
     * shelterphoneのプロパティ名を返します。
     *
     * @return shelterphoneのプロパティ名
     */
    public static PropertyName<String> shelterphone() {
        return new PropertyName<String>("shelterphone");
    }

    /**
     * shelterstaffのプロパティ名を返します。
     *
     * @return shelterstaffのプロパティ名
     */
    public static PropertyName<String> shelterstaff() {
        return new PropertyName<String>("shelterstaff");
    }

    /**
     * shelterstatusのプロパティ名を返します。
     *
     * @return shelterstatusのプロパティ名
     */
    public static PropertyName<String> shelterstatus() {
        return new PropertyName<String>("shelterstatus");
    }

    /**
     * circleのプロパティ名を返します。
     *
     * @return circleのプロパティ名
     */
    public static PropertyName<String> circle() {
        return new PropertyName<String>("circle");
    }

    /**
     * typeのプロパティ名を返します。
     *
     * @return typeのプロパティ名
     */
    public static PropertyName<String> type() {
        return new PropertyName<String>("type");
    }

    /**
     * typedetailのプロパティ名を返します。
     *
     * @return typedetailのプロパティ名
     */
    public static PropertyName<String> typedetail() {
        return new PropertyName<String>("typedetail");
    }

    /**
     * headcountのプロパティ名を返します。
     *
     * @return headcountのプロパティ名
     */
    public static PropertyName<String> headcount() {
        return new PropertyName<String>("headcount");
    }

    /**
     * headcountvoluntaryのプロパティ名を返します。
     *
     * @return headcountvoluntaryのプロパティ名
     */
    public static PropertyName<String> headcountvoluntary() {
        return new PropertyName<String>("headcountvoluntary");
    }

    /**
     * householdsのプロパティ名を返します。
     *
     * @return householdsのプロパティ名
     */
    public static PropertyName<String> households() {
        return new PropertyName<String>("households");
    }

    /**
     * householdsvoluntaryのプロパティ名を返します。
     *
     * @return householdsvoluntaryのプロパティ名
     */
    public static PropertyName<String> householdsvoluntary() {
        return new PropertyName<String>("householdsvoluntary");
    }

    /**
     * publiccommonsReportDataのプロパティ名を返します。
     *
     * @return publiccommonsReportDataのプロパティ名
     */
    public static _PubliccommonsReportDataNames publiccommonsReportData() {
        return new _PubliccommonsReportDataNames("publiccommonsReportData");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _PubliccommonsReportDataLastShelterNames extends PropertyName<PubliccommonsReportDataLastShelter> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsReportDataLastShelterNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _PubliccommonsReportDataLastShelterNames(final String name) {
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
        public _PubliccommonsReportDataLastShelterNames(final PropertyName<?> parent, final String name) {
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
         * pcommonsreportdataidのプロパティ名を返します。
         *
         * @return pcommonsreportdataidのプロパティ名
         */
        public PropertyName<Long> pcommonsreportdataid() {
            return new PropertyName<Long>(this, "pcommonsreportdataid");
        }

        /**
         * chikunameのプロパティ名を返します。
         *
         * @return chikunameのプロパティ名
         */
        public PropertyName<String> chikuname() {
            return new PropertyName<String>(this, "chikuname");
        }

        /**
         * closetimeのプロパティ名を返します。
         *
         * @return closetimeのプロパティ名
         */
        public PropertyName<Timestamp> closetime() {
            return new PropertyName<Timestamp>(this, "closetime");
        }

        /**
         * setuptimeのプロパティ名を返します。
         *
         * @return setuptimeのプロパティ名
         */
        public PropertyName<Timestamp> setuptime() {
            return new PropertyName<Timestamp>(this, "setuptime");
        }

        /**
         * shelteraddressのプロパティ名を返します。
         *
         * @return shelteraddressのプロパティ名
         */
        public PropertyName<String> shelteraddress() {
            return new PropertyName<String>(this, "shelteraddress");
        }

        /**
         * sheltercapacityのプロパティ名を返します。
         *
         * @return sheltercapacityのプロパティ名
         */
        public PropertyName<String> sheltercapacity() {
            return new PropertyName<String>(this, "sheltercapacity");
        }

        /**
         * shelterfaxのプロパティ名を返します。
         *
         * @return shelterfaxのプロパティ名
         */
        public PropertyName<String> shelterfax() {
            return new PropertyName<String>(this, "shelterfax");
        }

        /**
         * shelternameのプロパティ名を返します。
         *
         * @return shelternameのプロパティ名
         */
        public PropertyName<String> sheltername() {
            return new PropertyName<String>(this, "sheltername");
        }

        /**
         * shelterphoneのプロパティ名を返します。
         *
         * @return shelterphoneのプロパティ名
         */
        public PropertyName<String> shelterphone() {
            return new PropertyName<String>(this, "shelterphone");
        }

        /**
         * shelterstaffのプロパティ名を返します。
         *
         * @return shelterstaffのプロパティ名
         */
        public PropertyName<String> shelterstaff() {
            return new PropertyName<String>(this, "shelterstaff");
        }

        /**
         * shelterstatusのプロパティ名を返します。
         *
         * @return shelterstatusのプロパティ名
         */
        public PropertyName<String> shelterstatus() {
            return new PropertyName<String>(this, "shelterstatus");
        }

        /**
         * circleのプロパティ名を返します。
         *
         * @return circleのプロパティ名
         */
        public PropertyName<String> circle() {
            return new PropertyName<String>(this, "circle");
        }

        /**
         * typeのプロパティ名を返します。
         *
         * @return typeのプロパティ名
         */
        public PropertyName<String> type() {
            return new PropertyName<String>(this, "type");
        }

        /**
         * typedetailのプロパティ名を返します。
         *
         * @return typedetailのプロパティ名
         */
        public PropertyName<String> typedetail() {
            return new PropertyName<String>(this, "typedetail");
        }

        /**
         * headcountのプロパティ名を返します。
         *
         * @return headcountのプロパティ名
         */
        public PropertyName<String> headcount() {
            return new PropertyName<String>(this, "headcount");
        }

        /**
         * headcountvoluntaryのプロパティ名を返します。
         *
         * @return headcountvoluntaryのプロパティ名
         */
        public PropertyName<String> headcountvoluntary() {
            return new PropertyName<String>(this, "headcountvoluntary");
        }

        /**
         * householdsのプロパティ名を返します。
         *
         * @return householdsのプロパティ名
         */
        public PropertyName<String> households() {
            return new PropertyName<String>(this, "households");
        }

        /**
         * householdsvoluntaryのプロパティ名を返します。
         *
         * @return householdsvoluntaryのプロパティ名
         */
        public PropertyName<String> householdsvoluntary() {
            return new PropertyName<String>(this, "householdsvoluntary");
        }

        /**
         * publiccommonsReportDataのプロパティ名を返します。
         *
         * @return publiccommonsReportDataのプロパティ名
         */
        public _PubliccommonsReportDataNames publiccommonsReportData() {
            return new _PubliccommonsReportDataNames(this, "publiccommonsReportData");
        }
      }
}

