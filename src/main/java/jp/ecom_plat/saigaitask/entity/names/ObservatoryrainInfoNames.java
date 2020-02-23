/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ObservatoryrainInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ObservatoryrainInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/24 19:26:03")
public class ObservatoryrainInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Rainfall observation point info";
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
     * areacodeのプロパティ名を返します。
     *
     * @return areacodeのプロパティ名
     */
    public static PropertyName<Integer> areacode() {
        return new PropertyName<Integer>("areacode");
    }

    /**
     * officecodeのプロパティ名を返します。
     *
     * @return officecodeのプロパティ名
     */
    public static PropertyName<Integer> officecode() {
        return new PropertyName<Integer>("officecode");
    }

    /**
     * officenameのプロパティ名を返します。
     *
     * @return officenameのプロパティ名
     */
    public static PropertyName<String> officename() {
        return new PropertyName<String>("officename");
    }

    /**
     * jurisfacilitycodeのプロパティ名を返します。
     *
     * @return jurisfacilitycodeのプロパティ名
     */
    public static PropertyName<Integer> jurisfacilitycode() {
        return new PropertyName<Integer>("jurisfacilitycode");
    }

    /**
     * jurisfacilitynameのプロパティ名を返します。
     *
     * @return jurisfacilitynameのプロパティ名
     */
    public static PropertyName<String> jurisfacilityname() {
        return new PropertyName<String>("jurisfacilityname");
    }

    /**
     * basinのプロパティ名を返します。
     *
     * @return basinのプロパティ名
     */
    public static PropertyName<String> basin() {
        return new PropertyName<String>("basin");
    }

    /**
     * riverのプロパティ名を返します。
     *
     * @return riverのプロパティ名
     */
    public static PropertyName<String> river() {
        return new PropertyName<String>("river");
    }

    /**
     * obsrvtncodeのプロパティ名を返します。
     *
     * @return obsrvtncodeのプロパティ名
     */
    public static PropertyName<Integer> obsrvtncode() {
        return new PropertyName<Integer>("obsrvtncode");
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
     * readnameのプロパティ名を返します。
     *
     * @return readnameのプロパティ名
     */
    public static PropertyName<String> readname() {
        return new PropertyName<String>("readname");
    }

    /**
     * latitudeのプロパティ名を返します。
     *
     * @return latitudeのプロパティ名
     */
    public static PropertyName<Double> latitude() {
        return new PropertyName<Double>("latitude");
    }

    /**
     * longitudeのプロパティ名を返します。
     *
     * @return longitudeのプロパティ名
     */
    public static PropertyName<Double> longitude() {
        return new PropertyName<Double>("longitude");
    }

    /**
     * altitudeのプロパティ名を返します。
     *
     * @return altitudeのプロパティ名
     */
    public static PropertyName<String> altitude() {
        return new PropertyName<String>("altitude");
    }

    /**
     * prefnameのプロパティ名を返します。
     *
     * @return prefnameのプロパティ名
     */
    public static PropertyName<String> prefname() {
        return new PropertyName<String>("prefname");
    }

    /**
     * addressのプロパティ名を返します。
     *
     * @return addressのプロパティ名
     */
    public static PropertyName<String> address() {
        return new PropertyName<String>("address");
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
     * featureidのプロパティ名を返します。
     *
     * @return featureidのプロパティ名
     */
    public static PropertyName<Long> featureid() {
        return new PropertyName<Long>("featureid");
    }

    /**
     * iframeのプロパティ名を返します。
     *
     * @return iframeのプロパティ名
     */
    public static PropertyName<Boolean> iframe() {
        return new PropertyName<Boolean>("iframe");
    }

    /**
     * urlのプロパティ名を返します。
     *
     * @return urlのプロパティ名
     */
    public static PropertyName<String> url() {
        return new PropertyName<String>("url");
    }

    /**
     * widthのプロパティ名を返します。
     *
     * @return widthのプロパティ名
     */
    public static PropertyName<Integer> width() {
        return new PropertyName<Integer>("width");
    }

    /**
     * heightのプロパティ名を返します。
     *
     * @return heightのプロパティ名
     */
    public static PropertyName<Integer> height() {
        return new PropertyName<Integer>("height");
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
     * localgovInfoのプロパティ名を返します。
     *
     * @return localgovInfoのプロパティ名
     */
    public static _LocalgovInfoNames localgovInfo() {
        return new _LocalgovInfoNames("localgovInfo");
    }

    /**
     * tablemasterInfoのプロパティ名を返します。
     *
     * @return tablemasterInfoのプロパティ名
     */
    public static _TablemasterInfoNames tablemasterInfo() {
        return new _TablemasterInfoNames("tablemasterInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _ObservatoryrainInfoNames extends PropertyName<ObservatoryrainInfo> {

        /**
         * インスタンスを構築します。
         */
        public _ObservatoryrainInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _ObservatoryrainInfoNames(final String name) {
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
        public _ObservatoryrainInfoNames(final PropertyName<?> parent, final String name) {
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
         * areacodeのプロパティ名を返します。
         *
         * @return areacodeのプロパティ名
         */
        public PropertyName<Integer> areacode() {
            return new PropertyName<Integer>(this, "areacode");
        }

        /**
         * officecodeのプロパティ名を返します。
         *
         * @return officecodeのプロパティ名
         */
        public PropertyName<Integer> officecode() {
            return new PropertyName<Integer>(this, "officecode");
        }

        /**
         * officenameのプロパティ名を返します。
         *
         * @return officenameのプロパティ名
         */
        public PropertyName<String> officename() {
            return new PropertyName<String>(this, "officename");
        }

        /**
         * jurisfacilitycodeのプロパティ名を返します。
         *
         * @return jurisfacilitycodeのプロパティ名
         */
        public PropertyName<Integer> jurisfacilitycode() {
            return new PropertyName<Integer>(this, "jurisfacilitycode");
        }

        /**
         * jurisfacilitynameのプロパティ名を返します。
         *
         * @return jurisfacilitynameのプロパティ名
         */
        public PropertyName<String> jurisfacilityname() {
            return new PropertyName<String>(this, "jurisfacilityname");
        }

        /**
         * basinのプロパティ名を返します。
         *
         * @return basinのプロパティ名
         */
        public PropertyName<String> basin() {
            return new PropertyName<String>(this, "basin");
        }

        /**
         * riverのプロパティ名を返します。
         *
         * @return riverのプロパティ名
         */
        public PropertyName<String> river() {
            return new PropertyName<String>(this, "river");
        }

        /**
         * obsrvtncodeのプロパティ名を返します。
         *
         * @return obsrvtncodeのプロパティ名
         */
        public PropertyName<Integer> obsrvtncode() {
            return new PropertyName<Integer>(this, "obsrvtncode");
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
         * readnameのプロパティ名を返します。
         *
         * @return readnameのプロパティ名
         */
        public PropertyName<String> readname() {
            return new PropertyName<String>(this, "readname");
        }

        /**
         * latitudeのプロパティ名を返します。
         *
         * @return latitudeのプロパティ名
         */
        public PropertyName<Double> latitude() {
            return new PropertyName<Double>(this, "latitude");
        }

        /**
         * longitudeのプロパティ名を返します。
         *
         * @return longitudeのプロパティ名
         */
        public PropertyName<Double> longitude() {
            return new PropertyName<Double>(this, "longitude");
        }

        /**
         * altitudeのプロパティ名を返します。
         *
         * @return altitudeのプロパティ名
         */
        public PropertyName<String> altitude() {
            return new PropertyName<String>(this, "altitude");
        }

        /**
         * prefnameのプロパティ名を返します。
         *
         * @return prefnameのプロパティ名
         */
        public PropertyName<String> prefname() {
            return new PropertyName<String>(this, "prefname");
        }

        /**
         * addressのプロパティ名を返します。
         *
         * @return addressのプロパティ名
         */
        public PropertyName<String> address() {
            return new PropertyName<String>(this, "address");
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
         * featureidのプロパティ名を返します。
         *
         * @return featureidのプロパティ名
         */
        public PropertyName<Long> featureid() {
            return new PropertyName<Long>(this, "featureid");
        }

        /**
         * iframeのプロパティ名を返します。
         *
         * @return iframeのプロパティ名
         */
        public PropertyName<Boolean> iframe() {
            return new PropertyName<Boolean>(this, "iframe");
        }

        /**
         * urlのプロパティ名を返します。
         *
         * @return urlのプロパティ名
         */
        public PropertyName<String> url() {
            return new PropertyName<String>(this, "url");
        }

        /**
         * widthのプロパティ名を返します。
         *
         * @return widthのプロパティ名
         */
        public PropertyName<Integer> width() {
            return new PropertyName<Integer>(this, "width");
        }

        /**
         * heightのプロパティ名を返します。
         *
         * @return heightのプロパティ名
         */
        public PropertyName<Integer> height() {
            return new PropertyName<Integer>(this, "height");
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
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * tablemasterInfoのプロパティ名を返します。
         *
         * @return tablemasterInfoのプロパティ名
         */
        public _TablemasterInfoNames tablemasterInfo() {
            return new _TablemasterInfoNames(this, "tablemasterInfo");
        }
    }
}
