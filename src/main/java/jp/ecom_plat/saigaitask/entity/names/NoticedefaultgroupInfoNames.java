/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.NoticedefaultgroupInfo;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultInfoNames._NoticedefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link NoticedefaultgroupInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:30")
public class NoticedefaultgroupInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Notification default group info";
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
     * noticegroupinfoidのプロパティ名を返します。
     * 
     * @return noticegroupinfoidのプロパティ名
     */
    public static PropertyName<Long> noticegroupinfoid() {
        return new PropertyName<Long>("noticegroupinfoid");
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
     * noticegroupInfoのプロパティ名を返します。
     * 
     * @return noticegroupInfoのプロパティ名
     */
    public static _NoticegroupInfoNames noticegroupInfo() {
        return new _NoticegroupInfoNames("noticegroupInfo");
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
    public static class _NoticedefaultgroupInfoNames extends PropertyName<NoticedefaultgroupInfo> {

        /**
         * インスタンスを構築します。
         */
        public _NoticedefaultgroupInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _NoticedefaultgroupInfoNames(final String name) {
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
        public _NoticedefaultgroupInfoNames(final PropertyName<?> parent, final String name) {
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
         * noticegroupinfoidのプロパティ名を返します。
         *
         * @return noticegroupinfoidのプロパティ名
         */
        public PropertyName<Long> noticegroupinfoid() {
            return new PropertyName<Long>(this, "noticegroupinfoid");
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
         * noticegroupInfoのプロパティ名を返します。
         *
         * @return noticegroupInfoのプロパティ名
         */
        public _NoticegroupInfoNames noticegroupInfo() {
            return new _NoticegroupInfoNames(this, "noticegroupInfo");
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
