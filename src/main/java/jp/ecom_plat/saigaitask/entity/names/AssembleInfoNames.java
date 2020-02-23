/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AssembleInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticeTemplateNames._NoticeTemplateNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.StationclassInfoNames._StationclassInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AssembleInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/17 19:40:55")
public class AssembleInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Staff assembly info";
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
     * stationclassinfoidのプロパティ名を返します。
     * 
     * @return stationclassinfoidのプロパティ名
     */
    public static PropertyName<Long> stationclassinfoid() {
        return new PropertyName<Long>("stationclassinfoid");
    }

    /**
     * noticetemplateidのプロパティ名を返します。
     * 
     * @return noticetemplateidのプロパティ名
     */
    public static PropertyName<Long> noticetemplateid() {
        return new PropertyName<Long>("noticetemplateid");
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
     * validのプロパティ名を返します。
     * 
     * @return validのプロパティ名
     */
    public static PropertyName<Boolean> valid() {
        return new PropertyName<Boolean>("valid");
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
     * noticeTemplateのプロパティ名を返します。
     * 
     * @return noticeTemplateのプロパティ名
     */
    public static _NoticeTemplateNames noticeTemplate() {
        return new _NoticeTemplateNames("noticeTemplate");
    }

    /**
     * stationclassInfoのプロパティ名を返します。
     * 
     * @return stationclassInfoのプロパティ名
     */
    public static _StationclassInfoNames stationclassInfo() {
        return new _StationclassInfoNames("stationclassInfo");
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
    public static class _AssembleInfoNames extends PropertyName<AssembleInfo> {

        /**
         * インスタンスを構築します。
         */
        public _AssembleInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _AssembleInfoNames(final String name) {
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
        public _AssembleInfoNames(final PropertyName<?> parent, final String name) {
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
         * stationclassinfoidのプロパティ名を返します。
         *
         * @return stationclassinfoidのプロパティ名
         */
        public PropertyName<Long> stationclassinfoid() {
            return new PropertyName<Long>(this, "stationclassinfoid");
        }

        /**
         * noticetemplateidのプロパティ名を返します。
         *
         * @return noticetemplateidのプロパティ名
         */
        public PropertyName<Long> noticetemplateid() {
            return new PropertyName<Long>(this, "noticetemplateid");
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
         * validのプロパティ名を返します。
         *
         * @return validのプロパティ名
         */
        public PropertyName<Boolean> valid() {
            return new PropertyName<Boolean>(this, "valid");
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
         * noticeTemplateのプロパティ名を返します。
         *
         * @return noticeTemplateのプロパティ名
         */
        public _NoticeTemplateNames noticeTemplate() {
            return new _NoticeTemplateNames(this, "noticeTemplate");
        }

        /**
         * stationclassInfoのプロパティ名を返します。
         *
         * @return stationclassInfoのプロパティ名
         */
        public _StationclassInfoNames stationclassInfo() {
            return new _StationclassInfoNames(this, "stationclassInfo");
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
