/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.names.AlarmmessageInfoNames._AlarmmessageInfoNames;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AlarmmessageData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:30")
public class AlarmmessageDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Alarm message data";
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
     * localgovinfoidのプロパティ名を返します。
     * 
     * @return localgovinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovinfoid() {
        return new PropertyName<Long>("localgovinfoid");
    }

    /**
     * alarmmessageinfoidのプロパティ名を返します。
     * 
     * @return alarmmessageinfoidのプロパティ名
     */
    public static PropertyName<Long> alarmmessageinfoid() {
        return new PropertyName<Long>("alarmmessageinfoid");
    }

    /**
     * sendgroupidのプロパティ名を返します。
     * 
     * @return sendgroupidのプロパティ名
     */
    public static PropertyName<Long> sendgroupid() {
        return new PropertyName<Long>("sendgroupid");
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
     * noticetoのプロパティ名を返します。
     * 
     * @return noticetoのプロパティ名
     */
    public static PropertyName<String> noticeto() {
        return new PropertyName<String>("noticeto");
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
     * noticeurlのプロパティ名を返します。
     * 
     * @return noticeurlのプロパティ名
     */
    public static PropertyName<String> noticeurl() {
        return new PropertyName<String>("noticeurl");
    }

    /**
     * showmessageのプロパティ名を返します。
     * 
     * @return showmessageのプロパティ名
     */
    public static PropertyName<Boolean> showmessage() {
        return new PropertyName<Boolean>("showmessage");
    }

    /**
     * messagetypeのプロパティ名を返します。
     * 
     * @return messagetypeのプロパティ名
     */
    public static PropertyName<String> messagetype() {
        return new PropertyName<String>("messagetype");
    }

    /**
     * durationのプロパティ名を返します。
     * 
     * @return durationのプロパティ名
     */
    public static PropertyName<Integer> duration() {
        return new PropertyName<Integer>("duration");
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
     * sendunitidのプロパティ名を返します。
     * 
     * @return sendunitidのプロパティ名
     */
    public static PropertyName<Long> sendunitid() {
        return new PropertyName<Long>("sendunitid");
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
     * localgovInfoのプロパティ名を返します。
     * 
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * alarmmessageInfoのプロパティ名を返します。
     * 
     * @return alarmmessageInfoのプロパティ名
     */
    public static _AlarmmessageInfoNames alarmmessageInfo() {
        return new _AlarmmessageInfoNames("alarmmessageInfo");
    }

    /**
     * sendgroupInfoのプロパティ名を返します。
     * 
     * @return sendgroupInfoのプロパティ名
     */
    public static _GroupInfoNames sendgroupInfo() {
        return new _GroupInfoNames("sendgroupInfo");
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
    public static class _AlarmmessageDataNames extends PropertyName<AlarmmessageData> {

        /**
         * インスタンスを構築します。
         */
        public _AlarmmessageDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _AlarmmessageDataNames(final String name) {
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
        public _AlarmmessageDataNames(final PropertyName<?> parent, final String name) {
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
         * localgovinfoidのプロパティ名を返します。
         *
         * @return localgovinfoidのプロパティ名
         */
        public PropertyName<Long> localgovinfoid() {
            return new PropertyName<Long>(this, "localgovinfoid");
        }

        /**
         * alarmmessageinfoidのプロパティ名を返します。
         *
         * @return alarmmessageinfoidのプロパティ名
         */
        public PropertyName<Long> alarmmessageinfoid() {
            return new PropertyName<Long>(this, "alarmmessageinfoid");
        }

        /**
         * sendgroupidのプロパティ名を返します。
         *
         * @return sendgroupidのプロパティ名
         */
        public PropertyName<Long> sendgroupid() {
            return new PropertyName<Long>(this, "sendgroupid");
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
         * noticetoのプロパティ名を返します。
         *
         * @return noticetoのプロパティ名
         */
        public PropertyName<String> noticeto() {
            return new PropertyName<String>(this, "noticeto");
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
         * noticeurlのプロパティ名を返します。
         *
         * @return noticeurlのプロパティ名
         */
        public PropertyName<String> noticeurl() {
            return new PropertyName<String>(this, "noticeurl");
        }

        /**
         * showmessageのプロパティ名を返します。
         *
         * @return showmessageのプロパティ名
         */
        public PropertyName<Boolean> showmessage() {
            return new PropertyName<Boolean>(this, "showmessage");
        }

        /**
         * messagetypeのプロパティ名を返します。
         *
         * @return messagetypeのプロパティ名
         */
        public PropertyName<String> messagetype() {
            return new PropertyName<String>(this, "messagetype");
        }

        /**
         * durationのプロパティ名を返します。
         *
         * @return durationのプロパティ名
         */
        public PropertyName<Integer> duration() {
            return new PropertyName<Integer>(this, "duration");
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
         * sendunitidのプロパティ名を返します。
         *
         * @return sendunitidのプロパティ名
         */
        public PropertyName<Long> sendunitid() {
            return new PropertyName<Long>(this, "sendunitid");
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
         * localgovInfoのプロパティ名を返します。
         * 
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * alarmmessageInfoのプロパティ名を返します。
         * 
         * @return alarmmessageInfoのプロパティ名
         */
        public _AlarmmessageInfoNames alarmmessageInfo() {
            return new _AlarmmessageInfoNames(this, "alarmmessageInfo");
        }

        /**
         * sendgroupInfoのプロパティ名を返します。
         * 
         * @return sendgroupInfoのプロパティ名
         */
        public _GroupInfoNames sendgroupInfo() {
            return new _GroupInfoNames(this, "sendgroupInfo");
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