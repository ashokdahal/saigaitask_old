/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.AssemblestateData;
import jp.ecom_plat.saigaitask.entity.names.SafetystateInfoNames._SafetystateInfoNames;
import jp.ecom_plat.saigaitask.entity.names.SafetystateMasterNames._SafetystateMasterNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;
import jp.ecom_plat.saigaitask.entity.names.UserInfoNames._UserInfoNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link AssemblestateData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/17 19:40:55")
public class AssemblestateDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Staff request sending data";
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
     * trackdataidのプロパティ名を返します。
     *
     * @return trackdataidのプロパティ名
     */
    public static PropertyName<Long> trackdataid() {
        return new PropertyName<Long>("trackdataid");
    }

    /**
     * useridのプロパティ名を返します。
     *
     * @return useridのプロパティ名
     */
    public static PropertyName<Long> userid() {
        return new PropertyName<Long>("userid");
    }

    /**
     * safetystateidのプロパティ名を返します。
     *
     * @return safetystateidのプロパティ名
     */
    public static PropertyName<Integer> safetystateid() {
        return new PropertyName<Integer>("safetystateid");
    }

    /**
     * safetystateinfoidのプロパティ名を返します。
     *
     * @return safetystateinfoidのプロパティ名
     */
    public static PropertyName<Integer> safetystateinfoid() {
        return new PropertyName<Integer>("safetystateinfoid");
    }

    /**
     * groupnameのプロパティ名を返します。
     *
     * @return groupnameのプロパティ名
     */
    public static PropertyName<String> groupname() {
        return new PropertyName<String>("groupname");
    }

    /**
     * unitnameのプロパティ名を返します。
     *
     * @return unitnameのプロパティ名
     */
    public static PropertyName<String> unitname() {
        return new PropertyName<String>("unitname");
    }

    /**
     * usernameのプロパティ名を返します。
     *
     * @return usernameのプロパティ名
     */
    public static PropertyName<String> username() {
        return new PropertyName<String>("username");
    }

    /**
     * staffnoのプロパティ名を返します。
     *
     * @return staffnoのプロパティ名
     */
    public static PropertyName<String> staffno() {
        return new PropertyName<String>("staffno");
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
     * commentのプロパティ名を返します。
     *
     * @return commentのプロパティ名
     */
    public static PropertyName<String> comment() {
        return new PropertyName<String>("comment");
    }

    /**
     * loginstatetusのプロパティ名を返します。
     *
     * @return loginstatetusのプロパティ名
     */
    public static PropertyName<String> loginstatetus() {
        return new PropertyName<String>("loginstatetus");
    }

    /**
     * registtimeのプロパティ名を返します。
     *
     * @return registtimeのプロパティ名
     */
    public static PropertyName<Timestamp> registtime() {
        return new PropertyName<Timestamp>("registtime");
    }

    /**
     * updatetimeのプロパティ名を返します。
     *
     * @return updatetimeのプロパティ名
     */
    public static PropertyName<Timestamp> updatetime() {
        return new PropertyName<Timestamp>("updatetime");
    }

    /**
     * trackDataのプロパティ名を返します。
     *
     * @return trackDataのプロパティ名
     */
    public static _TrackDataNames trackData() {
        return new _TrackDataNames("trackData");
    }

    /**
     * userInfoのプロパティ名を返します。
     *
     * @return userInfoのプロパティ名
     */
    public static _UserInfoNames userInfo() {
        return new _UserInfoNames("userInfo");
    }

    /**
     * safetystateMasterのプロパティ名を返します。
     *
     * @return safetystateMasterのプロパティ名
     */
    public static _SafetystateMasterNames safetystateMaster() {
        return new _SafetystateMasterNames("safetystateMaster");
    }

    /**
     * safetystateInfoのプロパティ名を返します。
     *
     * @return safetystateInfoのプロパティ名
     */
    public static _SafetystateInfoNames safetystateInfo() {
        return new _SafetystateInfoNames("safetystateInfo");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _AssemblestateDataNames extends PropertyName<AssemblestateData> {

        /**
         * インスタンスを構築します。
         */
        public _AssemblestateDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _AssemblestateDataNames(final String name) {
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
        public _AssemblestateDataNames(final PropertyName<?> parent, final String name) {
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
         * trackdataidのプロパティ名を返します。
         *
         * @return trackdataidのプロパティ名
         */
        public PropertyName<Long> trackdataid() {
            return new PropertyName<Long>(this, "trackdataid");
        }

        /**
         * useridのプロパティ名を返します。
         *
         * @return useridのプロパティ名
         */
        public PropertyName<Long> userid() {
            return new PropertyName<Long>(this, "userid");
        }

        /**
         * safetystateidのプロパティ名を返します。
         *
         * @return safetystateidのプロパティ名
         */
        public PropertyName<Integer> safetystateid() {
            return new PropertyName<Integer>(this, "safetystateid");
        }

        /**
         * safetystateinfoidのプロパティ名を返します。
         *
         * @return safetystateinfoidのプロパティ名
         */
        public PropertyName<Integer> safetystateinfoid() {
            return new PropertyName<Integer>("safetystateinfoid");
        }

        /**
         * groupnameのプロパティ名を返します。
         *
         * @return groupnameのプロパティ名
         */
        public PropertyName<String> groupname() {
            return new PropertyName<String>(this, "groupname");
        }

        /**
         * unitnameのプロパティ名を返します。
         *
         * @return unitnameのプロパティ名
         */
        public PropertyName<String> unitname() {
            return new PropertyName<String>(this, "unitname");
        }

        /**
         * usernameのプロパティ名を返します。
         *
         * @return usernameのプロパティ名
         */
        public PropertyName<String> username() {
            return new PropertyName<String>(this, "username");
        }

        /**
         * staffnoのプロパティ名を返します。
         *
         * @return staffnoのプロパティ名
         */
        public PropertyName<String> staffno() {
            return new PropertyName<String>(this, "staffno");
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
         * commentのプロパティ名を返します。
         *
         * @return commentのプロパティ名
         */
        public PropertyName<String> comment() {
            return new PropertyName<String>("comment");
        }

        /**
         * loginstatetusのプロパティ名を返します。
         *
         * @return loginstatetusのプロパティ名
         */
        public PropertyName<String> loginstatetus() {
            return new PropertyName<String>("loginstatetus");
        }

        /**
         * registtimeのプロパティ名を返します。
         *
         * @return registtimeのプロパティ名
         */
        public PropertyName<Timestamp> registtime() {
            return new PropertyName<Timestamp>(this, "registtime");
        }

        /**
         * updatetimeのプロパティ名を返します。
         *
         * @return updatetimeのプロパティ名
         */
        public PropertyName<Timestamp> updatetime() {
            return new PropertyName<Timestamp>(this, "updatetime");
        }

        /**
         * trackDataのプロパティ名を返します。
         *
         * @return trackDataのプロパティ名
         */
        public _TrackDataNames trackData() {
            return new _TrackDataNames(this, "trackData");
        }

        /**
         * userInfoのプロパティ名を返します。
         *
         * @return userInfoのプロパティ名
         */
        public _UserInfoNames userInfo() {
            return new _UserInfoNames(this, "userInfo");
        }

        /**
         * safetystateMasterのプロパティ名を返します。
         *
         * @return safetystateMasterのプロパティ名
         */
        public _SafetystateMasterNames safetystateMaster() {
            return new _SafetystateMasterNames(this, "safetystateMaster");
        }

        /**
         * safetystateInfoのプロパティ名を返します。
         *
         * @return safetystateInfoのプロパティ名
         */
        public _SafetystateInfoNames safetystateInfo() {
            return new _SafetystateInfoNames("safetystateInfo");
        }
    }
}
