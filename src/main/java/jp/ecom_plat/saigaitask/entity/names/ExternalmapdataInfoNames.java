/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ExternalmapdataInfo;
import jp.ecom_plat.saigaitask.entity.names.AuthorizationInfoNames._AuthorizationInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ExternaltabledataInfoNames._ExternaltabledataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MenuInfoNames._MenuInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ExternalmapdataInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/03/12 18:27:00")
public class ExternalmapdataInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "External map data info";
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
     * menuinfoidのプロパティ名を返します。
     *
     * @return menuinfoidのプロパティ名
     */
    public static PropertyName<Long> menuinfoid() {
        return new PropertyName<Long>("menuinfoid");
    }

    /**
     * metadataidのプロパティ名を返します。
     *
     * @return metadataidのプロパティ名
     */
    public static PropertyName<String> metadataid() {
        return new PropertyName<String>("metadataid");
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
     * filteridのプロパティ名を返します。
     *
     * @return filteridのプロパティ名
     */
    public static PropertyName<Long> filterid() {
        return new PropertyName<Long>("filterid");
    }

    /**
     * visibleのプロパティ名を返します。
     *
     * @return visibleのプロパティ名
     */
    public static PropertyName<Boolean> visible() {
        return new PropertyName<Boolean>("visible");
    }

    /**
     * closedのプロパティ名を返します。
     *
     * @return closedのプロパティ名
     */
    public static PropertyName<Boolean> closed() {
        return new PropertyName<Boolean>("closed");
    }

    /**
     * searchableのプロパティ名を返します。
     *
     * @return searchableのプロパティ名
     */
    public static PropertyName<Boolean> searchable() {
        return new PropertyName<Boolean>("searchable");
    }

    /**
     * disporderのプロパティ名を返します。
     *
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
    }

    /**
     * layerparentのプロパティ名を返します。
     *
     * @return layerparentのプロパティ名
     */
    public static PropertyName<Long> layerparent() {
        return new PropertyName<Long>("layerparent");
    }

    /**
     * attributionのプロパティ名を返します。
     *
     * @return attributionのプロパティ名
     */
    public static PropertyName<String> attribution() {
        return new PropertyName<String>("attribution");
    }

    /**
     * layeropacityのプロパティ名を返します。
     *
     * @return layeropacityのプロパティ名
     */
    public static PropertyName<Double> layeropacity() {
        return new PropertyName<Double>("layeropacity");
    }

    /**
     * wmscapsurlのプロパティ名を返します。
     *
     * @return wmscapsurlのプロパティ名
     */
    public static PropertyName<String> wmscapsurl() {
        return new PropertyName<String>("wmscapsurl");
    }

    /**
     * wmsurlのプロパティ名を返します。
     *
     * @return wmsurlのプロパティ名
     */
    public static PropertyName<String> wmsurl() {
        return new PropertyName<String>("wmsurl");
    }

    /**
     * wmsformatのプロパティ名を返します。
     *
     * @return wmsformatのプロパティ名
     */
    public static PropertyName<String> wmsformat() {
        return new PropertyName<String>("wmsformat");
    }

    /**
     * wmslegendurlのプロパティ名を返します。
     *
     * @return wmslegendurlのプロパティ名
     */
    public static PropertyName<String> wmslegendurl() {
        return new PropertyName<String>("wmslegendurl");
    }

    /**
     * wmsfeatureurlのプロパティ名を返します。
     *
     * @return wmsfeatureurlのプロパティ名
     */
    public static PropertyName<String> wmsfeatureurl() {
        return new PropertyName<String>("wmsfeatureurl");
    }

    /**
     * featuretypeidのプロパティ名を返します。
     *
     * @return featuretypeidのプロパティ名
     */
    public static PropertyName<String> featuretypeid() {
        return new PropertyName<String>("featuretypeid");
    }

    /**
     * layerdescriptionのプロパティ名を返します。
     *
     * @return layerdescriptionのプロパティ名
     */
    public static PropertyName<String> layerdescription() {
        return new PropertyName<String>("layerdescription");
    }

    /**
     * authorizationinfoidのプロパティ名を返します。
     *
     * @return authorizationinfoidのプロパティ名
     */
    public static PropertyName<Long> authorizationinfoid() {
        return new PropertyName<Long>("authorizationinfoid");
    }

    /**
     * menuInfoのプロパティ名を返します。
     *
     * @return menuInfoのプロパティ名
     */
    public static _MenuInfoNames menuInfo() {
        return new _MenuInfoNames("menuInfo");
    }

    /**
     * authorizationInfoのプロパティ名を返します。
     *
     * @return menuInfoのプロパティ名
     */
    public static _AuthorizationInfoNames authorizationInfo() {
        return new _AuthorizationInfoNames("authorizationInfo");
    }

    /**
     * externaltabledataInfoのプロパティ名を返します。
     *
     * @return externaltabledataInfoのプロパティ名
     */
    public static _ExternaltabledataInfoNames externaltabledataInfo() {
        return new _ExternaltabledataInfoNames("externaltabledataInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _ExternalmapdataInfoNames extends PropertyName<ExternalmapdataInfo> {

        /**
         * インスタンスを構築します。
         */
        public _ExternalmapdataInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _ExternalmapdataInfoNames(final String name) {
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
        public _ExternalmapdataInfoNames(final PropertyName<?> parent, final String name) {
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
         * menuinfoidのプロパティ名を返します。
         *
         * @return menuinfoidのプロパティ名
         */
        public PropertyName<Long> menuinfoid() {
            return new PropertyName<Long>(this, "menuinfoid");
        }

        /**
         * metadataidのプロパティ名を返します。
         *
         * @return metadataidのプロパティ名
         */
        public PropertyName<String> metadataid() {
            return new PropertyName<String>(this, "metadataid");
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
         * filteridのプロパティ名を返します。
         *
         * @return filteridのプロパティ名
         */
        public PropertyName<Long> filterid() {
            return new PropertyName<Long>(this, "filterid");
        }

        /**
         * visibleのプロパティ名を返します。
         *
         * @return visibleのプロパティ名
         */
        public PropertyName<Boolean> visible() {
            return new PropertyName<Boolean>(this, "visible");
        }

        /**
         * closedのプロパティ名を返します。
         *
         * @return closedのプロパティ名
         */
        public PropertyName<Boolean> closed() {
            return new PropertyName<Boolean>(this, "closed");
        }

        /**
         * searchableのプロパティ名を返します。
         *
         * @return searchableのプロパティ名
         */
        public PropertyName<Boolean> searchable() {
            return new PropertyName<Boolean>(this, "searchable");
        }

        /**
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
        }

        /**
         * layerparentのプロパティ名を返します。
         *
         * @return layerparentのプロパティ名
         */
        public PropertyName<Long> layerparent() {
            return new PropertyName<Long>(this, "layerparent");
        }

        /**
         * attributionのプロパティ名を返します。
         *
         * @return attributionのプロパティ名
         */
        public PropertyName<String> attribution() {
            return new PropertyName<String>(this, "attribution");
        }

        /**
         * layeropacityのプロパティ名を返します。
         *
         * @return layeropacityのプロパティ名
         */
        public PropertyName<Double> layeropacity() {
            return new PropertyName<Double>(this, "layeropacity");
        }

        /**
         * wmscapsurlのプロパティ名を返します。
         *
         * @return wmscapsurlのプロパティ名
         */
        public PropertyName<String> wmscapsurl() {
            return new PropertyName<String>(this, "wmscapsurl");
        }

        /**
         * wmsurlのプロパティ名を返します。
         *
         * @return wmsurlのプロパティ名
         */
        public PropertyName<String> wmsurl() {
            return new PropertyName<String>(this, "wmsurl");
        }

        /**
         * wmsformatのプロパティ名を返します。
         *
         * @return wmsformatのプロパティ名
         */
        public PropertyName<String> wmsformat() {
            return new PropertyName<String>(this, "wmsformat");
        }

        /**
         * wmslegendurlのプロパティ名を返します。
         *
         * @return wmslegendurlのプロパティ名
         */
        public PropertyName<String> wmslegendurl() {
            return new PropertyName<String>(this, "wmslegendurl");
        }

        /**
         * wmsfeatureurlのプロパティ名を返します。
         *
         * @return wmsfeatureurlのプロパティ名
         */
        public PropertyName<String> wmsfeatureurl() {
            return new PropertyName<String>(this, "wmsfeatureurl");
        }

        /**
         * featuretypeidのプロパティ名を返します。
         *
         * @return featuretypeidのプロパティ名
         */
        public PropertyName<String> featuretypeid() {
            return new PropertyName<String>(this, "featuretypeid");
        }

        /**
         * layerdescriptionのプロパティ名を返します。
         *
         * @return layerdescriptionのプロパティ名
         */
        public PropertyName<String> layerdescription() {
            return new PropertyName<String>(this, "layerdescription");
        }

        /**
         * authorizationinfoidのプロパティ名を返します。
         *
         * @return authorizationinfoidのプロパティ名
         */
        public PropertyName<String> authorizationinfoid() {
            return new PropertyName<String>(this, "authorizationinfoid");
        }

        /**
         * menuInfoのプロパティ名を返します。
         *
         * @return menuInfoのプロパティ名
         */
        public _MenuInfoNames menuInfo() {
            return new _MenuInfoNames(this, "menuInfo");
        }

        /**
         * authorizationInfoのプロパティ名を返します。
         *
         * @return authorizationInfoのプロパティ名
         */
        public _AuthorizationInfoNames authorizationInfo() {
            return new _AuthorizationInfoNames(this, "authorizationInfo");
        }

        /**
         * externaltabledataInfoのプロパティ名を返します。
         *
         * @return externaltabledataInfoのプロパティ名
         */
        public _ExternaltabledataInfoNames externaltabledataInfo() {
            return new _ExternaltabledataInfoNames(this, "externaltabledataInfo");
        }
    }
}
