/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.db;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * OAuthトークンデータ
 *
 */
@Entity
@Table(name = "oauthtoken_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/10/21 20:02:44")
@lombok.Getter @lombok.Setter
public class OauthtokenData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = false)
    public Long id;

    /** クライアントキー */
    @Column(length = 256, nullable = false, unique = false)
    public String consumerKey;

    /** リクエストトークン */
    @Column(length = 256, nullable = true, unique = true)
    public String requestToken;

    /** アクセストークン */
    @Column(length = 256, nullable = true, unique = true)
    public String accessToken;

    /** 鍵トークン */
    @Column(length = 256, nullable = true, unique = false)
    public String tokenSecret;

    /** 認可コード */
    @Column(length = 256, nullable = true, unique = false)
    public String verifier;

    /** 班ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** 課ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long unitid;

    /** 作成日 */
    @Column(nullable = true, unique = false)
    public Timestamp created;

    /** 最終アクセス日時 */
    @Column(nullable = true, unique = false)
    public Timestamp lastAccess;

    /**
     * 班情報
     */
    @ManyToOne
    @JoinColumn(name="groupid", referencedColumnName="id")
    public GroupInfo groupInfo;

    /**
     * 課情報
     */
    @ManyToOne
    @JoinColumn(name="unitid", referencedColumnName="id")
    public UnitInfo unitInfo;

}
