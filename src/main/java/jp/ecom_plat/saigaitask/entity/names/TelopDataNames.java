/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TelopData;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TeloptypeMasterNames._TeloptypeMasterNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TelopData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:31")
public class TelopDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Telop data";
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
     * teloptypeidのプロパティ名を返します。
     * 
     * @return teloptypeidのプロパティ名
     */
    public static PropertyName<Integer> teloptypeid() {
        return new PropertyName<Integer>("teloptypeid");
    }

    /**
     * messageのプロパティ名を返します。
     * 
     * @return messageのプロパティ名
     */
    public static PropertyName<String> message() {
        return new PropertyName<String>("message");
    }

    /**
     * registtimeのプロパティ名を返します。
     * 
     * @return registtimeのプロパティ名
     */
    public static PropertyName<Timestamp> registtime() {
        return new PropertyName<Timestamp>("registtime");
    }

    /**
     * viewlimitのプロパティ名を返します。
     * 
     * @return viewlimitのプロパティ名
     */
    public static PropertyName<Timestamp> viewlimit() {
        return new PropertyName<Timestamp>("viewlimit");
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
     * teloptypeMasterのプロパティ名を返します。
     * 
     * @return teloptypeMasterのプロパティ名
     */
    public static _TeloptypeMasterNames teloptypeMaster() {
        return new _TeloptypeMasterNames("teloptypeMaster");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TelopDataNames extends PropertyName<TelopData> {

        /**
         * インスタンスを構築します。
         */
        public _TelopDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TelopDataNames(final String name) {
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
        public _TelopDataNames(final PropertyName<?> parent, final String name) {
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
         * teloptypeidのプロパティ名を返します。
         *
         * @return teloptypeidのプロパティ名
         */
        public PropertyName<Integer> teloptypeid() {
            return new PropertyName<Integer>(this, "teloptypeid");
        }

        /**
         * messageのプロパティ名を返します。
         *
         * @return messageのプロパティ名
         */
        public PropertyName<String> message() {
            return new PropertyName<String>(this, "message");
        }

        /**
         * registtimeのプロパティ名を返します。
         *
         * @return registtimeのプロパティ名
         */
        public PropertyName<Timestamp> registtime() {
            return new PropertyName<Timestamp>(this, "registtime");
        }

        /**
         * viewlimitのプロパティ名を返します。
         *
         * @return viewlimitのプロパティ名
         */
        public PropertyName<Timestamp> viewlimit() {
            return new PropertyName<Timestamp>(this, "viewlimit");
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
         * teloptypeMasterのプロパティ名を返します。
         * 
         * @return teloptypeMasterのプロパティ名
         */
        public _TeloptypeMasterNames teloptypeMaster() {
            return new _TeloptypeMasterNames(this, "teloptypeMaster");
        }
    }
}
