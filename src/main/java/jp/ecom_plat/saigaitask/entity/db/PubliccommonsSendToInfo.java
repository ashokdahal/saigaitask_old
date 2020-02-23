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
 * 公共情報コモンズ発信先データ
 *
 */
@Entity
@Table(name = "publiccommons_send_to_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 16:53:05")
@lombok.Getter @lombok.Setter
public class PubliccommonsSendToInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = false, unique = false)
    public Long localgovinfoid;

    /** エンドポイントURL */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String endpointUrl;

    /** ユーザ名 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String username;

    /** パスワード */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String password;

    /** 送信順 */
    @Column(precision = 10, nullable = false, unique = false)
    public Integer sendOrder;

    /** 運用種別 */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String statusValues;

    /** エンドポイントURLバックアップノード */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String endpointUrlBackup;

    /** ユーザ名バックアップノード */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String usernameBackup;

    /** パスワードバックアップノード */
    @Lob
    @Column(length = 2147483647, nullable = false, unique = false)
    public String passwordBackup;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}
