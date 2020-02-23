/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import java.sql.Timestamp;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastAntidisasterNames._PubliccommonsReportDataLastAntidisasterNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastDamageNames._PubliccommonsReportDataLastDamageNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastEventNames._PubliccommonsReportDataLastEventNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastGeneralNames._PubliccommonsReportDataLastGeneralNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastRefugeNames._PubliccommonsReportDataLastRefugeNames;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataLastShelterNames._PubliccommonsReportDataLastShelterNames;
import jp.ecom_plat.saigaitask.entity.names.TrackDataNames._TrackDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsReportData}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2013/07/09 19:47:29")
public class PubliccommonsReportDataNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert sending data";
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
     * trackdataidのプロパティ名を返します。
     *
     * @return trackdataidのプロパティ名
     */
    public static PropertyName<Long> trackdataid() {
        return new PropertyName<Long>("trackdataid");
    }

    /**
     * categoryのプロパティ名を返します。
     *
     * @return categoryのプロパティ名
     */
    public static PropertyName<String> category() {
        return new PropertyName<String>("category");
    }

    /**
     * distributionIdのプロパティ名を返します。
     *
     * @return distributionIdのプロパティ名
     */
    public static PropertyName<String> distributionId() {
        return new PropertyName<String>("distributionId");
    }

    /**
     * documentIdのプロパティ名を返します。
     *
     * @return documentIdのプロパティ名
     */
    public static PropertyName<String> documentId() {
        return new PropertyName<String>("documentId");
    }

    /**
     * documentIdSerialのプロパティ名を返します。
     *
     * @return documentIdSerialのプロパティ名
     */
    public static PropertyName<Integer> documentIdSerial() {
        return new PropertyName<Integer>("documentIdSerial");
    }

    /**
     * documentRevisionのプロパティ名を返します。
     *
     * @return documentRevisionのプロパティ名
     */
    public static PropertyName<Integer> documentRevision() {
        return new PropertyName<Integer>("documentRevision");
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
     * statusのプロパティ名を返します。
     *
     * @return statusのプロパティ名
     */
    public static PropertyName<String> status() {
        return new PropertyName<String>("status");
    }

    /**
     * createtimeのプロパティ名を返します。
     *
     * @return createtimeのプロパティ名
     */
    public static PropertyName<Timestamp> createtime() {
        return new PropertyName<Timestamp>("createtime");
    }

    /**
     * reporttimeのプロパティ名を返します。
     *
     * @return reporttimeのプロパティ名
     */
    public static PropertyName<Timestamp> reporttime() {
        return new PropertyName<Timestamp>("reporttime");
    }

    /**
     * startsendtimeのプロパティ名を返します。
     *
     * @return startsendtimeのプロパティ名
     */
    public static PropertyName<Timestamp> startsendtime() {
        return new PropertyName<Timestamp>("startsendtime");
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
     * successのプロパティ名を返します。
     *
     * @return successのプロパティ名
     */
    public static PropertyName<Boolean> success() {
        return new PropertyName<Boolean>("success");
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
     * distributionTypeのプロパティ名を返します。
     *
     * @return distributionTypeのプロパティ名
     */
    public static PropertyName<String> distributionType() {
        return new PropertyName<String>("distributionType");
    }

    /**
     * validDateTimeのプロパティ名を返します。
     *
     * @return validDateTimeのプロパティ名
     */
    public static PropertyName<String> validDateTime() {
        return new PropertyName<String>("validDateTime");
    }

    /**
     * complementaryinfoのプロパティ名を返します。
     *
     * @return complementaryinfoのプロパティ名
     */
    public static PropertyName<String> complementaryinfo() {
        return new PropertyName<String>("complementaryinfo");
    }

    /**
     * personresponsibleのプロパティ名を返します。
     *
     * @return personresponsibleのプロパティ名
     */
    public static PropertyName<String> personresponsible() {
        return new PropertyName<String>("personresponsible");
    }

    /**
     * organizationnameのプロパティ名を返します。
     *
     * @return organizationnameのプロパティ名
     */
    public static PropertyName<String> organizationname() {
        return new PropertyName<String>("organizationname");
    }

    /**
     * organizationcodeのプロパティ名を返します。
     *
     * @return organizationcodeのプロパティ名
     */
    public static PropertyName<String> organizationcode() {
        return new PropertyName<String>("organizationcode");
    }

    /**
     * organizationdomainnameのプロパティ名を返します。
     *
     * @return organizationdomainnameのプロパティ名
     */
    public static PropertyName<String> organizationdomainname() {
        return new PropertyName<String>("organizationdomainname");
    }

    /**
     * officenameのプロパティ名を返します。
     *
     * @return officenameのプロパティ名
     */
    public static PropertyName<String> officename() {
        return new PropertyName<String>("officename");
    }
    /**
     * officenamekanaのプロパティ名を返します。
     *
     * @return officenamekanaのプロパティ名
     */
    public static PropertyName<String> officenamekana() {
        return new PropertyName<String>("officenamekana");
    }

    /**
     * officelocationareaのプロパティ名を返します。
     *
     * @return officelocationareaのプロパティ名
     */
    public static PropertyName<String> officelocationarea() {
        return new PropertyName<String>("officelocationarea");
    }

    /**
     * phoneのプロパティ名を返します。
     *
     * @return phoneのプロパティ名
     */
    public static PropertyName<String> phone() {
        return new PropertyName<String>("phone");
    }

    /**
     * faxのプロパティ名を返します。
     *
     * @return faxのプロパティ名
     */
    public static PropertyName<String> fax() {
        return new PropertyName<String>("fax");
    }
    /**
     * officedomainnameのプロパティ名を返します。
     *
     * @return officedomainnameのプロパティ名
     */
    public static PropertyName<String> officedomainname() {
        return new PropertyName<String>("officedomainname");
    }

    /**
     * organizationnameeditorialのプロパティ名を返します。
     *
     * @return organizationnameeditorialのプロパティ名
     */
    public static PropertyName<String> organizationnameeditorial() {
        return new PropertyName<String>("organizationnameeditorial");
    }

    /**
     * organizationcodeeditorialのプロパティ名を返します。
     *
     * @return organizationcodeeditorialのプロパティ名
     */
    public static PropertyName<String> organizationcodeeditorial() {
        return new PropertyName<String>("organizationcodeeditorial");
    }

    /**
     * organizationdomainnameeditorialのプロパティ名を返します。
     *
     * @return organizationdomainnameeditorialのプロパティ名
     */
    public static PropertyName<String> organizationdomainnameeditorial() {
        return new PropertyName<String>("organizationdomainnameeditorial");
    }
    /**
     * officenameeditorialのプロパティ名を返します。
     *
     * @return officenameeditorialのプロパティ名
     */
    public static PropertyName<String> officenameeditorial() {
        return new PropertyName<String>("officenameeditorial");
    }

    /**
     * officenamekanaeditorialのプロパティ名を返します。
     *
     * @return officenamekanaeditorialのプロパティ名
     */
    public static PropertyName<String> officenamekanaeditorial() {
        return new PropertyName<String>("officenamekanaeditorial");
    }

    /**
     * officelocationareaeditorialのプロパティ名を返します。
     *
     * @return officelocationareaeditorialのプロパティ名
     */
    public static PropertyName<String> officelocationareaeditorial() {
        return new PropertyName<String>("officelocationareaeditorial");
    }

    /**
     * phoneeditorialのプロパティ名を返します。
     *
     * @return phoneeditorialのプロパティ名
     */
    public static PropertyName<String> phoneeditorial() {
        return new PropertyName<String>("phoneeditorial");
    }
    /**
     * faxeditorialのプロパティ名を返します。
     *
     * @return faxeditorialのプロパティ名
     */
    public static PropertyName<String> faxeditorial() {
        return new PropertyName<String>("faxeditorial");
    }

    /**
     * emaileditorialのプロパティ名を返します。
     *
     * @return emaileditorialのプロパティ名
     */
    public static PropertyName<String> emaileditorial() {
        return new PropertyName<String>("emaileditorial");
    }

    /**
     * officedomainnameeditorialのプロパティ名を返します。
     *
     * @return officedomainnameeditorialのプロパティ名
     */
    public static PropertyName<String> officedomainnameeditorial() {
        return new PropertyName<String>("officedomainnameeditorial");
    }

    /**
     * targetdatetimeのプロパティ名を返します。
     *
     * @return targetdatetimeのプロパティ名
     */
    public static PropertyName<Timestamp> targetdatetime() {
        return new PropertyName<Timestamp>("targetdatetime");
    }

    /**
     * contentdescriptionのプロパティ名を返します。
     *
     * @return contentdescriptionのプロパティ名
     */
    public static PropertyName<String> contentdescription() {
        return new PropertyName<String>("contentdescription");
    }

    /**
     * localgovInfoのプロパティ名を返します。
     *
     * @return localgovInfoのプロパティ名
     */
    public static _TrackDataNames localgovInfo() {
        return new _TrackDataNames("localgovInfo");
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
     * publiccommonsReportDataLastRefugeListのプロパティ名を返します。
     *
     * @return publiccommonsReportDataLastRefugeListのプロパティ名
     */
    public static _PubliccommonsReportDataLastRefugeNames publiccommonsReportDataLastRefugeList() {
        return new _PubliccommonsReportDataLastRefugeNames("publiccommonsReportDataLastRefugeList");
    }

    /**
     * publiccommonsReportDataLastShelterListのプロパティ名を返します。
     *
     * @return publiccommonsReportDataLastShelterListのプロパティ名
     */
    public static _PubliccommonsReportDataLastShelterNames publiccommonsReportDataLastShelterList() {
        return new _PubliccommonsReportDataLastShelterNames("publiccommonsReportDataLastShelterList");
    }

    /**
     * publiccommonsReportDataLastDamageListのプロパティ名を返します。
     *
     * @return publiccommonsReportDataLastDamageListのプロパティ名
     */
    public static _PubliccommonsReportDataLastDamageNames publiccommonsReportDataLastDamageList() {
        return new _PubliccommonsReportDataLastDamageNames("publiccommonsReportDataLastDamageList");
    }

    /**
     * publiccommonsReportDataLastEventListのプロパティ名を返します。
     *
     * @return publiccommonsReportDataLastEventListのプロパティ名
     */
    public static _PubliccommonsReportDataLastEventNames publiccommonsReportDataLastEventList() {
        return new _PubliccommonsReportDataLastEventNames("publiccommonsReportDataLastEventList");
    }

    /**
     * publiccommonsReportDataLastGeneralListのプロパティ名を返します。
     *
     * @return publiccommonsReportDataLastGeneralListのプロパティ名
     */
    public static _PubliccommonsReportDataLastGeneralNames publiccommonsReportDataLastGeneralList() {
        return new _PubliccommonsReportDataLastGeneralNames("publiccommonsReportDataLastGeneralList");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _PubliccommonsReportDataNames extends PropertyName<PubliccommonsReportData> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsReportDataNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _PubliccommonsReportDataNames(final String name) {
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
        public _PubliccommonsReportDataNames(final PropertyName<?> parent, final String name) {
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
         * trackdataidのプロパティ名を返します。
         *
         * @return trackdataidのプロパティ名
         */
        public PropertyName<Long> trackdataid() {
            return new PropertyName<Long>(this, "trackdataid");
        }

        /**
         * categoryのプロパティ名を返します。
         *
         * @return categoryのプロパティ名
         */
        public PropertyName<String> category() {
            return new PropertyName<String>(this, "category");
        }

        /**
         * distributionIdのプロパティ名を返します。
         *
         * @return distributionIdのプロパティ名
         */
        public PropertyName<String> distributionId() {
            return new PropertyName<String>(this, "distributionId");
        }

        /**
         * documentIdのプロパティ名を返します。
         *
         * @return documentIdのプロパティ名
         */
        public PropertyName<String> documentId() {
            return new PropertyName<String>(this, "documentId");
        }

        /**
         * documentIdSerialのプロパティ名を返します。
         *
         * @return documentIdSerialのプロパティ名
         */
        public PropertyName<Integer> documentIdSerial() {
            return new PropertyName<Integer>(this, "documentIdSerial");
        }

        /**
         * documentRevisionのプロパティ名を返します。
         *
         * @return documentRevisionのプロパティ名
         */
        public PropertyName<Integer> documentRevision() {
            return new PropertyName<Integer>(this, "documentRevision");
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
         * statusのプロパティ名を返します。
         *
         * @return statusのプロパティ名
         */
        public PropertyName<String> status() {
            return new PropertyName<String>(this, "status");
        }

        /**
         * createtimeのプロパティ名を返します。
         *
         * @return createtimeのプロパティ名
         */
        public PropertyName<Timestamp> createtime() {
            return new PropertyName<Timestamp>(this, "createtime");
        }

        /**
         * reporttimeのプロパティ名を返します。
         *
         * @return reporttimeのプロパティ名
         */
        public PropertyName<Timestamp> reporttime() {
            return new PropertyName<Timestamp>(this, "reporttime");
        }

        /**
         * startsendtimeのプロパティ名を返します。
         *
         * @return startsendtimeのプロパティ名
         */
        public PropertyName<Timestamp> startsendtime() {
            return new PropertyName<Timestamp>(this, "startsendtime");
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
         * successのプロパティ名を返します。
         *
         * @return successのプロパティ名
         */
        public PropertyName<Boolean> success() {
            return new PropertyName<Boolean>(this, "success");
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
         * distributionTypeのプロパティ名を返します。
         *
         * @return distributionTypeのプロパティ名
         */
        public PropertyName<String> distributionType() {
            return new PropertyName<String>(this, "distributionType");
        }

        /**
         * validDateTimeのプロパティ名を返します。
         *
         * @return validDateTimeのプロパティ名
         */
        public PropertyName<String> validDateTime() {
            return new PropertyName<String>(this, "validDateTime");
        }

        /**
         * complementaryinfoのプロパティ名を返します。
         *
         * @return complementaryinfoのプロパティ名
         */
        public PropertyName<String> complementaryinfo() {
            return new PropertyName<String>(this, "complementaryinfo");
        }

        /**
         * personresponsibleのプロパティ名を返します。
         *
         * @return personresponsibleのプロパティ名
         */
        public PropertyName<String> personresponsible() {
            return new PropertyName<String>(this, "personresponsible");
        }

        /**
         * organizationnameのプロパティ名を返します。
         *
         * @return organizationnameのプロパティ名
         */
        public PropertyName<String> organizationname() {
            return new PropertyName<String>(this, "organizationname");
        }

        /**
         * organizationcodeのプロパティ名を返します。
         *
         * @return organizationcodeのプロパティ名
         */
        public PropertyName<String> organizationcode() {
            return new PropertyName<String>(this, "organizationcode");
        }

        /**
         * organizationdomainnameのプロパティ名を返します。
         *
         * @return organizationdomainnameのプロパティ名
         */
        public PropertyName<String> organizationdomainname() {
            return new PropertyName<String>(this, "organizationdomainname");
        }

        /**
         * officenameのプロパティ名を返します。
         *
         * @return officenameのプロパティ名
         */
        public PropertyName<String> officename() {
            return new PropertyName<String>(this, "officename");
        }

        /**
         * officenamekanaのプロパティ名を返します。
         *
         * @return officenamekanaのプロパティ名
         */
        public PropertyName<String> officenamekana() {
            return new PropertyName<String>(this, "officenamekana");
        }

        /**
         * officelocationareaのプロパティ名を返します。
         *
         * @return officelocationareaのプロパティ名
         */
        public PropertyName<String> officelocationarea() {
            return new PropertyName<String>(this, "officelocationarea");
        }

        /**
         * phoneのプロパティ名を返します。
         *
         * @return phoneのプロパティ名
         */
        public PropertyName<String> phone() {
            return new PropertyName<String>(this, "phone");
        }

        /**
         * faxのプロパティ名を返します。
         *
         * @return faxのプロパティ名
         */
        public PropertyName<String> fax() {
            return new PropertyName<String>(this, "fax");
        }

        /**
         * emailのプロパティ名を返します。
         *
         * @return emailのプロパティ名
         */
        public PropertyName<String> email() {
            return new PropertyName<String>(this, "email");
        }

        /**
         * officedomainnameのプロパティ名を返します。
         *
         * @return officedomainnameのプロパティ名
         */
        public PropertyName<String> officedomainname() {
            return new PropertyName<String>(this, "officedomainname");
        }

        /**
         * organizationnameeditorialのプロパティ名を返します。
         *
         * @return organizationnameeditorialのプロパティ名
         */
        public PropertyName<String> organizationnameeditorial() {
            return new PropertyName<String>(this, "organizationnameeditorial");
        }

        /**
         * organizationcodeeditorialのプロパティ名を返します。
         *
         * @return organizationcodeeditorialのプロパティ名
         */
        public PropertyName<String> organizationcodeeditorial() {
            return new PropertyName<String>(this, "organizationcodeeditorial");
        }

        /**
         * organizationdomainnameeditorialのプロパティ名を返します。
         *
         * @return organizationdomainnameeditorialのプロパティ名
         */
        public PropertyName<String> organizationdomainnameeditorial() {
            return new PropertyName<String>(this, "organizationdomainnameeditorial");
        }

        /**
         * officenameeditorialのプロパティ名を返します。
         *
         * @return officenameeditorialのプロパティ名
         */
        public PropertyName<String> officenameeditorial() {
            return new PropertyName<String>(this, "officenameeditorial");
        }

        /**
         * officenamekanaeditorialのプロパティ名を返します。
         *
         * @return officenamekanaeditorialのプロパティ名
         */
        public PropertyName<String> officenamekanaeditorial() {
            return new PropertyName<String>(this, "officenamekanaeditorial");
        }

        /**
         * officelocationareaeditorialのプロパティ名を返します。
         *
         * @return officelocationareaeditorialのプロパティ名
         */
        public PropertyName<String> officelocationareaeditorial() {
            return new PropertyName<String>(this, "officelocationareaeditorial");
        }

        /**
         * phoneeditorialのプロパティ名を返します。
         *
         * @return phoneeditorialのプロパティ名
         */
        public PropertyName<String> phoneeditorial() {
            return new PropertyName<String>(this, "phoneeditorial");
        }

        /**
         * faxeditorialのプロパティ名を返します。
         *
         * @return faxeditorialのプロパティ名
         */
        public PropertyName<String> faxeditorial() {
            return new PropertyName<String>(this, "faxeditorial");
        }

        /**
         * emaileditorialのプロパティ名を返します。
         *
         * @return emaileditorialのプロパティ名
         */
        public PropertyName<String> emaileditorial() {
            return new PropertyName<String>(this, "emaileditorial");
        }

        /**
         * officedomainnameeditorialのプロパティ名を返します。
         *
         * @return officedomainnameeditorialのプロパティ名
         */
        public PropertyName<String> officedomainnameeditorial() {
            return new PropertyName<String>(this, "officedomainnameeditorial");
        }

        /**
         * targetdatetimeのプロパティ名を返します。
         *
         * @return targetdatetimeのプロパティ名
         */
        public PropertyName<Timestamp> targetdatetime() {
            return new PropertyName<Timestamp>(this, "targetdatetime");
        }

        /**
         * contentdescriptionのプロパティ名を返します。
         *
         * @return contentdescriptionのプロパティ名
         */
        public PropertyName<String> contentdescription() {
            return new PropertyName<String>(this, "contentdescription");
        }

        /**
         * localgovInfoのプロパティ名を返します。
         *
         * @return localgovInfoのプロパティ名
         */
        public _TrackDataNames localgovInfo() {
            return new _TrackDataNames(this, "localgovInfo");
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
         * publiccommonsReportDataLastRefugeListのプロパティ名を返します。
         *
         * @return publiccommonsReportDataLastRefugeListのプロパティ名
         */
        public _PubliccommonsReportDataLastRefugeNames publiccommonsReportDataLastRefugeList() {
            return new _PubliccommonsReportDataLastRefugeNames(this, "publiccommonsReportDataLastRefugeList");
        }

        /**
         * publiccommonsReportDataLastShelterListのプロパティ名を返します。
         *
         * @return publiccommonsReportDataLastShelterListのプロパティ名
         */
        public _PubliccommonsReportDataLastShelterNames publiccommonsReportDataLastShelterList() {
            return new _PubliccommonsReportDataLastShelterNames(this, "publiccommonsReportDataLastShelterList");
        }

        /**
         * publiccommonsReportDataLastDamageListのプロパティ名を返します。
         *
         * @return publiccommonsReportDataLastDamageListのプロパティ名
         */
        public _PubliccommonsReportDataLastDamageNames publiccommonsReportDataLastDamageList() {
            return new _PubliccommonsReportDataLastDamageNames(this, "publiccommonsReportDataLastDamageList");
        }


        /**
         * publiccommonsReportDataLastEventListのプロパティ名を返します。
         *
         * @return publiccommonsReportDataLastEventListのプロパティ名
         */
        public _PubliccommonsReportDataLastEventNames publiccommonsReportDataLastEventList() {
            return new _PubliccommonsReportDataLastEventNames(this, "publiccommonsReportDataLastEventList");
        }


        /**
         * publiccommonsReportDataLastGeneralListのプロパティ名を返します。
         *
         * @return publiccommonsReportDataLastGeneralListのプロパティ名
         */
        public _PubliccommonsReportDataLastGeneralNames publiccommonsReportDataLastGeneralList() {
            return new _PubliccommonsReportDataLastGeneralNames(this, "publiccommonsReportDataLastGeneralList");
        }

        /**
         * publiccommonsReportDataLastAntidisasterDtoのプロパティ名を返します。
         *
         * @return publiccommonsReportDataLastAntidisasterDtoのプロパティ名
         */
        public _PubliccommonsReportDataLastAntidisasterNames publiccommonsReportDataLastAntidisaster() {
            return new _PubliccommonsReportDataLastAntidisasterNames(this, "publiccommonsReportDataLastAntidisaster");
        }
    }
}
