/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.names.NoticetypeMasterNames._NoticetypeMasterNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link NoticemailData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/09/18 16:24:18")
public class NoticemailDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Notice data";
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
     * mailtoのプロパティ名を返します。
     * 
     * @return mailtoのプロパティ名
     */
    public static PropertyName<String> mailto() {
        return new PropertyName<String>("mailto");
    }

    /**
     * titleのプロパティ名を返します。
     * 
     * @return titleのプロパティ名
     */
    public static PropertyName<String> title() {
        return new PropertyName<String>("title");
    }

    /**
     * contentのプロパティ名を返します。
     * 
     * @return contentのプロパティ名
     */
    public static PropertyName<String> content() {
        return new PropertyName<String>("content");
    }

    /**
     * sendtimeのプロパティ名を返します。
     * 
     * @return sendtimeのプロパティ名
     */
    public static PropertyName<Timestamp> sendtime() {
        return new PropertyName<Timestamp>("sendtime");
    }

    /**
     * sendのプロパティ名を返します。
     * 
     * @return sendのプロパティ名
     */
    public static PropertyName<Boolean> send() {
        return new PropertyName<Boolean>("send");
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
     * attachfilenameのプロパティ名を返します。
     * 
     * @return attachfilenameのプロパティ名
     */
    public static PropertyName<String> attachfilename() {
        return new PropertyName<String>("attachfilename");
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
     * noticetypeMasterのプロパティ名を返します。
     * 
     * @return noticetypeMasterのプロパティ名
     */
    public static _NoticetypeMasterNames noticetypeMaster() {
        return new _NoticetypeMasterNames("noticetypeMaster");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _NoticemailDataNames extends PropertyName<NoticemailData> {

        /**
         * インスタンスを構築します。
         */
        public _NoticemailDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _NoticemailDataNames(final String name) {
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
        public _NoticemailDataNames(final PropertyName<?> parent, final String name) {
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
         * mailtoのプロパティ名を返します。
         *
         * @return mailtoのプロパティ名
         */
        public PropertyName<String> mailto() {
            return new PropertyName<String>(this, "mailto");
        }

        /**
         * titleのプロパティ名を返します。
         *
         * @return titleのプロパティ名
         */
        public PropertyName<String> title() {
            return new PropertyName<String>(this, "title");
        }

        /**
         * contentのプロパティ名を返します。
         *
         * @return contentのプロパティ名
         */
        public PropertyName<String> content() {
            return new PropertyName<String>(this, "content");
        }

        /**
         * sendtimeのプロパティ名を返します。
         *
         * @return sendtimeのプロパティ名
         */
        public PropertyName<Timestamp> sendtime() {
            return new PropertyName<Timestamp>(this, "sendtime");
        }

        /**
         * sendのプロパティ名を返します。
         *
         * @return sendのプロパティ名
         */
        public PropertyName<Boolean> send() {
            return new PropertyName<Boolean>(this, "send");
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
         * attachfilenameのプロパティ名を返します。
         *
         * @return attachfilenameのプロパティ名
         */
        public PropertyName<String> attachfilename() {
            return new PropertyName<String>(this, "attachfilename");
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
         * noticetypeMasterのプロパティ名を返します。
         * 
         * @return noticetypeMasterのプロパティ名
         */
        public _NoticetypeMasterNames noticetypeMaster() {
            return new _NoticetypeMasterNames(this, "noticetypeMaster");
        }
    }
}
