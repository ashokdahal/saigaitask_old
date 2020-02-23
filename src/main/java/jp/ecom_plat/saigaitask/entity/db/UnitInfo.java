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
 * ユニット情報
 *
 */
@Entity
@Table(name = "unit_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"userInfoList"})
public class UnitInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 班ID */
    @Deprecated
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** ユニット名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** パスワード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String password;

    /** eコミマップのアカウント */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String ecomuser;

    /** 管理権限 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean admin = false;

    /** 地図範囲（WKT） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String extent;

    /** 解像度 */
    @Column(precision = 17, scale = 17, nullable = true, unique = false)
    public Double resolution = 0.0;

    /** 代表電話番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String telno;

    /** FAX番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String faxno;

    /** メールアドレス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String email;

    /** APIキー */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String apikey;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 表示順 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer disporder;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid = true;

    /** 削除 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean deleted = false;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /**
     * 班情報
     */
    /*@ManyToOne
    @JoinColumn(name="groupid", referencedColumnName="id")
    public GroupInfo groupInfo;
     */
    /** ユーザ情報リスト */
    @OneToMany(mappedBy = "unitInfo")
    public List<UserInfo> userInfoList;
}
