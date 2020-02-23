/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.TelemeterfileData;
import jp.ecom_plat.saigaitask.entity.names.TelemeterofficeInfoNames._TelemeterofficeInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link TelemeterfileData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/20 19:42:39")
public class TelemeterfileDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Telemeter file data";
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
     * filenameのプロパティ名を返します。
     *
     * @return filenameのプロパティ名
     */
    public static PropertyName<String> filename() {
        return new PropertyName<String>("filename");
    }

    /**
     * observtimeのプロパティ名を返します。
     *
     * @return observtimeのプロパティ名
     */
    public static PropertyName<Timestamp> observtime() {
        return new PropertyName<Timestamp>("observtime");
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
    public static class _TelemeterfileDataNames extends PropertyName<TelemeterfileData> {

        /**
         * インスタンスを構築します。
         */
        public _TelemeterfileDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _TelemeterfileDataNames(final String name) {
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
        public _TelemeterfileDataNames(final PropertyName<?> parent, final String name) {
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
         * filenameのプロパティ名を返します。
         *
         * @return filenameのプロパティ名
         */
        public PropertyName<String> filename() {
            return new PropertyName<String>(this, "filename");
        }

        /**
         * observtimeのプロパティ名を返します。
         *
         * @return observtimeのプロパティ名
         */
        public PropertyName<Timestamp> observtime() {
            return new PropertyName<Timestamp>(this, "observtime");
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
