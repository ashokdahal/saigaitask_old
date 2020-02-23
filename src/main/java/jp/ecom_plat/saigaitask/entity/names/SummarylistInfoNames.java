/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.SummarylistInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link SummarylistInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2016/09/09 20:14:41")
public class SummarylistInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Summary list info";
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
     * targettablemasterinfoidのプロパティ名を返します。
     *
     * @return targettablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> targettablemasterinfoid() {
        return new PropertyName<Long>("targettablemasterinfoid");
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
     * localgovcodeのプロパティ名を返します。
     *
     * @return localgovcodeのプロパティ名
     */
    public static PropertyName<String> localgovcode() {
        return new PropertyName<String>("localgovcode");
    }

    /**
     * targettablemasterInfoのプロパティ名を返します。
     *
     * @return targettablemasterInfoのプロパティ名
     */
    public static _TablemasterInfoNames targettablemasterInfo() {
        return new _TablemasterInfoNames("targettablemasterInfo");
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
     * localgovInfoのプロパティ名を返します。
     *
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _SummarylistInfoNames extends PropertyName<SummarylistInfo> {

        /**
         * インスタンスを構築します。
         */
        public _SummarylistInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _SummarylistInfoNames(final String name) {
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
        public _SummarylistInfoNames(final PropertyName<?> parent, final String name) {
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
         * targettablemasterinfoidのプロパティ名を返します。
         *
         * @return targettablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> targettablemasterinfoid() {
            return new PropertyName<Long>("targettablemasterinfoid");
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
         * localgovcodeのプロパティ名を返します。
         *
         * @return localgovcodeのプロパティ名
         */
        public PropertyName<String> localgovcode() {
            return new PropertyName<String>(this, "localgovcode");
        }

        /**
         * targettablemasterInfoのプロパティ名を返します。
         *
         * @return targettablemasterInfoのプロパティ名
         */
        public _TablemasterInfoNames targettablemasterInfo() {
            return new _TablemasterInfoNames("targettablemasterInfo");
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
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }
    }
}
