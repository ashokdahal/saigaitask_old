/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.OauthtokenData;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.UnitInfoNames._UnitInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link OauthtokenData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/10/21 20:04:22")
public class OauthtokenDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "OAuth token data";
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
     * consumerKeyのプロパティ名を返します。
     *
     * @return consumerKeyのプロパティ名
     */
    public static PropertyName<String> consumerKey() {
        return new PropertyName<String>("consumerKey");
    }

    /**
     * requestTokenのプロパティ名を返します。
     *
     * @return requestTokenのプロパティ名
     */
    public static PropertyName<String> requestToken() {
        return new PropertyName<String>("requestToken");
    }

    /**
     * accessTokenのプロパティ名を返します。
     *
     * @return accessTokenのプロパティ名
     */
    public static PropertyName<String> accessToken() {
        return new PropertyName<String>("accessToken");
    }

    /**
     * tokenSecretのプロパティ名を返します。
     *
     * @return tokenSecretのプロパティ名
     */
    public static PropertyName<String> tokenSecret() {
        return new PropertyName<String>("tokenSecret");
    }

    /**
     * verifierのプロパティ名を返します。
     *
     * @return verifierのプロパティ名
     */
    public static PropertyName<String> verifier() {
        return new PropertyName<String>("verifier");
    }

    /**
     * groupidのプロパティ名を返します。
     *
     * @return groupidのプロパティ名
     */
    public static PropertyName<Long> groupid() {
        return new PropertyName<Long>("groupid");
    }

    /**
     * unitidのプロパティ名を返します。
     *
     * @return unitidのプロパティ名
     */
    public static PropertyName<Long> unitid() {
        return new PropertyName<Long>("unitid");
    }


    /**
     * createdのプロパティ名を返します。
     *
     * @return createdのプロパティ名
     */
    public static PropertyName<Timestamp> created() {
        return new PropertyName<Timestamp>("created");
    }

    /**
     * lastAccessのプロパティ名を返します。
     *
     * @return lastAccessのプロパティ名
     */
    public static PropertyName<Timestamp> lastAccess() {
        return new PropertyName<Timestamp>("lastAccess");
    }

    /**
     * groupInfoのプロパティ名を返します。
     *
     * @return groupInfoのプロパティ名
     */
    public static _GroupInfoNames groupInfo() {
        return new _GroupInfoNames("groupInfo");
    }

    /**
     * unitInfoのプロパティ名を返します。
     *
     * @return unitInfoのプロパティ名
     */
    public static _UnitInfoNames unitInfo() {
        return new _UnitInfoNames("unitInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _OauthtokenDataNames extends PropertyName<OauthtokenData> {

        /**
         * インスタンスを構築します。
         */
        public _OauthtokenDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _OauthtokenDataNames(final String name) {
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
        public _OauthtokenDataNames(final PropertyName<?> parent, final String name) {
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
         * consumerKeyのプロパティ名を返します。
         *
         * @return consumerKeyのプロパティ名
         */
        public PropertyName<String> consumerKey() {
            return new PropertyName<String>(this, "consumerKey");
        }

        /**
         * requestTokenのプロパティ名を返します。
         *
         * @return requestTokenのプロパティ名
         */
        public PropertyName<String> requestToken() {
            return new PropertyName<String>(this, "requestToken");
        }

        /**
         * accessTokenのプロパティ名を返します。
         *
         * @return accessTokenのプロパティ名
         */
        public PropertyName<String> accessToken() {
            return new PropertyName<String>(this, "accessToken");
        }

        /**
         * tokenSecretのプロパティ名を返します。
         *
         * @return tokenSecretのプロパティ名
         */
        public PropertyName<String> tokenSecret() {
            return new PropertyName<String>(this, "tokenSecret");
        }

        /**
         * verifierのプロパティ名を返します。
         *
         * @return verifierのプロパティ名
         */
        public PropertyName<String> verifier() {
            return new PropertyName<String>(this, "verifier");
        }

        /**
         * groupidのプロパティ名を返します。
         *
         * @return groupidのプロパティ名
         */
        public PropertyName<Long> groupid() {
            return new PropertyName<Long>(this, "groupid");
        }

        /**
         * unitidのプロパティ名を返します。
         *
         * @return unitidのプロパティ名
         */
        public PropertyName<Long> unitid() {
            return new PropertyName<Long>(this, "unitid");
        }

        /**
         * createdのプロパティ名を返します。
         *
         * @return createdのプロパティ名
         */
        public PropertyName<Timestamp> created() {
            return new PropertyName<Timestamp>(this, "created");
        }

        /**
         * lastAccessのプロパティ名を返します。
         *
         * @return lastAccessのプロパティ名
         */
        public PropertyName<Timestamp> lastAccess() {
            return new PropertyName<Timestamp>(this, "lastAccess");
        }

        /**
         * groupInfoのプロパティ名を返します。
         *
         * @return groupInfoのプロパティ名
         */
        public _GroupInfoNames groupInfo() {
            return new _GroupInfoNames(this, "groupInfo");
        }

        /**
         * unitInfoのプロパティ名を返します。
         *
         * @return unitInfoのプロパティ名
         */
        public _UnitInfoNames unitInfo() {
            return new _UnitInfoNames(this, "unitInfo");
        }
    }
}
