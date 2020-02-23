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
 * 職員参集状況
 *
 */
@Entity
@Table(name = "assemblestate_data")
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2013/07/17 19:36:02")
@lombok.Getter @lombok.Setter
public class AssemblestateData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(precision = 19, nullable = false, unique = true)
    public Long id;

    /** 記録ID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long trackdataid;

    /** ユーザID */
    @Column(precision = 19, nullable = true, unique = false)
    public Long userid;

    /** 安否確認状況 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer safetystateid;

    /** 安否応答状況 */
    @Column(precision = 10, nullable = true, unique = false)
    public Integer safetystateinfoid;

    /** 担当班名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String groupname;

    /** 部課名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String unitname;

    /** 職員名 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String username;

    /** 職員NO */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String staffno;

    /** 備考 */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String note;

    /** コメント */
    @Lob
    @Column(length = 2147483647, nullable = true, unique = false)
    public String comment;

    /** ログイン状態 */
    @Column(length = 1, nullable = true, unique = false)
    public Boolean loginstatetus;

    /** 登録時間 */
    @Column(nullable = true, unique = false)
    public Timestamp registtime;

    /** 更新時間 */
    @Column(nullable = true, unique = false)
    public Timestamp updatetime;

	@ManyToOne
	@JoinColumn(name = "trackdataid")
	public TrackData trackData;

	@ManyToOne
	@JoinColumn(name = "userid")
	public UserInfo userInfo;

	@ManyToOne
	@JoinColumn(name = "safetystateid")
	public SafetystateMaster safetystateMaster;

	@ManyToOne
	@JoinColumn(name = "safetystateinfoid")
	public SafetystateInfo safetystateInfo;
}
