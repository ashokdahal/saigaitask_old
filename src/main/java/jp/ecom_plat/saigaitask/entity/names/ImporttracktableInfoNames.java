/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ImporttracktableInfo;
import jp.ecom_plat.saigaitask.entity.names.ImporttrackInfoNames._ImporttrackInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ImporttracktableInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/02/20 18:46:19")
public class ImporttracktableInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Communication disconnected import table data";
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
     * tracktableinfoidのプロパティ名を返します。
     *
     * @return tracktableinfoidのプロパティ名
     */
    public static PropertyName<Long> tracktableinfoid() {
        return new PropertyName<Long>("tracktableinfoid");
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
     * oldlayeridのプロパティ名を返します。
     *
     * @return oldlayeridのプロパティ名
     */
    public static PropertyName<String> oldlayerid() {
        return new PropertyName<String>("oldlayerid");
    }

    /**
     * importtrackinfoidのプロパティ名を返します。
     *
     * @return importtrackinfoidのプロパティ名
     */
    public static PropertyName<Long> importtrackinfoid() {
        return new PropertyName<Long>("importtrackinfoid");
    }

    /**
     * importtrackInfoのプロパティ名を返します。
     *
     * @return importtrackInfoのプロパティ名
     */
    public static _ImporttrackInfoNames importtrackInfo() {
        return new _ImporttrackInfoNames("importtrackInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _ImporttracktableInfoNames extends PropertyName<ImporttracktableInfo> {

        /**
         * インスタンスを構築します。
         */
        public _ImporttracktableInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _ImporttracktableInfoNames(final String name) {
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
        public _ImporttracktableInfoNames(final PropertyName<?> parent, final String name) {
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
         * tracktableinfoidのプロパティ名を返します。
         *
         * @return tracktableinfoidのプロパティ名
         */
        public PropertyName<Long> tracktableinfoid() {
            return new PropertyName<Long>(this, "tracktableinfoid");
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
         * oldlayeridのプロパティ名を返します。
         *
         * @return oldlayeridのプロパティ名
         */
        public PropertyName<String> oldlayerid() {
            return new PropertyName<String>(this, "oldlayerid");
        }

        /**
         * importtrackinfoidのプロパティ名を返します。
         *
         * @return importtrackinfoidのプロパティ名
         */
        public PropertyName<Long> importtrackinfoid() {
            return new PropertyName<Long>(this, "importtrackinfoid");
        }

        /**
         * importtrackInfosのプロパティ名を返します。
         *
         * @return importtrackInfosのプロパティ名
         */
        public _ImporttrackInfoNames importtrackInfo() {
            return new _ImporttrackInfoNames(this, "importtrackInfo");
        }
    }
}
