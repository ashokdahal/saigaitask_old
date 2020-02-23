/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TablecalculateInfo;
import jp.ecom_plat.saigaitask.entity.names.TablecalculatecolumnInfoNames._TablecalculatecolumnInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TablecalculateInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/12/05 16:31:04")
public class TablecalculateInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Table calculation info";
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
     * tablecalculatecolumninfoidのプロパティ名を返します。
     * 
     * @return tablecalculatecolumninfoidのプロパティ名
     */
    public static PropertyName<Long> tablecalculatecolumninfoid() {
        return new PropertyName<Long>("tablecalculatecolumninfoid");
    }

    /**
     * functionのプロパティ名を返します。
     * 
     * @return functionのプロパティ名
     */
    public static PropertyName<String> function() {
        return new PropertyName<String>("function");
    }

    /**
     * tablecalculatecolumnInfoのプロパティ名を返します。
     * 
     * @return tablecalculatecolumnInfoのプロパティ名
     */
    public static _TablecalculatecolumnInfoNames tablecalculatecolumnInfo() {
        return new _TablecalculatecolumnInfoNames("tablecalculatecolumnInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TablecalculateInfoNames extends PropertyName<TablecalculateInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TablecalculateInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TablecalculateInfoNames(final String name) {
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
        public _TablecalculateInfoNames(final PropertyName<?> parent, final String name) {
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
         * tablecalculatecolumninfoidのプロパティ名を返します。
         *
         * @return tablecalculatecolumninfoidのプロパティ名
         */
        public PropertyName<Long> tablecalculatecolumninfoid() {
            return new PropertyName<Long>(this, "tablecalculatecolumninfoid");
        }

        /**
         * functionのプロパティ名を返します。
         *
         * @return functionのプロパティ名
         */
        public PropertyName<String> function() {
            return new PropertyName<String>(this, "function");
        }

        /**
         * tablecalculatecolumnInfoのプロパティ名を返します。
         * 
         * @return tablecalculatecolumnInfoのプロパティ名
         */
        public _TablecalculatecolumnInfoNames tablecalculatecolumnInfo() {
            return new _TablecalculatecolumnInfoNames(this, "tablecalculatecolumnInfo");
        }
    }
}
