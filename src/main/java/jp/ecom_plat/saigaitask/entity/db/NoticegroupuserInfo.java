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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * NoticegroupuserInfoエンティティクラス
 *
 */
@Entity
@Table(name = "noticegroupuser_info")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/10 12:24:52")
@lombok.Getter @lombok.Setter
public class NoticegroupuserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** メールグループ情報 */
    @Column(precision = 19, nullable = true, unique = false)
    public Long noticegroupinfoid;

    /** ユーザID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long userid;

    /** ユーザ情報 */
    @ManyToOne
    @JoinColumn(name="userid")
    public UserInfo userInfo;

    /** 通知グループ情報 */
    @ManyToOne
    @JoinColumn(name="noticegroupinfoid")
    public NoticegroupInfo noticegroupInfo;
}
