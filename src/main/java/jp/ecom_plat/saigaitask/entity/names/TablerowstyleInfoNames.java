/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TablerowstyleInfo;
import jp.ecom_plat.saigaitask.entity.names.TablelistcolumnInfoNames._TablelistcolumnInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TablerowstyleInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/23 11:45:42")
public class TablerowstyleInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Attribute row style info";
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
     * tablelistcolumninfoidのプロパティ名を返します。
     * 
     * @return tablelistcolumninfoidのプロパティ名
     */
    public static PropertyName<Long> tablelistcolumninfoid() {
        return new PropertyName<Long>("tablelistcolumninfoid");
    }

    /**
     * valのプロパティ名を返します。
     * 
     * @return valのプロパティ名
     */
    public static PropertyName<String> val() {
        return new PropertyName<String>("val");
    }

    /**
     * styleのプロパティ名を返します。
     * 
     * @return styleのプロパティ名
     */
    public static PropertyName<String> style() {
        return new PropertyName<String>("style");
    }

    /**
     * tablelistcolumnInfoのプロパティ名を返します。
     * 
     * @return tablelistcolumnInfoのプロパティ名
     */
    public static _TablelistcolumnInfoNames tablelistcolumnInfo() {
        return new _TablelistcolumnInfoNames("tablelistcolumnInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TablerowstyleInfoNames extends PropertyName<TablerowstyleInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TablerowstyleInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _TablerowstyleInfoNames(final String name) {
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
        public _TablerowstyleInfoNames(final PropertyName<?> parent, final String name) {
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
         * tablelistcolumninfoidのプロパティ名を返します。
         *
         * @return tablelistcolumninfoidのプロパティ名
         */
        public PropertyName<Long> tablelistcolumninfoid() {
            return new PropertyName<Long>(this, "tablelistcolumninfoid");
        }

        /**
         * valのプロパティ名を返します。
         *
         * @return valのプロパティ名
         */
        public PropertyName<String> val() {
            return new PropertyName<String>(this, "val");
        }

        /**
         * styleのプロパティ名を返します。
         *
         * @return styleのプロパティ名
         */
        public PropertyName<String> style() {
            return new PropertyName<String>(this, "style");
        }

        /**
         * tablelistcolumnInfoのプロパティ名を返します。
         *
         * @return tablelistcolumnInfoのプロパティ名
         */
        public _TablelistcolumnInfoNames tablelistcolumnInfo() {
            return new _TablelistcolumnInfoNames(this, "tablelistcolumnInfo");
        }
    }
}
