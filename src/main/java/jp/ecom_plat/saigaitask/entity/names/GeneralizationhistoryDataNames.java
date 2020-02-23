/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.GeneralizationhistoryData;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link GeneralizationhistoryData}のプロパティ名の集合です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/09/09 18:18:57")
public class GeneralizationhistoryDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "Summary history data";
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

//    /**
//     * menutypeidのプロパティ名を返します。
//     * 
//     * @return menutypeidのプロパティ名
//     */
//    public static PropertyName<Long> menutypeid() {
//        return new PropertyName<Long>("menutypeid");
//    }

    /**
     * pagetypeのプロパティ名を返します。
     * 
     * @return pagetypeのプロパティ名
     */
    public static PropertyName<String> pagetype() {
        return new PropertyName<String>("pagetype");
    }

    /**
     * listidのプロパティ名を返します。
     * 
     * @return listidのプロパティ名
     */
    public static PropertyName<String> listid() {
        return new PropertyName<String>("listid");
    }

    /**
     * csvpathのプロパティ名を返します。
     * 
     * @return csvpathのプロパティ名
     */
    public static PropertyName<String> csvpath() {
        return new PropertyName<String>("csvpath");
    }

    /**
     * pdfpathのプロパティ名を返します。
     * 
     * @return pdfpathのプロパティ名
     */
    public static PropertyName<String> pdfpath() {
        return new PropertyName<String>("pdfpath");
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
     * registtimeのプロパティ名を返します。
     * 
     * @return registtimeのプロパティ名
     */
    public static PropertyName<Timestamp> registtime() {
        return new PropertyName<Timestamp>("registtime");
    }

    /**
     * deletedのプロパティ名を返します。
     * 
     * @return deletedのプロパティ名
     */
    public static PropertyName<Boolean> deleted() {
        return new PropertyName<Boolean>("deleted");
    }

    /**
     * trackDataのプロパティ名を返します。
     * 
     * @return trackDataのプロパティ名
     */
    public static _TrackDataNames trackData() {
        return new _TrackDataNames("trackData");
    }

//    /**
//     * menutypeMasterのプロパティ名を返します。
//     * 
//     * @return menutypeMasterのプロパティ名
//     */
//    public static _MenutypeMasterNames menutypeMaster() {
//        return new _MenutypeMasterNames("menutypeMaster");
//    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _GeneralizationhistoryDataNames extends PropertyName<GeneralizationhistoryData> {

        /**
         * インスタンスを構築します。
         */
        public _GeneralizationhistoryDataNames() {
        }

        /**
         * インスタンスを構築します。
         * 
         * @param name
         *            名前
         */
        public _GeneralizationhistoryDataNames(final String name) {
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
        public _GeneralizationhistoryDataNames(final PropertyName<?> parent, final String name) {
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

//        /**
//         * menutypeidのプロパティ名を返します。
//         *
//         * @return menutypeidのプロパティ名
//         */
//        public PropertyName<Long> menutypeid() {
//            return new PropertyName<Long>(this, "menutypeid");
//        }

        /**
         * pagetypeのプロパティ名を返します。
         *
         * @return pagetypeのプロパティ名
         */
        public PropertyName<String> pagetype() {
            return new PropertyName<String>(this, "pagetype");
        }

        /**
         * listidのプロパティ名を返します。
         *
         * @return listidのプロパティ名
         */
        public PropertyName<String> listid() {
            return new PropertyName<String>(this, "listid");
        }

        /**
         * csvpathのプロパティ名を返します。
         *
         * @return csvpathのプロパティ名
         */
        public PropertyName<String> csvpath() {
            return new PropertyName<String>(this, "csvpath");
        }

        /**
         * pdfpathのプロパティ名を返します。
         *
         * @return pdfpathのプロパティ名
         */
        public PropertyName<String> pdfpath() {
            return new PropertyName<String>(this, "pdfpath");
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
         * registtimeのプロパティ名を返します。
         *
         * @return registtimeのプロパティ名
         */
        public PropertyName<Timestamp> registtime() {
            return new PropertyName<Timestamp>(this, "registtime");
        }

        /**
         * deletedのプロパティ名を返します。
         *
         * @return deletedのプロパティ名
         */
        public PropertyName<Boolean> deleted() {
            return new PropertyName<Boolean>(this, "deleted");
        }

        /**
         * trackDataのプロパティ名を返します。
         * 
         * @return trackDataのプロパティ名
         */
        public _TrackDataNames trackData() {
            return new _TrackDataNames(this, "trackData");
        }

//        /**
//         * menutypeMasterのプロパティ名を返します。
//         * 
//         * @return menutypeMasterのプロパティ名
//         */
//        public _MenutypeMasterNames menutypeMaster() {
//            return new _MenutypeMasterNames(this, "menutypeMaster");
//        }
    }
}
