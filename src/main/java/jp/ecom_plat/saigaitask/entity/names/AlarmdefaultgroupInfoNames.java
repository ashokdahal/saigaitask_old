/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AlarmdefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.names.GroupInfoNames._GroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultInfoNames._NoticedefaultInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AlarmdefaultgroupInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:30")
public class AlarmdefaultgroupInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Alarm default group info";
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
     * noticedefaultinfoidのプロパティ名を返します。
     * 
     * @return noticedefaultinfoidのプロパティ名
     */
    public static PropertyName<Long> noticedefaultinfoid() {
        return new PropertyName<Long>("noticedefaultinfoid");
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
     * defaultonのプロパティ名を返します。
     * 
     * @return defaultonのプロパティ名
     */
    public static PropertyName<Boolean> defaulton() {
        return new PropertyName<Boolean>("defaulton");
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
     * groupInfoのプロパティ名を返します。
     * 
     * @return groupInfoのプロパティ名
     */
    public static _GroupInfoNames groupInfo() {
        return new _GroupInfoNames("groupInfo");
    }

    /**
     * noticedefaultInfoのプロパティ名を返します。
     * 
     * @return noticedefaultInfoのプロパティ名
     */
    public static _NoticedefaultInfoNames noticedefaultInfo() {
        return new _NoticedefaultInfoNames("noticedefaultInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _AlarmdefaultgroupInfoNames extends PropertyName<AlarmdefaultgroupInfo> {

        /**
         * インスタンスを構築します。
         */
        public _AlarmdefaultgroupInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _AlarmdefaultgroupInfoNames(final String name) {
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
        public _AlarmdefaultgroupInfoNames(final PropertyName<?> parent, final String name) {
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
         * noticedefaultinfoidのプロパティ名を返します。
         *
         * @return noticedefaultinfoidのプロパティ名
         */
        public PropertyName<Long> noticedefaultinfoid() {
            return new PropertyName<Long>(this, "noticedefaultinfoid");
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
         * defaultonのプロパティ名を返します。
         *
         * @return defaultonのプロパティ名
         */
        public PropertyName<Boolean> defaulton() {
            return new PropertyName<Boolean>(this, "defaulton");
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
         * groupInfoのプロパティ名を返します。
         *
         * @return groupInfoのプロパティ名
         */
        public _GroupInfoNames groupInfo() {
            return new _GroupInfoNames(this, "groupInfo");
        }

        /**
         * noticedefaultInfoのプロパティ名を返します。
         *
         * @return noticedefaultInfoのプロパティ名
         */
        public _NoticedefaultInfoNames noticedefaultInfo() {
            return new _NoticedefaultInfoNames(this, "noticedefaultInfo");
        }
    }
}
