/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
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
 * 被災写真投稿振り分けデータ
 * 
 */
@Entity
@Table(name = "postingphotolayer_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/11/12 21:07:53")
@lombok.Getter @lombok.Setter
public class PostingphotolayerData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 被災写真投稿レイヤ情報ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long postingphotolayerinfoid;

    /** レイヤID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String layerid;

    /** フィーチャID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long photogid;

    /** コピー先記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long copytrackdataid;

    /** コピー先レイヤID */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String copylayerid;

    /** コピー先フィーチャID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long copygid;

    /** コピー時間 */
    @Column(nullable = true, unique = false)
    public Timestamp copytime;

    /** 被災写真投稿レイヤ情報 */
    @ManyToOne
    @JoinColumn(name="postingphotolayerinfoid")
    public PostingphotolayerInfo postingphotolayerInfo;
}
