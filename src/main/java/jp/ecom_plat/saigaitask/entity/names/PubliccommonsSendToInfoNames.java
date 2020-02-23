/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsSendToInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsSendToInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/09 19:47:29")
public class PubliccommonsSendToInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert receiver data";
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
     * endpointUrlのプロパティ名を返します。
     *
     * @return endpointUrlのプロパティ名
     */
    public static PropertyName<String> endpointUrl() {
        return new PropertyName<String>("endpointUrl");
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
     * passwordのプロパティ名を返します。
     *
     * @return passwordのプロパティ名
     */
    public static PropertyName<String> password() {
        return new PropertyName<String>("password");
    }

    /**
     * sendOrderのプロパティ名を返します。
     *
     * @return sendOrderのプロパティ名
     */
    public static PropertyName<Integer> sendOrder() {
        return new PropertyName<Integer>("sendOrder");
    }


    /**
     * statusValuesのプロパティ名を返します。
     *
     * @return statusValuesのプロパティ名
     */
    public static PropertyName<Integer> statusValues() {
        return new PropertyName<Integer>("statusValues");
    }

    /**
     * endpointUrlBackupのプロパティ名を返します。
     *
     * @return endpointUrlBackupのプロパティ名
     */
    public static PropertyName<String> endpointUrlBackup() {
        return new PropertyName<String>("endpointUrlBackup");
    }

    /**
     * usernameBackupのプロパティ名を返します。
     *
     * @return usernameBackupのプロパティ名
     */
    public static PropertyName<String> usernameBackup() {
        return new PropertyName<String>("usernameBackup");
    }

    /**
     * passwordBackupのプロパティ名を返します。
     *
     * @return passwordBackupのプロパティ名
     */
    public static PropertyName<String> passwordBackup() {
        return new PropertyName<String>("passwordBackup");
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
    public static class _PubliccommonsSendToInfoNames extends PropertyName<PubliccommonsSendToInfo> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsSendToInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _PubliccommonsSendToInfoNames(final String name) {
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
        public _PubliccommonsSendToInfoNames(final PropertyName<?> parent, final String name) {
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
         * endpointUrlのプロパティ名を返します。
         *
         * @return endpointUrlのプロパティ名
         */
        public PropertyName<String> endpointUrl() {
            return new PropertyName<String>(this, "endpointUrl");
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
         * passwordのプロパティ名を返します。
         *
         * @return passwordのプロパティ名
         */
        public PropertyName<String> password() {
            return new PropertyName<String>(this, "password");
        }

        /**
         * sendOrderのプロパティ名を返します。
         *
         * @return sendOrderのプロパティ名
         */
        public PropertyName<Integer> sendOrder() {
            return new PropertyName<Integer>(this, "sendOrder");
        }

        /**
         * statusValuesのプロパティ名を返します。
         *
         * @return statusValuesのプロパティ名
         */
        public PropertyName<String> statusValues() {
            return new PropertyName<String>(this, "statusValues");
        }

        /**
         * endpointUrlBackupのプロパティ名を返します。
         *
         * @return endpointUrlBackupのプロパティ名
         */
        public PropertyName<String> endpointUrlBackup() {
            return new PropertyName<String>(this, "endpointUrlBackup");
        }

        /**
         * usernameBackupのプロパティ名を返します。
         *
         * @return usernameBackupのプロパティ名
         */
        public PropertyName<String> usernameBackup() {
            return new PropertyName<String>(this, "usernameBackup");
        }

        /**
         * passwordBackupのプロパティ名を返します。
         *
         * @return passwordBackupのプロパティ名
         */
        public PropertyName<String> passwordBackup() {
            return new PropertyName<String>(this, "passwordBackup");
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
