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
 * {@link ClearinghousemetadatadefaultInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2016/09/26 17:52:58")
public class ClearinghousemetadatadefaultInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Metadata default settings info";
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
     * prefixのプロパティ名を返します。
     * 
     * @return prefixのプロパティ名
     */
    public static PropertyName<String> prefix() {
        return new PropertyName<String>("prefix");
    }

    /**
     * suffixのプロパティ名を返します。
     * 
     * @return suffixのプロパティ名
     */
    public static PropertyName<String> suffix() {
        return new PropertyName<String>("suffix");
    }

    /**
     * referenceのプロパティ名を返します。
     * 
     * @return referenceのプロパティ名
     */
    public static PropertyName<String> reference() {
        return new PropertyName<String>("reference");
    }

    /**
     * telnoのプロパティ名を返します。
     * 
     * @return telnoのプロパティ名
     */
    public static PropertyName<String> telno() {
        return new PropertyName<String>("telno");
    }

    /**
     * postcodeのプロパティ名を返します。
     * 
     * @return postcodeのプロパティ名
     */
    public static PropertyName<String> postcode() {
        return new PropertyName<String>("postcode");
    }

    /**
     * adminareaのプロパティ名を返します。
     * 
     * @return adminareaのプロパティ名
     */
    public static PropertyName<String> adminarea() {
        return new PropertyName<String>("adminarea");
    }

    /**
     * cityのプロパティ名を返します。
     * 
     * @return cityのプロパティ名
     */
    public static PropertyName<String> city() {
        return new PropertyName<String>("city");
    }

    /**
     * adminareacodeのプロパティ名を返します。
     * 
     * @return adminareacodeのプロパティ名
     */
    public static PropertyName<String> adminareacode() {
        return new PropertyName<String>("adminareacode");
    }

    /**
     * citycodeのプロパティ名を返します。
     * 
     * @return citycodeのプロパティ名
     */
    public static PropertyName<String> citycode() {
        return new PropertyName<String>("citycode");
    }

    /**
     * deliverypointのプロパティ名を返します。
     * 
     * @return deliverypointのプロパティ名
     */
    public static PropertyName<String> deliverypoint() {
        return new PropertyName<String>("deliverypoint");
    }

    /**
     * emailのプロパティ名を返します。
     * 
     * @return emailのプロパティ名
     */
    public static PropertyName<String> email() {
        return new PropertyName<String>("email");
    }

    /**
     * linkageのプロパティ名を返します。
     * 
     * @return linkageのプロパティ名
     */
    public static PropertyName<String> linkage() {
        return new PropertyName<String>("linkage");
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
    public static class _ClearinghousemetadatadefaultInfoNames extends PropertyName<ClearinghousemetadatadefaultInfo> {

        /**
         * インスタンスを構築します。
         */
        public _ClearinghousemetadatadefaultInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _ClearinghousemetadatadefaultInfoNames(final String name) {
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
        public _ClearinghousemetadatadefaultInfoNames(final PropertyName<?> parent, final String name) {
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
         * prefixのプロパティ名を返します。
         *
         * @return prefixのプロパティ名
         */
        public PropertyName<String> prefix() {
            return new PropertyName<String>(this, "prefix");
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
         * referenceのプロパティ名を返します。
         *
         * @return referenceのプロパティ名
         */
        public PropertyName<String> reference() {
            return new PropertyName<String>(this, "reference");
        }

        /**
         * telnoのプロパティ名を返します。
         *
         * @return telnoのプロパティ名
         */
        public PropertyName<String> telno() {
            return new PropertyName<String>(this, "telno");
        }

        /**
         * postcodeのプロパティ名を返します。
         *
         * @return postcodeのプロパティ名
         */
        public PropertyName<String> postcode() {
            return new PropertyName<String>(this, "postcode");
        }

        /**
         * adminareaのプロパティ名を返します。
         *
         * @return adminareaのプロパティ名
         */
        public PropertyName<String> adminarea() {
            return new PropertyName<String>(this, "adminarea");
        }

        /**
         * cityのプロパティ名を返します。
         *
         * @return cityのプロパティ名
         */
        public PropertyName<String> city() {
            return new PropertyName<String>(this, "city");
        }

        /**
         * adminareacodeのプロパティ名を返します。
         *
         * @return adminareacodeのプロパティ名
         */
        public PropertyName<String> adminareacode() {
            return new PropertyName<String>(this, "adminareacode");
        }

        /**
         * citycodeのプロパティ名を返します。
         *
         * @return citycodeのプロパティ名
         */
        public PropertyName<String> citycode() {
            return new PropertyName<String>(this, "citycode");
        }

        /**
         * deliverypointのプロパティ名を返します。
         *
         * @return deliverypointのプロパティ名
         */
        public PropertyName<String> deliverypoint() {
            return new PropertyName<String>(this, "deliverypoint");
        }

        /**
         * emailのプロパティ名を返します。
         *
         * @return emailのプロパティ名
         */
        public PropertyName<String> email() {
            return new PropertyName<String>(this, "email");
        }

        /**
         * linkageのプロパティ名を返します。
         *
         * @return linkageのプロパティ名
         */
        public PropertyName<String> linkage() {
            return new PropertyName<String>(this, "linkage");
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