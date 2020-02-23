/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TelemeterofficeInfo;
import jp.ecom_plat.saigaitask.entity.names.TelemeterserverInfoNames._TelemeterserverInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TelemeterofficeInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/20 19:42:39")
public class TelemeterofficeInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Telemeter management office info";
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
     * telemeterserverinfoidのプロパティ名を返します。
     *
     * @return telemeterserverinfoidのプロパティ名
     */
    public static PropertyName<Long> telemeterserverinfoid() {
        return new PropertyName<Long>("telemeterserverinfoid");
    }

    /**
     * officecodeのプロパティ名を返します。
     *
     * @return officecodeのプロパティ名
     */
    public static PropertyName<String> officecode() {
        return new PropertyName<String>("officecode");
    }

    /**
     * officenameのプロパティ名を返します。
     *
     * @return officenameのプロパティ名
     */
    public static PropertyName<String> officename() {
        return new PropertyName<String>("officename");
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
     * telemeterserverInfoのプロパティ名を返します。
     *
     * @return telemeterserverInfoのプロパティ名
     */
    public static _TelemeterserverInfoNames telemeterserverInfo() {
        return new _TelemeterserverInfoNames("telemeterserverInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TelemeterofficeInfoNames extends PropertyName<TelemeterofficeInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TelemeterofficeInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TelemeterofficeInfoNames(final String name) {
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
        public _TelemeterofficeInfoNames(final PropertyName<?> parent, final String name) {
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
         * telemeterserverinfoidのプロパティ名を返します。
         *
         * @return telemeterserverinfoidのプロパティ名
         */
        public PropertyName<Long> telemeterserverinfoid() {
            return new PropertyName<Long>(this, "telemeterserverinfoid");
        }

        /**
         * officecodeのプロパティ名を返します。
         *
         * @return officecodeのプロパティ名
         */
        public PropertyName<String> officecode() {
            return new PropertyName<String>(this, "officecode");
        }

        /**
         * officenameのプロパティ名を返します。
         *
         * @return officenameのプロパティ名
         */
        public PropertyName<String> officename() {
            return new PropertyName<String>(this, "officename");
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
         * telemeterserverInfoのプロパティ名を返します。
         *
         * @return telemeterserverInfoのプロパティ名
         */
        public _TelemeterserverInfoNames telemeterserverInfo() {
            return new _TelemeterserverInfoNames(this, "telemeterserverInfo");
        }
    }
}
