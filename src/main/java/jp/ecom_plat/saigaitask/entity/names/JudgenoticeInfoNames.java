/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.JudgenoticeInfo;
import jp.ecom_plat.saigaitask.entity.names.JudgealarmInfoNames._JudgealarmInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JudgenoticeInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/25 1:05:03")
public class JudgenoticeInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Data judging notification info";
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
     * judgealarminfoidのプロパティ名を返します。
     *
     * @return judgealarminfoidのプロパティ名
     */
    public static PropertyName<Long> judgealarminfoid() {
        return new PropertyName<Long>("judgealarminfoid");
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
     * judgealarmInfoのプロパティ名を返します。
     *
     * @return judgealarmInfoのプロパティ名
     */
    public static _JudgealarmInfoNames judgealarmInfo() {
        return new _JudgealarmInfoNames("judgealarmInfo");
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
    public static class _JudgenoticeInfoNames extends PropertyName<JudgenoticeInfo> {

        /**
         * インスタンスを構築します。
         */
        public _JudgenoticeInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _JudgenoticeInfoNames(final String name) {
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
        public _JudgenoticeInfoNames(final PropertyName<?> parent, final String name) {
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
         * judgealarminfoidのプロパティ名を返します。
         *
         * @return judgealarminfoidのプロパティ名
         */
        public PropertyName<Long> judgealarminfoid() {
            return new PropertyName<Long>(this, "judgealarminfoid");
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
         * judgealarmInfoのプロパティ名を返します。
         *
         * @return judgealarmInfoのプロパティ名
         */
        public _JudgealarmInfoNames judgealarmInfo() {
        	return new _JudgealarmInfoNames(this, "judgealarmInfo");
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
