/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TelemeterData;
import jp.ecom_plat.saigaitask.entity.names.JudgeresultstyleDataNames._JudgeresultstyleDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TelemeterData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/20 19:42:39")
public class TelemeterDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Telemeter data";
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
     * codeのプロパティ名を返します。
     * 
     * @return codeのプロパティ名
     */
    public static PropertyName<Long> code() {
        return new PropertyName<Long>("code");
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
     * contentscodeのプロパティ名を返します。
     * 
     * @return contentscodeのプロパティ名
     */
    public static PropertyName<Integer> contentscode() {
        return new PropertyName<Integer>("contentscode");
    }

    /**
     * observtimeのプロパティ名を返します。
     * 
     * @return observtimeのプロパティ名
     */
    public static PropertyName<Timestamp> observtime() {
        return new PropertyName<Timestamp>("observtime");
    }

    /**
     * valのプロパティ名を返します。
     * 
     * @return valのプロパティ名
     */
    public static PropertyName<Double> val() {
        return new PropertyName<Double>("val");
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
     * @author S2JDBC-Gen
     */
    public static class _TelemeterDataNames extends PropertyName<TelemeterData> {

        /**
         * インスタンスを構築します。
         */
        public _TelemeterDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TelemeterDataNames(final String name) {
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
        public _TelemeterDataNames(final PropertyName<?> parent, final String name) {
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
         * codeのプロパティ名を返します。
         *
         * @return codeのプロパティ名
         */
        public PropertyName<Long> code() {
            return new PropertyName<Long>(this, "code");
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
         * contentscodeのプロパティ名を返します。
         *
         * @return contentscodeのプロパティ名
         */
        public PropertyName<Integer> contentscode() {
            return new PropertyName<Integer>(this, "contentscode");
        }

        /**
         * observtimeのプロパティ名を返します。
         *
         * @return observtimeのプロパティ名
         */
        public PropertyName<Timestamp> observtime() {
            return new PropertyName<Timestamp>(this, "observtime");
        }

        /**
         * valのプロパティ名を返します。
         *
         * @return valのプロパティ名
         */
        public PropertyName<Double> val() {
            return new PropertyName<Double>(this, "val");
        }

        /**
         * judgeresultstyleDataListのプロパティ名を返します。
         * 
         * @return judgeresultstyleDataListのプロパティ名
         */
        public _JudgeresultstyleDataNames judgeresultstyleDataList() {
            return new _JudgeresultstyleDataNames(this, "judgeresultstyleDataList");
        }
    }
}
