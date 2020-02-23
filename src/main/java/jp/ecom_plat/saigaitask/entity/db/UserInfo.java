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
 * ユーザ情報
 *
 */
@Entity
@Table(name = "user_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 班ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** ユニットID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long unitid;

    /** 職員番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String staffno;

    /** 名前 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 役割 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String duty;

    /** 電話番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String telno;

    /** 携帯電話番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String mobileno;

    /** メールアドレス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String email;

    /** 携帯電話のメールアドレス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String mobilemail;

    /** Push通知用トークン */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String pushtoken;

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

    /** 班情報 */
    @ManyToOne
    @JoinColumn(name="groupid", referencedColumnName="id")
    public GroupInfo groupInfo;

    /** ユニット情報 */
    @ManyToOne
    @JoinColumn(name="unitid")
    public UnitInfo unitInfo;

    /** NoticegroupuserInfoエンティティクラスリスト */
    @OneToMany(mappedBy = "userInfo")
    public List<NoticegroupuserInfo> noticegroupuserInfoList;
}
