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
 * 認証情報
 *
 */
@Entity
@Table(name = "authorization_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2016/10/05 17:48:03")
@lombok.Getter @lombok.Setter
public class AuthorizationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 認証方式 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String authtype;

    /** ログイン名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String username;

    /** ログインパスワード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String userpass;

    /** 認証情報フリーワード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String authword;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
}
