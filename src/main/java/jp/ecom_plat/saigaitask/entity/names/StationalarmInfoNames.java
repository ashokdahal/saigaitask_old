/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.StationalarmInfo;
import jp.ecom_plat.saigaitask.entity.db.StationclassInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link StationalarmInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/11/13 19:09:48")
public class StationalarmInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "System alert info";
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
     * alarmmessageinfoidのプロパティ名を返します。
     *
     * @return alarmmessageinfoidのプロパティ名
     */
    public static PropertyName<Long> alarmmessageinfoid() {
        return new PropertyName<Long>("alarmmessageinfoid");
    }

    /**
     * tablemasterinfoidのプロパティ名を返します。
     *
     * @return tablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> tablemasterinfoid() {
        return new PropertyName<Long>("tablemasterinfoid");
    }

    /**
     * stationclassinfoidのプロパティ名を返します。
     *
     * @return stationclassinfoidのプロパティ名
     */
    public static PropertyName<Long> stationclassinfoid() {
        return new PropertyName<Long>("stationclassinfoid");
    }

    /**
     * stationclassorgidのプロパティ名を返します。
     *
     * @return stationclassorgidのプロパティ名
     */
    public static PropertyName<Long> stationclassorgid() {
        return new PropertyName<Long>("stationclassorgid");
    }

    /**
     *stationclassInfoのプロパティ名を返します。
     *
     * @return stationclassInfoのプロパティ名
     */
    public static PropertyName<StationclassInfo> stationclassInfo() {
        return new PropertyName<StationclassInfo>("stationclassInfo");
    }

    /**
     *tablemasterInfoのプロパティ名を返します。
     *
     * @return tablemasterInfoのプロパティ名
     */
    public static PropertyName<TablemasterInfo> tablemasterInfo() {
        return new PropertyName<TablemasterInfo>("tablemasterInfo");
    }

    /**
     *alarmmessageInfoのプロパティ名を返します。
     *
     * @return alarmmessageInfoのプロパティ名
     */
    public static PropertyName<AlarmmessageInfo> alarmmessageInfo() {
        return new PropertyName<AlarmmessageInfo>("alarmmessageInfo");
    }

    /**
     *localgovInfoのプロパティ名を返します。
     *
     * @return localgovInfoのプロパティ名
     */
    public static PropertyName<LocalgovInfo> localgovInfo() {
        return new PropertyName<LocalgovInfo>("localgovInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _StationalarmInfoNames extends PropertyName<StationalarmInfo> {

        /**
         * インスタンスを構築します。
         */
        public _StationalarmInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _StationalarmInfoNames(final String name) {
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
        public _StationalarmInfoNames(final PropertyName<?> parent, final String name) {
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
         * alarmmessageinfoidのプロパティ名を返します。
         *
         * @return alarmmessageinfoidのプロパティ名
         */
        public PropertyName<Long> alarmmessageinfoid() {
            return new PropertyName<Long>(this, "alarmmessageinfoid");
        }

        /**
         * tablemasterinfoidのプロパティ名を返します。
         *
         * @return tablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> tablemasterinfoid() {
            return new PropertyName<Long>(this, "tablemasterinfoid");
        }

        /**
         * stationclassinfoidのプロパティ名を返します。
         *
         * @return stationclassinfoidのプロパティ名
         */
        public PropertyName<Long> stationclassinfoid() {
            return new PropertyName<Long>(this, "stationclassinfoid");
        }

        /**
         * stationclassorgidのプロパティ名を返します。
         *
         * @return stationclassorgidのプロパティ名
         */
        public PropertyName<Long> stationclassorgid() {
            return new PropertyName<Long>(this, "stationclassorgid");
        }
        /**
         * stationclassInfoのプロパティ名を返します。
         *
         * @return stationclassInfoのプロパティ名
         */
        public PropertyName<StationclassInfo> stationclassInfo() {
            return new PropertyName<StationclassInfo>(this, "stationclassInfo");
        }
        /**
         * tablemasterInfoのプロパティ名を返します。
         *
         * @return tablemasterInfoのプロパティ名
         */
        public PropertyName<TablemasterInfo> tablemasterInfo() {
            return new PropertyName<TablemasterInfo>(this, "tablemasterInfo");
        }
        /**
         * alarmmessageInfoのプロパティ名を返します。
         *
         * @return alarmmessageInfoのプロパティ名
         */
        public PropertyName<AlarmmessageInfo> alarmmessageInfo() {
            return new PropertyName<AlarmmessageInfo>(this, "alarmmessageInfo");
        }
        /**
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public PropertyName<LocalgovInfo> localgovInfo() {
            return new PropertyName<LocalgovInfo>(this, "localgovInfo");
        }
    }

}
