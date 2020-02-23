/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ImporttablemasterData;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ImporttablemasterData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/11/18 16:09:01")
public class ImporttablemasterDataNames implements EntityNameInterface {
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	@Override
	public String entityName() {
		return ("Import table master info");
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
     * tablemasterinfoidのプロパティ名を返します。
     * 
     * @return tablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> tablemasterinfoid() {
        return new PropertyName<Long>("tablemasterinfoid");
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
    public static PropertyName<Boolean> copy() {
        return new PropertyName<Boolean>("copy");
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
     * @author S2JDBC-Gen
     */
    public static class _ImporttablemasterDataNames extends PropertyName<ImporttablemasterData> {

        /**
         * インスタンスを構築します。
         */
        public _ImporttablemasterDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _ImporttablemasterDataNames(final String name) {
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
        public _ImporttablemasterDataNames(final PropertyName<?> parent, final String name) {
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
         * tablemasterinfoidのプロパティ名を返します。
         *
         * @return tablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> tablemasterinfoid() {
            return new PropertyName<Long>(this, "tablemasterinfoid");
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
        public PropertyName<Boolean> copy() {
            return new PropertyName<Boolean>(this, "copy");
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
    }
}
