/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.names.DemoInfoNames._DemoInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.LocalgovgroupInfoNames._LocalgovgroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrackgroupDataNames._TrackgroupDataNames;
import jp.ecom_plat.saigaitask.entity.names.TrackmapInfoNames._TrackmapInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrainingplanDataNames._TrainingplanDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TrackData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/08/05 17:22:42")
public class TrackDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Record data";
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
     * trackmapinfoidのプロパティ名を返します。
     *
     * @return trackmapinfoidのプロパティ名
     */
    public static PropertyName<Long> trackmapinfoid() {
        return new PropertyName<Long>("trackmapinfoid");
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
     * demoinfoidのプロパティ名を返します。
     *
     * @return demoinfoidのプロパティ名
     */
    public static PropertyName<Long> demoinfoid() {
        return new PropertyName<Long>("demoinfoid");
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
     * trainingplandataidのプロパティ名を返します。
     *
     * @return trainingplandataidのプロパティ名
     */
    public static PropertyName<Long> trainingplandataid() {
        return new PropertyName<Long>("trainingplandataid");
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
     * starttimeのプロパティ名を返します。
     *
     * @return starttimeのプロパティ名
     */
    public static PropertyName<Timestamp> starttime() {
        return new PropertyName<Timestamp>("starttime");
    }

    /**
     * endtimeのプロパティ名を返します。
     *
     * @return endtimeのプロパティ名
     */
    public static PropertyName<Timestamp> endtime() {
        return new PropertyName<Timestamp>("endtime");
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
     * demoInfoのプロパティ名を返します。
     *
     * @return demoInfoのプロパティ名
     */
    public static _DemoInfoNames demoInfo() {
        return new _DemoInfoNames("demoInfo");
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
     * trackmapInfoのプロパティ名を返します。
     *
     * @return trackmapInfoのプロパティ名
     */
    public static _TrackmapInfoNames trackmapInfo() {
        return new _TrackmapInfoNames("trackmapInfo");
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
     * prefTrackgroupDatasのプロパティ名を返します。
     *
     * @return prefTrackgroupDatasのプロパティ名
     */
    public static _TrackgroupDataNames prefTrackgroupDatas() {
        return new _TrackgroupDataNames("prefTrackgroupDatas");
    }

    /**
     * cityTrackgroupDatasのプロパティ名を返します。
     *
     * @return cityTrackgroupDatasのプロパティ名
     */
    public static _TrackgroupDataNames cityTrackgroupDatas() {
        return new _TrackgroupDataNames("cityTrackgroupDatas");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TrackDataNames extends PropertyName<TrackData> {

        /**
         * インスタンスを構築します。
         */
        public _TrackDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TrackDataNames(final String name) {
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
        public _TrackDataNames(final PropertyName<?> parent, final String name) {
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
         * trackmapinfoidのプロパティ名を返します。
         *
         * @return trackmapinfoidのプロパティ名
         */
        public PropertyName<Long> trackmapinfoid() {
            return new PropertyName<Long>(this, "trackmapinfoid");
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
         * demoinfoidのプロパティ名を返します。
         *
         * @return demoinfoidのプロパティ名
         */
        public PropertyName<Long> demoinfoid() {
            return new PropertyName<Long>(this, "demoinfoid");
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
         * trainingplandataidのプロパティ名を返します。
         *
         * @return trainingplandataidのプロパティ名
         */
        public PropertyName<Long> trainingplandataid() {
            return new PropertyName<Long>(this, "trainingplandataid");
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
         * starttimeのプロパティ名を返します。
         *
         * @return starttimeのプロパティ名
         */
        public PropertyName<Timestamp> starttime() {
            return new PropertyName<Timestamp>(this, "starttime");
        }

        /**
         * endtimeのプロパティ名を返します。
         *
         * @return endtimeのプロパティ名
         */
        public PropertyName<Timestamp> endtime() {
            return new PropertyName<Timestamp>(this, "endtime");
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
         * demoInfoのプロパティ名を返します。
         *
         * @return demoInfoのプロパティ名
         */
        public _DemoInfoNames demoInfo() {
            return new _DemoInfoNames(this, "demoInfo");
        }

        /**
         * disasterMasterのプロパティ名を返します。
         *
         * @return disasterMasterのプロパティ名
         */
        /*public _DisasterMasterNames disasterMaster() {
            return new _DisasterMasterNames(this, "disasterMaster");
        }*/

        /**
         * trackmapInfoのプロパティ名を返します。
         *
         * @return trackmapInfoのプロパティ名
         */
        public _TrackmapInfoNames trackmapInfo() {
            return new _TrackmapInfoNames(this, "trackmapInfo");
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
         * prefTrackgroupDatasのプロパティ名を返します。
         *
         * @return prefTrackgroupDatasのプロパティ名
         */
        public _TrackgroupDataNames prefTrackgroupDatas() {
            return new _TrackgroupDataNames(this, "prefTrackgroupDatas");
        }

        /**
         * cityTrackgroupDatasのプロパティ名を返します。
         *
         * @return cityTrackgroupDatasのプロパティ名
         */
        public _TrackgroupDataNames cityTrackgroupDatas() {
            return new _TrackgroupDataNames(this, "cityTrackgroupDatas");
        }
    }
}
