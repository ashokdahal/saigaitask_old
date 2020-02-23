/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupuserInfo;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.UserInfoNames._UserInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link NoticegroupuserInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:31")
public class NoticegroupuserInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Notification group user info";
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
     * noticegroupinfoidのプロパティ名を返します。
     * 
     * @return noticegroupinfoidのプロパティ名
     */
    public static PropertyName<Long> noticegroupinfoid() {
        return new PropertyName<Long>("noticegroupinfoid");
    }

    /**
     * useridのプロパティ名を返します。
     * 
     * @return useridのプロパティ名
     */
    public static PropertyName<Long> userid() {
        return new PropertyName<Long>("userid");
    }

    /**
     * userInfoのプロパティ名を返します。
     * 
     * @return userInfoのプロパティ名
     */
    public static _UserInfoNames userInfo() {
        return new _UserInfoNames("userInfo");
    }

    /**
     * noticegroupInfoのプロパティ名を返します。
     * 
     * @return noticegroupInfoのプロパティ名
     */
    public static _NoticegroupInfoNames noticegroupInfo() {
        return new _NoticegroupInfoNames("noticegroupInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _NoticegroupuserInfoNames extends PropertyName<NoticegroupuserInfo> {

        /**
         * インスタンスを構築します。
         */
        public _NoticegroupuserInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _NoticegroupuserInfoNames(final String name) {
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
        public _NoticegroupuserInfoNames(final PropertyName<?> parent, final String name) {
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
         * noticegroupinfoidのプロパティ名を返します。
         *
         * @return noticegroupinfoidのプロパティ名
         */
        public PropertyName<Long> noticegroupinfoid() {
            return new PropertyName<Long>(this, "noticegroupinfoid");
        }

        /**
         * useridのプロパティ名を返します。
         *
         * @return useridのプロパティ名
         */
        public PropertyName<Long> userid() {
            return new PropertyName<Long>(this, "userid");
        }

        /**
         * userInfoのプロパティ名を返します。
         * 
         * @return userInfoのプロパティ名
         */
        public _UserInfoNames userInfo() {
            return new _UserInfoNames(this, "userInfo");
        }

        /**
         * noticegroupInfoのプロパティ名を返します。
         * 
         * @return noticegroupInfoのプロパティ名
         */
        public _NoticegroupInfoNames noticegroupInfo() {
            return new _NoticegroupInfoNames(this, "noticegroupInfo");
        }
    }
}
