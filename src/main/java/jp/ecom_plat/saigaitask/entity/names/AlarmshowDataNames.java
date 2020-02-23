/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AlarmshowData;
import jp.ecom_plat.saigaitask.entity.names.AlarmmessageDataNames._AlarmmessageDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AlarmshowData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:30")
public class AlarmshowDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Alarm display data";
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
     * alarmmessagedataidのプロパティ名を返します。
     * 
     * @return alarmmessagedataidのプロパティ名
     */
    public static PropertyName<Long> alarmmessagedataid() {
        return new PropertyName<Long>("alarmmessagedataid");
    }

    /**
     * sessionidのプロパティ名を返します。
     * 
     * @return sessionidのプロパティ名
     */
    public static PropertyName<String> sessionid() {
        return new PropertyName<String>("sessionid");
    }

    /**
     * stopのプロパティ名を返します。
     * 
     * @return stopのプロパティ名
     */
    public static PropertyName<Boolean> stop() {
        return new PropertyName<Boolean>("stop");
    }

    /**
     * alarmmessageDataのプロパティ名を返します。
     * 
     * @return alarmmessageDataのプロパティ名
     */
    public static _AlarmmessageDataNames alarmmessageData() {
        return new _AlarmmessageDataNames("alarmmessageData");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _AlarmshowDataNames extends PropertyName<AlarmshowData> {

        /**
         * インスタンスを構築します。
         */
        public _AlarmshowDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _AlarmshowDataNames(final String name) {
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
        public _AlarmshowDataNames(final PropertyName<?> parent, final String name) {
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
         * alarmmessagedataidのプロパティ名を返します。
         *
         * @return alarmmessagedataidのプロパティ名
         */
        public PropertyName<Long> alarmmessagedataid() {
            return new PropertyName<Long>(this, "alarmmessagedataid");
        }

        /**
         * sessionidのプロパティ名を返します。
         *
         * @return sessionidのプロパティ名
         */
        public PropertyName<String> sessionid() {
            return new PropertyName<String>(this, "sessionid");
        }

        /**
         * stopのプロパティ名を返します。
         *
         * @return stopのプロパティ名
         */
        public PropertyName<Boolean> stop() {
            return new PropertyName<Boolean>(this, "stop");
        }

        /**
         * alarmmessageDataのプロパティ名を返します。
         * 
         * @return alarmmessageDataのプロパティ名
         */
        public _AlarmmessageDataNames alarmmessageData() {
            return new _AlarmmessageDataNames(this, "alarmmessageData");
        }
    }
}
