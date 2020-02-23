/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.DatatransferInfo;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link DatatransferInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2017/06/13 11:44:13")
public class DatatransferInfoNames implements EntityNameInterface {


	@Override
	public String entityName() {
		return "Data transfer info";
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
     * tablemasterinfoidのプロパティ名を返します。
     * 
     * @return tablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> tablemasterinfoid() {
        return new PropertyName<Long>("tablemasterinfoid");
    }

    /**
     * formatのプロパティ名を返します。
     * 
     * @return formatのプロパティ名
     */
    public static PropertyName<String> format() {
        return new PropertyName<String>("format");
    }

    /**
     * protocolのプロパティ名を返します。
     * 
     * @return protocolのプロパティ名
     */
    public static PropertyName<String> protocol() {
        return new PropertyName<String>("protocol");
    }

    /**
     * hostのプロパティ名を返します。
     * 
     * @return hostのプロパティ名
     */
    public static PropertyName<String> host() {
        return new PropertyName<String>("host");
    }

    /**
     * portのプロパティ名を返します。
     * 
     * @return portのプロパティ名
     */
    public static PropertyName<String> port() {
        return new PropertyName<String>("port");
    }

    /**
     * authenticationのプロパティ名を返します。
     * 
     * @return authenticationのプロパティ名
     */
    public static PropertyName<String> authentication() {
        return new PropertyName<String>("authentication");
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
     * directoryのプロパティ名を返します。
     * 
     * @return directoryのプロパティ名
     */
    public static PropertyName<String> directory() {
        return new PropertyName<String>("directory");
    }

    /**
     * transfertypeのプロパティ名を返します。
     * 
     * @return transfertypeのプロパティ名
     */
    public static PropertyName<Integer> transfertype() {
        return new PropertyName<Integer>("transfertype");
    }

    /**
     * crontextのプロパティ名を返します。
     * 
     * @return crontextのプロパティ名
     */
    public static PropertyName<String> crontext() {
        return new PropertyName<String>("crontext");
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
     * noteのプロパティ名を返します。
     * 
     * @return noteのプロパティ名
     */
    public static PropertyName<String> note() {
        return new PropertyName<String>("note");
    }

    /**
     * tablemasterInfoのプロパティ名を返します。
     *
     * @return tablemasterInfoのプロパティ名
     */
    public static _TablemasterInfoNames tablemasterInfo() {
        return new _TablemasterInfoNames("tablemasterInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _DatatransferInfoNames extends PropertyName<DatatransferInfo> {

        /**
         * インスタンスを構築します。
         */
        public _DatatransferInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _DatatransferInfoNames(final String name) {
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
        public _DatatransferInfoNames(final PropertyName<?> parent, final String name) {
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
         * tablemasterinfoidのプロパティ名を返します。
         *
         * @return tablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> tablemasterinfoid() {
            return new PropertyName<Long>(this, "tablemasterinfoid");
        }

        /**
         * formatのプロパティ名を返します。
         *
         * @return formatのプロパティ名
         */
        public PropertyName<String> format() {
            return new PropertyName<String>(this, "format");
        }

        /**
         * protocolのプロパティ名を返します。
         *
         * @return protocolのプロパティ名
         */
        public PropertyName<String> protocol() {
            return new PropertyName<String>(this, "protocol");
        }

        /**
         * hostのプロパティ名を返します。
         *
         * @return hostのプロパティ名
         */
        public PropertyName<String> host() {
            return new PropertyName<String>(this, "host");
        }

        /**
         * portのプロパティ名を返します。
         *
         * @return portのプロパティ名
         */
        public PropertyName<String> port() {
            return new PropertyName<String>(this, "port");
        }

        /**
         * authenticationのプロパティ名を返します。
         *
         * @return authenticationのプロパティ名
         */
        public PropertyName<String> authentication() {
            return new PropertyName<String>(this, "authentication");
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
         * directoryのプロパティ名を返します。
         *
         * @return directoryのプロパティ名
         */
        public PropertyName<String> directory() {
            return new PropertyName<String>(this, "directory");
        }

        /**
         * transfertypeのプロパティ名を返します。
         *
         * @return transfertypeのプロパティ名
         */
        public PropertyName<Integer> transfertype() {
            return new PropertyName<Integer>(this, "transfertype");
        }

        /**
         * crontextのプロパティ名を返します。
         *
         * @return crontextのプロパティ名
         */
        public PropertyName<String> crontext() {
            return new PropertyName<String>(this, "crontext");
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
         * noteのプロパティ名を返します。
         *
         * @return noteのプロパティ名
         */
        public PropertyName<String> note() {
            return new PropertyName<String>(this, "note");
        }

        /**
         * tablemasterInfoのプロパティ名を返します。
         *
         * @return tablemasterInfoのプロパティ名
         */
        public _TablemasterInfoNames tablemasterInfo() {
            return new _TablemasterInfoNames(this, "tablemasterInfo");
        }

    }
}
