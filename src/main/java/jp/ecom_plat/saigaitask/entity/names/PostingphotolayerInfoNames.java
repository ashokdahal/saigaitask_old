/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PostingphotolayerInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;
import jp.ecom_plat.saigaitask.entity.names.PostingphotolayerDataNames._PostingphotolayerDataNames;
import jp.ecom_plat.saigaitask.entity.names.TablemasterInfoNames._TablemasterInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PostingphotolayerInfo}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/11/12 21:16:12")
public class PostingphotolayerInfoNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Disaster photos posted layer info";
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
     * tablemasterinfoidのプロパティ名を返します。
     * 
     * @return tablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> tablemasterinfoid() {
        return new PropertyName<Long>("tablemasterinfoid");
    }

    /**
     * commentAttridのプロパティ名を返します。
     * 
     * @return commentAttridのプロパティ名
     */
    public static PropertyName<String> commentAttrid() {
        return new PropertyName<String>("commentAttrid");
    }

    /**
     * groupAttridのプロパティ名を返します。
     * 
     * @return groupAttridのプロパティ名
     */
    public static PropertyName<String> groupAttrid() {
        return new PropertyName<String>("groupAttrid");
    }

    /**
     * nameAttridのプロパティ名を返します。
     * 
     * @return nameAttridのプロパティ名
     */
    public static PropertyName<String> nameAttrid() {
        return new PropertyName<String>("nameAttrid");
    }

    /**
     * contactAttridのプロパティ名を返します。
     * 
     * @return contactAttridのプロパティ名
     */
    public static PropertyName<String> contactAttrid() {
        return new PropertyName<String>("contactAttrid");
    }

    /**
     * timeAttridのプロパティ名を返します。
     * 
     * @return timeAttridのプロパティ名
     */
    public static PropertyName<String> timeAttrid() {
        return new PropertyName<String>("timeAttrid");
    }

    /**
     * directionAttridのプロパティ名を返します。
     * 
     * @return directionAttridのプロパティ名
     */
    public static PropertyName<String> directionAttrid() {
        return new PropertyName<String>("directionAttrid");
    }

    /**
     * heightAttridのプロパティ名を返します。
     * 
     * @return heightAttridのプロパティ名
     */
    public static PropertyName<String> heightAttrid() {
        return new PropertyName<String>("heightAttrid");
    }

    /**
     * flagAttridのプロパティ名を返します。
     * 
     * @return flagAttridのプロパティ名
     */
    public static PropertyName<String> flagAttrid() {
        return new PropertyName<String>("flagAttrid");
    }

    /**
     * copytablemasterinfoidのプロパティ名を返します。
     * 
     * @return copytablemasterinfoidのプロパティ名
     */
    public static PropertyName<Long> copytablemasterinfoid() {
        return new PropertyName<Long>("copytablemasterinfoid");
    }

    /**
     * copycommentAttridのプロパティ名を返します。
     * 
     * @return copycommentAttridのプロパティ名
     */
    public static PropertyName<String> copycommentAttrid() {
        return new PropertyName<String>("copycommentAttrid");
    }

    /**
     * copygroupAttridのプロパティ名を返します。
     * 
     * @return copygroupAttridのプロパティ名
     */
    public static PropertyName<String> copygroupAttrid() {
        return new PropertyName<String>("copygroupAttrid");
    }

    /**
     * copynameAttridのプロパティ名を返します。
     * 
     * @return copynameAttridのプロパティ名
     */
    public static PropertyName<String> copynameAttrid() {
        return new PropertyName<String>("copynameAttrid");
    }

    /**
     * copycontactAttridのプロパティ名を返します。
     * 
     * @return copycontactAttridのプロパティ名
     */
    public static PropertyName<String> copycontactAttrid() {
        return new PropertyName<String>("copycontactAttrid");
    }

    /**
     * copytimeAttridのプロパティ名を返します。
     * 
     * @return copytimeAttridのプロパティ名
     */
    public static PropertyName<String> copytimeAttrid() {
        return new PropertyName<String>("copytimeAttrid");
    }

    /**
     * copydirectionAttridのプロパティ名を返します。
     * 
     * @return copydirectionAttridのプロパティ名
     */
    public static PropertyName<String> copydirectionAttrid() {
        return new PropertyName<String>("copydirectionAttrid");
    }

    /**
     * copyheightAttridのプロパティ名を返します。
     * 
     * @return copyheightAttridのプロパティ名
     */
    public static PropertyName<String> copyheightAttrid() {
        return new PropertyName<String>("copyheightAttrid");
    }

    /**
     * maximagewidthのプロパティ名を返します。
     * 
     * @return maximagewidthのプロパティ名
     */
    public static PropertyName<Integer> maximagewidth() {
        return new PropertyName<Integer>("maximagewidth");
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
     * tablemasterInfoのプロパティ名を返します。
     * 
     * @return tablemasterInfoのプロパティ名
     */
    public static _TablemasterInfoNames tablemasterInfo() {
        return new _TablemasterInfoNames("tablemasterInfo");
    }

    /**
     * copytablemasterInfoのプロパティ名を返します。
     * 
     * @return copytablemasterInfoのプロパティ名
     */
    public static _TablemasterInfoNames copytablemasterInfo() {
        return new _TablemasterInfoNames("copytablemasterInfo");
    }

    /**
     * postingphotolayerDatasのプロパティ名を返します。
     * 
     * @return postingphotolayerDatasのプロパティ名
     */
    public static _PostingphotolayerDataNames postingphotolayerDatas() {
        return new _PostingphotolayerDataNames("postingphotolayerDatas");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _PostingphotolayerInfoNames extends PropertyName<PostingphotolayerInfo> {

        /**
         * インスタンスを構築します。
         */
        public _PostingphotolayerInfoNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _PostingphotolayerInfoNames(final String name) {
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
        public _PostingphotolayerInfoNames(final PropertyName<?> parent, final String name) {
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
         * tablemasterinfoidのプロパティ名を返します。
         *
         * @return tablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> tablemasterinfoid() {
            return new PropertyName<Long>(this, "tablemasterinfoid");
        }

        /**
         * commentAttridのプロパティ名を返します。
         *
         * @return commentAttridのプロパティ名
         */
        public PropertyName<String> commentAttrid() {
            return new PropertyName<String>(this, "commentAttrid");
        }

        /**
         * groupAttridのプロパティ名を返します。
         *
         * @return groupAttridのプロパティ名
         */
        public PropertyName<String> groupAttrid() {
            return new PropertyName<String>(this, "groupAttrid");
        }

        /**
         * nameAttridのプロパティ名を返します。
         *
         * @return nameAttridのプロパティ名
         */
        public PropertyName<String> nameAttrid() {
            return new PropertyName<String>(this, "nameAttrid");
        }

        /**
         * contactAttridのプロパティ名を返します。
         *
         * @return contactAttridのプロパティ名
         */
        public PropertyName<String> contactAttrid() {
            return new PropertyName<String>(this, "contactAttrid");
        }

        /**
         * timeAttridのプロパティ名を返します。
         *
         * @return timeAttridのプロパティ名
         */
        public PropertyName<String> timeAttrid() {
            return new PropertyName<String>(this, "timeAttrid");
        }

        /**
         * directionAttridのプロパティ名を返します。
         *
         * @return directionAttridのプロパティ名
         */
        public PropertyName<String> directionAttrid() {
            return new PropertyName<String>(this, "directionAttrid");
        }

        /**
         * heightAttridのプロパティ名を返します。
         *
         * @return heightAttridのプロパティ名
         */
        public PropertyName<String> heightAttrid() {
            return new PropertyName<String>(this, "heightAttrid");
        }

        /**
         * flagAttridのプロパティ名を返します。
         *
         * @return flagAttridのプロパティ名
         */
        public PropertyName<String> flagAttrid() {
            return new PropertyName<String>(this, "flagAttrid");
        }

        /**
         * copytablemasterinfoidのプロパティ名を返します。
         *
         * @return copytablemasterinfoidのプロパティ名
         */
        public PropertyName<Long> copytablemasterinfoid() {
            return new PropertyName<Long>(this, "copytablemasterinfoid");
        }

        /**
         * copycommentAttridのプロパティ名を返します。
         *
         * @return copycommentAttridのプロパティ名
         */
        public PropertyName<String> copycommentAttrid() {
            return new PropertyName<String>(this, "copycommentAttrid");
        }

        /**
         * copygroupAttridのプロパティ名を返します。
         *
         * @return copygroupAttridのプロパティ名
         */
        public PropertyName<String> copygroupAttrid() {
            return new PropertyName<String>(this, "copygroupAttrid");
        }

        /**
         * copynameAttridのプロパティ名を返します。
         *
         * @return copynameAttridのプロパティ名
         */
        public PropertyName<String> copynameAttrid() {
            return new PropertyName<String>(this, "copynameAttrid");
        }

        /**
         * copycontactAttridのプロパティ名を返します。
         *
         * @return copycontactAttridのプロパティ名
         */
        public PropertyName<String> copycontactAttrid() {
            return new PropertyName<String>(this, "copycontactAttrid");
        }

        /**
         * copytimeAttridのプロパティ名を返します。
         *
         * @return copytimeAttridのプロパティ名
         */
        public PropertyName<String> copytimeAttrid() {
            return new PropertyName<String>(this, "copytimeAttrid");
        }

        /**
         * copydirectionAttridのプロパティ名を返します。
         *
         * @return copydirectionAttridのプロパティ名
         */
        public PropertyName<String> copydirectionAttrid() {
            return new PropertyName<String>(this, "copydirectionAttrid");
        }

        /**
         * copyheightAttridのプロパティ名を返します。
         *
         * @return copyheightAttridのプロパティ名
         */
        public PropertyName<String> copyheightAttrid() {
            return new PropertyName<String>(this, "copyheightAttrid");
        }

        /**
         * maximagewidthのプロパティ名を返します。
         *
         * @return maximagewidthのプロパティ名
         */
        public PropertyName<Integer> maximagewidth() {
            return new PropertyName<Integer>(this, "maximagewidth");
        }

        /**
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }

        /**
         * tablemasterInfoのプロパティ名を返します。
         * 
         * @return tablemasterInfoのプロパティ名
         */
        public _TablemasterInfoNames tablemasterInfo() {
            return new _TablemasterInfoNames(this, "tablemasterInfo");
        }

        /**
         * copytablemasterInfoのプロパティ名を返します。
         * 
         * @return copytablemasterInfoのプロパティ名
         */
        public _TablemasterInfoNames copytablemasterInfo() {
            return new _TablemasterInfoNames(this, "copytablemasterInfo");
        }

        /**
         * postingphotolayerDatasのプロパティ名を返します。
         * 
         * @return postingphotolayerDatasのプロパティ名
         */
        public _PostingphotolayerDataNames postingphotolayerDatas() {
            return new _PostingphotolayerDataNames(this, "postingphotolayerDatas");
        }
    }
}
