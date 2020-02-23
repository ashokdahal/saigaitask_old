/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.MeteoxsltInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.MeteotypeMasterNames._MeteotypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.NoticetypeMasterNames._NoticetypeMasterNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link MeteoxsltInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/09 19:42:52")
public class MeteoxsltInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Weather XSLT info";
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
     * meteotypeidのプロパティ名を返します。
     * 
     * @return meteotypeidのプロパティ名
     */
    public static PropertyName<Integer> meteotypeid() {
        return new PropertyName<Integer>("meteotypeid");
    }

    /**
     * noticetypeidのプロパティ名を返します。
     * 
     * @return noticetypeidのプロパティ名
     */
    public static PropertyName<Integer> noticetypeid() {
        return new PropertyName<Integer>("noticetypeid");
    }

    /**
     * filepathのプロパティ名を返します。
     * 
     * @return filepathのプロパティ名
     */
    public static PropertyName<String> filepath() {
        return new PropertyName<String>("filepath");
    }

    /**
     * noticetypeMasterのプロパティ名を返します。
     * 
     * @return noticetypeMasterのプロパティ名
     */
    public static _NoticetypeMasterNames noticetypeMaster() {
        return new _NoticetypeMasterNames("noticetypeMaster");
    }

    /**
     * meteotypeMasterのプロパティ名を返します。
     * 
     * @return meteotypeMasterのプロパティ名
     */
    public static _MeteotypeMasterNames meteotypeMaster() {
        return new _MeteotypeMasterNames("meteotypeMaster");
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
     * @author S2JDBC-Gen
     */
    public static class _MeteoxsltInfoNames extends PropertyName<MeteoxsltInfo> {

        /**
         * インスタンスを構築します。
         */
        public _MeteoxsltInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _MeteoxsltInfoNames(final String name) {
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
        public _MeteoxsltInfoNames(final PropertyName<?> parent, final String name) {
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
         * meteotypeidのプロパティ名を返します。
         *
         * @return meteotypeidのプロパティ名
         */
        public PropertyName<Integer> meteotypeid() {
            return new PropertyName<Integer>(this, "meteotypeid");
        }

        /**
         * noticetypeidのプロパティ名を返します。
         *
         * @return noticetypeidのプロパティ名
         */
        public PropertyName<Integer> noticetypeid() {
            return new PropertyName<Integer>(this, "noticetypeid");
        }

        /**
         * filepathのプロパティ名を返します。
         *
         * @return filepathのプロパティ名
         */
        public PropertyName<String> filepath() {
            return new PropertyName<String>(this, "filepath");
        }

        /**
         * noticetypeMasterのプロパティ名を返します。
         *
         * @return noticetypeMasterのプロパティ名
         */
        public _NoticetypeMasterNames noticetypeMaster() {
            return new _NoticetypeMasterNames(this, "noticetypeMaster");
        }

        /**
         * meteotypeMasterのプロパティ名を返します。
         *
         * @return meteotypeMasterのプロパティ名
         */
        public _MeteotypeMasterNames meteotypeMaster() {
            return new _MeteotypeMasterNames(this, "meteotypeMaster");
        }

        /**
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }
    }
}
