/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.FacebookpostInfo;
import jp.ecom_plat.saigaitask.entity.names.FacebookpostdefaultInfoNames._FacebookpostdefaultInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link FacebookpostInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:31")
public class FacebookpostInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Facebook posting info";
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
     * pageidのプロパティ名を返します。
     *
     * @return pageidのプロパティ名
     */
    public static PropertyName<String> pageid() {
        return new PropertyName<String>("pageid");
    }

    /**
     * pagetypeのプロパティ名を返します。
     *
     * @return pagetypeのプロパティ名
     */
    public static PropertyName<Integer> pagetype() {
        return new PropertyName<Integer>("pagetype");
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
     * noteのプロパティ名を返します。
     *
     * @return noteのプロパティ名
     */
    public static PropertyName<String> note() {
        return new PropertyName<String>("note");
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
     * localgovInfoのプロパティ名を返します。
     *
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * facebookpostdefaultInfoListのプロパティ名を返します。
     *
     * @return facebookpostdefaultInfoListのプロパティ名
     */
    public static _FacebookpostdefaultInfoNames facebookpostdefaultInfoList() {
        return new _FacebookpostdefaultInfoNames("facebookpostdefaultInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _FacebookpostInfoNames extends PropertyName<FacebookpostInfo> {

        /**
         * インスタンスを構築します。
         */
        public _FacebookpostInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _FacebookpostInfoNames(final String name) {
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
        public _FacebookpostInfoNames(final PropertyName<?> parent, final String name) {
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
         * pageidのプロパティ名を返します。
         *
         * @return pageidのプロパティ名
         */
        public PropertyName<String> pageid() {
            return new PropertyName<String>(this, "pageid");
        }

        /**
         * pagetypeのプロパティ名を返します。
         *
         * @return pagetypeのプロパティ名
         */
        public PropertyName<Integer> pagetype() {
            return new PropertyName<Integer>(this, "pagetype");
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
         * noteのプロパティ名を返します。
         *
         * @return noteのプロパティ名
         */
        public PropertyName<String> note() {
            return new PropertyName<String>(this, "note");
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
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * facebookpostdefaultInfoListのプロパティ名を返します。
         *
         * @return facebookpostdefaultInfoListのプロパティ名
         */
        public _FacebookpostdefaultInfoNames facebookpostdefaultInfoList() {
            return new _FacebookpostdefaultInfoNames(this, "facebookpostdefaultInfoList");
        }

    }
}
