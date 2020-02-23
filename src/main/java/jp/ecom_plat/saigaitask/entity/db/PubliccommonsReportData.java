/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.OneToOne;

/**
 * 公共情報コモンズ発信データ
 *
 */
@Entity
@Table(name = "publiccommons_report_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 16:51:19")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"publiccommonsReportDataLastRefugeList","publiccommonsReportDataLastShelterList","publiccommonsReportDataLastDamageList","publiccommonsReportDataLastEventList","publiccommonsReportDataLastGeneralList"})
public class PubliccommonsReportData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 記録データID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** 情報種別 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String category;

    /** メッセージID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String distributionId;

    /** ドキュメントID */
    @Column(length = 2147483647, nullable = true, unique = false)
    public String documentId;

    /** ドキュメントID連番 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer documentIdSerial;

    /** ドキュメント版番号 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer documentRevision;

    /** ファイル名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String filename;

    /** ステータス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String status;

    /** 作成日時 */
    @Column(nullable = true, unique = false)
    public Timestamp createtime;

    /** 発表日時 */
    @Column(nullable = true, unique = false)
    public Timestamp reporttime;

    /** 初版作成日時 */
    @Column(nullable = true, unique = false)
    public Timestamp startsendtime;

    /** 発信成功日時 */
    @Column(nullable = true, unique = false)
    public Timestamp sendtime;

    /** 発信成功フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean success;

    /** 登録日時 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

    /** 更新種別 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String distributionType;

    /** 希望公開終了日時 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String validDateTime;

    /** 補足情報 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String complementaryinfo;

    /** 発表組織 担当者名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String personresponsible;

    /** 発表組織 組織名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String organizationname;

    /** 発表組織 地方公共団体コード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String organizationcode;

    /** 発表組織 組織ドメイン */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String organizationdomainname;

    /** 発表組織 部署名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officename;

    /** 発表組織 部署名(カナ) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officenamekana;

    /** 発表組織 部署住所 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officelocationarea;

    /** 発表組織 部署電話番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String phone;

    /** 発表組織 部署FAX */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String fax;

    /** 発表組織 部署メールアドレス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String email;

    /** 発表組織 部署ドメイン */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officedomainname;

    /** 作成組織 組織名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String organizationnameeditorial;

    /** 作成組織 地方公共団体コード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String organizationcodeeditorial;

    /** 作成組織 組織ドメイン */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String organizationdomainnameeditorial;

    /** 作成組織 部署名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officenameeditorial;

    /** 作成組織 部署名(カナ) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officenamekanaeditorial;

    /** 作成組織 部署住所 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officelocationareaeditorial;

    /** 作成組織 部署電話番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String phoneeditorial;

    /** 作成組織 部署FAX */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String faxeditorial;

    /** 作成組織 部署メールアドレス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String emaileditorial;

    /** 作成組織 部署ドメイン */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officedomainnameeditorial;

    /** 希望公開開始日時 */
    @Column(nullable = true, unique = false)
    public Timestamp targetdatetime;

    /** 見出し  */
    @Column(nullable = true, unique = false)
    public String contentdescription;

    /** 自治体ID */
	@ManyToOne
	@JoinColumn(name = "localgovinfoid")
	public TrackData localgovInfo;

	/** 記録データ */
	@ManyToOne
	@JoinColumn(name = "trackdataid")
	public TrackData trackData;

	/** 避難勧告／避難指示 */
    @OneToMany(mappedBy = "publiccommonsReportData")
    public List<PubliccommonsReportDataLastRefuge> publiccommonsReportDataLastRefugeList;

	/** 避難所 */
    @OneToMany(mappedBy = "publiccommonsReportData")
    public List<PubliccommonsReportDataLastShelter> publiccommonsReportDataLastShelterList;

	/** 被害情報 */
    @OneToMany(mappedBy = "publiccommonsReportData")
    public List<PubliccommonsReportDataLastDamage> publiccommonsReportDataLastDamageList;

	/** イベント */
    @OneToMany(mappedBy = "publiccommonsReportData")
    public List<PubliccommonsReportDataLastEvent> publiccommonsReportDataLastEventList;

	/** お知らせ */
    @OneToMany(mappedBy = "publiccommonsReportData")
    public List<PubliccommonsReportDataLastGeneral> publiccommonsReportDataLastGeneralList;

	/** 災害対策本部設置状況 */
    @OneToOne(mappedBy = "publiccommonsReportData")
    public PubliccommonsReportDataLastAntidisaster publiccommonsReportDataLastAntidisaster;
}
