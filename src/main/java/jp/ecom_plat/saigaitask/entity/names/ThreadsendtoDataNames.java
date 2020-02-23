/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ThreadsendtoData;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ThreadDataNames._ThreadDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ThreadsendtoData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/08/05 21:08:27")
public class ThreadsendtoDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Bulletin board thread send-to data";
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
     * threaddataidのプロパティ名を返します。
     * 
     * @return threaddataidのプロパティ名
     */
    public static PropertyName<Long> threaddataid() {
        return new PropertyName<Long>("threaddataid");
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
     * updatetimeのプロパティ名を返します。
     * 
     * @return updatetimeのプロパティ名
     */
    public static PropertyName<Timestamp> updatetime() {
        return new PropertyName<Timestamp>("updatetime");
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
     * threadDataのプロパティ名を返します。
     * 
     * @return threadDataのプロパティ名
     */
    public static _ThreadDataNames threadData() {
        return new _ThreadDataNames("threadData");
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
     * @author S2JDBC-Gen
     */
    public static class _ThreadsendtoDataNames extends PropertyName<ThreadsendtoData> {

        /**
         * インスタンスを構築します。
         */
        public _ThreadsendtoDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _ThreadsendtoDataNames(final String name) {
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
        public _ThreadsendtoDataNames(final PropertyName<?> parent, final String name) {
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
         * threaddataidのプロパティ名を返します。
         *
         * @return threaddataidのプロパティ名
         */
        public PropertyName<Long> threaddataid() {
            return new PropertyName<Long>(this, "threaddataid");
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
         * updatetimeのプロパティ名を返します。
         *
         * @return updatetimeのプロパティ名
         */
        public PropertyName<Timestamp> updatetime() {
            return new PropertyName<Timestamp>(this, "updatetime");
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
         * threadDataのプロパティ名を返します。
         * 
         * @return threadDataのプロパティ名
         */
        public _ThreadDataNames threadData() {
            return new _ThreadDataNames(this, "threadData");
        }

        /**
         * groupInfoのプロパティ名を返します。
         * 
         * @return groupInfoのプロパティ名
         */
        public _GroupInfoNames groupInfo() {
            return new _GroupInfoNames(this, "groupInfo");
        }
    }
}
