/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.JalertrequestInfo;
import jp.ecom_plat.saigaitask.entity.names.JalerttypeMasterNames._JalerttypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JalertrequestInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/03/13 20:19:00")
public class JalertrequestInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "JAlert info";
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
     * jalerttypeidのプロパティ名を返します。
     *
     * @return jalerttypeidのプロパティ名
     */
    public static PropertyName<Integer> jalerttypeid() {
        return new PropertyName<Integer>("jalerttypeid");
    }

    /**
     * meteoareaidのプロパティ名を返します。
     *
     * @return meteoareaidのプロパティ名
     */
    public static PropertyName<String> meteoareaid() {
        return new PropertyName<String>("meteoareaid");
    }

    /**
     * meteoareaid2のプロパティ名を返します。
     *
     * @return meteoareaid2のプロパティ名
     */
    public static PropertyName<String> meteoareaid2() {
        return new PropertyName<String>("meteoareaid2");
    }

    /**
     * alarmのプロパティ名を返します。
     *
     * @return alarmのプロパティ名
     */
    public static PropertyName<Boolean> alarm() {
        return new PropertyName<Boolean>("alarm");
    }

    /**
     * viewのプロパティ名を返します。
     *
     * @return viewのプロパティ名
     */
    public static PropertyName<Boolean> view() {
        return new PropertyName<Boolean>("view");
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
     * localgovInfoのプロパティ名を返します。
     *
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * jalerttypeMasterのプロパティ名を返します。
     *
     * @return jalerttypeMasterのプロパティ名
     */
    public static _JalerttypeMasterNames jalerttypeMaster() {
        return new _JalerttypeMasterNames("jalerttypeMaster");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _JalertrequestInfoNames extends PropertyName<JalertrequestInfo> {

        /**
         * インスタンスを構築します。
         */
        public _JalertrequestInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _JalertrequestInfoNames(final String name) {
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
        public _JalertrequestInfoNames(final PropertyName<?> parent, final String name) {
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
         * jalerttypeidのプロパティ名を返します。
         *
         * @return jalerttypeidのプロパティ名
         */
        public PropertyName<Integer> jalerttypeid() {
            return new PropertyName<Integer>(this, "jalerttypeid");
        }

        /**
         * meteoareaidのプロパティ名を返します。
         *
         * @return meteoareaidのプロパティ名
         */
        public PropertyName<String> meteoareaid() {
            return new PropertyName<String>(this, "meteoareaid");
        }

        /**
         * meteoareaid2のプロパティ名を返します。
         *
         * @return meteoareaid2のプロパティ名
         */
        public PropertyName<String> meteoareaid2() {
            return new PropertyName<String>(this, "meteoareaid2");
        }

        /**
         * alarmのプロパティ名を返します。
         *
         * @return alarmのプロパティ名
         */
        public PropertyName<Boolean> alarm() {
            return new PropertyName<Boolean>(this, "alarm");
        }

        /**
         * viewのプロパティ名を返します。
         *
         * @return viewのプロパティ名
         */
        public PropertyName<Boolean> view() {
            return new PropertyName<Boolean>(this, "view");
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
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * jalerttypeMasterのプロパティ名を返します。
         *
         * @return  jalerttypeMasterのプロパティ名
         */
        public _JalerttypeMasterNames jalerttypeMaster() {
            return new _JalerttypeMasterNames(this, "jalerttypeMaster");
        }
    }
}
