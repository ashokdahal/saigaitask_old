/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.HistorycolumnlistInfo;
import jp.ecom_plat.saigaitask.entity.names.HistorytableInfoNames._HistorytableInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link HistorycolumnlistInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/10/07 16:42:49")
public class HistorycolumnlistInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "History table item list";
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
     * historytableinfoidのプロパティ名を返します。
     *
     * @return historytableinfoidのプロパティ名
     */
    public static PropertyName<Long> historytableinfoid() {
        return new PropertyName<Long>("historytableinfoid");
    }

    /**
     * attrIdのプロパティ名を返します。
     *
     * @return attrIdのプロパティ名
     */
    public static PropertyName<String> attrId() {
        return new PropertyName<String>("attrId");
    }

    /**
     * attrNameのプロパティ名を返します。
     *
     * @return attrNameのプロパティ名
     */
    public static PropertyName<String> attrName() {
        return new PropertyName<String>("attrName");
    }

    /**
     * doLogのプロパティ名を返します。
     *
     * @return doLogのプロパティ名
     */
    public static PropertyName<Boolean> doLog() {
        return new PropertyName<Boolean>("doLog");
    }

    /**
     * isNumericのプロパティ名を返します。
     *
     * @return isNumericのプロパティ名
     */
    public static PropertyName<Boolean> isNumeric() {
        return new PropertyName<Boolean>("isNumeric");
    }

    /**
     * historytableInfoのプロパティ名を返します。
     *
     * @return historytableInfoのプロパティ名
     */
    public static _HistorytableInfoNames historytableInfo() {
        return new _HistorytableInfoNames("historytableInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _HistorycolumnlistInfoNames extends PropertyName<HistorycolumnlistInfo> {

        /**
         * インスタンスを構築します。
         */
        public _HistorycolumnlistInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _HistorycolumnlistInfoNames(final String name) {
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
        public _HistorycolumnlistInfoNames(final PropertyName<?> parent, final String name) {
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
         * historytableinfoidのプロパティ名を返します。
         *
         * @return historytableinfoidのプロパティ名
         */
        public PropertyName<Long> historytableinfoid() {
            return new PropertyName<Long>(this, "historytableinfoid");
        }

        /**
         * attrIdのプロパティ名を返します。
         *
         * @return attrIdのプロパティ名
         */
        public PropertyName<String> attrId() {
            return new PropertyName<String>(this, "attrId");
        }

        /**
         * attrNameのプロパティ名を返します。
         *
         * @return attrNameのプロパティ名
         */
        public PropertyName<String> attrName() {
            return new PropertyName<String>(this, "attrName");
        }

        /**
         * doLogのプロパティ名を返します。
         *
         * @return doLogのプロパティ名
         */
        public PropertyName<Boolean> doLog() {
            return new PropertyName<Boolean>(this, "doLog");
        }

        /**
         * isNumericのプロパティ名を返します。
         *
         * @return isNumericのプロパティ名
         */
        public PropertyName<Boolean> isNumeric() {
            return new PropertyName<Boolean>(this, "isNumeric");
        }

        /**
         * historytableInfoのプロパティ名を返します。
         *
         * @return historytableInfoのプロパティ名
         */
        public _HistorytableInfoNames historytableInfo() {
            return new _HistorytableInfoNames(this, "historytableInfo");
        }
    }
}