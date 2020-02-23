/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.DemoInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteorequestInfoNames._MeteorequestInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link DemoInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/10/16 20:07:37")
public class DemoInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Training info";
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
     * disasteridのプロパティ名を返します。
     * 
     * @return disasteridのプロパティ名
     */
    /*public static PropertyName<Integer> disasterid() {
        return new PropertyName<Integer>("disasterid");
    }*/

    /**
     * triggerurlのプロパティ名を返します。
     * 
     * @return triggerurlのプロパティ名
     */
    public static PropertyName<String> triggerurl() {
        return new PropertyName<String>("triggerurl");
    }

    /**
     * meteorequestinfoidのプロパティ名を返します。
     * 
     * @return meteorequestinfoidのプロパティ名
     */
    public static PropertyName<Long> meteorequestinfoid() {
        return new PropertyName<Long>("meteorequestinfoid");
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
     * disasterMasterのプロパティ名を返します。
     * 
     * @return disasterMasterのプロパティ名
     */
    /*public static _DisasterMasterNames disasterMaster() {
        return new _DisasterMasterNames("disasterMaster");
    }*/

    /**
     * localgovInfoのプロパティ名を返します。
     * 
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * meteorequestInfoのプロパティ名を返します。
     * 
     * @return meteorequestInfoのプロパティ名
     */
    public static _MeteorequestInfoNames meteorequestInfo() {
        return new _MeteorequestInfoNames("meteorequestInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _DemoInfoNames extends PropertyName<DemoInfo> {

        /**
         * インスタンスを構築します。
         */
        public _DemoInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _DemoInfoNames(final String name) {
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
        public _DemoInfoNames(final PropertyName<?> parent, final String name) {
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
         * disasteridのプロパティ名を返します。
         *
         * @return disasteridのプロパティ名
         */
        /*public PropertyName<Integer> disasterid() {
            return new PropertyName<Integer>(this, "disasterid");
        }*/

        /**
         * triggerurlのプロパティ名を返します。
         *
         * @return triggerurlのプロパティ名
         */
        public PropertyName<String> triggerurl() {
            return new PropertyName<String>(this, "triggerurl");
        }

        /**
         * meteorequestinfoidのプロパティ名を返します。
         *
         * @return meteorequestinfoidのプロパティ名
         */
        public PropertyName<Long> meteorequestinfoid() {
            return new PropertyName<Long>(this, "meteorequestinfoid");
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
         * disasterMasterのプロパティ名を返します。
         *
         * @return disasterMasterのプロパティ名
         */
        /*public _DisasterMasterNames disasterMaster() {
            return new _DisasterMasterNames(this, "disasterMaster");
        }*/

        /**
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * meteorequestInfoのプロパティ名を返します。
         *
         * @return meteorequestInfoのプロパティ名
         */
        public _MeteorequestInfoNames meteorequestInfo() {
            return new _MeteorequestInfoNames(this, "meteorequestInfo");
        }
    }
}
