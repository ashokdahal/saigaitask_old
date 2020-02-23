/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.names.DisasterMasterNames._DisasterMasterNames;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuprocessInfoNames._MenuprocessInfoNames;
import jp.ecom_plat.saigaitask.entity.names.UnitInfoNames._UnitInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MenuloginInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/16 11:27:22")
public class MenuloginInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Menu conf info";
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
     * disasteridのプロパティ名を返します。
     * 
     * @return disasteridのプロパティ名
     */
    public static PropertyName<Integer> disasterid() {
        return new PropertyName<Integer>("disasterid");
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
     * disasterMasterのプロパティ名を返します。
     * 
     * @return disasterMasterのプロパティ名
     */
    public static _DisasterMasterNames disasterMaster() {
        return new _DisasterMasterNames("disasterMaster");
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
     * menuprocessInfoListのプロパティ名を返します。
     * 
     * @return menuprocessInfoListのプロパティ名
     */
    public static _MenuprocessInfoNames menuprocessInfoList() {
        return new _MenuprocessInfoNames("menuprocessInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MenuloginInfoNames extends PropertyName<MenuloginInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MenuloginInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MenuloginInfoNames(final String name) {
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
        public _MenuloginInfoNames(final PropertyName<?> parent, final String name) {
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
         * disasteridのプロパティ名を返します。
         *
         * @return disasteridのプロパティ名
         */
        public PropertyName<Integer> disasterid() {
            return new PropertyName<Integer>(this, "disasterid");
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
         * disasterMasterのプロパティ名を返します。
         * 
         * @return disasterMasterのプロパティ名
         */
        public _DisasterMasterNames disasterMaster() {
            return new _DisasterMasterNames(this, "disasterMaster");
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

        /**
         * menuprocessInfoListのプロパティ名を返します。
         * 
         * @return menuprocessInfoListのプロパティ名
         */
        public _MenuprocessInfoNames menuprocessInfoList() {
            return new _MenuprocessInfoNames(this, "menuprocessInfoList");
        }
    }
}