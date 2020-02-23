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
 * 気象情報XSLT情報
 * 
 */
@Entity
@Table(name = "meteoxslt_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 19:03:56")
@lombok.Getter @lombok.Setter
public class MeteoxsltInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID（０） */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 気象情報種別 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer meteotypeid;

    /** 通知種別 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer noticetypeid;

    /** xsltファイルパス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String filepath;

    /** 通知種別マスタ */
    @ManyToOne
    @JoinColumn(name="noticetypeid")
    public NoticetypeMaster noticetypeMaster;

    /** 気象情報等取得種別マスタ */
    @ManyToOne
    @JoinColumn(name="meteotypeid")
    public MeteotypeMaster meteotypeMaster;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}
