/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.DisasterroadData;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link DisasterroadData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/22 14:41:59")
public class DisasterroadDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Constructional damage data";
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
     * trackdataidのプロパティ名を返します。
     * 
     * @return trackdataidのプロパティ名
     */
    public static PropertyName<Long> trackdataid() {
        return new PropertyName<Long>("trackdataid");
    }

    /**
     * roadのプロパティ名を返します。
     * 
     * @return roadのプロパティ名
     */
    public static PropertyName<Integer> road() {
        return new PropertyName<Integer>("road");
    }

    /**
     * bridgeのプロパティ名を返します。
     * 
     * @return bridgeのプロパティ名
     */
    public static PropertyName<Integer> bridge() {
        return new PropertyName<Integer>("bridge");
    }

    /**
     * riverのプロパティ名を返します。
     * 
     * @return riverのプロパティ名
     */
    public static PropertyName<Integer> river() {
        return new PropertyName<Integer>("river");
    }

    /**
     * harborのプロパティ名を返します。
     * 
     * @return harborのプロパティ名
     */
    public static PropertyName<Integer> harbor() {
        return new PropertyName<Integer>("harbor");
    }

    /**
     * landslideのプロパティ名を返します。
     * 
     * @return landslideのプロパティ名
     */
    public static PropertyName<Integer> landslide() {
        return new PropertyName<Integer>("landslide");
    }

    /**
     * cliffのプロパティ名を返します。
     * 
     * @return cliffのプロパティ名
     */
    public static PropertyName<Integer> cliff() {
        return new PropertyName<Integer>("cliff");
    }

    /**
     * railwayのプロパティ名を返します。
     * 
     * @return railwayのプロパティ名
     */
    public static PropertyName<Integer> railway() {
        return new PropertyName<Integer>("railway");
    }

    /**
     * shipのプロパティ名を返します。
     * 
     * @return shipのプロパティ名
     */
    public static PropertyName<Integer> ship() {
        return new PropertyName<Integer>("ship");
    }

    /**
     * waterのプロパティ名を返します。
     * 
     * @return waterのプロパティ名
     */
    public static PropertyName<Integer> water() {
        return new PropertyName<Integer>("water");
    }

    /**
     * roadmountのプロパティ名を返します。
     * 
     * @return roadmountのプロパティ名
     */
    public static PropertyName<Integer> roadmount() {
        return new PropertyName<Integer>("roadmount");
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
     * registtimeのプロパティ名を返します。
     * 
     * @return registtimeのプロパティ名
     */
    public static PropertyName<Timestamp> registtime() {
        return new PropertyName<Timestamp>("registtime");
    }

    /**
     * trackDataのプロパティ名を返します。
     * 
     * @return trackDataのプロパティ名
     */
    public static _TrackDataNames trackData() {
        return new _TrackDataNames("trackData");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _DisasterroadDataNames extends PropertyName<DisasterroadData> {

        /**
         * インスタンスを構築します。
         */
        public _DisasterroadDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _DisasterroadDataNames(final String name) {
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
        public _DisasterroadDataNames(final PropertyName<?> parent, final String name) {
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
         * trackdataidのプロパティ名を返します。
         *
         * @return trackdataidのプロパティ名
         */
        public PropertyName<Long> trackdataid() {
            return new PropertyName<Long>(this, "trackdataid");
        }

        /**
         * roadのプロパティ名を返します。
         *
         * @return roadのプロパティ名
         */
        public PropertyName<Integer> road() {
            return new PropertyName<Integer>(this, "road");
        }

        /**
         * bridgeのプロパティ名を返します。
         *
         * @return bridgeのプロパティ名
         */
        public PropertyName<Integer> bridge() {
            return new PropertyName<Integer>(this, "bridge");
        }

        /**
         * riverのプロパティ名を返します。
         *
         * @return riverのプロパティ名
         */
        public PropertyName<Integer> river() {
            return new PropertyName<Integer>(this, "river");
        }

        /**
         * harborのプロパティ名を返します。
         *
         * @return harborのプロパティ名
         */
        public PropertyName<Integer> harbor() {
            return new PropertyName<Integer>(this, "harbor");
        }

        /**
         * landslideのプロパティ名を返します。
         *
         * @return landslideのプロパティ名
         */
        public PropertyName<Integer> landslide() {
            return new PropertyName<Integer>(this, "landslide");
        }

        /**
         * cliffのプロパティ名を返します。
         *
         * @return cliffのプロパティ名
         */
        public PropertyName<Integer> cliff() {
            return new PropertyName<Integer>(this, "cliff");
        }

        /**
         * railwayのプロパティ名を返します。
         *
         * @return railwayのプロパティ名
         */
        public PropertyName<Integer> railway() {
            return new PropertyName<Integer>(this, "railway");
        }

        /**
         * shipのプロパティ名を返します。
         *
         * @return shipのプロパティ名
         */
        public PropertyName<Integer> ship() {
            return new PropertyName<Integer>(this, "ship");
        }

        /**
         * waterのプロパティ名を返します。
         *
         * @return waterのプロパティ名
         */
        public PropertyName<Integer> water() {
            return new PropertyName<Integer>(this, "water");
        }

        /**
         * roadmountのプロパティ名を返します。
         *
         * @return roadmountのプロパティ名
         */
        public PropertyName<Integer> roadmount() {
            return new PropertyName<Integer>(this, "roadmount");
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
         * registtimeのプロパティ名を返します。
         *
         * @return registtimeのプロパティ名
         */
        public PropertyName<Timestamp> registtime() {
            return new PropertyName<Timestamp>(this, "registtime");
        }

        /**
         * trackDataのプロパティ名を返します。
         * 
         * @return trackDataのプロパティ名
         */
        public _TrackDataNames trackData() {
            return new _TrackDataNames(this, "trackData");
        }
    }
}
