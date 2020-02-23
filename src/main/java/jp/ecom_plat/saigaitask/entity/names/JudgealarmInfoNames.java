/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.JudgealarmInfo;
import jp.ecom_plat.saigaitask.entity.names.AlarmmessageInfoNames._AlarmmessageInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgemanInfoNames._JudgemanInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JudgealarmInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/25 1:05:03")
public class JudgealarmInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Data judging alarm info";
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
     * alarmmessageinfoidのプロパティ名を返します。
     *
     * @return alarmmessageinfoidのプロパティ名
     */
    public static PropertyName<Long> alarmmessageinfoid() {
        return new PropertyName<Long>("alarmmessageinfoid");
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
     * judgemanInfoのプロパティ名を返します。
     *
     * @return judgemanInfoのプロパティ名
     */
    public static _JudgemanInfoNames judgemanInfo() {
        return new _JudgemanInfoNames("judgemanInfo");
    }

    /**
     *alarmmessageInfoのプロパティ名を返します。
     *
     * @return alarmmessageInfoのプロパティ名
     */
    public static _AlarmmessageInfoNames alarmmessageInfo() {
        return new _AlarmmessageInfoNames("alarmmessageInfo");
    }


    /**
     * @author S2JDBC-Gen
     */
    public static class _JudgealarmInfoNames extends PropertyName<JudgealarmInfo> {

        /**
         * インスタンスを構築します。
         */
        public _JudgealarmInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _JudgealarmInfoNames(final String name) {
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
        public _JudgealarmInfoNames(final PropertyName<?> parent, final String name) {
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
         * alarmmessageinfoidのプロパティ名を返します。
         *
         * @return alarmmessageinfoidのプロパティ名
         */
        public PropertyName<Long> alarmmessageinfoid() {
            return new PropertyName<Long>(this, "alarmmessageinfoid");
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
         * judgemanInfoのプロパティ名を返します。
         *
         * @return judgemanInfoのプロパティ名
         */
        public _JudgemanInfoNames judgemanInfo() {
        	return new _JudgemanInfoNames(this, "judgemanInfo");
        }

        /**
         * alarmmessageInfoのプロパティ名を返します。
         *
         * @return alarmmessageInfoのプロパティ名
         */
        public _AlarmmessageInfoNames alarmmessageInfo() {
            return new _AlarmmessageInfoNames(this, "alarmmessageInfo");
        }


    }
}
