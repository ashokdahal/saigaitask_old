/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.JudgeresultstyleData;
import jp.ecom_plat.saigaitask.entity.names.JudgeresultstyleInfoNames._JudgeresultstyleInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TelemeterDataNames._TelemeterDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JudgeresultstyleData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/03/08 13:03:54")
public class JudgeresultstyleDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Data judgement result style data";
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
     * judageresultstyleinfoidのプロパティ名を返します。
     * 
     * @return judageresultstyleinfoidのプロパティ名
     */
    public static PropertyName<Long> judageresultstyleinfoid() {
        return new PropertyName<Long>("judageresultstyleinfoid");
    }

    /**
     * telemeterdataidのプロパティ名を返します。
     * 
     * @return telemeterdataidのプロパティ名
     */
    public static PropertyName<Long> telemeterdataid() {
        return new PropertyName<Long>("telemeterdataid");
    }

    /**
     * telemeterDataのプロパティ名を返します。
     * 
     * @return telemeterDataのプロパティ名
     */
    public static _TelemeterDataNames telemeterData() {
        return new _TelemeterDataNames("telemeterData");
    }

    /**
     * judgeresultstyleInfoのプロパティ名を返します。
     * 
     * @return judgeresultstyleInfoのプロパティ名
     */
    public static _JudgeresultstyleInfoNames judgeresultstyleInfo() {
        return new _JudgeresultstyleInfoNames("judgeresultstyleInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _JudgeresultstyleDataNames extends PropertyName<JudgeresultstyleData> {

        /**
         * インスタンスを構築します。
         */
        public _JudgeresultstyleDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _JudgeresultstyleDataNames(final String name) {
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
        public _JudgeresultstyleDataNames(final PropertyName<?> parent, final String name) {
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
         * judageresultstyleinfoidのプロパティ名を返します。
         *
         * @return judageresultstyleinfoidのプロパティ名
         */
        public PropertyName<Long> judageresultstyleinfoid() {
            return new PropertyName<Long>(this, "judageresultstyleinfoid");
        }

        /**
         * telemeterdataidのプロパティ名を返します。
         *
         * @return telemeterdataidのプロパティ名
         */
        public PropertyName<Long> telemeterdataid() {
            return new PropertyName<Long>(this, "telemeterdataid");
        }

        /**
         * telemeterDataのプロパティ名を返します。
         * 
         * @return telemeterDataのプロパティ名
         */
        public _TelemeterDataNames telemeterData() {
            return new _TelemeterDataNames(this, "telemeterData");
        }

        /**
         * judgeresultstyleInfoのプロパティ名を返します。
         * 
         * @return judgeresultstyleInfoのプロパティ名
         */
        public _JudgeresultstyleInfoNames judgeresultstyleInfo() {
            return new _JudgeresultstyleInfoNames(this, "judgeresultstyleInfo");
        }
    }
}
