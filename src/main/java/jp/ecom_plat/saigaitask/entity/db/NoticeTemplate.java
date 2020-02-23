/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
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

/**
 * 通知テンプレート
 * 
 */
@Entity
@Table(name = "notice_template")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/10 12:26:25")
@lombok.Getter @lombok.Setter
public class NoticeTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** noticetypeidプロパティ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer noticetypeid;

    /** テンプレート種別 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer noticetemplatetypeid;

    /** 区分 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String templateclass;

    /** タイトル */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String title;

    /** 内容 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String content;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 通知テンプレート種別マスタ */
    @ManyToOne
    @JoinColumn(name="noticetemplatetypeid")
    public NoticetemplatetypeMaster noticetemplatetypeMaster;

    /** 通知種別マスタ */
    @ManyToOne
    @JoinColumn(name="noticetypeid")
    public NoticetypeMaster noticetypeMaster;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** 職員参集情報リスト */
    @OneToMany(mappedBy = "noticeTemplate")
    public List<AssembleInfo> assembleInfoList;
}
