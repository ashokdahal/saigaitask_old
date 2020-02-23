/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.EarthquakegrouplayerData;
import jp.ecom_plat.saigaitask.entity.names.EarthquakelayerDataNames._EarthquakelayerDataNames;
import jp.ecom_plat.saigaitask.entity.names.MapmasterInfoNames._MapmasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TrackmapInfoNames._TrackmapInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link EarthquakegrouplayerData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/09/09 18:18:57")
public class EarthquakegrouplayerDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Seismic intensity group layer data";
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
     * mapmasterinfoidのプロパティ名を返します。
     *
     * @return mapmasterinfoidのプロパティ名
     */
    public static PropertyName<Long> mapmasterinfoid() {
        return new PropertyName<Long>("mapmasterinfoid");
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
     * trackmapInfoのプロパティ名を返します。
     *
     * @return trackmapInfoのプロパティ名
     */
    public static _TrackmapInfoNames trackmapInfo() {
        return new _TrackmapInfoNames("trackmapInfo");
    }

    /**
     * mapmasterInfoのプロパティ名を返します。
     *
     * @return mapmasterInfoのプロパティ名
     */
    public static _MapmasterInfoNames mapmasterInfo() {
        return new _MapmasterInfoNames("mapmasterInfo");
    }

    /**
     * earthquakelayerDatasのプロパティ名を返します。
     *
     * @return earthquakelayerDatasのプロパティ名
     */
    public static _EarthquakelayerDataNames earthquakelayerDatas() {
        return new _EarthquakelayerDataNames("earthquakelayerDatas");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _EarthquakegrouplayerDataNames extends PropertyName<EarthquakegrouplayerData> {

        /**
         * インスタンスを構築します。
         */
        public _EarthquakegrouplayerDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _EarthquakegrouplayerDataNames(final String name) {
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
        public _EarthquakegrouplayerDataNames(final PropertyName<?> parent, final String name) {
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
         * mapmasterinfoidのプロパティ名を返します。
         *
         * @return mapmasterinfoidのプロパティ名
         */
        public PropertyName<Long> mapmasterinfoid() {
            return new PropertyName<Long>(this, "mapmasterinfoid");
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
         * trackmapInfoのプロパティ名を返します。
         *
         * @return trackmapInfoのプロパティ名
         */
        public _TrackmapInfoNames trackmapInfo() {
            return new _TrackmapInfoNames(this, "trackmapInfo");
        }

        /**
         * mapmasterInfoのプロパティ名を返します。
         *
         * @return mapmasterInfoのプロパティ名
         */
        public _MapmasterInfoNames mapmasterInfo() {
            return new _MapmasterInfoNames(this, "mapmasterInfo");
        }

        /**
         * earthquakelayerDatasのプロパティ名を返します。
         *
         * @return earthquakelayerDatasのプロパティ名
         */
        public _EarthquakelayerDataNames earthquakelayerDatas() {
            return new _EarthquakelayerDataNames(this, "earthquakelayerDatas");
        }
    }
}
