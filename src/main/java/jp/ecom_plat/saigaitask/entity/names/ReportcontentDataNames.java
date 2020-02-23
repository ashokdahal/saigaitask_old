/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ReportcontentData;
import jp.ecom_plat.saigaitask.entity.names.ReportDataNames._ReportDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ReportcontentData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/22 14:41:59")
public class ReportcontentDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Type-4 format-1";
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
     * reportdataidのプロパティ名を返します。
     * 
     * @return reportdataidのプロパティ名
     */
    public static PropertyName<Long> reportdataid() {
        return new PropertyName<Long>("reportdataid");
    }

    /**
     * receiverのプロパティ名を返します。
     * 
     * @return receiverのプロパティ名
     */
    public static PropertyName<String> receiver() {
        return new PropertyName<String>("receiver");
    }

    /**
     * reporttimeのプロパティ名を返します。
     * 
     * @return reporttimeのプロパティ名
     */
    public static PropertyName<String> reporttime() {
        return new PropertyName<String>("reporttime");
    }

    /**
     * prefのプロパティ名を返します。
     * 
     * @return prefのプロパティ名
     */
    public static PropertyName<String> pref() {
        return new PropertyName<String>("pref");
    }

    /**
     * cityのプロパティ名を返します。
     * 
     * @return cityのプロパティ名
     */
    public static PropertyName<String> city() {
        return new PropertyName<String>("city");
    }

    /**
     * reporterのプロパティ名を返します。
     * 
     * @return reporterのプロパティ名
     */
    public static PropertyName<String> reporter() {
        return new PropertyName<String>("reporter");
    }

    /**
     * disasternameのプロパティ名を返します。
     * 
     * @return disasternameのプロパティ名
     */
    public static PropertyName<String> disastername() {
        return new PropertyName<String>("disastername");
    }

    /**
     * reportnoのプロパティ名を返します。
     * 
     * @return reportnoのプロパティ名
     */
    public static PropertyName<Integer> reportno() {
        return new PropertyName<Integer>("reportno");
    }

    /**
     * placeのプロパティ名を返します。
     * 
     * @return placeのプロパティ名
     */
    public static PropertyName<String> place() {
        return new PropertyName<String>("place");
    }

    /**
     * occurtimeのプロパティ名を返します。
     * 
     * @return occurtimeのプロパティ名
     */
    public static PropertyName<String> occurtime() {
        return new PropertyName<String>("occurtime");
    }

    /**
     * summaryのプロパティ名を返します。
     * 
     * @return summaryのプロパティ名
     */
    public static PropertyName<String> summary() {
        return new PropertyName<String>("summary");
    }

    /**
     * casualties1のプロパティ名を返します。
     * 
     * @return casualties1のプロパティ名
     */
    public static PropertyName<Integer> casualties1() {
        return new PropertyName<Integer>("casualties1");
    }

    /**
     * casualties2のプロパティ名を返します。
     * 
     * @return casualties2のプロパティ名
     */
    public static PropertyName<Integer> casualties2() {
        return new PropertyName<Integer>("casualties2");
    }

    /**
     * casualties3のプロパティ名を返します。
     * 
     * @return casualties3のプロパティ名
     */
    public static PropertyName<Integer> casualties3() {
        return new PropertyName<Integer>("casualties3");
    }

    /**
     * totalのプロパティ名を返します。
     * 
     * @return totalのプロパティ名
     */
    public static PropertyName<Integer> total() {
        return new PropertyName<Integer>("total");
    }

    /**
     * house1のプロパティ名を返します。
     * 
     * @return house1のプロパティ名
     */
    public static PropertyName<Integer> house1() {
        return new PropertyName<Integer>("house1");
    }

    /**
     * house2のプロパティ名を返します。
     * 
     * @return house2のプロパティ名
     */
    public static PropertyName<Integer> house2() {
        return new PropertyName<Integer>("house2");
    }

    /**
     * house3のプロパティ名を返します。
     * 
     * @return house3のプロパティ名
     */
    public static PropertyName<Integer> house3() {
        return new PropertyName<Integer>("house3");
    }

    /**
     * house4のプロパティ名を返します。
     * 
     * @return house4のプロパティ名
     */
    public static PropertyName<Integer> house4() {
        return new PropertyName<Integer>("house4");
    }

    /**
     * headoffice1のプロパティ名を返します。
     * 
     * @return headoffice1のプロパティ名
     */
    public static PropertyName<String> headoffice1() {
        return new PropertyName<String>("headoffice1");
    }

    /**
     * headoffice2のプロパティ名を返します。
     * 
     * @return headoffice2のプロパティ名
     */
    public static PropertyName<String> headoffice2() {
        return new PropertyName<String>("headoffice2");
    }

    /**
     * status1のプロパティ名を返します。
     * 
     * @return status1のプロパティ名
     */
    public static PropertyName<String> status1() {
        return new PropertyName<String>("status1");
    }

    /**
     * status2のプロパティ名を返します。
     * 
     * @return status2のプロパティ名
     */
    public static PropertyName<String> status2() {
        return new PropertyName<String>("status2");
    }

    /**
     * reportDataのプロパティ名を返します。
     * 
     * @return reportDataのプロパティ名
     */
    public static _ReportDataNames reportData() {
        return new _ReportDataNames("reportData");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _ReportcontentDataNames extends PropertyName<ReportcontentData> {

        /**
         * インスタンスを構築します。
         */
        public _ReportcontentDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _ReportcontentDataNames(final String name) {
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
        public _ReportcontentDataNames(final PropertyName<?> parent, final String name) {
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
         * reportdataidのプロパティ名を返します。
         *
         * @return reportdataidのプロパティ名
         */
        public PropertyName<Long> reportdataid() {
            return new PropertyName<Long>(this, "reportdataid");
        }

        /**
         * receiverのプロパティ名を返します。
         *
         * @return receiverのプロパティ名
         */
        public PropertyName<String> receiver() {
            return new PropertyName<String>(this, "receiver");
        }

        /**
         * reporttimeのプロパティ名を返します。
         *
         * @return reporttimeのプロパティ名
         */
        public PropertyName<String> reporttime() {
            return new PropertyName<String>(this, "reporttime");
        }

        /**
         * prefのプロパティ名を返します。
         *
         * @return prefのプロパティ名
         */
        public PropertyName<String> pref() {
            return new PropertyName<String>(this, "pref");
        }

        /**
         * cityのプロパティ名を返します。
         *
         * @return cityのプロパティ名
         */
        public PropertyName<String> city() {
            return new PropertyName<String>(this, "city");
        }

        /**
         * reporterのプロパティ名を返します。
         *
         * @return reporterのプロパティ名
         */
        public PropertyName<String> reporter() {
            return new PropertyName<String>(this, "reporter");
        }

        /**
         * disasternameのプロパティ名を返します。
         *
         * @return disasternameのプロパティ名
         */
        public PropertyName<String> disastername() {
            return new PropertyName<String>(this, "disastername");
        }

        /**
         * reportnoのプロパティ名を返します。
         *
         * @return reportnoのプロパティ名
         */
        public PropertyName<Integer> reportno() {
            return new PropertyName<Integer>(this, "reportno");
        }

        /**
         * placeのプロパティ名を返します。
         *
         * @return placeのプロパティ名
         */
        public PropertyName<String> place() {
            return new PropertyName<String>(this, "place");
        }

        /**
         * occurtimeのプロパティ名を返します。
         *
         * @return occurtimeのプロパティ名
         */
        public PropertyName<String> occurtime() {
            return new PropertyName<String>(this, "occurtime");
        }

        /**
         * summaryのプロパティ名を返します。
         *
         * @return summaryのプロパティ名
         */
        public PropertyName<String> summary() {
            return new PropertyName<String>(this, "summary");
        }

        /**
         * casualties1のプロパティ名を返します。
         *
         * @return casualties1のプロパティ名
         */
        public PropertyName<Integer> casualties1() {
            return new PropertyName<Integer>(this, "casualties1");
        }

        /**
         * casualties2のプロパティ名を返します。
         *
         * @return casualties2のプロパティ名
         */
        public PropertyName<Integer> casualties2() {
            return new PropertyName<Integer>(this, "casualties2");
        }

        /**
         * casualties3のプロパティ名を返します。
         *
         * @return casualties3のプロパティ名
         */
        public PropertyName<Integer> casualties3() {
            return new PropertyName<Integer>(this, "casualties3");
        }

        /**
         * totalのプロパティ名を返します。
         *
         * @return totalのプロパティ名
         */
        public PropertyName<Integer> total() {
            return new PropertyName<Integer>(this, "total");
        }

        /**
         * house1のプロパティ名を返します。
         *
         * @return house1のプロパティ名
         */
        public PropertyName<Integer> house1() {
            return new PropertyName<Integer>(this, "house1");
        }

        /**
         * house2のプロパティ名を返します。
         *
         * @return house2のプロパティ名
         */
        public PropertyName<Integer> house2() {
            return new PropertyName<Integer>(this, "house2");
        }

        /**
         * house3のプロパティ名を返します。
         *
         * @return house3のプロパティ名
         */
        public PropertyName<Integer> house3() {
            return new PropertyName<Integer>(this, "house3");
        }

        /**
         * house4のプロパティ名を返します。
         *
         * @return house4のプロパティ名
         */
        public PropertyName<Integer> house4() {
            return new PropertyName<Integer>(this, "house4");
        }

        /**
         * headoffice1のプロパティ名を返します。
         *
         * @return headoffice1のプロパティ名
         */
        public PropertyName<String> headoffice1() {
            return new PropertyName<String>(this, "headoffice1");
        }

        /**
         * headoffice2のプロパティ名を返します。
         *
         * @return headoffice2のプロパティ名
         */
        public PropertyName<String> headoffice2() {
            return new PropertyName<String>(this, "headoffice2");
        }

        /**
         * status1のプロパティ名を返します。
         *
         * @return status1のプロパティ名
         */
        public PropertyName<String> status1() {
            return new PropertyName<String>(this, "status1");
        }

        /**
         * status2のプロパティ名を返します。
         *
         * @return status2のプロパティ名
         */
        public PropertyName<String> status2() {
            return new PropertyName<String>(this, "status2");
        }

        /**
         * reportDataのプロパティ名を返します。
         * 
         * @return reportDataのプロパティ名
         */
        public _ReportDataNames reportData() {
            return new _ReportDataNames(this, "reportData");
        }
    }
}
