/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import org.seasar.extension.jdbc.name.PropertyName;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ToolboxData;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ToolboxtypeMasterNames._ToolboxtypeMasterNames;

/**
 * {@link ToolboxData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2016/07/22 16:56:45")
public class ToolboxDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Tool box info";
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
     * toolboxtypeidのプロパティ名を返します。
     *
     * @return toolboxtypeidのプロパティ名
     */
    public static PropertyName<Long> toolboxtypeid() {
        return new PropertyName<Long>("toolboxtypeid");
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
     * attrid1のプロパティ名を返します。
     *
     * @return attrid1のプロパティ名
     */
    public static PropertyName<String> attrid1() {
        return new PropertyName<String>("attrid1");
    }

    /**
     * attrid2のプロパティ名を返します。
     *
     * @return attrid2のプロパティ名
     */
    public static PropertyName<String> attrid2() {
        return new PropertyName<String>("attrid2");
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
     * disporderのプロパティ名を返します。
     *
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
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
     * localgovInfoのプロパティ名を返します。
     *
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * toolboxtypeMasterのプロパティ名を返します。
     *
     * @return toolboxtypeMasterのプロパティ名
     */
    public static _ToolboxtypeMasterNames toolboxtypeMaster() {
        return new _ToolboxtypeMasterNames("toolboxtypeMaster");
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
    public static class _ToolboxDataNames extends PropertyName<ToolboxData> {

        /**
         * インスタンスを構築します。
         */
        public _ToolboxDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _ToolboxDataNames(final String name) {
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
        public _ToolboxDataNames(final PropertyName<?> parent, final String name) {
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
         * toolboxtypeidのプロパティ名を返します。
         *
         * @return toolboxtypeidのプロパティ名
         */
        public PropertyName<Long> toolboxtypeid() {
            return new PropertyName<Long>(this, "toolboxtypeid");
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
         * attrid1のプロパティ名を返します。
         *
         * @return attrid1のプロパティ名
         */
        public PropertyName<String> attrid1() {
            return new PropertyName<String>(this, "attrid1");
        }

        /**
         * attrid2のプロパティ名を返します。
         *
         * @return attrid2のプロパティ名
         */
        public PropertyName<String> attrid2() {
            return new PropertyName<String>(this, "attrid2");
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
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
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
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * toolboxtypeMasterのプロパティ名を返します。
         *
         * @return toolboxtypeMasterのプロパティ名
         */
        public _ToolboxtypeMasterNames toolboxtypeMaster() {
            return new _ToolboxtypeMasterNames(this, "toolboxtypeMaster");
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
