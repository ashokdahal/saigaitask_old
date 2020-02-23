/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;

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
 * 職員参集情報
 * 
 */
@Entity
@Table(name = "assemble_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/17 19:33:58")
@lombok.Getter @lombok.Setter
public class AssembleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 体制ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long stationclassinfoid;

    /** テンプレートID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long noticetemplateid;

    /** 通知グループID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long noticegroupinfoid;

    /** 参集名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 通知グループ情報 */
    @ManyToOne
    @JoinColumn(name="noticegroupinfoid")
    public NoticegroupInfo noticegroupInfo;

    /** 通知テンプレート */
    @ManyToOne
    @JoinColumn(name="noticetemplateid")
    public NoticeTemplate noticeTemplate;

    /** 体制区分 */
    @ManyToOne
    @JoinColumn(name="stationclassinfoid")
    public StationclassInfo stationclassInfo;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}
