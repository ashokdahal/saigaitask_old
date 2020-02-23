/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultData;
import jp.ecom_plat.saigaitask.entity.names.JudgemanInfoNames._JudgemanInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JudgeresultData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/25 1:05:03")
public class JudgeresultDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Judgement result data";
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
     * judgemaninfoidのプロパティ名を返します。
     *
     * @return judgemaninfoidのプロパティ名
     */
    public static PropertyName<Long> judgemaninfoid() {
        return new PropertyName<Long>("judgemaninfoid");
    }

    /**
     * judgetimeのプロパティ名を返します。
     *
     * @return judgetimeのプロパティ名
     */
    public static PropertyName<Timestamp> judgetime() {
        return new PropertyName<Timestamp>("judgetime");
    }

    /**
     * valのプロパティ名を返します。
     *
     * @return valのプロパティ名
     */
    public static PropertyName<String> val() {
        return new PropertyName<String>("val");
    }

    /**
     * canceltimeのプロパティ名を返します。
     *
     * @return canceltimeのプロパティ名
     */
    public static PropertyName<Timestamp> canceltime() {
        return new PropertyName<Timestamp>("canceltime");
    }

    /**
     * bcancelのプロパティ名を返します。
     *
     * @return bcancelのプロパティ名
     */
    public static PropertyName<Boolean> bcancel() {
        return new PropertyName<Boolean>("bcancel");
    }

    /**
     * judgemanInfoのプロパティ名を返します。
     * 
     * @return judgemanInfoのプロパティ名
     */
    public static _JudgemanInfoNames judgemanInfo() {
        return new _JudgemanInfoNames("judgemanInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _JudgeresultDataNames extends PropertyName<JudgeresultData> {

        /**
         * インスタンスを構築します。
         */
        public _JudgeresultDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _JudgeresultDataNames(final String name) {
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
        public _JudgeresultDataNames(final PropertyName<?> parent, final String name) {
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
         * judgemaninfoidのプロパティ名を返します。
         *
         * @return judgemaninfoidのプロパティ名
         */
        public PropertyName<Long> judgemaninfoid() {
            return new PropertyName<Long>(this, "judgemaninfoid");
        }

        /**
         * judgetimeのプロパティ名を返します。
         *
         * @return judgetimeのプロパティ名
         */
        public PropertyName<Timestamp> judgetime() {
            return new PropertyName<Timestamp>(this, "judgetime");
        }

        /**
         * valのプロパティ名を返します。
         *
         * @return valのプロパティ名
         */
        public PropertyName<String> val() {
            return new PropertyName<String>(this, "val");
        }

        /**
         * canceltimeのプロパティ名を返します。
         *
         * @return canceltimeのプロパティ名
         */
        public PropertyName<Timestamp> canceltime() {
            return new PropertyName<Timestamp>(this, "canceltime");
        }

        /**
         * bcancelのプロパティ名を返します。
         *
         * @return bcancelのプロパティ名
         */
        public PropertyName<Boolean> bcancel() {
            return new PropertyName<Boolean>(this, "bcancel");
        }

        /**
         * judgemanInfoのプロパティ名を返します。
         * 
         * @return judgemanInfoのプロパティ名
         */
        public _JudgemanInfoNames judgemanInfo() {
            return new _JudgemanInfoNames(this, "judgemanInfo");
        }
    }
}
