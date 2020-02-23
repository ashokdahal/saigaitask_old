/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.EcomgwpostdefaultInfo;
import jp.ecom_plat.saigaitask.entity.names.EcomgwpostInfoNames._EcomgwpostInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticedefaultInfoNames._NoticedefaultInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link EcomgwpostdefaultInfo}のプロパティ名の集合です。
 *
 */
public class EcomgwpostdefaultInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "e-Com GW post default info";
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
     * ecomgwpostinfoidのプロパティ名を返します。
     *
     * @return ecomgwpostinfoidのプロパティ名
     */
    public static PropertyName<Long> ecomgwpostinfoid() {
        return new PropertyName<Long>("ecomgwpostinfoid");
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
     * ecomgwpostInfoのプロパティ名を返します。
     *
     * @return ecomgwpostInfoのプロパティ名
     */
    public static _EcomgwpostInfoNames ecomgwpostInfo() {
        return new _EcomgwpostInfoNames("ecomgwpostInfo");
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
    public static class _EcomgwpostdefaultInfoNames extends PropertyName<EcomgwpostdefaultInfo> {

        /**
         * インスタンスを構築します。
         */
        public _EcomgwpostdefaultInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _EcomgwpostdefaultInfoNames(final String name) {
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
        public _EcomgwpostdefaultInfoNames(final PropertyName<?> parent, final String name) {
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
         * ecomgwpostinfoidのプロパティ名を返します。
         *
         * @return ecomgwpostinfoidのプロパティ名
         */
        public PropertyName<Long> ecomgwpostinfoid() {
            return new PropertyName<Long>(this, "ecomgwpostinfoid");
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
         * ecomgwpostInfoのプロパティ名を返します。
         *
         * @return ecomgwpostInfoのプロパティ名
         */
        public _EcomgwpostInfoNames ecomgwpostInfo() {
            return new _EcomgwpostInfoNames(this, "ecomgwpostInfo");
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
