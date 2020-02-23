/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.ObservlistInfo;
import jp.ecom_plat.saigaitask.entity.names.ObservMasterNames._ObservMasterNames;
import jp.ecom_plat.saigaitask.entity.names.ObservmenuInfoNames._ObservmenuInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link ObservlistInfo}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/02/24 19:26:03")
public class ObservlistInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Monitoring/observation list info";
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
     * observmenuinfoidのプロパティ名を返します。
     *
     * @return observmenuinfoidのプロパティ名
     */
    public static PropertyName<Long> observmenuinfoid() {
        return new PropertyName<Long>("observmenuinfoid");
    }

    /**
     * observidのプロパティ名を返します。
     *
     * @return observidのプロパティ名
     */
    public static PropertyName<Integer> observid() {
        return new PropertyName<Integer>("observid");
    }

    /**
     * observatoryinfoidのプロパティ名を返します。
     *
     * @return observatoryinfoidのプロパティ名
     */
    public static PropertyName<Long> observatoryinfoid() {
        return new PropertyName<Long>("observatoryinfoid");
    }

    /**
     * itemcodeのプロパティ名を返します。
     *
     * @return itemcodeのプロパティ名
     */
    public static PropertyName<Integer> itemcode() {
        return new PropertyName<Integer>("itemcode");
    }

    /**
     * disporderのプロパティ名を返します。
     *
     * @return disporderのプロパティ名
     */
    public static PropertyName<Integer> disporder() {
        return new PropertyName<Integer>("disporder");
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
     * observmenuInfoのプロパティ名を返します。
     *
     * @return observmenuInfoのプロパティ名
     */
    public static _ObservmenuInfoNames observmenuInfo() {
        return new _ObservmenuInfoNames("observmenuInfo");
    }

    /**
     * observMasterのプロパティ名を返します。
     *
     * @return observMasterのプロパティ名
     */
    public static _ObservMasterNames observMaster() {
        return new _ObservMasterNames("observMaster");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _ObservlistInfoNames extends PropertyName<ObservlistInfo> {

        /**
         * インスタンスを構築します。
         */
        public _ObservlistInfoNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _ObservlistInfoNames(final String name) {
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
        public _ObservlistInfoNames(final PropertyName<?> parent, final String name) {
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
         * observmenuinfoidのプロパティ名を返します。
         *
         * @return observmenuinfoidのプロパティ名
         */
        public PropertyName<Long> observmenuinfoid() {
            return new PropertyName<Long>(this, "observmenuinfoid");
        }

        /**
         * observidのプロパティ名を返します。
         *
         * @return observidのプロパティ名
         */
        public PropertyName<Integer> observid() {
            return new PropertyName<Integer>(this, "observid");
        }

        /**
         * observatoryinfoidのプロパティ名を返します。
         *
         * @return observatoryinfoidのプロパティ名
         */
        public PropertyName<Long> observatoryinfoid() {
            return new PropertyName<Long>(this, "observatoryinfoid");
        }

        /**
         * itemcodeのプロパティ名を返します。
         *
         * @return itemcodeのプロパティ名
         */
        public PropertyName<Integer> itemcode() {
            return new PropertyName<Integer>(this, "itemcode");
        }

        /**
         * disporderのプロパティ名を返します。
         *
         * @return disporderのプロパティ名
         */
        public PropertyName<Integer> disporder() {
            return new PropertyName<Integer>(this, "disporder");
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
         * observmenuInfoのプロパティ名を返します。
         *
         * @return observmenuInfoのプロパティ名
         */
        public _ObservmenuInfoNames observmenuInfo() {
            return new _ObservmenuInfoNames(this, "observmenuInfo");
        }

        /**
         * disasterMasterのプロパティ名を返します。
         *
         * @return disasterMasterのプロパティ名
         */
        public _ObservMasterNames observMaster() {
            return new _ObservMasterNames(this, "observMaster");
        }
    }
}