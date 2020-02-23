/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.LoginData;
import jp.ecom_plat.saigaitask.entity.names.DemoInfoNames._DemoInfoNames;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link LoginData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/06/18 20:04:45")
public class LoginDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Login history";
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
     * trackdataidのプロパティ名を返します。
     * 
     * @return trackdataidのプロパティ名
     */
    public static PropertyName<Long> trackdataid() {
        return new PropertyName<Long>("trackdataid");
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
     * demoinfoidのプロパティ名を返します。
     * 
     * @return demoinfoidのプロパティ名
     */
    public static PropertyName<Long> demoinfoid() {
        return new PropertyName<Long>("demoinfoid");
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
     * logintimeのプロパティ名を返します。
     * 
     * @return logintimeのプロパティ名
     */
    public static PropertyName<Timestamp> logintime() {
        return new PropertyName<Timestamp>("logintime");
    }

    /**
     * logouttimeのプロパティ名を返します。
     * 
     * @return logouttimeのプロパティ名
     */
    public static PropertyName<Timestamp> logouttime() {
        return new PropertyName<Timestamp>("logouttime");
    }

    /**
     * trackDataのプロパティ名を返します。
     * 
     * @return trackDataのプロパティ名
     */
    public static _GroupInfoNames trackData() {
        return new _GroupInfoNames("trackData");
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
     * demoInfoのプロパティ名を返します。
     * 
     * @return demoInfoのプロパティ名
     */
    public static _DemoInfoNames demoInfo() {
        return new _DemoInfoNames("demoInfo");
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
     * @author S2JDBC-Gen
     */
    public static class _LoginDataNames extends PropertyName<LoginData> {

        /**
         * インスタンスを構築します。
         */
        public _LoginDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _LoginDataNames(final String name) {
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
        public _LoginDataNames(final PropertyName<?> parent, final String name) {
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
         * trackdataidのプロパティ名を返します。
         *
         * @return trackdataidのプロパティ名
         */
        public PropertyName<Long> trackdataid() {
            return new PropertyName<Long>(this, "trackdataid");
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
         * demoinfoidのプロパティ名を返します。
         *
         * @return demoinfoidのプロパティ名
         */
        public PropertyName<Long> demoinfoid() {
            return new PropertyName<Long>(this, "demoinfoid");
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
         * logintimeのプロパティ名を返します。
         *
         * @return logintimeのプロパティ名
         */
        public PropertyName<Timestamp> logintime() {
            return new PropertyName<Timestamp>(this, "logintime");
        }

        /**
         * logouttimeのプロパティ名を返します。
         *
         * @return logouttimeのプロパティ名
         */
        public PropertyName<Timestamp> logouttime() {
            return new PropertyName<Timestamp>(this, "logouttime");
        }

        /**
         * trackDataのプロパティ名を返します。
         * 
         * @return trackDataのプロパティ名
         */
        public _GroupInfoNames trackData() {
            return new _GroupInfoNames(this, "trackData");
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
         * demoInfoのプロパティ名を返します。
         * 
         * @return demoInfoのプロパティ名
         */
        public _DemoInfoNames demoInfo() {
            return new _DemoInfoNames(this, "demoInfo");
        }

        /**
         * disasterMasterのプロパティ名を返します。
         * 
         * @return disasterMasterのプロパティ名
         */
        /*public _DisasterMasterNames disasterMaster() {
            return new _DisasterMasterNames(this, "disasterMaster");
        }*/
    }
}
