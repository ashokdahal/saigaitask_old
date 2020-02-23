/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TrainingmeteoData;
import jp.ecom_plat.saigaitask.entity.names.MeteotypeMasterNames._MeteotypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.TrainingplanDataNames._TrainingplanDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TrainingmeteoData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/05/13 17:11:00")
public class TrainingmeteoDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "External drill XML data";
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
     * meteourlのプロパティ名を返します。
     * 
     * @return meteourlのプロパティ名
     */
    public static PropertyName<String> meteourl() {
        return new PropertyName<String>("meteourl");
    }

    /**
     * meteotypeidのプロパティ名を返します。
     * 
     * @return meteotypeidのプロパティ名
     */
    public static PropertyName<Integer> meteotypeid() {
        return new PropertyName<Integer>("meteotypeid");
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
     * updatetimeのプロパティ名を返します。
     * 
     * @return updatetimeのプロパティ名
     */
    public static PropertyName<Timestamp> updatetime() {
        return new PropertyName<Timestamp>("updatetime");
    }

    /**
     * deletedのプロパティ名を返します。
     * 
     * @return deletedのプロパティ名
     */
    public static PropertyName<Boolean> deleted() {
        return new PropertyName<Boolean>("deleted");
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
     * meteotypeMasterのプロパティ名を返します。
     * 
     * @return meteotypeMasterのプロパティ名
     */
    public static _MeteotypeMasterNames meteotypeMaster() {
        return new _MeteotypeMasterNames("meteotypeMaster");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TrainingmeteoDataNames extends PropertyName<TrainingmeteoData> {

        /**
         * インスタンスを構築します。
         */
        public _TrainingmeteoDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TrainingmeteoDataNames(final String name) {
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
        public _TrainingmeteoDataNames(final PropertyName<?> parent, final String name) {
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
         * meteourlのプロパティ名を返します。
         *
         * @return meteourlのプロパティ名
         */
        public PropertyName<String> meteourl() {
            return new PropertyName<String>(this, "meteourl");
        }

        /**
         * meteotypeidのプロパティ名を返します。
         *
         * @return meteotypeidのプロパティ名
         */
        public PropertyName<Integer> meteotypeid() {
            return new PropertyName<Integer>(this, "meteotypeid");
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
         * updatetimeのプロパティ名を返します。
         *
         * @return updatetimeのプロパティ名
         */
        public PropertyName<Timestamp> updatetime() {
            return new PropertyName<Timestamp>(this, "updatetime");
        }

        /**
         * deletedのプロパティ名を返します。
         *
         * @return deletedのプロパティ名
         */
        public PropertyName<Boolean> deleted() {
            return new PropertyName<Boolean>(this, "deleted");
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
         * meteotypeMasterのプロパティ名を返します。
         * 
         * @return meteotypeMasterのプロパティ名
         */
        public _MeteotypeMasterNames meteotypeMaster() {
            return new _MeteotypeMasterNames(this, "meteotypeMaster");
        }
    }
}
