/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TelemeterserverInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TelemeterserverInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/20 19:42:39")
public class TelemeterserverInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Telemeter server info";
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
     * urlのプロパティ名を返します。
     *
     * @return urlのプロパティ名
     */
    public static PropertyName<String> url() {
        return new PropertyName<String>("url");
    }

    /**
     * useridのプロパティ名を返します。
     *
     * @return useridのプロパティ名
     */
    public static PropertyName<String> userid() {
        return new PropertyName<String>("userid");
    }

    /**
     * passwordのプロパティ名を返します。
     *
     * @return passwordのプロパティ名
     */
    public static PropertyName<String> password() {
        return new PropertyName<String>("password");
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
     * @author S2JDBC-Gen
     */
    public static class _TelemeterserverInfoNames extends PropertyName<TelemeterserverInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TelemeterserverInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TelemeterserverInfoNames(final String name) {
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
        public _TelemeterserverInfoNames(final PropertyName<?> parent, final String name) {
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
         * urlのプロパティ名を返します。
         *
         * @return urlのプロパティ名
         */
        public PropertyName<String> url() {
            return new PropertyName<String>(this, "url");
        }

        /**
         * useridのプロパティ名を返します。
         *
         * @return useridのプロパティ名
         */
        public PropertyName<String> userid() {
            return new PropertyName<String>(this, "userid");
        }

        /**
         * passwordのプロパティ名を返します。
         *
         * @return passwordのプロパティ名
         */
        public PropertyName<String> password() {
            return new PropertyName<String>(this, "password");
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
    }
}
