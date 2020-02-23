/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.NoticeaddressInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link NoticeaddressInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:30")
public class NoticeaddressInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Notification contact info";
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
     * nameのプロパティ名を返します。
     * 
     * @return nameのプロパティ名
     */
    public static PropertyName<String> name() {
        return new PropertyName<String>("name");
    }

    /**
     * telnoのプロパティ名を返します。
     * 
     * @return telnoのプロパティ名
     */
    public static PropertyName<String> telno() {
        return new PropertyName<String>("telno");
    }

    /**
     * mobilenoのプロパティ名を返します。
     * 
     * @return mobilenoのプロパティ名
     */
    public static PropertyName<String> mobileno() {
        return new PropertyName<String>("mobileno");
    }

    /**
     * faxnoのプロパティ名を返します。
     * 
     * @return faxnoのプロパティ名
     */
    public static PropertyName<String> faxno() {
        return new PropertyName<String>("faxno");
    }

    /**
     * emailのプロパティ名を返します。
     * 
     * @return emailのプロパティ名
     */
    public static PropertyName<String> email() {
        return new PropertyName<String>("email");
    }

    /**
     * mobilemailのプロパティ名を返します。
     * 
     * @return mobilemailのプロパティ名
     */
    public static PropertyName<String> mobilemail() {
        return new PropertyName<String>("mobilemail");
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
     * @author S2JDBC-Gen
     */
    public static class _NoticeaddressInfoNames extends PropertyName<NoticeaddressInfo> {

        /**
         * インスタンスを構築します。
         */
        public _NoticeaddressInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _NoticeaddressInfoNames(final String name) {
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
        public _NoticeaddressInfoNames(final PropertyName<?> parent, final String name) {
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
         * nameのプロパティ名を返します。
         *
         * @return nameのプロパティ名
         */
        public PropertyName<String> name() {
            return new PropertyName<String>(this, "name");
        }

        /**
         * telnoのプロパティ名を返します。
         *
         * @return telnoのプロパティ名
         */
        public PropertyName<String> telno() {
            return new PropertyName<String>(this, "telno");
        }

        /**
         * mobilenoのプロパティ名を返します。
         *
         * @return mobilenoのプロパティ名
         */
        public PropertyName<String> mobileno() {
            return new PropertyName<String>(this, "mobileno");
        }

        /**
         * faxnoのプロパティ名を返します。
         *
         * @return faxnoのプロパティ名
         */
        public PropertyName<String> faxno() {
            return new PropertyName<String>(this, "faxno");
        }

        /**
         * emailのプロパティ名を返します。
         *
         * @return emailのプロパティ名
         */
        public PropertyName<String> email() {
            return new PropertyName<String>(this, "email");
        }

        /**
         * mobilemailのプロパティ名を返します。
         *
         * @return mobilemailのプロパティ名
         */
        public PropertyName<String> mobilemail() {
            return new PropertyName<String>(this, "mobilemail");
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
    }
}
