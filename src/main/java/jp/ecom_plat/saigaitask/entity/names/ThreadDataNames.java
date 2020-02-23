/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ThreadData;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ThreadresponseDataNames._ThreadresponseDataNames;
import jp.ecom_plat.saigaitask.entity.names.ThreadsendtoDataNames._ThreadsendtoDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ThreadData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/08/06 21:19:51")
public class ThreadDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Bulletin board thread data";
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
     * titleのプロパティ名を返します。
     *
     * @return titleのプロパティ名
     */
    public static PropertyName<String> title() {
        return new PropertyName<String>("title");
    }

    /**
     * priorityのプロパティ名を返します。
     *
     * @return priorityのプロパティ名
     */
    public static PropertyName<Integer> priority() {
        return new PropertyName<Integer>("priority");
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
     * closetimeのプロパティ名を返します。
     *
     * @return closetimeのプロパティ名
     */
    public static PropertyName<Timestamp> closetime() {
        return new PropertyName<Timestamp>("closetime");
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
     * unitidのプロパティ名を返します。
     * 
     * @return unitidのプロパティ名
     */
    public static PropertyName<Long> unitid() {
        return new PropertyName<Long>("unitid");
    }

    /**
     * trackDataのプロパティ名を返します。
     *
     * @return trackDataのプロパティ名
     */
    public static _TrackDataNames trackData() {
        return new _TrackDataNames("trackData");
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
     * threadresponseDataListのプロパティ名を返します。
     *
     * @return threadresponseDataListのプロパティ名
     */
    public static _ThreadresponseDataNames threadresponseDataList() {
        return new _ThreadresponseDataNames("threadresponseDataList");
    }

    /**
     * threadsendtoDataListのプロパティ名を返します。
     *
     * @return threadsendtoDataListのプロパティ名
     */
    public static _ThreadsendtoDataNames threadsendtoDataList() {
        return new _ThreadsendtoDataNames("threadsendtoDataList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _ThreadDataNames extends PropertyName<ThreadData> {

        /**
         * インスタンスを構築します。
         */
        public _ThreadDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _ThreadDataNames(final String name) {
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
        public _ThreadDataNames(final PropertyName<?> parent, final String name) {
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
         * titleのプロパティ名を返します。
         *
         * @return titleのプロパティ名
         */
        public PropertyName<String> title() {
            return new PropertyName<String>(this, "title");
        }

        /**
         * priorityのプロパティ名を返します。
         *
         * @return priorityのプロパティ名
         */
        public PropertyName<Integer> priority() {
            return new PropertyName<Integer>(this, "priority");
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
         * closetimeのプロパティ名を返します。
         *
         * @return closetimeのプロパティ名
         */
        public PropertyName<Timestamp> closetime() {
            return new PropertyName<Timestamp>(this, "closetime");
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
         * unitidのプロパティ名を返します。
         *
         * @return unitidのプロパティ名
         */
        public PropertyName<Long> unitid() {
            return new PropertyName<Long>(this, "unitid");
        }

        /**
         * trackDataのプロパティ名を返します。
         * 
         * @return trackDataのプロパティ名
         */
        public _TrackDataNames trackData() {
            return new _TrackDataNames(this, "trackData");
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
         * threadresponseDataListのプロパティ名を返します。
         *
         * @return threadresponseDataListのプロパティ名
         */
        public _ThreadresponseDataNames threadresponseDataList() {
            return new _ThreadresponseDataNames(this, "threadresponseDataList");
        }

        /**
         * threadsendtoDataListのプロパティ名を返します。
         *
         * @return threadsendtoDataListのプロパティ名
         */
        public _ThreadsendtoDataNames threadsendtoDataList() {
            return new _ThreadsendtoDataNames(this, "threadsendtoDataList");
        }
    }
}
