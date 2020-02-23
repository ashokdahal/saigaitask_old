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
 * テレメータ管理事務所情報
 *
 */
@Entity
@Table(name = "telemeteroffice_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2014/02/20 19:38:28")
@lombok.Getter @lombok.Setter
public class TelemeterofficeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** テレメータサーバ情報 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long telemeterserverinfoid;

    /** 管理事務所番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officecode;

    /** 管理事務所名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String officename;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効／無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** テレメータサーバ情報 */
    @ManyToOne
    @JoinColumn(name="telemeterserverinfoid")
    public TelemeterserverInfo telemeterserverInfo;
}
