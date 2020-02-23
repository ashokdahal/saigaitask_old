/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.JudgemanInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JudgemanInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/25 1:05:03")
public class JudgemanInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Data judging management info";
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
     * disasteridのプロパティ名を返します。
     *
     * @return disasteridのプロパティ名
     */
    /*public static PropertyName<Integer> disasterid() {
        return new PropertyName<Integer>("disasterid");
    }*/

    /**
     * nameのプロパティ名を返します。
     *
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
    }

    /**
     * intervalのプロパティ名を返します。
     *
     * @return intervalのプロパティ名
     */
    public static PropertyName<Integer> interval() {
        return new PropertyName<Integer>("interval");
    }

    /**
     * delayのプロパティ名を返します。
     *
     * @return delayのプロパティ名
     */
    public static PropertyName<Integer> delay() {
        return new PropertyName<Integer>("delay");
    }

    /**
     * judgeorderのプロパティ名を返します。
     *
     * @return judgeorderのプロパティ名
     */
    public static PropertyName<Integer> judgeorder() {
        return new PropertyName<Integer>("judgeorder");
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
     * validのプロパティ名を返します。
     *
     * @return validのプロパティ名
     */
    public static PropertyName<Boolean> valid() {
        return new PropertyName<Boolean>("valid");
    }

    /**
     * disasterMasterのプロパティ名を返します。
     *
     * @return disasterMasterのプロパティ名
     */
    /*public static _DisasterMasterNames disasterMaster() {
        return new _DisasterMasterNames("disasterMaster");
    }*/

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
    public static class _JudgemanInfoNames extends PropertyName<JudgemanInfo> {

        /**
         * インスタンスを構築します。
         */
        public _JudgemanInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _JudgemanInfoNames(final String name) {
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
        public _JudgemanInfoNames(final PropertyName<?> parent, final String name) {
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
         * disasteridのプロパティ名を返します。
         *
         * @return disasteridのプロパティ名
         */
        /*public PropertyName<Integer> disasterid() {
            return new PropertyName<Integer>(this, "disasterid");
        }*/

        /**
         * nameのプロパティ名を返します。
         *
         * @return nameのプロパティ名
         */
        public PropertyName<String> name() {
            return new PropertyName<String>(this, "name");
        }

        /**
         * intervalのプロパティ名を返します。
         *
         * @return intervalのプロパティ名
         */
        public PropertyName<Integer> interval() {
            return new PropertyName<Integer>(this, "interval");
        }

        /**
         * delayのプロパティ名を返します。
         *
         * @return delayのプロパティ名
         */
        public PropertyName<Integer> delay() {
            return new PropertyName<Integer>(this, "delay");
        }

        /**
         * judgeorderのプロパティ名を返します。
         *
         * @return judgeorderのプロパティ名
         */
        public PropertyName<Integer> judgeorder() {
            return new PropertyName<Integer>(this, "judgeorder");
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
         * validのプロパティ名を返します。
         *
         * @return validのプロパティ名
         */
        public PropertyName<Boolean> valid() {
            return new PropertyName<Boolean>(this, "valid");
        }

        /**
         * disasterMasterのプロパティ名を返します。
         *
         * @return disasterMasterのプロパティ名
         */
        /*public _DisasterMasterNames disasterMaster() {
            return new _DisasterMasterNames(this, "disasterMaster");
        }*/

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
