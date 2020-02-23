/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanlinkData;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrainingplanDataNames._TrainingplanDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TrainingplanlinkData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/05/13 16:55:18")
public class TrainingplanlinkDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Drill plan linked municipality data";
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
     * trainingplandataidのプロパティ名を返します。
     * 
     * @return trainingplandataidのプロパティ名
     */
    public static PropertyName<Long> trainingplandataid() {
        return new PropertyName<Long>("trainingplandataid");
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
     * updatetimeのプロパティ名を返します。
     * 
     * @return updatetimeのプロパティ名
     */
    public static PropertyName<Timestamp> updatetime() {
        return new PropertyName<Timestamp>("updatetime");
    }

    /**
     * trainingplanDataのプロパティ名を返します。
     * 
     * @return trainingplanDataのプロパティ名
     */
    public static _TrainingplanDataNames trainingplanData() {
        return new _TrainingplanDataNames("trainingplanData");
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
    public static class _TrainingplanlinkDataNames extends PropertyName<TrainingplanlinkData> {

        /**
         * インスタンスを構築します。
         */
        public _TrainingplanlinkDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TrainingplanlinkDataNames(final String name) {
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
        public _TrainingplanlinkDataNames(final PropertyName<?> parent, final String name) {
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
         * trainingplandataidのプロパティ名を返します。
         *
         * @return trainingplandataidのプロパティ名
         */
        public PropertyName<Long> trainingplandataid() {
            return new PropertyName<Long>(this, "trainingplandataid");
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
         * updatetimeのプロパティ名を返します。
         *
         * @return updatetimeのプロパティ名
         */
        public PropertyName<Timestamp> updatetime() {
            return new PropertyName<Timestamp>(this, "updatetime");
        }

        /**
         * trainingplanDataのプロパティ名を返します。
         * 
         * @return trainingplanDataのプロパティ名
         */
        public _TrainingplanDataNames trainingplanData() {
            return new _TrainingplanDataNames(this, "trainingplanData");
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
