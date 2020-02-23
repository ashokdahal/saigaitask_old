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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ログイン履歴
 * 
 */
@Entity
@Table(name = "login_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/09 10:52:25")
@lombok.Getter @lombok.Setter
public class LoginData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** 班ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long groupid;

    /** 課ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long unitid;

    /** 訓練ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long demoinfoid;

    /** 災害種別マスタID */
    /*@Column(precision = 10, nullable = true, unique = false)
    public Integer disasterid;*/

    /** ログイン時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp logintime;

    /** ログアウト時刻 */
    @Column(nullable = true, unique = false)
    public Timestamp logouttime;

    @ManyToOne
    @JoinColumn(name="trackdataid")
    public GroupInfo trackData;

    /** 班情報 */
    @ManyToOne
    @JoinColumn(name="groupid")
    public GroupInfo groupInfo;

    @ManyToOne
    @JoinColumn(name="demoinfoid")
    public DemoInfo demoInfo;

    /*@ManyToOne
    @JoinColumn(name="disasterid")
    public DisasterMaster disasterMaster;*/
}
