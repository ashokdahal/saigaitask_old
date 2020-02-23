/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.UserInfo;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.UnitInfoNames._UnitInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link UserInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/06/18 20:04:45")
public class UserInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "User info";
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
     * staffnoのプロパティ名を返します。
     * 
     * @return staffnoのプロパティ名
     */
    public static PropertyName<String> staffno() {
        return new PropertyName<String>("staffno");
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
     * dutyのプロパティ名を返します。
     * 
     * @return dutyのプロパティ名
     */
    public static PropertyName<String> duty() {
        return new PropertyName<String>("duty");
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
     * mobilenoのプロパティ名を返します。
     * 
     * @return mobilenoのプロパティ名
     */
    public static PropertyName<String> mobileno() {
        return new PropertyName<String>("mobileno");
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
     * mobilemailのプロパティ名を返します。
     * 
     * @return mobilemailのプロパティ名
     */
    public static PropertyName<String> mobilemail() {
        return new PropertyName<String>("mobilemail");
    }

    /**
     * pushtokenのプロパティ名を返します。
     * 
     * @return pushtokenのプロパティ名
     */
    public static PropertyName<String> pushtoken() {
        return new PropertyName<String>("pushtoken");
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
    public static class _UserInfoNames extends PropertyName<UserInfo> {

        /**
         * インスタンスを構築します。
         */
        public _UserInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _UserInfoNames(final String name) {
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
        public _UserInfoNames(final PropertyName<?> parent, final String name) {
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
         * staffnoのプロパティ名を返します。
         *
         * @return staffnoのプロパティ名
         */
        public PropertyName<String> staffno() {
            return new PropertyName<String>(this, "staffno");
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
         * dutyのプロパティ名を返します。
         *
         * @return dutyのプロパティ名
         */
        public PropertyName<String> duty() {
            return new PropertyName<String>(this, "duty");
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
         * mobilenoのプロパティ名を返します。
         *
         * @return mobilenoのプロパティ名
         */
        public PropertyName<String> mobileno() {
            return new PropertyName<String>(this, "mobileno");
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
         * mobilemailのプロパティ名を返します。
         *
         * @return mobilemailのプロパティ名
         */
        public PropertyName<String> mobilemail() {
            return new PropertyName<String>(this, "mobilemail");
        }

        /**
         * pushtokenのプロパティ名を返します。
         *
         * @return pushtokenのプロパティ名
         */
        public PropertyName<String> pushtoken() {
            return new PropertyName<String>(this, "pushtoken");
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
