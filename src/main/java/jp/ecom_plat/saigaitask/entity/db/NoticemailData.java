/* Copyright (c) 2013 National Research Institute for Earth Science and
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
 * 通知データ
 *
 */
@Entity
@Table(name = "noticemail_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/09/18 16:23:47")
@lombok.Getter @lombok.Setter
public class NoticemailData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** 宛先 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String mailto;

    /** タイトル */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String title;

    /** 本文 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String content;

    /** 送信時間 */
    @Column(nullable = true, unique = false)
    public Timestamp sendtime;

    /** 送信フラグ */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean send;

    /** noticetypeidプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer noticetypeid;

    /** 添付ファイル保存先パス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String attachfilename;

    /** 災害名称 */
    @Column(length = 2147483647, nullable = true, unique = false)
    public String trackdataname;

    /** 記録データ */
    @ManyToOne
    @JoinColumn(name="trackdataid")
    public TrackData trackData;

    /** 通知種別マスタ */
    @ManyToOne
    @JoinColumn(name="noticetypeid")
    public NoticetypeMaster noticetypeMaster;
}