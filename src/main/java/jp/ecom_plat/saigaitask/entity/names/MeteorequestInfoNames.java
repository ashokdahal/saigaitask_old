/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MeteorequestInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotriggerInfoNames._MeteotriggerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotypeMasterNames._MeteotypeMasterNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MeteorequestInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/09 19:42:52")
public class MeteorequestInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Acquired weather info";
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
     * meteotypeidのプロパティ名を返します。
     *
     * @return meteotypeidのプロパティ名
     */
    public static PropertyName<Integer> meteotypeid() {
        return new PropertyName<Integer>("meteotypeid");
    }

    /**
     * meteoareaidのプロパティ名を返します。
     *
     * @return meteoareaidのプロパティ名
     */
    public static PropertyName<String> meteoareaid() {
        return new PropertyName<String>("meteoareaid");
    }

    /**
     * meteoareaid2のプロパティ名を返します。
     *
     * @return meteoareaid2のプロパティ名
     */
    public static PropertyName<String> meteoareaid2() {
        return new PropertyName<String>("meteoareaid2");
    }

    /**
     * alarmのプロパティ名を返します。
     *
     * @return alarmのプロパティ名
     */
    public static PropertyName<Boolean> alarm() {
        return new PropertyName<Boolean>("alarm");
    }

    /**
     * viewのプロパティ名を返します。
     *
     * @return viewのプロパティ名
     */
    public static PropertyName<Boolean> view() {
        return new PropertyName<Boolean>("view");
    }

//    /**
//     * mapのプロパティ名を返します。
//     *
//     * @return mapのプロパティ名
//     */
//    public static PropertyName<Boolean> map() {
//        return new PropertyName<Boolean>("map");
//    }

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
     * meteotypeMasterのプロパティ名を返します。
     *
     * @return meteotypeMasterのプロパティ名
     */
    public static _MeteotypeMasterNames meteotypeMaster() {
        return new _MeteotypeMasterNames("meteotypeMaster");
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
     * meteotriggerInfoListのプロパティ名を返します。
     *
     * @return meteotriggerInfoListのプロパティ名
     */
    public static _MeteotriggerInfoNames meteotriggerInfoList() {
        return new _MeteotriggerInfoNames("meteotriggerInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _MeteorequestInfoNames extends PropertyName<MeteorequestInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MeteorequestInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _MeteorequestInfoNames(final String name) {
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
        public _MeteorequestInfoNames(final PropertyName<?> parent, final String name) {
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
         * meteotypeidのプロパティ名を返します。
         *
         * @return meteotypeidのプロパティ名
         */
        public PropertyName<Integer> meteotypeid() {
            return new PropertyName<Integer>(this, "meteotypeid");
        }

        /**
         * meteoareaidのプロパティ名を返します。
         *
         * @return meteoareaidのプロパティ名
         */
        public PropertyName<String> meteoareaid() {
            return new PropertyName<String>(this, "meteoareaid");
        }

        /**
         * meteoareaid2のプロパティ名を返します。
         *
         * @return meteoareaid2のプロパティ名
         */
        public PropertyName<String> meteoareaid2() {
            return new PropertyName<String>(this, "meteoareaid2");
        }

        /**
         * alarmのプロパティ名を返します。
         *
         * @return alarmのプロパティ名
         */
        public PropertyName<Boolean> alarm() {
            return new PropertyName<Boolean>(this, "alarm");
        }

        /**
         * viewのプロパティ名を返します。
         *
         * @return viewのプロパティ名
         */
        public PropertyName<Boolean> view() {
            return new PropertyName<Boolean>(this, "view");
        }

//        /**
//         * mapのプロパティ名を返します。
//         *
//         * @return mapのプロパティ名
//         */
//        public PropertyName<Boolean> map() {
//            return new PropertyName<Boolean>(this, "map");
//        }

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
         * meteotypeMasterのプロパティ名を返します。
         *
         * @return meteotypeMasterのプロパティ名
         */
        public _MeteotypeMasterNames meteotypeMaster() {
            return new _MeteotypeMasterNames(this, "meteotypeMaster");
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
         * meteotriggerInfoListのプロパティ名を返します。
         *
         * @return meteotriggerInfoListのプロパティ名
         */
        public _MeteotriggerInfoNames meteotriggerInfoList() {
            return new _MeteotriggerInfoNames(this, "meteotriggerInfoList");
        }
    }
}
