/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanData;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovgroupInfoNames._LocalgovgroupInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TrainingplanData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/05/13 17:05:35")
public class TrainingplanDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Drill plan";
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
     * localgovgroupinfoidのプロパティ名を返します。
     * 
     * @return localgovgroupinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovgroupinfoid() {
        return new PropertyName<Long>("localgovgroupinfoid");
    }

    /**
     * disasteridのプロパティ名を返します。
     * 
     * @return disasteridのプロパティ名
     */
    /*public static PropertyName<Integer> disasterid() {
        return new PropertyName<Integer>("disasterid");
    }*/

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
     * publiccommonsflagのプロパティ名を返します。
     * 
     * @return publiccommonsflagのプロパティ名
     */
    public static PropertyName<Boolean> publiccommonsflag() {
        return new PropertyName<Boolean>("publiccommonsflag");
    }

    /**
     * facebookflagのプロパティ名を返します。
     * 
     * @return facebookflagのプロパティ名
     */
    public static PropertyName<Boolean> facebookflag() {
        return new PropertyName<Boolean>("facebookflag");
    }

    /**
     * twitterflagのプロパティ名を返します。
     * 
     * @return twitterflagのプロパティ名
     */
    public static PropertyName<Boolean> twitterflag() {
        return new PropertyName<Boolean>("twitterflag");
    }

    /**
     * ecommapgwflagのプロパティ名を返します。
     * 
     * @return ecommapgwflagのプロパティ名
     */
    public static PropertyName<Boolean> ecommapgwflag() {
        return new PropertyName<Boolean>("ecommapgwflag");
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
     * localgovInfoのプロパティ名を返します。
     * 
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * localgovgroupinfoのプロパティ名を返します。
     * 
     * @return localgovgroupinfoのプロパティ名
     */
    public static _LocalgovgroupInfoNames localgovgroupinfo() {
        return new _LocalgovgroupInfoNames("localgovgroupinfo");
    }
    
    /**
     * disasterMasterのプロパティ名を返します。
     * 
     * @return disasterMasterのプロパティ名
     */
    /*public static _DisasterMasterNames disasterMaster() {
        return new _DisasterMasterNames("disasterMaster");
    }*/

    /**
     * @author S2JDBC-Gen
     */
    public static class _TrainingplanDataNames extends PropertyName<TrainingplanData> {

        /**
         * インスタンスを構築します。
         */
        public _TrainingplanDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TrainingplanDataNames(final String name) {
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
        public _TrainingplanDataNames(final PropertyName<?> parent, final String name) {
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
         * localgovgroupinfoidのプロパティ名を返します。
         *
         * @return localgovgroupinfoidのプロパティ名
         */
        public PropertyName<Long> localgovgroupinfoid() {
            return new PropertyName<Long>(this, "localgovgroupinfoid");
        }
        
        /**
         * disasteridのプロパティ名を返します。
         *
         * @return disasteridのプロパティ名
         */
        /*public PropertyName<Integer> disasterid() {
            return new PropertyName<Integer>(this, "disasterid");
        }*/

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
         * publiccommonsflagのプロパティ名を返します。
         *
         * @return publiccommonsflagのプロパティ名
         */
        public PropertyName<Boolean> publiccommonsflag() {
            return new PropertyName<Boolean>(this, "publiccommonsflag");
        }

        /**
         * facebookflagのプロパティ名を返します。
         *
         * @return facebookflagのプロパティ名
         */
        public PropertyName<Boolean> facebookflag() {
            return new PropertyName<Boolean>(this, "facebookflag");
        }

        /**
         * twitterflagのプロパティ名を返します。
         *
         * @return twitterflagのプロパティ名
         */
        public PropertyName<Boolean> twitterflag() {
            return new PropertyName<Boolean>(this, "twitterflag");
        }

        /**
         * ecommapgwflagのプロパティ名を返します。
         *
         * @return ecommapgwflagのプロパティ名
         */
        public PropertyName<Boolean> ecommapgwflag() {
            return new PropertyName<Boolean>(this, "ecommapgwflag");
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
         * localgovInfoのプロパティ名を返します。
         * 
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * localgovgroupinfoのプロパティ名を返します。
         * 
         * @return localgovgroupinfoのプロパティ名
         */
        public _LocalgovgroupInfoNames localgovgroupinfo() {
            return new _LocalgovgroupInfoNames(this, "localgovgroupinfo");
        }
        
        /**
         * disasterMasterのプロパティ名を返します。
         * 
         * @return disasterMasterのプロパティ名
         */
        /*public _DisasterMasterNames disasterMaster() {
            return new _DisasterMasterNames(this, "disasterMaster");
        }*/
    }
}
