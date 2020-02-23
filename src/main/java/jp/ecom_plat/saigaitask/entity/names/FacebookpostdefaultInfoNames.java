/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.FacebookpostdefaultInfo;
import jp.ecom_plat.saigaitask.entity.names.FacebookpostInfoNames._FacebookpostInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultInfoNames._NoticedefaultInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link FacebookpostdefaultInfo}のプロパティ名の集合です。
 *
 */
public class FacebookpostdefaultInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Facebook post default info";
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
     * facebookpostinfoidのプロパティ名を返します。
     *
     * @return facebookpostinfoidのプロパティ名
     */
    public static PropertyName<Long> facebookpostinfoid() {
        return new PropertyName<Long>("facebookpostinfoid");
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
     * facebookpostInfoのプロパティ名を返します。
     *
     * @return facebookpostInfoのプロパティ名
     */
    public static _FacebookpostInfoNames facebookpostInfo() {
        return new _FacebookpostInfoNames("facebookpostInfo");
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
    public static class _FacebookpostdefaultInfoNames extends PropertyName<FacebookpostdefaultInfo> {

        /**
         * インスタンスを構築します。
         */
        public _FacebookpostdefaultInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _FacebookpostdefaultInfoNames(final String name) {
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
        public _FacebookpostdefaultInfoNames(final PropertyName<?> parent, final String name) {
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
         * facebookpostinfoidのプロパティ名を返します。
         *
         * @return facebookpostinfoidのプロパティ名
         */
        public PropertyName<Long> facebookpostinfoid() {
            return new PropertyName<Long>(this, "facebookpostinfoid");
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
         * facebookpostInfoのプロパティ名を返します。
         *
         * @return facebookpostInfoのプロパティ名
         */
        public _FacebookpostInfoNames facebookpostInfo() {
            return new _FacebookpostInfoNames(this, "facebookpostInfo");
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
