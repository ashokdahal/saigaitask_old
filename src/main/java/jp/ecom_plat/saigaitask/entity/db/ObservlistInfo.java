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
 * 監視観測リスト情報
 *
 */
@Entity
@Table(name = "observlist_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/02/24 18:39:28")
@lombok.Getter @lombok.Setter
public class ObservlistInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 監視観測メニューID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long observmenuinfoid;

    /** 監視観測ID */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer observid;

    /** 観測所ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long observatoryinfoid;

    /** データ項目コード */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer itemcode;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 監視観測メニュー情報 */
    @ManyToOne
    @JoinColumn(name="observmenuinfoid")
    public ObservmenuInfo observmenuInfo;

    /** 監視観測マスタ */
    @JoinColumn(name="observid")
    @ManyToOne
    public ObservMaster observMaster;
}
