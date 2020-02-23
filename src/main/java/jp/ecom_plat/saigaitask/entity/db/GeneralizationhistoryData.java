/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 総括表履歴データ
 *
 */
@Entity
@Table(name = "generalizationhistory_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/09/09 17:42:30")
@lombok.Getter @lombok.Setter
public class GeneralizationhistoryData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

//  /** メニュータイプID */
//  @Column(precision = 19, nullable = true, unique = false)
//  public Long menutypeid;

    /** ページタイプID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String pagetype;

    /** リストID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String listid;

    /** CSVファイルパス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String csvpath;

    /** PDFファイルパス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String pdfpath;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 登録日時 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

    /** 削除フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean deleted;

    /** 記録データ */
    @ManyToOne
	@JoinColumn(name = "trackdataid")
	public TrackData trackData;

//    /** メニュータイプマスタ */
//    @ManyToOne
//    @JoinColumn(name="menutypeid")
//    public MenutypeMaster menutypeMaster;

}
