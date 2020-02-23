/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.EcomgwpostInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link EcomgwpostInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/03/16 14:29:54")
public class EcomgwpostInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "e-Com GW posting info";
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
     * nameのプロパティ名を返します。
     *
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
    }

    /**
     * groupidのプロパティ名を返します。
     *
     * @return groupidのプロパティ名
     */
    public static PropertyName<String> groupid() {
        return new PropertyName<String>("groupid");
    }

    /**
     * partsidのプロパティ名を返します。
     *
     * @return partsidのプロパティ名
     */
    public static PropertyName<String> partsid() {
        return new PropertyName<String>("partsid");
    }

    /**
     * blockidのプロパティ名を返します。
     *
     * @return blockidのプロパティ名
     */
    public static PropertyName<String> blockid() {
        return new PropertyName<String>("blockid");
    }

    /**
     * posturlのプロパティ名を返します。
     *
     * @return posturlのプロパティ名
     */
    public static PropertyName<String> posturl() {
        return new PropertyName<String>("posturl");
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
     * disporderのプロパティ名を返します。
     *
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
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
    public static class _EcomgwpostInfoNames extends PropertyName<EcomgwpostInfo> {

        /**
         * インスタンスを構築します。
         */
        public _EcomgwpostInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _EcomgwpostInfoNames(final String name) {
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
        public _EcomgwpostInfoNames(final PropertyName<?> parent, final String name) {
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
         * nameのプロパティ名を返します。
         *
         * @return nameのプロパティ名
         */
        public PropertyName<String> name() {
            return new PropertyName<String>(this, "name");
        }

        /**
         * groupidのプロパティ名を返します。
         *
         * @return groupidのプロパティ名
         */
        public PropertyName<String> groupid() {
            return new PropertyName<String>(this, "groupid");
        }

        /**
         * partsidのプロパティ名を返します。
         *
         * @return partsidのプロパティ名
         */
        public PropertyName<String> partsid() {
            return new PropertyName<String>(this, "partsid");
        }

        /**
         * blockidのプロパティ名を返します。
         *
         * @return blockidのプロパティ名
         */
        public PropertyName<String> blockid() {
            return new PropertyName<String>(this, "blockid");
        }

        /**
         * posturlのプロパティ名を返します。
         *
         * @return posturlのプロパティ名
         */
        public PropertyName<String> posturl() {
            return new PropertyName<String>(this, "posturl");
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
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
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
