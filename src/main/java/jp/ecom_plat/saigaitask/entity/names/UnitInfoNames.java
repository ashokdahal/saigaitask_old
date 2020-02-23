/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link UnitInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/06/18 20:04:45")
public class UnitInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Unit info";
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
     * groupidのプロパティ名を返します。
     * 
     * @return groupidのプロパティ名
     */
    public static PropertyName<Long> groupid() {
        return new PropertyName<Long>("groupid");
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
     * passwordのプロパティ名を返します。
     *
     * @return passwordのプロパティ名
     */
    public static PropertyName<String> password() {
        return new PropertyName<String>("password");
    }

    /**
     * ecomuserのプロパティ名を返します。
     *
     * @return ecomuserのプロパティ名
     */
    public static PropertyName<String> ecomuser() {
        return new PropertyName<String>("ecomuser");
    }

    /**
     * adminのプロパティ名を返します。
     *
     * @return adminのプロパティ名
     */
    public static PropertyName<Boolean> admin() {
        return new PropertyName<Boolean>("admin");
    }

    /**
     * extentのプロパティ名を返します。
     *
     * @return extentのプロパティ名
     */
    public static PropertyName<String> extent() {
        return new PropertyName<String>("extent");
    }

    /**
     * resolutionのプロパティ名を返します。
     *
     * @return resolutionのプロパティ名
     */
    public static PropertyName<Double> resolution() {
        return new PropertyName<Double>("resolution");
    }

    /**
     * telnoのプロパティ名を返します。
     * 
     * @return telnoのプロパティ名
     */
    public static PropertyName<String> telno() {
        return new PropertyName<String>("telno");
    }

    /**
     * faxnoのプロパティ名を返します。
     * 
     * @return faxnoのプロパティ名
     */
    public static PropertyName<String> faxno() {
        return new PropertyName<String>("faxno");
    }

    /**
     * emailのプロパティ名を返します。
     * 
     * @return emailのプロパティ名
     */
    public static PropertyName<String> email() {
        return new PropertyName<String>("email");
    }

    /**
     * apikeyのプロパティ名を返します。
     *
     * @return apikeyのプロパティ名
     */
    public static PropertyName<String> apikey() {
        return new PropertyName<String>("apikey");
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
     * deletedのプロパティ名を返します。
     * 
     * @return deletedのプロパティ名
     */
    public static PropertyName<Boolean> deleted() {
        return new PropertyName<Boolean>("deleted");
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
     * groupInfoのプロパティ名を返します。
     * 
     * @return groupInfoのプロパティ名
     */
    /*public static _GroupInfoNames groupInfo() {
        return new _GroupInfoNames("groupInfo");
    }*/

    /**
     * @author S2JDBC-Gen
     */
    public static class _UnitInfoNames extends PropertyName<UnitInfo> {

        /**
         * インスタンスを構築します。
         */
        public _UnitInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _UnitInfoNames(final String name) {
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
        public _UnitInfoNames(final PropertyName<?> parent, final String name) {
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
         * groupidのプロパティ名を返します。
         *
         * @return groupidのプロパティ名
         */
        public PropertyName<Long> groupid() {
            return new PropertyName<Long>(this, "groupid");
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
         * passwordのプロパティ名を返します。
         *
         * @return passwordのプロパティ名
         */
        public PropertyName<String> password() {
            return new PropertyName<String>(this, "password");
        }

        /**
         * ecomuserのプロパティ名を返します。
         *
         * @return ecomuserのプロパティ名
         */
        public PropertyName<String> ecomuser() {
            return new PropertyName<String>(this, "ecomuser");
        }

        /**
         * adminのプロパティ名を返します。
         *
         * @return adminのプロパティ名
         */
        public PropertyName<Boolean> admin() {
            return new PropertyName<Boolean>(this, "admin");
        }

        /**
         * extentのプロパティ名を返します。
         *
         * @return extentのプロパティ名
         */
        public PropertyName<String> extent() {
            return new PropertyName<String>(this, "extent");
        }

        /**
         * resolutionのプロパティ名を返します。
         *
         * @return resolutionのプロパティ名
         */
        public PropertyName<Double> resolution() {
            return new PropertyName<Double>(this, "resolution");
        }

        /**
         * telnoのプロパティ名を返します。
         *
         * @return telnoのプロパティ名
         */
        public PropertyName<String> telno() {
            return new PropertyName<String>(this, "telno");
        }

        /**
         * faxnoのプロパティ名を返します。
         *
         * @return faxnoのプロパティ名
         */
        public PropertyName<String> faxno() {
            return new PropertyName<String>(this, "faxno");
        }

        /**
         * emailのプロパティ名を返します。
         *
         * @return emailのプロパティ名
         */
        public PropertyName<String> email() {
            return new PropertyName<String>(this, "email");
        }

        /**
         * apikeyのプロパティ名を返します。
         *
         * @return apikeyのプロパティ名
         */
        public PropertyName<String> apikey() {
            return new PropertyName<String>("apikey");
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
         * deletedのプロパティ名を返します。
         *
         * @return deletedのプロパティ名
         */
        public PropertyName<Boolean> deleted() {
            return new PropertyName<Boolean>(this, "deleted");
        }

        /**
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * groupInfoのプロパティ名を返します。
         * 
         * @return groupInfoのプロパティ名
         */
        /*public _GroupInfoNames groupInfo() {
            return new _GroupInfoNames(this, "groupInfo");
        }*/
    }
}
