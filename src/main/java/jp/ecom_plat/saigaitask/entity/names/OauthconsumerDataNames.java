/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.OauthconsumerData;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link OauthconsumerData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/12/22 10:34:22")
public class OauthconsumerDataNames implements EntityNameInterface{

	@Override
	public String entityName() {
		return "OAuth consumer data";
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
     * applicationnameのプロパティ名を返します。
     *
     * @return applicationnameのプロパティ名
     */
    public static PropertyName<String> applicationname() {
        return new PropertyName<String>("applicationname");
    }

    /**
     * consumerkeyのプロパティ名を返します。
     *
     * @return consumerkeyのプロパティ名
     */
    public static PropertyName<String> consumerkey() {
        return new PropertyName<String>("consumerkey");
    }

    /**
     * consumerkeysecretのプロパティ名を返します。
     *
     * @return consumerkeysecretのプロパティ名
     */
    public static PropertyName<String> consumerkeysecret() {
        return new PropertyName<String>("consumerkeysecret");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _OauthconsumerDataNames extends PropertyName<OauthconsumerData> {

        /**
         * インスタンスを構築します。
         */
        public _OauthconsumerDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _OauthconsumerDataNames(final String name) {
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
        public _OauthconsumerDataNames(final PropertyName<?> parent, final String name) {
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
         * applicationnameのプロパティ名を返します。
         *
         * @return applicationnameのプロパティ名
         */
        public PropertyName<String> applicationname() {
            return new PropertyName<String>(this, "applicationname");
        }

        /**
         * consumerkeyのプロパティ名を返します。
         *
         * @return consumerkeyのプロパティ名
         */
        public PropertyName<String> consumerkey() {
            return new PropertyName<String>(this, "consumerkey");
        }

        /**
         * consumerkeysecretのプロパティ名を返します。
         *
         * @return consumerkeysecretのプロパティ名
         */
        public PropertyName<String> consumerkeysecret() {
            return new PropertyName<String>(this, "consumerkeysecret");
        }
    }
}
