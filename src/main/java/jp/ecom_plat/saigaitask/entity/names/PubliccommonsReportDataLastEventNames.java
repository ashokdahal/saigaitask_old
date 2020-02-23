/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastEvent;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames._PubliccommonsReportDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsReportDataLastEvent}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/04/07 14:07:42")
public class PubliccommonsReportDataLastEventNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert last event sending history data";
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
     * pcommonsreportdataidのプロパティ名を返します。
     *
     * @return pcommonsreportdataidのプロパティ名
     */
    public static PropertyName<Long> pcommonsreportdataid() {
        return new PropertyName<Long>("pcommonsreportdataid");
    }

    /**
     * divisionのプロパティ名を返します。
     *
     * @return divisionのプロパティ名
     */
    public static PropertyName<String> division() {
    	return new PropertyName<String>("division");
    }

    /**
     * mailtitleのプロパティ名を返します。
     *
     * @return mailtitleのプロパティ名
     */
    public static PropertyName<String> mailtitle() {
    	return new PropertyName<String>("mailtitle");
    }

    /**
     * filecaptionのプロパティ名を返します。
     *
     * @return filecaptionのプロパティ名
     */
    public static PropertyName<String> filecaption() {
    	return new PropertyName<String>("filecaption");
    }

    /**
     * validdatetimeのプロパティ名を返します。
     *
     * @return validdatetimeのプロパティ名
     */
    public static PropertyName<String> validdatetime() {
    	return new PropertyName<String>("validdatetime");
    }

    /**
     * distributiontypeのプロパティ名を返します。
     *
     * @return distributiontypeのプロパティ名
     */
    public static PropertyName<String> distributiontype() {
    	return new PropertyName<String>("distributiontype");
    }

    /**
     * eventfromのプロパティ名を返します。
     *
     * @return eventfromのプロパティ名
     */
    public static PropertyName<String> eventfrom() {
    	return new PropertyName<String>("eventfrom");
    }

    /**
     * eventtoのプロパティ名を返します。
     *
     * @return eventtoのプロパティ名
     */
    public static PropertyName<String> eventto() {
    	return new PropertyName<String>("eventto");
    }

    /**
     * eventfeeのプロパティ名を返します。
     *
     * @return eventfeeのプロパティ名
     */
    public static PropertyName<String> eventfee() {
    	return new PropertyName<String>("eventfee");
    }

    /**
     * disasterinformationtypeのプロパティ名を返します。
     *
     * @return disasterinformationtypeのプロパティ名
     */
    public static PropertyName<String> disasterinformationtype() {
    	return new PropertyName<String>("disasterinformationtype");
    }

    /**
     * areaのプロパティ名を返します。
     *
     * @return areaのプロパティ名
     */
    public static PropertyName<String> area() {
    	return new PropertyName<String>("area");
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
     * textのプロパティ名を返します。
     *
     * @return textのプロパティ名
     */
    public static PropertyName<String> text() {
    	return new PropertyName<String>("text");
    }

    /**
     * notificationuriのプロパティ名を返します。
     *
     * @return notificationuriのプロパティ名
     */
    public static PropertyName<String> notificationuri() {
    	return new PropertyName<String>("notificationuri");
    }

    /**
     * fileuriのプロパティ名を返します。
     *
     * @return fileuriのプロパティ名
     */
    public static PropertyName<String> fileuri() {
    	return new PropertyName<String>("fileuri");
    }

    /**
     * mediatypeのプロパティ名を返します。
     *
     * @return mediatypeのプロパティ名
     */
    public static PropertyName<String> mediatype() {
    	return new PropertyName<String>("mediatype");
    }

    /**
     * mimetypeのプロパティ名を返します。
     *
     * @return mimetypeのプロパティ名
     */
    public static PropertyName<String> mimetype() {
    	return new PropertyName<String>("mimetype");
    }

    /**
     * documentidのプロパティ名を返します。
     *
     * @return documentidのプロパティ名
     */
    public static PropertyName<String> documentid() {
    	return new PropertyName<String>("documentid");
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
     * localgovinfoidのプロパティ名を返します。
     *
     * @return localgovinfoidのプロパティ名
     */
    public static PropertyName<Long> localgovinfoid() {
    	return new PropertyName<Long>("localgovinfoid");
    }

    /**
     * documentrevisionのプロパティ名を返します。
     *
     * @return documentrevisionのプロパティ名
     */
    public static PropertyName<Long> documentrevision() {
    	return new PropertyName<Long>("documentrevision");
    }

    /**
     * publiccommonsReportDataのプロパティ名を返します。
     *
     * @return publiccommonsReportDataのプロパティ名
     */
    public static _PubliccommonsReportDataNames publiccommonsReportData() {
        return new _PubliccommonsReportDataNames("publiccommonsReportData");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _PubliccommonsReportDataLastEventNames extends PropertyName<PubliccommonsReportDataLastEvent> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsReportDataLastEventNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _PubliccommonsReportDataLastEventNames(final String name) {
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
        public _PubliccommonsReportDataLastEventNames(final PropertyName<?> parent, final String name) {
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
         * pcommonsreportdataidのプロパティ名を返します。
         *
         * @return pcommonsreportdataidのプロパティ名
         */
        public PropertyName<Long> pcommonsreportdataid() {
            return new PropertyName<Long>(this, "pcommonsreportdataid");
        }

        /**
         * divisionのプロパティ名を返します。
         *
         * @return divisionのプロパティ名
         */
        public PropertyName<String> division() {
            return new PropertyName<String>(this, "division");
        }

        /**
         * mailtitleのプロパティ名を返します。
         *
         * @return mailtitleのプロパティ名
         */
        public PropertyName<String> mailtitle() {
            return new PropertyName<String>(this, "mailtitle");
        }

        /**
         * filecaptionのプロパティ名を返します。
         *
         * @return filecaptionのプロパティ名
         */
        public PropertyName<String> filecaption() {
            return new PropertyName<String>(this, "filecaption");
        }
        /**
         * validdatetimeのプロパティ名を返します。
         *
         * @return validdatetimeのプロパティ名
         */
        public PropertyName<String> validdatetime() {
            return new PropertyName<String>(this, "validdatetime");
        }

        /**
         * distributiontypeのプロパティ名を返します。
         *
         * @return distributiontypeのプロパティ名
         */
        public PropertyName<String> distributiontype() {
            return new PropertyName<String>(this, "distributiontype");
        }

        /**
         * eventfromのプロパティ名を返します。
         *
         * @return eventfromのプロパティ名
         */
        public PropertyName<String> eventfrom() {
            return new PropertyName<String>(this, "eventfrom");
        }

        /**
         * eventtoのプロパティ名を返します。
         *
         * @return eventtoのプロパティ名
         */
        public PropertyName<String> eventto() {
            return new PropertyName<String>(this, "eventto");
        }

        /**
         * eventfeeのプロパティ名を返します。
         *
         * @return eventfeeのプロパティ名
         */
        public PropertyName<String> eventfee() {
            return new PropertyName<String>(this, "eventfee");
        }

        /**
         * disasterinformationtypeのプロパティ名を返します。
         *
         * @return disasterinformationtypeのプロパティ名
         */
        public PropertyName<String> disasterinformationtype() {
            return new PropertyName<String>(this, "disasterinformationtype");
        }

        /**
         * areaのプロパティ名を返します。
         *
         * @return areaのプロパティ名
         */
        public PropertyName<String> area() {
            return new PropertyName<String>(this, "area");
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
         * notificationuriのプロパティ名を返します。
         *
         * @return notificationuriのプロパティ名
         */
        public PropertyName<String> notificationuri() {
            return new PropertyName<String>(this, "notificationuri");
        }

        /**
         * fileuriのプロパティ名を返します。
         *
         * @return fileuriのプロパティ名
         */
        public PropertyName<String> fileuri() {
            return new PropertyName<String>(this, "fileuri");
        }

        /**
         * mediatypeのプロパティ名を返します。
         *
         * @return mediatypeのプロパティ名
         */
        public PropertyName<String> mediatype() {
            return new PropertyName<String>(this, "mediatype");
        }

        /**
         * mimetypeのプロパティ名を返します。
         *
         * @return mimetypeのプロパティ名
         */
        public PropertyName<String> mimetype() {
            return new PropertyName<String>(this, "mimetype");
        }

        /**
         * documentidのプロパティ名を返します。
         *
         * @return documentidのプロパティ名
         */
        public PropertyName<String> documentid() {
            return new PropertyName<String>(this, "documentid");
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
         * localgovinfoidのプロパティ名を返します。
         *
         * @return localgovinfoidのプロパティ名
         */
        public PropertyName<Long> localgovinfoid() {
            return new PropertyName<Long>(this, "localgovinfoid");
        }

        /**
         * documentrevisionのプロパティ名を返します。
         *
         * @return documentrevisionのプロパティ名
         */
        public PropertyName<Long> documentrevision() {
            return new PropertyName<Long>(this, "documentrevision");
        }

        /**
         * publiccommonsReportDataのプロパティ名を返します。
         *
         * @return publiccommonsReportDataのプロパティ名
         */
        public _PubliccommonsReportDataNames publiccommonsReportData() {
            return new _PubliccommonsReportDataNames(this, "publiccommonsReportData");
        }
    }
}
