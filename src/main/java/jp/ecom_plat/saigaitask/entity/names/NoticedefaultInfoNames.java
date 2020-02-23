/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultInfo;
import jp.ecom_plat.saigaitask.entity.names.AlarmdefaultgroupInfoNames._AlarmdefaultgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultgroupInfoNames._NoticedefaultgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticetemplatetypeMasterNames._NoticetemplatetypeMasterNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link NoticedefaultInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:31")
public class NoticedefaultInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Notification default info";
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
     * menuinfoidのプロパティ名を返します。
     * 
     * @return menuinfoidのプロパティ名
     */
    public static PropertyName<Long> menuinfoid() {
        return new PropertyName<Long>("menuinfoid");
    }

    /**
     * noticetemplatetypeidのプロパティ名を返します。
     * 
     * @return noticetemplatetypeidのプロパティ名
     */
    public static PropertyName<Integer> noticetemplatetypeid() {
        return new PropertyName<Integer>("noticetemplatetypeid");
    }

    /**
     * templateclassのプロパティ名を返します。
     * 
     * @return templateclassのプロパティ名
     */
    public static PropertyName<String> templateclass() {
        return new PropertyName<String>("templateclass");
    }

    /**
     * alarmdefaultgroupInfoListのプロパティ名を返します。
     * 
     * @return alarmdefaultgroupInfoListのプロパティ名
     */
    public static _AlarmdefaultgroupInfoNames alarmdefaultgroupInfoList() {
        return new _AlarmdefaultgroupInfoNames("alarmdefaultgroupInfoList");
    }

    /**
     * menuInfoのプロパティ名を返します。
     * 
     * @return menuInfoのプロパティ名
     */
    public static _MenuInfoNames menuInfo() {
        return new _MenuInfoNames("menuInfo");
    }

    /**
     * noticedefaultgroupInfoListのプロパティ名を返します。
     * 
     * @return noticedefaultgroupInfoListのプロパティ名
     */
    public static _NoticedefaultgroupInfoNames noticedefaultgroupInfoList() {
        return new _NoticedefaultgroupInfoNames("noticedefaultgroupInfoList");
    }

    /**
     * noticetemplatetypeMasterのプロパティ名を返します。
     * 
     * @return noticetemplatetypeMasterのプロパティ名
     */
    public static _NoticetemplatetypeMasterNames noticetemplatetypeMaster() {
        return new _NoticetemplatetypeMasterNames("noticetemplatetypeMaster");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _NoticedefaultInfoNames extends PropertyName<NoticedefaultInfo> {

        /**
         * インスタンスを構築します。
         */
        public _NoticedefaultInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _NoticedefaultInfoNames(final String name) {
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
        public _NoticedefaultInfoNames(final PropertyName<?> parent, final String name) {
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
         * menuinfoidのプロパティ名を返します。
         *
         * @return menuinfoidのプロパティ名
         */
        public PropertyName<Long> menuinfoid() {
            return new PropertyName<Long>(this, "menuinfoid");
        }

        /**
         * noticetemplatetypeidのプロパティ名を返します。
         *
         * @return noticetemplatetypeidのプロパティ名
         */
        public PropertyName<Integer> noticetemplatetypeid() {
            return new PropertyName<Integer>(this, "noticetemplatetypeid");
        }

        /**
         * templateclassのプロパティ名を返します。
         *
         * @return templateclassのプロパティ名
         */
        public PropertyName<String> templateclass() {
            return new PropertyName<String>(this, "templateclass");
        }

        /**
         * alarmdefaultgroupInfoListのプロパティ名を返します。
         * 
         * @return alarmdefaultgroupInfoListのプロパティ名
         */
        public _AlarmdefaultgroupInfoNames alarmdefaultgroupInfoList() {
            return new _AlarmdefaultgroupInfoNames(this, "alarmdefaultgroupInfoList");
        }

        /**
         * menuInfoのプロパティ名を返します。
         * 
         * @return menuInfoのプロパティ名
         */
        public _MenuInfoNames menuInfo() {
            return new _MenuInfoNames(this, "menuInfo");
        }

        /**
         * noticedefaultgroupInfoListのプロパティ名を返します。
         * 
         * @return noticedefaultgroupInfoListのプロパティ名
         */
        public _NoticedefaultgroupInfoNames noticedefaultgroupInfoList() {
            return new _NoticedefaultgroupInfoNames(this, "noticedefaultgroupInfoList");
        }

        /**
         * noticetemplatetypeMasterのプロパティ名を返します。
         * 
         * @return noticetemplatetypeMasterのプロパティ名
         */
        public _NoticetemplatetypeMasterNames noticetemplatetypeMaster() {
            return new _NoticetemplatetypeMasterNames(this, "noticetemplatetypeMaster");
        }
    }
}