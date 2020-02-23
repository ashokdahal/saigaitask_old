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
 * 通知グループ情報
 * 
 */
@Entity
@Table(name = "noticegroup_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/10 12:24:25")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"assembleInfoList","meteotriggerInfoList","noticedefaultgroupInfoList","noticegroupuserInfoList","noticegroupaddressInfoList"})
public class NoticegroupInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** グループ名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String name;

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

    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;

    /** 職員参集情報リスト */
    @OneToMany(mappedBy = "noticegroupInfo")
    public List<AssembleInfo> assembleInfoList;

    /** 気象情報トリガー情報リスト */
    @OneToMany(mappedBy = "noticegroupInfo")
    public List<MeteotriggerInfo> meteotriggerInfoList;

    /** 通知デフォルトグループ情報リスト */
    @OneToMany(mappedBy = "noticegroupInfo")
    public List<NoticedefaultgroupInfo> noticedefaultgroupInfoList;

    /** NoticegroupuserInfoエンティティクラスリスト */
    @OneToMany(mappedBy = "noticegroupInfo")
    public List<NoticegroupuserInfo> noticegroupuserInfoList;

    /** 通知グループ連絡先情報リスト */
    @OneToMany(mappedBy = "noticegroupInfo")
    public List<NoticegroupaddressInfo> noticegroupaddressInfoList;
}
