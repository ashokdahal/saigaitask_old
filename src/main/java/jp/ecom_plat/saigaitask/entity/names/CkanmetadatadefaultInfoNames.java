/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadatadefaultInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;
/**
 * {@link CkanmetadatadefaultInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2016/09/26 17:52:58")
public class CkanmetadatadefaultInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "CKAN metadata default settings info";
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
     * authorのプロパティ名を返します。
     * 
     * @return authorのプロパティ名
     */
    public static PropertyName<String> author() {
        return new PropertyName<String>("author");
    }

    /**
     * authoremailのプロパティ名を返します。
     * 
     * @return authoremailのプロパティ名
     */
    public static PropertyName<String> authoremail() {
        return new PropertyName<String>("authoremail");
    }

    /**
     * maintainerのプロパティ名を返します。
     * 
     * @return maintainerのプロパティ名
     */
    public static PropertyName<String> maintainer() {
        return new PropertyName<String>("maintainer");
    }

    /**
     * maintaineremailのプロパティ名を返します。
     * 
     * @return maintaineremailのプロパティ名
     */
    public static PropertyName<String> maintaineremail() {
        return new PropertyName<String>("maintaineremail");
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
    public static class _CkanmetadatadefaultInfoNames extends PropertyName<ClearinghousemetadatadefaultInfo> {

        /**
         * インスタンスを構築します。
         */
        public _CkanmetadatadefaultInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _CkanmetadatadefaultInfoNames(final String name) {
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
        public _CkanmetadatadefaultInfoNames(final PropertyName<?> parent, final String name) {
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
         * authorのプロパティ名を返します。
         *
         * @return authorのプロパティ名
         */
        public PropertyName<String> author() {
            return new PropertyName<String>(this, "author");
        }

        /**
         * suffixのプロパティ名を返します。
         *
         * @return suffixのプロパティ名
         */
        public PropertyName<String> suffix() {
            return new PropertyName<String>(this, "suffix");
        }

        /**
         * authoremailのプロパティ名を返します。
         *
         * @return authoremailのプロパティ名
         */
        public PropertyName<String> authoremail() {
            return new PropertyName<String>(this, "authoremail");
        }

        /**
         * maintainerのプロパティ名を返します。
         *
         * @return maintainerのプロパティ名
         */
        public PropertyName<String> maintainer() {
            return new PropertyName<String>(this, "maintainer");
        }

        /**
         * maintaineremailのプロパティ名を返します。
         *
         * @return maintaineremailのプロパティ名
         */
        public PropertyName<String> maintaineremail() {
            return new PropertyName<String>(this, "maintaineremail");
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