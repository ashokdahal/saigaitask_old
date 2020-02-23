/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.DecisionsupportInfo;
import jp.ecom_plat.saigaitask.entity.names.DecisionsupporttypeMasterNames._DecisionsupporttypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link DecisionsupportInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2016/02/26 17:17:06")
public class DecisionsupportInfoNames implements EntityNameInterface {
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	@Override
	public String entityName() {
		return ("Decision support layer info");
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
     * decisionsupporttypeidのプロパティ名を返します。
     *
     * @return decisionsupporttypeidのプロパティ名
     */
    public static PropertyName<Integer> decisionsupporttypeid() {
        return new PropertyName<Integer>("decisionsupporttypeid");
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
     * calculation_methodのプロパティ名を返します。
     *
     * @return calculation_methodのプロパティ名
     */
    public static PropertyName<String> calculation_method() {
        return new PropertyName<String>("calculation_method");
    }

    /**
     * bufferのプロパティ名を返します。
     *
     * @return bufferのプロパティ名
     */
    public static PropertyName<Integer> buffer() {
        return new PropertyName<Integer>("buffer");
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
     * decisionsupporttypeMasterのプロパティ名を返します。
     *
     * @return decisionsupporttypeMasterのプロパティ名
     */
    public static _DecisionsupporttypeMasterNames decisionsupporttypeMaster() {
        return new _DecisionsupporttypeMasterNames("decisionsupporttypeMaster");
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
    public static class _DecisionsupportInfoNames extends PropertyName<DecisionsupportInfo> {

        /**
         * インスタンスを構築します。
         */
        public _DecisionsupportInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _DecisionsupportInfoNames(final String name) {
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
        public _DecisionsupportInfoNames(final PropertyName<?> parent, final String name) {
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
         * decisionsupporttypeidのプロパティ名を返します。
         *
         * @return decisionsupporttypeidのプロパティ名
         */
        public PropertyName<Integer> decisionsupporttypeid() {
            return new PropertyName<Integer>(this, "decisionsupporttypeid");
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
         * calculation_methodのプロパティ名を返します。
         *
         * @return calculation_methodのプロパティ名
         */
        public PropertyName<String> calculation_method() {
            return new PropertyName<String>(this, "calculation_method");
        }

        /**
         * bufferのプロパティ名を返します。
         *
         * @return bufferのプロパティ名
         */
        public PropertyName<Integer> buffer() {
            return new PropertyName<Integer>(this, "buffer");
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
         * decisionsupporttypeMasterのプロパティ名を返します。
         *
         * @return decisionsupporttypeMasterのプロパティ名
         */
        public _DecisionsupporttypeMasterNames decisionsupporttypeMaster() {
            return new _DecisionsupporttypeMasterNames(this, "decisionsupporttypeMaster");
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
