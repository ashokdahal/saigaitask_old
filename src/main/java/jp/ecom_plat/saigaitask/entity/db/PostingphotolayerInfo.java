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
 * 被災写真投稿レイヤ情報
 * 
 */
@Entity
@Table(name = "postingphotolayer_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/11/12 21:07:22")
@lombok.Getter @lombok.Setter
@lombok.ToString(exclude={"postingphotolayerDatas"})
public class PostingphotolayerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 自治体ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long localgovinfoid;

    /** テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long tablemasterinfoid;

    /** コメント属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String commentAttrid;

    /** 所属班属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String groupAttrid;

    /** 送信者氏名属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String nameAttrid;

    /** 連絡先属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String contactAttrid;

    /** 撮影時刻属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String timeAttrid;

    /** 方位属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String directionAttrid;

    /** 高度属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String heightAttrid;

    /** 災害フラグ属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String flagAttrid;

    /** コピー先テーブルID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long copytablemasterinfoid;

    /** コピー先コメント属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String copycommentAttrid;

    /** コピー先所属班属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String copygroupAttrid;

    /** コピー先送信者氏名属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String copynameAttrid;

    /** コピー先連絡先属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String copycontactAttrid;

    /** コピー先撮影時刻属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String copytimeAttrid;

    /** コピー先方位属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String copydirectionAttrid;

    /** コピー先高度属性ID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String copyheightAttrid;

    /** 表示画像幅（高さ） */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer maximagewidth;
    
    /** 地方自治体情報 */
    @ManyToOne
    @JoinColumn(name="localgovinfoid")
    public LocalgovInfo localgovInfo;
    
    /** テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="tablemasterinfoid")
    public TablemasterInfo tablemasterInfo;
    
    /** コピー先テーブルマスター情報 */
    @ManyToOne
    @JoinColumn(name="copytablemasterinfoid")
    public TablemasterInfo copytablemasterInfo;

    /** 被災写真投稿振り分けデータ */
    @OneToMany(mappedBy="postingphotolayerInfo")
    public List<PostingphotolayerData> postingphotolayerDatas;
}
