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
 * 班情報
 *
 */
@Entity
@Table(name = "group_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude = { "localgovInfo", "userInfos", "menuloginInfos", "alarmmessageInfoList",
		"alarmdefaultgroupInfoList", "autocompleteInfoList", "adminbackupDataList", "localgovInfos" })
public class GroupInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType
    .IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 班名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** ログインパスワード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String password;

    /** eコミマップのアカウント */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String ecomuser;

    /** eコミマップのアカウントパスワード */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String ecompass;

    /** 管理権限 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean admin;

    /** 本部権限 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean headoffice;

    /** 地図範囲（WKT） */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String extent;

    /** 解像度 */
    @Column(precision = 17, scale = 17, nullable = true, unique = false)
    public Double resolution = 0.0;

    /** 班名カナ(コモンズ発信作成組織情報) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String namekana;

    /** 電話番号(コモンズ発信作成組織情報) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String phone;

    /** FAX番号(コモンズ発信作成組織情報) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String fax;

    /** Eメールアドレス(コモンズ発信作成組織情報) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String email;

    /** 住所(コモンズ発信作成組織情報) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String address;

    /** ドメイン(コモンズ発信作成組織情報) */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String domain;

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
    public Boolean valid;

    /** 削除フラグ */
    @Column(length = 1, nullable = false, unique = false)
    public Boolean deleted;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** ユニット情報リスト */
    /*@OneToMany(mappedBy="groupInfo")
    public List<UnitInfo> unitInfos;
	*/
    /* ユーザ情報リスト */
    @OneToMany(mappedBy="groupInfo")
    public List<UserInfo> userInfos;

    /** メニュー設定情報リスト */
    @OneToMany(mappedBy="groupInfo")
    public List<MenuloginInfo> menuloginInfos;

    /** アラームメッセージ設定リスト */
    @OneToMany(mappedBy = "groupInfo")
    public List<AlarmmessageInfo> alarmmessageInfoList;

    /** アラームデフォルトグループ情報リスト */
    @OneToMany(mappedBy = "groupInfo")
    public List<AlarmdefaultgroupInfo> alarmdefaultgroupInfoList;

    /** 入力補完情報リスト */
    @OneToMany(mappedBy = "groupInfo")
    public List<AutocompleteInfo> autocompleteInfoList;

    /** 管理バックアップデータリスト */
    @OneToMany(mappedBy = "groupInfo")
    public List<AdminbackupData> adminbackupDataList;

    /** 地方自治体情報リスト */
    @OneToMany(mappedBy="groupInfo")
    public List<LocalgovInfo> localgovInfos;
}
