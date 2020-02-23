/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PostingphotolayerData;
import jp.ecom_plat.saigaitask.entity.names.PostingphotolayerInfoNames._PostingphotolayerInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PostingphotolayerData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/11/12 21:16:12")
public class PostingphotolayerDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Disaster photos assortment data";
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
     * postingphotolayerinfoidのプロパティ名を返します。
     * 
     * @return postingphotolayerinfoidのプロパティ名
     */
    public static PropertyName<Long> postingphotolayerinfoid() {
        return new PropertyName<Long>("postingphotolayerinfoid");
    }

    /**
     * layeridのプロパティ名を返します。
     * 
     * @return layeridのプロパティ名
     */
    public static PropertyName<String> layerid() {
        return new PropertyName<String>("layerid");
    }

    /**
     * photogidのプロパティ名を返します。
     * 
     * @return photogidのプロパティ名
     */
    public static PropertyName<Long> photogid() {
        return new PropertyName<Long>("photogid");
    }

    /**
     * copytrackdataidのプロパティ名を返します。
     * 
     * @return copytrackdataidのプロパティ名
     */
    public static PropertyName<Long> copytrackdataid() {
        return new PropertyName<Long>("copytrackdataid");
    }

    /**
     * copylayeridのプロパティ名を返します。
     * 
     * @return copylayeridのプロパティ名
     */
    public static PropertyName<String> copylayerid() {
        return new PropertyName<String>("copylayerid");
    }

    /**
     * copygidのプロパティ名を返します。
     * 
     * @return copygidのプロパティ名
     */
    public static PropertyName<Long> copygid() {
        return new PropertyName<Long>("copygid");
    }

    /**
     * copytimeのプロパティ名を返します。
     * 
     * @return copytimeのプロパティ名
     */
    public static PropertyName<Timestamp> copytime() {
        return new PropertyName<Timestamp>("copytime");
    }

    /**
     * postingphotolayerInfoのプロパティ名を返します。
     * 
     * @return postingphotolayerInfoのプロパティ名
     */
    public static _PostingphotolayerInfoNames postingphotolayerInfo() {
        return new _PostingphotolayerInfoNames("postingphotolayerInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _PostingphotolayerDataNames extends PropertyName<PostingphotolayerData> {

        /**
         * インスタンスを構築します。
         */
        public _PostingphotolayerDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _PostingphotolayerDataNames(final String name) {
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
        public _PostingphotolayerDataNames(final PropertyName<?> parent, final String name) {
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
         * postingphotolayerinfoidのプロパティ名を返します。
         *
         * @return postingphotolayerinfoidのプロパティ名
         */
        public PropertyName<Long> postingphotolayerinfoid() {
            return new PropertyName<Long>(this, "postingphotolayerinfoid");
        }

        /**
         * layeridのプロパティ名を返します。
         *
         * @return layeridのプロパティ名
         */
        public PropertyName<String> layerid() {
            return new PropertyName<String>(this, "layerid");
        }

        /**
         * photogidのプロパティ名を返します。
         *
         * @return photogidのプロパティ名
         */
        public PropertyName<Long> photogid() {
            return new PropertyName<Long>(this, "photogid");
        }

        /**
         * copytrackdataidのプロパティ名を返します。
         *
         * @return copytrackdataidのプロパティ名
         */
        public PropertyName<Long> copytrackdataid() {
            return new PropertyName<Long>(this, "copytrackdataid");
        }

        /**
         * copylayeridのプロパティ名を返します。
         *
         * @return copylayeridのプロパティ名
         */
        public PropertyName<String> copylayerid() {
            return new PropertyName<String>(this, "copylayerid");
        }

        /**
         * copygidのプロパティ名を返します。
         *
         * @return copygidのプロパティ名
         */
        public PropertyName<Long> copygid() {
            return new PropertyName<Long>(this, "copygid");
        }

        /**
         * copytimeのプロパティ名を返します。
         *
         * @return copytimeのプロパティ名
         */
        public PropertyName<Timestamp> copytime() {
            return new PropertyName<Timestamp>(this, "copytime");
        }

        /**
         * postingphotolayerInfoのプロパティ名を返します。
         * 
         * @return postingphotolayerInfoのプロパティ名
         */
        public _PostingphotolayerInfoNames postingphotolayerInfo() {
            return new _PostingphotolayerInfoNames(this, "postingphotolayerInfo");
        }
    }
}
