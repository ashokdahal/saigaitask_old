/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.names.AdminbackupDataNames._AdminbackupDataNames;
import jp.ecom_plat.saigaitask.entity.names.AlarmdefaultgroupInfoNames._AlarmdefaultgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AlarmmessageInfoNames._AlarmmessageInfoNames;
import jp.ecom_plat.saigaitask.entity.names.AutocompleteInfoNames._AutocompleteInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuloginInfoNames._MenuloginInfoNames;
import jp.ecom_plat.saigaitask.entity.names.UserInfoNames._UserInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link GroupInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/04 11:53:15")
public class GroupInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Group info<!--2-->";
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
     * ecompassのプロパティ名を返します。
     *
     * @return ecompassのプロパティ名
     */
    public static PropertyName<String> ecompass() {
        return new PropertyName<String>("ecompass");
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
     * headofficeのプロパティ名を返します。
     *
     * @return headofficeのプロパティ名
     */
    public static PropertyName<Boolean> headoffice() {
        return new PropertyName<Boolean>("headoffice");
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
     * namekanaのプロパティ名を返します。
     *
     * @return namekanaのプロパティ名
     */
    public static PropertyName<String> namekana() {
        return new PropertyName<String>("namekana");
    }

    /**
     * phoneのプロパティ名を返します。
     *
     * @return phoneのプロパティ名
     */
    public static PropertyName<String> phone() {
        return new PropertyName<String>("phone");
    }

    /**
     * faxのプロパティ名を返します。
     *
     * @return faxのプロパティ名
     */
    public static PropertyName<String> fax() {
        return new PropertyName<String>("fax");
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
     * addressのプロパティ名を返します。
     *
     * @return addressのプロパティ名
     */
    public static PropertyName<String> address() {
        return new PropertyName<String>("address");
    }

    /**
     * domainのプロパティ名を返します。
     *
     * @return domainのプロパティ名
     */
    public static PropertyName<String> domain() {
        return new PropertyName<String>("domain");
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
     * unitInfosのプロパティ名を返します。
     *
     * @return unitInfosのプロパティ名
     */
    /*public static _UnitInfoNames unitInfos() {
        return new _UnitInfoNames("unitInfos");
    }*/

    /**
     * userInfosのプロパティ名を返します。
     *
     * @return userInfosのプロパティ名
     */
    public static _UserInfoNames userInfos() {
        return new _UserInfoNames("userInfos");
    }
    
    /**
     * menuloginInfosのプロパティ名を返します。
     *
     * @return menuloginInfosのプロパティ名
     */
    public static _MenuloginInfoNames menuloginInfos() {
        return new _MenuloginInfoNames("menuloginInfos");
    }

    /**
     * alarmmessageInfoListのプロパティ名を返します。
     *
     * @return alarmmessageInfoListのプロパティ名
     */
    public static _AlarmmessageInfoNames alarmmessageInfoList() {
        return new _AlarmmessageInfoNames("alarmmessageInfoList");
    }

    /**
     * alarmdefaultgroupInfoListのプロパティ名を返します。
     *
     * @return alarmdefaultgroupInfoListのプロパティ名
     */
    public static _AlarmdefaultgroupInfoNames alarmdefaultgroupInfoList() {
        return new _AlarmdefaultgroupInfoNames("alarmdefaultgroupInfoList");
    }

    /**
     * autocompleteInfoListのプロパティ名を返します。
     *
     * @return autocompleteInfoListのプロパティ名
     */
    public static _AutocompleteInfoNames autocompleteInfoList() {
        return new _AutocompleteInfoNames("autocompleteInfoList");
    }

    /**
     * adminbackupDataListのプロパティ名を返します。
     *
     * @return adminbackupDataListのプロパティ名
     */
    public static _AdminbackupDataNames adminbackupDataList() {
        return new _AdminbackupDataNames("adminbackupDataList");
    }

    /**
     * localgovInfosのプロパティ名を返します。
     *
     * @return localgovInfosのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfos() {
        return new _LocalgovInfoNames("localgovInfos");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _GroupInfoNames extends PropertyName<GroupInfo> {

        /**
         * インスタンスを構築します。
         */
        public _GroupInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _GroupInfoNames(final String name) {
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
        public _GroupInfoNames(final PropertyName<?> parent, final String name) {
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
         * ecompassのプロパティ名を返します。
         *
         * @return ecompassのプロパティ名
         */
        public PropertyName<String> ecompass() {
            return new PropertyName<String>(this, "ecompass");
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
         * headofficeのプロパティ名を返します。
         *
         * @return headofficeのプロパティ名
         */
        public PropertyName<Boolean> headoffice() {
            return new PropertyName<Boolean>(this, "headoffice");
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
         * namekanaのプロパティ名を返します。
         *
         * @return namekanaのプロパティ名
         */
        public PropertyName<String> namekana() {
            return new PropertyName<String>(this, "namekana");
        }

        /**
         * phoneのプロパティ名を返します。
         *
         * @return phoneのプロパティ名
         */
        public PropertyName<String> phone() {
            return new PropertyName<String>(this, "phone");
        }

        /**
         * faxのプロパティ名を返します。
         *
         * @return faxのプロパティ名
         */
        public PropertyName<String> fax() {
            return new PropertyName<String>(this, "fax");
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
         * addressのプロパティ名を返します。
         *
         * @return addressのプロパティ名
         */
        public PropertyName<String> address() {
            return new PropertyName<String>(this, "address");
        }

        /**
         * domainのプロパティ名を返します。
         *
         * @return domainのプロパティ名
         */
        public PropertyName<String> domain() {
            return new PropertyName<String>(this, "domain");
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
         * unitInfosのプロパティ名を返します。
         *
         * @return unitInfosのプロパティ名
         */
        /*public _UnitInfoNames unitInfos() {
            return new _UnitInfoNames(this, "unitInfos");
        }*/

        /**
         * userInfosのプロパティ名を返します。
         *
         * @return userInfosのプロパティ名
         */
        public _UserInfoNames userInfos() {
            return new _UserInfoNames(this, "userInfos");
        }

        /**
         * menuloginInfosのプロパティ名を返します。
         *
         * @return menuloginInfosのプロパティ名
         */
        public _MenuloginInfoNames menuloginInfos() {
            return new _MenuloginInfoNames(this, "menuloginInfos");
        }

        /**
         * alarmmessageInfoListのプロパティ名を返します。
         *
         * @return alarmmessageInfoListのプロパティ名
         */
        public _AlarmmessageInfoNames alarmmessageInfoList() {
            return new _AlarmmessageInfoNames(this, "alarmmessageInfoList");
        }

        /**
         * alarmdefaultgroupInfoListのプロパティ名を返します。
         *
         * @return alarmdefaultgroupInfoListのプロパティ名
         */
        public _AlarmdefaultgroupInfoNames alarmdefaultgroupInfoList() {
            return new _AlarmdefaultgroupInfoNames(this, "alarmdefaultgroupInfoList");
        }

        /**
         * autocompleteInfoListのプロパティ名を返します。
         *
         * @return autocompleteInfoListのプロパティ名
         */
        public _AutocompleteInfoNames autocompleteInfoList() {
            return new _AutocompleteInfoNames(this, "autocompleteInfoList");
        }

        /**
         * adminbackupDataListのプロパティ名を返します。
         *
         * @return adminbackupDataListのプロパティ名
         */
        public _AdminbackupDataNames adminbackupDataList() {
            return new _AdminbackupDataNames(this, "adminbackupDataList");
        }

        /**
         * localgovInfosのプロパティ名を返します。
         *
         * @return localgovInfosのプロパティ名
         */
        public _LocalgovInfoNames localgovInfos() {
            return new _LocalgovInfoNames(this, "localgovInfos");
        }
    }
}
