/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.JudgeInfo;
import jp.ecom_plat.saigaitask.entity.names.JudgemanInfoNames._JudgemanInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JudgeInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/25 1:05:03")
public class JudgeInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Data judging info";
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
     * telemeterdatacodeのプロパティ名を返します。
     *
     * @return telemeterdatacodeのプロパティ名
     */
    public static PropertyName<Long> telemeterdatacode() {
        return new PropertyName<Long>("telemeterdatacode");
    }

    /**
     * itemcodeのプロパティ名を返します。
     *
     * @return itemcodeのプロパティ名
     */
    public static PropertyName<Integer> itemcode() {
        return new PropertyName<Integer>("itemcode");
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
     * valのプロパティ名を返します。
     *
     * @return valのプロパティ名
     */
    public static PropertyName<String> val() {
        return new PropertyName<String>("val");
    }

    /**
     * judgeformulaidのプロパティ名を返します。
     *
     * @return judgeformulaidのプロパティ名
     */
    public static PropertyName<Integer> judgeformulaid() {
        return new PropertyName<Integer>("judgeformulaid");
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
     * @author S2JDBC-Gen
     */
    public static class _JudgeInfoNames extends PropertyName<JudgeInfo> {

        /**
         * インスタンスを構築します。
         */
        public _JudgeInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _JudgeInfoNames(final String name) {
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
        public _JudgeInfoNames(final PropertyName<?> parent, final String name) {
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
         * telemeterdatacodeのプロパティ名を返します。
         *
         * @return telemeterdatacodeのプロパティ名
         */
        public PropertyName<Long> telemeterdatacode() {
            return new PropertyName<Long>(this, "telemeterdatacode");
        }

        /**
         * itemcodeのプロパティ名を返します。
         *
         * @return itemcodeのプロパティ名
         */
        public PropertyName<Integer> itemcode() {
            return new PropertyName<Integer>(this, "itemcode");
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
         * valのプロパティ名を返します。
         *
         * @return valのプロパティ名
         */
        public PropertyName<String> val() {
            return new PropertyName<String>(this, "val");
        }

        /**
         * judgeformulaidのプロパティ名を返します。
         *
         * @return judgeformulaidのプロパティ名
         */
        public PropertyName<Integer> judgeformulaid() {
            return new PropertyName<Integer>(this, "judgeformulaid");
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
    }
}
