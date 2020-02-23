/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleInfo;
import jp.ecom_plat.saigaitask.entity.names.JudgemanInfoNames._JudgemanInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JudgeresultstyleDataNames._JudgeresultstyleDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JudgeresultstyleInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/03/08 13:03:54")
public class JudgeresultstyleInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Data result style info";
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
     * styleのプロパティ名を返します。
     *
     * @return styleのプロパティ名
     */
    public static PropertyName<String> style() {
        return new PropertyName<String>("style");
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
     * judgeresultstyleDataListのプロパティ名を返します。
     *
     * @return judgeresultstyleDataListのプロパティ名
     */
    public static _JudgeresultstyleDataNames judgeresultstyleDataList() {
        return new _JudgeresultstyleDataNames("judgeresultstyleDataList");
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
    public static class _JudgeresultstyleInfoNames extends PropertyName<JudgeresultstyleInfo> {

        /**
         * インスタンスを構築します。
         */
        public _JudgeresultstyleInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _JudgeresultstyleInfoNames(final String name) {
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
        public _JudgeresultstyleInfoNames(final PropertyName<?> parent, final String name) {
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
         * styleのプロパティ名を返します。
         *
         * @return styleのプロパティ名
         */
        public PropertyName<String> style() {
            return new PropertyName<String>(this, "style");
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
         * judgeresultstyleDataListのプロパティ名を返します。
         *
         * @return judgeresultstyleDataListのプロパティ名
         */
        public _JudgeresultstyleDataNames judgeresultstyleDataList() {
            return new _JudgeresultstyleDataNames(this, "judgeresultstyleDataList");
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
