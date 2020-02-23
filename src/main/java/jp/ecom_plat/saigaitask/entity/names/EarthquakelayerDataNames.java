/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.EarthquakelayerData;
import jp.ecom_plat.saigaitask.entity.names.EarthquakegrouplayerDataNames._EarthquakegrouplayerDataNames;
import jp.ecom_plat.saigaitask.entity.names.EarthquakelayerInfoNames._EarthquakelayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteoDataNames._MeteoDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link EarthquakelayerData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/09/09 18:18:57")
public class EarthquakelayerDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Seismic intensity layer data";
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
     * meteodataidのプロパティ名を返します。
     *
     * @return meteodataidのプロパティ名
     */
    public static PropertyName<Long> meteodataid() {
        return new PropertyName<Long>("meteodataid");
    }

    /**
     * earthquakegrouplayeridのプロパティ名を返します。
     *
     * @return earthquakegrouplayeridのプロパティ名
     */
    @Deprecated
    public static PropertyName<Long> earthquakegrouplayerid() {
        return new PropertyName<Long>("earthquakegrouplayerid");
    }

    /**
     * eventidのプロパティ名を返します。
     *
     * @return eventidのプロパティ名
     */
    public static PropertyName<String> eventid() {
        return new PropertyName<String>("eventid");
    }

    /**
     * origintimeのプロパティ名を返します。
     *
     * @return origintimeのプロパティ名
     */
    public static PropertyName<Timestamp> origintime() {
        return new PropertyName<Timestamp>("origintime");
    }

    /**
     * reportdatetimeのプロパティ名を返します。
     *
     * @return reportdatetimeのプロパティ名
     */
    public static PropertyName<Timestamp> reportdatetime() {
        return new PropertyName<Timestamp>("reportdatetime");
    }

    /**
     * layeridのプロパティ名を返します。
     *
     * @return layeridのプロパティ名
     */
    public static PropertyName<String> layerid() {
        return new PropertyName<String>("layerid");
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
     * earthquakelayerinfoidのプロパティ名を返します。
     *
     * @return earthquakelayerinfoidのプロパティ名
     */
    public static PropertyName<Long> earthquakelayerinfoid() {
        return new PropertyName<Long>("earthquakelayerinfoid");
    }

    /**
     * meteoDataのプロパティ名を返します。
     *
     * @return meteoDataのプロパティ名
     */
    public static _MeteoDataNames meteoData() {
        return new _MeteoDataNames("meteoData");
    }

    /**
     * earthquakegrouplayerDataのプロパティ名を返します。
     *
     * @return earthquakegrouplayerDataのプロパティ名
     */
    @Deprecated
    public static _EarthquakegrouplayerDataNames earthquakegrouplayerData() {
        return new _EarthquakegrouplayerDataNames("earthquakegrouplayerData");
    }

    /**
     * earthquakelayerInfoのプロパティ名を返します。
     *
     * @return earthquakelayerInfoのプロパティ名
     */
    public static _EarthquakelayerInfoNames earthquakelayerInfo() {
        return new _EarthquakelayerInfoNames("earthquakelayerInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _EarthquakelayerDataNames extends PropertyName<EarthquakelayerData> {

        /**
         * インスタンスを構築します。
         */
        public _EarthquakelayerDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _EarthquakelayerDataNames(final String name) {
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
        public _EarthquakelayerDataNames(final PropertyName<?> parent, final String name) {
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
         * meteodataidのプロパティ名を返します。
         *
         * @return meteodataidのプロパティ名
         */
        public PropertyName<Long> meteodataid() {
            return new PropertyName<Long>(this, "meteodataid");
        }

        /**
         * earthquakegrouplayeridのプロパティ名を返します。
         *
         * @return earthquakegrouplayeridのプロパティ名
         */
        @Deprecated
        public PropertyName<Long> earthquakegrouplayerid() {
            return new PropertyName<Long>(this, "earthquakegrouplayerid");
        }

        /**
         * eventidのプロパティ名を返します。
         *
         * @return eventidのプロパティ名
         */
        public PropertyName<String> eventid() {
            return new PropertyName<String>(this, "eventid");
        }

        /**
         * origintimeのプロパティ名を返します。
         *
         * @return origintimeのプロパティ名
         */
        public PropertyName<Timestamp> origintime() {
            return new PropertyName<Timestamp>(this, "origintime");
        }

        /**
         * reportdatetimeのプロパティ名を返します。
         *
         * @return reportdatetimeのプロパティ名
         */
        public PropertyName<Timestamp> reportdatetime() {
            return new PropertyName<Timestamp>(this, "reportdatetime");
        }

        /**
         * layeridのプロパティ名を返します。
         *
         * @return layeridのプロパティ名
         */
        public PropertyName<String> layerid() {
            return new PropertyName<String>(this, "layerid");
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
         * earthquakelayerinfoidのプロパティ名を返します。
         *
         * @return earthquakelayerinfoidのプロパティ名
         */
        public PropertyName<Long> earthquakelayerinfoid() {
            return new PropertyName<Long>(this, "earthquakelayerinfoid");
        }

        /**
         * meteoDataのプロパティ名を返します。
         *
         * @return meteoDataのプロパティ名
         */
        public _MeteoDataNames meteoData() {
            return new _MeteoDataNames(this, "meteoData");
        }

        /**
         * earthquakegrouplayerDataのプロパティ名を返します。
         *
         * @return earthquakegrouplayerDataのプロパティ名
         */
        @Deprecated
        public _EarthquakegrouplayerDataNames earthquakegrouplayerData() {
            return new _EarthquakegrouplayerDataNames(this, "earthquakegrouplayerData");
        }

        /**
         * earthquakelayerInfoのプロパティ名を返します。
         *
         * @return earthquakelayerInfoのプロパティ名
         */
        public _EarthquakelayerInfoNames earthquakelayerInfo() {
            return new _EarthquakelayerInfoNames(this, "earthquakelayerInfo");
        }
    }
}
