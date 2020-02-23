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
 * 通知連絡先情報
 * 
 */
@Entity
@Table(name = "noticeaddress_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/10 12:25:07")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"noticegroupaddressInfoList"})
public class NoticeaddressInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** 名前 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

    /** 電話番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String telno;

    /** 携帯電話番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String mobileno;

    /** FAX番号 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String faxno;

    /** メールアドレス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String email;

    /** 携帯のメールアドレス */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String mobilemail;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** 有効・無効 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean valid;

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** 通知グループ連絡先情報リスト */
    @OneToMany(mappedBy = "noticeaddressInfo")
    public List<NoticegroupaddressInfo> noticegroupaddressInfoList;
}
