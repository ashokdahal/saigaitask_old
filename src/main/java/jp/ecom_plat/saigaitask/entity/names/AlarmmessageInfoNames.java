/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageInfo;
import jp.ecom_plat.saigaitask.entity.names.AlarmtypeMasterNames._AlarmtypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AlarmmessageInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:30")
public class AlarmmessageInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Alarm message settings info";
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
     * alarmtypeidのプロパティ名を返します。
     * 
     * @return alarmtypeidのプロパティ名
     */
    public static PropertyName<Integer> alarmtypeid() {
        return new PropertyName<Integer>("alarmtypeid");
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
     * messageのプロパティ名を返します。
     * 
     * @return messageのプロパティ名
     */
    public static PropertyName<String> message() {
        return new PropertyName<String>("message");
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
     * alarmtypeMasterのプロパティ名を返します。
     * 
     * @return alarmtypeMasterのプロパティ名
     */
    public static _AlarmtypeMasterNames alarmtypeMaster() {
        return new _AlarmtypeMasterNames("alarmtypeMaster");
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
     * localgovInfoのプロパティ名を返します。
     * 
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _AlarmmessageInfoNames extends PropertyName<AlarmmessageInfo> {

        /**
         * インスタンスを構築します。
         */
        public _AlarmmessageInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _AlarmmessageInfoNames(final String name) {
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
        public _AlarmmessageInfoNames(final PropertyName<?> parent, final String name) {
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
         * alarmtypeidのプロパティ名を返します。
         *
         * @return alarmtypeidのプロパティ名
         */
        public PropertyName<Integer> alarmtypeid() {
            return new PropertyName<Integer>(this, "alarmtypeid");
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
         * messageのプロパティ名を返します。
         *
         * @return messageのプロパティ名
         */
        public PropertyName<String> message() {
            return new PropertyName<String>(this, "message");
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
         * alarmtypeMasterのプロパティ名を返します。
         *
         * @return alarmtypeMasterのプロパティ名
         */
        public _AlarmtypeMasterNames alarmtypeMaster() {
            return new _AlarmtypeMasterNames(this, "alarmtypeMaster");
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
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }
    }
}
