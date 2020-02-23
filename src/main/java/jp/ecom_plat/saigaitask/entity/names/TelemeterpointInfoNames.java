/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TelemeterpointInfo;
import jp.ecom_plat.saigaitask.entity.names.TelemeterofficeInfoNames._TelemeterofficeInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TelemeterpointInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/28 0:07:37")
public class TelemeterpointInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Telemeter observatory info";
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
     * telemeterofficeinfoidのプロパティ名を返します。
     *
     * @return telemeterofficeinfoidのプロパティ名
     */
    public static PropertyName<Long> telemeterofficeinfoid() {
        return new PropertyName<Long>("telemeterofficeinfoid");
    }

    /**
     * itemkindcodeのプロパティ名を返します。
     *
     * @return itemkindcodeのプロパティ名
     */
    public static PropertyName<Integer> itemkindcode() {
        return new PropertyName<Integer>("itemkindcode");
    }

    /**
     * pointcodeのプロパティ名を返します。
     *
     * @return pointcodeのプロパティ名
     */
    public static PropertyName<Long> pointcode() {
        return new PropertyName<Long>("pointcode");
    }

    /**
     * itemcodeのプロパティ名を返します。
     *
     * @return itemcodeのプロパティ名
     */
    public static PropertyName<String> itemcode() {
        return new PropertyName<String>("itemcode");
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
     * validのプロパティ名を返します。
     *
     * @return validのプロパティ名
     */
    public static PropertyName<Boolean> valid() {
        return new PropertyName<Boolean>("valid");
    }

    /**
     * telemeterofficeInfoのプロパティ名を返します。
     *
     * @return telemeterofficeInfoのプロパティ名
     */
    public static _TelemeterofficeInfoNames telemeterofficeInfo() {
        return new _TelemeterofficeInfoNames("telemeterofficeInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _TelemeterpointInfoNames extends PropertyName<TelemeterpointInfo> {

        /**
         * インスタンスを構築します。
         */
        public _TelemeterpointInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TelemeterpointInfoNames(final String name) {
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
        public _TelemeterpointInfoNames(final PropertyName<?> parent, final String name) {
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
         * telemeterofficeinfoidのプロパティ名を返します。
         *
         * @return telemeterofficeinfoidのプロパティ名
         */
        public PropertyName<Long> telemeterofficeinfoid() {
            return new PropertyName<Long>(this, "telemeterofficeinfoid");
        }

        /**
         * itemkindcodeのプロパティ名を返します。
         *
         * @return itemkindcodeのプロパティ名
         */
        public PropertyName<Integer> itemkindcode() {
            return new PropertyName<Integer>(this, "itemkindcode");
        }

        /**
         * pointcodeのプロパティ名を返します。
         *
         * @return pointcodeのプロパティ名
         */
        public PropertyName<Long> pointcode() {
            return new PropertyName<Long>(this, "pointcode");
        }

        /**
         * itemcodeのプロパティ名を返します。
         *
         * @return itemcodeのプロパティ名
         */
        public PropertyName<String> itemcode() {
            return new PropertyName<String>(this, "itemcode");
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
         * validのプロパティ名を返します。
         *
         * @return validのプロパティ名
         */
        public PropertyName<Boolean> valid() {
            return new PropertyName<Boolean>(this, "valid");
        }

        /**
         * telemeterofficeInfoのプロパティ名を返します。
         *
         * @return telemeterofficeInfoのプロパティ名
         */
        public _TelemeterofficeInfoNames telemeterofficeInfo() {
            return new _TelemeterofficeInfoNames(this, "telemeterofficeInfo");
        }
    }
}
