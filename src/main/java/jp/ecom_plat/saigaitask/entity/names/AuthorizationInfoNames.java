/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AuthorizationInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AuthorizationInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2016/10/05 17:49:07")
public class AuthorizationInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Authorization info";
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
     * nameのプロパティ名を返します。
     *
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
    }

    /**
     * authtypeのプロパティ名を返します。
     *
     * @return authtypeのプロパティ名
     */
    public static PropertyName<String> authtype() {
        return new PropertyName<String>("authtype");
    }

    /**
     * usernameのプロパティ名を返します。
     *
     * @return usernameのプロパティ名
     */
    public static PropertyName<String> username() {
        return new PropertyName<String>("username");
    }

    /**
     * userpassのプロパティ名を返します。
     *
     * @return userpassのプロパティ名
     */
    public static PropertyName<String> userpass() {
        return new PropertyName<String>("userpass");
    }

    /**
     * authwordのプロパティ名を返します。
     *
     * @return authwordのプロパティ名
     */
    public static PropertyName<String> authword() {
        return new PropertyName<String>("authword");
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
    public static class _AuthorizationInfoNames extends PropertyName<AuthorizationInfo> {

        /**
         * インスタンスを構築します。
         */
        public _AuthorizationInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _AuthorizationInfoNames(final String name) {
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
        public _AuthorizationInfoNames(final PropertyName<?> parent, final String name) {
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
         * nameのプロパティ名を返します。
         *
         * @return nameのプロパティ名
         */
        public PropertyName<String> name() {
            return new PropertyName<String>(this, "name");
        }

        /**
         * authtypeのプロパティ名を返します。
         *
         * @return authtypeのプロパティ名
         */
        public PropertyName<String> authtype() {
            return new PropertyName<String>(this, "authtype");
        }

        /**
         * usernameのプロパティ名を返します。
         *
         * @return usernameのプロパティ名
         */
        public PropertyName<String> username() {
            return new PropertyName<String>(this, "username");
        }

        /**
         * userpassのプロパティ名を返します。
         *
         * @return userpassのプロパティ名
         */
        public PropertyName<String> userpass() {
            return new PropertyName<String>(this, "userpass");
        }

        /**
         * authwordのプロパティ名を返します。
         *
         * @return authwordのプロパティ名
         */
        public PropertyName<String> authword() {
            return new PropertyName<String>(this, "authword");
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
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }
    }
}
