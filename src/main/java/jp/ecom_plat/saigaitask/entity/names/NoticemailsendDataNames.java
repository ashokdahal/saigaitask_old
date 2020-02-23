/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.NoticemailsendData;
import jp.ecom_plat.saigaitask.entity.names.NoticegroupInfoNames._NoticegroupInfoNames;
import jp.ecom_plat.saigaitask.entity.names.NoticemailDataNames._NoticemailDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link NoticemailsendData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/10 12:56:31")
public class NoticemailsendDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Notice-to data";
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
     * noticemaildataidのプロパティ名を返します。
     * 
     * @return noticemaildataidのプロパティ名
     */
    public static PropertyName<Long> noticemaildataid() {
        return new PropertyName<Long>("noticemaildataid");
    }

    /**
     * noticegroupinfoidのプロパティ名を返します。
     * 
     * @return noticegroupinfoidのプロパティ名
     */
    public static PropertyName<Long> noticegroupinfoid() {
        return new PropertyName<Long>("noticegroupinfoid");
    }

    /**
     * noticemailDataのプロパティ名を返します。
     * 
     * @return noticemailDataのプロパティ名
     */
    public static _NoticemailDataNames noticemailData() {
        return new _NoticemailDataNames("noticemailData");
    }

    /**
     * noticgroupInfoのプロパティ名を返します。
     * 
     * @return noticgroupInfoのプロパティ名
     */
    public static _NoticegroupInfoNames noticgroupInfo() {
        return new _NoticegroupInfoNames("noticgroupInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _NoticemailsendDataNames extends PropertyName<NoticemailsendData> {

        /**
         * インスタンスを構築します。
         */
        public _NoticemailsendDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _NoticemailsendDataNames(final String name) {
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
        public _NoticemailsendDataNames(final PropertyName<?> parent, final String name) {
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
         * noticemaildataidのプロパティ名を返します。
         *
         * @return noticemaildataidのプロパティ名
         */
        public PropertyName<Long> noticemaildataid() {
            return new PropertyName<Long>(this, "noticemaildataid");
        }

        /**
         * noticegroupinfoidのプロパティ名を返します。
         *
         * @return noticegroupinfoidのプロパティ名
         */
        public PropertyName<Long> noticegroupinfoid() {
            return new PropertyName<Long>(this, "noticegroupinfoid");
        }

        /**
         * noticemailDataのプロパティ名を返します。
         * 
         * @return noticemailDataのプロパティ名
         */
        public _NoticemailDataNames noticemailData() {
            return new _NoticemailDataNames(this, "noticemailData");
        }

        /**
         * noticgroupInfoのプロパティ名を返します。
         * 
         * @return noticgroupInfoのプロパティ名
         */
        public _NoticegroupInfoNames noticgroupInfo() {
            return new _NoticegroupInfoNames(this, "noticgroupInfo");
        }
    }
}
