/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ExternaltabledataInfo;
import jp.ecom_plat.saigaitask.entity.names.AuthorizationInfoNames._AuthorizationInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ExternalmapdataInfoNames._ExternalmapdataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ExternaltabledataInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/08/19 12:45:15")
public class ExternaltabledataInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "External list data info";
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
     * menuinfoidのプロパティ名を返します。
     *
     * @return menuinfoidのプロパティ名
     */
    public static PropertyName<Long> menuinfoid() {
        return new PropertyName<Long>("menuinfoid");
    }

    /**
     * metadataidのプロパティ名を返します。
     *
     * @return metadataidのプロパティ名
     */
    public static PropertyName<String> metadataid() {
        return new PropertyName<String>("metadataid");
    }

    /**
     * filteridのプロパティ名を返します。
     *
     * @return filteridのプロパティ名
     */
    public static PropertyName<Long> filterid() {
        return new PropertyName<Long>("filterid");
    }

    /**
     * authorizationinfoidのプロパティ名を返します。
     *
     * @return authorizationinfoidのプロパティ名
     */
    public static PropertyName<Long> authorizationinfoid() {
        return new PropertyName<Long>("authorizationinfoid");
    }

    /**
     * menuInfoのプロパティ名を返します。
     *
     * @return menuInfoのプロパティ名
     */
    public static _MenuInfoNames menuInfo() {
        return new _MenuInfoNames("menuInfo");
    }

    /**
     * authorizationInfoのプロパティ名を返します。
     *
     * @return menuInfoのプロパティ名
     */
    public static _AuthorizationInfoNames authorizationInfo() {
        return new _AuthorizationInfoNames("authorizationInfo");
    }

    /**
     * externalmapdataInfoListのプロパティ名を返します。
     *
     * @return externalmapdataInfoListのプロパティ名
     */
    public static _ExternalmapdataInfoNames externalmapdataInfoList() {
        return new _ExternalmapdataInfoNames("externalmapdataInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _ExternaltabledataInfoNames extends PropertyName<ExternaltabledataInfo> {

        /**
         * インスタンスを構築します。
         */
        public _ExternaltabledataInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _ExternaltabledataInfoNames(final String name) {
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
        public _ExternaltabledataInfoNames(final PropertyName<?> parent, final String name) {
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
         * menuinfoidのプロパティ名を返します。
         *
         * @return menuinfoidのプロパティ名
         */
        public PropertyName<Long> menuinfoid() {
            return new PropertyName<Long>(this, "menuinfoid");
        }

        /**
         * metadataidのプロパティ名を返します。
         *
         * @return metadataidのプロパティ名
         */
        public PropertyName<String> metadataid() {
            return new PropertyName<String>(this, "metadataid");
        }

        /**
         * filteridのプロパティ名を返します。
         *
         * @return filteridのプロパティ名
         */
        public PropertyName<Long> filterid() {
            return new PropertyName<Long>(this, "filterid");
        }

        /**
         * authorizationinfoidのプロパティ名を返します。
         *
         * @return authorizationinfoidのプロパティ名
         */
        public PropertyName<String> authorizationinfoid() {
            return new PropertyName<String>(this, "authorizationinfoid");
        }

        /**
         * menuInfoのプロパティ名を返します。
         *
         * @return menuInfoのプロパティ名
         */
        public _MenuInfoNames menuInfo() {
            return new _MenuInfoNames(this, "menuInfo");
        }

        /**
         * authorizationInfoのプロパティ名を返します。
         *
         * @return authorizationInfoのプロパティ名
         */
        public _AuthorizationInfoNames authorizationInfo() {
            return new _AuthorizationInfoNames(this, "authorizationInfo");
        }

        /**
         * externalmapdataInfoListのプロパティ名を返します。
         *
         * @return externalmapdataInfoListのプロパティ名
         */
        public _ExternalmapdataInfoNames externalmapdataInfoList() {
            return new _ExternalmapdataInfoNames(this, "externalmapdataInfoList");
        }
    }
}
