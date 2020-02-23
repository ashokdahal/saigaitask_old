/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TimelinetableInfo;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TimelinemenueMasterNames._TimelinemenueMasterNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TimelinetableInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/11/29 17:23:50")
public class TimelinetableInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Timeline settings";
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
     * timelinemenuemasteridのプロパティ名を返します。
     *
     * @return timelinemenuemasteridのプロパティ名
     */
    public static PropertyName<Long> timelinemenuemasterid() {
        return new PropertyName<Long>("timelinemenuemasterid");
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
     * column1のプロパティ名を返します。
     *
     * @return column1のプロパティ名
     */
    public static PropertyName<String> column1() {
        return new PropertyName<String>("column1");
    }

    /**
     * column2のプロパティ名を返します。
     *
     * @return column2のプロパティ名
     */
    public static PropertyName<String> column2() {
        return new PropertyName<String>("column2");
    }

    /**
     * column3のプロパティ名を返します。
     *
     * @return column3のプロパティ名
     */
    public static PropertyName<String> column3() {
        return new PropertyName<String>("column3");
    }

    /**
     * statecolumnのプロパティ名を返します。
     *
     * @return statecolumnのプロパティ名
     */
    public static PropertyName<String> statecolumn() {
        return new PropertyName<String>("statecolumn");
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
     * timelinemenueMasterのプロパティ名を返します。
     *
     * @return timelinemenueMasterのプロパティ名
     */
    public static _TimelinemenueMasterNames timelinemenueMaster() {
        return new _TimelinemenueMasterNames("timelinemenueMaster");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TimelinetableInfoNames extends PropertyName<TimelinetableInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TimelinetableInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TimelinetableInfoNames(final String name) {
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
        public _TimelinetableInfoNames(final PropertyName<?> parent, final String name) {
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
         * timelinemenuemasteridのプロパティ名を返します。
         *
         * @return timelinemenuemasteridのプロパティ名
         */
        public PropertyName<Long> timelinemenuemasterid() {
            return new PropertyName<Long>(this, "timelinemenuemasterid");
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
         * column1のプロパティ名を返します。
         *
         * @return column1のプロパティ名
         */
        public PropertyName<String> column1() {
            return new PropertyName<String>(this, "column1");
        }

        /**
         * column2のプロパティ名を返します。
         *
         * @return column2のプロパティ名
         */
        public PropertyName<String> column2() {
            return new PropertyName<String>(this, "column2");
        }

        /**
         * column3のプロパティ名を返します。
         *
         * @return column3のプロパティ名
         */
        public PropertyName<String> column3() {
            return new PropertyName<String>(this, "column3");
        }

        /**
         * statecolumnのプロパティ名を返します。
         *
         * @return statecolumnのプロパティ名
         */
        public PropertyName<String> statecolumn() {
            return new PropertyName<String>(this, "statecolumn");
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
         * timelinemenueMasterのプロパティ名を返します。
         *
         * @return timelinemenueMasterのプロパティ名
         */
        public _TimelinemenueMasterNames timelinemenueMaster() {
            return new _TimelinemenueMasterNames(this, "timelinemenueMaster");
        }
    }
}