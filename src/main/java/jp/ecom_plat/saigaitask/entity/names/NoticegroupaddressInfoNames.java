/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.NoticegroupaddressInfo;
import jp.ecom_plat.saigaitask.entity.names.NoticeaddressInfoNames._NoticeaddressInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link NoticegroupaddressInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:31")
public class NoticegroupaddressInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Notification group address info";
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
     * noticegroupinfoidのプロパティ名を返します。
     * 
     * @return noticegroupinfoidのプロパティ名
     */
    public static PropertyName<Long> noticegroupinfoid() {
        return new PropertyName<Long>("noticegroupinfoid");
    }

    /**
     * noticeaddressinfoidのプロパティ名を返します。
     * 
     * @return noticeaddressinfoidのプロパティ名
     */
    public static PropertyName<Long> noticeaddressinfoid() {
        return new PropertyName<Long>("noticeaddressinfoid");
    }

    /**
     * noticeaddressInfoのプロパティ名を返します。
     * 
     * @return noticeaddressInfoのプロパティ名
     */
    public static _NoticeaddressInfoNames noticeaddressInfo() {
        return new _NoticeaddressInfoNames("noticeaddressInfo");
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
     * @author S2JDBC-Gen
     */
    public static class _NoticegroupaddressInfoNames extends PropertyName<NoticegroupaddressInfo> {

        /**
         * インスタンスを構築します。
         */
        public _NoticegroupaddressInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _NoticegroupaddressInfoNames(final String name) {
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
        public _NoticegroupaddressInfoNames(final PropertyName<?> parent, final String name) {
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
         * noticegroupinfoidのプロパティ名を返します。
         *
         * @return noticegroupinfoidのプロパティ名
         */
        public PropertyName<Long> noticegroupinfoid() {
            return new PropertyName<Long>(this, "noticegroupinfoid");
        }

        /**
         * noticeaddressinfoidのプロパティ名を返します。
         *
         * @return noticeaddressinfoidのプロパティ名
         */
        public PropertyName<Long> noticeaddressinfoid() {
            return new PropertyName<Long>(this, "noticeaddressinfoid");
        }

        /**
         * noticeaddressInfoのプロパティ名を返します。
         * 
         * @return noticeaddressInfoのプロパティ名
         */
        public _NoticeaddressInfoNames noticeaddressInfo() {
            return new _NoticeaddressInfoNames(this, "noticeaddressInfo");
        }

        /**
         * noticegroupInfoのプロパティ名を返します。
         * 
         * @return noticegroupInfoのプロパティ名
         */
        public _NoticegroupInfoNames noticegroupInfo() {
            return new _NoticegroupInfoNames(this, "noticegroupInfo");
        }
    }
}
