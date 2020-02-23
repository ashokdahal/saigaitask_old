/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AutocompleteData;
import jp.ecom_plat.saigaitask.entity.names.AutocompleteInfoNames._AutocompleteInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AutocompleteData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/18 11:50:44")
public class AutocompleteDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Input complimentary data";
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
     * autocompleteinfoidのプロパティ名を返します。
     * 
     * @return autocompleteinfoidのプロパティ名
     */
    public static PropertyName<Long> autocompleteinfoid() {
        return new PropertyName<Long>("autocompleteinfoid");
    }

    /**
     * stringのプロパティ名を返します。
     * 
     * @return stringのプロパティ名
     */
    public static PropertyName<String> string() {
        return new PropertyName<String>("string");
    }

    /**
     * countのプロパティ名を返します。
     * 
     * @return countのプロパティ名
     */
    public static PropertyName<Integer> count() {
        return new PropertyName<Integer>("count");
    }

    /**
     * lasttimeのプロパティ名を返します。
     * 
     * @return lasttimeのプロパティ名
     */
    public static PropertyName<Timestamp> lasttime() {
        return new PropertyName<Timestamp>("lasttime");
    }

    /**
     * autocompleteInfoのプロパティ名を返します。
     * 
     * @return autocompleteInfoのプロパティ名
     */
    public static _AutocompleteInfoNames autocompleteInfo() {
        return new _AutocompleteInfoNames("autocompleteInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _AutocompleteDataNames extends PropertyName<AutocompleteData> {

        /**
         * インスタンスを構築します。
         */
        public _AutocompleteDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _AutocompleteDataNames(final String name) {
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
        public _AutocompleteDataNames(final PropertyName<?> parent, final String name) {
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
         * autocompleteinfoidのプロパティ名を返します。
         *
         * @return autocompleteinfoidのプロパティ名
         */
        public PropertyName<Long> autocompleteinfoid() {
            return new PropertyName<Long>(this, "autocompleteinfoid");
        }

        /**
         * stringのプロパティ名を返します。
         *
         * @return stringのプロパティ名
         */
        public PropertyName<String> string() {
            return new PropertyName<String>(this, "string");
        }

        /**
         * countのプロパティ名を返します。
         *
         * @return countのプロパティ名
         */
        public PropertyName<Integer> count() {
            return new PropertyName<Integer>(this, "count");
        }

        /**
         * lasttimeのプロパティ名を返します。
         *
         * @return lasttimeのプロパティ名
         */
        public PropertyName<Timestamp> lasttime() {
            return new PropertyName<Timestamp>(this, "lasttime");
        }

        /**
         * autocompleteInfoのプロパティ名を返します。
         * 
         * @return autocompleteInfoのプロパティ名
         */
        public _AutocompleteInfoNames autocompleteInfo() {
            return new _AutocompleteInfoNames(this, "autocompleteInfo");
        }
    }
}
