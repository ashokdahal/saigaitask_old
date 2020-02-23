/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.names.AutocompleteInfoNames._AutocompleteInfoNames;
import jp.ecom_plat.saigaitask.entity.names.ClearinghousemetadataInfoNames._ClearinghousemetadataInfoNames;
import jp.ecom_plat.saigaitask.entity.names.JsonimportlayerInfoNames._JsonimportlayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MaplayerInfoNames._MaplayerInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MapmasterInfoNames._MapmasterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportRefugeInfoNames._PubliccommonsReportRefugeInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportShelterInfoNames._PubliccommonsReportShelterInfoNames;
import jp.ecom_plat.saigaitask.entity.names.StationalarmInfoNames._StationalarmInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TablecalculatecolumnInfoNames._TablecalculatecolumnInfoNames;
import jp.ecom_plat.saigaitask.entity.names.TracktableInfoNames._TracktableInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TablemasterInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/23 16:58:46")
public class TablemasterInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Table master info";
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
     * tablenameのプロパティ名を返します。
     *
     * @return tablenameのプロパティ名
     */
    public static PropertyName<String> tablename() {
        return new PropertyName<String>("tablename");
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
     * geometrytypeのプロパティ名を返します。
     *
     * @return geometrytypeのプロパティ名
     */
    public static PropertyName<String> geometrytype() {
        return new PropertyName<String>("geometrytype");
    }

    /**
     * copyのプロパティ名を返します。
     *
     * @return copyのプロパティ名
     */
    public static PropertyName<Short> copy() {
        return new PropertyName<Short>("copy");
    }

    /**
     * addresscolumnのプロパティ名を返します。
     *
     * @return addresscolumnのプロパティ名
     */
    public static PropertyName<String> addresscolumn() {
        return new PropertyName<String>("addresscolumn");
    }

    /**
     * updatecolumnのプロパティ名を返します。
     *
     * @return updatecolumnのプロパティ名
     */
    public static PropertyName<String> updatecolumn() {
        return new PropertyName<String>("updatecolumn");
    }

    /**
     * coordinatecolumnのプロパティ名を返します。
     *
     * @return coordinatecolumnのプロパティ名
     */
    public static PropertyName<String> coordinatecolumn() {
        return new PropertyName<String>("coordinatecolumn");
    }

    /**
     * mgrscolumnのプロパティ名を返します。
     *
     * @return mgrscolumnのプロパティ名
     */
    public static PropertyName<String> mgrscolumn() {
        return new PropertyName<String>("mgrscolumn");
    }

    /**
     * mgrsdigitのプロパティ名を返します。
     *
     * @return mgrsdigitのプロパティ名
     */
    public static PropertyName<Integer> mgrsdigit() {
        return new PropertyName<Integer>("mgrsdigit");
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
     * deletedのプロパティ名を返します。
     *
     * @return deletedのプロパティ名
     */
    public static PropertyName<Boolean> deleted() {
        return new PropertyName<Boolean>("deleted");
    }

    /**
     * resetのプロパティ名を返します。
     *
     * @return resetのプロパティ名
     */
    public static PropertyName<Boolean> reset() {
        return new PropertyName<Boolean>("reset");
    }

    /**
     * maplayerInfosのプロパティ名を返します。
     *
     * @return maplayerInfosのプロパティ名
     */
    public static _MaplayerInfoNames maplayerInfos() {
        return new _MaplayerInfoNames("maplayerInfos");
    }

    /**
     * tracktableInfosのプロパティ名を返します。
     *
     * @return tracktableInfosのプロパティ名
     */
    public static _TracktableInfoNames tracktableInfos() {
        return new _TracktableInfoNames("tracktableInfos");
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
     * publiccommonsReportRefugeInfoListのプロパティ名を返します。
     *
     * @return publiccommonsReportRefugeInfoListのプロパティ名
     */
    public static _PubliccommonsReportRefugeInfoNames publiccommonsReportRefugeInfoList() {
        return new _PubliccommonsReportRefugeInfoNames("publiccommonsReportRefugeInfoList");
    }

    /**
     * publiccommonsReportShelterInfoListのプロパティ名を返します。
     *
     * @return publiccommonsReportShelterInfoListのプロパティ名
     */
    public static _PubliccommonsReportShelterInfoNames publiccommonsReportShelterInfoList() {
        return new _PubliccommonsReportShelterInfoNames("publiccommonsReportShelterInfoList");
    }

    /**
     * clearinghousemetadataInfoListのプロパティ名を返します。
     *
     * @return clearinghousemetadataInfoListのプロパティ名
     */
    public static _ClearinghousemetadataInfoNames clearinghousemetadataInfoList() {
        return new _ClearinghousemetadataInfoNames("clearinghousemetadataInfoList");
    }

    /**
     * stationalarmInfoListのプロパティ名を返します。
     *
     * @return stationalarmInfoListのプロパティ名
     */
    public static _StationalarmInfoNames stationalarmInfoList() {
        return new _StationalarmInfoNames("stationalarmInfoList");
    }

    /**
     * autocompleteInfoListのプロパティ名を返します。
     *
     * @return autocompleteInfoListのプロパティ名
     */
    public static _AutocompleteInfoNames autocompleteInfoList() {
        return new _AutocompleteInfoNames("autocompleteInfoList");
    }

    /**
     * tablecalculatecolumnInfoListのプロパティ名を返します。
     *
     * @return tablecalculatecolumnInfoListのプロパティ名
     */
    public static _TablecalculatecolumnInfoNames tablecalculatecolumnInfoList() {
        return new _TablecalculatecolumnInfoNames("tablecalculatecolumnInfoList");
    }

    /**
     * jsonimportlayerInfoListのプロパティ名を返します。
     *
     * @return jsonimportlayerInfoListのプロパティ名
     */
    public static _JsonimportlayerInfoNames jsonimportlayerInfoList() {
        return new _JsonimportlayerInfoNames("jsonimportlayerInfoList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TablemasterInfoNames extends PropertyName<TablemasterInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TablemasterInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TablemasterInfoNames(final String name) {
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
        public _TablemasterInfoNames(final PropertyName<?> parent, final String name) {
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
         * tablenameのプロパティ名を返します。
         *
         * @return tablenameのプロパティ名
         */
        public PropertyName<String> tablename() {
            return new PropertyName<String>(this, "tablename");
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
         * geometrytypeのプロパティ名を返します。
         *
         * @return geometrytypeのプロパティ名
         */
        public PropertyName<String> geometrytype() {
            return new PropertyName<String>(this, "geometrytype");
        }

        /**
         * copyのプロパティ名を返します。
         *
         * @return copyのプロパティ名
         */
        public PropertyName<Short> copy() {
            return new PropertyName<Short>(this, "copy");
        }

        /**
         * addresscolumnのプロパティ名を返します。
         *
         * @return addresscolumnのプロパティ名
         */
        public PropertyName<String> addresscolumn() {
            return new PropertyName<String>(this, "addresscolumn");
        }

        /**
         * updatecolumnのプロパティ名を返します。
         *
         * @return updatecolumnのプロパティ名
         */
        public PropertyName<String> updatecolumn() {
            return new PropertyName<String>(this, "updatecolumn");
        }

        /**
         * coordinatecolumnのプロパティ名を返します。
         *
         * @return coordinatecolumnのプロパティ名
         */
        public PropertyName<String> coordinatecolumn() {
            return new PropertyName<String>(this, "coordinatecolumn");
        }

        /**
         * mgrscolumnのプロパティ名を返します。
         *
         * @return mgrscolumnのプロパティ名
         */
        public PropertyName<String> mgrscolumn() {
            return new PropertyName<String>(this, "mgrscolumn");
        }

        /**
         * mgrsdigitのプロパティ名を返します。
         *
         * @return mgrsdigitのプロパティ名
         */
        public PropertyName<Integer> mgrsdigit() {
            return new PropertyName<Integer>(this, "mgrsdigit");
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
         * deletedのプロパティ名を返します。
         *
         * @return deletedのプロパティ名
         */
        public PropertyName<Boolean> deleted() {
            return new PropertyName<Boolean>(this, "deleted");
        }

        /**
         * resetのプロパティ名を返します。
         *
         * @return resetのプロパティ名
         */
        public PropertyName<Boolean> reset() {
            return new PropertyName<Boolean>(this, "reset");
        }

        /**
         * maplayerInfosのプロパティ名を返します。
         *
         * @return maplayerInfosのプロパティ名
         */
        public _MaplayerInfoNames maplayerInfos() {
            return new _MaplayerInfoNames(this, "maplayerInfos");
        }

        /**
         * tracktableInfosのプロパティ名を返します。
         *
         * @return tracktableInfosのプロパティ名
         */
        public _TracktableInfoNames tracktableInfos() {
            return new _TracktableInfoNames(this, "tracktableInfos");
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
         * publiccommonsReportRefugeInfoListのプロパティ名を返します。
         *
         * @return publiccommonsReportRefugeInfoListのプロパティ名
         */
        public _PubliccommonsReportRefugeInfoNames publiccommonsReportRefugeInfoList() {
            return new _PubliccommonsReportRefugeInfoNames(this, "publiccommonsReportRefugeInfoList");
        }

        /**
         * publiccommonsReportShelterInfoListのプロパティ名を返します。
         *
         * @return publiccommonsReportShelterInfoListのプロパティ名
         */
        public _PubliccommonsReportShelterInfoNames publiccommonsReportShelterInfoList() {
            return new _PubliccommonsReportShelterInfoNames(this, "publiccommonsReportShelterInfoList");
        }

        /**
         * clearinghousemetadataInfoListのプロパティ名を返します。
         *
         * @return clearinghousemetadataInfoListのプロパティ名
         */
        public _ClearinghousemetadataInfoNames clearinghousemetadataInfoList() {
            return new _ClearinghousemetadataInfoNames(this, "clearinghousemetadataInfoList");
        }

        /**
         * stationalarmInfoListのプロパティ名を返します。
         *
         * @return stationalarmInfoListのプロパティ名
         */
        public _StationalarmInfoNames stationalarmInfoList() {
            return new _StationalarmInfoNames(this, "stationalarmInfoList");
        }

        /**
         * autocompleteInfoListのプロパティ名を返します。
         *
         * @return autocompleteInfoListのプロパティ名
         */
        public _AutocompleteInfoNames autocompleteInfoList() {
            return new _AutocompleteInfoNames(this, "autocompleteInfoList");
        }

        /**
         * tablecalculatecolumnInfoListのプロパティ名を返します。
         *
         * @return tablecalculatecolumnInfoListのプロパティ名
         */
        public _TablecalculatecolumnInfoNames tablecalculatecolumnInfoList() {
            return new _TablecalculatecolumnInfoNames(this, "tablecalculatecolumnInfoList");
        }

        /**
         * jsonimportlayerInfoListのプロパティ名を返します。
         *
         * @return jsonimportlayerInfoListのプロパティ名
         */
        public _JsonimportlayerInfoNames jsonimportlayerInfoList() {
            return new _JsonimportlayerInfoNames(this, "jsonimportlayerInfoList");
        }
    }
}
