/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.SummarylistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.names.SummarylistInfoNames._SummarylistInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link SummarylistcolumnInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/06/03 10:54:38")
public class SummarylistcolumnInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Aggregation list info";
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
     * functionのプロパティ名を返します。
     *
     * @return functionのプロパティ名
     */
    public static PropertyName<String> function() {
        return new PropertyName<String>("function");
    }

    /**
     * conditionのプロパティ名を返します。
     *
     * @return conditionのプロパティ名
     */
    public static PropertyName<String> condition() {
        return new PropertyName<String>("condition");
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
     * summarylistinfoidのプロパティ名を返します。
     *
     * @return summarylistinfoidのプロパティ名
     */
    public static PropertyName<Boolean> summarylistinfoid() {
        return new PropertyName<Boolean>("summarylistinfoid");
    }

    /**
     * attridのプロパティ名を返します。
     *
     * @return attridのプロパティ名
     */
    public static PropertyName<Boolean> attrid() {
        return new PropertyName<Boolean>("attrid");
    }

    /**
     * summarylistInfoのプロパティ名を返します。
     *
     * @return menuInfoのプロパティ名
     */
    public static _SummarylistInfoNames summarylistInfo() {
        return new _SummarylistInfoNames("summarylistInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _SummarylistcolumnInfoNames extends PropertyName<SummarylistcolumnInfo> {

        /**
         * インスタンスを構築します。
         */
        public _SummarylistcolumnInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _SummarylistcolumnInfoNames(final String name) {
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
        public _SummarylistcolumnInfoNames(final PropertyName<?> parent, final String name) {
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
         * functionのプロパティ名を返します。
         *
         * @return functionのプロパティ名
         */
        public PropertyName<String> function() {
            return new PropertyName<String>(this, "function");
        }

        /**
         * conditionのプロパティ名を返します。
         *
         * @return conditionのプロパティ名
         */
        public PropertyName<String> condition() {
            return new PropertyName<String>(this, "condition");
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
         * summarylistinfoidのプロパティ名を返します。
         *
         * @return summarylistinfoidのプロパティ名
         */
        public PropertyName<Boolean> summarylistinfoid() {
            return new PropertyName<Boolean>(this, "summarylistinfoid");
        }

        /**
         * attridのプロパティ名を返します。
         *
         * @return attridのプロパティ名
         */
        public PropertyName<Boolean> attrid() {
            return new PropertyName<Boolean>(this, "attrid");
        }

        /**
         * summarylistInfoのプロパティ名を返します。
         *
         * @return menuInfoのプロパティ名
         */
        public _SummarylistInfoNames summarylistInfo() {
            return new _SummarylistInfoNames(this, "summarylistInfo");
        }
    }
}
