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
 * 意思決定支援レイヤ情報
 *
 */
@Entity
@Table(name = "decisionsupport_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2016/02/26 16:35:23")
@lombok.Getter @lombok.Setter
public class DecisionsupportInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 意思決定支援レイヤ種別ID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer decisionsupporttypeid;

    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** 属性ID1 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String attrid1;

    /** 属性ID2 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String attrid2;

    /** 演算対象日数と避難所率 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String calculation_method;

    /** バッファ */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer buffer;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 有効無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** 意思決定支援レイヤ種別マスタ */
    @ManyToOne
    @JoinColumn(name="decisionsupporttypeid")
    public DecisionsupporttypeMaster decisionsupporttypeMaster;

    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;
}
