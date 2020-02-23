/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.JalertreceivefileData;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames._LocalgovInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link JalertreceivefileData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2015/03/13 20:19:00")
public class JalertreceivefileDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "JAlert receive data info";
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
     * jalertrequestinfoidのプロパティ名を返します。
     * 
     * @return jalertrequestinfoidのプロパティ名
     */
    public static PropertyName<Long> jalertrequestinfoid() {
        return new PropertyName<Long>("jalertrequestinfoid");
    }

    /**
     * jalerttypeidのプロパティ名を返します。
     *
     * @return jalerttypeidのプロパティ名
     */
    public static PropertyName<Integer> jalerttypeid() {
        return new PropertyName<Integer>("jalerttypeid");
    }

    /**
     * receivetimeのプロパティ名を返します。
     *
     * @return receivetimeのプロパティ名
     */
    public static PropertyName<Timestamp> receivetime() {
        return new PropertyName<Timestamp>("receivetime");
    }

    /**
     * orgtextfilenameのプロパティ名を返します。
     *
     * @return orgtextfilenameのプロパティ名
     */
    public static PropertyName<String> orgtextfilename() {
        return new PropertyName<String>("orgtextfilename");
    }

    /**
     * orgdatafilenameのプロパティ名を返します。
     *
     * @return orgdatafilenameのプロパティ名
     */
    public static PropertyName<String> orgdatafilename() {
        return new PropertyName<String>("orgdatafilename");
    }

    /**
     * textfilepathのプロパティ名を返します。
     *
     * @return textfilepathのプロパティ名
     */
    public static PropertyName<String> textfilepath() {
        return new PropertyName<String>("textfilepath");
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
    public static class _JalertreceivefileDataNames extends PropertyName<JalertreceivefileData> {

        /**
         * インスタンスを構築します。
         */
        public _JalertreceivefileDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _JalertreceivefileDataNames(final String name) {
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
        public _JalertreceivefileDataNames(final PropertyName<?> parent, final String name) {
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
         * jalertrequestinfoidのプロパティ名を返します。
         *
         * @return jalertrequestinfoidのプロパティ名
         */
        public PropertyName<Long> jalertrequestinfoid() {
            return new PropertyName<Long>(this, "jalertrequestinfoid");
        }

        /**
         * jalerttypeidのプロパティ名を返します。
         *
         * @return jalerttypeidのプロパティ名
         */
        public PropertyName<Integer> jalerttypeid() {
            return new PropertyName<Integer>(this, "jalerttypeid");
        }

        /**
         * receivetimeのプロパティ名を返します。
         *
         * @return receivetimeのプロパティ名
         */
        public PropertyName<Timestamp> receivetime() {
            return new PropertyName<Timestamp>(this, "receivetime");
        }

        /**
         * orgtextfilenameのプロパティ名を返します。
         *
         * @return orgtextfilenameのプロパティ名
         */
        public PropertyName<String> orgtextfilename() {
            return new PropertyName<String>(this, "orgtextfilename");
        }

        /**
         * orgdatafilenameのプロパティ名を返します。
         *
         * @return orgdatafilenameのプロパティ名
         */
        public PropertyName<String> orgdatafilename() {
            return new PropertyName<String>(this, "orgdatafilename");
        }

        /**
         * textfilepathのプロパティ名を返します。
         *
         * @return textfilepathのプロパティ名
         */
        public PropertyName<String> textfilepath() {
            return new PropertyName<String>(this, "textfilepath");
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
         * localgovInfoのプロパティ名を返します。
         * 
         * @return localgovInfoのプロパティ名
         */
        public _LocalgovInfoNames localgovInfo() {
            return new _LocalgovInfoNames(this, "localgovInfo");
        }
    }
}
