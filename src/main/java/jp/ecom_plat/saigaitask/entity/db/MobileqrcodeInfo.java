package jp.ecom_plat.saigaitask.entity.db;

import java.io.Serializable;
import java.sql.Timestamp;
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
 * 投稿アプリ認証QR設定
 * 
 */
@Entity
@Table(name = "mobileqrcode_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2018/04/04 18:09:33")
@lombok.Getter @lombok.Setter
public class MobileqrcodeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** OAuthコンシューマID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long oauthconsumerid;
    
    /** QRコード名称 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String title;

    /** 認証班ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** 認証課ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long unitid;

    /** 投稿先テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** 認証終了日 */
    @Column(nullable = true, unique = false)
    public Timestamp authenticationdate;

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

    /** group関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "groupid", referencedColumnName = "id")
    public GroupInfo group;

    /** localgovinfo関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "localgovinfoid", referencedColumnName = "id")
    public LocalgovInfo localgovinfo;

    /** tablemasterinfo関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "tablemasterinfoid", referencedColumnName = "id")
    public TablemasterInfo tablemasterinfo;

    /** unit関連プロパティ */
    @ManyToOne
    @JoinColumn(name = "unitid", referencedColumnName = "id")
    public UnitInfo unit;
}